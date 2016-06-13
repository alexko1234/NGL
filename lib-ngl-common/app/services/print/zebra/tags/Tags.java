package services.print.zebra.tags;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import services.print.PrintServicesException;

public class Tags {
    
    private static void sendCommands(String printer, String commands) throws IOException {

        Socket printerSocket = new Socket();
        
        printer = printer + ".tx.local";
        System.out.println("Connexion \u00e0 " + printer);
        printerSocket.connect(new InetSocketAddress(printer, 9100), 1000);

        System.out.println("Commandes envoy\u00e9es:");
        System.out.print(commands);

        BufferedOutputStream output = new BufferedOutputStream(printerSocket.getOutputStream());
        output.write(commands.getBytes());
        output.write('\n');
        output.close();
        printerSocket.close();
    }
    
    private int getNbColonnes(BufferedReader data) throws IOException {
        String line = data.readLine();
        String[] colonnes = line.split("[\t| ]");
        return colonnes.length;
    }
    
    private StringBuffer uneLigne(BufferedReader data) throws IOException {
        StringBuffer commands = new StringBuffer();
        String line;
        
        while((line = data.readLine()) != null) {
            line.replace("\r", "\n");
            String[] lines = line.split("[\t| ]");
            commands.append("\nq298\n");
            commands.append("N\n");
            commands.append("A60,5,0,3,1,1,N,\"").append(lines[0]).append("\"\n");
            commands.append("P1\n");
        }
        
        return commands;
    }
    
    private StringBuffer deuxLignes(BufferedReader data) throws IOException {
        /**
         * reglages recommandes pour deux lignes : "A30,5,0,4,1,1,N,\"" & "A30,65,0,4,1,1,N,\""
         */
        StringBuffer commands = new StringBuffer();
        String line;
        
        while((line = data.readLine()) != null) {
            line.replace("\r", "\n");
            String[] lines = line.split("[\t| ]");
            commands.append("\nq298\n");
            commands.append("N\n");
            commands.append("A60,5,0,3,1,1,N,\"").append(lines[0]).append("\"\n");
            commands.append("A60,65,0,3,1,1,N,\"").append(lines[1]).append("\"\n");
            commands.append("P1\n");
        }
        
        return commands;
    }
    
    private StringBuffer troisLignes(BufferedReader data) throws IOException {
        /**
         * reglages recommandes pour trois lignes : "A30,5,0,3,1,1,N,\"" & "A30,45,0,3,1,1,N,\"" & "A35,80,0,3,1,1,N,\""
         */
        StringBuffer commands = new StringBuffer();
        String line;
        
        while((line = data.readLine()) != null) {
            line.replace("\r", "\n");
            String[] lines = line.split("[\t| ]");
            commands.append("\nq298\n");
            commands.append("N\n");
            commands.append("A60,5,0,3,1,1,N,\"").append(lines[0]).append("\"\n");
            commands.append("A60,45,0,3,1,1,N,\"").append(lines[1]).append("\"\n");
            commands.append("A60,85,0,3,1,1,N,\"").append(lines[2]).append("\"\n");
            commands.append("P1\n");
        }
        
        return commands;
    }
    
    
    private Tags(String printer, String file) {
        try {
            BufferedReader data = new BufferedReader(new FileReader(file));
            
            int nbColonnes = getNbColonnes(data);
            data = new BufferedReader(new FileReader(file));
            
            StringBuffer commands = null;
            switch(nbColonnes) {
            case 1: commands = uneLigne(data); break;
            case 2: commands = deuxLignes(data); break;
            case 3: commands = troisLignes(data); break;
            default : throw new PrintServicesException("Le fichier <" + file + "> contient plus de trois colonnes");
            }
            
            data.close();
            
            sendCommands(printer, commands.toString());

        } catch (FileNotFoundException e) {
            System.err.println("Fichier non trouv\u00e9");
        } catch (IOException e) {
            System.err.println("Erreur r\u00e9seau");
            System.err.println(" - V\u00e9rifier que l'imprimante est en marche,");
            System.err.println(" - V\u00e9rifier la nomenclature de l'imprimante (i.e. tlp9)");
        } catch (PrintServicesException e) {
            System.err.println(e.getMessage());
        }
    }
    
    private static void usage() {
        System.out.println("Usage: etqfic <printer> <file>");
        System.out.println(" <printer> imprimante de destination (ex: tlp9)");
        System.out.println(" <file> fichier texte des \u00e9tiquettes");
        System.out.println("\tformat de fichier: 1 ligne du fichier = 1 \u00e9tiquette");
        System.out.println("\t1, 2 ou 3 colonnes par fichier correspondant aux lignes de l'\u00e9tiquette");
        System.out.println("\ts\u00e9paration des colonnes par un espace");
    }
    
    public static void main(String[] args) {
        if(args.length == 2)
            new Tags(args[0], args[1]);
        else usage();
    }

}
