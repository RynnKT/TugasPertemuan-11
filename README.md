# Laporan Tugas Pemrograman Berorientasi Obyek

## 🎯 Tujuan
1. Memahami cara membuat persistence unit yang menghubungkan aplikasi dengan basis data
2. Membuat Entity Class dari skema basis data yang sudah ada
3. Menggunakan Entity Class untuk melakukan operasi CRUD (Create, Read, Update, Delete)
4. Memanfaatkan anotasi JPA seperti `@Entity`, `@Id`, `@NamedQuery` untuk memetakan relasi objek-relasional

## 📖 Penjelasan Singkat

### A. Pendahuluan
- **Persistence** mengacu pada kemampuan aplikasi untuk menyimpan status data ke dalam media penyimpanan permanen
- **Fungsi Persistence**:
  - Keabadian Data: Data tidak hilang meskipun aplikasi di-restart
  - Konsistensi: Data tersimpan dengan struktur yang jelas dan teratur
- Entity Class berperan sebagai representasi objek dari tabel basis data
- Menggunakan Java Persistence API (JPA) untuk transaksi data yang terstandarisasi

### B. Praktikum
#### 1. Membuat Class Persistence
- Entity Class dibuat dari database menggunakan fitur **New Entity Classes from Database**
- Package dan file `persistence.xml` berhasil dibuat di folder `META-INF`
- Entity Class yang dibuat:
  - `KelasemenLigaSawah.java`
  - `Ligasawah.java`

#### 2. Implementasi EntityManager dan NamedQuery
- Kode dimodifikasi untuk menggunakan `EntityManager` dan `NamedQuery`
- Implementasi meliputi:
  - Tombol **Simpan** pada JDialog Insert
  - Tombol **Perbarui** pada JDialog Update
  - Tombol **Hapus** pada JDialog Delete
  - Method untuk **upload CSV** pada JFrame utama

#### 3. Menampilkan Data ke Tabel
- Source code disediakan untuk menampilkan data ke dalam tabel dengan implementasi persistence

### C. Kesimpulan
1. Persistence dengan Entity Class berhasil diimplementasikan untuk menghubungkan objek aplikasi dengan basis data
2. Entity Class memudahkan manipulasi data dengan merepresentasikan tabel basis data
3. JPA menyediakan cara terstandarisasi untuk operasi CRUD menggunakan `EntityManager`
4. Anotasi JPA seperti `@Entity`, `@Id`, `@NamedQuery` mempermudah proses mapping objek-relasional

## 🛠️ Teknologi yang Digunakan
- Java
- Java Persistence API (JPA)
- EntityManager
- NamedQuery
- NetBeans IDE
- Database 
