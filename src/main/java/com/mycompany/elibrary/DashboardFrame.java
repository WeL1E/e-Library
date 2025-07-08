/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.elibrary;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.Timer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author aldor
 */
public class DashboardFrame extends javax.swing.JFrame {
    private ManajemenBukuPanel manajemenBukuPanel;
    private AktivitasPanel aktivitasPanel;
    private PinjamPanel pinjamPanel;
    public DashboardFrame(){
        this("DASHBOARD");
    }

    /**
     * Creates new form DashboardFrame
     */
    public DashboardFrame(String halamanAwal) {
        setUndecorated(false);
        initComponents();

        // Hilangkan suara beep saat txtSearch kosong + tekan tombol tertentu
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_ENTER 
                        || e.getKeyCode() == KeyEvent.VK_DELETE 
                        || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
                    && txtSearch.getText().trim().isEmpty()) {
                    e.consume(); // Hentikan event agar tidak beep
                }
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String keyword = txtSearch.getText().trim().toLowerCase();

                switch (panelAktif) {
                    case "Aktivitas":
                        if (aktivitasPanel != null) {
                            aktivitasPanel.search(keyword);
                        }
                        break;
                    case "Pinjam":
                        if (pinjamPanel != null) {
                            pinjamPanel.search(keyword);
                        }
                        break;
                    case "ManajemenBuku":
                        if (manajemenBukuPanel != null) {
                            manajemenBukuPanel.search(keyword);
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        // Styling waktu & label jumlah buku
        lblWaktu.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWaktu.setForeground(new Color(30, 30, 30));
        styleLabelInfo(lblTotalBuku, lblWaktu); 
        lblTotalBuku.setVisible(false);

        tampilkanTotalBuku();

        // Styling tombol sidebar
        JButton[] semuaButton = {
            btnScan,
            btnAktivitas,
            btnManajemenBuku,
            btnPinjam, // pinjam & kembali
            btnLogout
        };

        for (JButton btn : semuaButton) {
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        }
        btnPinjam.addActionListener(e -> tampilkanPanelPinjam());
        btnAktivitas.addActionListener(e -> tampilkanAktivitas());
        btnManajemenBuku.addActionListener(e -> tampilkanManajemenBuku());
        // Pengaturan JFrame
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("e - library | Dashboard/Aktivitas");
        setResizable(false);
        MainPanel.setLayout(new CardLayout());

        // Timer real-time untuk waktu sekarang
        lblWaktu.setText(WaktuFormatter.now());
        Timer timer = new Timer(1000, e -> lblWaktu.setText(WaktuFormatter.now()));
        timer.start();

        // Panel awal
        if (halamanAwal.equalsIgnoreCase("AKTIVITAS")) {
            tampilkanAktivitas();
        }
    }

    public void refreshAktivitasPanel() {
        tampilkanAktivitas();
    }
    
    private void tampilkanManajemenBuku() {
        if (manajemenBukuPanel == null) {
            manajemenBukuPanel = new ManajemenBukuPanel(txtSearch, lblTotalBuku);
        }
        MainPanel.removeAll();
        MainPanel.add(manajemenBukuPanel);
        MainPanel.revalidate();
        MainPanel.repaint();
        panelAktif = "ManajemenBuku";

        tampilkanTotalBuku();
        lblTotalBuku.setVisible(true);

        String keyword = txtSearch.getText().trim().toLowerCase();
        manajemenBukuPanel.search(keyword);
    }

    private void tampilkanAktivitas() {
        aktivitasPanel = new AktivitasPanel(txtSearch);
        MainPanel.removeAll();
        MainPanel.add(aktivitasPanel);
        MainPanel.revalidate();
        MainPanel.repaint();
        panelAktif = "Aktivitas";
        
        lblTotalBuku.setVisible(false);
    }

    public void tampilkanPanelPinjam() {
        if (pinjamPanel == null) {
            pinjamPanel = new PinjamPanel();
        }
        MainPanel.removeAll();
        MainPanel.add(pinjamPanel);
        MainPanel.revalidate();
        MainPanel.repaint();
        panelAktif = "Pinjam";
        
        lblTotalBuku.setVisible(false);
    }

    
    private String panelAktif = ""; // untuk melacak panel aktif saat ini

    private void styleLabelInfo(JLabel target, JLabel referensi){
        target.setFont(referensi.getFont());
        target.setForeground(referensi.getForeground());
        target.setBorder(referensi.getBorder());
    }
    
    private void tampilkanTotalBuku(){
        try (Connection conn = DBConnection.connect()){
            String sql = "SELECT SUM(stock) FROM buku";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                int total = rs.getInt(1);
                lblTotalBuku.setText("Total Stok Buku: " + total);
                lblTotalBuku.setVisible(true);
            }
        } catch (SQLException e){
            lblTotalBuku.setText("Gagal memuat data buku");
            System.err.println("Error: " + e.getMessage());
        }
    }
    public ManajemenBukuPanel getManajemenBukuPanel() {
    return manajemenBukuPanel;
}

    public PinjamPanel getPinjamPanel() {
    return pinjamPanel;
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnAktivitas = new javax.swing.JButton();
        btnPinjam = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        btnScan = new javax.swing.JButton();
        btnManajemenBuku = new javax.swing.JButton();
        MainPanel = new javax.swing.JPanel();
        PanelInfoAtas = new javax.swing.JPanel();
        lblTotalBuku = new javax.swing.JLabel();
        lblWaktu = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnAktivitas.setText("Aktivitas");
        btnAktivitas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAktivitasActionPerformed(evt);
            }
        });

        btnPinjam.setText("Pinjam & kembali");
        btnPinjam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPinjamActionPerformed(evt);
            }
        });

        btnLogout.setText("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        btnScan.setText("Scan");
        btnScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScanActionPerformed(evt);
            }
        });

        btnManajemenBuku.setText("Manajemen Buku");
        btnManajemenBuku.setMaximumSize(new java.awt.Dimension(128, 27));
        btnManajemenBuku.setMinimumSize(new java.awt.Dimension(128, 27));
        btnManajemenBuku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManajemenBukuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnManajemenBuku, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAktivitas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnScan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPinjam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(9, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnScan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAktivitas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnManajemenBuku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPinjam)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLogout)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout MainPanelLayout = new javax.swing.GroupLayout(MainPanel);
        MainPanel.setLayout(MainPanelLayout);
        MainPanelLayout.setHorizontalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 259, Short.MAX_VALUE)
        );
        MainPanelLayout.setVerticalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        lblTotalBuku.setText("TB");

        lblWaktu.setText("Time");

        javax.swing.GroupLayout PanelInfoAtasLayout = new javax.swing.GroupLayout(PanelInfoAtas);
        PanelInfoAtas.setLayout(PanelInfoAtasLayout);
        PanelInfoAtasLayout.setHorizontalGroup(
            PanelInfoAtasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelInfoAtasLayout.createSequentialGroup()
                .addComponent(lblTotalBuku)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblWaktu))
        );
        PanelInfoAtasLayout.setVerticalGroup(
            PanelInfoAtasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelInfoAtasLayout.createSequentialGroup()
                .addGroup(PanelInfoAtasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblWaktu)
                    .addComponent(lblTotalBuku))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
            }
        });

        jLabel2.setText("Cari Data:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearch)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelInfoAtas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtSearch)
                    .addComponent(PanelInfoAtas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void refreshTransaksiPanel() {
        MainPanel.removeAll();
        MainPanel.add(new PinjamPanel()); // atau nama panel transaksi kamu
        MainPanel.revalidate();
        MainPanel.repaint();
    }

    private void btnPinjamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPinjamActionPerformed
        // TODO add your handling code here:
        setTitle("e - library | Dashboard/Pinjam & Kembali");
        lblTotalBuku.setVisible(false);
        pinjamPanel = new PinjamPanel();
        MainPanel.removeAll();
        MainPanel.add(pinjamPanel);
        MainPanel.revalidate();
        MainPanel.repaint();
        panelAktif = "Pinjam";
    }//GEN-LAST:event_btnPinjamActionPerformed

    private void btnScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScanActionPerformed
        // TODO add your handling code here:
        new ScannerDialog(this);
        lblTotalBuku.setVisible(false);
    }//GEN-LAST:event_btnScanActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        lblTotalBuku.setVisible(false);
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin logout?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION){
            dispose(); // Tutup dashboard
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true); // Buka kembali form login
            });
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnManajemenBukuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManajemenBukuActionPerformed
        // TODO add your handling code here:
        setTitle("e - library | Dashboard/Manajemen Buku");
        lblTotalBuku.setVisible(false);
        tampilkanManajemenBuku();
    }//GEN-LAST:event_btnManajemenBukuActionPerformed

    private void btnAktivitasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAktivitasActionPerformed
        // TODO add your handling code here:
        setTitle("e - library | Dashboard/Aktivitas");
        lblTotalBuku.setVisible(false);
        tampilkanAktivitas();
    }//GEN-LAST:event_btnAktivitasActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_txtSearchKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DashboardFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel MainPanel;
    private javax.swing.JPanel PanelInfoAtas;
    private javax.swing.JButton btnAktivitas;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnManajemenBuku;
    private javax.swing.JButton btnPinjam;
    private javax.swing.JButton btnScan;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblTotalBuku;
    private javax.swing.JLabel lblWaktu;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
