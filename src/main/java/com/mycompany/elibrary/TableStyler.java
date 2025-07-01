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
        Color headerLineColor = new Color(225, 225, 225);
        tabel.setShowHorizontalLines(true);
        tabel.setShowVerticalLines(true);
        tabel.setGridColor(headerLineColor);

        TableModel model = tabel.getModel();
        for (int i = 0; i < model.getColumnCount(); i++) {
            final int colIndex = i;
            final int[] finalCenterColumns = centerColumns;
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {

                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, table.getGridColor()));
                    setHorizontalAlignment(TableStyler.contains(finalCenterColumns, column) ? SwingConstants.CENTER : SwingConstants.LEFT);
                    setVerticalAlignment(SwingConstants.CENTER);
                    setToolTipText(value != null ? value.toString() : "");

                    return this;
                }
            };

            tabel.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private static boolean contains(int[] arr, int val) {
        if (arr == null) return false;
        for (int v : arr) if (v == val) return true;
        return false;
    }
}
