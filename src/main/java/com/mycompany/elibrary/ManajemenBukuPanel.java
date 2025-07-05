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

        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.LEFT));
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

    void loadDataBuku() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "No", "Kode Buku", "Judul", "Penulis", "Penerbit",
                "Tahun", "Kategori", "Stock"
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
                        rs.getInt("stock")
                });
            }

            tabelBuku.setModel(model);
            tabelBuku.getColumnModel().getColumn(0).setPreferredWidth(40);

            sorter = new TableRowSorter<>(model);
            tabelBuku.setRowSorter(sorter);

            int[] centerBefore = {0, 5, 7}; // No, Tahun, Stock
            int[] centerAfter = {5, 7};
            TableStyler.setTabelStyle(tabelBuku, rowYangDibesarkan, 24, centerBefore, centerAfter);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data buku: " + e.getMessage());
        }
    }

    private void tambahDataBuku() {
        JTextField tfKode = new JTextField();
        JTextField tfJudul = new JTextField();
        JTextField tfPenulis = new JTextField();
        JTextField tfPenerbit = new JTextField();
        JTextField tfTahun = new JTextField();
        JTextField tfKategori = new JTextField();
        JTextField tfStock = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Kode Buku:"));
        panel.add(tfKode);
        panel.add(new JLabel("Judul:"));
        panel.add(tfJudul);
        panel.add(new JLabel("Penulis:"));
        panel.add(tfPenulis);
        panel.add(new JLabel("Penerbit:"));
        panel.add(tfPenerbit);
        panel.add(new JLabel("Tahun Terbit (yyyy):"));
        panel.add(tfTahun);
        panel.add(new JLabel("Kategori:"));
        panel.add(tfKategori);
        panel.add(new JLabel("Stock:"));
        panel.add(tfStock);

        while (true) {
            int result = JOptionPane.showConfirmDialog(this, panel, "Tambah Buku",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) return;

            try {
                int tahun = Integer.parseInt(tfTahun.getText().trim());
                int stock = Integer.parseInt(tfStock.getText().trim());

                try (Connection conn = DBConnection.connect()) {
                    String sql = "INSERT INTO buku (kode_buku, judul_buku, penulis, penerbit, tahun_terbit, kategori, stock) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, tfKode.getText().trim());
                    stmt.setString(2, tfJudul.getText().trim());
                    stmt.setString(3, tfPenulis.getText().trim());
                    stmt.setString(4, tfPenerbit.getText().trim());
                    stmt.setInt(5, tahun);
                    stmt.setString(6, tfKategori.getText().trim());
                    stmt.setInt(7, stock);

                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!");
                    loadDataBuku();
                    tampilkanTotalBuku();
                    return; // keluar dari loop
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Tahun Terbit dan Stock harus berupa angka!", "Input Tidak Valid", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menambah data: " + e.getMessage(), "Kesalahan Database", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    private void editDataBuku() {
        int selectedRow = tabelBuku.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diedit!");
            return;
        }

        int modelRow = tabelBuku.convertRowIndexToModel(selectedRow);
        DefaultTableModel model = (DefaultTableModel) tabelBuku.getModel();

        String kode = model.getValueAt(modelRow, 1).toString();
        JTextField tfJudul = new JTextField(model.getValueAt(modelRow, 2).toString());
        JTextField tfPenulis = new JTextField(model.getValueAt(modelRow, 3).toString());
        JTextField tfPenerbit = new JTextField(model.getValueAt(modelRow, 4).toString());
        JTextField tfTahun = new JTextField(model.getValueAt(modelRow, 5).toString());
        JTextField tfKategori = new JTextField(model.getValueAt(modelRow, 6).toString());
        JTextField tfStock = new JTextField(model.getValueAt(modelRow, 7).toString());

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Judul:"));
        panel.add(tfJudul);
        panel.add(new JLabel("Penulis:"));
        panel.add(tfPenulis);
        panel.add(new JLabel("Penerbit:"));
        panel.add(tfPenerbit);
        panel.add(new JLabel("Tahun Terbit (yyyy):"));
        panel.add(tfTahun);
        panel.add(new JLabel("Kategori:"));
        panel.add(tfKategori);
        panel.add(new JLabel("Stock:"));
        panel.add(tfStock);

        while (true) {
            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Buku",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) return;

            try {
                int tahun = Integer.parseInt(tfTahun.getText().trim());
                int stock = Integer.parseInt(tfStock.getText().trim());

                try (Connection conn = DBConnection.connect()) {
                    String sql = "UPDATE buku SET judul_buku=?, penulis=?, penerbit=?, tahun_terbit=?, kategori=?, stock=? WHERE kode_buku=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, tfJudul.getText().trim());
                    stmt.setString(2, tfPenulis.getText().trim());
                    stmt.setString(3, tfPenerbit.getText().trim());
                    stmt.setInt(4, tahun);
                    stmt.setString(5, tfKategori.getText().trim());
                    stmt.setInt(6, stock);
                    stmt.setString(7, kode);

                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
                    loadDataBuku();
                    tampilkanTotalBuku();
                    return; // keluar dari loop
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Tahun Terbit dan Stock harus berupa angka!", "Input Tidak Valid", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data: " + e.getMessage(), "Kesalahan Database", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    private void hapusDataBuku() {
        int selectedRow = tabelBuku.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!");
            return;
        }

        int modelRow = tabelBuku.convertRowIndexToModel(selectedRow);
        String kode = tabelBuku.getModel().getValueAt(modelRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus buku dengan kode: " + kode + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.connect()) {
                String sql = "DELETE FROM buku WHERE kode_buku=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, kode);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                loadDataBuku();
                tampilkanTotalBuku();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
            }
        }
    }

    void tampilkanTotalBuku() {
        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT SUM(stock) FROM buku";
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
