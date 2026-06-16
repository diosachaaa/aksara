# PRODUCT REQUIREMENTS DOCUMENT (PRD)
# BookNook — Bookstore Membership Card App

> Mini Project untuk Evaluasi Akhir mata kuliah **Pemrograman Perangkat Bergerak**

---

## 1. Informasi Produk

| | |
|---|---|
| **Nama Produk** | BookNook Membership |
| **Versi** | 1.0 |
| **Platform** | Android |
| **Teknologi** | Kotlin · Jetpack Compose · Room Database · MVVM Architecture · Navigation Compose · StateFlow · Material Design 3 |
| **Target Pengguna** | Member (Pelanggan) Toko Buku · Pemilik Toko Buku |

---

## 2. Latar Belakang

Toko buku saat ini masih banyak menggunakan kartu member fisik untuk program loyalitas pelanggan. Kartu fisik memiliki beberapa kelemahan:

- Mudah hilang atau rusak
- Sulit diperbarui (jika ganti tier/level)
- Membutuhkan biaya cetak
- Tidak dapat menampilkan riwayat pembelian buku
- Tidak bisa menunjukkan buku/genre apa yang pernah dibeli member

Untuk meningkatkan pengalaman pelanggan, diperlukan aplikasi digital membership yang memungkinkan pelanggan memiliki kartu member digital, mengumpulkan poin dari setiap pembelian buku, melihat riwayat buku yang dibeli, dan memperoleh reward secara otomatis.

---

## 3. Tujuan Produk

Membangun aplikasi Android yang memungkinkan:

- Registrasi member
- Penyimpanan data member secara lokal (offline-first)
- Digital Membership Card dengan QR Code
- Pengumpulan poin dari transaksi pembelian buku
- Pencatatan riwayat transaksi beserta judul & kategori buku
- Penukaran poin dengan reward
- Penentuan level member otomatis berdasarkan poin

---

## 4. Problem Statement

Pelanggan sering kehilangan kartu member fisik sehingga poin yang sudah dikumpulkan tidak dapat digunakan. Toko buku juga kesulitan melakukan pencatatan transaksi member secara manual, serta tidak memiliki catatan kategori buku yang diminati pelanggan.

---

## 5. Success Metrics

**Functional Metrics**
- Member dapat didaftarkan
- Data tersimpan di Room Database
- Poin dihitung otomatis dari nominal pembelian
- Riwayat transaksi (judul + kategori + poin) tampil
- Reward dapat ditukarkan
- Level member berubah otomatis sesuai poin

**Technical Metrics**
- Crash rate < 2%
- Data tersimpan lokal & persisten
- Waktu loading < 2 detik

---

## 6. User Persona

**Persona 1**
- **Nama:** Sari
- **Umur:** 20 Tahun
- **Pekerjaan:** Mahasiswi
- **Kebutuhan:**
  - Mengumpulkan poin dari pembelian buku
  - Melihat reward yang tersedia
  - Melihat kembali buku apa saja yang pernah dibeli
  - Tidak ingin membawa kartu fisik

**Persona 2**
- **Nama:** Dimas
- **Umur:** 33 Tahun
- **Pekerjaan:** Karyawan Swasta (pembaca aktif)
- **Kebutuhan:**
  - Mencatat setiap pembelian buku agar poin terus terkumpul
  - Memantau tier keanggotaan dan progres menuju tier berikutnya
  - Menukar poin dengan reward (voucher/tote bag)

---

## 7. Scope Produk

**In Scope**
- Registrasi Member
- Dashboard Member
- Membership Card + QR Code
- Point System
- Transaction History (dengan judul & kategori buku)
- Member Level System
- Redeem Reward
- Room Database

**Out of Scope**
- Online Payment
- Cloud Database
- Login Google / Authentication
- Push Notification
- Multi Device Sync
- Katalog buku & stok inventory

---

## 8. User Flow

1. User membuka aplikasi (Splash Screen)
2. User mendaftarkan data member
3. Data disimpan ke Room Database
4. User melihat kartu member digital + QR Code
5. User mencatat transaksi pembelian buku
6. Sistem menghitung poin otomatis
7. Poin bertambah & level member diperbarui
8. User menukar poin dengan reward
9. Poin berkurang setelah redeem

---

## 9. Functional Requirements

### FR-01 Registrasi Member
**Deskripsi:** Pengguna dapat membuat akun member baru.
- **Input:** Nama, Email, Nomor HP
- **Output:** Data member tersimpan
- **Acceptance Criteria:**
  - Semua field wajib diisi
  - Email harus valid
  - Member baru otomatis berlevel "Pembaca" dengan 0 poin

### FR-02 Dashboard Member
**Deskripsi:** Menampilkan ringkasan akun member yang sedang login: sapaan nama, total poin, dan menu navigasi ke fitur utama (Kartu, Transaksi, Reward, Profil).
- **Acceptance Criteria:**
  - Data member tampil dari Room Database
  - Total poin ter-update otomatis saat ada transaksi (reactive via StateFlow)

### FR-03 Membership Card
**Deskripsi:** Menampilkan kartu member digital.
- **Informasi:** Nama, ID Member (format `BN-0001`), Level Member, Total Point, QR Code
- **Acceptance Criteria:**
  - Data tampil sesuai database
  - QR Code ter-generate dari ID Member

### FR-04 Tambah Transaksi
**Deskripsi:** Mencatat transaksi pembelian buku.
- **Input:** Judul Buku, Kategori Buku, Nominal Pembelian
- **Output:** Point otomatis bertambah, level diperbarui
- **Formula:** `1 Point = Rp10.000`
- **Contoh:** Pembelian Rp120.000 → 12 Point
- **Acceptance Criteria:**
  - Point dihitung otomatis
  - Riwayat tersimpan beserta judul & kategori

### FR-05 Riwayat Transaksi
**Deskripsi:** Menampilkan transaksi member.
- **Data:** Tanggal, Judul Buku, Kategori, Nominal, Point
- **Acceptance Criteria:**
  - Riwayat dapat dilihat kapan saja
  - Terurut dari transaksi terbaru

### FR-06 Redeem Reward
**Deskripsi:** Menukarkan point dengan hadiah.

| Reward | Point |
|---|---|
| Pembatas Buku Eksklusif | 30 Point |
| Voucher Diskon Rp15.000 | 50 Point |
| Tote Bag BookNook | 100 Point |
| Voucher Buku Gratis (s.d. Rp75.000) | 150 Point |

- **Acceptance Criteria:**
  - Point berkurang setelah redeem
  - Tidak bisa redeem jika point kurang

### FR-07 Member Level System
**Deskripsi:** Menentukan level member secara otomatis berdasarkan total poin (computed, tidak disimpan terpisah).

| Level | Rentang Poin |
|---|---|
| Pembaca | 0 – 99 |
| Kutu Buku | 100 – 299 |
| Bibliofil | 300+ |

- **Acceptance Criteria:**
  - Level dihitung dari total poin secara real-time

---

## 10. Non Functional Requirements

**Performance**
- Startup < 3 detik
- Query database < 500 ms

**Reliability**
- Data tetap tersedia setelah aplikasi ditutup

**Usability**
- UI sederhana
- Material Design 3

**Maintainability**
- Menggunakan MVVM
- Menggunakan Repository Pattern

---

## 11. Database Design

**Tabel Members**

| Field | Type | Keterangan |
|---|---|---|
| id | Integer | Primary Key, auto-generate |
| name | Text | |
| email | Text | |
| phone | Text | |
| points | Integer | default 0 |
| joinDate | String | tanggal registrasi |

**Tabel Transactions**

| Field | Type | Keterangan |
|---|---|---|
| id | Integer | Primary Key, auto-generate |
| memberId | Integer | Foreign Key → Members.id |
| bookTitle | Text | judul buku |
| category | Text | Fiksi / Non-Fiksi / Komik / Akademik |
| amount | Double | nominal pembelian |
| pointEarned | Integer | amount ÷ 10.000 |
| date | String | tanggal transaksi |

> **Catatan:** Level member **tidak disimpan** di database, melainkan dihitung dari field `points` (computed property) agar tidak ada data duplikat yang bisa tidak sinkron.

---

## 12. Screen List

- **Splash Screen** — Logo toko buku & tombol Start
- **Login Screen** — Masuk dengan email member
- **Register Screen** — Form registrasi member (Nama, Email, No HP)
- **Home Screen** — Dashboard member: sapaan, ringkasan total poin, dan menu navigasi ke fitur utama
- **Member Card Screen** — Kartu member digital + QR Code + tier
- **Transaction History Screen** — Daftar riwayat pembelian (judul, kategori, tanggal, nominal, poin)
- **Add Transaction Screen** — Input transaksi (judul, kategori, nominal) & hitung poin otomatis
- **Reward Screen** — Daftar hadiah yang dapat ditukar
- **Reward Detail Screen** — Detail reward + konfirmasi & proses redeem
- **Profile Screen** — Lihat & edit data member, logout

---

## 13. Navigation Structure

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

## 14. MVVM Architecture

```
Presentation Layer
  Compose UI
  State Management
        ↓
ViewModel Layer
  Business Logic
  StateFlow
        ↓
Repository Layer
  Data Access
        ↓
Room Database
  Member Table
  Transaction Table
```

---

## 15. Future Enhancement

**Version 2.0**
- Firebase Authentication
- Cloud Sync
- Barcode Scanner (scan ISBN buku)
- Katalog & stok buku
- Statistik genre favorit member
- Push Notification (promo & buku baru)
- Membership Tier dengan benefit khusus
- Analytics Dashboard untuk pemilik
- PDF Membership Card Export

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

- ✓ Registrasi member berjalan
- ✓ Data tersimpan di Room Database
- ✓ Membership card + QR Code tampil
- ✓ Poin dihitung otomatis
- ✓ Riwayat transaksi (judul + kategori) tampil
- ✓ Level member berubah otomatis sesuai poin
- ✓ Reward dapat ditukar
- ✓ Navigasi antar halaman berfungsi
- ✓ Tidak terdapat error saat pengujian
