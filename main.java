/**
 * Main - write a description of the class here
 * Created on 02/02/2020
 * 
 * Jordan Christensen
 * Version #b0.1
 * 
 */

import java.util.ArrayList;
import java.nio.channels.*;
import java.util.Random;
import java.lang.Object;
import java.awt.event.*;
import java.util.zip.*;
import java.nio.file.*;
import javax.swing.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.io.*;

public class Main extends JFrame {
    // MARK: - Instance Variables
    private Controller controller;
    private JPanel panel;
    
    private Dimension buttonSize = new Dimension(75, 30);
    private Dimension windowSize = new Dimension(225, 150);
    
    private int buttons = 0;

    // MARK: - Constructor
    public Main() {
        super("Blobs R Us");
        this.controller = new Controller();
        
        panel = new JPanel();
        add(panel);
        setSize(windowSize);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        
        panel.setLayout(new FlowLayout());
        
        applicationPaneHome();
    }
    
    public static void main (String[] args) {
        Main main = new Main();
    }
    
    //MARK: - View Methods
    private void applicationPaneHome() {
        panel.removeAll(); panel.revalidate(); panel.repaint();
        
        JLabel options = new JLabel("\tSHSH Options:\t", SwingConstants.CENTER); 
        options.setPreferredSize(new Dimension(250, 20)); panel.add(options);
        
        JButton savedDevice = new JButton("Saved");
        savedDevice.setPreferredSize(buttonSize); panel.add(savedDevice);
        JButton newDevice = new JButton("New");
        newDevice.setPreferredSize(buttonSize); panel.add(newDevice);
        JButton ranDevice = new JButton("Random");
        ranDevice.setPreferredSize(buttonSize);panel.add(ranDevice);
        JButton checkBlob = new JButton("Check Blob"); panel.add(checkBlob);
        
        savedDevice.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            applicationPaneSaved();
        }});
        newDevice.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            applicationPaneNew();
        }});
        ranDevice.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            applicationPaneRandom();
        }});
        
        checkBlob.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            applicationPaneCheck();
        }});
        
        setSize(windowSize);
    }
    
    private void applicationPaneSaved() {
        panel.removeAll(); panel.revalidate(); panel.repaint();
        Device[] deviceList = controller.getSavedDevices();
        
        if (deviceList.length == 0) {
            JLabel selectDevice = new JLabel("You don't have any saved devices", SwingConstants.CENTER); 
            selectDevice.setPreferredSize(new Dimension(500, 20)); panel.add(selectDevice);
        
            setSize(500, 300);
        }
        else {
            JLabel selectDevice = new JLabel("Select a Device", SwingConstants.CENTER); panel.add(selectDevice);
            selectDevice.setPreferredSize(new Dimension(500, 20));
            
            panel.add(new JLabel("\tAmount of Blobs to be saved:  \t", SwingConstants.CENTER));
            JTextField blobsNum = new JTextField(); panel.add(blobsNum);
            blobsNum.setPreferredSize(new Dimension(100, 20));
            
            for (int i=0; i<deviceList.length; i++) {
                JButton dev = new JButton(deviceList[i].getName());
                panel.add(dev);
                int val = i;
                dev.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        try {
                        for (int i=0; i<Integer.parseInt(blobsNum.getText()); i++) {
                                if (controller.seeOutput()) System.out.println(controller.saveBlobs(deviceList[val], 1));
                                else controller.saveBlobs(deviceList[val], 1);
                        }
                        } catch (NumberFormatException e) {
                            if (controller.seeOutput()) System.out.println(controller.saveBlobs(deviceList[val], 1));
                            else controller.saveBlobs(deviceList[val], 1);
                        }
                    }
                });
            }
            
        }
        JButton back = new JButton("Back"); panel.add(back);
        back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applicationPaneHome();
            }
        });
            
        setSize(500, 300);
        panel.revalidate(); panel.repaint();
    }
    
    private void applicationPaneNew() {
        panel.removeAll(); panel.revalidate(); panel.repaint();
        
        panel.add(new JLabel("\tEnter device name:  \t", SwingConstants.CENTER));
        JTextField devName = new JTextField(); panel.add(devName); 
        devName.setPreferredSize(new Dimension(100, 20));
        panel.add(new JLabel("\tEnter device ECID:   \t", SwingConstants.CENTER));
        JTextField devECID = new JTextField(); panel.add(devECID);
        devECID.setPreferredSize(new Dimension(100, 20));
        panel.add(new JLabel("\tEnter device model:\t", SwingConstants.CENTER));
        JTextField devModel = new JTextField(); panel.add(devModel);
        devModel.setPreferredSize(new Dimension(100, 20));
        panel.add(new JLabel("\tAmount of Blobs:    \t", SwingConstants.CENTER));
        JTextField devAmount = new JTextField(); panel.add(devAmount); 
        devAmount.setPreferredSize(new Dimension(100, 20));
        
        JButton submit = new JButton("Submit"); panel.add(submit);
        submit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Device temp = new Device(devName.getText(), devModel.getText(), devECID.getText());
                controller.saveDevice(temp);
                int blobCount = Integer.parseInt(devAmount.getText());
                
                if (controller.seeOutput()) System.out.println(controller.saveBlobs(temp, blobCount));
                else controller.saveBlobs(temp, blobCount);
            }
        });
        
        JButton back = new JButton("Back"); panel.add(back);
        back.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    applicationPaneHome();
                }
            });
        
        setSize(300, 170);
    }
    
    private void applicationPaneRandom() {
        panel.removeAll(); panel.revalidate(); panel.repaint();
        
        panel.add(new JLabel("Amount of Blobs to Save: ", SwingConstants.CENTER));
        JTextField blobsNum = new JTextField(); panel.add(blobsNum); 
        blobsNum.setPreferredSize(new Dimension(100, 20));
        
        JButton submit = new JButton("Submit"); panel.add(submit);
        submit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                if (controller.seeOutput()) System.out.println(controller.saveRandomBlobs(Integer.parseInt(blobsNum.getText())));
                else controller.saveRandomBlobs(Integer.parseInt(blobsNum.getText()));
            }
        });
        JButton back = new JButton("Back"); panel.add(back);
        back.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    applicationPaneHome();
                }
            });
        
        setSize(350, 100);
    }
    
    private void applicationPaneCheck() {
        panel.removeAll(); panel.revalidate(); panel.repaint();
        
        panel.add(new JLabel("Selected SHSH2 blob to validate:", SwingConstants.CENTER));
        
        FileDialog dialog = new FileDialog((Frame)null, "Selected SHSH2 blob to validate:");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        String shshblob = dialog.getFile();
        String printBlob = shshblob;
        while (shshblob.length() < 1) {
            panel.revalidate();
            panel.repaint();
        }
        if (shshblob.length() > 50) {
            printBlob = shshblob.substring(0, 46) + "...shsh2";
        }
        panel.add(new JLabel(printBlob, SwingConstants.CENTER));
        JButton confirmBlob = new JButton("Confirm"); panel.add(confirmBlob);
        
        confirmBlob.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    String urlString = "";
                    /* try {
                        saveUrl("url", urlString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } */
                }
            });
        
        JButton back = new JButton("Back"); panel.add(back);
        back.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    applicationPaneHome();
                }
            });
            
        panel.revalidate(); panel.repaint();
        setSize(450, 150);
    }

    // MARK: - Methods
    public void ret() {
        return;
    }
}
