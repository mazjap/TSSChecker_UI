import java.io.InputStreamReader;
import java.util.Random;
import java.awt.event.*;
import java.lang.Object;
import java.nio.file.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.io.*;

public class main {
    static String[][] deviceList = {
    //Delete, add, and change these according to your hearts desires. Both model and boardconfig work
    //   Device Name       Model           ECID          (Make sure you add the comma)
        {"iPhone 7P", "D111AP", "518410A58F2506"},
        {"A Cool iPhone", "D11AP", "B0"},
        {"Other", "iPhone7,1", "1122334454321"},
        {"ipone u", "iPhone9,1", "58320357294A"}
    };
    
    //Leave this as is if TSSChecker is in the same folder as main.java
    static String tsscheckerPath = "directory/to/tsschecker_macos";
    
    //Change this to true if you would like to see the output from TSSChecker or to false if you don't
    static boolean seeOutput = false;
    
    static JFrame frame = new JFrame("TSS UI");
    static JPanel panel = new JPanel();
    
    static Dimension buttonSize = new Dimension(75, 30);
    
    public static void main (String[] args) {
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
            if (seeOutput) System.out.println(executeCommand(getRandModel(), getRandECID()));
            else executeCommand(getRandModel(), getRandECID());
        }});
        
        
        frame.add(panel);
        frame.setSize(300, 100);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
    }
    
    public static void applicationPaneSaved() {
        panel.removeAll(); panel.revalidate(); panel.repaint();
        JLabel selectDevice = new JLabel("Select a Device"); panel.add(selectDevice);
        for (int i=0; i<deviceList.length; i++) {
            JButton dev = new JButton(deviceList[i][0]);
            panel.add(dev);
            String model = deviceList[i][1];
            String ECID = deviceList[i][2];
            dev.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (seeOutput) System.out.println(executeCommand(model, ECID));
                    else executeCommand(model, ECID);
                }
            });
        }
        int frameSize = (1+deviceList.length)/2*100;
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
                if (seeOutput) System.out.println(executeCommand(devModel.getText(), devECID.getText()));
                else executeCommand(devModel.getText(), devECID.getText());
            }
        });
        frame.setSize(300, 150);
    }
    
    public static String getRandECID() {
        Random rand = new Random();
        int n = rand.nextInt(99999999) + 10000000;
        int m = rand.nextInt(99999999) + 10000000;
        return "" + n + m;
    }
    
    public static String getRandModel() {
        Random rand = new Random();
        String[] configList = {"D22AP", "D221AP", "D211AP", "D21AP", "D201AP", "D20AP", 
                "D111AP", "D11AP", "D101AP", "D10AP", "N69AP", "N69uAP", "N66mAP", "N66AP", 
                "N71mAP", "N71AP", "N56AP", "N61AP", "N53AP", "N51AP"};
        return configList[(rand.nextInt(19) + 0)];
    }
    
    public static String executeCommand(String model, String ecid) {
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
        
	if (model.charAt(0) == 'i' || model.charAt(0) == 'I') BorD = "-d ";
	
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(tsscheckerPath + " -l " + BorD + 
                model + " -e " + ecid + " -s --save-path " + savePath);
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
