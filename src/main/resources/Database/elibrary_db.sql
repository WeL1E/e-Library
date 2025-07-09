-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Jul 09, 2025 at 08:33 AM
-- Server version: 8.4.3
-- PHP Version: 8.3.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `elibrary_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `aktivitas`
--

CREATE TABLE `aktivitas` (
  `id_aktivitas` int NOT NULL,
  `nim` varchar(10) NOT NULL,
  `nama_mahasiswa` varchar(100) NOT NULL,
  `waktu_masuk` datetime NOT NULL,
  `waktu_keluar` datetime DEFAULT NULL,
  `prodi` varchar(100) DEFAULT NULL,
  `keterangan` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `aktivitas`
--

INSERT INTO `aktivitas` (`id_aktivitas`, `nim`, `nama_mahasiswa`, `waktu_masuk`, `waktu_keluar`, `prodi`, `keterangan`) VALUES
(56, '231351014', 'Aldo Ripaldo', '2025-07-08 21:23:47', '2025-07-08 21:23:59', 'Teknik Informatika', 'Keluar'),
(57, '231351014', 'Aldo Ripaldo', '2025-07-08 21:24:17', '2025-07-08 21:29:10', 'Teknik Informatika', 'Keluar'),
(58, '231351083', 'Moehamad Al Syahrefi', '2025-07-08 21:43:57', '2025-07-08 21:44:56', 'Teknik Informatika', 'Keluar'),
(59, '231351014', 'Aldo Ripaldo', '2025-07-08 23:22:20', NULL, 'Teknik Informatika', 'Masuk');

-- --------------------------------------------------------

--
-- Table structure for table `buku`
--

CREATE TABLE `buku` (
  `id_buku` int NOT NULL,
  `kode_buku` varchar(50) NOT NULL,
  `judul_buku` varchar(255) NOT NULL,
  `penulis` varchar(100) NOT NULL,
  `penerbit` varchar(100) DEFAULT NULL,
  `tahun_terbit` int DEFAULT NULL,
  `kategori` varchar(100) DEFAULT NULL,
  `stock` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `buku`
--

INSERT INTO `buku` (`id_buku`, `kode_buku`, `judul_buku`, `penulis`, `penerbit`, `tahun_terbit`, `kategori`, `stock`) VALUES
(1, '005.1 TAN s/2022', 'Struktur Data', 'Andrew S. Tanenbaum', 'Pearson', 2022, 'Teknik Informatika', 10),
(2, '005.1 KUR a/2021', 'Algoritma dan Pemrograman', 'Agus Kurniawan', 'Informatika Bandung', 2021, 'Teknik Informatika', 10),
(3, '005.4 SIL o/2020', 'Operating System Concepts', 'Abraham Silberschatz', 'Wiley', 2020, 'Sistem Operasi', 8),
(4, '004.6 FOR c/2021', 'Computer Networks', 'Behrouz Forouzan', 'McGraw-Hill', 2021, 'Jaringan Komputer', 7),
(5, '330 MAN p/2019', 'Prinsip Ekonomi', 'N. Gregory Mankiw', 'Salemba Empat', 2019, 'Ekonomi', 15),
(6, '658 KOT m/2020', 'Marketing Management', 'Philip Kotler', 'Pearsons', 2020, 'Manajemen', 6),
(7, '428 AZA u/2021', 'Understanding English Grammar', 'Betty Schrampfer Azar', 'Pearson Longman', 2021, 'Bahasa Inggris', 9),
(9, '530 HAL f/2019', 'Fisika Dasar', 'David Halliday', 'Wiley', 2019, 'Fisika', 10),
(10, '340 EFF h/2021', 'Hukum Pidana Indonesia', 'Marwan Effendy', 'Gramedia', 2021, 'Hukum', 8),
(24, '047.5 LUR a/2025', 'statistika', 'PAK ALAM', 'SINAR DUNIA', 2019, 'TEKNIK INFORMATIKA', 100);

-- --------------------------------------------------------

--
-- Table structure for table `mahasiswa`
--

CREATE TABLE `mahasiswa` (
  `nim` varchar(10) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `prodi` varchar(100) NOT NULL,
  `jenis_kelamin` enum('L','P') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `mahasiswa`
--

INSERT INTO `mahasiswa` (`nim`, `nama`, `prodi`, `jenis_kelamin`) VALUES
('231351010', 'Muzaki Ash-Shiddiq', 'Teknik Informatika', 'L'),
('231351014', 'Aldo Ripaldo', 'Teknik Informatika', 'L'),
('231351017', 'Alvira Septiadi Saputra', 'Teknik Informatika', 'L'),
('231351083', 'Moehamad Al Syahrefi', 'Teknik Informatika', 'L'),
('231351087', 'Muhamad Agil Hidayat', 'Teknik Informatika', 'L');

-- --------------------------------------------------------

--
-- Table structure for table `pinjaman`
--

CREATE TABLE `pinjaman` (
  `id` int NOT NULL,
  `nim` varchar(10) DEFAULT NULL,
  `nama` varchar(100) DEFAULT NULL,
  `kode_buku` varchar(20) DEFAULT NULL,
  `judul_buku` varchar(255) DEFAULT NULL,
  `waktu_pinjam` datetime DEFAULT CURRENT_TIMESTAMP,
  `waktu_kembali` datetime DEFAULT NULL,
  `denda` int DEFAULT '0',
  `keterangan` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `pinjaman`
--

INSERT INTO `pinjaman` (`id`, `nim`, `nama`, `kode_buku`, `judul_buku`, `waktu_pinjam`, `waktu_kembali`, `denda`, `keterangan`) VALUES
(131, '231351014', 'Aldo Ripaldo', '005.1 KUR a/2021', 'Algoritma dan Pemrograman', '2025-07-08 21:25:04', '2025-07-08 21:25:14', 0, 'Dikembalikan'),
(132, '231351083', 'Moehamad Al Syahrefi', '005.1 KUR a/2021', 'Algoritma dan Pemrograman', '2025-07-08 21:44:26', '2025-07-08 21:44:41', 0, 'Dikembalikan'),
(133, '231351014', 'Aldo Ripaldo', '005.1 KUR a/2021', 'Algoritma dan Pemrograman', '2025-05-01 23:22:40', '2025-07-08 23:39:54', 30500, 'Dikembalikan');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int NOT NULL,
  `username` varchar(10) NOT NULL,
  `password` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`) VALUES
(1, 'admin', 'admin123');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `aktivitas`
--
ALTER TABLE `aktivitas`
  ADD PRIMARY KEY (`id_aktivitas`);

--
-- Indexes for table `buku`
--
ALTER TABLE `buku`
  ADD PRIMARY KEY (`id_buku`),
  ADD UNIQUE KEY `kode_buku` (`kode_buku`);

--
-- Indexes for table `mahasiswa`
--
ALTER TABLE `mahasiswa`
  ADD PRIMARY KEY (`nim`);

--
-- Indexes for table `pinjaman`
--
ALTER TABLE `pinjaman`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `aktivitas`
--
ALTER TABLE `aktivitas`
  MODIFY `id_aktivitas` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=60;

--
-- AUTO_INCREMENT for table `buku`
--
ALTER TABLE `buku`
  MODIFY `id_buku` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `pinjaman`
--
ALTER TABLE `pinjaman`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=134;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
