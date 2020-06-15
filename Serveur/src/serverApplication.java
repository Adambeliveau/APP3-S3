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
        String data = new String(dataWithHeader,28,serverThread.getPacket().getLength()-28);
        out = new File(data);
    }

    public static void storeFile(){
        try {
            FileOutputStream Fout = new FileOutputStream(out);
            for (byte[] dataAndHeader: serverTransport.getPacketArray()){
                byte[] data = Arrays.copyOfRange(dataAndHeader,28,dataAndHeader.length-28);
                Fout.write(data);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
