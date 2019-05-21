/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package ProgettoFinale.Server1;
import java.rmi.*;

public interface Client extends Remote {
   
   void updateClient(Messaggio m) throws RemoteException;

   void updateModel() throws RemoteException;
}

