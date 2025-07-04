package main.filetransferserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KlijentNit extends Thread {

    private static final Logger logger = Logger.getLogger(KlijentNit.class.getName());

    private Socket clientSocket;
    private ConcurrentHashMap<Integer, ClientInfo> fileRegistry;
    private int nextId = 0;

    public KlijentNit(Socket clientSocket, ConcurrentHashMap<Integer, ClientInfo> fileRegistry) {
        this.clientSocket = clientSocket;
        this.fileRegistry = fileRegistry;
        

    }

    @Override
    public void run() {
        

        try (BufferedReader odKlijentaKaServeru = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); PrintWriter odServeraKaKlijentu = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String fileInfo = odKlijentaKaServeru.readLine(); //cita podatke o fajlu
            
            if (fileInfo == null) {
                return;
            } 

            System.out.println("Recieved file info: " + fileInfo); //proveravam da li se podaci pravilno citaju
            
            
            
            // ovo saljem na klijentGUI, da bi znao sa kojim racunarom se povezuje
            if (fileInfo != null && fileInfo.startsWith("download")) { //sa slucaj kada se pritissne download

                String trazeniId = fileInfo.split(";")[1];

                System.out.println("Trazeni ID: " + trazeniId);

                int trazeniIdd = Integer.parseInt(fileInfo.split(";")[1]);

                ClientInfo info = fileRegistry.get(trazeniIdd);

                if (info != null) {
                    String ip = info.getClientIP();
                    String lokacija = info.getFilePath();
                    int id = info.getId(); //OVDE JE GRESKA!!!!!!!!

                    String odgovor = lokacija + ";" + ip + ";" + id;
                    odServeraKaKlijentu.println(odgovor);
                } else {
                    // ako nije pronadjeno
                    System.out.println("Nije pronadjen fajl za ID: " + trazeniId);
                    odServeraKaKlijentu.println("");
                }
            }

            String[] podaci = fileInfo.split(";"); //ovde podatke dodajem u FileRegistry

            System.out.println("fileInfo: " + fileInfo);
            System.out.println("Broj delova: " + podaci.length);

            if (podaci.length == 4) { //Za slucaj kada se dodaje fajl u server

                String clientIP = clientSocket.getInetAddress().toString();
                String fileLocation = podaci[1];

                File f = new File(fileLocation);
                
                long fileSize = f.exists() ? f.length() : 0; //Velicina fajla u bajtovima
                String uploadDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); //trenutni datum

                int id = Server.nextId.getAndIncrement();
             
                odServeraKaKlijentu.println(id);
                
                System.out.println("ovo je id: " + id);

                fileRegistry.put(id, new ClientInfo(clientIP, fileLocation, fileSize, uploadDate, id)); //automatski da dodajem fajlove u HashMap kada imam vec pokrenut program i dodam neki fajl
                
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println(id);
                
           //     String fileInfoZaUpis = clientIP + ";" + fileLo

                saveFileInfoToNotes(fileInfo + ";" + id);
                odServeraKaKlijentu.println(id);

            }

            //snimam podatke u fajl

            //------------ovo je za konzolu------------
            String zahtev = odKlijentaKaServeru.readLine();
            
            if (zahtev != null && (zahtev.startsWith("prijava") || zahtev.equalsIgnoreCase("pretraga") || zahtev.equalsIgnoreCase ("pregled"))) {
         
            while ((zahtev = odKlijentaKaServeru.readLine()) != null) {
                logger.log(Level.INFO, "Primljen zahtev: " + zahtev);

                //System.out.println("Trenutni fajlovi: " +fileRegistry);
                if (zahtev.startsWith("prijava")) {

                    if (zahtev.length() > 8) {
                        String fileName = zahtev.substring(8).trim(); //uzimam putanju
                        String clientIP = clientSocket.getInetAddress().getHostAddress();

                        String fileLocation = podaci[1];

                        File f = new File(fileLocation);

                        long fileSize = f.exists() ? f.length() : 0; //Velicina fajla u bajtovima

                        String uploadDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); //trenutni datum

                        if (fileRegistry.containsKey(fileName)) {
                            odServeraKaKlijentu.println("Fajl je vec prijavljen: " + fileName);
                        } else {

                            //      fileRegistry.computeIfAbsent(fileName, k -> new ArrayList<>()).add(new ClientInfo(clientIP, fileName, fileSize, uploadDate, id));
                            sacuvajUFajl(fileName, clientIP, fileSize, uploadDate); //sacuvaj podatke u fajl
                            odServeraKaKlijentu.println("Fajl je uspesno prijavljen: " + fileName); //posalji odgovor klijentu
                        }

                    } else {
                        odServeraKaKlijentu.println("Nevalidan zahtev. Nedostaje putanja fajla.");
                    }
                    //-------------------

                } else if ("pretraga".equalsIgnoreCase(zahtev)) {

                    odServeraKaKlijentu.println("Unesite ime fajla za pretragu.");

                    int trazeniId = Integer.parseInt(fileInfo.split(";")[1]);

                    ClientInfo clients = fileRegistry.get(trazeniId);

                    if (clients != null) {
                        odServeraKaKlijentu.println("Pronadjeni klijenti za fajl: " + trazeniId);
                       

                    } else {

                        odServeraKaKlijentu.println("Fajl nije pronadjen. ");

                    }

                } else if ("pregled".equalsIgnoreCase(zahtev)) {

                    if (fileRegistry.isEmpty()) {
                        odServeraKaKlijentu.println("Nema prijavljenih fajlova.");
                    } else {
                        odServeraKaKlijentu.println("Prijavljeni fajlovi:");
                        for (Integer fileName : fileRegistry.keySet()) {
                            odServeraKaKlijentu.println(fileName);
                        }
                    }

                } else {
                    odServeraKaKlijentu.println("Nepoznat zahtev. ");
                }

            } } else {
                odServeraKaKlijentu.println("Nepoznat zahtev");
                return;
            }
        } catch (IOException e) {

            logger.log(Level.SEVERE, "Greska pri komunikaciji sa klijentom " + e.getMessage(), e);

        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {

                logger.log(Level.WARNING, "Greska pri zatvaranju soketa: " + e.getMessage(), e);

            }
        } 
    }

    private void sacuvajUFajl(String fileName, String clientIP, long fileSize, String uploadDate) { //za konzolu
        try (PrintWriter pw = new PrintWriter(new FileWriter("fajlovi.txt", true))) {
            pw.println(clientIP + ";" + fileName + ";" + fileSize + ";" + uploadDate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFileInfoToNotes(String fileInfo) { //za gui
        try (PrintWriter pw = new PrintWriter(new FileWriter("fajlovi.txt", true))) {
            pw.println(fileInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
