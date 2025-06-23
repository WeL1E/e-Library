package com.mycompany.elibrary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashSet;
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

        // Terapkan style tabel
        
    }

    private void loadData() {
        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT kode_buku, nim, judul_buku, waktu_pinjam, waktu_kembali, denda, keterangan FROM pinjaman ORDER BY waktu_pinjam DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Kode Buku", "NIM", "Judul Buku", "Waktu Pinjam", "Waktu Kembali", "Denda", "Keterangan"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // 🔒 Semua kolom tidak bisa diedit
                }
            };


            while (rs.next()) {
                model.addRow(new Object[]{
                rs.getString("kode_buku"),
                rs.getString("nim"),
                rs.getString("judul_buku"),
                rs.getTimestamp("waktu_pinjam"),
                rs.getTimestamp("waktu_kembali"),
                rs.getDouble("denda"),
                rs.getString("keterangan").equalsIgnoreCase("Dipinjam") ? "Pinjam" : rs.getString("keterangan")
            });

            }

            tabel.setModel(model);

            // 🔥 Baru terapkan style dan listener setelah model di-set
            TabelStyler.setTabelStyle(tabel, rowYangDibesarkan, 35);
            TabelStyler.setCenterAlignment(tabel, 1, 3, 4, 5);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data pinjaman.");
        }
    }

}
