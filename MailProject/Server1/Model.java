/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package ProgettoFinale.Server1;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Luca
 */
public class Model extends Observable implements Serializable{
    private LinkedList<Messaggio> mails;
    private String indirizzo;
    private int id;
    
    public Model(String mail){
        this.indirizzo = mail;
        mails = new LinkedList<Messaggio>();
        id = 0; 
    }
    
	//Ci possono essere più accessi contemporanei alla lettura delle email
    public synchronized void readMails(File f){            
		String line = "", mittente = "", destinatario = "", oggetto = "", data = "",
		testo = "", priorita = "", id1 = "", read="", indirizzo = "";
		int id = 0, i=1;
		try{
			boolean flag = true, b=false;
			Scanner message = new Scanner(f).useDelimiter("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
			while(message.hasNext()){
				line=message.next();
				Scanner pieces = new Scanner(line).useDelimiter(";\n");				
				while(pieces.hasNext()){
					if(flag){
						indirizzo = pieces.next();
						id = Integer.parseInt(pieces.next());
						flag = false;
					}else{
						switch(i){
							case (1):
								mittente=pieces.next();
								i++;
								break;
							case (2):
								destinatario=pieces.next();
								i++;
								break;
							case (3):
								oggetto=pieces.next();
								i++;
								break;
							case (4):
								data=pieces.next();
								i++;
								break;
							case (5):
								testo=pieces.next();
								i++;
								break;
							case (6):
								priorita=pieces.next();
								i++;
								break;
							case (7):
								id1=pieces.next();
								i++;
								break;
							case(8):
								read=pieces.next();
								if(read.equals("true"))
									b=true;
								else
									b=false;
								i=1;
								Messaggio tmp = new Messaggio(mittente, destinatario, oggetto, data, testo, priorita,id1, b);
								mails.addLast(tmp);
								break;

							default:
								break;
						}

					}	
				}
			}
		}catch(IOException exc) {
			exc.getCause();
		}
		
	}
        
    
    public void saveModel(File file){
		
		String text = this.toString();
		PrintWriter p=null;
		
		try{
			p=new PrintWriter(file);
			p.print(text);
			p.flush();
		}catch(IOException | RuntimeException exc) {
				exc.getCause();
		}finally {
			if (p!=null)	
				p.close(); 
		}
    }
    
    	public String[][] getSentMessages(){
                
		String[][] ogg = this.getMessagesData();
		ArrayList<String[]> msg= new ArrayList<String[]>();                
		for (int i=0; i< ogg.length ; i++ ) {                        
			if((ogg[i][0]).equals(this.indirizzo)){
				msg.add(ogg[i]);                              
                        }
		}
		String[][] res= new String[msg.size()][5];
		res = msg.toArray(res);
		return res;
	}
        
	public String[][] getRecMessages(){
		String[][] ogg = this.getMessagesData();
		ArrayList<String[]> msg= new ArrayList<String[]>();               
		for (int i=0; i< ogg.length ; i++ ) {
			if((ogg[i][1]).contains(this.indirizzo))
				msg.add(ogg[i]);
		}
		String[][] res= new String[msg.size()][5];
		res = msg.toArray(res);
		return res;
	}
        
        public String[][] getMessagesData(){
		Messaggio mail[]= new Messaggio[mails.size()];
		String res[][] = new String[mails.size()][7];
		mail = mails.toArray(mail);
		for(int i=0; i<res.length; i++){                    
                    res[i][0] = mail[i].getMitt();                    
                    res[i][1] = mail[i].getDest();
                    res[i][2] = mail[i].getData();
                    res[i][3] = mail[i].getOgg();
                    res[i][5] = (new Integer(i)).toString();
                    res[i][4] = new Integer(mail[i].getId()).toString();
                    res[i][6] = mail[i].getRead();
		}
		return res;
	}
        
        public synchronized void addMessage (Messaggio m){
            mails.addFirst(m);
            m.setId(id);
            id++;
            //callNotify(null);
	}
                
        public String getIndirizzo(){
            return indirizzo;
        }
        
        public void removeMessage(int n){
            if(mails.size() == 0)
		throw new NoMessageException("Nessun Messaggio Da Cancellare!");
            else{
		if(n <= mails.size()){
                    mails.remove(n);
                    callNotify(null);
                }
            }
	}
        
        public Messaggio getMessage(int i){
            if(mails.size() == 0)
		throw new NoMessageException("Nessun Messaggio Da Visualizzare!");
            else{
		Messaggio mail[]= new Messaggio[mails.size()];
		mail = mails.toArray(mail);
		return(mail[i]);
	    }
	}
                
    public Messaggio readMessage(int i){
        Messaggio m = getMessage(i);
        m.setRead();
        return m;
	}
	
	public String toString(){
		
		Messaggio mail[]= new Messaggio[mails.size()];
		String res= this.indirizzo+";\n"+this.id+";\n";
		//Trasforma la lista di messaggi in array
		mail = mails.toArray(mail);
		
		for (int i=0; i<mails.size() ; i++ ) {
			res = res + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n";
			res = res + mail[i].toString();		
		}
		
		return res;
	}
        
        public void callNotify(Messaggio m){
            setChanged();
            Thread t = new Thread(){
            public void run() {
		if(m==null)
                    notifyObservers();
		else
                    notifyObservers(m);
		}
            };
            t.start();
	}

}



class NoMessageException extends RuntimeException{

	private String message;

	public NoMessageException(String msg){
		this.message=msg;
	}

	public String getMessage(){
		return this.message;
	}
}

