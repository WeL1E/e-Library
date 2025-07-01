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
import javax.swing.RowFilter;

public class PinjamPanel extends JPanel {
    private JTable tabelPinjam;
    private final Set<Integer> rowYangDibesarkan = new HashSet<>();
    private String currentFilter = null;
    private TableRowSorter<DefaultTableModel> sorter;

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
            } else {
                currentFilter = STATUS_DIPINJAM;
                loadData(currentFilter);
            }
        });

        btnKembali.addActionListener(e -> {
            if (STATUS_DIKEMBALIKAN.equals(currentFilter)) {
                currentFilter = null;
                loadData(null);
            } else {
                currentFilter = STATUS_DIKEMBALIKAN;
                loadData(currentFilter);
            }
        });

        panelKiri.add(btnPinjam);
        panelKiri.add(btnKembali);

        // Tambahkan panel kiri saja (tanpa label keterangan)
        panelBawah.add(panelKiri, BorderLayout.WEST);
        add(panelBawah, BorderLayout.SOUTH);

        loadData(null);
    }

    public void search(String keyword) {
        if (sorter != null) {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword));
        }
    }

    void loadData(String filterStatus) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
            "No", "Kode Buku", "NIM", "Nama", "Judul Buku",
            "Waktu Pinjam", "Waktu Kembali", "Denda", "Status"
        });

        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT kode_buku, nim, nama, judul_buku, waktu_pinjam, waktu_kembali, denda FROM pinjaman";
            if (STATUS_DIPINJAM.equals(filterStatus)) {
                sql += " WHERE waktu_kembali IS NULL";
            } else if (STATUS_DIKEMBALIKAN.equals(filterStatus)) {
                sql += " WHERE waktu_kembali IS NOT NULL";
            }
            sql += " ORDER BY waktu_pinjam DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            NumberFormat formatRupiah = NumberFormat.getNumberInstance(new Locale("id", "ID"));
            int no = 1;

            while (rs.next()) {
                String waktuPinjam = WaktuFormatter.format(rs.getTimestamp("waktu_pinjam"));
                Timestamp kembaliTS = rs.getTimestamp("waktu_kembali");
                String waktuKembali = (kembaliTS != null)
                        ? WaktuFormatter.format(kembaliTS)
                        : "-";
                double dendaRaw = rs.getDouble("denda");
                String dendaFormatted = formatRupiah.format(dendaRaw);

                String status = (kembaliTS == null) ? "Dipinjam" : "Dikembalikan";

                model.addRow(new Object[]{
                    no++,
                    rs.getString("kode_buku"),
                    rs.getString("nim"),
                    rs.getString("nama"),
                    rs.getString("judul_buku"),
                    waktuPinjam,
                    waktuKembali,
                    dendaFormatted,
                    status
                });
            }

            tabelPinjam.setModel(model);

            if (sorter == null) {
                sorter = new TableRowSorter<>(model);
                tabelPinjam.setRowSorter(sorter);
            } else {
                sorter.setModel(model);
            }

            tabelPinjam.getColumnModel().getColumn(0).setPreferredWidth(40);

            int[] centerBefore = {0, 2, 5, 6, 7};
            int[] centerAfter = {5, 6, 7};
            TableStyler.setTabelStyle(tabelPinjam, rowYangDibesarkan, 24, centerBefore, centerAfter);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data pinjaman.");
        }
    }
}
