# Aksara — Bookstore Membership Card App

Aplikasi **kartu member digital** untuk toko buku **Aksara**. Pelanggan dapat
mendaftar sebagai member, mengumpulkan poin dari setiap pembelian buku, melihat
kartu member digital lengkap dengan QR Code, dan menukar poin dengan reward.

Dibangun sebagai **Mini Project Evaluasi Akhir** mata kuliah *Pemrograman
Perangkat Bergerak* menggunakan Android modern (Kotlin + Jetpack Compose + Room).

---

## 📚 Daftar Isi
1. [Teknologi](#-teknologi)
2. [Fitur](#-fitur)
3. [Aturan Bisnis](#-aturan-bisnis)
4. [Struktur Project](#-struktur-project-mvvm)
5. [Arsitektur](#-arsitektur)
6. [Desain Database](#-desain-database)
7. [Daftar Layar & Navigasi](#-daftar-layar--navigasi)
8. [Cara Menjalankan](#-cara-menjalankan)
9. [Alur Uji Coba Cepat](#-alur-uji-coba-cepat)
10. [Status Definition of Done](#-status-definition-of-done)
11. [Catatan Teknis](#-catatan-teknis)

---

## 🛠 Teknologi

| Komponen | Detail |
|---|---|
| Bahasa | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 |
| Database | Room 2.6.1 (compiler via KSP) |
| Arsitektur | MVVM + Repository Pattern |
| Navigasi | Navigation Compose 2.8.5 |
| State | StateFlow / `collectAsState` |
| QR Code | ZXing (`com.google.zxing:core`) — dibuat lokal, tanpa internet |
| Build | AGP 8.7.3 · JDK 17 · minSdk 24 · targetSdk/compileSdk 35 |

---

## ✨ Fitur

1. **Splash Screen** — logo toko buku & tombol *Start*.
2. **Login & Registrasi** — masuk dengan email member; registrasi mengisi Nama,
   Email, dan No HP (tersimpan ke Room). Validasi: nama wajib, format email valid,
   nomor HP minimal 8 digit.
3. **Home / Dashboard** — sapaan nama, ringkasan total poin, dan menu navigasi.
4. **Digital Membership Card** — nama, nomor member (mis. `AKS00001`), **tier**,
   total poin, dan **QR Code** yang digenerate dari nomor member.
5. **Riwayat Transaksi** — daftar pembelian: **judul buku, kategori**, tanggal,
   nominal, dan poin didapat (urut dari terbaru).
6. **Tambah Transaksi** — input judul buku, pilih kategori (dropdown), dan nominal;
   poin dihitung otomatis dengan **pratinjau real-time**.
7. **Rewards** — katalog hadiah toko buku yang dapat ditukar dengan poin.
8. **Redeem** — pilih reward → halaman detail → konfirmasi → sukses; sistem
   memverifikasi poin mencukupi sebelum menukar.
9. **Profile** — lihat & edit data member, serta logout.

---

## 📐 Aturan Bisnis

**Perhitungan Poin** (`util/PointCalculator.kt`)
```
poin = nominal_pembelian / 10.000   (pembagian bulat)
Contoh: Rp150.000 → 15 poin
```

**Tier Keanggotaan** (`util/MemberTier.kt`) — dihitung otomatis dari total poin,
tidak disimpan terpisah di database:

| Tier | Rentang Poin |
|---|---|
| Pembaca | 0 – 99 |
| Kutu Buku | 100 – 299 |
| Bibliofil | ≥ 300 |

**Katalog Reward** (di-*seed* otomatis saat database pertama dibuat):

| Reward | Poin |
|---|---|
| Pembatas Buku Eksklusif | 30 |
| Voucher Diskon Rp15.000 | 50 |
| Tote Bag Aksara | 100 |
| Voucher Buku Gratis (s.d. Rp75.000) | 150 |

**Kategori Buku** (`util/BookCategory.kt`): Fiksi, Non-Fiksi, Komik, Akademik.

---

## 🗂 Struktur Project (MVVM)

```
com.aksara.membership
├── AksaraApp.kt                 # Application — container dependency (DI sederhana)
├── MainActivity.kt              # Host Compose + pasang ViewModel & NavGraph
│
├── data/                        # ===== LAYER DATA =====
│   ├── entity/
│   │   ├── Member.kt            # Tabel members
│   │   ├── Transaction.kt       # Tabel transactions (+ judul & kategori buku)
│   │   └── Reward.kt            # Tabel rewards
│   ├── dao/
│   │   ├── MemberDao.kt
│   │   ├── TransactionDao.kt
│   │   └── RewardDao.kt
│   ├── database/
│   │   └── AksaraDatabase.kt    # RoomDatabase + seed reward default
│   └── repository/
│       └── AksaraRepository.kt  # Single source of truth (ViewModel ↔ DAO)
│
├── ui/                          # ===== LAYER PRESENTATION =====
│   ├── theme/                   # Color, Type, Theme (Material 3)
│   ├── navigation/
│   │   ├── Screen.kt            # Definisi rute
│   │   └── AppNavGraph.kt       # NavHost & alur antar layar
│   ├── viewmodel/
│   │   └── MembershipViewModel.kt   # State + business logic
│   ├── components/
│   │   └── CommonComponents.kt  # TopBar & tombol reusable
│   └── screens/                 # 1 file per layar (10 layar)
│
└── util/                        # ===== UTILITY =====
    ├── PointCalculator.kt       # Aturan poin
    ├── MemberTier.kt            # Logika tier
    ├── BookCategory.kt          # Daftar kategori buku
    ├── Formatters.kt            # Format Rupiah & tanggal Indonesia
    └── QrCodeGenerator.kt       # Generate bitmap QR Code
```

---

## 🔄 Arsitektur

Aliran data satu arah mengikuti pola MVVM:

```
   Compose UI (screens)
         │  event (klik, input)
         ▼
   MembershipViewModel  ──── StateFlow ────►  UI (reactive)
         │
         ▼
   AksaraRepository      (satu-satunya pintu ke data)
         │
         ▼
   Room Database  →  MemberDao · TransactionDao · RewardDao
```

- UI **tidak pernah** mengakses DAO langsung — selalu lewat ViewModel → Repository.
- Perubahan data (poin, transaksi) otomatis terpancar ke UI melalui `Flow`/`StateFlow`.

---

## 🗄 Desain Database

**Tabel `members`**

| Field | Tipe | Keterangan |
|---|---|---|
| id | Long | Primary Key, auto-generate |
| memberNumber | String | mis. `AKS00001` |
| name | String | |
| email | String | |
| phone | String | |
| status | String | default `Active` |
| totalPoints | Int | default 0 |
| joinDate | Long | timestamp registrasi |

**Tabel `transactions`**

| Field | Tipe | Keterangan |
|---|---|---|
| id | Long | Primary Key, auto-generate |
| memberId | Long | Foreign Key → `members.id` (CASCADE) |
| bookTitle | String | judul buku |
| category | String | Fiksi / Non-Fiksi / Komik / Akademik |
| date | Long | timestamp transaksi |
| amount | Long | nominal pembelian (Rupiah) |
| pointsEarned | Int | hasil `amount / 10.000` |

**Tabel `rewards`**

| Field | Tipe | Keterangan |
|---|---|---|
| id | Long | Primary Key, auto-generate |
| name | String | nama reward |
| pointCost | Int | poin yang dibutuhkan |
| description | String | deskripsi reward |

> Tier member **tidak disimpan** — dihitung dari `totalPoints` agar tidak ada
> data ganda yang bisa tidak sinkron.

---

## 🧭 Daftar Layar & Navigasi

**10 layar:** Splash · Login · Register · Home · Member Card · Transaction History ·
Add Transaction · Rewards · Reward Detail · Profile.

```
Splash Screen
    ↓
Login Screen  ──►  Register Screen
    ↓
Home Screen (Dashboard)
    ├── Member Card Screen
    ├── Transaction History Screen
    │       └── Add Transaction Screen
    ├── Reward Screen
    │       └── Reward Detail Screen  (redeem)
    └── Profile Screen  (edit / logout)
```

---

## ▶ Cara Menjalankan

1. Buka folder project ini di **Android Studio** (Ladybug atau lebih baru).
2. Tunggu **Gradle Sync** selesai (perlu koneksi internet pada kali pertama).
3. Pilih emulator / perangkat fisik (**minSdk 24 / Android 7.0**).
4. Klik **Run** ▶.

---

## 🧪 Alur Uji Coba Cepat

1. **Splash → Start → Login → "Daftar di sini"**.
2. Isi Nama, Email, No HP → **Daftar** → masuk Home.
3. **My Card** untuk melihat QR Code + tier.
4. **Transactions → Add Transaction**: isi judul buku, pilih kategori, isi
   `150000` → dapat **15 poin**.
5. **Rewards** → pilih reward → **Redeem** → konfirmasi → sukses.
6. **Profile** untuk mengubah data atau logout.

---

## ✅ Status Definition of Done

| Kriteria | Status |
|---|---|
| Registrasi member berjalan | ✅ |
| Data tersimpan di Room Database | ✅ |
| Membership card + QR Code tampil | ✅ |
| Poin dihitung otomatis | ✅ |
| Riwayat transaksi (judul + kategori) tampil | ✅ |
| Level member otomatis sesuai poin | ✅ |
| Reward dapat ditukar | ✅ |
| Navigasi antar halaman berfungsi | ✅ |
| Tidak ada error saat pengujian | ⏳ verifikasi via Gradle Sync + Run |

---

## 🧷 Catatan Teknis

- **Aturan poin:** `poin = nominal / 10.000` (pembagian bulat).
- **Reward default** dibuat otomatis saat database pertama kali dibuat (seed).
- **Sesi login** disimpan di ViewModel (in-memory); dapat dikembangkan ke DataStore.
- **QR Code** dibuat lokal via ZXing — aplikasi tidak butuh izin internet.
- **`amount` & `id` bertipe `Long`** (lebih tepat untuk Rupiah tanpa desimal).
- **Versi database = 2** dengan `fallbackToDestructiveMigration()`: saat skema
  berubah, database lama dibangun ulang (data uji lama terhapus — aman untuk dev).

---

## 🚀 Future Enhancement (v2.0)

Firebase Authentication · Cloud Sync · Barcode Scanner (scan ISBN) · katalog & stok
buku · statistik genre favorit · push notification · analytics dashboard untuk
pemilik · ekspor kartu member ke PDF.
