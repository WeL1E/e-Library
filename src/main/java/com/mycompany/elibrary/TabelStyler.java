package com.mycompany.elibrary;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

public class TabelStyler {

    public static void setTabelStyle(JTable tabel, Set<Integer> rowYangDibesarkan, int defaultRowHeight) {
        tabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tabel.setRowHeight(defaultRowHeight);
        tabel.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.setFillsViewportHeight(true);

        // Klik dua kali untuk perbesar baris
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

        // Efek ellipsis + tooltip
        TableModel model = tabel.getModel();
        for (int i = 0; i < model.getColumnCount(); i++) {
            int col = i;
            tabel.getColumnModel().getColumn(col).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
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
    }

    public static void setCenterAlignment(JTable tabel, int... columnIndexes) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int col : columnIndexes) {
            tabel.getColumnModel().getColumn(col).setCellRenderer(centerRenderer);
        }
    }
}
