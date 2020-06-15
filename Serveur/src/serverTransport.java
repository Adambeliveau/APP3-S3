import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;

public class serverTransport {

    private static CRC32 crc = new CRC32();
    private static List<byte[]> packetArray = new ArrayList<byte[]>();
    private static int nbTotalPacket = 0;
    private int firstSeqInt = 0;
    private int lastSeqInt = 0;
    private boolean IsLastPacket = true;


    public static boolean checksum(DatagramPacket packet){
        byte[] receivedChecksum = Arrays.copyOfRange(packet.getData(), 0,8);
        ByteBuffer wrap = ByteBuffer.wrap(receivedChecksum);
        String data = new String(packet.getData(),28,packet.getLength()-28);
        return createChecksum(data.getBytes()) == wrap.getLong();

    }
    public static long createChecksum(byte[] bytes){
        crc.reset();
        crc.update(bytes);
        return crc.getValue();
    }

    public static List<byte[]> getPacketArray() {
        return packetArray;
    }

    public boolean checkForMissingPacket(){

        if(lastSeqInt - firstSeqInt + 1 == nbTotalPacket)
            return true;
        else{
            findMissingPacket();
            return false;
        }
    }

    public void findMissingPacket(){
        for(int i = 0;i<packetArray.size();i++){
            int nextPlace = Integer.parseInt(new String(Arrays.copyOfRange(packetArray.get(i+1), 8, 13)));
            int currentPlace = Integer.parseInt(new String(Arrays.copyOfRange(packetArray.get(i+1), 8, 13)));
            int retreiver = 1;
            while(currentPlace + retreiver != nextPlace){
                serverLiaisonDonnees.reSend(currentPlace+retreiver);
                retreiver++;
            }
        }
    }

    public void verifyPacket() {

        DatagramPacket packet = serverLiaisonDonnees.getPacket();
        nbTotalPacket++;
        try{
            if(!checksum(packet)){
                throw new IOException("mamamiam (ce que jonathan dirait)");
            }
        }
        catch (IOException e){
            System.err.println(e);
        }
        byte[] firstSeq = Arrays.copyOfRange(packet.getData(), 18, 23);
        firstSeqInt = Integer.parseInt(new String(firstSeq));
        byte[] lastSeq = Arrays.copyOfRange(packet.getData(), 23, 28);
        lastSeqInt = Integer.parseInt(new String(lastSeq));
        byte[] currentSeq = Arrays.copyOfRange(packet.getData(), 8, 13);
        int currentSeqInt = Integer.parseInt(new String(currentSeq));

        if(currentSeqInt == firstSeqInt){
            IsLastPacket = false;
            serverApplication.openFile(packet.getData());
        }
        else if(currentSeqInt == lastSeqInt){
            //tell the thread it's finished
            try{
                if(!checkForMissingPacket()){
                    throw new IOException("mamamiam X2");
                }
            }
            catch (IOException e){
                System.err.println(e);
            }
            IsLastPacket = true;
            nbTotalPacket = 0;
            packetArray.add(packet.getData());
            serverApplication.storeFile();
        }
        else{
            packetArray.add(packet.getData());
            serverLiaisonDonnees.sendConfirmation();
        }

    }

    public boolean getIsLastPacket() {
        return IsLastPacket;
    }

    public int getLastSeq() {
        return lastSeqInt;
    }
}
