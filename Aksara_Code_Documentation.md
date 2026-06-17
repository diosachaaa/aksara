# Dokumentasi Kode — `com.aksara.membership`

Penjelasan lengkap **setiap file** (32 file) di dalam aplikasi Aksara, dikelompokkan
per layer arsitektur. Untuk tiap file dijelaskan: **tujuannya**, **isi utamanya**,
dan **hubungannya** dengan file lain.

---

## 🧭 Gambaran Umum

Aplikasi memakai pola **MVVM** dengan aliran data **satu arah**. Analoginya seperti
restoran:

- **Screen (UI)** = pelanggan yang memesan dan menerima makanan.
- **ViewModel** = pelayan yang menerima pesanan & mengantar hasil.
- **Repository** = kepala dapur yang mengatur dari mana bahan diambil.
- **DAO + Database (Room)** = gudang & juru masak yang menyimpan/mengolah data.

```
ui/screens  →  ui/viewmodel  →  data/repository  →  data/dao  →  Room (data/database)
   (UI)          (state)          (logika data)      (query)       (penyimpanan)
```

UI **tidak pernah** memanggil DAO langsung — selalu lewat ViewModel → Repository.

### Peta File

```
com.aksara.membership
├── AksaraApp.kt              ← titik awal aplikasi (container dependency)
├── MainActivity.kt           ← layar host Compose
├── data/
│   ├── entity/               ← bentuk tabel: Member, Transaction, Reward
│   ├── dao/                  ← perintah query: MemberDao, TransactionDao, RewardDao
│   ├── database/             ← AksaraDatabase (+ seed reward)
│   └── repository/           ← AksaraRepository
├── ui/
│   ├── viewmodel/            ← MembershipViewModel (otak aplikasi)
│   ├── theme/                ← Color, Type, Theme
│   ├── navigation/           ← Screen (rute) + AppNavGraph (alur)
│   ├── components/           ← komponen reusable
│   └── screens/              ← 10 layar
└── util/                     ← PointCalculator, MemberTier, BookCategory, Formatters, QrCodeGenerator
```

---

## 🚀 Root Package

### `AksaraApp.kt`
**Class `Application` aplikasi — bertindak sebagai "container" dependency sederhana.**
Saat aplikasi pertama dijalankan, file ini membuat **satu instance** database dan
**satu instance** repository (`by lazy`, jadi baru dibuat ketika benar-benar dipakai)
yang kemudian dipakai bersama di seluruh aplikasi. Ini versi ringan dari *Dependency
Injection* tanpa library seperti Hilt/Dagger.
→ Dipakai oleh `MainActivity` untuk mengambil `repository`.
→ Harus didaftarkan di `AndroidManifest.xml` lewat atribut `android:name`.

### `MainActivity.kt`
**Satu-satunya Activity — pintu masuk tampilan.**
Memanggil `setContent { ... }` untuk memasang UI Compose, membungkusnya dengan
`AksaraTheme`, lalu membuat `MembershipViewModel` (melalui `Factory`) dan menyerahkan
seluruh kendali tampilan ke `AppNavGraph`. Juga mengaktifkan `enableEdgeToEdge()`
agar konten memenuhi layar penuh.
→ Menghubungkan: `AksaraApp` (sumber repository) → `MembershipViewModel` → `AppNavGraph`.

---

## 💾 `data/` — Layer Data

### `data/entity/Member.kt`
**Mewakili tabel `members` di database.**
Sebuah `data class` ber-anotasi `@Entity`. Kolomnya: `id` (primary key, auto-generate),
`memberNumber` (mis. `AKS00001`), `name`, `email`, `phone`, `status` (default `"Active"`),
`totalPoints` (default 0), dan `joinDate` (timestamp). Tier member **tidak disimpan**
di sini — dihitung dari `totalPoints`.

### `data/entity/Transaction.kt`
**Mewakili tabel `transactions`.**
Menyimpan satu pembelian buku. Kolomnya: `id`, `memberId`, `bookTitle`, `category`,
`date`, `amount` (Rupiah, `Long`), dan `pointsEarned`. Memiliki **Foreign Key** ke
`Member` dengan `onDelete = CASCADE` (jika member dihapus, transaksinya ikut terhapus)
dan `Index("memberId")` agar query per-member cepat.
> `bookTitle` & `category` adalah ciri khas toko buku — membedakan dari aplikasi loyalitas biasa.

### `data/entity/Reward.kt`
**Mewakili tabel `rewards`.**
Hadiah yang bisa ditukar dengan poin. Kolomnya: `id`, `name`, `pointCost`, dan
`description`. Datanya diisi otomatis (seed) oleh `AksaraDatabase`.

### `data/dao/MemberDao.kt`
**DAO (Data Access Object) untuk tabel members — kumpulan perintah database.**
Menyediakan: `insert`, `update`, `observeMember(id)` (mengembalikan `Flow`, reaktif),
`getMemberById(id)` (sekali ambil), `getMemberByEmail(email)` (untuk login), dan
`countByEmail(email)` (untuk cek duplikat saat registrasi).
→ Diimplementasikan otomatis oleh Room; dipanggil oleh `AksaraRepository`.

### `data/dao/TransactionDao.kt`
**DAO untuk tabel transactions.**
Berisi `insert(transaction)` dan `observeTransactions(memberId)` yang mengembalikan
`Flow<List<Transaction>>` terurut `date DESC` (terbaru di atas).

### `data/dao/RewardDao.kt`
**DAO untuk tabel rewards.**
Berisi `insertAll(rewards)` (dengan `OnConflictStrategy.IGNORE` agar tidak dobel saat
seed), `observeRewards()` (terurut `pointCost ASC`), dan `count()` untuk mengecek
apakah seed sudah terisi.

### `data/database/AksaraDatabase.kt`
**Kelas database Room — pusat penyimpanan.**
Anotasi `@Database` mendaftarkan 3 entity dengan **`version = 2`**. Menyediakan akses ke
ketiga DAO. Memakai pola **singleton** (`getInstance`) agar database hanya dibuat sekali.
Dua konfigurasi penting:
- `fallbackToDestructiveMigration()` — jika skema berubah, database lama dibangun ulang
  (aman untuk pengembangan).
- `SeedCallback` — saat database **pertama kali dibuat** (`onCreate`), otomatis mengisi
  4 reward default (Pembatas Buku 30, Voucher Diskon 50, Tote Bag 100, Voucher Buku Gratis 150).

### `data/repository/AksaraRepository.kt`
**Single source of truth — perantara antara ViewModel dan DAO.**
Membungkus operasi data menjadi fungsi bisnis yang bermakna:
- `register(name, email, phone)` — insert member lalu memberi `memberNumber` berformat `AKS%05d`.
- `isEmailRegistered`, `login(email)`, `observeMember`, `updateMember`.
- `addTransaction(memberId, bookTitle, category, amount)` — menghitung poin via
  `PointCalculator`, menyimpan transaksi, **lalu menambah `totalPoints` member**.
- `observeTransactions`, `observeRewards`.
- `redeem(memberId, reward)` — cek poin cukup; bila ya, kurangi poin & kembalikan `true`.
→ ViewModel hanya berbicara dengan file ini, tidak menyentuh DAO langsung.

---

## 🧠 `ui/viewmodel/`

### `ui/viewmodel/MembershipViewModel.kt`
**Otak aplikasi — menyimpan state & menjalankan logika, penghubung UI ↔ Repository.**
Komponen utamanya:
- **Session:** `_currentMemberId` menyimpan siapa yang sedang login; `isLoggedIn` turunannya.
- **State reaktif:** `member`, `transactions`, dan `rewards` adalah `StateFlow`. `member`
  dan `transactions` dibangun dengan `flatMapLatest` — otomatis berganti sumber data
  saat member yang login berubah, lalu `stateIn` menjadikannya state yang bisa diamati UI.
- **Event UI:** `UiEvent` (Success/Error) untuk menampilkan pesan; `consumeEvent()` mereset.
- **Aksi:** `register`, `login`, `previewPoints`, `addTransaction`, `redeem`,
  `updateProfile`, `logout`.
- **`Factory`:** kelas pembantu agar ViewModel bisa dibuat dengan parameter `repository`.
→ Dibuat di `MainActivity`, dipakai oleh **semua** screen.

---

## 🎨 `ui/theme/`

### `ui/theme/Color.kt`
**Definisi seluruh warna (palet brand).**
Nuansa indigo–emas–lavender: `IndigoPrimary`, `IndigoDark/Light`, `GoldAccent`,
`PaperBg`, `LavenderCard`, dll. Plus warna tier: `TierBronze`, `TierSilver`, `TierGold`.
→ Dipakai oleh `Theme.kt`, `MemberTier.kt`, dan berbagai screen.

### `ui/theme/Type.kt`
**Definisi tipografi (ukuran & ketebalan teks).**
Objek `Typography` Material 3: `headlineLarge`, `titleMedium`, `bodyLarge`, `labelLarge`,
dan lainnya. Memberi gaya teks konsisten di seluruh aplikasi.

### `ui/theme/Theme.kt`
**Menggabungkan warna + tipografi menjadi `AksaraTheme`.**
Membuat `lightColorScheme` dari warna di `Color.kt` dan membungkus konten dengan
`MaterialTheme`. Semua UI berada di dalam `AksaraTheme { ... }` (dipasang di `MainActivity`).

---

## 🧭 `ui/navigation/`

### `ui/navigation/Screen.kt`
**Daftar semua "alamat" layar (rute) — tipe aman, anti salah ketik.**
`sealed class` dengan satu `data object` per layar (`Splash`, `Login`, `Home`, dll).
`RewardDetail` istimewa karena membawa argumen: rutenya `reward_detail/{rewardId}` dan
punya helper `createRoute(rewardId)`.

### `ui/navigation/AppNavGraph.kt`
**Peta navigasi — mengatur layar mana muncul & ke mana tombol mengarah.**
Membuat `NavHost` dengan `startDestination = Splash`. Tiap `composable(route) { ... }`
memasang satu screen dan menyuntikkan callback navigasinya (mis. `onOpenCard`,
`onLoginSuccess`). Mengatur juga `popUpTo` agar tombol back berperilaku benar (mis.
setelah login, Splash/Login dihapus dari back stack; saat logout, seluruh stack dibersihkan).
→ Menghubungkan seluruh 10 screen menjadi satu alur.

---

## 🧩 `ui/components/`

### `ui/components/CommonComponents.kt`
**Komponen UI reusable agar tidak menulis ulang.**
- `AksaraTopBar(title, onBack)` — top bar konsisten berwarna indigo, dengan tombol kembali opsional.
- `PrimaryButton(text, enabled, onClick)` — tombol utama lebar penuh khas aplikasi.
- `CircleStep(number)` — lingkaran bernomor (elemen dekoratif).
→ Dipakai berulang di hampir semua screen.

---

## 📱 `ui/screens/` — 10 Layar

### `ui/screens/SplashScreen.kt`
**Layar pembuka.** `SplashScreen(onStart)`. Menampilkan logo/branding Aksara dan tombol
**Start**. Saat ditekan, `AppNavGraph` mengarahkan ke Home (jika sudah login) atau Login.

### `ui/screens/LoginScreen.kt`
**Layar masuk via email.** `LoginScreen(viewModel, onLoginSuccess, onGoToRegister)`.
Input email → `viewModel.login()`. Jika ditemukan, masuk ke Home; jika tidak, muncul
pesan error. Ada tautan "Daftar di sini" menuju Register.

### `ui/screens/RegisterScreen.kt`
**Form registrasi member.** `RegisterScreen(viewModel, onRegisterSuccess, onBack)`.
Mengisi Nama, Email, No HP dengan **validasi**: nama wajib, format email valid, HP minimal
8 digit. Memanggil `viewModel.register()` → otomatis login & menuju Home.

### `ui/screens/HomeScreen.kt`
**Dashboard member.** `HomeScreen(viewModel, onOpenCard, onOpenTransactions, onOpenRewards, onOpenProfile)`.
Menampilkan sapaan + ringkasan total poin, lalu menu navigasi (`MenuItem`) ke: My Card,
Transactions, Rewards, Profile.

### `ui/screens/MemberCardScreen.kt`
**Kartu member digital.** `MemberCardScreen(viewModel, onBack)`. Menampilkan nama, nomor
member, **tier** (via `MemberTier.of`), total poin, dan **QR Code** yang dibuat dari nomor
member memakai `rememberQrBitmap`.

### `ui/screens/TransactionScreen.kt`
**Riwayat transaksi.** `TransactionScreen(viewModel, onBack, onAddTransaction)`. Menampilkan
daftar pembelian (judul buku, kategori • tanggal, nominal, poin) dari `viewModel.transactions`,
terurut terbaru. Tombol **FAB (+)** membuka Add Transaction.

### `ui/screens/AddTransactionScreen.kt`
**Form tambah transaksi.** `AddTransactionScreen(viewModel, onBack, onSaved)`. Input
**judul buku**, **kategori** (dropdown dari `BookCategory`), dan **nominal**. Menampilkan
**pratinjau poin real-time** (`viewModel.previewPoints`). Saat disimpan, memanggil
`viewModel.addTransaction()` lalu menampilkan dialog sukses berisi poin yang didapat.

### `ui/screens/RewardsScreen.kt`
**Katalog reward.** `RewardsScreen(viewModel, onBack, onSelectReward)`. Menampilkan daftar
reward dari `viewModel.rewards`. Mengetuk satu reward memanggil `onSelectReward(reward.id)`
→ membuka halaman detail.

### `ui/screens/RewardDetailScreen.kt`
**Detail & proses redeem.** `RewardDetailScreen(viewModel, rewardId, onBack, onDone)`.
Menampilkan detail reward terpilih + konfirmasi. Memanggil `viewModel.redeem()` yang
memverifikasi poin cukup; jika berhasil, poin berkurang dan muncul status sukses.

### `ui/screens/ProfileScreen.kt`
**Profil member.** `ProfileScreen(viewModel, onBack, onLogout)`. Menampilkan & mengizinkan
**edit** data member (`viewModel.updateProfile`), serta tombol **Logout** yang memanggil
`viewModel.logout()` dan mengembalikan ke Login.

---

## 🔧 `util/` — Utility

### `util/PointCalculator.kt`
**Aturan perhitungan poin.** Objek dengan konstanta `RUPIAH_PER_POINT = 10_000` dan fungsi
`calculate(amount) = (amount / 10.000).toInt()`. Contoh: Rp150.000 → 15 poin.
→ Dipakai di `AksaraRepository` dan `MembershipViewModel` (untuk pratinjau).

### `util/MemberTier.kt`
**Logika tier keanggotaan.** `enum class MemberTier(label, color)` dengan helper
`of(points)`: < 100 → **Pembaca**, 100–299 → **Kutu Buku**, ≥ 300 → **Bibliofil**.
Nama konstanta tetap BRONZE/SILVER/GOLD agar referensi warna stabil; yang tampil adalah `label`.

### `util/BookCategory.kt`
**Daftar kategori buku.** Objek dengan `ALL = [Fiksi, Non-Fiksi, Komik, Akademik]` dan
`DEFAULT`. Menjadi sumber pilihan dropdown di Add Transaction.

### `util/Formatters.kt`
**Fungsi format tampilan.** Dua *extension function*: `Long.toRupiah()` (mis. `150000` →
`"Rp150.000"`) dan `Long.toIndoDate()` (timestamp → `"20 Mei 2024"`, locale Indonesia).

### `util/QrCodeGenerator.kt`
**Pembuat QR Code (lokal, tanpa internet).** Objek `QrCodeGenerator.generate(content)`
memakai `QRCodeWriter` dari ZXing untuk menghasilkan `Bitmap`. Disertai composable
`rememberQrBitmap(content)` yang meng-cache hasilnya (`remember`) sebagai `ImageBitmap`.
→ Dipakai di `MemberCardScreen`.

---

## 📖 Urutan Membaca yang Disarankan

Bagi yang baru pertama membaca kode ini, ikuti aliran data dari bawah ke atas:

1. **`data/entity/`** — pahami bentuk datanya dulu (Member, Transaction, Reward).
2. **`data/dao/`** — perintah apa saja yang bisa dilakukan ke data itu.
3. **`data/database/`** & **`data/repository/`** — bagaimana data disimpan & dikelola.
4. **`util/`** — aturan bisnis kecil (poin, tier, format).
5. **`ui/viewmodel/`** — bagaimana state dirakit dari repository.
6. **`ui/navigation/`** — alur antar layar.
7. **`ui/screens/`** — terakhir, bagaimana semuanya ditampilkan.

> Tip: mulai dari `MainActivity.kt` → `AppNavGraph.kt` untuk melihat "peta besar",
> lalu telusuri satu fitur penuh (mis. Add Transaction) dari screen → viewmodel →
> repository → dao untuk memahami satu aliran utuh.
