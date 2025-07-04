package main.filetransferserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;




public class Server {
    
    public static AtomicInteger nextId = new AtomicInteger(0); //koristim AtomicInteger da bi svaki id bio 100% jedinstven
    
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    
    private static final int PORT = 12345; //port na kojem server slusa
    
    
    private ConcurrentHashMap<Integer, ClientInfo> fileRegistry;
 
    
    public Server() {
        fileRegistry = new ConcurrentHashMap<>();
    }
    
    public void start() {
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.log(Level.INFO, "Server je pokrenut i slusa se na portu: " + PORT);
            
            ucitajIzFajla();
                        
            nextId = new AtomicInteger(najveciId() + 1); 
            

            while (true) {
                Socket clientSocket = serverSocket.accept();
                
                logger.log(Level.INFO, "Klijent se povezao: " + clientSocket.getInetAddress());
                
                new KlijentNit(clientSocket, fileRegistry).start();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Greska prilikom pokretanja servera: " + e.getMessage(), e);
        }
    }
    
    public static void main(String[] args) {
        
        new Server().start();
    }
    
   
  /*   public static int ucitajNajveciIdIzFajla(String "/Users/veljkogavrilovic/NetBeansProjects/FileTransferServer/fajlovi.txt") {
                
               return 0;
    }*/
                
               
    
    
    private void ucitajIzFajla() throws FileNotFoundException, IOException { //ucitavanje fajlova iz teksta u HashMap
        
        ///Users/veljkogavrilovic/NetBeansProjects/Projekat/FileTransferServer/fajlovi.txt
                
        File file = new File("/Users/veljkogavrilovic/NetBeansProjects/Projekat/FileTransferServer/fajlovi.txt");
        
        System.out.println("Putanja fajla: " + file.getAbsolutePath());
        if (!file.exists()) {
            System.out.println("Fajl ne postoji");
            return;
        }
        
        if (!file.exists()) return; //ako fajl ne postoji nema sta da se ucita
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] podaci = line.split(";");
                if (podaci.length == 5) {
                    
                    String clientIP = podaci[0];
                    String fileName = podaci[1];
                    String sizeWithUnit = podaci[2];
                    String uploadDate = podaci[3];
                    int id = Integer.parseInt(podaci[4]);
                    
                    System.out.println("Ucitani ID iz fajla je: " + id);
                    
                    long fileSize = parseFileSize(sizeWithUnit);
                                        
                    fileRegistry.put(id, new ClientInfo(clientIP, fileName, fileSize, uploadDate, id));
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private long parseFileSize(String sizeWithUnit) {
        
        try {
            
            String [] parts = sizeWithUnit.split(" ");
            if(parts.length != 2) {
                return Long.parseLong(sizeWithUnit);
            }
            double number = Double.parseDouble(parts[0]);
            String unit = parts[1].toUpperCase();
           
            switch (unit) {
                case "KB":
                    return (long) (number * 1024);
                case "MB":
                    return (long) (number * 1024 * 1024);
                case "GB":
                    return (long) (number * 1024 * 1024 * 1024);
                case "TB":
                    return (long) (number * 1024 * 1024 * 1024 * 1024);
                default:
                    return (long) number;
            }
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
            
        } 
    }
    
    public int najveciId () {
        int maxId = 0;
        try(BufferedReader br = new BufferedReader(new FileReader("fajlovi.txt"));) {     
            String linija;
            while ((linija = br.readLine()) != null) {
                String[] delovi = linija.split(";");
                if (delovi.length >= 5) {
                    int id = Integer.parseInt(delovi[4]);
                    if (id > maxId) {
                        maxId = id;
                    }
                }
            }
            System.out.println(maxId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return maxId;
    }
     
    //ucitajIzFajla ako se izbrise, snimace isti fajl iako vec postoji
    
}
