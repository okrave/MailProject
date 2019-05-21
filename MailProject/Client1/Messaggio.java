/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Luca
 */
public class Messaggio implements Serializable{

    public void setMittente(String mittente) {
        this.mittente = mittente;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public void setPriorita(String priorita) {
        this.priorita = priorita;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
    
	private int id;
	private String mittente;
	private String destinatario;
	private String oggetto;
	private String data;
	private String testo;
	private String priorita;
	private Boolean read;

	public Messaggio(){
		mittente = null;
		destinatario= null;
		oggetto = null;
		data = null;
		testo = null;
		priorita = null;
		read=false;
	}

	public Messaggio(String mittente1, String destinatario1, String oggetto1, 
					 String data1, String testo1, String priorita1){
		mittente = mittente1;
		destinatario= destinatario1;
		oggetto = oggetto1;
		data = data1;
		testo = testo1;
		priorita = priorita1;
		id =0;
		read=false;
	}

	public Messaggio(String mittente1, String destinatario1, String oggetto1,
					 String data1, String testo1, String priorita1, String id1, Boolean b){
		mittente = mittente1;
		destinatario= destinatario1;
		oggetto = oggetto1;
		data = data1;
		testo = testo1;
		priorita = priorita1;
		id = Integer.parseInt(id1);
		read=b;
	}

	public int getId(){ return this.id; }

	public void setId( int i){ this.id=i; }

	public String getMitt(){ return mittente; }

	public String getDest(){ return destinatario; }

	public String getOgg(){ return oggetto; }

	public String getData(){ return data; }

	public String getText(){ return testo; }

	public String getPri(){ return priorita; }

	public void setRead(){ this.read=true; }

	public String getRead(){ return read.toString(); }

	public String toString(){
		return this.getMitt() +";\n"+ this.getDest() +";\n"+ this.getOgg() +";\n"
			+ this.getData() +";\n"+ this.getText() +";\n"+ this.getPri() +";\n"
			+ this.getId() + ";\n" + this.getRead()+";\n";
	}

	public String toStringFwd(){
		return "In data "+ this.getData()+" "+this.getMitt()+" scrive a "+this.getDest()+".\nOggetto:"+this.getOgg()
				+"\n"+this.getText();
	}

	/**
	* Metodo che restituisce la stringa con tutti i destinataria a cui bisogna rispondere
	*
	*/
	public String getRepAll(String newMitt){
		ArrayList<String> ricevuti = new ArrayList<String>(); 
		String dest = this.getDest();
		Scanner lines = new Scanner(dest).useDelimiter("\\s*;\\s*");
		while(lines.hasNext()){ ricevuti.add(lines.next()); }		
		String res=this.getMitt()+";";
		for (int i=0; i<ricevuti.size(); i++ ) { 
			if(!ricevuti.get(i).equals(newMitt))
				res = res + ricevuti.get(i) + ";"; 
		}
		return res;
	}
}
