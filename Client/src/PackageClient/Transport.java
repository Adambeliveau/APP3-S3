package PackageClient;

import PackageClient.Liaison;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Transport {
    private String[] args; //arguments de l'execution
    private byte[] fileInBytes; //fichier au complet en byte
    private byte[] packetInBytes;  //packet de 200 bytes
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
        firstPacket = cptPacket;
        lastPacket = firstPacket;
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

        if (fileInBytes.length < tailleMax) {
            packetInBytes = fileInBytes;
        } else {
            packetInBytes = new byte[tailleMax];

            while (posFile < fileInBytes.length) {
                if (cptByte < tailleMax) {
                    packetInBytes[cptByte] = fileInBytes[posFile];
                    posFile++;
                    cptByte++;
                } else {
                    cptByte = 0;
                    myPackets.add(packetInBytes);
                    lastPacket++;
                    packetInBytes = new byte[tailleMax];
                }
            }
            //ca sort de la boucle si le file est fini mais ca a pas save pcq byte<200
            myPackets.add(packetInBytes);
            lastPacket++;
        }
        int i = 0;
        for (byte[] b : myPackets) {
            i++;
            //System.out.println("num packet " + i + " " + new String(b));
        }
    }

    //fok jme souviens pu ce quon avait dit quon allait mettre
    private void createHeader() {

        int cpt = firstPacket;
        String header;
        lastPacket++; //on ajoute un packet qui n'etait pas la avant (header + nomFichier)

        String firstPacketString = numInString(firstPacket);
        String lastPacketString = numInString(lastPacket);
        String tailleTotaleString = numInString(tailleTotale);
        String seqNumString;

        seqNumString = numInString(cpt);

        // modele header = numSeq + taille + seqDebut + seqFin
        String firstHeader = seqNumString + tailleTotaleString + firstPacketString + lastPacketString + fileName;

        myPacketsWithHeaders.add(firstHeader.getBytes());

        for (byte[] b : myPackets) {
            cpt++;
            seqNumString = numInString(cpt);
            header = seqNumString + tailleTotaleString + firstPacketString + lastPacketString;
            String textWithHeader = header + new String(b);
            myPacketsWithHeaders.add(textWithHeader.getBytes());
            //System.out.println("yolo" + cpt + " " + header);
        }
    }

    public String numInString(int num) {
        String myString;
        String myStringDebut;
        if (num < 10) {
            myStringDebut = "0000";
        } else if (num < 100) {
            myStringDebut = "000";
        } else if (num < 1000) {
            myStringDebut = "00";
        } else if (num < 10000) {
            myStringDebut = "0";
        } else {
            myStringDebut = "";
        }
        myString = myStringDebut + num;
        return myString;
    }
}
