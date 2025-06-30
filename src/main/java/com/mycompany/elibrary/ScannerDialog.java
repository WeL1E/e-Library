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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ScannerDialog extends JDialog {

    private Webcam webcam;
    private WebcamPanel webcamPanel;
    private JPanel webcamContainer;
    private boolean isWebcamRunning = false;
    private long lastInvalidPopupTime = 0;
    private static final int POPUP_COOLDOWN_MS = 2000;

    public ScannerDialog(JFrame parent) {
        super(parent, "e - Library | Scanner", true);
        setSize(700, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton btnMulaiScan = new JButton("Mulai Scan");
        controlPanel.add(btnMulaiScan);
        add(controlPanel, BorderLayout.SOUTH);

        webcamContainer = new JPanel(new BorderLayout());
        add(webcamContainer, BorderLayout.CENTER);

        btnMulaiScan.addActionListener(e -> {
            stopWebcam();

            webcam = Webcam.getDefault();
            if (webcam == null) {
                JOptionPane.showMessageDialog(this, "Webcam tidak ditemukan.");
                return;
            }

            webcam.setViewSize(new java.awt.Dimension(640, 480));
            webcamPanel = new WebcamPanel(webcam);
            webcamPanel.setMirrored(true);

            webcamContainer.removeAll();
            webcamContainer.add(webcamPanel, BorderLayout.CENTER);
            webcamContainer.revalidate();
            webcamContainer.repaint();

            webcam.open();
            isWebcamRunning = true;

            JOptionPane.showMessageDialog(this, "📸 Scan dimulai...");

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
                            continue;
                        }

                        if (result != null) {
                            String hasilScan = result.getText().trim();

                            if (isValidNIM(hasilScan)) {
                                isWebcamRunning = false;
                                webcam.close();
                                SwingUtilities.invokeLater(() -> prosesAbsensi(hasilScan, "Absensi"));
                            } else if (isValidKodeBuku(hasilScan)) {
                                isWebcamRunning = false;
                                webcam.close();

                                SwingUtilities.invokeLater(() -> {
                                    String nim = ambilNIMDariAktivitasHariIni();
                                    if (nim != null && isValidNIM(nim)) {
                                        prosesPeminjaman(nim, hasilScan);
                                    } else {
                                        showCooldownPopup("❌ NIM tidak valid atau tidak ditemukan.");
                                    }
                                });
                            } else {
                                showCooldownPopup("❌ Barcode tidak dikenali sebagai NIM atau kode buku.");
                            }
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        });

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

    private void showCooldownPopup(String message) {
        long now = System.currentTimeMillis();
        if (now - lastInvalidPopupTime > POPUP_COOLDOWN_MS) {
            lastInvalidPopupTime = now;
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, message));
        }
    }

    private boolean isValidKodeBuku(String kodeBuku) {
        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT 1 FROM buku WHERE kode_buku = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, kodeBuku);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidNIM(String nim) {
        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT 1 FROM mahasiswa WHERE nim = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nim);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void stopWebcam() {
        if (webcam != null && webcam.isOpen()) webcam.close();
        if (webcamContainer != null) {
            webcamContainer.removeAll();
            webcamContainer.revalidate();
            webcamContainer.repaint();
        }
        isWebcamRunning = false;
    }

    private String ambilNIMDariAktivitasHariIni() {
        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT nim, nama_mahasiswa FROM aktivitas WHERE DATE(waktu_masuk) = CURDATE() AND waktu_keluar IS NULL";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            java.util.List<String> daftarNIM = new java.util.ArrayList<>();
            java.util.Map<String, String> nimKeNama = new java.util.HashMap<>();

            while (rs.next()) {
                String nim = rs.getString("nim");
                String nama = rs.getString("nama_mahasiswa");
                daftarNIM.add(nim);
                nimKeNama.put(nim, nama);
            }

            if (daftarNIM.isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Tidak ada mahasiswa yang sedang berada di perpustakaan.");
                return null;
            } else if (daftarNIM.size() == 1) {
                return daftarNIM.get(0);
            } else {
                String[] pilihan = daftarNIM.stream()
                        .map(nim -> nim + " - " + nimKeNama.get(nim))
                        .toArray(String[]::new);

                String selected = (String) JOptionPane.showInputDialog(
                        this,
                        "Pilih NIM yang sedang berada di perpustakaan:",
                        "Pilih Mahasiswa",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        pilihan,
                        pilihan[0]
                );

                if (selected != null) {
                    return selected.split(" - ")[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Gagal mengambil data aktivitas.");
        }
        return null;
    }

    private void prosesAbsensi(String nim, String kategori) {
        try (Connection conn = DBConnection.connect()) {
            String checkSQL = "SELECT * FROM aktivitas WHERE nim = ? AND DATE(waktu_masuk) = CURDATE() AND waktu_keluar IS NULL";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setString(1, nim);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int idAktivitas = rs.getInt("id_aktivitas");
                String updateSQL = "UPDATE aktivitas SET waktu_keluar = NOW() WHERE id_aktivitas = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
                updateStmt.setInt(1, idAktivitas);
                updateStmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "✅ Keluar dicatat untuk NIM: " + nim);
            } else {
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
                    JOptionPane.showMessageDialog(this, "✅ Masuk dicatat untuk NIM: " + nim, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ NIM tidak ditemukan.");
                }
            }

            SwingUtilities.invokeLater(() -> {
                if (getParent() instanceof DashboardFrame dashboard) {
                    dashboard.refreshAktivitasPanel(); // ✅ tampilkan aktivitas
                }
                dispose();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Terjadi kesalahan saat memproses absensi.");
        }
    }

    private void prosesPeminjaman(String nim, String kodeBuku) {
        try (Connection conn = DBConnection.connect()) {
            String judul = null;
            String sqlJudul = "SELECT judul_buku FROM buku WHERE kode_buku = ?";
            PreparedStatement stmtJudul = conn.prepareStatement(sqlJudul);
            stmtJudul.setString(1, kodeBuku);
            ResultSet rsJudul = stmtJudul.executeQuery();
            if (rsJudul.next()) judul = rsJudul.getString("judul_buku");

            if (judul == null || judul.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Judul buku tidak ditemukan untuk kode: " + kodeBuku);
                return;
            }

            String namaMahasiswa = null;
            String sqlNama = "SELECT nama FROM mahasiswa WHERE nim = ?";
            PreparedStatement stmtNama = conn.prepareStatement(sqlNama);
            stmtNama.setString(1, nim);
            ResultSet rsNama = stmtNama.executeQuery();
            if (rsNama.next()) namaMahasiswa = rsNama.getString("nama");

            if (namaMahasiswa == null || namaMahasiswa.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Nama mahasiswa tidak ditemukan untuk NIM: " + nim);
                return;
            }

            String sql = "SELECT * FROM pinjaman WHERE nim = ? AND kode_buku = ? AND waktu_pinjam IS NOT NULL AND waktu_kembali IS NULL";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nim);
            stmt.setString(2, kodeBuku);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp waktuPinjam = rs.getTimestamp("waktu_pinjam");
                long days = ChronoUnit.DAYS.between(waktuPinjam.toLocalDateTime().toLocalDate(), LocalDate.now());
                int denda = (int) Math.max(0, days - 7) * 500;

                String update = "UPDATE pinjaman SET waktu_kembali = NOW(), denda = ?, keterangan = 'Dikembalikan' WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(update);
                updateStmt.setInt(1, denda);
                updateStmt.setInt(2, rs.getInt("id"));
                updateStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Buku dikembalikan. Denda: Rp" + String.format("%,d", denda).replace(',', '.'));
            } else {
                String insert = "INSERT INTO pinjaman (nim, nama, kode_buku, judul_buku, waktu_pinjam, keterangan) VALUES (?, ?, ?, ?, NOW(), 'Dipinjam')";
                PreparedStatement insertStmt = conn.prepareStatement(insert);
                insertStmt.setString(1, nim);
                insertStmt.setString(2, namaMahasiswa);
                insertStmt.setString(3, kodeBuku);
                insertStmt.setString(4, judul);
                insertStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Buku dipinjamkan ke " + nim + " - " + namaMahasiswa);
            }

            SwingUtilities.invokeLater(() -> {
                if (getParent() instanceof DashboardFrame dashboard) {
                    dashboard.tampilkanPanelPinjam(); // ✅ tampilkan pinjam
                }
                dispose();
            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Gagal memproses transaksi peminjaman.");
        }
    }

    @Override
    public void dispose() {
        stopWebcam();
        super.dispose();
    }
}
