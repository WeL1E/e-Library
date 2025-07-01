package com.mycompany.elibrary;

import com.mycompany.elibrary.DBConnection;
import com.mycompany.elibrary.WaktuFormatter;
import com.mycompany.elibrary.TableStyler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Set;

public class TableDataLoader {

    public static void loadAktivitasData(JTable table, Set<Integer> rowYangDibesarkan, JPanel panelTarget) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("No");
        model.addColumn("NIM");
        model.addColumn("Nama");
        model.addColumn("Prodi");
        model.addColumn("Check in");
        model.addColumn("Check out");
        model.addColumn("Keterangan");

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

            table.setModel(model);
            table.getColumnModel().getColumn(0).setPreferredWidth(40);

            TableStyler.setTabelStyle(table, rowYangDibesarkan, 24, new int[]{0, 4, 5}, new int[]{4, 5});

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panelTarget, "Gagal memuat data aktivitas: " + e.getMessage());
        }
    }
}
