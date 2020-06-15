import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class serverTransport {



    public static void sendCpt(DatagramPacket packet, DatagramSocket socket, int cpt){
        InetAddress adress = packet.getAddress();
        int port = packet.getPort();
        DatagramPacket cptPacket = new DatagramPacket(String.valueOf(cpt).getBytes(),String.valueOf(cpt).getBytes().length, adress, port);
        try {
            socket.send(cptPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
