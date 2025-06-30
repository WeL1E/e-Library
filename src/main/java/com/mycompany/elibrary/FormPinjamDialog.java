package com.mycompany.elibrary;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class FormPinjamDialog extends JDialog {
    private JComboBox<String> comboNIM;
    private JTextField txtNama, txtKodeBuku, txtJudul, txtPenulis, txtPenerbit, txtTahun, txtKategori;
    private JButton btnSimpan;

    public FormPinjamDialog(String kodeBuku) {
        setTitle("Form Peminjaman Buku");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(9, 2, 5, 5));

        comboNIM = new JComboBox<>();
        txtNama = new JTextField();
        txtKodeBuku = new JTextField(kodeBuku);
        txtJudul = new JTextField();
        txtPenulis = new JTextField();
        txtPenerbit = new JTextField();
        txtTahun = new JTextField();
        txtKategori = new JTextField();
        btnSimpan = new JButton("Simpan Peminjaman");

        txtNama.setEditable(false);
        txtKodeBuku.setEditable(false);
        txtJudul.setEditable(false);
        txtPenulis.setEditable(false);
        txtPenerbit.setEditable(false);
        txtTahun.setEditable(false);
        txtKategori.setEditable(false);

        add(new JLabel("NIM Mahasiswa:"));
        add(comboNIM);
        add(new JLabel("Nama Mahasiswa:"));
        add(txtNama);
        add(new JLabel("Kode Buku:"));
        add(txtKodeBuku);
        add(new JLabel("Judul Buku:"));
        add(txtJudul);
        add(new JLabel("Penulis:"));
        add(txtPenulis);
        add(new JLabel("Penerbit:"));
        add(txtPenerbit);
        add(new JLabel("Tahun Terbit:"));
        add(txtTahun);
        add(new JLabel("Kategori:"));
        add(txtKategori);
        add(new JLabel());
        add(btnSimpan);

        loadDropdownNIM();
        loadBuku(kodeBuku);

        comboNIM.addActionListener(e -> loadNamaMahasiswa());

        btnSimpan.addActionListener(e -> simpanPeminjaman());

        setVisible(true);
    }

    private void loadDropdownNIM() {
        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT DISTINCT nim FROM aktivitas WHERE waktu_keluar IS NULL";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                comboNIM.addItem(rs.getString("nim"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat NIM: " + e.getMessage());
        }
    }

    private void loadNamaMahasiswa() {
        String nim = (String) comboNIM.getSelectedItem();
        if (nim == null) return;

        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT nama FROM mahasiswa WHERE nim = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nim);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                txtNama.setText(rs.getString("nama"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat nama mahasiswa: " + e.getMessage());
        }
    }

    private void loadBuku(String kodeBuku) {
        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT * FROM buku WHERE kode_buku = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, kodeBuku);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txtJudul.setText(rs.getString("judul_buku"));
                txtPenulis.setText(rs.getString("penulis"));
                txtPenerbit.setText(rs.getString("penerbit"));
                txtTahun.setText(String.valueOf(rs.getInt("tahun_terbit")));
                txtKategori.setText(rs.getString("kategori"));
            } else {
                JOptionPane.showMessageDialog(this, "Buku tidak ditemukan!");
                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data buku: " + e.getMessage());
        }
    }

    private void simpanPeminjaman() {
        String nim = (String) comboNIM.getSelectedItem();
        if (nim == null || nim.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih NIM terlebih dahulu.");
            return;
        }

        try (Connection conn = DBConnection.connect()) {
            String sql = "INSERT INTO pinjam (nim, kode_buku, waktu_pinjam) VALUES (?, ?, NOW())";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nim);
            stmt.setString(2, txtKodeBuku.getText());
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Peminjaman berhasil dicatat!");
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Gagal menyimpan data: " + e.getMessage());
        }
    }
}
