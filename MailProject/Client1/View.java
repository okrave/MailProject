/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author Luca
 */
public class View extends JPanel implements Observer{
    
    private String columns[] = {"Da:", "A:", "Data:", "Oggetto:", "Id:", "Indice:", "Letto:"};
    private JTable ricevuti;
    private JTable inviati;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JEditorPane viewer;
    
   
    
 
    public View(Controller controll){
        super(new BorderLayout(10,0));
        
        
       	JPanel lista = new JPanel();
        lista.add(tabbedPane);
        
        viewer = createViewer();
        
        JScrollPane reader = new JScrollPane(viewer);
        //Creare lista ricevuti inviati
        createLists(controll.getControllSentMessages(),controll.getControllRecMessages());
   
        JScrollPane recs = new JScrollPane(ricevuti);
        JScrollPane sents = new JScrollPane(inviati);
        
        tabbedPane.add("Ricevuti",recs);
        tabbedPane.addTab("Inviati",sents);
        
        //Da fare
        ChangeListener changeListener = new ChangeListener(){
            public void stateChanged(ChangeEvent changeEvent){
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                if(sourceTabbedPane.getTitleAt(index).equals("Ricevuti"))
                    controll.setTable(ricevuti);
                else
                    controll.setTable(inviati);
            }
        };
        
        tabbedPane.addChangeListener(changeListener);
        
        //Iniziamente come default verranno visualizzati i messaggi ricevuti
        controll.setTable(ricevuti);
        
        this.add(lista,BorderLayout.LINE_START);
        this.add(reader,BorderLayout.CENTER);        
        this.add(controll,BorderLayout.SOUTH);
        
        updateList(controll.getControllRecMessages(),controll.getControllSentMessages());
        updateMsg(new Messaggio());
        
    }
    
    public void createLists(String rec[][], String sent[][]){
        ricevuti = new JTable(rec,columns){
        
		public boolean isCellEditable(int row, int column){return false;}
        
        public Component prepareRenderer(TableCellRenderer renderer,int Index_row, int Index_col){
			Component comp = super.prepareRenderer(renderer, Index_row, Index_col);
				if (!(getModel().getValueAt(Index_row,6)).equals("true"))//Se sono letti il font deve essere diverso
					comp.setFont(this.getFont().deriveFont(Font.BOLD));
				else
					comp.setFont(this.getFont());
				return comp;           
            }
        };
        
        inviati = new JTable(sent,columns){
            public boolean isCellEditable(int row, int column){return false;}
        };
    }
    
    public static void setListeners(Controller controlloMail){
    
    }
    
    public void updateMsg(Messaggio msg){
		String htmlString = "";
		if(msg.getMitt()==null){
			htmlString = "<html>"
	                   + "<body>"
	                   + "<h1>Welcome!</h1>"
	                   + "</body>";
		}else{
			String[] destArr = msg.getDest().split(";");
			String dest="";
			if(destArr.length>1){ 
				dest="<h1>Destinatari: ";
				for(String x : destArr){
					dest+="<br><span>"+x+"</span>";
				}
				dest+="</h1>";
			}
			else{
				dest="<h1>Destinatario: <br><span>"+msg.getDest()+"</span></h1>";
			}
			
		    htmlString =  "<html>"
	                      + "<body>"
	                      + "<h1>Mittente: <br><span>"+ msg.getMitt() +"</span></h1>"
	                      + "<h1>Data: <br><span>"+ msg.getData() +"</span></h1>"
	                      + "<h1>Oggetto: <br><span>"+msg.getOgg()+"</span></h1>"
	                      + /*"<h2>"+msg.getDest()+"</h2>"*/dest
	                      + "<p>"+msg.getText()+"</p>"
	                      + "<h2>Priorità: <span>"+msg.getPri()+"</span></h2>"	                      
	                      /*+ "<p >"+msg.getPri()+"</p>"*/
	                      + "</body>";
	    }
	    Document doc = viewer.getEditorKit().createDefaultDocument();
	    viewer.setDocument(doc);
	    viewer.setText(htmlString);
    }

    public void updateList(String rec[][], String sent[][]){
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
				//Setta la tabella con i nuovi ricevuti
				ricevuti.setModel(new DefaultTableModel(rec, columns));
	    		//Setta la tabella con i nuovi inviati
				inviati.setModel(new DefaultTableModel(sent, columns));
		    	
				ricevuti.getColumnModel().removeColumn(ricevuti.getColumnModel().getColumn(6));
		      	inviati.getColumnModel().removeColumn(inviati.getColumnModel().getColumn(6));      
		      	
				ricevuti.getColumnModel().removeColumn(ricevuti.getColumnModel().getColumn(5));
      			inviati.getColumnModel().removeColumn(inviati.getColumnModel().getColumn(5)); 
      			
				ricevuti.getColumnModel().removeColumn(ricevuti.getColumnModel().getColumn(1));
      			inviati.getColumnModel().removeColumn(inviati.getColumnModel().getColumn(0));           	
            }
        });
        this.repaint();
    }

    public JEditorPane createViewer(){
		JEditorPane jEditorPane = new JEditorPane();
		jEditorPane.setEditable(false); 	// make it read-only
		HTMLEditorKit kit = new HTMLEditorKit(); 	// add an html editor kit
		jEditorPane.setEditorKit(kit);
		   
		StyleSheet styleSheet = kit.getStyleSheet();  	// add some styles to the html
		styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; max-width: 100%; }");
		styleSheet.addRule("h1 {font-size: 12px;font-weight: bold; color: rgb(30,30,30);}");
		styleSheet.addRule("h2 {text-align: center; width: 100%; font-size: 10px;font-weight: bold; color: rgb(30,30,30);}");
		styleSheet.addRule("span {font-size: 10px; font-weight: normal; color: rgb(45,45,45);}");
		styleSheet.addRule("h2 span {margin-top: 30px; font-size: 8px; font-weight: normal; color: rgb(45,45,45);}");
		styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");
		styleSheet.addRule("p {border: 1px solid rgb(60,60,60); width: 100%; padding: 5px}");
		return jEditorPane;
    }

    /**
	* In questo caso il mio update fa due cose fondamentali, aggiorna la lista dei messaggi ricevuti/inviati
	* e nel caso in cui l'update contiene un messaggio (significa che il chiamante ha eseguito il metodo visualizza)
	* mostra nell'apposita text area il messaggio da visualizzare;
	*
	*/
    public void update(Observable ob, Object arg) {
        Controller support = new Controller((Model) ob);
        Messaggio obj;
        updateList(support.getControllRecMessages(),support.getControllSentMessages());
        if(arg != null){
            obj=(Messaggio)arg;
            updateMsg(obj);
    	}
        
    }
}
