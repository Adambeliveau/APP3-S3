package PackageClient;

//import PackageClient.Transport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    public void run() throws IOException {
//        fileText = readFile();

        readFileByte();

        Transport transport = new Transport(args, fileByteContent);
        transport.run();

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

    public void readFileByte(){
        File file = new File(filePath);
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fileByteContent = in.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
