package com.mycompany.elibrary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.RowFilter;

public class ManajemenBukuPanel extends JPanel {

    private JTextField txtSearch;
    private JLabel lblTotalBuku;
    private JTable tabelBuku;
    private final Set<Integer> rowYangDibesarkan = new HashSet<>();
    private TableRowSorter<DefaultTableModel> sorter;

    public ManajemenBukuPanel(JTextField txtSearchExternal, JLabel lblTotalBuku) {
        setLayout(new BorderLayout());
        this.txtSearch = txtSearchExternal;
        this.lblTotalBuku = lblTotalBuku;

        tabelBuku = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JScrollPane scrollPane = new JScrollPane(tabelBuku);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnTambah = new JButton("Tambah");
        JButton btnEdit = new JButton("Edit");
        JButton btnHapus = new JButton("Hapus");

        btnTambah.addActionListener(e -> tambahDataBuku());
        btnEdit.addActionListener(e -> editDataBuku());
        btnHapus.addActionListener(e -> hapusDataBuku());

        panelBawah.add(btnTambah);
        panelBawah.add(btnEdit);
        panelBawah.add(btnHapus);
        add(panelBawah, BorderLayout.SOUTH);

        loadDataBuku();
        tampilkanTotalBuku();
    }

    public void search(String keyword) {
        if (sorter != null) {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword));
        }
    }

    private void loadDataBuku() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "No", "Kode Buku", "Judul", "Penulis", "Penerbit",
                "Tahun", "Kategori", "Jumlah", "Tersedia"
        });

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
                        rs.getString("kategori"),
                        rs.getInt("jumlah"),
                        rs.getInt("tersedia")
                });
            }

            tabelBuku.setModel(model);
            tabelBuku.getColumnModel().getColumn(0).setPreferredWidth(40);

            sorter = new TableRowSorter<>(model);
            tabelBuku.setRowSorter(sorter);

            int[] centerBefore = {0, 5, 7, 8}; // No, Tahun, Jumlah, Tersedia
            int[] centerAfter = {5, 7, 8};
            TableStyler.setTabelStyle(tabelBuku, rowYangDibesarkan, 24, centerBefore, centerAfter);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data buku: " + e.getMessage());
        }
    }

    private void tambahDataBuku() {
        JOptionPane.showMessageDialog(this, "Form tambah data belum diimplementasikan.");
    }

    private void editDataBuku() {
        JOptionPane.showMessageDialog(this, "Form edit data belum diimplementasikan.");
    }

    private void hapusDataBuku() {
        JOptionPane.showMessageDialog(this, "Fungsi hapus data belum diimplementasikan.");
    }

    private void tampilkanTotalBuku() {
        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT SUM(tersedia) FROM buku";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int total = rs.getInt(1);
                lblTotalBuku.setText("Total Buku: " + total);
                lblTotalBuku.setVisible(true);
            }
        } catch (SQLException e) {
            lblTotalBuku.setText("Gagal memuat data buku");
            System.err.println("Error: " + e.getMessage());
        }
    }
}
