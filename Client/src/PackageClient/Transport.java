package PackageClient;

import PackageClient.Liaison;

import java.util.ArrayList;
import java.util.List;

public class Transport {
    private String[] args; //arguments de l'execution
    private byte[] fileInBytes; //fichier au complet en byte
    private byte[] packetInBytes;  //packet de 200 bytes
    private List<byte[]> myPackets = new ArrayList<>(); //liste de tous les packets
    private List<byte[]> myPacketsWithHeaders = new ArrayList<>();//liste de tous les packets de 200 + header
    private String fileName;


    private int cptPacket;
    private int firstPacket;
    private int lastPacket;
    private final int tailleMax = 200;
    private int tailleTotale;

    public Transport(String[] args, byte[] textBytes) {
        this.args = args;
        fileInBytes = textBytes;
        tailleTotale = fileInBytes.length;
        cptPacket = 0;
        firstPacket = 0;
        lastPacket = 0;
    }

    public void run() {
        //prend le nom du fichier a partir du path
        fileName = args[1].substring(args[1].lastIndexOf("/") + 1);
        System.out.println("yololololo " + fileName);
        splitBytes();
        createHeader();
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
                    cptPacket++;
                    packetInBytes = new byte[tailleMax];
                }
            }
            //ca sort de la boucle si le file est fini mais ca a pas save pcq byte<200
            myPackets.add(packetInBytes);
            cptPacket++;
        }
        int i = 0;
        for (byte[] b : myPackets) {
            i++;
            //System.out.println("num packet " + i + " " + new String(b));
        }
    }

    //fok jme souviens pu ce quon avait dit quon allait mettre
    private void createHeader() {
        int cpt = 1;
        // modele header = num paquet - taille fichier complet-nbre packets-nom fichier-...???
        String firstHeader = cpt + "-" + tailleTotale + "-" + cptPacket + "-" + fileName + "-";
        myPacketsWithHeaders.add(firstHeader.getBytes());
        cptPacket++; //on ajoute un packet qui n'etait pas la avant

        String header;
        byte[] lol = firstHeader.getBytes();
        for (byte[] b : myPackets) {
            cpt++;
            header = cpt + "-" + tailleTotale + "-" + cptPacket + "-" + fileName + "-";
            String textWithHeader = header + new String(b);
            myPacketsWithHeaders.add(textWithHeader.getBytes());


        }
        int i = 1;
        for (byte[] bb : myPacketsWithHeaders) {
            System.out.println("Caca" + i + " " + new String(bb));
            i++;
        }
    }


}