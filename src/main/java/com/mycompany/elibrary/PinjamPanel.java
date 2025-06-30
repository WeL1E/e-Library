package com.mycompany.elibrary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class PinjamPanel extends JPanel {
    private JTable tabel;
    private JScrollPane scrollPane;
    private final Set<Integer> rowYangDibesarkan = new HashSet<>();

    public PinjamPanel() {
        setLayout(new BorderLayout());

        tabel = new JTable();
        scrollPane = new JScrollPane(tabel);
        add(scrollPane, BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        try (Connection conn = DBConnection.connect()) {
            // 🔁 Tambahkan kolom "nama" di SQL
            String sql = "SELECT kode_buku, nim, nama, judul_buku, waktu_pinjam, waktu_kembali, denda, keterangan FROM pinjaman ORDER BY waktu_pinjam DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // 🔁 Tambahkan "Nama" di header kolom
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Kode Buku", "NIM", "Nama", "Judul Buku", "Waktu Pinjam", "Waktu Kembali", "Denda", "Keterangan"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            NumberFormat formatRupiah = NumberFormat.getNumberInstance(new Locale("id", "ID"));

            while (rs.next()) {
                String kodeBuku = rs.getString("kode_buku");
                String nim = rs.getString("nim");
                String nama = rs.getString("nama"); // ✅ Ambil nama mahasiswa
                String judul = rs.getString("judul_buku");
                String waktuPinjam = WaktuFormatter.format(rs.getTimestamp("waktu_pinjam"));
                String waktuKembali = rs.getTimestamp("waktu_kembali") != null
                    ? WaktuFormatter.format(rs.getTimestamp("waktu_kembali"))
                    : "-";
                double dendaRaw = rs.getDouble("denda");

                String dendaFormatted = formatRupiah.format(dendaRaw);

                String keterangan = rs.getString("keterangan");
                if (keterangan == null || keterangan.equalsIgnoreCase("Dipinjam")) {
                    keterangan = "Pinjam";
                }

                model.addRow(new Object[]{
                    kodeBuku,
                    nim,
                    nama,
                    judul,
                    waktuPinjam,
                    waktuKembali,
                    dendaFormatted,
                    keterangan
                });
            }

            tabel.setModel(model);

            TabelStyler.setTabelStyle(tabel, rowYangDibesarkan, 35);
            TabelStyler.setCenterAlignment(tabel, 1, 2, 4, 5, 6);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data pinjaman.");
        }
    }
}
