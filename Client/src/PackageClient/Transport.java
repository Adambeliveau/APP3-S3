package PackageClient;

import PackageClient.Liaison;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Transport {
    private String[] args; //arguments de l'execution
    private byte[] fileInBytes; //fichier au complet en byte
    private byte[] packetInBytes = new byte[200];  //packet de 200 bytes
    private List<byte[]> myPackets = new ArrayList<>(); //liste de tous les packets
    private List<byte[]> myPacketsWithHeaders = new ArrayList<>();//liste de tous les packets de 200 + header


    private String fileName;
    private static int cptPacket = 0; //nbre de packets total envoyés
    private int firstPacket;    //premier packet de la sequence
    private int lastPacket;     //dernier packet de la sequence
    private final int tailleMax = 200;
    private int tailleTotale;   //nbre de byte total du fichier

    public Transport(String[] args, byte[] textBytes) {
        this.args = args;
        fileInBytes = textBytes;
        tailleTotale = fileInBytes.length;
        lastPacket = cptPacket+1;
        firstPacket = lastPacket;

    }

    public void run() throws IOException {
        //prend le nom du fichier a partir du path
        fileName = args[1].substring(args[1].lastIndexOf("/") + 1);
        splitBytes();
        createHeader();
        Liaison liaison= new Liaison(args, myPacketsWithHeaders);
        liaison.run();
    }

    private void splitBytes() {
        int posFile = 0;
        int cptByte = 0;

        /*if (fileInBytes.length < tailleMax) {
            packetInBytes = fileInBytes;
        } else {*/
            packetInBytes = new byte[tailleMax];
            boolean lastOne = false;
            while (!lastOne) {
                packetInBytes = Arrays.copyOfRange(fileInBytes,posFile,posFile+200);
                posFile += 200;
                lastPacket++;
                myPackets.add(packetInBytes);
                if (posFile > fileInBytes.length-200){
                    lastOne = true;
                }
            }
            //ca sort de la boucle si le file est fini mais ca a pas save pcq byte<200
            packetInBytes = Arrays.copyOfRange(fileInBytes,posFile,fileInBytes.length);
            myPackets.add(packetInBytes);
            lastPacket++;
        //}
        int i = 0;
        for (byte[] b : myPackets) {
            i++;

        }
    }

    //fok jme souviens pu ce quon avait dit quon allait mettre
    private void createHeader() {

        int cpt = firstPacket;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte [] firstPacketByte;
        byte [] lastPacketByte;
        byte [] tailleTotaleByte;
        byte [] seqNumByte;

        firstPacketByte = ByteBuffer.allocate(5).putInt(firstPacket).array();
        lastPacketByte = ByteBuffer.allocate(5).putInt(lastPacket).array();
        tailleTotaleByte = ByteBuffer.allocate(7).putInt(tailleTotale).array();
        seqNumByte = ByteBuffer.allocate(5).putInt(cpt++).array();

        try {
            out.write(seqNumByte);
            out.write(tailleTotaleByte);
            out.write(firstPacketByte);
            out.write(lastPacketByte);
            out.write(fileName.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte [] firstHeader = out.toByteArray();
        myPacketsWithHeaders.add(firstHeader);
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (byte[] b : myPackets) {
            out = new ByteArrayOutputStream();
            lastPacket++; //on ajoute un packet qui n'etait pas la avant (header + nomFichier)
            seqNumByte = ByteBuffer.allocate(5).putInt(cpt++).array();
            try {
                out.write(seqNumByte);
                out.write(tailleTotaleByte);
                out.write(firstPacketByte);
                out.write(lastPacketByte);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte [] header = out.toByteArray();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                outputStream.write(header);
                outputStream.write(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
            myPacketsWithHeaders.add(outputStream.toByteArray());
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
