/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Luca
 */
public class Controller extends JPanel implements ActionListener{
    private Model model;
    private JButton[] buttons = new JButton[5];
    private JTable table;
	
     
    public Controller(Model modello){
        super(new GridLayout(1, 5, 10, 20));
        this.model = modello;        
        String[] labels = {"Scrivi", "Elimina", "Visualizza", "Inoltra", "Rispondi"};
        
        for(int i = 0; i < buttons.length ; i++){
            buttons[i] = new JButton(labels[i]);
            buttons[i].addActionListener(this);
            this.add(buttons[i]);
            buttons[i].setEnabled(true);
            
        }        
    }
	
    public String [][] getControllSentMessages(){
        return model.getSentMessages();
    }
	
    public String[][] getControllRecMessages(){
        return model.getRecMessages();
    }
	
    public void setTable(JTable t){ table = t; }
    
    //individua l'indice del messaggio selezionato nella tabella correntemente consultata
    public int getMsgIndex(){
        
		String j = ""; int i=0;
        int selectedRow = table.getSelectedRow();
        if(selectedRow >= 0){
            j = (String)table.getModel().getValueAt(table.getSelectedRow(),5);
            i = Integer.parseInt(j);  
            
            return i;
        }else{
            return -1;
        }
    }
    
    public void setButtons(Model m){
		
		model= m;
		for (int i = 0; i<buttons.length ;i++ ) {
			buttons[i].setEnabled(true);
		}
		model.callNotify(null);
	}
    
	/**
	*
	*	Funzioni ActionPerformed per la gestione dei bottoni, in questo caso prendo in input l'evento e parsifico una volta estratto il bottone
	*	il testo di quest'ultimo, nel caso in cui siamo nell'opzione Scrivi,Rispondi,Inoltra creo un messaggio con i parametri richiesti (nel
	*	caso in cui devo rispondere il titolo dell'oggetto deve essere lo stesso ma devo anche settare il destinatario) 
	*
	*/
    public void actionPerformed(ActionEvent e){
        JButton source = (JButton)e.getSource();   
        MsgFrame m = null;
        switch(source.getText()){
            case "Scrivi":               
                m = new MsgFrame(model);
            break;  
            
            case "Elimina":
                try{                    
                    int i = getMsgIndex();
                    if(i != -1)
                        model.removeMessage(i);
                    else
                        JOptionPane.showMessageDialog(getRoot(), "Selezionare un messaggio per eliminarlo");
                    
                }catch(NoMessageException err){
                    System.out.println(err.getMessage());
                    JOptionPane.showMessageDialog(getRoot(), "La casella è vuota, non puoi eliminare messaggi!");
                }
            break;
            
            case "Visualizza":
                try{
                    int i = getMsgIndex();
                    if(i != -1)
                        model.callNotify(model.readMessage(getMsgIndex()));
                    else
                        JOptionPane.showMessageDialog(getRoot(),"Selezionare un messaggio per visualizzartlo");
                }catch(NoMessageException err){
                    System.out.println(err.getMessage());
                    JOptionPane.showMessageDialog(getRoot(), "La casella è vuota, non puoi visualizzare messaggi!"); 
                }
            break;
            
            case "Inoltra":
                int i = getMsgIndex();
                if(i!= -1)
                    m = new FwdFrame(model, model.getMessage(i));
                else
                    JOptionPane.showMessageDialog(getRoot(), "Selezionare un messaggio per poterlo inoltrare"); 
            break;
            
            case "Rispondi":
                i = getMsgIndex();
                if(i!= -1)
                    m = new RepFrame(model, model.getMessage(i));
                else
                    JOptionPane.showMessageDialog(getRoot(), "Selezionare un messaggio per poter rispondere"); 
            break;
        }
        if(m!= null){
            startMsgFrame(m);
        }
    }
    
    public void startMsgFrame(MsgFrame m){
        Container parent=getRoot();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                m.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                m.setSize(500, 250);
                m.setLocation((int)parent.getLocation().getX()+190, (int)parent.getLocation().getY()+112);
                m.setVisible(true);
            }
        });    
    }
	
    public Container getRoot(){
		Container parent = this; 
		
		do{
			parent = parent.getParent();
		}while(!(parent instanceof Window) && parent != null);
		
		return parent;
	}
    
}
