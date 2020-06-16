import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class serverApplication {

    private static File out = null;
    public static void openFile(byte[] dataWithHeader){
        String data = new String(dataWithHeader,30,serverThread.getPacket().getLength()-30);
        out = new File(data);
    }

    public static void storeFile(){
        try {
            FileOutputStream Fout = new FileOutputStream(out);
            for (byte[] dataAndHeader: serverTransport.getPacketArray()){
                byte[] data = Arrays.copyOfRange(dataAndHeader,30,dataAndHeader.length);
                Fout.write(data);
            }
            Fout.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        serverTransport.getPacketArray().clear();

    }



}
