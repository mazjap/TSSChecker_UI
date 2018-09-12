import java.util.ArrayList;
import java.util.Random;
import java.lang.Object;
import java.awt.event.*;
import java.nio.file.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.io.*;

public class main {
    //Leave this as is if TSSChecker is in the same folder as main.java
    static String tsscheckerPath = "directory/to/tsschecker_macos";
    
    //Change this to true if you would like to see the output from TSSChecker or to false if you don't
    static boolean seeOutput = false;
    
    static ArrayList<device> deviceList = new ArrayList<device>();
    
    static JFrame frame = new JFrame("TSS UI");
    static JPanel panel = new JPanel();
    
    static Dimension buttonSize = new Dimension(75, 30);
    
    public static void main (String[] args) {
        File file = new File("Objects");
        String objectPath = file.getAbsolutePath();
        Path path = Paths.get(objectPath);
        if (Files.notExists(path)) {
            file.mkdir();
        }
        
        applicationPaneHome();
    }
    
    public static void applicationPaneHome() {
        panel.setLayout(new FlowLayout());
        panel.removeAll(); panel.revalidate(); panel.repaint();
        
        JLabel options = new JLabel("\tSHSH Options:\t", SwingConstants.CENTER); 
        options.setPreferredSize(new Dimension(250, 20)); panel.add(options);
        
        JButton savedDevice = new JButton("Saved");
        savedDevice.setPreferredSize(buttonSize); panel.add(savedDevice);
        JButton newDevice = new JButton("New");
        newDevice.setPreferredSize(buttonSize); panel.add(newDevice);
        JButton ranDevice = new JButton("Random");
        ranDevice.setPreferredSize(buttonSize);panel.add(ranDevice);
        
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
            device temp = new device();
            if (seeOutput) System.out.println(executeCommand(temp));
            else executeCommand(temp);
        }});
        
        frame.add(panel);
        frame.setSize(300, 100);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
    }
    
    public static void applicationPaneSaved() {
        File file = new File("Objects");
        File[] listOfFiles = file.listFiles();
        for (int i=1; i<file.list().length; i++) {
            try {
                ObjectInputStream is = new ObjectInputStream(new FileInputStream(listOfFiles[i]));
                device temp = (device) is.readObject();
                deviceList.add(temp);
                is.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            
        }
        
        panel.removeAll(); panel.revalidate(); panel.repaint();
        JLabel selectDevice = new JLabel("Select a Device"); panel.add(selectDevice);
        for (int i=0; i<deviceList.size(); i++) {
            JButton dev = new JButton(deviceList.get(i).getName());
            panel.add(dev);
            int val = i;
            dev.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (seeOutput) System.out.println(executeCommand(deviceList.get(val)));
                    else executeCommand(deviceList.get(val));
                }
            });
        }
        JButton back = new JButton("Back");
        back.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    applicationPaneHome();
                }
            });
        int frameSize = (1+deviceList.size())/2*100;
        frame.setSize(500, frameSize);
    }
    
    public static void applicationPaneNew() {
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
        
        JButton submit = new JButton("Submit"); panel.add(submit);
        submit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                device temp = new device(devName.getText(), devModel.getText(), devECID.getText());
                int nameInt = 1;
                
                try {
                    ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("Objects/" + temp.getName() + ".txt"));
                    os.writeObject(temp);
                    os.close();
                } catch (FileNotFoundException e){
                    System.out.println("File Error");
                } catch (IOException e) {
                    System.out.println ("There was an Error");
                }
                
                if (seeOutput) System.out.println(executeCommand(temp));
                else executeCommand(temp);
            }
        });
        frame.setSize(300, 150);
    }
    
    public static String executeCommand(device iPhone) {
        String savePath;
        File file = new File("Blobs");
        savePath = file.getAbsolutePath();
        Path path = Paths.get(savePath);
    
        if (Files.notExists(path)) {
            file.mkdir();
        }
        
    
        String BorD = "-B ";
        if (tsscheckerPath.equals("directory/to/tsschecker_macos") || tsscheckerPath.equals("")) {
            tsscheckerPath = savePath.substring(0, savePath.length()-5) + "tsschecker_macos";
        }
        
        if (iPhone.getModel().charAt(0) == 'i' || iPhone.getModel().charAt(0) == 'I') BorD = "-d ";
    
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(tsscheckerPath + " -l " + BorD + 
                iPhone.getModel() + " -e " + iPhone.getECID() + " -s --save-path " + savePath);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }
}
