package com.mycompany.elibrary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class AktivitasPanel extends JPanel {

    private JTable tabelAktivitas;
    private final Set<Integer> rowYangDibesarkan = new HashSet<>();

    public AktivitasPanel() {
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

    private void loadDataAktivitas() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("NIM");
        model.addColumn("Nama");
        model.addColumn("Prodi");
        model.addColumn("Waktu Masuk");
        model.addColumn("Waktu Keluar");
        model.addColumn("Keterangan");

        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT nim, nama_mahasiswa, prodi, waktu_masuk, waktu_keluar, keterangan FROM aktivitas ORDER BY waktu_masuk DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
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

            // 🔧 Terapkan styling dari TabelStyler
            TabelStyler.setTabelStyle(tabelAktivitas, rowYangDibesarkan, 24);
            TabelStyler.setCenterAlignment(tabelAktivitas, 3, 4); // waktu_masuk & keluar

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data aktivitas: " + e.getMessage());
        }
    }
}
