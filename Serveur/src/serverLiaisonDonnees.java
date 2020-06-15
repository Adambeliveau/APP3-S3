import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;

public class serverLiaisonDonnees {
    private static int offsetNoHeader = 13;
    private static int offsetChecksum = 5;
    private static int noOffset = 0;
    private static int getSeqNb = offsetChecksum;
    private static int getChecksum = offsetNoHeader;
    private static CRC32 crc = new CRC32();

    public static boolean firstPacket(DatagramPacket packet, int cpt){
        String data = new String(packet.getData(),noOffset,getSeqNb);
        return cpt != Integer.parseInt(data);
    }

    public static boolean checksum(DatagramPacket packet){
        byte[] receivedChecksum = Arrays.copyOfRange(packet.getData(), offsetChecksum,getChecksum);
        ByteBuffer wrap = ByteBuffer.wrap(receivedChecksum);
        String data = new String(packet.getData(),offsetNoHeader,packet.getLength()-offsetNoHeader);
        return createChecksum(data.getBytes()) == wrap.getLong();

    }
    public static long createChecksum(byte[] bytes){
        crc.reset();
        crc.update(bytes);
        return crc.getValue();
    }
}
