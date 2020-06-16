package PackageClient;

import javax.xml.crypto.Data;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

public class Liaison {
    private InetAddress address;
    String[] args;
    private static CRC32 crc = new CRC32();
    private List<byte[]> packetsList = new ArrayList<>();
    private List<byte[]> packetsListCRC = new ArrayList<>();
    DatagramSocket socket;
    long checkSum;
    int nbPacket = 0;

    public Liaison(String[] args, List<byte[]> splitBytes) throws IOException {
        this.args = args;
        address = InetAddress.getByName(args[0]);
        packetsList = splitBytes;

        nbPacket = packetsList.size();


    }

    
    public void run() throws IOException {
        String crcString;
        String content;
        Boolean confirmation = true;
        for (byte[] b : packetsList) {
            checkSum = createChecksum(b);
            byte[] checksumArr = ByteBuffer.allocate(8).putLong(checkSum).array();
            addCRC(b, checksumArr);

        }
        int cpt = 1;

        for (byte[] b : packetsListCRC) {

            if (args[2].equals("corrupt") && cpt > 1) {
                corruption(b);
            }

            confirmation = sendPackets(b, cpt);

            while (!confirmation) {
                confirmation = sendPackets(b, cpt);
            }
            cpt++;
        }
    }

    private void addCRC(byte[] packetNoCRC, byte[] checkSumArr) {
        int taille = packetNoCRC.length + checkSumArr.length;
        byte[] packetCRC = new byte[taille];
        int pos = 0;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(checkSumArr);
            out.write(packetNoCRC);
        } catch (IOException e) {
            e.printStackTrace();
        }
        packetCRC = out.toByteArray();
//        for (byte b : checkSumArr) {
//            packetCRC[pos] = b;
//            pos++;
//        }
//        for (byte b : packetNoCRC) {
//            packetCRC[pos] = b;
//            pos++;
//        }
        packetsListCRC.add(packetCRC);

    }

    private Boolean sendPackets(byte[] packetCRC, int cpt) throws IOException {
        Boolean confi = true;
        //send packets
        socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(packetCRC, packetCRC.length, address, 32367);
        String test = new String(packet.getData());
        socket.send(packet);

        //receive confirmation
        byte[] buf = new byte[5];
        DatagramPacket confirmation = new DatagramPacket(buf, buf.length);
        socket.receive(confirmation);
        ByteBuffer wrap = ByteBuffer.wrap(confirmation.getData());
        int confirmationI = wrap.getInt();
        if (cpt == confirmationI) {
            confi = true;
        } else {
            confi = false;
        }
        return confi;
    }

    private static long createChecksum(byte[] bytes) {
        crc.reset();
        crc.update(bytes);
        return crc.getValue();
    }

    private void corruption(byte[] b) {
        //met une virgule a la fin
        b[b.length - 1] = 44;
    }

}
