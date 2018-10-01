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

public class main {
    static String[] differentOS = {"macos", "windows.exe", "linux"};
    //Change this to be 0, 1, or two depending on your Operating System listed above.
    static String OS = differentOS[0];
    
    //Change this to true if you would like to see the output from TSSChecker or to false if you don't
    static boolean seeOutput = false;
    
    static ArrayList<device> deviceList = new ArrayList<device>();
    
    static JFrame frame = new JFrame("TSS UI");
    static JPanel panel = new JPanel();
    
    static Dimension buttonSize = new Dimension(75, 30);
    
    static int buttons = 0;
    
    public static void main (String[] args) {
        File path = new File("tsschecker_macos");
        String objectPath = path.getAbsolutePath();
        
        if (Files.notExists(Paths.get(objectPath))) {
            try {
                saveUrl("tsschecker_v212_mac_win_linux.zip", "https://github.com/tihmstar/tsschecker/releases/download/v212/tsschecker_v212_mac_win_linux.zip");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                
                extractFile(Paths.get(objectPath.substring(0,objectPath.length()-6) + "_v212_mac_win_linux.zip"),"tsschecker" + "_" + OS, Paths.get(objectPath));
                if (!(OS.equals(differentOS[1]))) executeCommand("chmod 755 " + objectPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        File deleteTssZip = new File("tsschecker_v212_mac_win_linux.zip");
        deleteTssZip.delete();
        
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
            applicationPaneRandom();
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
        String objectPath = file.getAbsolutePath();
        Path path = Paths.get(objectPath);
        if (Files.notExists(path)) {
            file.mkdir();
        }
        
        File[] listOfFiles = file.listFiles();
        deviceList.clear();
        
        for (int i=0; i<listOfFiles.length; i++) {
            try {
                ObjectInputStream is = new ObjectInputStream(new FileInputStream(listOfFiles[i]));
                device temp = (device) is.readObject();
                deviceList.add(temp);
                is.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        
        
        if (deviceList.size() == 0) {
            panel.removeAll(); panel.revalidate(); panel.repaint();
            JLabel selectDevice = new JLabel("You don't have any saved devices", SwingConstants.CENTER); 
            selectDevice.setPreferredSize(new Dimension(500, 20)); panel.add(selectDevice);
        
            frame.setSize(500, 300);
        }
        else {
            panel.removeAll(); panel.revalidate(); panel.repaint();
            JLabel selectDevice = new JLabel("Select a Device", SwingConstants.CENTER); panel.add(selectDevice);
            selectDevice.setPreferredSize(new Dimension(500, 20));
            
            panel.add(new JLabel("\tAmount of Blobs to be saved:  \t", SwingConstants.CENTER));
            JTextField blobsNum = new JTextField(); panel.add(blobsNum);
            blobsNum.setPreferredSize(new Dimension(100, 20));
            
            for (int i=0; i<deviceList.size(); i++) {
                JButton dev = new JButton(deviceList.get(i).getName());
                panel.add(dev);
                int val = i;
                dev.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        try {
                        for (int i=0; i<Integer.parseInt(blobsNum.getText()); i++) {
                                if (seeOutput) System.out.println(executeTSS(deviceList.get(val)));
                                else executeTSS(deviceList.get(val));
                        }
                        } catch (NumberFormatException e) {
                            if (seeOutput) System.out.println(executeTSS(deviceList.get(val)));
                            else executeTSS(deviceList.get(val));
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
            
        frame.setSize(500, 300);
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
        panel.add(new JLabel("\tAmount of Blobs:    \t", SwingConstants.CENTER));
        JTextField devAmount = new JTextField(); panel.add(devAmount); 
        devAmount.setPreferredSize(new Dimension(100, 20));
        
        File file = new File("Objects");
        String objectPath = file.getAbsolutePath();
        Path path = Paths.get(objectPath);
        if (Files.notExists(path)) {
            file.mkdir();
        }
        
        JButton submit = new JButton("Submit"); panel.add(submit);
        submit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    for (int i=0; i<Integer.parseInt(devAmount.getText()); i++) {
                        device temp = new device(devName.getText(), devModel.getText(), devECID.getText());
                    
                        int nameInt = 1;
                    
                        try {
                            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("Objects/" + temp.getName() + ".bin"));
                            os.writeObject(temp);
                            os.close();
                        } catch (FileNotFoundException e){
                            System.out.println("File Error");
                        } catch (IOException e) {
                            System.out.println ("There was an Error");
                        }
                    
                        if (seeOutput) System.out.println(executeTSS(temp));
                        else executeTSS(temp);
                    }
                } catch (NumberFormatException f) {
                    device temp = new device(devName.getText(), devModel.getText(), devECID.getText());
                    try {
                        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("Objects/" + temp.getName() + ".bin"));
                        os.writeObject(temp);
                        os.close();
                    } catch (FileNotFoundException e){
                        System.out.println("File Error");
                    } catch (IOException e) {
                        System.out.println ("There was an Error");
                    }
                    
                    if (seeOutput) System.out.println(executeTSS(temp));
                    else executeTSS(temp);
                }
            }
        });
        
        JButton back = new JButton("Back"); panel.add(back);
        back.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    applicationPaneHome();
                }
            });
        
        frame.setSize(300, 170);
    }
    
    public static void applicationPaneRandom() {
        panel.removeAll(); panel.revalidate(); panel.repaint();
        
        panel.add(new JLabel("Amount of Blobs to Save: ", SwingConstants.CENTER));
        JTextField blobsNum = new JTextField(); panel.add(blobsNum); 
        blobsNum.setPreferredSize(new Dimension(100, 20));
        
        JButton submit = new JButton("Submit"); panel.add(submit);
        submit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    for (int i=0; i<Integer.parseInt(blobsNum.getText()); i++) {
                        device temp = new device();
                        if (seeOutput) System.out.println(executeTSS(temp));
                        else executeTSS(temp);
                    }
                } catch (NumberFormatException e) {
                    device temp = new device();
                    if (seeOutput) System.out.println(executeTSS(temp));
                    else executeTSS(temp);;
                }
            }
        });
        JButton back = new JButton("Back"); panel.add(back);
        back.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    applicationPaneHome();
                }
            });
        
        frame.setSize(350, 100);
    }
    
    public static void saveUrl(final String filename, final String urlString) throws MalformedURLException, IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(urlString).openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }
    
    public static void extractFile(Path zipFile, String fileName, Path outputFile) throws IOException{
        try (FileSystem fileSystem = FileSystems.newFileSystem(zipFile, null)) {
            Path fileToExtract = fileSystem.getPath(fileName);
            Files.copy(fileToExtract, outputFile);
        }
    }
    
    public static String executeTSS(device iPhone) {
        String savePath;
        File file = new File("Blobs");
        savePath = file.getAbsolutePath();
        Path path = Paths.get(savePath);
    
        if (Files.notExists(path)) {
            file.mkdir();
        }
        
    
        String BorD = "-B ";
        String tsscheckerPath = savePath.substring(0, savePath.length()-5) + "tsschecker" + "_" + OS;
        
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
    
    public static String executeCommand(String command) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
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
