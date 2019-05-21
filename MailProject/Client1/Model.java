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
    
	/**
	* Metodo che legge le lettere della propria email e le salva nella LinkedList mails, essa conterrà sia 
	* i messaggi inviati che i messaggi ricevuti
	*
	* @param f file nel quale sono salvati le lettere della mail corrispondente
	*
	*/
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
    
	/**
	*	Metodo utilizzato per salvare tutte le lettere della casella postale nel file .txt corrispondente
	* 	questo metodo verra chiamato alla chiusura del log del client aggiornando così la casella postale
	*	
	* 	@param file: file .txt che conterra le mail aggiornate
	*
	*/
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
    
	
	/**
	*
	*	Metodo che crea una matrice di stringhe che conterrà tutte le mail inviate dall'indirizzo corrispondente al model
	*	ricordiamo che una mail deve stare in "Inviate" quando il campo mittente è uguale al all'indirizzo del model
	*
	* 	@return res Matrice contenente tutte le mail inviate
	*
	**/
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
    
	/**
	*
	*	Metodo che crea una matrice di stringhe che conterrà tutte le mail ricevute dall'indirizzo corrispondente al model
	*	ricordiamo che una mail deve stare in "Ricevute" quando il campo destinatario è uguale al all'indirizzo del model
	*
	* 	@return res Matrice contenente tutte le mail ricevute
	*
	**/
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
    

	/**
	*
	*	Metodo che restituisce tutte le mail(ricevute/inviate) della casella postale e le inserisce in una matrice di stringhe
	*
	*	@return res: matrice di stringhe che contiene tutte le mail(ricevute/inviate) della casella postale
	*/
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
    
	/**
	*	
	*	Metodo che inserisce un messaggio nella lista mails, lista che rappresenta tutte le mail(ricevute/inviate) della casella postale
	*	In questo caso norifichiamo che il messaggio è stato aggiunto alla view che ritornera un pannello visivo che indicherà che avremo
	*	ricevuto un messaggio;
	*
	*/
    public synchronized void addMessage (Messaggio m){
        mails.addFirst(m);
        m.setId(id);
        id++;
        callNotify(null);
	}
    
	/**
	* Metodo che restituisce l'indirizzo della casella corrispondente
	*
	* @return indirizzo : indirizzo casella corrispondente
	*/	
    public String getIndirizzo(){
        return indirizzo;
    }
    
	
	/**
	*	Metodo che rimuove una mail nella lista mails, lista che rappresenta tutte le mail(ricevute/inviate) della casella postale
	* 	In questo caso norifichiamo che il messaggio è stato aggiunto alla view che fare un repaint delle caselle ricevute;
	*
	*/
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
	
	/**
	* Metodo toString che utilizza il separatore %%%%%%%%%%%%%%%%%%%%%%%%%%%%% quest'ultimo verrà utilizzato da un parser per capire
	* quando le email iniziano
	*
	* @return res stringa contenente tutte le email suddivise da un separatore
	*/
    public String toString(){
		Messaggio mail[]= new Messaggio[mails.size()];
		String res= this.indirizzo+";\n"+this.id+";\n";
		mail = mails.toArray(mail);
		for (int i=0; i<mails.size() ; i++ ) {
			res = res + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n";
			res = res + mail[i].toString();		
		}
		return res;
	}
	
	/**
	*	Metodo che restituisce un elemento delle mail
	*
	*	@return mail[i] cioè la mail in posizione i
	*
	*/
    public Messaggio getMessage(int i){
        if(mails.size() == 0)
			throw new NoMessageException("Nessun Messaggio Da Visualizzare!");
        else{
			Messaggio mail[]= new Messaggio[mails.size()];
			mail = mails.toArray(mail);
			return(mail[i]);
	    }
	}
    
	/**
	*
	* Metodo che viene chiamato quando un email viene letta, in questo caso l'unico cambiamento sarà il font
	*
	* @return m messaggio che è stato letto
	*
	*/
    public Messaggio readMessage(int i){
        Messaggio m = getMessage(i);
        m.setRead();
        return m;
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

