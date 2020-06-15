import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;

public class serverTransport {

    private static int offsetNoHeader = 13;
    private static int offsetChecksum = 5;
    private static int noOffset = 0;
    private static int getSeqNb = offsetChecksum;
    private static int getChecksum = offsetNoHeader;
    private static CRC32 crc = new CRC32();
    private static List<byte[]> packetArray = new ArrayList<byte[]>();
    private static boolean isLastpacket = false;
    private static int nbTotalPacket = 0;
    private static int sommeVerif = 0;

    public static List<byte[]> getPacketArray() {
        return packetArray;
    }

    public static void setNbTotalPacket(int nbTotalPacket) {
        serverTransport.nbTotalPacket = nbTotalPacket;
    }

    public static void setSommeVerif(int sommeVerif) {
        serverTransport.sommeVerif = sommeVerif;
    }

    public static boolean getIsLastpacket() {
        return isLastpacket;
    }

    public static int getNbTotalPacket(){
        return nbTotalPacket;
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

    public static void reconstructData() {
        try{
            if(!checksum(serverThread.getPacket()))
                throw new IOException("mama mia");
            storePacket(serverThread.getPacket());
        }
        catch (IOException e){
            System.err.println(e);
        }
    }

    public static void storePacket(DatagramPacket packet){

        byte[] seqNb = Arrays.copyOfRange(packet.getData(),noOffset,getSeqNb);
        int currentPacket = Integer.parseInt(new String(seqNb));
        if(nbTotalPacket == 0){
            serverApplication.openFile();
            nbTotalPacket = currentPacket;
            for (int i = 1 ; i <= nbTotalPacket; i++){
                sommeVerif += i;
            }
            System.out.println(sommeVerif);
        }
        else {
            if(currentPacket + 1 - serverThread.getCpt() == nbTotalPacket){
                isLastpacket = true;
            }
        }

        packetArray.add(packet.getData());
    }

    public static boolean checkForMissingPacket(){

        int receivedPacketSomme = 0;
        for(byte[] data : packetArray.subList(1,packetArray.size())){
            byte[] getNb = Arrays.copyOfRange(data,noOffset,getSeqNb);
            int currentPacket = Integer.parseInt(new String(getNb));
            receivedPacketSomme += currentPacket + 1 - serverThread.getCpt();
        }
        return receivedPacketSomme == sommeVerif;
    }
}
