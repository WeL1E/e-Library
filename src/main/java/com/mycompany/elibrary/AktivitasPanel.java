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
            "No", "NIM", "Nama", "Prodi", "Waktu Masuk", "Waktu Keluar", "Status"
        });

        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT nim, nama_mahasiswa, prodi, waktu_masuk, waktu_keluar FROM aktivitas ORDER BY waktu_masuk DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            int no = 1;
            while (rs.next()) {
                Timestamp waktuMasuk = rs.getTimestamp("waktu_masuk");
                Timestamp waktuKeluar = rs.getTimestamp("waktu_keluar");

                String status = (waktuKeluar == null) ? "Di Dalam" : "Sudah Keluar";

                model.addRow(new Object[]{
                    no++,
                    rs.getString("nim"),
                    rs.getString("nama_mahasiswa"),
                    rs.getString("prodi"),
                    WaktuFormatter.format(waktuMasuk),
                    (waktuKeluar != null) ? WaktuFormatter.format(waktuKeluar) : "-",
                    status
                });
            }

            tabelAktivitas.setModel(model);

            sorter = new TableRowSorter<>(model);
            tabelAktivitas.setRowSorter(sorter);

            tabelAktivitas.getColumnModel().getColumn(0).setPreferredWidth(40);

            int[] centerBefore = {0, 1, 4, 5}; // No, Masuk, Keluar, Status
            int[] centerAfter = {4, 5, 6};
            TableStyler.setTabelStyle(tabelAktivitas, rowYangDibesarkan, 24, centerBefore, centerAfter);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data aktivitas: " + e.getMessage());
        }
    }
}
