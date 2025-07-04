package main.filetransferclient;

import com.aliyun.pds.client.models.FileInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import main.common.FileInformation;

public class Klijent {

    private static final String SERVER_ADDRESS = "127.0.0.1"; // Lokacija servera
    private static final int SERVER_PORT = 12345;

    //odraditi prijavu, pretragu, uspesno preuzeti fajl i onda mogu da predjem na GUI. Trenutno imam prijavu fajla na kojoj radim. 
    public void start() throws IOException {  
        
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT); BufferedReader odServeraKaKlijentu = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter odKlijentaKaServeru = new PrintWriter(socket.getOutputStream(), true); BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Povezan na server.");
            
            
            //OVO JE SVE ZA KONOZLU
            
            Klijent klijent = new Klijent();
        
            String input;
            while (true) {
                System.out.println("Unesite zahtev (prijava/pretraga/pregled/izlaz): ");
                input = userInput.readLine();
                if ("izlaz".equalsIgnoreCase(input)) {
                    break;
                }
                if ("prijava".equalsIgnoreCase(input)) {
                    System.out.println("Unesite putanju do fajla (ili 'kraj' za zavrsetak): ");

                    List<String> prijavljeniFajlovi = new ArrayList<>();
                    String fajl;

                    while (!(fajl = userInput.readLine()).equalsIgnoreCase("kraj")) {
                        File file = new File(fajl);

                        if (!file.exists() || !file.isFile()) {

                            System.out.println("Fajl ne postoji ili nije validan. Pokusajte ponovo.");

                            continue;
                        }

                        odKlijentaKaServeru.println("prijava " + fajl); // Pošaljite zahtev serveru
                        String odgovor = odServeraKaKlijentu.readLine(); // Pročitajte odgovor servera

                        System.out.println("Odgovor servera: " + odgovor);

                        if (odgovor == null) {
                            System.out.println("Greska, server nije poslao odgovor.");
                        } else if (odgovor.contains("Fajl je uspesno prijavljen ")) {
                            prijavljeniFajlovi.add(fajl);
                        }

                    }

                    System.out.println("Završili ste sa prijavom. Prijavljeni fajlovi: " + prijavljeniFajlovi);

                } else if ("pretraga".equalsIgnoreCase(input)) {

                    odKlijentaKaServeru.println("pretraga");
                    System.out.println("Unesite ime fajla za pretragu");
                    String fileName = userInput.readLine();

                    odKlijentaKaServeru.println(fileName);
                    String odgovor;

                    while (!(odgovor = odServeraKaKlijentu.readLine()).isEmpty()) {
                        System.out.println(odgovor);
                    }

                } else if ("pregled".equalsIgnoreCase(input)) {
                    odKlijentaKaServeru.println("pregled");

                    String odgovor;
                    while (!(odgovor = odServeraKaKlijentu.readLine()).isEmpty()) {
                        System.out.println(odgovor);
                    }
                } else {

                    odKlijentaKaServeru.println(input);

                    String odgovor = odServeraKaKlijentu.readLine();
                    System.out.println("Odgovor servera: " + odgovor);

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Klijent().start();
    }

    //---------------------------------------------
    
    

    
    

 
}
