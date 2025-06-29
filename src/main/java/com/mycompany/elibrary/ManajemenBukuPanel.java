package com.mycompany.elibrary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class ManajemenBukuPanel extends JPanel {

    private JTextField txtSearch;
    private JTable tabelBuku;
    private JScrollPane scrollPane;
    private final Set<Integer> rowYangDibesarkan = new HashSet<>();

    public ManajemenBukuPanel(JTextField txtSearchExternal) {
        setLayout(new BorderLayout());
        this.txtSearch = txtSearchExternal;

        tabelBuku = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scrollPane = new JScrollPane(tabelBuku);
        add(scrollPane, BorderLayout.CENTER);

        String keyword = txtSearch.getText().trim();
        if (!keyword.isEmpty()) {
            cariBuku(keyword);
        } else {
            loadDataBuku();
        }
    }

    private void loadDataBuku() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("No");
        model.addColumn("Kode Buku");
        model.addColumn("Judul");
        model.addColumn("Penulis");
        model.addColumn("Penerbit");
        model.addColumn("Tahun");
        model.addColumn("Kategori");

        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT * FROM buku";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            int no = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                    no++,
                    rs.getString("kode_buku"),
                    rs.getString("judul_buku"),
                    rs.getString("penulis"),
                    rs.getString("penerbit"),
                    rs.getInt("tahun_terbit"),
                    rs.getString("kategori")
                });
            }

            tabelBuku.setModel(model);
            tabelBuku.getColumnModel().getColumn(0).setPreferredWidth(40); // "No" kecil
            TabelStyler.setTabelStyle(tabelBuku, rowYangDibesarkan, 24);
            TabelStyler.setCenterAlignment(tabelBuku, 5); // Kolom Tahun

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data buku: " + e.getMessage());
        }
    }

    private void cariBuku(String keyword) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("No");
        model.addColumn("Kode Buku");
        model.addColumn("Judul");
        model.addColumn("Penulis");
        model.addColumn("Penerbit");
        model.addColumn("Tahun");
        model.addColumn("Kategori");

        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT * FROM buku WHERE " +
                    "LOWER(kode_buku) LIKE ? OR " +
                    "LOWER(judul_buku) LIKE ? OR " +
                    "LOWER(penulis) LIKE ? OR " +
                    "LOWER(penerbit) LIKE ? OR " +
                    "CAST(tahun_terbit AS CHAR) LIKE ? OR " +
                    "LOWER(kategori) LIKE ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            String keywordLike = "%" + keyword.toLowerCase() + "%";
            for (int i = 1; i <= 6; i++) {
                stmt.setString(i, keywordLike);
            }

            ResultSet rs = stmt.executeQuery();

            int no = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                    no++,
                    rs.getString("kode_buku"),
                    rs.getString("judul_buku"),
                    rs.getString("penulis"),
                    rs.getString("penerbit"),
                    rs.getInt("tahun_terbit"),
                    rs.getString("kategori")
                });
            }

            tabelBuku.setModel(model);
            tabelBuku.getColumnModel().getColumn(0).setPreferredWidth(40); // "No" kecil
            TabelStyler.setTabelStyle(tabelBuku, rowYangDibesarkan, 24);
            TabelStyler.setCenterAlignment(tabelBuku, 5); // Kolom Tahun

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencari buku: " + e.getMessage());
        }
    }
}
