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

    private static int offsetNoHeader = 13;
    private static int offsetChecksum = 5;
    private static int noOffset = 0;
    private static int getSeqNb = offsetChecksum;
    private static int getChecksum = offsetNoHeader;

    public static List<byte[]> getFinalData() {
        return finalData;
    }

    private static List<byte[]> finalData = new ArrayList<byte[]>();



    public static void openFile(){
        String data = new String(serverThread.getPacket().getData(),offsetNoHeader,serverThread.getPacket().getLength()-offsetNoHeader);
        out = new File(data);
    }

    public static void putByteArrayInOrder(){
        for(byte[] data: serverLiaisonDonnees.getPacketArray()){
            byte[] seqNb = Arrays.copyOfRange(data,noOffset,getSeqNb);
            int currentSeqNb = Integer.parseInt(new String(seqNb));
            finalData.add(currentSeqNb+1-serverThread.getCpt(),data);
        }
    }

    public static void storeFile(){
        putByteArrayInOrder();
        try {
            FileOutputStream Fout = new FileOutputStream(out);
            for (byte[] dataAndHeader: finalData){
                byte[] data = Arrays.copyOfRange(dataAndHeader,offsetNoHeader,dataAndHeader.length-offsetNoHeader);
                Fout.write(data);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
