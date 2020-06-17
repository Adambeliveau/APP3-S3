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
    private static int nbErreur = 0;
    private static boolean TError = false;
    private static boolean Error = false;


    /**
     * Receive the packet Checksum and verifies it
     * @param packet Datagram packet you want to verify his integrity
     * @return if the packet was corrupted
     */
    public static boolean checksum(DatagramPacket packet){
        byte[] receivedChecksum = Arrays.copyOfRange(packet.getData(), 0,8);
        ByteBuffer wrap = ByteBuffer.wrap(receivedChecksum);
        ByteBuffer wrap1 = ByteBuffer.wrap(receivedChecksum);
        byte[] data = Arrays.copyOfRange(packet.getData(),8,packet.getLength());
        return createChecksum(data) == wrap.getLong();
    }

    /**
     * Does the Checksum Algorithm to the packet data
     * @param bytes Data bytes subject to corruption
     * @return The value of the Checksum ran on the data
     */
    public static long createChecksum(byte[] bytes){
        crc.reset();
        crc.update(bytes);
        return crc.getValue();
    }

    /**
     * @return Gets the array filled with the different packet
     */
    public static List<byte[]> getPacketArray() {
        return packetArray;
    }

    /**
     * @return Gets The state of TError(id there's transmission errors)
     */
    public static boolean getTError() {
        return TError;
    }

    /**
     * Verifies if there's any missing packet
     * @return return a boolean (true if there's no missing packet and false if there is)
     */
    public boolean checkForMissingPacket(){

        if(lastSeqInt - firstSeqInt + 1 == nbTotalPacket)
            return true;
        else{
            findMissingPacket();
            return false;
        }
    }

    /**
     * Searches for the missing packet if any
     */
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

    /**
     * Calls the different verifying functions and adds the packet to an array
     */
    public void verifyPacket() {
        Error = false;
        DatagramPacket packet = serverLiaisonDonnees.getPacket();
        nbTotalPacket++;
        try{
            if(!checksum(packet)){
                Error = true;
                throw new IOException("Invalid Checksum Corrupted File");
            }
        }
        catch (IOException e){
            System.err.println(e);
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
            try{
                if(!checkForMissingPacket()){
                    Error = true;
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

    /**
     * @return Gets the state of IsLastPacket (if the current packet is the last)
     */
    public boolean getIsLastPacket() {
        return IsLastPacket;
    }

    /**
     * @return Gets the sequence number of the last packet
     */
    public int getLastSeq() {
        return lastSeqInt;
    }

    public static boolean getError(){
        return Error;
    }
}
