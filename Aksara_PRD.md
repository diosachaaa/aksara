# PRODUCT REQUIREMENTS DOCUMENT (PRD)
# Aksara — Bookstore Membership & Loyalty App

> Mini Project untuk Evaluasi Akhir mata kuliah **Pemrograman Perangkat Bergerak**

---

## 1. Informasi Produk

| | |
|---|---|
| **Nama Produk** | Aksara — Membership & Loyalty App |
| **Versi** | 2.0 |
| **Platform** | Android |
| **Teknologi** | Kotlin · Jetpack Compose · Room Database · MVVM Architecture · Navigation Compose · StateFlow · Material Design 3 · ZXing (QR Code) |
| **Target Pengguna** | Member (Pelanggan) Toko Buku · Pemilik Toko Buku |

---

## 2. Latar Belakang

Toko buku saat ini masih banyak menggunakan kartu member fisik untuk program loyalitas pelanggan. Kartu fisik memiliki beberapa kelemahan:

- Mudah hilang atau rusak
- Sulit diperbarui (jika ganti tier/level)
- Membutuhkan biaya cetak
- Tidak dapat menampilkan riwayat pembelian
- Tidak ada katalog produk digital yang bisa diakses pelanggan secara mandiri

Untuk meningkatkan pengalaman pelanggan, diperlukan aplikasi digital membership yang memungkinkan pelanggan memiliki kartu member digital, **berbelanja langsung dari katalog produk**, mengumpulkan poin dari setiap transaksi, melihat riwayat pembelian, dan menukarkan poin dengan reward secara otomatis.

---

## 3. Tujuan Produk

Membangun aplikasi Android yang memungkinkan:

- Registrasi & login member
- Penyimpanan data member secara lokal (offline-first)
- Digital Membership Card dengan QR Code
- Penjelajahan katalog produk (buku, alat tulis, menu kafe, dan lainnya)
- Belanja melalui keranjang dan checkout dengan perhitungan poin otomatis
- Pencatatan riwayat transaksi per kategori produk
- Penukaran poin dengan reward yang menghasilkan kode voucher unik
- Pencatatan riwayat penukaran reward
- Penentuan tier member otomatis berdasarkan total poin
- Personalisasi profil (foto, data diri) dan preferensi tampilan (mode gelap)

---

## 4. Problem Statement

Pelanggan sering kehilangan kartu member fisik sehingga poin yang sudah dikumpulkan tidak dapat digunakan. Toko buku juga kesulitan melakukan pencatatan transaksi member secara manual, serta belum memiliki kanal digital yang memungkinkan pelanggan menjelajah produk dan bertransaksi secara mandiri.

---

## 5. Success Metrics

**Functional Metrics**
- Member dapat didaftarkan dan login kembali
- Data tersimpan di Room Database
- Katalog produk tampil dan dapat ditambahkan ke keranjang
- Checkout berhasil membuat transaksi dan menambah poin secara otomatis
- Riwayat transaksi tampil per kategori
- Reward dapat ditukarkan dan menghasilkan kode voucher
- Riwayat penukaran (redemption) tampil
- Tier member berubah otomatis sesuai akumulasi poin

**Technical Metrics**
- Crash rate < 2%
- Data tersimpan lokal & persisten (bertahan setelah aplikasi ditutup)
- Waktu loading < 2 detik
- Tidak membutuhkan koneksi internet (offline-first, termasuk pembuatan QR Code)

---

## 6. User Persona

**Persona 1**
- **Nama:** Sari
- **Umur:** 20 Tahun
- **Pekerjaan:** Mahasiswi
- **Kebutuhan:**
  - Berbelanja buku & alat tulis tanpa harus ke kasir untuk mengecek katalog
  - Mengumpulkan poin dari setiap transaksi
  - Melihat reward yang tersedia
  - Tidak ingin membawa kartu fisik

**Persona 2**
- **Nama:** Dimas
- **Umur:** 33 Tahun
- **Pekerjaan:** Karyawan Swasta (pembaca aktif)
- **Kebutuhan:**
  - Mencatat setiap pembelian secara otomatis lewat checkout agar poin terus terkumpul
  - Memantau tier keanggotaan dan progres menuju tier berikutnya
  - Menukar poin dengan reward (voucher diskon, tote bag, merchandise)
  - Melihat riwayat reward yang pernah ditukar beserta kode vouchernya

---

## 7. Scope Produk

**In Scope**
- Registrasi & Login Member
- Dashboard Member (Home)
- Katalog Produk (Buku, ATK, Kafe, Lainnya)
- Keranjang Belanja & Checkout
- Membership Card + QR Code
- Point System (otomatis dari checkout)
- Transaction History (per kategori)
- Member Tier System (Bronze/Silver/Gold)
- Redeem Reward dengan kode voucher
- Riwayat Penukaran Reward
- Profil Member (edit data, foto profil)
- Mode Gelap (Dark Mode)
- Room Database

**Out of Scope**
- Online Payment / payment gateway
- Cloud Database / Cloud Sync
- Login Google / OAuth Authentication
- Push Notification
- Multi Device Sync
- Manajemen stok inventaris toko (penambahan/pengurangan stok oleh admin)
- Barcode/ISBN Scanner

---

## 8. User Flow

1. User membuka aplikasi (Splash Screen)
2. User mendaftar (Register) atau login dengan email yang sudah terdaftar
3. Data member disimpan/dibaca dari Room Database
4. User menjelajah Katalog Produk dan menambahkan item ke Keranjang
5. User melakukan Checkout
6. Sistem mengelompokkan item per kategori, menghitung poin, dan mencatat transaksi
7. Total poin member bertambah secara otomatis; tier diperbarui jika ambang batas terlampaui
8. User melihat Kartu Member (QR Code, tier, total poin) atau Riwayat Transaksi
9. User memilih Reward dan melakukan Redeem
10. Sistem memverifikasi poin cukup, mengurangi poin, dan menghasilkan kode voucher
11. Kode voucher tersimpan di Riwayat Penukaran
12. User dapat mengatur profil (edit data, foto, mode gelap) atau logout

---

## 9. Functional Requirements

### FR-01 Registrasi Member
**Deskripsi:** Pengguna dapat membuat akun member baru.
- **Input:** Nama, Email, Nomor HP
- **Output:** Data member tersimpan dengan nomor member otomatis
- **Acceptance Criteria:**
  - Semua field wajib diisi
  - Email yang sudah terdaftar ditolak dengan pesan kesalahan
  - Member baru otomatis memiliki nomor member berformat `AKS00001` dan 0 poin

### FR-02 Login Member
**Deskripsi:** Pengguna yang sudah terdaftar dapat masuk menggunakan email.
- **Input:** Email
- **Output:** Sesi member aktif
- **Acceptance Criteria:**
  - Email yang tidak ditemukan menampilkan pesan kesalahan
  - Login berhasil mengarahkan ke Dashboard (Home)

### FR-03 Dashboard Member (Home)
**Deskripsi:** Menampilkan ringkasan akun member yang sedang login: sapaan nama, total poin, tier, dan menu navigasi ke fitur utama.
- **Acceptance Criteria:**
  - Data member tampil dari Room Database
  - Total poin & tier ter-update otomatis saat ada transaksi (reactive via StateFlow)

### FR-04 Katalog Produk
**Deskripsi:** Menampilkan daftar produk yang dapat dibeli, dikelompokkan per kategori.
- **Kategori:** Buku, ATK (Alat Tulis), Kafe, Lainnya
- **Informasi per produk:** Nama, kategori, keterangan singkat (penulis/catatan), harga
- **Acceptance Criteria:**
  - Daftar produk dimuat otomatis (data awal/seed) saat aplikasi pertama kali dijalankan
  - Produk dapat ditambahkan ke keranjang dari katalog

### FR-05 Keranjang & Checkout
**Deskripsi:** Mengelola item yang akan dibeli, lalu memprosesnya menjadi transaksi.
- **Input:** Daftar produk & jumlah masing-masing di keranjang
- **Output:** Transaksi tercatat per kategori, poin otomatis bertambah
- **Formula:** `1 Poin = Rp10.000` (dibulatkan ke bawah)
- **Contoh:** Checkout berisi buku senilai Rp200.000 dan kopi senilai Rp22.000 menghasilkan dua transaksi terpisah (kategori Buku +20 poin, kategori Kafe +2 poin), total +22 poin
- **Acceptance Criteria:**
  - Pengguna dapat menambah/mengurangi jumlah item sebelum checkout
  - Checkout dengan keranjang kosong tidak diproses
  - Setiap kategori dalam satu checkout dicatat sebagai transaksi tersendiri
  - Keranjang dikosongkan otomatis setelah checkout berhasil

### FR-06 Riwayat Transaksi
**Deskripsi:** Menampilkan transaksi member.
- **Data:** Tanggal, Kategori, Nominal, Poin yang diperoleh
- **Acceptance Criteria:**
  - Riwayat dapat dilihat kapan saja
  - Terurut dari transaksi terbaru

### FR-07 Redeem Reward
**Deskripsi:** Menukarkan poin dengan hadiah.

| Reward | Poin |
|---|---|
| Pembatas Buku Eksklusif | 50 Poin |
| Tote Bag Aksara | 100 Poin |
| Voucher Buku Gratis | 150 Poin |
| Diskon 50% Buku | 250 Poin |
| Merchandise Eksklusif | 400 Poin |

- **Output:** Kode voucher unik berformat `AKS-XXXX-XXXX`
- **Acceptance Criteria:**
  - Poin berkurang sejumlah biaya reward setelah redeem berhasil
  - Tidak bisa redeem jika poin tidak mencukupi
  - Kode voucher dibuat otomatis dan unik setiap kali redeem

### FR-08 Riwayat Penukaran (Redemption History)
**Deskripsi:** Menampilkan daftar reward yang sudah pernah ditukar oleh member.
- **Data:** Nama reward, poin yang digunakan, kode voucher, tanggal penukaran
- **Acceptance Criteria:**
  - Riwayat dapat dilihat kapan saja
  - Kode voucher tetap dapat dibaca/disalin oleh pengguna

### FR-09 Member Tier System
**Deskripsi:** Menentukan tier member secara otomatis berdasarkan total poin (computed, tidak disimpan terpisah di database).

| Tier | Rentang Poin |
|---|---|
| Bronze | 0 – 99 |
| Silver | 100 – 299 |
| Gold | ≥ 300 |

- **Acceptance Criteria:**
  - Tier dihitung dari total poin secara real-time
  - Ditampilkan beserta progres menuju tier berikutnya di Kartu Member

### FR-10 Membership Card
**Deskripsi:** Menampilkan kartu member digital.
- **Informasi:** Nama, Nomor Member (format `AKS00001`), Tier, Total Poin, QR Code
- **Acceptance Criteria:**
  - Data tampil sesuai database
  - QR Code dibuat secara lokal dari nomor member tanpa membutuhkan koneksi internet

### FR-11 Profil Member
**Deskripsi:** Pengguna dapat melihat dan mengubah data dirinya.
- **Input:** Nama, Email, Nomor HP, Foto Profil
- **Acceptance Criteria:**
  - Perubahan data tersimpan ke Room Database
  - Foto profil tersimpan ke penyimpanan internal aplikasi agar tetap tersedia setelah aplikasi ditutup
  - Tersedia opsi logout yang mengakhiri sesi & mengosongkan keranjang

### FR-12 Mode Gelap (Dark Mode)
**Deskripsi:** Pengguna dapat mengaktifkan/menonaktifkan tampilan gelap.
- **Acceptance Criteria:**
  - Perubahan tema diterapkan secara langsung ke seluruh tampilan aplikasi

---

## 10. Non Functional Requirements

**Performance**
- Startup < 3 detik
- Query database < 500 ms

**Reliability**
- Data tetap tersedia setelah aplikasi ditutup
- Proses seeding katalog & reward tidak menyebabkan aplikasi crash apabila gagal

**Usability**
- UI sederhana dan konsisten
- Material Design 3
- Mendukung mode terang & gelap

**Maintainability**
- Menggunakan MVVM
- Menggunakan Repository Pattern sebagai satu-satunya sumber akses data

---

## 11. Database Design

Aplikasi menggunakan Room Database dengan 5 tabel.

**Tabel Members**

| Field | Type | Keterangan |
|---|---|---|
| id | Long | Primary Key, auto-generate |
| memberNumber | String | format `AKS00001`, dibuat otomatis saat registrasi |
| name | String | |
| email | String | |
| phone | String | |
| status | String | default `"Active"` |
| totalPoints | Int | default 0 |
| photoPath | String? | path foto profil di penyimpanan internal |
| joinDate | Long | timestamp registrasi |

**Tabel Products**

| Field | Type | Keterangan |
|---|---|---|
| id | Long | Primary Key, auto-generate |
| category | String | BUKU / ATK / KAFE / LAINNYA |
| name | String | nama produk |
| author | String | penulis (buku) atau keterangan singkat |
| price | Long | harga produk |
| colorHex | String | warna sampul placeholder |

**Tabel Transactions**

| Field | Type | Keterangan |
|---|---|---|
| id | Long | Primary Key, auto-generate |
| memberId | Long | Foreign Key → Members.id (onDelete = CASCADE) |
| date | Long | timestamp transaksi |
| amount | Long | subtotal nominal per kategori |
| pointsEarned | Int | amount ÷ 10.000 |
| category | String | kategori produk dalam transaksi tersebut |

**Tabel Rewards**

| Field | Type | Keterangan |
|---|---|---|
| id | Long | Primary Key, auto-generate |
| name | String | nama reward |
| pointCost | Int | poin yang dibutuhkan |
| description | String | deskripsi reward |

**Tabel Redemptions**

| Field | Type | Keterangan |
|---|---|---|
| id | Long | Primary Key, auto-generate |
| memberId | Long | Foreign Key → Members.id (onDelete = CASCADE) |
| rewardName | String | nama reward yang ditukar |
| pointCost | Int | poin yang digunakan |
| voucherCode | String | kode voucher unik, format `AKS-XXXX-XXXX` |
| date | Long | timestamp penukaran |

> **Catatan:** Tier member **tidak disimpan** di database, melainkan dihitung dari field `totalPoints` (computed property) agar tidak ada data duplikat yang bisa tidak sinkron. Data tabel Products dan Rewards diisi otomatis (seed) saat database pertama kali dibuat atau dibangun ulang.

---

## 12. Screen List

- **Splash Screen** — Logo aplikasi & transisi awal
- **Login Screen** — Masuk dengan email member
- **Register Screen** — Form registrasi member (Nama, Email, No HP)
- **Home Screen** — Dashboard member: sapaan, ringkasan total poin & tier, menu navigasi ke fitur utama
- **Catalog Screen** — Daftar produk per kategori, dapat ditambahkan ke keranjang
- **Cart Screen** — Pengaturan jumlah item & proses checkout
- **Member Card Screen** — Kartu member digital + QR Code + tier
- **Transaction History Screen** — Daftar riwayat transaksi per kategori
- **Reward Screen** — Daftar hadiah yang dapat ditukar
- **Reward Detail Screen** — Detail reward + konfirmasi & proses redeem
- **Redemption History Screen** — Daftar reward yang sudah ditukar beserta kode voucher
- **Profile Screen** — Lihat & edit data member, ubah foto profil, mode gelap, logout

---

## 13. Navigation Structure

```
Splash Screen
    ↓
Login Screen  ──►  Register Screen
    ↓
┌─────────────────── Bottom Navigation ───────────────────┐
│   Home   │   Member Card   │   Rewards   │   Profile     │
└───────────────────────────────────────────────────────────┘
    │              │               │             │
    │              │               │             └── (edit profil / dark mode / logout)
    │              │               └── Reward Detail (redeem)
    │              │                       └── Redemption History
    │              └── (QR Code & progres tier)
    ├── Catalog Screen ──► Cart Screen ──► checkout ──► kembali ke Home
    └── Transaction History Screen
```

---

## 14. MVVM Architecture

```
Presentation Layer
  Compose UI (Screens)
  State Management (collectAsState)
        ↓ event
ViewModel Layer
  Session, Keranjang (in-memory), Dark Mode
  Business Logic ringan, StateFlow reaktif
        ↓
Repository Layer
  Satu-satunya sumber akses data
  register, login, checkout, redeem, observeProducts, dll.
        ↓
Room Database
  Member · Product · Transaction · Reward · Redemption
```

UI tidak pernah mengakses Room Database secara langsung — selalu melalui ViewModel dan Repository. Perhitungan poin dan pengelompokan transaksi per kategori ditangani di Repository (`checkout()`), bukan di UI maupun ViewModel.

---

## 15. Future Enhancement

**Version 3.0**
- Firebase Authentication
- Cloud Sync
- Online Payment / integrasi payment gateway
- Barcode/ISBN Scanner
- Manajemen stok produk oleh admin/pemilik toko
- Statistik kategori favorit member
- Push Notification (promo & produk baru)
- Analytics Dashboard untuk pemilik
- Ekspor Kartu Member ke PDF

---

## 16. Deliverables

Mahasiswa wajib menghasilkan:

1. Source Code Kotlin
2. Room Database Implementation
3. Jetpack Compose UI
4. MVVM Architecture
5. APK File
6. User Manual
7. Presentasi Demo Aplikasi

---

## 17. Definition of Done (DoD)

Proyek dianggap selesai jika:

- ✓ Registrasi & login member berjalan
- ✓ Data tersimpan di Room Database
- ✓ Katalog produk tampil dan dapat ditambahkan ke keranjang
- ✓ Checkout berhasil membuat transaksi dan poin bertambah otomatis
- ✓ Membership card + QR Code tampil
- ✓ Tier member berubah otomatis sesuai poin
- ✓ Riwayat transaksi (per kategori) tampil
- ✓ Reward dapat ditukar dan menghasilkan kode voucher
- ✓ Riwayat penukaran (redemption) tampil
- ✓ Profil dapat diubah (data & foto), serta mode gelap berfungsi
- ✓ Navigasi antar halaman berfungsi
- ✓ Tidak terdapat error saat pengujian
