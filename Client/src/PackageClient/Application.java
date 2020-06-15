package PackageClient;

//import PackageClient.Transport;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Application {
    String[] args;
    byte[] fileByteContent;
    String filePath;
    String fileText;

    public Application(String[] args) {
        this.args = args;
        filePath = args[1];
    }

    public void run() {
        fileText = readFile();

        fileByteContent = fileText.getBytes();

        Transport transport = new Transport(args, fileByteContent);
        transport.run();

        // System.out.println("BYTE TXT: "+ (new String(fileByteContent)));
        // System.out.println("TEXTE: "+ fileText);
    }

    private String readFile() {
        String fileString = "";

        try {
            File myFile = new File(filePath);
            Scanner myReader = new Scanner(myFile);

            while (myReader.hasNextLine()) {
                fileString += myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Yolo ton fichier yest pas la");
            e.printStackTrace();
        }
        return fileString;
    }
}
