/**
 * Device Model
 * Created on 02/02/2020
 * 
 * Jordan Christensen
 * Version #b0.1
 * 
 */

import java.util.Random;
import java.io.Serializable;

public class Device implements Serializable {
    Random rand = new Random();
    
    private String name;
    private String model;
    private String ecid;
    
    String[] configList = {"D22AP", "D221AP", "D211AP", "D21AP", "D201AP", "D20AP", 
                              "D111AP", "D11AP", "D101AP", "D10AP", "N69AP", "N69uAP", 
                              "N66mAP", "N66AP", "N71mAP", "N71AP", "N56AP", "N61AP", 
                              "N53AP", "N51AP"};
    
    public Device(){
        name = "Untitled iPhone";
        ecid = randomECID();
        int modelAndBase = (rand.nextInt(19) + 0);
        model = configList[modelAndBase];
    }
    
    public Device(String deviceModel, String deviceECID){
        name = "Untitled iPhone";
        ecid = deviceECID;
        model = deviceModel;
    }
    
    public Device(String deviceName, String deviceModel, String deviceECID) {
        name = deviceName;
        model = deviceModel;
        ecid = deviceECID;
    }
    
    public String randomECID() {
        String temp = "";
        int n = rand.nextInt(90000000) + 10000000;
        int m = rand.nextInt(90000000) + 10000000;
        temp = "" + n + m;
        return temp;
    }
    
    public String getName() { return this.name; }
    
    public String getECID() { return this.ecid; }
    
    public String getModel() { return this.model; }
    
    public void setName(String name) { this.name = name; }
    
    public void setECID(String ecid) { this.ecid = ecid; }
    
    public void setModel(String model) { this.model = model; }
    
    public String toString() {
        return "Device Name: " + this.name + "\nECID: " + this.ecid + 
               "\nDevice Model: " + this.model;
    }
}
