
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class QuoteClient {
    private static CRC32 crc = new CRC32();

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Usage: java QuoteClient <hostname>");
            return;
        }

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();


        // send request
        String s = "mangemoilesfesses.xml";
        long checkSum = createChecksum(s.getBytes());
        byte[] checksumArr = ByteBuffer.allocate(8).putLong(checkSum).array();
        byte[] seqNumber1 = new byte[5];
        String seqNb1 = "00200000000020000202";
        seqNumber1 = seqNb1.getBytes();
        byte[] seqNumber2 = new byte[5];
        String seqNb2 = "00201000000020000202";
        seqNumber2 = seqNb2.getBytes();
        byte[] seqNumber3 = new byte[5];
        String seqNb3 = "00202000000020000202";
        seqNumber3 = seqNb3.getBytes();
        byte[] buf = s.getBytes();
        byte[] finalpacket1 = new byte[checksumArr.length + seqNumber1.length + buf.length];
        byte[] finalpacket2 = new byte[checksumArr.length + seqNumber1.length + buf.length];
        byte[] finalpacket3 = new byte[checksumArr.length + seqNumber1.length + buf.length];
        List<byte[]> done = new ArrayList<byte[]>();


        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        out1.write(checksumArr);
        out1.write(seqNumber1);

        out1.write(buf);
        finalpacket1 = out1.toByteArray();
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        out2.write(checksumArr);
        out2.write(seqNumber2);
        out2.write(buf);
        finalpacket2 = out2.toByteArray();
        ByteArrayOutputStream out3 = new ByteArrayOutputStream();
        out3.write(checksumArr);
        out3.write(seqNumber3);
        out3.write(buf);
        finalpacket3 = out3.toByteArray();
        done.add(finalpacket1);
        done.add(finalpacket2);
        done.add(finalpacket3);

        InetAddress address = InetAddress.getByName(args[0]);

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);

        for (byte[] finalpacket : done) {
            packet = new DatagramPacket(finalpacket, finalpacket.length, address, 4445);
            socket.send(packet);
        }


        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Quote of the Moment: " + received);

        socket.close();
    }

    public static long createChecksum(byte[] bytes) {
        crc.reset();
        crc.update(bytes);
        return crc.getValue();
    }
}

