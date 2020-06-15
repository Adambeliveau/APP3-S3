import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

public class serverApplication {

    private static File out = null;
    private static List<byte[]> packetbytes = new ArrayList<byte[]>();
    private static int offsetNoHeader = 13;
    private static int offsetChecksum = 5;
    private static int noOffset = 0;
    private static int getSeqNb = offsetChecksum;
    private static int getChecksum = offsetNoHeader;

    public static int reconstructData(DatagramPacket packet, int cpt) {
        try{
            if(!serverLiaisonDonnees.checksum(packet))
                throw new IOException("mama mia");
            return getThoseBytesBackTogether(packet, cpt);

        }
        catch (IOException e){
            System.err.println(e);
        }
        return cpt;
    }

    public static void openFile(DatagramPacket packet){
        String data = new String(packet.getData(),offsetNoHeader,packet.getLength());
        out = new File(data);
    }

    public static int getThoseBytesBackTogether(DatagramPacket packet, int cpt){
        packetbytes.add(packet.getData());
        return ++cpt;
    }

//    public long bytesToLong(byte[] bytes) {
//        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
//        buffer.put(bytes);
//        buffer.flip();//need flip
//        return buffer.getLong();
//    }
}
