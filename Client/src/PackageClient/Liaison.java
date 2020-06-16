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
        for (byte[] b : packetsList) {
            checkSum = createChecksum(b);
            content = new String(b);
            byte[] checksumArr = ByteBuffer.allocate(8).putLong(checkSum).array();
            addCRC(b, checksumArr);
            sendPackets(b);
        }
    }

    private void addCRC(byte[] packetNoCRC, byte[] checkSumArr) {
        int taille = packetNoCRC.length + checkSumArr.length;
        byte[] packetCRC = new byte[taille];
        int pos = 0;

        for (byte b : checkSumArr) {
            packetCRC[pos] = b;
            pos++;
        }
        for (byte b : packetNoCRC) {
            packetCRC[pos] = b;
            pos++;
        }
        packetsListCRC.add(packetCRC);

    }

    private void sendPackets(byte[] packetCRC) throws IOException {
        socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(packetCRC, packetCRC.length, address, 32367);
        socket.send(packet);
    }

    public static long createChecksum(byte[] bytes) {
        crc.reset();
        crc.update(bytes);
        return crc.getValue();
    }

}
