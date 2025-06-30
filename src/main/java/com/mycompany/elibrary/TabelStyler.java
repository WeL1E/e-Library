package com.mycompany.elibrary;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

public class TabelStyler {

    // Versi 3 parameter (default bold header = true)
    public static void setTabelStyle(JTable tabel, Set<Integer> rowYangDibesarkan, int defaultRowHeight) {
        applyTabelStyle(tabel, rowYangDibesarkan, defaultRowHeight, true);
    }

    // Versi 4 parameter (boleh atur apakah header bold)
    public static void setTabelStyle(JTable tabel, Set<Integer> rowYangDibesarkan, int defaultRowHeight, boolean boldHeader) {
        applyTabelStyle(tabel, rowYangDibesarkan, defaultRowHeight, boldHeader);
    }

    // Method inti yang dipanggil oleh keduanya
    private static void applyTabelStyle(JTable tabel, Set<Integer> rowYangDibesarkan, int defaultRowHeight, boolean boldHeader) {
        tabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tabel.setRowHeight(defaultRowHeight);
        tabel.getTableHeader().setFont(new Font("Segoe UI", boldHeader ? Font.BOLD : Font.PLAIN, 18));
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.setFillsViewportHeight(true);

        // Klik dua kali untuk memperbesar baris
        tabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabel.rowAtPoint(e.getPoint());
                    if (row == -1) return;

                    if (rowYangDibesarkan.contains(row)) {
                        tabel.setRowHeight(row, defaultRowHeight);
                        rowYangDibesarkan.remove(row);
                    } else {
                        tabel.setRowHeight(row, 60);
                        rowYangDibesarkan.add(row);
                    }

                    tabel.repaint();
                }
            }
        });

        // Efek ellipsis + tooltip + wrapping saat baris diperbesar
        TableModel model = tabel.getModel();
        for (int i = 0; i < model.getColumnCount(); i++) {
            int col = i;
            tabel.getColumnModel().getColumn(col).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
                String teks = value != null ? value.toString() : "";

                if (rowYangDibesarkan.contains(row)) {
                    // Baris dibesarkan
                    if (column == 0) {
                        // Kolom "No" saat dibesarkan → Top Center
                        JLabel label = new JLabel(teks);
                        label.setFont(table.getFont());
                        label.setOpaque(true);
                        label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                        label.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        label.setVerticalAlignment(SwingConstants.TOP);
                        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
                        return label;
                    } else {
                        // Kolom lain dibesarkan → TextArea (wrap)
                        JTextArea textArea = new JTextArea(teks);
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        textArea.setFont(table.getFont());
                        textArea.setOpaque(true);
                        textArea.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                        textArea.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                        return textArea;
                    }
                } else {
                    // Default row
                    JLabel label = new JLabel();
                    label.setText(teks.length() > 40 ? teks.substring(0, 37) + "..." : teks);
                    label.setToolTipText(teks);
                    label.setFont(table.getFont());
                    label.setOpaque(true);
                    label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    label.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                    label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                    label.setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEADING);
                    label.setVerticalAlignment(SwingConstants.CENTER);
                    return label;
                }
            });
        }
    }

    public static void setCenterAlignment(JTable tabel, int... columnIndexes) {
        DefaultTableCellRenderer centerTopRenderer = new DefaultTableCellRenderer();
        centerTopRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerTopRenderer.setVerticalAlignment(SwingConstants.TOP);

        for (int col : columnIndexes) {
            tabel.getColumnModel().getColumn(col).setCellRenderer(centerTopRenderer);
        }
    }
}
