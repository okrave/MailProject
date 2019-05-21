/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package ProgettoFinale.Server1;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;
import javax.naming.*;


/**
 *
 * @author Luca
 */
public class SModel extends UnicastRemoteObject implements Server{
    
    private static SModel s;
    private Model[] caselle;
    private int connessi = 0;
    private Notify obs;
    
    public SModel(String[] ind) throws RemoteException{
        super();
        s = this;
        caselle = new Model[ind.length];//Creo tanti model quanti sono gli indirizzi
        obs = new Notify();
        for(int i = 0; i<caselle.length; i++){
            caselle[i] = this.setCasella(ind[i]);
        }
        connessi = 0;
    }
    
    public static synchronized SModel getInstance(String[] ind) throws RemoteException{
		if (s==null){ s=new SModel(ind); }
		return s;
    } 

    public int getConnessi(){ return connessi; }
	
    public Notify getObs(){ return obs;}
        
    public static void lanciaRMIRegistry(){
      	try{ 
			LocateRegistry.createRegistry(2000);
			System.out.println("REGISTRO CREATO!");
		}catch(RemoteException e) { 
			System.out.println("REGISTRO GIA ESISTENTE!"); 
		}
    }
        
    public void checkConnection(String[] ind){
		for (int i = 0;i<caselle.length;i++ ){
			try {
				Context namingContext = new InitialContext();
				System.out.println("Indirizzo: "+ind[i]);
				Client c = (Client)Naming.lookup("//127.0.0.1:2000/"+ind[i]);					
				System.out.println("\nSto per invocare il metodo updateModel() dell'oggetto remoto");
				c.updateModel();             
			}catch(Exception e2){
				System.out.println("Non era attivo il client "+ind[i] + "\n" + e2.getMessage());					
			}
		}
    }
                
    
	/*
	* Metodo setCasella: Una volta che viene avviata una Mail viene settata dal server
	* la casella rispondente, quindi verranno lette le email dal modello e notificato alla view
	*
	**/
    @Override
    public Model setCasella(String s) throws RemoteException {		
		
        File f = new File("caselle/"+s+".txt");
		
        if(!f.exists() || f.isDirectory()) { 
			System.out.println("File inesistente");
            obs.callNotify(s+ " ha provato a connettersi, ma non esiste la casella corrispondente\n"); 
            
			return null;
		}
		
        Model model = new Model(s);
       
        model.readMails(f);
		
        obs.callNotify(s+" si è connesso.\n");
		
        connessi++;
		
        return model;
    }

    @Override
    public synchronized String insertMessage(Messaggio m) throws RemoteException {
		
        String s = m.getDest();
        
        ArrayList<String> ricevuti = new ArrayList<String>();
        
        Scanner lines = new Scanner(s).useDelimiter("\\s*;\\s*");
        
		//Salvo tutti i destinatari del messaggi in un ArrayList
        while(lines.hasNext()){
            ricevuti.add(lines.next());
        }
        
        String dest[]=new String[ricevuti.size()];
        
		//Scorro i file in caselle per vedere se esiste la casella postale del destinatario
        for(int i = 0; i < ricevuti.size(); i++){
            dest[i] = ricevuti.get(i);
            if(!Files.exists(Paths.get("caselle/"+dest[i]+".txt"))){
                return "L'indirizzo destinatario specificato:" +dest[i]+ " non è presente nel server.";
            }
        }
        
        for(int i = 0; i< dest.length ; i++){
            for(int  j = 0; j< caselle.length;j++){
                if(m.getMitt().equals(caselle[j].getIndirizzo()) && m.getMitt().equals(dest[i])){//Caso in cui mittente == destinatario in questo caso il messaggio è stato gia aggiunto dal client
                    obs.callNotify(m.getMitt()+" ha inviato un messaggio a "+dest[i]+"\n");
                }else if(dest[i].equals(caselle[j].getIndirizzo()) && !(m.getMitt().equals(dest[i]))){//Caso in cui mittente != destinatario devo aggiornare il client destinatario con il nuovo messaggio
                    caselle[j].addMessage(m);
                    File file = new File("caselle/"+caselle[j].getIndirizzo()+".txt");
                    file.delete();
                    file = new File("caselle/"+caselle[j].getIndirizzo()+".txt");
                    caselle[j].saveModel(file);
                    try{
                        Context namingContext = new InitialContext();
                        Client c = (Client) Naming.lookup("//127.0.0.1:2000/"+dest[i]);
                        System.out.println("\n Sto per invocare il metodo updateClient() dell'oggetto remoto");
                        c.updateClient(m);
                    }catch(Exception e2){
                        System.out.println("(Server)E' fallita la naming: " + e2.getMessage());
                    }
                    obs.callNotify(m.getMitt()+" ha inviato un messaggio a "+dest[i]+"\n");
                    
                }
            }
        }
        return null;
        
    }
	
	/**
		Metodo synchronized perche più utenti potrebbero accedere all'array dei modelli caselle
		il metodo in questo non fa altro che cancellare il "vecchio" modello per ricrearne un alotr
		aggiornato
	*/
    @Override
    public synchronized void saveModel(Model m )throws RemoteException{ 
		
		String s = m.getIndirizzo();
		boolean t=false; 
		
		//Fin quando non trova il Modello corrispondente
		for (int i=0; i<caselle.length && !t; i++ ) {
			if(s.equals(caselle[i].getIndirizzo())){
				t=true;
				File file = new File("caselle/"+m.getIndirizzo()+".txt");
				file.delete();
				file = new File("caselle/"+m.getIndirizzo()+".txt");
				m.saveModel(file);
			}	
		}
		connessi--;
		obs.callNotify(s+ " si è disconnesso\n");
    }
    
}
