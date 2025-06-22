package com.mycompany.elibrary;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ScannerDialog extends JDialog {
    
    private Webcam webcam;
    private WebcamPanel webcamPanel;
    private JComboBox<String> comboKategori;
    private JPanel webcamContainer;
    private boolean isWebcamRunning = false;

    public ScannerDialog(JFrame parent) {
        super(parent, "e - Library | Scanner", true);
        setSize(700, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // === DROPDOWN & BUTTON ===
        JPanel controlPanel = new JPanel(new FlowLayout());
        comboKategori = new JComboBox<>(new String[]{"Absensi", "Pinjam & Kembali"});
        JButton btnMulaiScan = new JButton("Mulai Scan");

        controlPanel.add(new JLabel("Kategori:"));
        controlPanel.add(comboKategori);
        controlPanel.add(btnMulaiScan);

        add(controlPanel, BorderLayout.SOUTH);

        // === KONTENER PREVIEW WEBCAM (kosong dulu) ===
        webcamContainer = new JPanel(new BorderLayout());
        add(webcamContainer, BorderLayout.CENTER);

        // === AKSI SAAT MULAI SCAN ===
        btnMulaiScan.addActionListener(e -> {
            stopWebcam(); // matikan dulu kalau sebelumnya sudah jalan

            String selected = (String) comboKategori.getSelectedItem();
            if (selected == null) return;

            webcam = Webcam.getDefault();
            if (webcam == null) {
                JOptionPane.showMessageDialog(this, "Webcam tidak ditemukan.");
                return;
            }

            webcam.setViewSize(new Dimension(640, 480));
            webcamPanel = new WebcamPanel(webcam);
            webcamPanel.setMirrored(true);

            webcamContainer.removeAll(); // kosongkan preview sebelumnya
            webcamContainer.add(webcamPanel, BorderLayout.CENTER);
            webcamContainer.revalidate();
            webcamContainer.repaint();

            webcam.open();
            isWebcamRunning = true;

            JOptionPane.showMessageDialog(this, "Mode " + selected + " Dipilih!");
        });

        // === SAAT KATEGORI BERUBAH: matikan webcam dulu ===
        comboKategori.addActionListener(e -> {
            stopWebcam(); // matikan kalau berpindah kategori
        });

        // === SAAT DITUTUP ===
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopWebcam();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                stopWebcam();
            }
        });

        setVisible(true);
    }

    private void stopWebcam() {
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
        if (webcamContainer != null) {
            webcamContainer.removeAll();
            webcamContainer.revalidate();
            webcamContainer.repaint();
        }
        isWebcamRunning = false;
    }

    @Override
    public void dispose() {
        stopWebcam();
        super.dispose();
    }
}
