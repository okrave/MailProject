/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.rmi.*;
import javax.naming.*;



/**
 *
 * @author Luca
 */
public class MsgFrame extends JFrame implements ActionListener{
    private JFrame root;
    private JLabel labels[] = new JLabel[3]; 
    private JTextField fields[] = new JTextField[2];
    private JTextArea text= new JTextArea(5, 10);
    private JButton send = new JButton("Send");
    private JComboBox<Integer> prior; //specifico i dati contenuti nella JComboBox per evitare warnings
    private Model modello;
    
    public MsgFrame(Model m){
        super("Invia");
        modello = m;
        setLayout(new BorderLayout());
        
        JPanel campi = new JPanel(new SpringLayout());
        String names[]={"A:", "Oggetto:", "Testo:", "Priorità"};
        
        for(int i = 0; i < labels.length; i++){
            labels[i] = new JLabel(names[i],JLabel.TRAILING);
            campi.add(labels[i]);
            if(i<=1){
                fields[i] = new JTextField(20);
                labels[i].setLabelFor(fields[i]);
                campi.add(fields[i]);
            }else{
                labels[i].setLabelFor(text);
                JScrollPane scrollPane = new  JScrollPane(text);
				campi.add(scrollPane);		
				Border border = BorderFactory.createLineBorder(Color.GRAY); //Costruzione del bordo per la textarea
				text.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(5, 5, 5, 5)));
				text.setText("Scrivi qua il corpo del messaggio!"); //composizione di un nuovo messaggio                
            }
        }
		
        JPanel bottoni= new JPanel(); //Pannello per i controlli
		JLabel priorita = new JLabel(names[3], JLabel.TRAILING);
		Integer range[] = {1, 2, 3, 4, 5};
		prior = new JComboBox<>(range);
		priorita.setLabelFor(prior);
		bottoni.add(priorita);		
		bottoni.add(prior);
		bottoni.add(send);
        root = this;
        send.addActionListener(this);
		SpringUtilities.makeCompactGrid(campi, 3, 2, 6, 6, 6, 6); //rows, cols //initX, initY //xPad, yPad
		
		add(bottoni, BorderLayout.PAGE_END);
		add(campi, BorderLayout.CENTER);
		addCloser(this);   
        
    }
    
    public Messaggio getMessage(){
        String destinatario = fields[0].getText(),oggetto= fields[1].getText(), tmp = "",dest = "", priorita = prior.getSelectedItem().toString(), testo = text.getText();
        
        if(destinatario.equals("")){
            JOptionPane.showMessageDialog(this,"Errore! Il campo destinatario è vuoto");
            return null;
        }
        if(oggetto.equals("")){
            JOptionPane.showMessageDialog(this,"Errore! Il campo oggetto è vuoto");
            return null;
        }
        
        if(testo.equals("")){
            JOptionPane.showMessageDialog(this,"Errore! Il campo testo è vuoto");
            return null;
        }
        
        //Parsifico i destinatari
        Scanner destinatari = new Scanner(destinatario).useDelimiter("\\s*;\\s*");
        while(destinatari.hasNext()){
            tmp = destinatari.next();
            if(!dest.contains(tmp))//Se scrivo più volte lo stesso indirizzo nel campo "A:" non dò errori, semplicemente lo inserisco una volta sola (e invio un solo messaggio).
                dest = dest + tmp + ";";
        }
        
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");//Ottengo la data corrente
        
        Messaggio messages = new Messaggio(modello.getIndirizzo(), dest, oggetto, dateFormat.format(new Date()), testo, priorita);
        
        return messages;
    }
    
    public void addCloser(JFrame frame){		
        frame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                if(!fields[0].getText().equals("") || !fields[1].getText().equals("")){
                    int n = JOptionPane.showConfirmDialog(frame,"Sei sicuro di voler chiudere la finestra?", "Attenzione!",JOptionPane.YES_NO_OPTION);
                    if(n==0){ frame.dispose(); }
                }else{ frame.dispose(); }
            }
        } );		
    }
    
    public void setFields(String s){fields[1].setText(s);}

    public void setFields(String s, String ss){fields[0].setText(s); fields[1].setText(ss);}

    public void setText(String s){text.setText(s);}
    
    public void actionPerformed(ActionEvent e){        
        Messaggio m = getMessage();
        if(m != null){
            try{
                Context namingContext = new InitialContext();
                Server s = (Server) Naming.lookup("rmi://127.0.0.1:2000/Server");                
                System.out.println("\nSto per invocare il metodo insertMessage() dell'oggetto remoto");
                
                String res = "";
                modello.addMessage(m);
                
                if(!(m.getDest()).equals(modello.getIndirizzo()))
                    res = s.insertMessage(m);
                
                if(res != null){
                    JOptionPane.showMessageDialog(root, "Errore nell'invio del messaggio! "+res);
                }else{
                    JOptionPane.showMessageDialog(root, "Messaggio inviato!");
                    dispose();
                }
                
            }catch(Exception e1){
            }
        }
    }
}

class FwdFrame extends MsgFrame {
    public FwdFrame(Model m, Messaggio msg){
		super(m);
		setFields("I: "+msg.getOgg());
		setText("\n"+msg.toStringFwd());
    }
}

class RepFrame extends MsgFrame {
    public RepFrame(Model m, Messaggio msg){
		super(m);
		setFields(msg.getRepAll(m.getIndirizzo()), "R: "+msg.getOgg()); 
		setText("\n"+msg.toStringFwd());
    }
}

