import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Checksum;

public class serverApplication extends Thread {

    protected DatagramSocket socket = null;
    private File out = null;
    private int cpt = 1;
    private boolean quit = false;
    private boolean cptsent = false;
    private DatagramPacket packet;
    private Checksum crc = new Checksum() {
        @Override
        public void update(int b) {

        }

        @Override
        public void update(byte[] b, int off, int len) {

        }

        @Override
        public long getValue() {
            return 0;
        }

        @Override
        public void reset() {

        }
    };
    private List<String> packetbytes = new ArrayList<String>();
    private int cptPacketReceived = 0;
    private int nbPacket = 0;

    public serverApplication() throws IOException {
        this("serverApplication");
    }

    public serverApplication(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);
    }

    public void run() {
       while(!quit){
           try{
               byte[] buf = new byte[256];

               // receive request
               packet = new DatagramPacket(buf, buf.length);
               socket.receive(packet);
               if(!cptsent) {
                   sendCpt();
                   cptsent = true;
               }
               if(false)
                   quit = true;

               if(firstpacket())
                   openFile();

               if(!firstpacket()){
                   reconstructData();
               }
           }catch (IOException e){
               e.printStackTrace();
           }
       }
       socket.close();
    }

    private void reconstructData() {
        try{
            if(!checksum())
                throw new IOException("mama mia");
            getThoseBytesBackTogether();
        }
        catch (IOException e){
            System.err.println(e);
        }
    }

    public void openFile(){
        String data = new String(packet.getData(),10,packet.getLength());
        nbPacket = Integer.parseInt(new String(packet.getData(),0,5));
        out = new File(data);
    }

    public void sendCpt(){
        InetAddress adress = packet.getAddress();
        int port = packet.getPort();
        DatagramPacket packet = new DatagramPacket(String.valueOf(cpt).getBytes(),String.valueOf(cpt).getBytes().length, adress, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean firstpacket(){
        String data = new String(packet.getData(),0,5);
        return cpt != Integer.parseInt(data);
    }

    public boolean checksum(){
        String receivedChecksum = new String(packet.getData(),5,5);
        String data = new String(packet.getData(),10,packet.getLength());
        return createChecksum(data.getBytes()) == Long.parseLong(receivedChecksum);

    }
    public long createChecksum(byte[] bytes){
        crc.reset();
        crc.update(bytes);
        return crc.getValue();
    }

    public void getThoseBytesBackTogether(){
        String data = new String(packet.getData(),10,packet.getLength());
        if(nbPacket > ++cptPacketReceived)
            packetbytes.add(data);
        else
            cptPacketReceived = 0;

    }
}
