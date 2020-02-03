/**
 * Controller
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

public class Controller {
    // MARK: - Instance Variables
    private OperatingSystem OS;
    private ArrayList<Device> deviceList;
    
    private File shouldSeeOutputPath;
    private File devicePath;
    
    
    // MARK: - Constructor
    public Controller() {
        deviceList = new ArrayList<Device>();
        OS = getOS();
        shouldSeeOutputPath = new File("Objects/shouldSeeOutput/shouldSeeOutput.bin");
        devicePath = new File("Objects/Devices");
        
        setUpFiles();
        fetchSavedDevices();
    }
    
    // MARK: - Private Methods
    private void fetchSavedDevices() {
        
        String objectPath = devicePath.getAbsolutePath();
        System.out.println(objectPath);
        if (Files.notExists(Paths.get(objectPath))) {
            devicePath.mkdir();
            return;
        }
        
        File[] listOfFiles = devicePath.listFiles();
        deviceList.clear();
        
        for (int i=0; i<listOfFiles.length; i++) {
            try {
                ObjectInputStream is = new ObjectInputStream(new FileInputStream(listOfFiles[i]));
                Device temp = (Device) is.readObject();
                deviceList.add(temp);
                is.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }
    }
    
    private void setUpFiles() {
        String file = new File("file.zip").getAbsolutePath();
        File tsscheckerPath = new File("tsschecker_" + OS);
        File img4toolPath = new File("img4tool");
        String tsscheckerObjectPath = tsscheckerPath.getAbsolutePath();
        String img4toolObjectPath = img4toolPath.getAbsolutePath();
        if (!fileDoesExist(tsscheckerObjectPath, 
        "https://github.com/tihmstar/tsschecker/releases/download/v212/tsschecker_v212_mac_win_linux.zip")) {
            extractedFile(file, "tsschecker_" + OS, tsscheckerObjectPath);
        }
        
        if (!fileDoesExist(img4toolObjectPath, "https://api.tihmstar.net/builds/img4tool/img4tool-latest.zip")) {
            extractedFile(file, "img4tool_" + OS, img4toolObjectPath);
        }
    }
    
    private boolean fileDoesExist(String path, String url) {
        if (Files.notExists(Paths.get(path))) {
            boolean didFail = false;
            try {
                saveUrl("file.zip", url);
            } catch (MalformedURLException e) {
                didFail = true;
                e.printStackTrace();
            } catch (IOException e) {
                didFail = true;
                e.printStackTrace();
            }
            File deleteTssZip = new File("tsschecker_v212_mac_win_linux.zip");
            deleteTssZip.delete();
            
            return didFail;
        }
        return true;
    }
    
    private boolean extractedFile(String fromPath, String fileName, String toPath) {
        try {
                extractFile(Paths.get(fromPath), fileName, Paths.get(toPath));
                if (!(OS.equals(OperatingSystem.win))) executeCommand("chmod 755 " + toPath);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        return true;
    }
    
    private void saveUrl(String filename, String urlString) throws MalformedURLException, IOException {
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
    
    private void extractFile(Path zipFile, String fileName, Path outputFile) throws IOException{
        try (FileSystem fileSystem = FileSystems.newFileSystem(zipFile, null)) {
            Path fileToExtract = fileSystem.getPath(fileName);
            Files.copy(fileToExtract, outputFile);
        }
    }
    
    private String executeCommand(String command) {
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
    
    private String createCommand(Device device) {
        String savePath = new File("").getAbsolutePath();
        Path path = Paths.get(savePath);
        
        String modelType = Character.toLowerCase(device.getModel().charAt(0)) == 'i' ? "-d " : "-B ";
        String tsscheckerPath = savePath + "/tsschecker" + "_" + OS;
        
        return tsscheckerPath + " -l " + modelType + device.getModel() + " -e " + 
        device.getECID() + " -s --save-path " + savePath + "/Blobs";
    }

    // MARK: - Public Methods
    public OperatingSystem getOS() {
        String OS = System.getProperty("os.name");
        if (OS.toLowerCase().contains("mac")) return OperatingSystem.mac;
        
        else if (OS.toLowerCase().contains("windows")) return OperatingSystem.win;
        
        else if (OS.toLowerCase().contains("linux")) return OperatingSystem.lin;
        
        else return OperatingSystem.mac;
    }
    
    public void redownloadFiles() {
        setUpFiles();
    }
    
    public String saveBlobs(Device device, int amount) {
        if (device.getModel().equals("") || device.getECID().equals("") || device.getName().equals("")) {
            System.out.println(
            "Controller.saveBlobs: Unable to save device blobs. At least one of these is empty:\n" +
            (device.getModel().equals("") ? "Device Model\n" : "") + 
            (device.getECID().equals("") ? "Device ECID\n" : "") + 
            (device.getName().equals("") ? "Device Name" : ""));
            return "";
        }
        
        String command = createCommand(device);

        File file = new File("Blobs");
        Path path = Paths.get(file.getAbsolutePath());
        if (Files.notExists(path)) {
            file.mkdir();
        }
        System.out.println(command);
        String returnString = "";
        for (int i=0; i<amount; i++) {
            returnString += executeCommand(command) + "\n";
        }
        return returnString;
    }
    
    public String saveRandomBlobs(int amount) {
        String returnString = "";
        for (int i=0; i<amount; i++) {
            returnString += saveBlobs(new Device(), 1);
        }
        
        return returnString;
    }
    
    public Device[] getSavedDevices() {
        fetchSavedDevices();
        Device[] tempList = new Device[deviceList.size()];
        for (int i=0; i<deviceList.size(); i++) {
            tempList[i] = deviceList.get(i);
        }
        return tempList;
    }
    
    public boolean saveDevice(Device device) {
        if (device.getModel().equals("") || device.getECID().equals("") || device.getName().equals("")) {
            System.out.println(
            "Controller.saveDevice: Unable to save device blobs. At least one of these is empty:\n" +
            (device.getModel().equals("") ? "Device Model\n" : "") + 
            (device.getECID().equals("") ? "Device ECID\n" : "") + 
            (device.getName().equals("") ? "Device Name" : ""));
            return false;
        }
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("Objects/Devices/" + device.getName() + ".bin"));
            os.writeObject(device);
            os.close();
            fetchSavedDevices();
        } catch (FileNotFoundException e){
            System.out.println("File could not be found. Error: ");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            System.out.println("There was an Error: ");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean seeOutput() {
        String objectPath = shouldSeeOutputPath.getAbsolutePath();
        if (Files.notExists(Paths.get(objectPath))) {
            shouldSeeOutputPath.mkdir();
            shouldSeeOutput(false);
            return false;
        }
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(shouldSeeOutputPath));
            boolean temp = (boolean) is.readObject();
            is.close();
            return temp;
        } catch (Exception e) {
            return false;
        }
    }
    
    public void shouldSeeOutput(boolean bool) {
        String objectPath = shouldSeeOutputPath.getAbsolutePath();
        if (Files.notExists(Paths.get(objectPath))) {
            shouldSeeOutputPath.mkdir();
            return;
        }
        
        File[] listOfFiles = shouldSeeOutputPath.listFiles();
        if (listOfFiles.length > 1) {
            for (File file : listOfFiles) {
                file.delete();
            }
        }
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(
            shouldSeeOutputPath.getAbsolutePath()));
            os.writeObject(bool);
            os.close();
        } catch (Exception e) {
            System.out.println("Controller.shouldSeeOutput: Unable to save boolean to persistant store.");
            return;
        }
    }
}
