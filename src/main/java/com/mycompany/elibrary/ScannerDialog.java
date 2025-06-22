package com.mycompany.elibrary;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.sql.*;

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
        comboKategori = new JComboBox<>(new String[]{"Absensi", "Pinjam", "Kembalikan"});
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

            webcam.setViewSize(new java.awt.Dimension(640, 480));
            webcamPanel = new WebcamPanel(webcam);
            webcamPanel.setMirrored(true);

            webcamContainer.removeAll(); // kosongkan preview sebelumnya
            webcamContainer.add(webcamPanel, BorderLayout.CENTER);
            webcamContainer.revalidate();
            webcamContainer.repaint();

            webcam.open();
            isWebcamRunning = true;

            JOptionPane.showMessageDialog(this, "Mode " + selected + " Dipilih!");

            // === THREAD SCAN BARCODE ===
            new Thread(() -> {
                while (isWebcamRunning) {
                    try {
                        BufferedImage image = webcam.getImage();
                        if (image == null) continue;

                        LuminanceSource source = new BufferedImageLuminanceSource(image);
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                        Result result;
                        try {
                            result = new MultiFormatReader().decode(bitmap);
                        } catch (NotFoundException ex) {
                            continue; // belum ada barcode
                        }

                        if (result != null) {
                            String nimHasilScan = result.getText().trim();
                            String kategoriDipilih = (String) comboKategori.getSelectedItem();

                            isWebcamRunning = false;
                            webcam.close();

                            SwingUtilities.invokeLater(() -> prosesAbsensi(nimHasilScan, kategoriDipilih));
                            break;
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        });

        // === SAAT KATEGORI BERUBAH: matikan webcam dulu ===
        comboKategori.addActionListener(e -> stopWebcam());

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

    private void prosesAbsensi(String nim, String kategori) {
        try {
            Connection conn = DBConnection.connect();

            String checkSQL = "SELECT * FROM aktivitas WHERE nim = ? AND DATE(waktu_masuk) = CURDATE() AND waktu_keluar IS NULL";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setString(1, nim);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Ada entri aktif → update waktu_keluar
                int idAktivitas = rs.getInt("id_aktivitas");

                String updateSQL = "UPDATE aktivitas SET waktu_keluar = NOW() WHERE id_aktivitas = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
                updateStmt.setInt(1, idAktivitas);
                updateStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Keluar dicatat untuk NIM: " + nim);
            } else {
                // Tidak ada entri aktif → INSERT masuk baru
                String mhsSQL = "SELECT nama, prodi FROM mahasiswa WHERE nim = ?";
                PreparedStatement mhsStmt = conn.prepareStatement(mhsSQL);
                mhsStmt.setString(1, nim);
                ResultSet rsMhs = mhsStmt.executeQuery();

                if (rsMhs.next()) {
                    String nama = rsMhs.getString("nama");
                    String prodi = rsMhs.getString("prodi");

                    String insertSQL = "INSERT INTO aktivitas (nim, nama_mahasiswa, prodi, waktu_masuk, keterangan) VALUES (?, ?, ?, NOW(), ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
                    insertStmt.setString(1, nim);
                    insertStmt.setString(2, nama);
                    insertStmt.setString(3, prodi);
                    insertStmt.setString(4, kategori);
                    insertStmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "✅ Masuk dicatat untuk NIM: " + nim);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Terjadi kesalahan saat memproses absensi.");
                }
            }

            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Terjadi kesalahan saat memproses absensi.");
        }
    }

    @Override
    public void dispose() {
        stopWebcam();
        super.dispose();
    }
}
