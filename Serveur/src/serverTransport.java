import java.io.ByteArrayOutputStream;
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
    private static  int nbErreur = 0;
    private static  boolean TError = false;


    public static boolean checksum(DatagramPacket packet){
        byte[] receivedChecksum = Arrays.copyOfRange(packet.getData(), 0,8);
        ByteBuffer wrap = ByteBuffer.wrap(receivedChecksum);
        ByteBuffer wrap1 = ByteBuffer.wrap(receivedChecksum);
        System.out.println(wrap1.getLong());
        System.out.println(packet.getLength()-8);
        byte[] data = Arrays.copyOfRange(packet.getData(),8,packet.getLength());
        return createChecksum(data) == wrap.getLong();

    }
    public static long createChecksum(byte[] bytes){
        crc.reset();
        crc.update(bytes);
        System.out.println(new String(bytes));
        System.out.println(crc.getValue());
        return crc.getValue();
    }

    public static List<byte[]> getPacketArray() {
        return packetArray;
    }

    public static boolean getTError() {
        return TError;
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
            ByteBuffer wrapnext = ByteBuffer.wrap(Arrays.copyOfRange(packetArray.get(i+1), 8, 13));
            ByteBuffer wrapcurrent = ByteBuffer.wrap(Arrays.copyOfRange(packetArray.get(i+1), 8, 13));
            int nextPlace = wrapnext.getInt();
            int currentPlace = wrapcurrent.getInt();
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
                System.out.println(new String(packet.getData()));
                throw new IOException("Invalid Checksum Corrupted File");
            }
        }
        catch (IOException e){
            try{
                if(++nbErreur>2){
                    TError = true;
                    throw new IOException("Transmission Error exception");
                }
            }catch (IOException ioe){
                System.err.println(ioe);
            }
            byte[] seq = Arrays.copyOfRange(packet.getData(),8,13);
            ByteBuffer wrap = ByteBuffer.wrap(seq);
            serverLiaisonDonnees.reSend(wrap.getInt());
            System.err.println(e);
        }
        byte[] firstSeq = Arrays.copyOfRange(packet.getData(), 20, 25);
        ByteBuffer wrap1 = ByteBuffer.wrap(firstSeq);
        firstSeqInt = wrap1.getInt();
        byte[] lastSeq = Arrays.copyOfRange(packet.getData(), 25, 30);
        ByteBuffer wrap2 = ByteBuffer.wrap(lastSeq);
        lastSeqInt = wrap2.getInt();
        byte[] currentSeq = Arrays.copyOfRange(packet.getData(), 8, 13);
        ByteBuffer wrap3 = ByteBuffer.wrap(currentSeq);
        int currentSeqInt = wrap3.getInt();

        if(currentSeqInt == firstSeqInt){
            IsLastPacket = false;
            serverApplication.openFile(packet.getData());
        }
        else if(currentSeqInt == lastSeqInt){
            //tell the thread it's finished
            try{
                if(!checkForMissingPacket()){
                    throw new IOException("Missing packet");
                }
            }
            catch (IOException e){
                try{
                    if(++nbErreur>2){
                        TError = true;
                        throw new IOException("Transmission Error exception");
                    }
                }catch (IOException ioe){
                    System.err.println(ioe);
                }
                System.err.println(e);
            }
            IsLastPacket = true;
            nbTotalPacket = 0;
            packetArray.add(packet.getData());
            serverApplication.storeFile();
        }
        else{
            packetArray.add(packet.getData());
        }

    }

    public boolean getIsLastPacket() {
        return IsLastPacket;
    }

    public int getLastSeq() {
        return lastSeqInt;
    }
}
