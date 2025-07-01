package com.mycompany.elibrary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.swing.table.TableRowSorter;

public class PinjamPanel extends JPanel {
    private JTable tabelPinjam;
    private final Set<Integer> rowYangDibesarkan = new HashSet<>();
    private JLabel labelKeterangan;
    private String currentFilter = null;

    private TableRowSorter<DefaultTableModel> sorter; // Untuk search

    // Status konstanta
    private static final String STATUS_DIPINJAM = "Dipinjam";
    private static final String STATUS_DIKEMBALIKAN = "Dikembalikan";

    public PinjamPanel() {
        setLayout(new BorderLayout());

        tabelPinjam = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JScrollPane scrollPane = new JScrollPane(tabelPinjam);
        add(scrollPane, BorderLayout.CENTER);

        // Panel bawah
        JPanel panelBawah = new JPanel(new BorderLayout());

        // Tombol kiri
        JPanel panelKiri = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnPinjam = new JButton("📚 Pinjam");
        JButton btnKembali = new JButton("📤 Kembali");

        btnPinjam.addActionListener(e -> {
            if (STATUS_DIPINJAM.equals(currentFilter)) {
                currentFilter = null;
                loadData(null);
                updateKeterangan("Menampilkan semua data pinjaman");
            } else {
                currentFilter = STATUS_DIPINJAM;
                loadData(currentFilter);
                updateKeterangan("Data buku yang sedang dipinjam");
            }
        });

        btnKembali.addActionListener(e -> {
            if (STATUS_DIKEMBALIKAN.equals(currentFilter)) {
                currentFilter = null;
                loadData(null);
                updateKeterangan("Menampilkan semua data pinjaman");
            } else {
                currentFilter = STATUS_DIKEMBALIKAN;
                loadData(currentFilter);
                updateKeterangan("Data buku yang sudah dikembalikan");
            }
        });

        panelKiri.add(btnPinjam);
        panelKiri.add(btnKembali);

        // Label kanan
        labelKeterangan = new JLabel(" ");
        labelKeterangan.setHorizontalAlignment(SwingConstants.RIGHT);
        styleLabelInfo(labelKeterangan);

        JPanel panelKanan = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelKanan.add(labelKeterangan);

        panelBawah.add(panelKiri, BorderLayout.WEST);
        panelBawah.add(panelKanan, BorderLayout.EAST);
        add(panelBawah, BorderLayout.SOUTH);

        loadData(null);
        updateKeterangan("Menampilkan semua data pinjaman");
    }

    public void search(String keyword) {
        if (sorter != null) {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword));
        }
    }

    private void styleLabelInfo(JLabel... labels) {
        for (JLabel label : labels) {
            label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            label.setForeground(new Color(30, 30, 30));
        }
    }

    private void updateKeterangan(String teks) {
        labelKeterangan.setText(teks + ": " + tabelPinjam.getRowCount());
    }

    private void loadData(String filterKeterangan) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
            "No", "Kode Buku", "NIM", "Nama", "Judul Buku",
            "Waktu Pinjam", "Waktu Kembali", "Denda", "Keterangan"
        });

        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT kode_buku, nim, nama, judul_buku, waktu_pinjam, waktu_kembali, denda, keterangan FROM pinjaman";
            if (filterKeterangan != null) {
                sql += " WHERE keterangan = ?";
            }
            sql += " ORDER BY waktu_pinjam DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            if (filterKeterangan != null) {
                stmt.setString(1, filterKeterangan);
            }

            ResultSet rs = stmt.executeQuery();
            NumberFormat formatRupiah = NumberFormat.getNumberInstance(new Locale("id", "ID"));
            int no = 1;

            while (rs.next()) {
                String waktuPinjam = WaktuFormatter.format(rs.getTimestamp("waktu_pinjam"));
                String waktuKembali = rs.getTimestamp("waktu_kembali") != null
                        ? WaktuFormatter.format(rs.getTimestamp("waktu_kembali"))
                        : "-";
                double dendaRaw = rs.getDouble("denda");
                String dendaFormatted = formatRupiah.format(dendaRaw);

                String keterangan = rs.getString("keterangan");
                if (keterangan == null || keterangan.equalsIgnoreCase(STATUS_DIPINJAM)) {
                    keterangan = "Pinjam";
                }

                model.addRow(new Object[]{
                        no++,
                        rs.getString("kode_buku"),
                        rs.getString("nim"),
                        rs.getString("nama"),
                        rs.getString("judul_buku"),
                        waktuPinjam,
                        waktuKembali,
                        dendaFormatted,
                        keterangan
                });
            }

            tabelPinjam.setModel(model);

            // TableRowSorter hanya dibuat sekali dan model-nya diperbarui
            if (sorter == null) {
                sorter = new TableRowSorter<>(model);
                tabelPinjam.setRowSorter(sorter);
            } else {
                sorter.setModel(model);
            }

            tabelPinjam.getColumnModel().getColumn(0).setPreferredWidth(40);

            int[] centerBefore = {0, 7};       // No, Denda
            int[] centerAfter = {5, 6, 7};     // Waktu Pinjam, Waktu Kembali, Denda
            TableStyler.setTabelStyle(tabelPinjam, rowYangDibesarkan, 24, centerBefore, centerAfter);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data pinjaman.");
        }
    }
}
