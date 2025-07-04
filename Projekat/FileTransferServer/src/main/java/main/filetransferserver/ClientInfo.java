package main.filetransferserver;

public class ClientInfo {

    private String clientName; //Ime klijenta (LOKACIJA)
    private String clientIP; //ip adresa klijenta
    private long fileSize; //velicina
    private String uploadDate; 
    private int id;
    
    public ClientInfo (String clientName, String clientIP, long fileSize, String uploadDate, int id1) {
        this.clientName = clientName;
        this.clientIP = clientIP;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
        this.id = id1;
                
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }
    
     public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
       
    @Override
    public String toString() {
        
        return "Ime klijenta: " + clientName + ", IP Adresa: " + clientIP + ", Veličina fajla: " + fileSize + " bajtova, Datum postavljanja: " + uploadDate + " ID: " + id;
          
    }

    String getFilePath() {
        return clientName;
    }
    
}
