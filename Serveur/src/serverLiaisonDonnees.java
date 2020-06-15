import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;



public class serverLiaisonDonnees {


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

    public static void reSend() {
        serverApplication.putByteArrayInOrder();
        int cpt = 0;
        for (byte[] data : serverApplication.getFinalData()) {
            if (data == null) {
                int seqNbint = cpt + serverThread.getCpt();
                byte[] seqNb = ByteBuffer.allocate(5).putInt(seqNbint).array();
                DatagramPacket packet = new DatagramPacket(seqNb,seqNb.length,serverThread.getPacket().getAddress(),serverThread.getPacket().getPort());
                try {
                    serverThread.getSocket().send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cpt++;
        }
    }
}
