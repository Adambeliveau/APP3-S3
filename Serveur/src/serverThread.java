import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class serverThread extends Thread {


    private static DatagramSocket socket = null;
    private static DatagramPacket packet;
    private boolean cptsent = false;
    private static int cpt = 200;
    private boolean quit = false;

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
        this("serverApplication");
    }

    public serverThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);
    }

    public void run() {
        while (!quit) {
            byte[] buf = new byte[256];


            packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!cptsent) {
                serverLiaisonDonnees.sendCpt();
                cptsent = true;
            }
            if (false)
                quit = true;

            serverTransport.reconstructData();
            if(serverTransport.getIsLastpacket()){
                if(!serverTransport.checkForMissingPacket()){
                    serverLiaisonDonnees.reSend();
                }
                serverTransport.setNbTotalPacket(0);
                serverTransport.setSommeVerif(0);
                cpt += serverTransport.getNbTotalPacket();
                serverApplication.storeFile();
            }

        }

        socket.close();
    }


}

