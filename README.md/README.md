# README_Tugas 2 Pemrograman Berorientasi Objek_REST API Manajemen Event & Ticketing

## 1. Deskripsi Proyek
### 1.1 Ringkasan
Proyek ini merupakan REST API Sistem Manajemen Event dan Ticketing yang dibuat untuk memenuhi Tugas 2 Mata Kuliah Pemrograman Berorientasi Objek. API ini menangani seluruh alur data operasional, mulai dari manajemen pengguna (User), pengaturan lokasi (Venue), pembuatan acara (Event), transaksi pembelian tiket (Ticket), hingga penarikan laporan penjualan (Report).

### 1.2 Konsep OOP Pada Proyek
Sistem ini dibangun menggunakan Java 11 native tanpa bantuan framework seperti Spring Boot atau Hibernate. Fokus utama proyek ini adalah menerapkan konsep OOP:

- Inheritance & Abstract Class: Kelas Event dijadikan abstract class (induk) yang diturunkan ke kelas Concert, Seminar, dan SportMatch untuk menghindari duplikasi kode yang tidak perlu.

- Polymorphism (Runtime): Perhitungan harga tiket menggunakan method calculateTicketPrice() yang perilakunya dinamis. Tiap jenis event punya rumus perkalian harga yang berbeda untuk kategori VIP dan Regular saat di-load dari database.

- Interface : Fitur pengembalian dana dikendalikan lewat interface Refundable. Interface ini dipasang secara selektif hanya pada Concert dan Seminar untuk menghitung potongan biaya berdasarkan sisa hari, sedangkan SportMatch diatur tidak bisa refund.

- Data Persistence (SQL Murni): Semua transaksi data terhubung ke SQLite menggunakan JDBC dengan query manual. Logika bisnis seperti pemotongan otomatis kapasitas kursi saat tiket dipesan juga divalidasi langsung di dalam sistem ini.

## 2. Cara menjalankan server (langkah-langkah dari clone sampai server berjalan)
Berikut adalah langkah-langkah untuk menjalankan server mulai dari proses clone repositori hingga server berjalan.
A. Clone Repositori Buka terminal atau command prompt dan jalankan perintah berikut:
    git clone https://github.com/Eriixcc/tugas2-oop-b-kelompok7.git
    cd tugas2-oop-b-kelompok7

B. Pindah ke Direktori src Pastikan berada di direktori src sebelum melakukan kompilasi.
    cd src

C. Kompilasi Kode Program Jalankan perintah kompilasi sesuai sistem operasi (pastikan folder lib sejajar dengan src dan berisi file JAR yang dibutuhkan):
    Windows:
    javac -cp ".;../lib/*" App.java model/*.java service/*.java repository/*.java handler/*.java exception/*.java database/*.java server/*.java
    Linux / Mac:
    javac -cp ".:../lib/*" App.java model/*.java service/*.java repository/*.java handler/*.java exception/*.java database/*.java server/*.java

D. Jalankan Server Windows:
    java -cp ".;../lib/*" App
    Linux / Mac:
    java -cp ".:../lib/*" App

E. Server Berjalan Server akan berjalan secara lokal. Anda dapat mengujinya melalui browser, Curl, atau Postman.
    http://localhost:8080

## 3. Daftar endpoint API lengkap beserta contoh request dan response
Berikut adalah 18 daftar endpoint kami :
| 1 | GET | /api/users | User |
| 2 | GET | /api/users/{id} | User |
| 3 | POST | /api/users | User |
| 4 | PUT | /api/users/{id} | User |
| 5 | GET | /api/venues | Venue |
| 6 | GET | /api/venues/{id} | Venue |
| 7 | POST | /api/venues | Venue |
| 8 | PUT | /api/venues/{id} | Venue |
| 9 | GET | /api/events | Event |
| 10 | GET | /api/events/{id} | Event |
| 11 | POST | /api/events | Event |
| 12 | PUT | /api/events/{id} | Event |
| 13 | GET | /api/tickets | Ticket |
| 14 | GET | /api/tickets/{id} | Ticket |
| 15 | POST | /api/tickets | Ticket |
| 16 | PUT | /api/tickets/{id}/refund | Ticket |
| 17 | GET | /api/events/price-summary | Report |
| 18 | GET | /api/reports/sales?eventId={id} | Report |

## 4. Struktur Proyek
project/
|-- lib/                           # JAR library (Jackson Databind & SQLite JDBC Driver)
+-- src/
    |-- App.java                   # Entry point utama aplikasi untuk inisialisasi database dan menjalankan HTTP server.
    |
    |-- server/                    # [TEMPLATE - Core HTTP Engine & Routing]
    |   |-- Server.java            # Mengatur konfigurasi port server, threading, dan mapping routing URL.
    |   |-- Request.java           # Wrapper HTTP request untuk membaca path parameter, query parameter, dan body JSON.
    |   |-- Response.java          # Wrapper HTTP response untuk mempermudah pengiriman status JSON (success/error).
    |   +-- RouteHandler.java      # Interface standardisasi yang harus diimplementasikan oleh setiap file handler.
    |
    |-- database/                  # [TEMPLATE - Manajemen Database]
    |   +-- DatabaseManager.java   # Mengelola koneksi JDBC ke SQLite dan mengeksekusi DDL pembuatan tabel awal.
    |
    |-- model/                     # [TUGAS MAHASISWA - Entitas & Hierarki OOP]
    |   |-- Event.java             # Abstract class sebagai cetak biru (blueprint) utama dari seluruh jenis acara.
    |   |-- Concert.java           # Subclass dari Event untuk acara konser musik (Mengimplementasikan Interface Refundable).
    |   |-- Seminar.java           # Subclass dari Event untuk acara edukasi/seminar (Mengimplementasikan Interface Refundable).
    |   |-- SportMatch.java        # Subclass dari Event untuk pertandingan olahraga (TIDAK mendukung fitur refund).
    |   |-- Ticket.java            # Kelas objek yang menyimpan data transaksi tiket, kuantitas, harga, dan status aktif.
    |   |-- User.java              # Kelas objek untuk menyimpan data profil pengguna beserta rolenya (buyer/organizer).
    |   |-- Venue.java             # Kelas objek yang menampung informasi lokasi acara beserta kapasitas maksimalnya.
    |   +-- Refundable.java        # Interface kontrak bisnis untuk menghitung nilai pengembalian dana berdasarkan sisa hari.
    |
    |-- repository/                # [TUGAS MAHASISWA - Data Access Object / SQL Query]
    |   |-- UserRepository.java    # Menangani query SQL murni ke database SQLite untuk operasi CRUD data user.
    |   |-- VenueRepository.java   # Menangani query SQL untuk menyimpan, mengubah, dan mencari data lokasi/venue.
    |   |-- EventRepository.java   # Mengelola persistensi data event beserta pemetaan sub-jenis event saat dibaca dari database.
    |   +-- TicketRepository.java  # Menangani query transaksi tiket, update status refund, dan manipulasi sisa kuota kursi.
    |
    |-- service/                   # [TUGAS MAHASISWA - Logika Bisnis Utama]
    |   |-- UserService.java       # Memvalidasi kelengkapan data registrasi user.
    |   |-- VenueService.java      # Memvalidasi tempat/venue agar tidak terjadi bentrok.
    |   |-- EventService.java      # Mengatur logika bisnis pembuatan acara serta validasi kecocokan kuota dengan venue.
    |   +-- TicketService.java     # Pusat logika pembelian tiket (cek kuota kelas) dan kalkulasi refund (cek tipe event & selisih hari).
    |
    |-- handler/                   # [TUGAS MAHASISWA - HTTP Controller & Request Controller]
    |   |-- UserHandler.java       # Mengontrol endpoint /api/users, membaca input client, dan meneruskannya ke UserService.
    |   |-- VenueHandler.java      # Mengontrol endpoint /api/venues untuk mengelola request data lokasi atau tempat.
    |   |-- EventHandler.java      # Mengontrol endpoint /api/events serta menyajikan data laporan summary pendapatan event.
    |   +-- TicketHandler.java     # Mengontrol endpoint /api/tickets untuk memproses transaksi pembelian dan request refund tiket.
    |
    +-- exception/                      # [TUGAS MAHASISWA - Manajemen Error Kustom]
    |-- EventNotFoundException.java # Exception kustom saat Event ID yang dicari di database tidak ditemukan.
    |-- TicketSoldOutException.java # Exception kustom saat kuota atau kapasitas kursi suatu event sudah habis terjual.
    +-- RefundNotAllowedException.java # Exception kustom saat pembatalan tiket melanggar aturan bisnis (misal: tipe SportMatch).

## Tabel pembagian tugas anggota
| Anggota |    NIM     | Tanggung Jawab                                                                      |
|---------|------------|-------------------------------------------------------------------------------------|
| Ella    | 2505551027 | Model: Concert.java, Seminar.java, Event.java, Refundable.java                      |
| Nanda   | 2505551110 | Model: SportMatch.java, User.java, Venue.java, Ticket.java                          |
| Alisha  | 2505551063 | Repository: EventRepository.java, TicketRepository.java, DatabaseManager            |
| Deswita | 2505551125 | Service: EventService.java, TicketService.java, UserService.java, VenueService.java |
| Andre   | 2505551034 | Handler: semua handler. Exception classes.                                          |
