# Giorno 3 — Java Records & Modellazione Dominio

> **Durata:** 4 ore · **Obiettivo:** Rappresentare le entità del dominio in modo pulito, immutabile e senza codice ripetitivo, sfruttando i Java Records e le Enum.

---

## Indice

1. [Il Problema del Codice Boilerplate](#1-il-problema-del-codice-boilerplate)
2. [Java Records — Introduzione](#2-java-records--introduzione)
3. [Costruttore Canonico e Validazione](#3-costruttore-canonico-e-validazione)
4. [Enum in Java](#4-enum-in-java)
5. [Modellazione dell'Entità di Dominio con Record](#5-modellazione-dellentità-di-dominio-con-record)
6. [Record vs Classe Tradizionale — Confronto](#6-record-vs-classe-tradizionale--confronto)
7. [Quando Usare Record e Quando Usare Classi](#7-quando-usare-record-e-quando-usare-classi)
8. [LocalDateTime — Gestione Date e Ore](#8-localdatetime--gestione-date-e-ore)
9. [CommandLineRunner — Eseguire Codice all'Avvio](#9-commandlinerunner--eseguire-codice-allavvio)
10. [Esercizio — Entità Menu Ristorante](#10-esercizio--entità-menu-ristorante)
11. [Riepilogo](#11-riepilogo)

---

## 1. Il Problema del Codice Boilerplate

### Teoria

Il **codice boilerplate** è tutto quel codice ripetitivo e meccanico che uno sviluppatore scrive non perché contenga logica di business, ma solo perché il linguaggio lo richiede. In Java, ogni classe che rappresenta dati (un'entità, un DTO, un Value Object) richiede tradizionalmente di scrivere manualmente:

- Tutti i **campi privati**
- Un **costruttore** con tutti i parametri
- Un **getter** per ogni campo
- Il metodo `toString()` per la stampa leggibile
- Il metodo `equals()` per il confronto tra oggetti
- Il metodo `hashCode()` per l'uso in collezioni (HashMap, HashSet)

Questo rappresenta un problema reale perché:

- Aumenta la quantità di codice da leggere e mantenere
- Introduce più punti in cui possono nascondersi bug
- Oscura la vera intenzione del codice: rappresentare dati
- Una modifica a un campo richiede di aggiornare costruttore, getter, toString, equals, hashCode

### Esempio pratico di codice

Una semplice classe `Run` con 6 campi, scritta nel modo tradizionale, occupa **oltre 80 righe**:

```java
import java.time.LocalDateTime;

public class Run {
    private Integer id;
    private String title;
    private LocalDateTime startedOn;
    private LocalDateTime completedOn;
    private Integer miles;
    private Location location;

    // Costruttore
    public Run(Integer id, String title, LocalDateTime startedOn,
               LocalDateTime completedOn, Integer miles, Location location) {
        this.id = id;
        this.title = title;
        this.startedOn = startedOn;
        this.completedOn = completedOn;
        this.miles = miles;
        this.location = location;
    }

    // Getter
    public Integer getId()               { return id; }
    public String getTitle()             { return title; }
    public LocalDateTime getStartedOn()  { return startedOn; }
    public LocalDateTime getCompletedOn(){ return completedOn; }
    public Integer getMiles()            { return miles; }
    public Location getLocation()        { return location; }

    // Setter
    public void setId(Integer id)                    { this.id = id; }
    public void setTitle(String title)               { this.title = title; }
    public void setStartedOn(LocalDateTime startedOn){ this.startedOn = startedOn; }
    public void setCompletedOn(LocalDateTime completedOn){ this.completedOn = completedOn; }
    public void setMiles(Integer miles)              { this.miles = miles; }
    public void setLocation(Location location)       { this.location = location; }

    // toString
    @Override
    public String toString() {
        return "Run{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startedOn=" + startedOn +
                ", completedOn=" + completedOn +
                ", miles=" + miles +
                ", location=" + location +
                '}';
    }

    // equals e hashCode (omessi per brevità, ma servono)
}
```

> ⚠️ **Problema:** 80+ righe di codice meccanico per descrivere 6 campi. Se aggiungessi un settimo campo, dovresti aggiornare costruttore, getter, setter, toString, equals e hashCode.

---

## 2. Java Records — Introduzione

### Teoria

I **Java Records** sono una feature introdotta come preview in **Java 14** e stabilizzata definitivamente in **Java 16**. Sono una forma specializzata di classe progettata specificamente per rappresentare **dati immutabili** in modo conciso.

La parola chiave `record` istruisce il compilatore Java a generare automaticamente:

| Elemento generato automaticamente | Descrizione |
|---|---|
| **Costruttore canonico** | Prende tutti i campi come parametri |
| **Getter impliciti** | Metodi con lo stesso nome del campo (senza prefisso `get`) |
| `toString()` | Rappresentazione testuale leggibile |
| `equals()` | Confronto basato sul valore di tutti i campi |
| `hashCode()` | Hash basato sul valore di tutti i campi |

**Caratteristiche fondamentali:**

- I campi sono implicitamente `private final` → **immutabilità garantita**
- Non esistono setter: un record non può essere modificato dopo la creazione
- I record sono implicitamente `final`: non possono essere estesi
- Possono implementare interfacce
- Possono avere metodi aggiuntivi

### Esempio pratico di codice

La stessa entità `Run` di 80 righe, riscritta come Record:

```java
import java.time.LocalDateTime;

public record Run(
    Integer id,
    String title,
    LocalDateTime startedOn,
    LocalDateTime completedOn,
    Integer miles,
    Location location
) {}
```

**Solo 8 righe.** Il compilatore genera tutto il resto automaticamente.

Utilizzo del record:

```java
// Creazione (usa il costruttore canonico generato automaticamente)
Run run = new Run(
    1,
    "Corsa mattutina",
    LocalDateTime.now(),
    LocalDateTime.now().plusHours(1),
    5,
    Location.OUTDOOR
);

// Accesso ai dati: getter impliciti SENZA prefisso "get"
System.out.println(run.id());          // 1       (NON run.getId())
System.out.println(run.title());       // "Corsa mattutina"
System.out.println(run.miles());       // 5
System.out.println(run.location());    // OUTDOOR

// toString() automatico
System.out.println(run);
// Output: Run[id=1, title=Corsa mattutina, startedOn=..., completedOn=..., miles=5, location=OUTDOOR]

// equals() basato sui valori
Run run2 = new Run(1, "Corsa mattutina", run.startedOn(), run.completedOn(), 5, Location.OUTDOOR);
System.out.println(run.equals(run2));  // true
```

> 💡 **Nota chiave:** Con i Record si usa `run.title()` invece di `run.getTitle()`. Il getter ha lo stesso nome del campo.

---

## 3. Costruttore Canonico e Validazione

### Teoria

Il **costruttore canonico** è il costruttore automaticamente generato dal Record che accetta tutti i campi come parametri. È possibile **sovrascrivere** questo costruttore per aggiungere logica di validazione, senza dover riscrivere l'assegnazione dei campi.

Nella forma compatta (compact constructor), non si scrivono i parametri né le assegnazioni: il compilatore le gestisce automaticamente. Si scrive solo la logica di validazione.

### Esempio pratico di codice

Aggiungere una validazione al record `Run` per impedire che le miglia siano negative:

```java
import java.time.LocalDateTime;

public record Run(
    Integer id,
    String title,
    LocalDateTime startedOn,
    LocalDateTime completedOn,
    Integer miles,
    Location location
) {
    // Costruttore canonico compatto per la validazione
    // Non si scrivono i parametri: il compilatore li aggiunge automaticamente
    public Run {
        if (miles < 0) {
            throw new IllegalArgumentException("Miles cannot be negative: " + miles);
        }
        if (completedOn.isBefore(startedOn)) {
            throw new IllegalArgumentException("completedOn must be after startedOn");
        }
    }
}
```

Comportamento con valori non validi:

```java
// Questo funziona correttamente
Run valida = new Run(1, "Corsa", LocalDateTime.now(),
                     LocalDateTime.now().plusHours(1), 10, Location.OUTDOOR);

// Questo lancia IllegalArgumentException: "Miles cannot be negative: -5"
Run nonValida = new Run(2, "Errore", LocalDateTime.now(),
                        LocalDateTime.now().plusHours(1), -5, Location.OUTDOOR);
```

> **Best practice:** Inserire la validazione nel costruttore canonico garantisce che sia **impossibile creare un oggetto in uno stato non valido**. Questo è uno dei principi fondamentali della programmazione orientata agli oggetti.

---

## 4. Enum in Java

### Teoria

Un **Enum** (enumerazione) è un tipo speciale di classe in Java che rappresenta un insieme **fisso e finito** di costanti nominate. È usato quando un campo può assumere solo un numero limitato di valori predefiniti.

Vantaggi degli Enum rispetto alle stringhe o agli interi:

- **Type safety**: il compilatore impedisce valori non validi a tempo di compilazione
- **Leggibilità**: `Location.OUTDOOR` è molto più chiaro di `"outdoor"` o `1`
- **Refactoring sicuro**: rinominare una costante in un IDE aggiorna tutto il codice
- **Completamento automatico** nell'IDE: l'IDE suggerisce solo i valori possibili

### Esempio pratico di codice

```java
// Definizione dell'Enum
public enum Location {
    INDOOR,
    OUTDOOR
}
```

```java
// Utilizzo
Location dove = Location.OUTDOOR;

// Switch expression con Enum (Java 14+)
String messaggio = switch (dove) {
    case INDOOR  -> "Corsa in palestra 🏋️";
    case OUTDOOR -> "Corsa all'aperto 🏃";
};

System.out.println(messaggio); // "Corsa all'aperto 🏃"

// Confronto
if (dove == Location.OUTDOOR) {
    System.out.println("Porta gli occhiali da sole!");
}

// Iterare su tutti i valori
for (Location loc : Location.values()) {
    System.out.println(loc); // INDOOR, OUTDOOR
}

// Da String a Enum
Location fromString = Location.valueOf("INDOOR"); // Location.INDOOR

// Nome come stringa
System.out.println(Location.OUTDOOR.name()); // "OUTDOOR"
```

Un Enum può avere anche campi e metodi:

```java
public enum Location {
    INDOOR("Al chiuso"),
    OUTDOOR("All'aperto");

    private final String descrizione;

    // Costruttore dell'Enum (sempre privato)
    Location(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }
}

// Utilizzo
System.out.println(Location.OUTDOOR.getDescrizione()); // "All'aperto"
```

---

## 5. Modellazione dell'Entità di Dominio con Record

### Teoria

La **modellazione del dominio** consiste nel rappresentare nel codice le entità reali del problema che si vuole risolvere. In un'applicazione per il monitoraggio delle corse, l'entità centrale è `Run` (una corsa).

Una buona entità di dominio:

- Ha un **identificatore univoco** (`id`)
- Ha **attributi descrittivi** che la caratterizzano (titolo, chilometri)
- Ha **vincoli temporali** (quando è iniziata, quando è finita)
- Ha attributi **tipizzati correttamente** (Enum per categorie, LocalDateTime per date)
- **Non può essere creata in uno stato non valido** (validazione nel costruttore)

### Esempio pratico di codice

L'entità `Run` completa, pronta per essere usata nell'applicazione:

```java
// Location.java
public enum Location {
    INDOOR,
    OUTDOOR
}
```

```java
// Run.java
import java.time.LocalDateTime;

public record Run(
    Integer id,
    String title,
    LocalDateTime startedOn,
    LocalDateTime completedOn,
    Integer miles,
    Location location
) {
    // Validazione nel costruttore canonico compatto
    public Run {
        if (miles < 0) {
            throw new IllegalArgumentException("Miles cannot be negative");
        }
        if (completedOn.isBefore(startedOn)) {
            throw new IllegalArgumentException("completedOn must be after startedOn");
        }
    }
}
```

Creazione di alcune istanze di esempio:

```java
import java.time.LocalDateTime;

// Corsa outdoor di stamattina
Run corsaMattina = new Run(
    1,
    "Corsa al parco",
    LocalDateTime.of(2024, 3, 15, 7, 0),   // 15/03/2024 alle 07:00
    LocalDateTime.of(2024, 3, 15, 8, 30),   // 15/03/2024 alle 08:30
    10,
    Location.OUTDOOR
);

// Corsa indoor in palestra
Run corsaPalestra = new Run(
    2,
    "Tapis roulant",
    LocalDateTime.of(2024, 3, 16, 18, 0),
    LocalDateTime.of(2024, 3, 16, 19, 0),
    8,
    Location.INDOOR
);

// Stampa (toString automatico del Record)
System.out.println(corsaMattina);
// Run[id=1, title=Corsa al parco, startedOn=2024-03-15T07:00, completedOn=2024-03-15T08:30, miles=10, location=OUTDOOR]

// Accesso ai campi
System.out.println("Titolo: " + corsaMattina.title());
System.out.println("Miglia: " + corsaMattina.miles());
System.out.println("Dove:   " + corsaMattina.location());
```

---

## 6. Record vs Classe Tradizionale — Confronto

### Teoria

Il confronto diretto mostra perché i Record sono preferibili per la modellazione di dati puri.

| Caratteristica | Classe tradizionale | Record |
|---|---|---|
| **Righe di codice** | ~80 righe | ~8 righe |
| **Boilerplate** | Manuale (costruttore, getter, setter, toString, equals, hashCode) | Generato automaticamente |
| **Immutabilità** | Opzionale (richiede sforzo) | Garantita per default |
| **Modificabilità** | Sì (tramite setter) | No (nessun setter) |
| **Estensibilità** | Sì (può estendere classi) | No (final implicito) |
| **Uso con JPA** |  Compatibile con `@Entity` | ❌ Non compatibile con `@Entity` |
| **Getter** | `run.getTitle()` | `run.title()` |
| **Uso ideale** | Entità JPA, oggetti mutabili | DTO, Value Object, dati di sola lettura |

### Esempio pratico di codice

```java
//  CLASSE TRADIZIONALE — necessaria per JPA (@Entity)
@Entity
@Table(name = "runs")
public class Run {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    // ... costruttore no-arg obbligatorio per JPA, getter, setter, ecc.
}

//  RECORD — ideale per DTO o oggetti in memoria
public record RunResponse(Integer id, String title, Integer miles) {}

// Utilizzo del Record come DTO di risposta
RunResponse response = new RunResponse(run.getId(), run.getTitle(), run.getMiles());
```

---

## 7. Quando Usare Record e Quando Usare Classi

### Teoria

La scelta tra Record e Classe dipende dal **ruolo** che l'oggetto ha nell'architettura.

**Usa un Record quando:**

- L'oggetto è un **portatore di dati puro** (DTO, Value Object, Response)
- I dati non devono cambiare dopo la creazione (**immutabilità desiderata**)
- Vuoi la massima concisione
- L'oggetto viene **trasportato** tra i layer (es. dal Service al Controller)
- Non hai bisogno di ereditarietà

**Usa una Classe quando:**

- L'oggetto deve essere **mutabile** nel tempo (es. entità che viene aggiornata)
- Hai bisogno di **ereditarietà** (`extends`)
- Stai creando un'**entità JPA** (`@Entity`): JPA richiede un costruttore no-arg e setter, incompatibili con i Record
- Hai logica di business complessa che richiede stato interno modificabile

> **Regola pratica nel corso:** usiamo **Record** per modellare le entità in memoria (Giorno 3 e 4), e le convertiamo in **Classi con `@Entity`** quando integriamo JPA (Giorno 5).

---

## 8. LocalDateTime — Gestione Date e Ore

### Teoria

`LocalDateTime` è la classe Java (introdotta in Java 8, package `java.time`) per rappresentare una data e ora senza fuso orario. È la scelta corretta per registrare quando una corsa è iniziata e terminata.

La classe `ChronoUnit` fornisce le unità di misura del tempo (minuti, ore, giorni, ecc.) e si usa spesso per calcoli temporali.

### Esempio pratico di codice

```java
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

// Data e ora correnti
LocalDateTime ora = LocalDateTime.now();
System.out.println(ora); // es. 2024-03-15T14:30:00

// Data e ora specifiche
LocalDateTime dataSpecifica = LocalDateTime.of(2024, 3, 15, 7, 0, 0);

// Operazioni aritmetiche
LocalDateTime traUnOra    = ora.plusHours(1);
LocalDateTime ieriStessOra = ora.minusDays(1);
LocalDateTime tra30Min    = ora.plus(30, ChronoUnit.MINUTES);

// Calcolo della durata tra due istanti
LocalDateTime inizio = LocalDateTime.of(2024, 3, 15, 7, 0);
LocalDateTime fine   = LocalDateTime.of(2024, 3, 15, 8, 30);

long minutiTotali = ChronoUnit.MINUTES.between(inizio, fine); // 90
long oreTotali    = ChronoUnit.HOURS.between(inizio, fine);   // 1

System.out.println("Durata: " + minutiTotali + " minuti");
System.out.println("Durata: " + oreTotali + " ore");

// Confronti
boolean fineDopoDiInizio = fine.isAfter(inizio);   // true
boolean inizioPrimaDiFine = inizio.isBefore(fine); // true
```

Esempio di creazione di un `Run` usando `LocalDateTime.now()`:

```java
// Corsa iniziata adesso, terminata fra 1 ora
Run corsaOra = new Run(
    3,
    "Corsa serale",
    LocalDateTime.now(),
    LocalDateTime.now().plusHours(1),
    7,
    Location.OUTDOOR
);
```

---

## 9. CommandLineRunner — Eseguire Codice all'Avvio

### Teoria

`CommandLineRunner` è un'interfaccia funzionale di Spring Boot che permette di **eseguire codice subito dopo l'avvio dell'applicazione**. È utile per:

- Testare che le entità siano create correttamente
- Pre-caricare dati di esempio in memoria
- Verificare la configurazione prima di aprire il server alle richieste

Si usa come `@Bean` in una classe `@Configuration` o direttamente nella classe principale `@SpringBootApplication`. Il metodo `run()` viene eseguito una sola volta, subito dopo che tutti i Bean Spring sono stati inizializzati e il server Tomcat è avviato.

### Esempio pratico di codice

```java
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.LocalDateTime;

@SpringBootApplication
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    // Questo metodo viene eseguito automaticamente all'avvio
    @Bean
    CommandLineRunner runner() {
        return args -> {

            // Creo alcune istanze di Run per verificare il modello
            Run run1 = new Run(
                1,
                "Corsa mattutina al parco",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                10,
                Location.OUTDOOR
            );

            Run run2 = new Run(
                2,
                "Tapis roulant in palestra",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(45),
                6,
                Location.INDOOR
            );

            // Stampa in console
            System.out.println("=== Test Entità Run ===");
            System.out.println(run1);
            System.out.println(run2);
            System.out.println("ID run1:       " + run1.id());
            System.out.println("Titolo run1:   " + run1.title());
            System.out.println("Location run2: " + run2.location());
        };
    }
}
```

**Output atteso in console al avvio:**

```
=== Test Entità Run ===
Run[id=1, title=Corsa mattutina al parco, startedOn=2024-03-15T14:30:00, completedOn=2024-03-15T15:30:00, miles=10, location=OUTDOOR]
Run[id=2, title=Tapis roulant in palestra, startedOn=2024-03-15T14:30:00, completedOn=2024-03-15T15:15:00, miles=6, location=INDOOR]
ID run1:       1
Titolo run1:   Corsa mattutina al parco
Location run2: INDOOR
```

> 💡 **Nota:** Navigando su `http://localhost:8080` dopo l'avvio si vedrà un errore `404 Whitelabel Error Page` — è **normale** e **atteso**, perché non abbiamo ancora definito nessun endpoint REST. Lo faremo nel Giorno 4.

---

## 10. Esercizio — Entità Menu Ristorante

### Obiettivo

Modellare le entità di un menù ristorante usando Java Records ed Enum, applicando validazioni nel costruttore canonico e testandole con un `CommandLineRunner`.

### Specifiche

1. Creare l'**Enum `Categoria`** con i valori: `PRIMO`, `SECONDO`, `DOLCE`
2. Creare il **Record `Piatto`** con i campi: `nome` (String), `descrizione` (String), `prezzo` (Double), `categoria` (Categoria)
3. Aggiungere **validazione nel costruttore canonico**: il prezzo deve essere maggiore di 0
4. Creare **3 piatti** di categorie diverse e stamparli in console tramite un `CommandLineRunner`

### Struttura del Progetto

```
src/main/java/com/esempio/ristorante/
├── RistoranteApplication.java
├── model/
│   ├── Categoria.java   ← Enum
│   └── Piatto.java      ← Record
```

### Output Atteso

```
========== MENÙ DEL GIORNO ==========
[PRIMO] Spaghetti alla Carbonara — 14,50€
   Pasta con uova, guanciale, pecorino e pepe nero

[SECONDO] Bistecca alla Fiorentina — 38,00€
   Manzo Chianina con rosmarino e olio EVO

[DOLCE] Tiramisù della casa — 8,00€
   Ricetta tradizionale con savoiardi e mascarpone

 Validazione funziona: Il prezzo deve essere maggiore di 0, ricevuto: -1.0
```

### Domande di Verifica

1. Cosa succederebbe se provassi a modificare il prezzo di un `Piatto` dopo la creazione?
2. Perché hai usato un Record invece di una Classe per `Piatto`?
3. In quale momento viene eseguito il codice dentro `CommandLineRunner`?
4. Come si chiamerebbe il getter del campo `nome` in un Record?
5. Cosa succede se crei un `Piatto` con prezzo `0.0`? E con `0.01`?

---

## 11. Riepilogo

| Argomento | Concetto chiave |
|---|---|
| **Boilerplate** | Codice meccanico e ripetitivo (costruttori, getter, toString) richiesto dalle classi tradizionali Java |
| **Java Record** | Classe specializzata per dati immutabili: genera automaticamente costruttore, getter, toString, equals, hashCode |
| **Campi final** | I campi di un Record sono `private final`: non modificabili dopo la creazione |
| **Getter impliciti** | Si usano senza prefisso `get`: `run.title()` invece di `run.getTitle()` |
| **Costruttore canonico** | Il costruttore auto-generato; può essere personalizzato in forma compatta per aggiungere validazioni |
| **Enum** | Tipo per insiemi fissi di valori; garantisce type safety e leggibilità (`Location.OUTDOOR`) |
| **LocalDateTime** | Classe Java per date e ore senza fuso orario; usare `now()`, `plusHours()`, `ChronoUnit` per calcoli |
| **CommandLineRunner** | Interfaccia Spring Boot per eseguire codice all'avvio, dopo l'inizializzazione di tutti i Bean |
| **Record vs Classe** | Record per dati in memoria e DTO; Classe con `@Entity` per la persistenza JPA (Giorno 5) |

---

> **Prossimo passo — Giorno 4:** REST Controller & HTTP — come esporre le entità modellate oggi tramite endpoint HTTP accessibili con Postman, usando `@RestController`, `@GetMapping` e la Stream API per cercare dati in memoria.
