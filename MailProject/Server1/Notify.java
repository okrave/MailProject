/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package ProgettoFinale.Server1;
import java.util.*;

/**
 *
 * @author Luca
 */
public class Notify extends Observable{

	public void callNotify(String s){
		setChanged();
		Thread t = new Thread(){
			public void run() {
				notifyObservers(s);
			}
		};
		t.start();
	}

}
