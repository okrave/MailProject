/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.*;
import java.net.URL;
/**
 *
 * @author Luca
 */

public class MailFrame extends UnicastRemoteObject implements Client{
	
    private JFrame frame;
    private Model model;
    private View vista;
    private Controller controlloMail;
    private Boolean conn = false;
    
    public MailFrame(String mail,String position) throws RemoteException{  
                                //Creo il Model
        this.model = new Model(mail);	
								//Leggo il file delle caselle di posta dell'email corrispondente
        model.readMails(readFile(mail));
                                //Creo il controller
        this.controlloMail = new Controller(model);
                                //Creo la View
        vista = new View(controlloMail);
                                
        model.addObserver(vista);
                                
        frame = new JFrame();       
        
        frame.setSize(890, 500);
        frame.setLocation(Integer.parseInt(position),Integer.parseInt(position)/2);
		frame.setLayout(new BorderLayout());
		frame.add(Box.createHorizontalStrut(50));        
        frame.add(vista,BorderLayout.CENTER);
        frame.setTitle("Casella Posta: "+mail);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
	public static File readFile(String mail){
		URL url = MailFrame.class.getResource("/local");
                
		File f = new File(url.getPath()+"/"+mail+".txt");
        
		
        if(!f.exists() || f.isDirectory()){
            System.out.println("Errore file inesistente");
            System.exit(0);
        }	
		
		return f;
	}
    
    public void connect(String mail){
		
    	try {
			
            Context namingContext = new InitialContext();
			
            System.out.println("\nFinestra del client.");
			
            Server s = (Server)Naming.lookup("//127.0.0.1:2000/Server");
			
            System.out.println("\nSto per invocare il metodo setCasella() dell'oggetto remoto");
			
            model = s.setCasella(mail);    
			
            model.addObserver(vista);  
						
            if(model==null){
            	JOptionPane.showMessageDialog(frame, "Indirizzo specificato non valido!");
            	System.exit(0);
            }
			
			//Una volta che la connessione si è stabilita posso azionare i bottoni
            controlloMail.setButtons(model);
			
	    	conn=true;			
	    	addCloser();
			
	    	JOptionPane.showMessageDialog(frame, "Connessione stabilita!");
	    }catch(Exception e2){ 
			System.out.println("Problema: il server non è attivo! " + e2.getMessage()); 
		}
    }
	
	public void addCloser(){
		
    	frame.addWindowListener( new WindowAdapter() {
			@Override
            public void windowClosing(WindowEvent we) {
        		try {
		            Context namingContext = new InitialContext();
		            System.out.println("\nFinestra del client.");
		            Server s = (Server)Naming.lookup("//127.0.0.1:2000/Server");
		            System.out.println("\nSto per invocare il metodo saveModel() dell'oggetto remoto");
		            s.saveModel(model);
			    }catch(Exception e2){ System.out.println("Problema chiusura: " + e2.getMessage()); }
            	
				File file = new File("local/"+model.getIndirizzo()+".txt");
				file.delete();
				file = new File("local/"+model.getIndirizzo()+".txt");
                model.saveModel(file);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.dispose();
                System.exit(0);
			}
		});
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    public void updateModel() throws RemoteException{
    	Thread t = new Thread(new Runnable(){
	        public void run(){
	            connect(model.getIndirizzo());
	        }
	  	});
	  	t.start();	
    }

	public static void lanciaRMIRegistry(){
	  	try{ 
			//Creates and exports a Registry instance on the local host that accepts requests on the specified port.
	        LocateRegistry.createRegistry(2000);
	        System.out.println("REGISTRO CREATO!");
	    }catch(RemoteException e) { System.out.println("REGISTRO GIA ESISTENTE!"); }
	}

	public void updateClient(Messaggio m) throws RemoteException{
		model.addMessage(m);
	    Thread t = new Thread(new Runnable(){
	        public void run(){ JOptionPane.showMessageDialog(frame, "Messaggio ricevuto!"); }
	  	});
	  	t.start();
	}

    public static void main(String[] args){
    	SwingUtilities.invokeLater(new Runnable() {
			
            public void run() {  
          	
            	lanciaRMIRegistry(); // prima di far salire il server devo lanciare il registry
				
            	try{
					
            		MailFrame f = new MailFrame(args[0], "600");
					
					try{
						
                        Naming.rebind("//127.0.0.1:2000/"+args[0], f);
				        System.out.println("\nClient: "+args[0]);
						
					}catch(Exception e){ 
						System.out.println("E' fallita la naming: "+e.getMessage()); 
					}
					
					f.connect(args[0]);
            	}
    			catch(RemoteException e){ System.out.println(e.getMessage()); }
            }
			
        });
    }   
}
