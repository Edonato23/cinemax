package cinemax;

import java.time.format.DateTimeFormatter;

public final class Costanti {
    private Costanti() {
        // Costruttore privato per impedire l'instanziazione della classe
    }

    public static final String SEPARATORE_CSV = "§";
    public static final DateTimeFormatter FORMATTATORE_DATA_ORA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
}
