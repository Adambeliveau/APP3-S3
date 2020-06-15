import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeoutException;

public class serverThread extends Thread {


    private static DatagramSocket socket = null;
    private static DatagramPacket packet;
    private boolean cptsent = false;
    private static int cpt = 1;
    private boolean quit = false;
    private boolean nextPacket = false;

    public static int getCpt() {
        return cpt;
    }

    public static DatagramPacket getPacket() {
        return packet;
    }

    public static DatagramSocket getSocket() {
        return socket;
    }

    public serverThread() throws IOException {
        this("serverThread");
    }

    public serverThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);
        byte[] buf = new byte[256];
        packet = new DatagramPacket(buf, buf.length, InetAddress.getLocalHost(),4445);
    }

    public void run() {
       while(!quit){
           byte[] buf = new byte[256];
           DatagramPacket packet = new DatagramPacket(buf, buf.length);
           try {
               if(!serverLiaisonDonnees.lastPacketArrived()){
                   socket.setSoTimeout(500);
               }
               else {
                   socket.setSoTimeout(1000000000);
               }
               socket.receive(packet);
               if ((!cptsent))
               {
                   serverLiaisonDonnees.sendCpt();
               }
               serverLiaisonDonnees.setPacket(packet);
           } catch (SocketTimeoutException e) {
               System.err.println("ayayaye too late");
               serverLiaisonDonnees.reSend(serverLiaisonDonnees.getLastSeq());
           } catch (IOException e){
               e.printStackTrace();
           }
       }
    }


}


