package com.mycompany.elibrary;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Set;

public class TableStyler {

    public static void setTabelStyle(JTable tabel, Set<Integer> rowYangDibesarkan, int defaultRowHeight, int[] centerColumns, int[] centerAgainIgnored) {
        tabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tabel.setRowHeight(defaultRowHeight);
        tabel.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.setFillsViewportHeight(true);

        TableModel model = tabel.getModel();
        for (int i = 0; i < model.getColumnCount(); i++) {
            int col = i;
            boolean isCenter = contains(centerColumns, col);

            tabel.getColumnModel().getColumn(col).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
                JLabel label = new JLabel();
                String teks = value != null ? value.toString() : "";
                label.setText(teks);
                label.setToolTipText(teks);
                label.setFont(table.getFont());
                label.setOpaque(true);
                label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                label.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setHorizontalAlignment(isCenter ? SwingConstants.CENTER : SwingConstants.LEADING);

                return label;
            });
        }
    }

    private static boolean contains(int[] arr, int val) {
        if (arr == null) return false;
        for (int v : arr) if (v == val) return true;
        return false;
    }
}
