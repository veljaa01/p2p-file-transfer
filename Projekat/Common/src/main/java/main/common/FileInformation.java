package main.common;

public class FileInformation {
    
    private static int brojacID = 0;

    private final String ipAdresa;
    private final String lokacija;
    private final int id;

    public FileInformation(String ipAdresa, String lokacija, int id) {
        this.ipAdresa = ipAdresa;
        this.lokacija = lokacija;
        this.id = id;
    }

    public String getIpAdresa() {
        return ipAdresa;
    }

    public String getLokacija() {
        return lokacija;
    }

    public int getID() {
        return id;
    }
}