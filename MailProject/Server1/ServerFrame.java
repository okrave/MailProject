
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package ProgettoFinale.Server1;
import java.util.*;
import java.text.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.Naming;
import javax.naming.*;
import javax.swing.border.*;
import java.net.*;
import java.net.URL;
/**
 *
 * @author Luca
 */
public class ServerFrame extends JFrame implements Observer{
    
    private JTextArea log = new JTextArea(15,45);
    private JButton save = new JButton("Salva log");
    private SModel caselle;
    
    public ServerFrame(SModel m){
		
        super();
        this.caselle = m;
        JScrollPane scrollPane = new JScrollPane(log);
        
        Border border = BorderFactory.createLineBorder(Color.GRAY);
        log.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        log.setEditable(false);
        
        save.addActionListener(new saveListener());
        
       
        this.setLayout(new BorderLayout());
        this.add(save,BorderLayout.PAGE_END);
        this.add(scrollPane,BorderLayout.PAGE_START);
    }
    

    
    public void addClosed(){
        JFrame m = this;
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we){
                if(caselle.getConnessi()>0){
                    JOptionPane.showMessageDialog(log,"Non puoi chiudere la finestra, ci sono utenti connessi" );
                }else{
                    System.exit(0);
                }
            }
        });
    }
    public static void main(String[]args){
        SwingUtilities.invokeLater(new Runnable() {
            public void run(){
            	try{
                    
                    String[] caselle=getCaselle();
                    
                    SModel m = SModel.getInstance(caselle);
                    
                    m.lanciaRMIRegistry(); // prima di far salire il server devo lanciare il registry
                    
                    try{
                        Naming.rebind("rmi://127.0.0.1:2000/Server", m);
                    }catch(Exception e){
                        System.out.println("E' fallita la rebind del server! "+e.getMessage()+e.getLocalizedMessage()+e.getCause());
                    }
                    ServerFrame s = new ServerFrame(m);
                    s.setResizable(false);
                    s.setTitle("MAIL SERVER");
					s.pack();
					s.setVisible(true);
					s.addCloser();
					s.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    m.getObs().addObserver(s);
                    m.checkConnection(caselle);
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }	
            }
	});
    }
    
    public static String[] getCaselle(){		
		Scanner scan = new Scanner(System.in);
		
        URL url = ServerFrame.class.getResource("/Caselle");
        File folder = new File(url.getPath());
		     
		File[] listOfFiles = folder.listFiles();
		
		String[] caselle = new String[listOfFiles.length];
		int i = 0;
		
		for (File file : listOfFiles) {
			if (file.isFile()) {
				int l = file.getName().length();
				caselle[i] = file.getName().substring(0,l-4);			
				
			}
			i++;
		}
		
		return caselle;
    }
        
    public void addCloser(){
	JFrame m = this;
	this.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                if(caselle.getConnessi()>0){
                    JOptionPane.showMessageDialog(log, "Non puoi chiudere il server finchè ci sono client attivi!");
                }else{
                    System.exit(0);
                }
            }
	});
    }
    
    public void update(Observable o, Object arg) {
        if(arg!=null)
            log.append((String) arg);
    }
    
    class saveListener implements ActionListener{
    
    public void actionPerformed(ActionEvent e){
        URL url = ServerFrame.class.getResource("logfile.txt");    
        File file = new File(url.getPath());
		if(!file.exists() || file.isDirectory()){
            System.out.println("Errore file inesistente");
            System.exit(0);           
        }
        
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");//Formato della data che stamperò
        Date date = new Date();
        
        String data = dateFormat.format(date);        
        String text = "\n" + data + "\n" + log.getText() + "\n";
        
        PrintWriter p = null;
        
        try{
            p = new PrintWriter(new FileOutputStream(file,true)); //Il true è per l'append
            p.append(text);
            p.flush();
        }catch(IOException | RuntimeException exc){
            exc.getCause();
        }finally{
            if (p!= null)
                p.close();
        }
        
        JOptionPane.showMessageDialog(log, "Testo salvato nel file di log");
        log.setText("");
        
    }

}
    
}


