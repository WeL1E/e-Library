package com.mycompany.elibrary;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class ManajemenBukuPanel extends JPanel {

    private JTable tabelBuku;
    private JScrollPane scrollPane;
    private final Set<Integer> rowYangDibesarkan = new HashSet<>();

    public ManajemenBukuPanel() {
        setLayout(new BorderLayout());

        tabelBuku = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelBuku.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelBuku.setFillsViewportHeight(true);
        tabelBuku.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tabelBuku.setRowHeight(24);
        tabelBuku.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));

        scrollPane = new JScrollPane(tabelBuku);
        add(scrollPane, BorderLayout.CENTER);

        loadData();
        
        //setting kolom tabel
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        
        tabelBuku.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        tabelBuku.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        tabelBuku.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);

        tabelBuku.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabelBuku.rowAtPoint(e.getPoint());
                    if (row == -1) return;

                    if (rowYangDibesarkan.contains(row)) {
                        tabelBuku.setRowHeight(row, 24);
                        rowYangDibesarkan.remove(row);
                    } else {
                        tabelBuku.setRowHeight(row, 60);
                        rowYangDibesarkan.add(row);
                    }

                    tabelBuku.repaint(); // trigger render ulang
                }
            }
        });
    }

    private void loadData() {
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

            // Center alignment untuk angka
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            tabelBuku.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Tahun
            tabelBuku.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Jumlah
            tabelBuku.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Tersedia

            // Renderer semua kolom pakai ellipsis jika tidak diperbesar
            for (int i = 0; i < model.getColumnCount(); i++) {
                int colIndex = i;
                tabelBuku.getColumnModel().getColumn(i).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
                    if (rowYangDibesarkan.contains(row)) {
                        JTextArea textArea = new JTextArea(value != null ? value.toString() : "");
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        textArea.setFont(table.getFont());
                        textArea.setOpaque(true);
                        textArea.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                        textArea.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                        return textArea;
                    } else {
                        JLabel label = new JLabel();
                        String teks = value != null ? value.toString() : "";
                        label.setText(teks.length() > 40 ? teks.substring(0, 37) + "..." : teks);
                        label.setToolTipText(teks);
                        label.setFont(table.getFont());
                        label.setOpaque(true);
                        label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                        label.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                        label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                        return label;
                    }
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data buku: " + e.getMessage());
        }
    }
}
