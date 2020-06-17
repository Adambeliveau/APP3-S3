import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Arrays;


public class serverLiaisonDonnees {

    private static DatagramPacket packet;
    private static serverTransport st = new serverTransport();
    private static File log = new File("Event.log");
    private static FileWriter file;
    static {
        try {
            file = new FileWriter(log,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Gets the File object used for the Log file
     */
    public static FileWriter getFile() {
        return file;
    }

    /**
     * Ask the sender to resend a faulty packet
     * @param seq Sequence number of the faulty packet
     */
    public static void reSend(int seq) {
       byte[] seqNb = ByteBuffer.allocate(5).putInt(seq-1).array();
        try {
            serverThread.getSocket().send(new DatagramPacket(seqNb, seqNb.length,
                   packet.getAddress(),packet.getPort()));
            putInlog("resend Packet");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Send a confirmation packet to the sender
     */
    public static void sendConfirmation() {
        byte[] seqNb = Arrays.copyOfRange(serverThread.getPacket().getData(),8,13);
        try {
            serverThread.getSocket().send(new DatagramPacket(seqNb, seqNb.length,
                    packet.getAddress(),packet.getPort()));
            putInlog("send Confirmation");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialised the class Datagram packet for later use and starts the verifying process of the packet
     * @param p Current Datagram packet
     */
    public static void setPacket(DatagramPacket p) {
        packet = p;
        putInlog("Packet received");
        st.verifyPacket();
    }

    /**
     * Print each event in a log
     * @param desc Description of the Event the file is going to log
     */
    public static void putInlog(String desc){
        try {
            file = new FileWriter(log,true);
            String data;
            if(desc.equals("send Confirmation") || desc.equals("resend Packet")){
                    byte[] seqNb = Arrays.copyOfRange(serverThread.getPacket().getData(),8,13);
                    data = new String(seqNb);
            }
            else{
                data = new String(packet.getData(), 30, packet.getLength() - 30);
            }
            String finalLog = String.format("%s\t\t%s\t\t\t%s\n", new Timestamp(System.currentTimeMillis()),desc,data);
            file.write(finalLog);
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Gets the Datagram Packet
     */
    public static DatagramPacket getPacket(){
        return packet;
    }


    /**
     * @return Gets the state of IsLastPacketArrived (if the last packet was received)
     */
    public static boolean lastPacketArrived() {
        return st.getIsLastPacket();
    }

    /**
     * @return Gets the last packet sequence number
     */
    public static int getLastSeq() {
        return st.getLastSeq();
    }

    /**
     * @return Gets the state of TError (if there's any transmission errors)
     */
    public static boolean getTError() {
        return serverTransport.getTError();
    }

    public static boolean getError(){
        return serverTransport.getError();
    }
}
