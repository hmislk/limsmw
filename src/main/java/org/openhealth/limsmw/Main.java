package org.openhealth.limsmw;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultListModel;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Main extends javax.swing.JFrame {

    List<Analyzer> analyzers;

    private javax.swing.JList<Analyzer> lstAnalyzersCustom;
    private static Map<Analyzer, AnalyzerCommHandler> analyzerCommHandlers = new HashMap<>();

    public Main() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblBillNo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        btnClear = new javax.swing.JButton();
        btnSettings = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        lblBillNo.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N

        txtLog.setColumns(20);
        txtLog.setRows(5);
        jScrollPane1.setViewportView(txtLog);

        jLabel1.setText("Log");

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnSettings.setText("Settings");
        btnSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSettingsActionPerformed(evt);
            }
        });

        jLabel2.setText("Analyzers");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(321, 321, 321)
                                .addComponent(lblBillNo))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnClear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSettings)
                        .addGap(50, 50, 50)
                        .addComponent(lblBillNo))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        lstAnalyzersCustom = new javax.swing.JList<Analyzer>();
        lstAnalyzersCustom.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstAnalyzersCustom.setCellRenderer(new AnalyzerListCellRenderer());
        lstAnalyzersCustom.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstAnalyzersValueChanged(evt);
            }
        });

        jScrollPane2.setViewportView(lstAnalyzersCustom);
        listAnalyzers();
        establishConnections();
    }//GEN-LAST:event_formWindowOpened

    private void establishConnections() {
        txtLog.setText("Interfacing is Starting");
        for (Analyzer analyzer : analyzers) {
            // System.out.println("analyzer = " + analyzer.getName());
            if (analyzer.getInterfaceType() == Analyzer.InterfaceType.SERIAL) {
                try {
                    SerialCommHandler serialCommHandler = new SerialCommHandler(analyzer.getPortName(), analyzer.getBaudRate());
                    analyzerCommHandlers.put(analyzer, serialCommHandler);
                    serialCommHandler.connect();  // Establish the connection
                    txtLog.append("Serial connection established for analyzer: " + analyzer.getName() + "\n");
                    // Start listening
                    serialCommHandler.startListening();
                    txtLog.append("Started listening to analyzer: " + analyzer.getName() + "\n");
                } catch (Exception e) {
                    txtLog.append("Failed to establish serial connection or start listening for analyzer: " + analyzer.getName() + "\n");
                    e.printStackTrace();
                }

            } else if (analyzer.getInterfaceType() == Analyzer.InterfaceType.TCP_IP && analyzer.getCommunicationType() == Analyzer.CommunicationType.SERVER) {
                try {
                    System.err.println("Going to start TCPServerCommHandler for  = " + analyzer.getName());
                    TCPServerCommHandler tcpServerCommHandler = new TCPServerCommHandler(analyzer);
                    analyzerCommHandlers.put(analyzer, tcpServerCommHandler);
                    new Thread(tcpServerCommHandler).start();
                    txtLog.append("TCP/IP server connection established for analyzer: " + analyzer.getName() + "\n");
                } catch (Exception e) {
                    txtLog.append("Failed to establish TCP/IP server connection for analyzer: " + analyzer.getName() + "\n");
                    e.printStackTrace();
                }
            } else if (analyzer.getInterfaceType() == Analyzer.InterfaceType.TCP_IP && analyzer.getCommunicationType() == Analyzer.CommunicationType.CLIENT) {
                try {
                    TCPClientCommHandler tcpClientCommHandler = new TCPClientCommHandler(analyzer.getIpAddress(), analyzer.getPort());
                    analyzerCommHandlers.put(analyzer, tcpClientCommHandler);
                    txtLog.append("TCP/IP client connection established for analyzer: " + analyzer.getName() + "\n");
                } catch (Exception e) {
                    txtLog.append("Failed to establish TCP/IP client connection for analyzer: " + analyzer.getName() + "\n");
                    e.printStackTrace();
                }
            }
        }
    }

    private void listAnalyzers() {
        analyzers = PrefsController.getPreference().getAnalyzers();
        Collections.sort(analyzers, new Comparator<Analyzer>() {
            @Override
            public int compare(Analyzer a1, Analyzer a2) {
                return a1.getName().compareToIgnoreCase(a2.getName());
            }
        });

        DefaultListModel<Analyzer> model = new DefaultListModel<>();
        for (Analyzer analyzer : analyzers) {
            model.addElement(analyzer);
        }

        lstAnalyzersCustom.setModel(model);
        lstAnalyzersCustom.setCellRenderer(new AnalyzerListCellRenderer());
    }

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        txtLog.setText("");
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSettingsActionPerformed
        Settings settings = new Settings();
        settings.setVisible(true);
    }//GEN-LAST:event_btnSettingsActionPerformed

    private void lstAnalyzersValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            Analyzer selectedAnalyzer = lstAnalyzersCustom.getSelectedValue();
            if (selectedAnalyzer != null) {
                // System.out.println("Selected Analyzer: " + selectedAnalyzer.getName());
            }
        }
    }

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
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSettings;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblBillNo;
    private javax.swing.JTextArea txtLog;
    // End of variables declaration//GEN-END:variables

}
