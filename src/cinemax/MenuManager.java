package cinemax;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

public class MenuManager {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Scanner pScanner;
    private final List<Utente> pUtenti = new ArrayList<>();
    private final List<Proiezione> pProiezioni = new ArrayList<>();
    private final List<Prenotazione> pPrenotazioni = new ArrayList<>();
    private int nextUserId = 1;
    private int nextProjectonId = 1;
    private int nextReservationId = 1;

    private FileManager pFileManager;

    public MenuManager() {
        pScanner = new Scanner(System.in);
        this.pFileManager = new FileManager("../../data");
        this.pCaricaDati();
        // loadDemoData();
    }

    public void Menu() {
        boolean esecuzione = true;
        pStampaTestata();

        while (esecuzione) {
            System.out.println("\nCosa vuoi fare?");
            System.out.println("1. Login");
            System.out.println("2. Registrazione");
            System.out.println("3. Continua come ospite");
            System.out.println("0. Esci");

            switch (pLeggiIntero("Scelta: ", 0, 3)) {
                case 1 -> pLogin();
                case 2 -> pRegistrazione();
                case 3 -> pGuest();
                case 0 -> esecuzione = false;
                default -> throw new IllegalStateException("Scelta non gestita.");
            }
        }

        System.out.println("\nGrazie per aver scelto Cinemax. Arrivederci!");
        pScanner.close();
    }

    private void pMostraProiezioni() {
        System.out.println("\n===== PROIEZIONI DISPONIBILI =====");
        List<Proiezione> proiezioniFuture = this.pProiezioni.stream()
                .filter(it -> it.getDataOra().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Proiezione::getDataOra))
                .toList();

        if (proiezioniFuture.isEmpty()) {
            System.out.println("Non ci sono proiezioni disponibili.");
            return;
        }

        proiezioniFuture.forEach(proiezione -> {
            System.out.println(proiezione);
            System.out.println("   Posti liberi: " + proiezione.postiDisponibiliFormattati());
        });
    }

    private void showProjectonDetails() {
        if (projectons.isEmpty()) {
            System.out.println("Non ci sono proiezioni.");
            return;
        }

        int id = pLeggiIntero("Inserisci l'ID della proiezione: ", 1, Integer.MAX_VALUE);
        Projecton projecton = findProjecton(id);
        if (projecton == null) {
            System.out.println("Proiezione non trovata.");
            return;
        }

        System.out.println("\n" + projecton);
        System.out.println("Film: " + projecton.getFilm());
        System.out.println("Data e ora: " + projecton.getDataOraFormattata());
        System.out.println("Posti totali: " + projecton.getPostiTotali());
        System.out.println("Posti disponibili: " + projecton.getPostiDisponibili());
        System.out.println("Posti liberi: " + projecton.postiDisponibiliFormattati());
    }

    private void createReservation(User user) {
        pMostraProiezioni();
        if (projectons.isEmpty()) {
            return;
        }

        int projectonId = pLeggiIntero("ID della proiezione: ", 1, Integer.MAX_VALUE);
        Projecton projecton = findProjecton(projectonId);
        if (projecton == null || !projecton.getDataOra().isAfter(LocalDateTime.now())) {
            System.out.println("Proiezione non trovata o non più disponibile.");
            return;
        }

        List<Integer> seats = pLeggiPosti(projecton);
        if (seats == null) {
            return;
        }

        try {
            projecton.occupaPosti(seats);
            Reservation reservation = new Reservation(nextReservationId++, user, projecton, seats);
            reservations.add(reservation);
            System.out.println("Prenotazione effettuata con successo!");
            System.out.println(reservation);
        } catch (IllegalArgumentException exception) {
            System.out.println("Prenotazione non effettuata: " + exception.getMessage());
        }
    }

    private void showReservations(User user) {
        System.out.println("\n===== LE MIE PRENOTAZIONI =====");
        List<Reservation> ownReservations = reservations.stream()
                .filter(reservation -> reservation.getUser().getId() == user.getId())
                .toList();
        if (ownReservations.isEmpty()) {
            System.out.println("Non hai ancora effettuato prenotazioni.");
            return;
        }
        ownReservations.forEach(reservation -> System.out.println(reservation));
    }

    private void changeReservation(User user) {
        List<Reservation> activeReservations = getActiveReservations(user);
        if (activeReservations.isEmpty()) {
            System.out.println("Non hai prenotazioni attive da modificare.");
            return;
        }

        showReservations(user);
        int reservationId = pLeggiIntero("ID della prenotazione da modificare: ", 1, Integer.MAX_VALUE);
        Reservation reservation = findOwnReservation(user, reservationId);
        if (reservation == null || !reservation.isActive()) {
            System.out.println("Prenotazione attiva non trovata.");
            return;
        }

        System.out.println("Posti attuali: " + reservation.getSeats());
        List<Integer> newSeats = pLeggiPosti(reservation.getProjecton());
        if (newSeats == null) {
            return;
        }

        try {
            reservation.changeSeats(newSeats);
            System.out.println("Prenotazione modificata con successo.");
            System.out.println(reservation);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            System.out.println("Modifica non effettuata: " + exception.getMessage());
        }
    }

    private void cancelReservation(User user) {
        List<Reservation> activeReservations = getActiveReservations(user);
        if (activeReservations.isEmpty()) {
            System.out.println("Non hai prenotazioni attive da eliminare.");
            return;
        }

        showReservations(user);
        int reservationId = pLeggiIntero("ID della prenotazione da eliminare: ", 1, Integer.MAX_VALUE);
        Reservation reservation = findOwnReservation(user, reservationId);
        if (reservation == null || !reservation.isActive()) {
            System.out.println("Prenotazione attiva non trovata.");
            return;
        }

        if (pSiNo("Confermi l'eliminazione? (s/n): ")) {
            reservation.cancel();
            System.out.println("Prenotazione eliminata. I posti sono nuovamente disponibili.");
        } else {
            System.out.println("Operazione annullata.");
        }
    }

    private void addProjecton() {
        System.out.println("\n===== NUOVA PROIEZIONE =====");
        String film = pLeggiTesto("Titolo del film: ");
        LocalDateTime dateTime = pLeggiDataFutura();
        int capacity = pLeggiIntero("Numero di posti della sala: ", 1, 1000);

        boolean sameTime = projectons.stream()
                .anyMatch(projecton -> projecton.getDataOra().equals(dateTime));
        if (sameTime) {
            System.out.println(
                    "Operazione rifiutata: il cinema può proiettare un solo film alle "
                            + dateTime.format(DATE_TIME_FORMAT) + ".");
            return;
        }

        projectons.add(new Projecton(nextProjectonId++, film, dateTime, capacity));
        System.out.println("Proiezione aggiunta con successo.");
    }

    private void removeProjecton() {
        pMostraProiezioni();
        int id = pLeggiIntero("ID della proiezione da rimuovere: ", 1, Integer.MAX_VALUE);
        Projecton projecton = findProjecton(id);
        if (projecton == null) {
            System.out.println("Proiezione non trovata.");
            return;
        }

        boolean hasActiveReservations = reservations.stream()
                .anyMatch(reservation -> reservation.isActive()
                        && reservation.getProjecton().getId() == projecton.getId());
        if (hasActiveReservations) {
            System.out.println(
                    "Non puoi rimuovere questa proiezione: esistono prenotazioni attive.");
            return;
        }

        projectons.remove(projecton);
        System.out.println("Proiezione rimossa.");
    }

    private User findUser(String username) {
        return pUtenti.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    private Projecton findProjecton(int id) {
        return projectons.stream()
                .filter(projecton -> projecton.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private Reservation findOwnReservation(User user, int id) {
        return reservations.stream()
                .filter(reservation -> reservation.getId() == id)
                .filter(reservation -> reservation.getUser().getId() == user.getId())
                .findFirst()
                .orElse(null);
    }

    private List<Reservation> getActiveReservations(User user) {
        return reservations.stream()
                .filter(reservation -> reservation.getUser().getId() == user.getId())
                .filter(Reservation::isActive)
                .toList();
    }

    // #region Metodi demo

    private void loadDemoData() {
        pUtenti.add(new Utente(
                nextUserId++,
                "Admin",
                "Cinemax",
                "admin",
                "admin123",
                "Cinemax",
                "",
                Utente.Role.PROIEZIONISTA));

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        addDemoProjecton("Interstellar", LocalDateTime.of(tomorrow, LocalTime.of(18, 0)), 30);
        addDemoProjecton("The Grand Budapest Hotel", LocalDateTime.of(tomorrow, LocalTime.of(21, 0)), 20);
    }

    private void addDemoProjecton(String film, LocalDateTime dateTime, int capacity) {
        projectons.add(new Projecton(nextProjectonId++, film, dateTime, capacity));
    }

    // #endregion

    // #region Metodi di controllo input

    private int pLeggiIntero(String istruzioni, int min, int max) {
        while (true) {

            System.out.print(istruzioni);
            String input = pScanner.nextLine().trim();

            try {
                int inputParsato = Integer.parseInt(input);

                // Controlla che l'input sia compreso tra min e max
                if (inputParsato < min || inputParsato > max) {
                    System.out.println("Inserisci un numero tra " + min + " e " + max + ".");
                    continue;
                }

                return inputParsato;
            } catch (NumberFormatException e) {
                System.out.println("Input non valido. Inserisci un numero.");
            }
        }
    }

    private String pLeggiTesto(String istruzioni) {
        while (true) {

            System.out.print(istruzioni);
            String input = pScanner.nextLine().trim();

            // Controlla che l'input sia valido (non vuoto e non solo spazi bianchi)
            if (!input.isBlank()) {
                return input;
            }
            System.out.println("Il campo non può essere vuoto.");
        }
    }

    private String pLeggiTestoOpzionale(String istruzioni) {
        System.out.print(istruzioni);
        return pScanner.nextLine().trim();
    }

    private boolean pSiNo(String istruzioni) {
        while (true) {
            String risposta = pLeggiTesto(istruzioni).toLowerCase();

            if (risposta.equals("s")
                    ||
                    risposta.equals("si")
                    ||
                    risposta.equals("sì")) {
                return true;
            }

            if (risposta.equals("n")
                    ||
                    risposta.equals("no")) {
                return false;
            }

            System.out.println("Rispondi con s oppure n.");
        }
    }

    private LocalDateTime pLeggiDataFutura() {
        while (true) {
            String input = pLeggiTesto("Data e ora (dd/MM/yyyy HH:mm): ");

            try {
                LocalDateTime data = LocalDateTime.parse(input, DATE_TIME_FORMAT);
                if (!data.isAfter(LocalDateTime.now())) {
                    System.out.println("Inserisci una data e ora future.");
                    continue;
                }

                return data;
            } catch (DateTimeParseException exception) {
                System.out.println("Formato non valido. Esempio: 25/12/2026 20:30");
            }
        }
    }

    private List<Integer> pLeggiPosti(Proiezione proiezione) {
        System.out.println("Posti liberi: " + proiezione.postiDisponibiliFormattati());
        String input = pLeggiTesto(
                "Inserisci i numeri dei posti separati da virgola (es. 1,2,3), oppure 0 per annullare: ");
        if (input.equals("0")) {
            System.out.println("Operazione annullata.");
            return null;
        }

        try {
            List<Integer> seats = Arrays.stream(input.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .toList();
            if (seats.isEmpty()) {
                throw new IllegalArgumentException("Seleziona almeno un posto.");
            }
            return seats;
        } catch (NumberFormatException exception) {
            System.out.println("Formato non valido. Usa numeri separati da virgola.");
            return null;
        }
    }

    private boolean pDataValida(String dataStringa) {
        try {
            LocalDate.parse(dataStringa, DATE_FORMAT);
            return true;
        } catch (DateTimeParseException exception) {
            return false;
        }
    }

    private String pCifraPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    private boolean pControllaPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }

    // #endregion

    // #region Metodi privati

    private void pCaricaDati() {
        this.pUtenti.addAll(this.pFileManager.carica("utenti.csv", null));

        this.pProiezioni.addAll(this.pFileManager.carica("proiezioni.csv", Proiezione::fromCSV));

        this.pPrenotazioni.addAll(this.pFileManager.carica("prenotazioni.csv", null));
    }

    private void pStampaTestata() {
        System.out.println("========================================");
        System.out.println("|          BENVENUTO AL CINEMAX        |");
        System.out.println("|     Cinema monosala con 200 posti    |");
        System.out.println("========================================");
        // System.out.println("\nAccesso demo proiezionista: admin / admin123");
    }

    private void pLogin() {
        System.out.println("\n===== LOGIN =====");
        String username = pLeggiTesto("Username: ");
        String password = pLeggiTesto("Password: ");

        Utente utente = pUtenti.stream()
                .filter(it -> it.getUsername().equalsIgnoreCase(username))
                .filter(it -> pControllaPassword(password, it.getPassword()))
                .findFirst()
                .orElse(null);

        if (utente == null) {
            System.out.println("Username o password non validi.");
            return;
        }

        System.out.println("\nAccesso effettuato. Benvenuto/a, " + utente.getNomeCompleto() + "!");

        switch (utente.getRuolo()) {
            case Utente.Ruolo.CLIENTE -> pMenuCliente(utente);
            case Utente.Ruolo.PROIEZIONISTA -> pMenuProiezionista(utente);
            case Utente.Ruolo.BIGLIETTAIO -> pMenuBigliettaio(utente);
            default -> throw new IllegalStateException("Ruolo non gestito.");
        }
    }

    private void pRegistrazione() {
        System.out.println("\n===== REGISTRAZIONE =====");
        String nome = pLeggiTesto("Nome: ");
        String cognome = pLeggiTesto("Cognome: ");
        String username;

        while (true) {
            username = pLeggiTesto("Username: ");
            if (findUser(username) == null) {
                break;
            }
            System.out.println("Username già in uso. Scegline un altro.");
        }

        String password = pLeggiTesto("Password: ");
        String dataNascita = pLeggiTestoOpzionale("Data di nascita (dd/MM/yyyy, invio per saltare): ");
        if (!dataNascita.isBlank() && !pDataValida(dataNascita)) {
            System.out.println("Formato data non valido: la data verrà lasciata vuota.");
            dataNascita = "";
        }
        String domicilio = pLeggiTesto("Domicilio: ");

        int ruolo = this.pLeggiIntero(
                "Che ruolo vuoi registrare?\n0. Cliente\n1. Proiezionista\n2. Bigliettaio\nScelta: ",
                0, 2);

        Utente utente = new Utente(
                nextUserId++,
                nome,
                cognome,
                username,
                this.pCifraPassword(password),
                domicilio,
                dataNascita,
                ruolo);

        pUtenti.add(utente);
        this.pFileManager.salva("utenti.csv",
                this.pUtenti,
                null,
                "id;nome;cognome;username;password;domicilio;dataNascita;ruolo");

        System.out.println("Registrazione completata. Ora puoi effettuare il login.");
    }

    // #region Menu

    private void pGuest() {
        boolean open = true;
        while (open) {
            System.out.println("\n===== AREA OSPITE =====");
            System.out.println("1. Cerca proiezioni");
            System.out.println("2. Visualizza dettagli proiezione");
            System.out.println("3. Registrati");
            System.out.println("0. Torna al menu principale");

            switch (pLeggiIntero("Scelta: ", 0, 3)) {
                case 1 -> pMostraProiezioni();
                case 2 -> showProjectonDetails();
                case 3 -> pRegistrazione();
                case 0 -> open = false;
                default -> throw new IllegalStateException("Scelta non gestita.");
            }
        }
    }

    private void pMenuCliente(Utente utente) {
        boolean open = true;

        while (open) {
            System.out.println("\n===== AREA CLIENTE =====");
            System.out.println("1. Visualizza proiezioni");
            System.out.println("2. Effettua prenotazione");
            System.out.println("3. Visualizza le mie prenotazioni");
            System.out.println("4. Modifica prenotazione");
            System.out.println("5. Elimina prenotazione");
            System.out.println("0. Logout");

            switch (pLeggiIntero("Scelta: ", 0, 5)) {
                case 1 -> pMostraProiezioni();
                case 2 -> createReservation(utente);
                case 3 -> showReservations(utente);
                case 4 -> changeReservation(utente);
                case 5 -> cancelReservation(utente);
                case 0 -> open = false;
                default -> throw new IllegalStateException("Scelta non gestita.");
            }
        }
    }

    private void pMenuProiezionista(Utente utente) {
        boolean open = true;
        while (open) {
            System.out.println("\n===== AREA PROIEZIONISTA =====");
            System.out.println("1. Visualizza proiezioni");
            System.out.println("2. Aggiungi proiezione");
            System.out.println("3. Rimuovi proiezione");
            System.out.println("0. Logout");

            switch (pLeggiIntero("Scelta: ", 0, 3)) {
                case 1 -> pMostraProiezioni();
                case 2 -> addProjecton();
                case 3 -> removeProjecton();
                case 0 -> open = false;
                default -> throw new IllegalStateException("Scelta non gestita.");
            }
        }
    }

    private void pMenuBigliettaio(Utente utente) {
        boolean open = true;

        while (open) {
            System.out.println("\n===== AREA BIGLIETTAIO =====");
            System.out.println("1. Cerca prenotazione");
            System.out.println("2. Visualizza prenotazioni");
            System.out.println("0. Logout");

            switch (pLeggiIntero("Scelta: ", 0, 2)) {
                case 1 -> pCercaPrenotazione();
                case 2 -> pMostraPrenotazioni();
                case 0 -> open = false;
                default -> throw new IllegalStateException("Scelta non gestita.");
            }
        }
    }

    // #endregion

    // #endregion
}