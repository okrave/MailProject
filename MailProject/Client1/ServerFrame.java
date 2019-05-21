/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 *
 * @author Luca
 */
public class ServerFrame extends JFrame implements Observer{
    private JTextArea log = new JTextArea(15, 45);
    private JButton save =  new JButton("Save log file");
    
    public ServerFrame(){
        super();
        JScrollPane scrollPane = new JScrollPane(log);
        Border border = BorderFactory.createLineBorder(Color.GRAY);
        log.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        log.setEditable(false);
        save.addActionListener(new saveListener());
        Container pane=this.getContentPane();
		pane.setLayout(new BorderLayout());
		pane.add(save,BorderLayout.PAGE_END );
		pane.add(scrollPane, BorderLayout.PAGE_START);
      
        pane.setVisible(true);
    }
    
    public static void main(String[]args){
        ServerFrame x = new ServerFrame();
    }
	
    class saveListener implements ActionListener{
        public void actionPerformed(ActionEvent e){		
            File file=new File("logfile.txt");
            PrintWriter p=null;
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm"); //Ottengo la data corrente
            Date date = new Date();
            String data= dateFormat.format(date);
            String text="\n"+data+"\n"+log.getText()+"\n";
            try{
				p = new PrintWriter(new FileOutputStream(file, true /* append = true */)); 
                p.append(text);
				p.flush();
            }catch(IOException | RuntimeException exc) {
				exc.getCause();
            }finally{
                if (p!=null)	
                    p.close(); 
            }
            JOptionPane.showMessageDialog(log, "Testo salvato nel file di log!");
            log.setText("");
        }
    }
       
    @Override
    public void update(Observable ob, Object extra_arg){
        if(extra_arg!=null){
            String s = (String)extra_arg;
            log.append(s);
		}
    }
}
 
