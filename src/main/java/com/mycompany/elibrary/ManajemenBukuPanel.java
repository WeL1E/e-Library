package com.mycompany.elibrary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class ManajemenBukuPanel extends JPanel {
    
    private JTextField txtSearch;
    private JButton btnSearch;
    
    
    private JTable tabelBuku;
    private JScrollPane scrollPane;
    private final Set<Integer> rowYangDibesarkan = new HashSet<>();

    public ManajemenBukuPanel() {
        
        txtSearch = new JTextField(20);
        btnSearch = new JButton("Cari");

        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSearch.add(new JLabel("Cari Judul:"));
        panelSearch.add(txtSearch);
        panelSearch.add(btnSearch);

        add(panelSearch, BorderLayout.NORTH);
        
        setLayout(new BorderLayout());

        tabelBuku = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scrollPane = new JScrollPane(tabelBuku);
        add(scrollPane, BorderLayout.CENTER);

        loadDataBuku();
    }

    private void loadDataBuku() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Kode Buku");
        model.addColumn("Judul");
        model.addColumn("Penulis");
        model.addColumn("Penerbit");
        model.addColumn("Tahun");
        model.addColumn("Kategori");
        model.addColumn("Jumlah");
        model.addColumn("Tersedia");

        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT * FROM buku";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("kode_buku"),
                    rs.getString("judul_buku"),
                    rs.getString("penulis"),
                    rs.getString("penerbit"),
                    rs.getInt("tahun_terbit"),
                    rs.getString("kategori"),
                    rs.getInt("jumlah"),
                    rs.getInt("tersedia")
                });
            }

            tabelBuku.setModel(model);

            // ✅ Terapkan style modular
            TabelStyler.setTabelStyle(tabelBuku, rowYangDibesarkan, 24);
            TabelStyler.setCenterAlignment(tabelBuku, 4, 6, 7); // Tahun, Jumlah, Tersedia

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data buku: " + e.getMessage());
        }
    }
}
