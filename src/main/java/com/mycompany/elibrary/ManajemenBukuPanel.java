package com.mycompany.elibrary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class ManajemenBukuPanel extends JPanel {

    private JTextField txtSearch;
    private JLabel lblTotalBuku;
    private JTable tabelBuku;
    private JScrollPane scrollPane;
    private final Set<Integer> rowYangDibesarkan = new HashSet<>();
    private JButton btnEdit, btnHapus, btnTambah;

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

        scrollPane = new JScrollPane(tabelBuku);
        add(scrollPane, BorderLayout.CENTER);

        btnEdit = new JButton("Edit");
        btnEdit.addActionListener(e -> editDataBuku());

        btnHapus = new JButton("Hapus");
        btnHapus.addActionListener(e -> hapusDataBuku());

        btnTambah = new JButton("Tambah");
        btnTambah.addActionListener(e -> tambahDataBuku());

        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBawah.add(btnTambah);
        panelBawah.add(btnEdit);
        panelBawah.add(btnHapus);
        add(panelBawah, BorderLayout.SOUTH);

        refreshData();
    }

    private void refreshData() {
        String keyword = txtSearch.getText().trim();
        if (!keyword.isEmpty()) {
            cariBuku(keyword);
        } else {
            loadDataBuku();
        }
        tampilkanTotalBuku();
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
        model.addColumn("Jumlah");
        model.addColumn("Tersedia");

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
            TabelStyler.setTabelStyle(tabelBuku, rowYangDibesarkan, 24);
            TabelStyler.setCenterAlignment(tabelBuku, 5, 7, 8);

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
        model.addColumn("Jumlah");
        model.addColumn("Tersedia");

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
                    rs.getString("kategori"),
                    rs.getInt("jumlah"),
                    rs.getInt("tersedia")
                });
            }

            tabelBuku.setModel(model);
            tabelBuku.getColumnModel().getColumn(0).setPreferredWidth(40);
            TabelStyler.setTabelStyle(tabelBuku, rowYangDibesarkan, 24);
            TabelStyler.setCenterAlignment(tabelBuku, 5, 7, 8);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencari buku: " + e.getMessage());
        }
    }

    private void editDataBuku() {
        int selectedRow = tabelBuku.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih satu baris data untuk diedit.");
            return;
        }

        String kodeBukuLama = tabelBuku.getValueAt(selectedRow, 1).toString();

        JTextField txtKode = new JTextField(kodeBukuLama);
        JTextField txtJudul = new JTextField(tabelBuku.getValueAt(selectedRow, 2).toString());
        JTextField txtPenulis = new JTextField(tabelBuku.getValueAt(selectedRow, 3).toString());
        JTextField txtPenerbit = new JTextField(tabelBuku.getValueAt(selectedRow, 4).toString());
        JTextField txtTahun = new JTextField(tabelBuku.getValueAt(selectedRow, 5).toString());
        JTextField txtKategori = new JTextField(tabelBuku.getValueAt(selectedRow, 6).toString());
        JTextField txtJumlah = new JTextField(tabelBuku.getValueAt(selectedRow, 7).toString());
        JTextField txtTersedia = new JTextField(tabelBuku.getValueAt(selectedRow, 8).toString());

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Kode Buku:")); panel.add(txtKode);
        panel.add(new JLabel("Judul:")); panel.add(txtJudul);
        panel.add(new JLabel("Penulis:")); panel.add(txtPenulis);
        panel.add(new JLabel("Penerbit:")); panel.add(txtPenerbit);
        panel.add(new JLabel("Tahun:")); panel.add(txtTahun);
        panel.add(new JLabel("Kategori:")); panel.add(txtKategori);
        panel.add(new JLabel("Jumlah:")); panel.add(txtJumlah);
        panel.add(new JLabel("Tersedia:")); panel.add(txtTersedia);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Data Buku", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.connect()) {
                String sql = "UPDATE buku SET kode_buku=?, judul_buku=?, penulis=?, penerbit=?, tahun_terbit=?, kategori=?, jumlah=?, tersedia=? WHERE kode_buku=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, txtKode.getText().trim());
                stmt.setString(2, txtJudul.getText().trim());
                stmt.setString(3, txtPenulis.getText().trim());
                stmt.setString(4, txtPenerbit.getText().trim());
                stmt.setInt(5, Integer.parseInt(txtTahun.getText().trim()));
                stmt.setString(6, txtKategori.getText().trim());
                stmt.setInt(7, Integer.parseInt(txtJumlah.getText().trim()));
                stmt.setInt(8, Integer.parseInt(txtTersedia.getText().trim()));
                stmt.setString(9, kodeBukuLama);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil diperbarui.");
                refreshData();
                TabelStyler.setTabelStyle(tabelBuku, rowYangDibesarkan, 24);
                TabelStyler.setCenterAlignment(tabelBuku, 5, 7, 8);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal update: " + e.getMessage());
            }
        }
    }

    private void hapusDataBuku() {
        int selectedRow = tabelBuku.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus.");
            return;
        }

        String kodeBuku = tabelBuku.getValueAt(selectedRow, 1).toString();
        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus buku dengan kode " + kodeBuku + "?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.connect()) {
                String sql = "DELETE FROM buku WHERE kode_buku=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, kodeBuku);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                refreshData();
                TabelStyler.setTabelStyle(tabelBuku, rowYangDibesarkan, 24);
                TabelStyler.setCenterAlignment(tabelBuku, 5, 7, 8);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
            }
        }
    }

    private void tambahDataBuku() {
        JTextField txtKode = new JTextField();
        JTextField txtJudul = new JTextField();
        JTextField txtPenulis = new JTextField();
        JTextField txtPenerbit = new JTextField();
        JTextField txtTahun = new JTextField();
        JTextField txtKategori = new JTextField();
        JTextField txtJumlah = new JTextField();
        JTextField txtTersedia = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Kode Buku:")); panel.add(txtKode);
        panel.add(new JLabel("Judul:")); panel.add(txtJudul);
        panel.add(new JLabel("Penulis:")); panel.add(txtPenulis);
        panel.add(new JLabel("Penerbit:")); panel.add(txtPenerbit);
        panel.add(new JLabel("Tahun:")); panel.add(txtTahun);
        panel.add(new JLabel("Kategori:")); panel.add(txtKategori);
        panel.add(new JLabel("Jumlah:")); panel.add(txtJumlah);
        panel.add(new JLabel("Tersedia:")); panel.add(txtTersedia);

        int result;
        do {
            result = JOptionPane.showConfirmDialog(this, panel, "Tambah Buku Baru", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String kode = txtKode.getText().trim();
                    String judul = txtJudul.getText().trim();
                    String penulis = txtPenulis.getText().trim();
                    String penerbit = txtPenerbit.getText().trim();
                    String tahun = txtTahun.getText().trim();
                    String kategori = txtKategori.getText().trim();
                    String jumlah = txtJumlah.getText().trim();
                    String tersedia = txtTersedia.getText().trim();

                    if (kode.isEmpty() || judul.isEmpty() || penulis.isEmpty() || penerbit.isEmpty() || tahun.isEmpty() ||
                            kategori.isEmpty() || jumlah.isEmpty() || tersedia.isEmpty()) {
                        throw new Exception("Semua field wajib diisi.");
                    }

                    int tahunInt = Integer.parseInt(tahun);
                    if (tahunInt < 0 || tahunInt > 9999) {
                        throw new Exception("Tahun harus antara 0 - 9999.");
                    }
                    int jumlahInt = Integer.parseInt(jumlah);
                    int tersediaInt = Integer.parseInt(tersedia);

                    try (Connection conn = DBConnection.connect()) {
                        String sql = "INSERT INTO buku (kode_buku, judul_buku, penulis, penerbit, tahun_terbit, kategori, jumlah, tersedia) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setString(1, kode);
                        stmt.setString(2, judul);
                        stmt.setString(3, penulis);
                        stmt.setString(4, penerbit);
                        stmt.setInt(5, tahunInt);
                        stmt.setString(6, kategori);
                        stmt.setInt(7, jumlahInt);
                        stmt.setInt(8, tersediaInt);
                        stmt.executeUpdate();

                        JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan.");
                        refreshData();
                        TabelStyler.setTabelStyle(tabelBuku, rowYangDibesarkan, 24);
                        TabelStyler.setCenterAlignment(tabelBuku, 5, 7, 8);
                        break;
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Tahun, Jumlah, dan Tersedia harus berupa angka.", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                break; // Jika Cancel ditekan
            }
        } while (true);
    }

    public void tampilkanTotalBuku() {
        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT COUNT(*) FROM buku";
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
