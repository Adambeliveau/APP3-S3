import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Arrays;


public class serverLiaisonDonnees {

    private static DatagramPacket packet;
    private static serverTransport st = new serverTransport();

    public static void sendCpt() {
        InetAddress adress = serverThread.getPacket().getAddress();
        int port = serverThread.getPacket().getPort();
        DatagramPacket cptPacket = new DatagramPacket(String.valueOf(serverThread.getCpt()).getBytes(), String.valueOf(serverThread.getCpt()).getBytes().length, adress, port);
        try {
            serverThread.getSocket().send(cptPacket);
            putInlog("send Cpt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reSend(int seq) {
       byte[] seqNb = String.valueOf(seq).getBytes();
        try {
            serverThread.getSocket().send(new DatagramPacket(seqNb, seqNb.length,
                    serverThread.getPacket().getAddress(),serverThread.getPacket().getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendConfirmation() {
        byte[] seqNb = Arrays.copyOfRange(serverThread.getPacket().getData(),0,5);
        try {
            serverThread.getSocket().send(new DatagramPacket(seqNb, seqNb.length,
                    serverThread.getPacket().getAddress(),serverThread.getPacket().getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setPacket(DatagramPacket p) {
        packet = p;
        st.verifyPacket();
    }

    public static void putInlog(String desc){
        File log = new File("Event.log");
        try {
            FileWriter file = new FileWriter(log);
            String data;
            if(desc.equals("send Cpt") || desc.equals("send Confirmation") || desc.equals("resend Packet")){
                if(desc.equals("send Cpt")){
                    data = String.valueOf(serverThread.getCpt());
                }
                else{
                    byte[] seqNb = Arrays.copyOfRange(serverThread.getPacket().getData(),0,5);
                    data = new String(seqNb);
                }
            }
            else{
                data = new String(packet.getData(), 28, packet.getLength() - 28);
            }
            String finalLog = String.format("%s\t\t%s\t%s", new Timestamp(System.currentTimeMillis()),desc,data);
            file.write(finalLog);
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static DatagramPacket getPacket(){
        return packet;
    }


    public static boolean lastPacketArrived() {
        return st.getIsLastPacket();
    }

    public static int getLastSeq() {
        return st.getLastSeq();
    }
}
