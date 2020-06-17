import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeoutException;

public class serverThread extends Thread {


    private static DatagramSocket socket = null;
    private static DatagramPacket packet;
    private boolean quit = false;

    /**
     * @return The current Datagram packet
     */
    public static DatagramPacket getPacket() {
        return packet;
    }

    /**
     * @return The current Socket
     */
    public static DatagramSocket getSocket() {
        return socket;
    }

    /**
     * @throws IOException Construction didn't go as planned
     */
    public serverThread() throws IOException {
        this("serverThread");
    }

    /**
     * @param name Name of the thread
     * @throws IOException Construction didn't go as planned
     */
    public serverThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(32367);
        serverLiaisonDonnees.getFile().write("------------------------------------------------------------------------------------------------------------\n");
        serverLiaisonDonnees.getFile().write("TimeStamp\t\t\t\t\tEvent Description\t\tData\n");
        serverLiaisonDonnees.getFile().close();
    }

    /**
     * Starts the thread
     */
    public void run() {
       while(!quit){
           byte[] buf = new byte[230];
                packet = new DatagramPacket(buf, buf.length);
           try {
               if(!serverLiaisonDonnees.lastPacketArrived()){
                   socket.setSoTimeout(500);
               }
               else {
                   socket.setSoTimeout(1000000000);
               }
               socket.receive(packet);
               serverLiaisonDonnees.setPacket(packet);
               if(!serverTransport.getError()){
                   serverLiaisonDonnees.sendConfirmation();
               }
               if(serverLiaisonDonnees.getTError()){
                   quit = true;
               }
           } catch (SocketTimeoutException e) {
               System.err.println("Socket timeout");
               serverLiaisonDonnees.reSend(serverLiaisonDonnees.getLastSeq());
           } catch (IOException e){
               e.printStackTrace();
           }

       }
       socket.close();
        try {
            serverLiaisonDonnees.getFile().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


