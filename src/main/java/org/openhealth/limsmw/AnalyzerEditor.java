/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.openhealth.limsmw;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 *
 * @author Buddhika
 */
public class AnalyzerEditor extends JDialog {

    private Analyzer analyzer;
    private JFrame parentFrame; // Change the variable name to parentFrame
    private boolean isNewAnalyzer;
    private List<Analyzer> analyzerList;

    private JTextField tfName;
    private JTextField tfManufacturer;
    private JTextField tfModel;
    private JTextField tfSerialNumber;
    JTextField tfPort;
    private JComboBox<Analyzer.InterfaceType> cbInterfaceType;
    private JComboBox<Analyzer.InterfaceProtocol> cbInterfaceProtocol;
    private JComboBox<Analyzer.CommunicationType> cbCommunicationType;
    private JComboBox<Analyzer.Encoding> cbEncodingType;
    private JTextField tfBaudRate;
    private JTextField tfIpAddress;

    private UpdateAnalyzerListCallback callback;

    private boolean isUpdated = false;

    public boolean isUpdated() {
        return isUpdated;
    }

    public AnalyzerEditor(Frame parent, Analyzer analyzer, List<Analyzer> analyzerList) {
        this(parent, analyzer, false, analyzerList, null);
    }

    public AnalyzerEditor(Frame parent, Analyzer analyzer, boolean isNewAnalyzer, List<Analyzer> analyzerList, UpdateAnalyzerListCallback callback) {
        super(parent, "Analyzer Editor", true);
        this.analyzer = analyzer;
        this.isNewAnalyzer = isNewAnalyzer;
        this.analyzerList = analyzerList;
        this.callback = callback;
        initComponents();
        loadAnalyzerDetails();
    }

    private void loadAnalyzerDetails() {
        if (isNewAnalyzer) {
            setTitle("Add New Analyzer");
            // You can set default values for the components if necessary.
        } else {
            setTitle("Edit Analyzer");
            // Load the details of the selected Analyzer into the components of the AnalyzerEditor.
        }
    }

    private void initComponents() {
        setTitle("Analyzer Editor");
        setSize(400, 400);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // System.out.println("analyzer = " + analyzer);
        // System.out.println("analyzer.getName() = " + analyzer.getName());

        c.gridx = 0;
        c.gridy = 0;
        add(new JLabel("Name:"), c);
        tfName = new JTextField(analyzer.getName(), 20);
        c.gridx = 1;
        add(tfName, c);

        c.gridx = 0;
        c.gridy = 1;
        add(new JLabel("Manufacturer:"), c);
        tfManufacturer = new JTextField(analyzer.getManufacturer(), 20);
        c.gridx = 1;
        add(tfManufacturer, c);

        c.gridx = 0;
        c.gridy = 2;
        add(new JLabel("Model:"), c);
        tfModel = new JTextField(analyzer.getModel(), 20);
        c.gridx = 1;
        add(tfModel, c);

        c.gridx = 0;
        c.gridy = 3;
        add(new JLabel("Serial Number:"), c);
        tfSerialNumber = new JTextField(analyzer.getSerialNumber(), 20);
        c.gridx = 1;
        add(tfSerialNumber, c);

        c.gridx = 0;
        c.gridy = 4;
        add(new JLabel("Interface Type:"), c);
        cbInterfaceType = new JComboBox<>(Analyzer.InterfaceType.values());
        cbInterfaceType.setSelectedItem(analyzer.getInterfaceType());
        c.gridx = 1;
        add(cbInterfaceType, c);

        c.gridx = 0;
        c.gridy = 5;
        add(new JLabel("Interface Protocol:"), c);
        cbInterfaceProtocol = new JComboBox<>(Analyzer.InterfaceProtocol.values());
        cbInterfaceProtocol.setSelectedItem(analyzer.getInterfaceProtocol());
        c.gridx = 1;
        add(cbInterfaceProtocol, c);

        c.gridx = 0;
        c.gridy = 6;
        add(new JLabel("Communication Type:"), c);
        cbCommunicationType = new JComboBox<>(Analyzer.CommunicationType.values());
        cbCommunicationType.setSelectedItem(analyzer.getCommunicationType());
        c.gridx = 1;
        add(cbCommunicationType, c);

        c.gridx = 0;
        c.gridy = 7;
        add(new JLabel("Encording Type:"), c);
        cbEncodingType = new JComboBox<>(Analyzer.Encoding.values());
        cbEncodingType.setSelectedItem(analyzer.getCommunicationType());
        c.gridx = 1;
        add(cbEncodingType, c);

        c.gridx = 0;
        c.gridy = 8;
        add(new JLabel("Baud Rate:"), c);
        tfBaudRate = new JTextField(String.valueOf(analyzer.getBaudRate()), 20);
        c.gridx = 1;
        add(tfBaudRate, c);

        c.gridx = 0;
        c.gridy = 9;
        add(new JLabel("Port:"), c);
        tfPort = new JTextField(String.valueOf(analyzer.getPort()), 20);
        c.gridx = 1;
        add(tfPort, c);

        c.gridx = 0;
        c.gridy = 10;
        add(new JLabel("IP Address:"), c);
        tfIpAddress = new JTextField(analyzer.getIpAddress(), 30);
        c.gridx = 1;
        add(tfIpAddress, c);

        c.gridx = 0;
        c.gridy = 11;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAnalyzer();
            }
        });
        buttonsPanel.add(btnSave);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeEditor();
            }
        });
        buttonsPanel.add(btnCancel);

        c.fill = GridBagConstraints.HORIZONTAL;
        add(buttonsPanel, c);
    }

    private void saveAnalyzer() {
        analyzer.setName(tfName.getText());
        analyzer.setManufacturer(tfManufacturer.getText());
        analyzer.setModel(tfModel.getText());
        analyzer.setSerialNumber(tfSerialNumber.getText());
        analyzer.setPort(Integer.parseInt(tfPort.getText()));
        analyzer.setInterfaceType((Analyzer.InterfaceType) cbInterfaceType.getSelectedItem());
        analyzer.setInterfaceProtocol((Analyzer.InterfaceProtocol) cbInterfaceProtocol.getSelectedItem());
        analyzer.setCommunicationType((Analyzer.CommunicationType) cbCommunicationType.getSelectedItem());
        analyzer.setEncodingType((Analyzer.Encoding) cbEncodingType.getSelectedItem());
        
        analyzer.setBaudRate(Integer.parseInt(tfBaudRate.getText()));
        analyzer.setIpAddress(tfIpAddress.getText());
        if (isNewAnalyzer) {
            analyzerList.add(analyzer);
        }

        PrefsController.savePrefs();
        isUpdated = true;

        if (callback != null) {
            callback.updateAnalyzerList();
        }

        closeEditor();
    }

    private void closeEditor() {
        setVisible(false);
        dispose();
    }

    public interface UpdateAnalyzerListCallback {

        void updateAnalyzerList();
    }

}
