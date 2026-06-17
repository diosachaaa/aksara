# Aksara — Bookstore Membership & Loyalty App

Aplikasi **member digital toko buku Aksara**. Pelanggan mendaftar sebagai member,
**berbelanja** buku/ATK/menu kafe lewat katalog, mengumpulkan poin otomatis dari
tiap transaksi, melihat **kartu member digital + QR Code**, dan menukar poin dengan
**reward bervoucher**.

Mini Project Evaluasi Akhir mata kuliah *Pemrograman Perangkat Bergerak* —
Android modern (Kotlin + Jetpack Compose + Room).

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
10. [Catatan Teknis](#-catatan-teknis)

---

## 🛠 Teknologi

| Komponen | Versi |
|---|---|
| Bahasa | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material 3 (Compose BOM 2024.12.01) |
| Database | Room 2.8.4 (compiler via KSP 2.3.2) |
| Arsitektur | MVVM + Repository Pattern |
| Navigasi | Navigation Compose 2.8.5 (+ bottom navigation) |
| State | StateFlow / `collectAsState` |
| QR Code | ZXing 3.5.3 (`com.google.zxing:core`) — dibuat lokal, tanpa internet |
| Build | AGP 9.1.1 · Gradle 9.3.1 · JDK 17 · minSdk 24 · target/compileSdk 35 |

---

## ✨ Fitur

1. **Splash Screen** — branding & tombol masuk.
2. **Login & Registrasi** — masuk dengan email; registrasi (Nama, Email, No HP)
   dengan validasi. Email duplikat ditolak.
3. **Home / Dashboard** — sapaan, ringkasan poin & tier, aksi cepat (Belanja,
   Riwayat, Reward). Dilengkapi **bottom navigation** (Home · Kartu · Reward · Profil).
4. **Katalog Belanja** — daftar produk (Buku, ATK, Kafe, Lainnya) yang bisa
   ditambahkan ke keranjang.
5. **Keranjang (Cart)** — atur jumlah item, lihat total, lalu **checkout**. Sistem
   membuat transaksi (dikelompokkan per kategori) dan menambahkan poin otomatis.
6. **Digital Membership Card** — nama, nomor member (mis. `AKS00001`), **tier dengan
   progress bar**, total poin, dan **QR Code**.
7. **Riwayat Transaksi** — daftar pembelian (tanggal, kategori, nominal, poin).
8. **Rewards** — katalog hadiah; mengetuk satu reward membuka detail.
9. **Reward Detail + Redeem** — verifikasi poin cukup, lalu **menghasilkan kode
   voucher** (mis. `AKS-7K2P-9XQ4`).
10. **Riwayat Penukaran** — daftar reward yang sudah ditukar beserta kode vouchernya.
11. **Profile** — edit data member, **foto profil (avatar)** dari galeri, **toggle
    dark mode**, dan logout.

---

## 📐 Aturan Bisnis

**Perhitungan Poin** (`util/PointCalculator.kt`)
```
poin = total_belanja / 10.000   (pembulatan ke bawah)
Contoh: Rp150.000 → 15 poin
```

**Tier Keanggotaan** (`util/MemberTier.kt`) — dihitung dari total poin (computed),
lengkap dengan progress menuju tier berikutnya:

| Tier | Rentang Poin |
|---|---|
| Bronze | 0 – 99 |
| Silver | 100 – 299 |
| Gold | ≥ 300 |

**Katalog Reward** (di-*seed* otomatis):

| Reward | Poin |
|---|---|
| Pembatas Buku Eksklusif | 50 |
| Tote Bag Aksara | 100 |
| Voucher Buku Gratis | 150 |
| Diskon 50% Buku | 250 |
| Merchandise Eksklusif | 400 |

**Kategori Produk/Transaksi** (`util/TransactionCategory.kt`): Buku, ATK (Alat
Tulis), Kafe, Lainnya.

**Kode Voucher** (`util/CodeGenerator.kt`): format `AKS-XXXX-XXXX` (acak).

---

## 🗂 Struktur Project (MVVM)

```
com.aksara.membership                 (42 file Kotlin)
├── AksaraApp.kt                  # Application — container dependency
├── MainActivity.kt               # Host Compose + dark mode + NavGraph
│
├── data/                         # ===== LAYER DATA =====
│   ├── entity/                   # Member, Product, Transaction, Reward, Redemption
│   ├── dao/                      # 5 DAO (Member, Product, Transaction, Reward, Redemption)
│   ├── database/                 # AksaraDatabase (+ seed produk & reward)
│   └── repository/               # AksaraRepository (single source of truth)
│
├── ui/                           # ===== LAYER PRESENTATION =====
│   ├── viewmodel/                # MembershipViewModel (state, cart, aksi)
│   ├── theme/                    # Color, Type, Theme (light + dark)
│   ├── navigation/               # Screen (rute) + AppNavGraph (alur + bottom bar)
│   ├── components/               # AksaraBottomBar, AvatarImage, CommonComponents
│   └── screens/                  # 12 layar
│
└── util/                         # ===== UTILITY =====
    ├── PointCalculator.kt        # aturan poin
    ├── MemberTier.kt             # tier + progress
    ├── TransactionCategory.kt    # kategori + ikon
    ├── CodeGenerator.kt          # kode voucher
    ├── ImageStorage.kt           # simpan & muat foto profil
    ├── Formatters.kt             # format Rupiah & tanggal
    └── QrCodeGenerator.kt        # generate QR Code
```

---

## 🔄 Arsitektur

Aliran data satu arah (MVVM):

```
   ui/screens (Compose)
        │  event (klik, input, checkout)
        ▼
   MembershipViewModel  ──── StateFlow ────►  UI (reactive)
        │   (session, keranjang, dark mode)
        ▼
   AksaraRepository      (satu-satunya pintu ke data)
        │
        ▼
   Room Database  →  Member · Product · Transaction · Reward · Redemption DAO
```

- UI tidak pernah mengakses DAO langsung — selalu lewat ViewModel → Repository.
- **Keranjang** disimpan in-memory di ViewModel (`Map<productId, qty>`).
- Perubahan poin/transaksi/redemption otomatis terpancar ke UI lewat `Flow`/`StateFlow`.

---

## 🗄 Desain Database

Room versi **4**, 5 tabel.

**`members`** — id, memberNumber, name, email, phone, status, totalPoints,
`photoPath` (foto profil), joinDate.

**`products`** (katalog) — id, category (BUKU/ATK/KAFE/LAINNYA), name, author, price,
colorHex (warna sampul placeholder).

**`transactions`** — id, memberId (FK→members, CASCADE), date, amount, pointsEarned,
category.

**`rewards`** — id, name, pointCost, description.

**`redemptions`** — id, memberId (FK→members, CASCADE), rewardName, pointCost,
voucherCode, date.

> Saat database dibuat/dibangun ulang, tabel `rewards` & `products` otomatis diisi
> data awal (seed) memakai SQL mentah agar selalu tersedia. Tier member tidak
> disimpan — dihitung dari `totalPoints`.

---

## 🧭 Daftar Layar & Navigasi

**12 layar.** Empat di antaranya adalah **tab bottom bar**: Home, Kartu, Reward, Profil.

```
Splash
  ↓
Login  ──►  Register
  ↓
┌─────────────── Bottom Bar ───────────────┐
│  Home   ·   Kartu   ·   Reward   ·  Profil │
└───────────────────────────────────────────┘
   │            │            │          │
   │            │            │          ├─ Riwayat Penukaran
   │            │            │          └─ (toggle dark mode, edit avatar, logout)
   │            │            └─ Reward Detail (redeem → voucher)
   │            │               Riwayat Penukaran
   │            └─ (QR Code + tier progress)
   ├─ Belanja (Katalog) ─► Keranjang ─► checkout ─► kembali ke Home
   └─ Riwayat Transaksi
```

---

## ▶ Cara Menjalankan

1. Buka folder project di **Android Studio** (versi yang mendukung AGP 9.x).
2. Tunggu **Gradle Sync** selesai (butuh internet pada kali pertama).
3. Pilih emulator / perangkat (**minSdk 24 / Android 7.0**).
4. Klik **Run** ▶.

---

## 🧪 Alur Uji Coba Cepat

1. **Splash → Login → "Daftar di sini"** → isi data → masuk Home.
2. **Belanja** → tambahkan beberapa produk ke keranjang → buka **Keranjang** →
   **Bayar**. Poin bertambah otomatis (Rp10.000 = 1 poin).
3. **Kartu** → lihat QR Code, tier, dan progress poin.
4. **Reward** → pilih reward → **Tukar** → dapat **kode voucher** → cek di
   **Riwayat Penukaran**.
5. **Profil** → ganti foto, aktifkan **dark mode**, atau logout.

---

## 🧷 Catatan Teknis

- **Poin:** `total / 10.000` (Rp10.000 = 1 poin).
- **Checkout** membuat satu transaksi per kategori belanja, lalu mengakumulasi poin.
- **Reward & produk** diisi otomatis saat database dibuat (seed, idempotent).
- **Kode voucher** dibuat acak per penukaran dan dicatat di tabel `redemptions`.
- **Foto profil** disalin ke penyimpanan internal aplikasi (`ImageStorage`); pemilihan
  gambar memakai `GetContent()` sehingga tidak butuh izin penyimpanan.
- **Dark mode** disimpan di ViewModel (in-memory) dan diterapkan via `AksaraTheme`.
- **QR Code** dibuat lokal via ZXing — tanpa izin internet.
- **`amount`, `price`, `id` bertipe `Long`** (tepat untuk Rupiah tanpa desimal).

> ⚠️ **Catatan build:** `AksaraDatabase` masih memakai `fallbackToDestructiveMigration()`
> tanpa parameter, yang **deprecated sejak Room 2.7+**. Disarankan menggantinya
> dengan `fallbackToDestructiveMigration(dropAllTables = true)` agar bebas peringatan
> dan aman terhadap versi Room mendatang.

---

## 🚀 Future Enhancement

Firebase Authentication · Cloud Sync · Barcode/ISBN scanner · pencarian & filter
katalog · statistik kategori favorit · push notification · checkout dengan metode
pembayaran · dashboard analytics untuk pemilik.
