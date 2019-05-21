/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.rmi.*;

/**
 *
 * @author Luca
 */
public interface Server extends Remote{
	
    Model setCasella(String s) throws RemoteException;
   
    String insertMessage(Messaggio m) throws RemoteException;
   
    void saveModel(Model m) throws RemoteException;
}
