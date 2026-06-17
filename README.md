# Aksara - Bookstore Membership & Loyalty App

Aplikasi member digital untuk toko buku Aksara. Pengguna dapat mendaftar, melakukan login, melihat katalog produk, menambahkan item ke keranjang, mengumpulkan poin dari transaksi, melihat kartu member beserta QR Code, dan menukar poin untuk reward.

Mini project akhir mata kuliah Pemrograman Perangkat Bergerak dengan arsitektur Kotlin + Jetpack Compose + Room.

---

## Daftar Isi
1. [Latar Belakang](#latar-belakang)
2. [Tujuan Aplikasi](#tujuan-aplikasi)
3. [Teknologi yang Digunakan](#teknologi-yang-digunakan)
4. [Fitur Utama](#fitur-utama)
5. [Aturan Bisnis](#aturan-bisnis)
6. [Struktur Project](#struktur-project)
7. [Arsitektur Aplikasi](#arsitektur-aplikasi)
8. [Desain Database](#desain-database)
9. [Cara Menjalankan](#cara-menjalankan)
10. [Alur Uji Coba](#alur-uji-coba)
11. [Catatan Teknis](#catatan-teknis)
12. [Kesimpulan](#kesimpulan)

---

## Latar Belakang

Aksara merupakan aplikasi membership digital untuk toko buku yang bertujuan membantu pelanggan mengelola pengalaman berbelanja secara lebih praktis. Aplikasi ini menyediakan fitur login, katalog produk, keranjang belanja, pengumpulan poin, kartu member digital, hingga penukaran reward.

---

## Tujuan Aplikasi

Tujuan dari aplikasi ini adalah:
- mempermudah pelanggan dalam melakukan pembelian dengan alur yang sederhana;
- memberikan pengalaman loyalitas melalui sistem poin dan reward;
- menyajikan kartu member digital yang dapat dilihat dengan cepat;
- membantu pengguna memahami riwayat transaksi dan penukaran reward.

---

## Teknologi yang Digunakan

| Komponen | Detail |
|---|---|
| Bahasa Pemrograman | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material 3 |
| Compose BOM | 2024.12.01 |
| Navigation | Navigation Compose 2.8.5 |
| Database | Room 2.8.4 (KSP 2.3.2) |
| Arsitektur | MVVM + Repository Pattern |
| State Management | StateFlow / collectAsState |
| QR Code | ZXing 3.5.3 |
| Build Tool | AGP 9.1.1, Gradle 9.3.1, JDK 17 |
| Target SDK | compileSdk 35, targetSdk 35, minSdk 24 |

---

## Fitur Utama

1. **Splash Screen** - layar pembuka sebelum pengguna masuk ke alur aplikasi.
2. **Login dan Register** - pengguna dapat masuk menggunakan email dan mendaftar akun baru.
3. **Home** - menampilkan sapaan, ringkasan poin, tier member, serta akses cepat ke belanja, riwayat, dan reward.
4. **Katalog Belanja** - menampilkan produk dari kategori buku, ATK, kafe, dan lainnya, lengkap dengan gambar tiap produk.
5. **Keranjang** - pengguna dapat mengubah jumlah produk sebelum melakukan checkout.
6. **Riwayat Transaksi** - menampilkan data transaksi beserta kategori dan poin yang diperoleh.
7. **Digital Member Card** - menampilkan nomor member, tier, progress poin, total poin, dan QR Code.
8. **Rewards** - menampilkan daftar reward yang tersedia untuk ditukar.
9. **Reward Detail** - menampilkan detail reward dan tombol redeem jika poin mencukupi.
10. **Riwayat Penukaran** - menampilkan voucher yang sudah berhasil ditukar.
11. **Profile** - pengguna dapat mengedit data, mengganti foto profil, mengaktifkan dark mode, dan logout.

---

## Aturan Bisnis

### Perhitungan Poin
- 1 poin diperoleh dari setiap Rp10.000 belanja.
- Rumus yang digunakan: `poin = totalBelanja / 10.000` (dibulatkan ke bawah).

### Tier Member

Tier dihitung otomatis dari total poin (tidak disimpan terpisah di database).

| Tier | Rentang Poin |
|---|---|
| Bronze | 0 - 99 |
| Silver | 100 - 299 |
| Gold | >= 300 |

### Reward yang Tersedia

| Reward | Poin |
|---|---|
| Pembatas Buku Eksklusif | 50 |
| Tote Bag Aksara | 100 |
| Voucher Buku Gratis | 150 |
| Diskon 50% Buku | 250 |
| Merchandise Eksklusif | 400 |

### Kategori Produk
- BUKU
- ATK
- KAFE
- LAINNYA

### Format Voucher
- Kode voucher yang dihasilkan memiliki format `AKS-XXXX-XXXX`.

---

## Struktur Project

```text
app/
└── src/main/
    ├── java/com/aksara/membership/
    │   ├── AksaraApp.kt              # Application; menyiapkan repository & seeding awal
    │   ├── MainActivity.kt           # Host Compose, tema, dan NavGraph
    │   ├── data/
    │   │   ├── CatalogSeed.kt         # data awal katalog produk
    │   │   ├── RewardSeed.kt          # data awal daftar reward
    │   │   ├── entity/                # Member, Product, Transaction, Reward, Redemption
    │   │   ├── dao/                   # MemberDao, ProductDao, TransactionDao, RewardDao, RedemptionDao
    │   │   ├── database/              # AksaraDatabase (+ seed callback)
    │   │   └── repository/            # AksaraRepository
    │   ├── ui/
    │   │   ├── navigation/            # Screen, AppNavGraph
    │   │   ├── screens/               # 12 layar (Splash, Login, Register, Home, Catalog, dst.)
    │   │   ├── components/            # AksaraBottomBar, AvatarImage, CommonComponents
    │   │   ├── viewmodel/             # MembershipViewModel
    │   │   └── theme/                 # Color, Theme, Type
    │   └── util/                      # PointCalculator, MemberTier, TransactionCategory,
    │                                  # CodeGenerator, ImageStorage, ProductImage,
    │                                  # Formatters, QrCodeGenerator
    └── res/
        └── drawable/                 # ikon aplikasi + 19 gambar produk (prod_*)
```

---

## Arsitektur Aplikasi

Aplikasi ini menggunakan arsitektur MVVM dengan repository sebagai lapisan penghubung antara ViewModel dan Room Database.

```text
UI (Compose)
   │  event
   ▼
ViewModel  ── StateFlow ──►  UI (reaktif)
   │
   ▼
Repository
   │
   ▼
Room Database
```

Alur utama yang digunakan:
- ViewModel mengelola state aplikasi, sesi pengguna, keranjang, dan event UI.
- Repository menangani operasi login, register, checkout, redeem reward, dan query data.
- Room Database menyimpan data member, transaksi, reward, redemption, dan produk.
- UI tidak pernah mengakses Room secara langsung - selalu melalui ViewModel dan Repository.

---

## Desain Database

Aplikasi menggunakan Room Database **versi 5** dengan 5 tabel utama.

| Tabel | Fungsi |
|---|---|
| members | menyimpan data member, poin, foto profil, dan tanggal join |
| products | menyimpan katalog produk beserta kunci gambar (imageKey) |
| transactions | menyimpan riwayat transaksi dan poin yang diperoleh |
| rewards | menyimpan daftar reward yang tersedia |
| redemptions | menyimpan riwayat redeem beserta kode voucher |

Beberapa poin penting:
- `memberNumber` dibuat otomatis saat registrasi (format `AKS00001`).
- `totalPoints` diperbarui saat checkout atau redeem reward.
- `transactions` dan `redemptions` terhubung ke `members` lewat Foreign Key (`onDelete = CASCADE`).
- Data produk dan reward di-seed otomatis melalui dua mekanisme: (1) seed callback pada database saat dibuat atau dibangun ulang, dan (2) pemeriksaan cadangan `ensureCatalogSeeded()` / `ensureRewardsSeeded()` yang dipanggil saat aplikasi mulai (`AksaraApp`), sehingga katalog dan reward tetap terisi meskipun database lama kosong.
- Database builder masih menggunakan `fallbackToDestructiveMigration()`.

---

## Cara Menjalankan

1. Buka project di Android Studio.
2. Tunggu proses Gradle sync selesai.
3. Pilih emulator atau perangkat Android dengan API 24+.
4. Jalankan aplikasi melalui menu Run.
5. Jika ingin melakukan build secara manual, jalankan perintah berikut:

```bash
./gradlew assembleDebug
```

---

## Alur Uji Coba

1. Jalankan aplikasi hingga muncul splash screen.
2. Daftar akun baru lalu masuk ke Home.
3. Buka katalog, tambahkan produk ke keranjang, lalu lakukan checkout.
4. Periksa kenaikan poin yang diterima.
5. Buka halaman Kartu untuk melihat QR Code dan progres tier member.
6. Pilih reward, lakukan redeem, lalu cek voucher pada riwayat penukaran.
7. Masuk ke halaman Profil untuk mengubah data atau foto profil.

---

## Catatan Teknis

- `checkout()` membuat satu transaksi per kategori dan menghitung poin dari total belanja.
- `redeem()` mengurangi poin pengguna dan menyimpan kode voucher ke tabel redemptions.
- `darkMode` disimpan sebagai state di ViewModel.
- Setiap produk memiliki `imageKey` yang dipetakan ke gambar drawable melalui `ProductImage.kt`; jika kunci tidak dikenal, aplikasi memakai ikon kategori sebagai fallback.
- Foto profil disimpan ke internal storage agar tetap tersedia setelah aplikasi ditutup.
- QR Code dibuat secara lokal tanpa membutuhkan koneksi internet.
- Semua data utama dipantau dengan Flow/StateFlow agar UI dapat bereaksi secara real-time.

> Catatan build: pada tahap pengembangan saat ini, database masih memakai `fallbackToDestructiveMigration()`. Jika ingin memperbaiki warning Room di masa mendatang, disarankan untuk menggunakan `fallbackToDestructiveMigration(dropAllTables = true)`.

---

## Kesimpulan

Aksara merupakan aplikasi membership digital yang menggabungkan konsep belanja, loyalitas, dan reward dalam satu antarmuka yang sederhana. Dengan arsitektur MVVM, Room Database, dan Jetpack Compose, aplikasi ini mampu menyediakan pengalaman pengguna yang modern serta mudah dikembangkan lebih lanjut.
