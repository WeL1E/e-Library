package com.mycompany.elibrary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.RowFilter;

public class AktivitasPanel extends JPanel {

    private final JTextField txtSearch;
    private JTable tabelAktivitas;
    private final Set<Integer> rowYangDibesarkan = new HashSet<>();
    private TableRowSorter<DefaultTableModel> sorter;

    public AktivitasPanel(JTextField txtSearchExternal) {
        this.txtSearch = txtSearchExternal;
        setLayout(new BorderLayout());

        tabelAktivitas = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JScrollPane scrollPane = new JScrollPane(tabelAktivitas);
        add(scrollPane, BorderLayout.CENTER);

        loadDataAktivitas();
    }

    public void search(String keyword) {
        if (sorter != null) {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword));
        }
    }

    public void loadDataAktivitas() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
            "No", "NIM", "Nama", "Prodi", "Check in", "Check out", "Keterangan"
        });

        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT nim, nama_mahasiswa, prodi, waktu_masuk, waktu_keluar, keterangan FROM aktivitas ORDER BY waktu_masuk DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            int no = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                    no++,
                    rs.getString("nim"),
                    rs.getString("nama_mahasiswa"),
                    rs.getString("prodi"),
                    WaktuFormatter.format(rs.getTimestamp("waktu_masuk")),
                    rs.getTimestamp("waktu_keluar") != null
                        ? WaktuFormatter.format(rs.getTimestamp("waktu_keluar"))
                        : "-",
                    rs.getString("keterangan")
                });
            }

            tabelAktivitas.setModel(model);

            // ✅ SOLUSI: selalu set RowSorter baru tiap kali data di-load
            sorter = new TableRowSorter<>(model);
            tabelAktivitas.setRowSorter(sorter);

            tabelAktivitas.getColumnModel().getColumn(0).setPreferredWidth(40);

            int[] centerBefore = {0, 4, 5}; // No, Check in, Check out
            int[] centerAfter = {4, 5};    // Check in, Check out
            TableStyler.setTabelStyle(tabelAktivitas, rowYangDibesarkan, 24, centerBefore, centerAfter);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data aktivitas: " + e.getMessage());
        }
    }
}
