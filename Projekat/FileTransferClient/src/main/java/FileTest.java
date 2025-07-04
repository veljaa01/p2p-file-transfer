import java.io.File;

public class FileTest {
    public static void main(String[] args) {
        String path = "/Users/veljkogavrilovic/Desktop/untitled folder/Tekst12.txt";  
        File file = new File(path);
        
        if (file.exists()) {
            System.out.println("Fajl postoji!");
        } else {
            System.out.println("Fajl ne postoji. Proveri putanju.");
        }
    }
    
    
    
    
    //  
/* 
/Users/veljkogavrilovic/Desktop/untitled folder/Tekst.txt
    
/Users/veljkogavrilovic/Desktop/untitled folder/Tekst2.txt
    
    
*/
}
