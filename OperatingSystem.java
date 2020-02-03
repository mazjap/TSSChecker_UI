
/**
 * Enumeration OperatingSystem
 * Created on 02/02/2020
 * 
 * Jordan Christensen
 * Version #b0.1
 * 
 */

public enum OperatingSystem
{
    mac("macos"), win("windows.exe"), lin("linux");
    private final String osName;
    
    OperatingSystem(final String text) {
        this.osName = text;
    }
    
    @Override
    public String toString() {
        return osName;
    }
}
