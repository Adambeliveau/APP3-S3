import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
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
