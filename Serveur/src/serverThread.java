import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class serverThread extends Thread {

    protected DatagramSocket socket = null;
    private DatagramPacket packet;
    private boolean cptsent = false;
    private int cpt = 1;
    private boolean quit = false;

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
                serverTransport.sendCpt(packet, socket, cpt);
                cptsent = true;
            }
            if (false)
                quit = true;

            if (serverLiaisonDonnees.firstPacket(packet, cpt))
                serverApplication.openFile(packet);

            if (!serverLiaisonDonnees.firstPacket(packet, cpt)) {
                cpt = serverApplication.reconstructData(packet, cpt);
            }
        }

        socket.close();
    }


}

