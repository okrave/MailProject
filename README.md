# MailProject
Applicazione java che implementa un servizio di posta elettronica organizzato con un mail server che gestisce le caselle di posta elettronica degli utenti e i mail client necessari per permettere agli utenti di accedere alle proprie caselle di posta. Vi sono 3 utenti di posta elettronica che comunicano tra loro.
* Il mail server gestisce una lista di caselle di posta elettronica. Il mail server ha
un’interfaccia grafica sulla quale viene visualizzato il log delle azioni effettuate
dai mail clients e degli eventi che occorrono durante l’interazione tra i client e il
server. Per esempio: apertura/chiusura di una connessione tra mail client e server,
invio di messaggi da parte di un client, ricezione di messaggi da parte di un client,
errori nella consegna di messaggi, eliminazione di messaggi, etc. (tutte le tipologie
di azioni permesse dai client
* Una casella di posta elettronica contiene:
  * Nome dell’account di mail associato alla casella postale (es.giorgio@mia.mail.com).
  * Lista eventualmente vuota di messaggi. I messaggi di posta elettronica sono istanze di una classe   Email che specifica ID, mittente, destinatario,argomento, testo e data di spedizione del messaggio.
* Il mail client, associato ad un particolare account di posta elettronica, ha un’interfaccia grafica così caratterizzata:
  * L’interfaccia permette di:
    * creare e inviare un messaggio a uno o più destinatari
    * leggere i messaggi della casella di posta
    * rispondere a un messaggio ricevuto, in Reply (al mittente del destinatario) e/o in Reply-all (al mittente e a tutti i destinatari del messaggio ricevuto)
    * girare (forward) un messaggio a uno o più account di posta elettronica
    * rimuovere un messaggio dalla casella di posta. 
  * L’interfaccia mostra sempre la lista aggiornata dei messaggi in casella e,quando arriva un nuovo messaggio, notifica l’utente attraverso una finestra di dialogo che mostra mittente e il titolo del messaggio.

## Requisiti tecnici utilizzati

* applicazione dsviuppata in Java e basata su architettura MVC, con
Controller + viste e Model, seguendo il pattern Observer Observable.
Non vi è comunicazione diretta tra viste e model: ogni tipo di
comunicazione tra questi due livelli deve essere mediato dal controller o
supportata dal pattern Observer Observable.

* L’applicazione parallelizza le attività che non necessitano di esecuzione
sequenziale e gestisce gli eventuali problemi di accesso a risorse in mutua
esclusione. In particolare, i client e il server di mail sono thread distinti e
la creazione/gestione dei messaggi avviene in parallelo alla ricezione di altri
messaggi

* L’applicazione è distribuita (i mail client e il server devono stare tutti
su JVM distinte) attraverso l’uso di RMI o Socket Java.

* L'interfaccia è implementata utilizzando il linguaggio Java e in particolare
SWING e Thread java.

