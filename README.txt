CINEMAX
=======

Applicazione Java per la gestione di un cinema.

REQUISITI
---------
Per eseguire il programma è necessario avere Java installato sul computer.

È consigliato Java 17 o una versione successiva compatibile.

Per verificare che Java sia installato, aprire il terminale e digitare:

java -version


STRUTTURA DEL PROGRAMMA
-----------------------
La cartella del programma deve mantenere la seguente struttura:

Cinemax/
│
├── Cinemax.jar
│
├── data/
│   └── proiezioni.csv
│
└── lib/
    └── jbcrypt-0.4.jar


IMPORTANTE
----------
Non spostare Cinemax.jar separatamente dalle cartelle "data" e "lib".

Il programma utilizza:

- la cartella "data" per i file utilizzati dall'applicazione;
- la cartella "lib" per la libreria BCrypt necessaria alla gestione delle password.


AVVIO DEL PROGRAMMA
-------------------
1. Aprire un terminale nella cartella contenente Cinemax.jar.

2. Eseguire il comando:

java -jar Cinemax.jar

3. Il programma verrà avviato nel terminale.


WINDOWS
-------
Su Windows è possibile:

1. Aprire la cartella Cinemax.
2. Fare clic sulla barra dell'indirizzo della cartella.
3. Digitare:

powershell

4. Premere Invio.

5. Nel terminale appena aperto eseguire:

java -jar Cinemax.jar


INSTALLAZIONE DI JAVA
---------------------
Se il comando:

java -version

non viene riconosciuto, è necessario installare Java prima di eseguire il programma.

Dopo l'installazione di Java, chiudere e riaprire il terminale e riprovare:

java -version

quindi:

java -jar Cinemax.jar


AUTORI
------
Progetto realizzato per il Laboratorio A, sede di Varese.
Per merito di: Gentile Giorgio, Donato Edoardo, Fejzaj Cristina
