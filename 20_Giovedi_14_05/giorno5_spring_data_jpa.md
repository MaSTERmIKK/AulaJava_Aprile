# Giorno 5 — Spring Data JPA & Persistenza DB

> **Obiettivo:** Integrare Spring Data JPA per salvare i dati su database, convertire il modello `Run` in un'entità persistente e implementare il CRUD completo con H2.

---

## Indice

1. [JPA & Hibernate — Introduzione](#1-jpa--hibernate--introduzione)
2. [@Entity & Configurazione Tabella](#2-entity--configurazione-tabella)
3. [JpaRepository — CRUD Automatico](#3-jparepository--crud-automatico)
4. [Relazioni tra Entità](#4-relazioni-tra-entità)
5. [Esercizio — CRUD Completo con DB](#5-esercizio--crud-completo-con-db)

---

## 1. JPA & Hibernate — Introduzione

### Teoria

#### Il problema senza persistenza

Fino al Giorno 4, i dati dell'applicazione erano memorizzati in una **lista in memoria** (in-memory list). Questo significa che ogni volta che l'applicazione viene riavviata, tutti i dati vengono persi. Non è accettabile per un'applicazione reale.

La soluzione è **salvare i dati su un database relazionale** (come H2, PostgreSQL o MySQL). Per farlo, Java mette a disposizione uno standard chiamato **JPA**.

---

#### ORM — Object-Relational Mapping

Un database relazionale organizza i dati in **tabelle** con righe e colonne. Java organizza i dati in **oggetti** con campi e metodi. Queste due rappresentazioni sono strutturalmente diverse: è il cosiddetto problema dell'**impedance mismatch** (disallineamento di paradigma).

L'**ORM (Object-Relational Mapping)** risolve questo problema automaticamente: si occupa di tradurre gli oggetti Java in righe di tabella, e viceversa.

```
Java Object                          Tabella DB
──────────────────────               ──────────────────────────────────
Run {                                | id | title | miles | location |
  id: 1                    ←──→     |  1 | "5K"  |  3.1  | OUTDOOR  |
  title: "5K"                        |  2 | "10K" |  6.2  | INDOOR   |
  miles: 3.1                         └──────────────────────────────────
  location: OUTDOOR
}
```

---

#### JPA vs Hibernate

| Concetto   | Descrizione                                                                   |
|------------|-------------------------------------------------------------------------------|
| **JPA**    | *Java Persistence API* — è una **specifica** (un insieme di interfacce e regole) definita da Jakarta EE |
| **Hibernate** | È l'**implementazione** più popolare di JPA. Fa il lavoro vero: genera le query SQL, gestisce la cache, ecc. |

> 💡 **Analogia:** JPA è come la norma ISO per le prese elettriche (definisce gli standard), Hibernate è il produttore che costruisce le prese fisiche. Spring Data JPA aggiunge un ulteriore livello di astrazione sopra Hibernate, rendendo tutto ancora più semplice.

---

#### Aggiungere le dipendenze — `pom.xml`

Per usare Spring Data JPA con il database H2 (embedded, ideale per sviluppo e test), aggiungi queste dipendenze al file `pom.xml`:

```xml
<!-- Spring Data JPA: include Hibernate come implementazione -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-data-jpa-test</artifactId>
   <scope>test</scope>
</dependency>

<!-- H2: database embedded in memoria (nessuna installazione richiesta) -->

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-h2console</artifactId>
</dependency>

<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

> 📝 `scope>runtime</scope>` significa che H2 è disponibile solo a runtime, non durante la compilazione. In produzione sostituiresti H2 con PostgreSQL o MySQL.

---

#### Configurazione in `application.properties`

Dopo aver aggiunto le dipendenze, configura JPA e H2 nel file `src/main/resources/application.properties`:

```properties
# ── Datasource H2 ────────────────────────────────────────────────────────────
# URL per database H2 in memoria (i dati vengono persi al riavvio)
spring.datasource.url=jdbc:h2:mem:rundb

# Driver JDBC per H2
spring.datasource.driver-class-name=org.h2.Driver

# Credenziali (default H2)
spring.datasource.username=sa
spring.datasource.password=

# ── JPA / Hibernate ──────────────────────────────────────────────────────────
# Dialetto SQL da usare (H2Dialect per il database H2)
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# DDL auto: crea le tabelle allo startup (drop-and-create ad ogni avvio)
# Valori possibili: none | validate | update | create | create-drop
spring.jpa.hibernate.ddl-auto=create-drop

# Mostra le query SQL generate da Hibernate nella console
spring.jpa.show-sql=true

# Formatta le query SQL per renderle leggibili
spring.jpa.properties.hibernate.format_sql=true

# ── H2 Console ───────────────────────────────────────────────────────────────
# Abilita la console web di H2 (accessibile su /h2-console)
spring.h2.console.enabled=true
```

---

#### H2 Console — interfaccia web

H2 include una console web integrata che permette di interrogare il database direttamente dal browser, utile per verificare che le tabelle vengano create correttamente e che i dati vengano persistiti.

**Come accedere:**

1. Avvia l'applicazione
2. Apri il browser su: `http://localhost:8080/h2-console`
3. Inserisci i dati di connessione:
   - **JDBC URL:** `jdbc:h2:mem:rundb`
   - **User Name:** `sa`
   - **Password:** *(lascia vuoto)*
4. Clicca **Connect**

Nella console potrai eseguire query SQL come:

```sql
SELECT * FROM runs;
```

---

## 2. @Entity & Configurazione Tabella

### Teoria

#### Perché non possiamo usare un `record` con JPA

Nel Giorno 3 abbiamo modellato `Run` come un **Java Record**. I record sono immutabili per design: tutti i campi sono `final` e non esiste un costruttore senza argomenti.

JPA richiede invece:

- Un **costruttore senza argomenti** (no-arg constructor), per poter istanziare l'oggetto prima di popolare i campi
- La possibilità di **modificare i campi** tramite setter, per mappare i valori letti dal database

Per questo motivo, **le entità JPA devono essere classi ordinarie**, non record.

---

#### L'annotazione `@Entity`

`@Entity` è l'annotazione principale di JPA: segnala a Hibernate che questa classe Java deve essere mappata su una tabella del database.

```java
import jakarta.persistence.Entity;

@Entity
public class Run {
    // ...
}
```

Quando Spring Boot si avvia e trova una classe annotata con `@Entity`, Hibernate crea automaticamente la tabella corrispondente nel database (in base alla configurazione `ddl-auto`).

---

#### `@Id` e `@GeneratedValue`

Ogni tabella relazionale deve avere una **chiave primaria**: un campo che identifica univocamente ogni riga.

- `@Id` indica quale campo della classe è la chiave primaria
- `@GeneratedValue` specifica come viene generato il valore della chiave

```java
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;
```

Le strategie di generazione più comuni:

| Strategia         | Descrizione                                                        |
|-------------------|--------------------------------------------------------------------|
| `IDENTITY`        | Delega la generazione al DB (auto-increment). La più comune.       |
| `SEQUENCE`        | Usa una sequenza DB (supportata da PostgreSQL, Oracle).            |
| `AUTO`            | Hibernate sceglie automaticamente in base al DB.                   |
| `TABLE`           | Usa una tabella dedicata per tenere traccia degli ID (raro).       |

---

#### `@Column` — configurare le colonne

`@Column` permette di personalizzare il modo in cui un campo viene mappato sulla colonna del database. È opzionale: se omessa, JPA usa il nome del campo come nome della colonna.

```java
import jakarta.persistence.Column;

// Colonna con nome esplicito, non nullable, lunghezza massima 255
@Column(name = "title", nullable = false, length = 255)
private String title;

// Colonna con precisione decimale
@Column(name = "miles", nullable = false)
private double miles;
```

Attributi principali di `@Column`:

| Attributo    | Tipo      | Default    | Descrizione                            |
|--------------|-----------|------------|----------------------------------------|
| `name`       | `String`  | nome campo | Nome della colonna nel DB              |
| `nullable`   | `boolean` | `true`     | Se il campo può essere `NULL`          |
| `length`     | `int`     | `255`      | Lunghezza massima (per `String`)       |
| `unique`     | `boolean` | `false`    | Vincolo di unicità sulla colonna       |
| `updatable`  | `boolean` | `true`     | Se la colonna è aggiornabile           |

---

#### `@Table` — configurare la tabella

Per default, Hibernate usa il nome della classe come nome della tabella (es. `Run` → tabella `RUN`). Puoi sovrascrivere questo comportamento con `@Table`:

```java
import jakarta.persistence.Table;

@Entity
@Table(name = "runs")  // la tabella si chiamerà "runs" (lowercase, plurale)
public class Run {
    // ...
}
```

---

#### Conversione di `Run` da Record a `@Entity`

Vediamo il refactoring completo: si parte dal Record del Giorno 3 e si arriva a una classe JPA.

**Prima (Record — Giorno 3):**

```java
public record Run(
    Integer id,
    String title,
    LocalDateTime startedOn,
    LocalDateTime completedOn,
    double miles,
    Location location
) {
    // Costruttore canonico compatto con validazione
    public Run {
        if (miles < 0) throw new IllegalArgumentException("Le miglia non possono essere negative");
        if (completedOn.isBefore(startedOn)) throw new IllegalArgumentException("La data di fine deve essere dopo quella di inizio");
    }
}
```

**Dopo (@Entity — Giorno 5):**

```java
package com.example.run.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "runs")
public class Run {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "started_on", nullable = false)
    private LocalDateTime startedOn;

    @Column(name = "completed_on", nullable = false)
    private LocalDateTime completedOn;

    @Column(name = "miles", nullable = false)
    private double miles;

    @Enumerated(EnumType.STRING)  // salva il nome dell'enum come stringa ("INDOOR"/"OUTDOOR")
    @Column(name = "location", nullable = false)
    private Location location;

    // ── Costruttore no-arg richiesto da JPA ──────────────────────────────────
    protected Run() {}

    // ── Costruttore completo per uso applicativo ─────────────────────────────
    public Run(String title, LocalDateTime startedOn, LocalDateTime completedOn,
               double miles, Location location) {
        if (miles < 0) throw new IllegalArgumentException("Le miglia non possono essere negative");
        if (completedOn.isBefore(startedOn)) throw new IllegalArgumentException("La data di fine deve essere dopo quella di inizio");
        this.title = title;
        this.startedOn = startedOn;
        this.completedOn = completedOn;
        this.miles = miles;
        this.location = location;
    }

    // ── Getter ───────────────────────────────────────────────────────────────
    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public LocalDateTime getStartedOn() { return startedOn; }
    public LocalDateTime getCompletedOn() { return completedOn; }
    public double getMiles() { return miles; }
    public Location getLocation() { return location; }

    // ── Setter ───────────────────────────────────────────────────────────────
    public void setTitle(String title) { this.title = title; }
    public void setStartedOn(LocalDateTime startedOn) { this.startedOn = startedOn; }
    public void setCompletedOn(LocalDateTime completedOn) { this.completedOn = completedOn; }
    public void setMiles(double miles) { this.miles = miles; }
    public void setLocation(Location location) { this.location = location; }

    // ── toString ─────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Run{id=" + id + ", title='" + title + "', miles=" + miles + ", location=" + location + "}";
    }
}
```

> **Nota sull'`@Enumerated`:** senza questa annotazione, Hibernate salverebbe l'enum come numero intero (`0`, `1`, ...). Con `EnumType.STRING` viene salvato come testo (`"INDOOR"`, `"OUTDOOR"`), rendendo il database molto più leggibile.

---

#### Enum `Location` (invariato)

```java
package com.example.run.model;

public enum Location {
    INDOOR,
    OUTDOOR
}
```

---

### Esempio pratico — verifica della tabella su H2

Dopo aver annotato la classe con `@Entity` e avviato l'applicazione, Spring Boot crea automaticamente la tabella. Nella console troverai un output simile a:

```
Hibernate:
    create table runs (
       id integer generated by default as identity,
        completed_on timestamp(6) not null,
        miles float(53) not null,
        started_on timestamp(6) not null,
        location varchar(255) not null,
        title varchar(255) not null,
        primary key (id)
    )
```

Puoi verificare l'esistenza della tabella anche dalla H2 Console eseguendo:

```sql
SHOW TABLES;
```

---

## 3. JpaRepository — CRUD Automatico

### Teoria

#### Cos'è `JpaRepository`

`JpaRepository<T, ID>` è un'**interfaccia** fornita da Spring Data JPA. Quando la tua repository la estende, Spring genera automaticamente a runtime l'implementazione completa, senza che tu debba scrivere una riga di SQL o codice JDBC.

Basta dichiarare:

```java
public interface RunRepository extends JpaRepository<Run, Integer> {}
```

E Spring Data crea per te un bean `RunRepository` con oltre 18 metodi già implementati.

---

#### Gerarchia delle interfacce

```
Repository<T, ID>                      ← interfaccia base marker
    └── CrudRepository<T, ID>          ← CRUD base (save, findById, delete...)
            └── PagingAndSortingRepository<T, ID>   ← paginazione e ordinamento
                    └── JpaRepository<T, ID>        ← metodi JPA aggiuntivi (flush, saveAll...)
```

Nella maggior parte dei casi si usa direttamente `JpaRepository`, che include tutto.

---

#### Metodi disponibili su `JpaRepository`

**Lettura:**

| Metodo                        | Descrizione                                           |
|-------------------------------|-------------------------------------------------------|
| `findAll()`                   | Restituisce tutte le entità come `List<T>`            |
| `findAll(Sort sort)`          | Restituisce tutte le entità ordinate                  |
| `findAll(Pageable pageable)`  | Restituisce una pagina di entità                      |
| `findById(ID id)`             | Restituisce `Optional<T>` per l'ID dato              |
| `existsById(ID id)`           | Restituisce `boolean`                                 |
| `count()`                     | Restituisce il numero totale di entità                |

**Scrittura:**

| Metodo                        | Descrizione                                           |
|-------------------------------|-------------------------------------------------------|
| `save(T entity)`              | Inserisce o aggiorna l'entità (upsert)                |
| `saveAll(Iterable<T> entities)`| Salva una lista di entità                            |
| `saveAndFlush(T entity)`      | Salva e forza il flush immediato su DB                |

**Cancellazione:**

| Metodo                        | Descrizione                                           |
|-------------------------------|-------------------------------------------------------|
| `deleteById(ID id)`           | Elimina l'entità con l'ID dato                       |
| `delete(T entity)`            | Elimina l'entità passata come oggetto                 |
| `deleteAll()`                 | Elimina tutte le entità della tabella                 |

---

#### Query derivate dal nome del metodo

Spring Data JPA permette di creare query **derivandole direttamente dal nome del metodo**, senza scrivere SQL. Il nome del metodo viene analizzato e tradotto in una query JPQL automaticamente.

**Sintassi:** `findBy<Campo><Condizione>(...)`

```java
public interface RunRepository extends JpaRepository<Run, Integer> {

    // Trova tutte le corse con un titolo specifico
    List<Run> findByTitle(String title);

    // Trova tutte le corse con miglia maggiori di una soglia
    List<Run> findByMilesGreaterThan(double miles);

    // Trova corse per location
    List<Run> findByLocation(Location location);

    // Combina condizioni con AND
    List<Run> findByLocationAndMilesGreaterThan(Location location, double miles);

    // Trova la prima corsa per titolo (Optional per sicurezza)
    Optional<Run> findFirstByTitle(String title);

    // Verifica se esiste una corsa con un certo titolo
    boolean existsByTitle(String title);
}
```

**Parole chiave supportate nel nome del metodo:**

| Keyword              | Esempio                             | Equivalente SQL                    |
|----------------------|-------------------------------------|------------------------------------|
| `And`                | `findByTitleAndLocation`            | `WHERE title = ? AND location = ?` |
| `Or`                 | `findByTitleOrLocation`             | `WHERE title = ? OR location = ?`  |
| `GreaterThan`        | `findByMilesGreaterThan`            | `WHERE miles > ?`                  |
| `LessThan`           | `findByMilesLessThan`               | `WHERE miles < ?`                  |
| `Between`            | `findByMilesBetween`                | `WHERE miles BETWEEN ? AND ?`      |
| `Like`               | `findByTitleLike`                   | `WHERE title LIKE ?`               |
| `OrderBy`            | `findByLocationOrderByMilesDesc`    | `ORDER BY miles DESC`              |

---

#### `@Query` — query JPQL personalizzate

Quando il nome del metodo diventa troppo lungo o complesso, puoi scrivere una query **JPQL** (Java Persistence Query Language) esplicitamente con `@Query`.

JPQL è simile a SQL ma usa i **nomi delle classi Java** invece dei nomi delle tabelle.

```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RunRepository extends JpaRepository<Run, Integer> {

    // JPQL: usa "Run" (nome classe) e "r.location" (nome campo Java)
    @Query("SELECT r FROM Run r WHERE r.location = :location ORDER BY r.miles DESC")
    List<Run> findByLocationOrderedByMiles(@Param("location") Location location);

    // Query con conteggio
    @Query("SELECT COUNT(r) FROM Run r WHERE r.location = :location")
    Long countByLocation(@Param("location") Location location);

    // Puoi anche usare SQL nativo con nativeQuery = true
    @Query(value = "SELECT * FROM runs WHERE miles > :minMiles", nativeQuery = true)
    List<Run> findRunsWithMoreThanMiles(@Param("minMiles") double minMiles);
}
```

---

#### Creare `RunRepository`

```java
package com.example.run.repository;

import com.example.run.model.Location;
import com.example.run.model.Run;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RunRepository extends JpaRepository<Run, Integer> {

    // Query derivata: trova corse per location
    List<Run> findByLocation(Location location);

    // Query derivata: trova corse con miglia maggiori di una soglia
    List<Run> findByMilesGreaterThan(double miles);
}
```

> **`@Repository` è opzionale** quando si estende `JpaRepository` (Spring la rileva automaticamente), ma è buona pratica includerla per chiarezza architetturale e per abilitare la traduzione delle eccezioni JPA in eccezioni Spring.

---

#### Eliminare la lista in memoria

Prima del Giorno 5, il controller gestiva i dati con una lista statica in memoria:

```java
// Prima — dati in memoria (tutto si perde al riavvio)
private final List<Run> runs = new ArrayList<>(List.of(
    new Run(1, "Corsa mattutina", startedOn, completedOn, 5.0, Location.OUTDOOR),
    new Run(2, "Corsa in palestra", startedOn, completedOn, 3.1, Location.INDOOR)
));
```

Con `RunRepository`, quella lista non serve più. I dati arrivano dal database:

```java
// Dopo — dati dal database
@GetMapping
public List<Run> findAll() {
    return runRepository.findAll();  // SELECT * FROM runs
}
```

---

### Esempio pratico — `RunController` con `RunRepository`

Aggiornamento del controller del Giorno 4 per usare il database:

```java
package com.example.run.controller;

import com.example.run.model.Run;
import com.example.run.repository.RunRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/runs")
public class RunController {

    private final RunRepository runRepository;

    // Iniezione via costruttore (raccomandata)
    public RunController(RunRepository runRepository) {
        this.runRepository = runRepository;
    }

    // GET /api/runs → lista tutte le corse
    @GetMapping
    public List<Run> findAll() {
        return runRepository.findAll();
    }

    // GET /api/runs/{id} → singola corsa
    @GetMapping("/{id}")
    public ResponseEntity<Run> findById(@PathVariable Integer id) {
        Optional<Run> run = runRepository.findById(id);
        return run.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/runs → crea nuova corsa
    @PostMapping
    public ResponseEntity<Run> create(@RequestBody Run run) {
        Run saved = runRepository.save(run);
        return ResponseEntity.status(201).body(saved);
    }

    // PUT /api/runs/{id} → aggiorna corsa esistente
    @PutMapping("/{id}")
    public ResponseEntity<Run> update(@PathVariable Integer id, @RequestBody Run runDetails) {
        Optional<Run> existing = runRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Run run = existing.get();
        run.setTitle(runDetails.getTitle());
        run.setStartedOn(runDetails.getStartedOn());
        run.setCompletedOn(runDetails.getCompletedOn());
        run.setMiles(runDetails.getMiles());
        run.setLocation(runDetails.getLocation());
        Run updated = runRepository.save(run);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/runs/{id} → elimina corsa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!runRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        runRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

#### Dati iniziali con `data.sql`

Per popolare il database con dei dati di test all'avvio, crea il file `src/main/resources/data.sql`:

```sql
INSERT INTO runs (title, started_on, completed_on, miles, location)
VALUES ('Corsa mattutina al parco', '2024-03-01 07:00:00', '2024-03-01 07:45:00', 5.0, 'OUTDOOR');

INSERT INTO runs (title, started_on, completed_on, miles, location)
VALUES ('Tapis roulant in palestra', '2024-03-02 18:00:00', '2024-03-02 18:30:00', 3.1, 'INDOOR');

INSERT INTO runs (title, started_on, completed_on, miles, location)
VALUES ('Mezza maratona domenicale', '2024-03-03 09:00:00', '2024-03-03 10:45:00', 13.1, 'OUTDOOR');
```

Spring Boot esegue `data.sql` automaticamente all'avvio, dopo che Hibernate ha creato le tabelle.

> Con `ddl-auto=create-drop`, il file `data.sql` viene eseguito ad ogni avvio. Con `ddl-auto=update` invece, i dati vengono aggiunti senza azzerare la tabella.

---

## 4. Relazioni tra Entità

### Teoria

Nelle applicazioni reali le entità raramente esistono in isolamento: un `Autore` ha molti `Libri`, un `Ordine` contiene molti `Prodotti`, un `Utente` ha un `Profilo`. JPA gestisce queste relazioni tramite quattro annotazioni principali.

#### Panoramica delle relazioni

| Annotazione     | Significato                                  | Esempio                                |
|-----------------|----------------------------------------------|----------------------------------------|
| `@ManyToOne`    | Molti oggetti → un solo oggetto padre        | Molti `Run` appartengono a un `User`   |
| `@OneToMany`    | Un oggetto → molti figli                     | Un `User` ha molte `Run`               |
| `@OneToOne`     | Un oggetto ↔ un solo oggetto                 | Un `User` ha un `Profilo`              |
| `@ManyToMany`   | Molti oggetti ↔ molti oggetti                | Un `Corso` ha molti `Studenti` e viceversa |

---

#### `@ManyToOne` — il lato "molti"

`@ManyToOne` è l'annotazione più comune. Si mette **sul lato che possiede la foreign key** (il lato "molti").

```java
// Scenario: molte Run appartengono a un singolo User

@Entity
@Table(name = "runs")
public class Run {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    // ── Relazione: molte Run → un User ──────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  // nome della FK nella tabella runs
    private User user;

    // ... altri campi, getter, setter
}
```

`@JoinColumn(name = "user_id")` dice a Hibernate di creare/usare la colonna `user_id` nella tabella `runs` come foreign key verso la tabella `users`.

---

#### `@OneToMany` — il lato "uno"

`@OneToMany` si usa sull'entità che contiene la **collection** dei figli. Di solito è l'inverso di `@ManyToOne`.

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    // ── Relazione inversa: un User → molte Run ──────────────────────────────
    @OneToMany(
        mappedBy = "user",           // nome del campo @ManyToOne in Run
        cascade = CascadeType.ALL,   // operazioni propagate ai figli
        orphanRemoval = true         // cancella i figli orfani automaticamente
    )
    private List<Run> runs = new ArrayList<>();

    // ... costruttore, getter, setter
}
```

> **`mappedBy`** indica che la relazione è già mappata dall'altro lato (`Run.user`). Senza `mappedBy`, JPA creerebbe una tabella di join intermedia non necessaria.

---

#### Relazione bidirezionale vs unidirezionale

| Tipo             | Descrizione                                                                        |
|------------------|------------------------------------------------------------------------------------|
| **Unidirezionale** | Solo un lato conosce l'altro. Più semplice, meno overhead.                       |
| **Bidirezionale**  | Entrambi i lati si riferiscono l'un l'altro (`@ManyToOne` + `@OneToMany`). Utile per navigare la relazione in entrambe le direzioni. |

In una relazione bidirezionale è buona norma aggiungere **metodi helper** per mantenere la coerenza:

```java
// In User.java
public void addRun(Run run) {
    runs.add(run);
    run.setUser(this);  // mantiene sincronizzato anche il lato Run
}

public void removeRun(Run run) {
    runs.remove(run);
    run.setUser(null);
}
```

---

#### `@OneToOne` — relazione uno a uno

Usata quando un'entità è associata a esattamente un'altra entità. La FK può stare su entrambi i lati; per convenzione si mette sul lato "dipendente".

```java
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String bio;
    private String avatarUrl;

    // ── Relazione: un Profilo → un User (proprietario della FK) ────────────
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
```

```java
// Lato inverso in User.java
@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
private UserProfile profile;
```

---

#### `@ManyToMany` — relazione molti a molti

Una relazione molti-a-molti richiede una **tabella di join** intermedia. JPA la crea automaticamente tramite `@JoinTable`.

```java
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    // ── Relazione: un Corso ↔ molti Studenti ────────────────────────────────
    @ManyToMany
    @JoinTable(
        name = "course_students",               // nome tabella di join
        joinColumns = @JoinColumn(name = "course_id"),    // FK verso Course
        inverseJoinColumns = @JoinColumn(name = "student_id")  // FK verso Student
    )
    private List<Student> students = new ArrayList<>();
}
```

```java
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    // ── Lato inverso ────────────────────────────────────────────────────────
    @ManyToMany(mappedBy = "students")
    private List<Course> courses = new ArrayList<>();
}
```

Hibernate crea automaticamente:
```sql
CREATE TABLE course_students (
    course_id  INTEGER NOT NULL,
    student_id INTEGER NOT NULL,
    PRIMARY KEY (course_id, student_id)
);
```

---

#### FetchType — caricamento Lazy vs Eager

Quando JPA carica un'entità, deve decidere **quando** recuperare le entità correlate dal database.

| FetchType    | Comportamento                                                                 | Default per              |
|--------------|-------------------------------------------------------------------------------|---------------------------|
| `LAZY`       | Le entità correlate vengono caricate **solo quando si accede al campo**       | `@OneToMany`, `@ManyToMany` |
| `EAGER`      | Le entità correlate vengono caricate **subito, nella stessa query**           | `@ManyToOne`, `@OneToOne`  |

```java
// Caricamento esplicito LAZY (raccomandato per collection)
@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
private List<Run> runs;

// Caricamento esplicito EAGER (usa con cautela: può generare query molto pesanti)
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "user_id")
private User user;
```

> **Regola pratica:** usa sempre `LAZY` per le collection (`@OneToMany`, `@ManyToMany`). `EAGER` su una collection con centinaia di elementi genera un unico SELECT gigante che può bloccare il database.

---

#### CascadeType — propagazione delle operazioni

`CascadeType` specifica quali operazioni JPA vengono automaticamente **propagate** ai figli quando vengono eseguite sul padre.

| CascadeType   | Operazione propagata                              |
|---------------|---------------------------------------------------|
| `PERSIST`     | `save()` del padre → salva anche i figli         |
| `MERGE`       | aggiornamento del padre → aggiorna anche i figli  |
| `REMOVE`      | `delete()` del padre → cancella anche i figli    |
| `REFRESH`     | `refresh()` del padre → ricarica anche i figli   |
| `DETACH`      | `detach()` del padre → stacca anche i figli      |
| `ALL`         | tutte le operazioni sopra                         |

```java
// Con CascadeType.ALL: salvare un User salva automaticamente anche le sue Run
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Run> runs;

// Senza cascade: bisogna salvare Run separatamente
@ManyToOne
@JoinColumn(name = "user_id")
private User user;  // non propaga nulla
```

> 💡 **`orphanRemoval = true`:** se rimuovi un `Run` dalla lista `user.getRuns()`, Hibernate lo cancella automaticamente dal database. Senza questa opzione, la Run diventerebbe un "orfano" con `user_id = null`.

---

### Esempio pratico — `User` con `Run` (relazione bidirezionale)

Vediamo l'implementazione completa della relazione `User` → `Run`:

```java
// User.java
package com.example.run.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<Run> runs = new ArrayList<>();

    protected User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Helper per mantenere la coerenza della relazione bidirezionale
    public void addRun(Run run) {
        runs.add(run);
        run.setUser(this);
    }

    public void removeRun(Run run) {
        runs.remove(run);
        run.setUser(null);
    }

    public Integer getId()       { return id; }
    public String getName()      { return name; }
    public String getEmail()     { return email; }
    public List<Run> getRuns()   { return runs; }

    public void setName(String name)   { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}
```

```java
// Run.java — aggiunta del campo user
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;

// Getter e setter da aggiungere
public User getUser()          { return user; }
public void setUser(User user) { this.user = user; }
```

Hibernate genera le tabelle:

```sql
CREATE TABLE users (
    id    INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE runs (
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    started_on   TIMESTAMP    NOT NULL,
    completed_on TIMESTAMP    NOT NULL,
    miles        FLOAT        NOT NULL,
    location     VARCHAR(255) NOT NULL,
    user_id      INTEGER      NOT NULL,  -- ← FK aggiunta da @ManyToOne
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

### Riepilogo — Relazioni JPA

| Annotazione     | Lato proprietario FK | Usa `mappedBy`? | `@JoinTable`? |
|-----------------|---------------------|-----------------|---------------|
| `@ManyToOne`    | Sì (questo lato)    | No              | No            |
| `@OneToMany`    | No (lato inverso)   | Sì              | No            |
| `@OneToOne`     | Il lato con la FK   | Lato inverso    | No            |
| `@ManyToMany`   | Il proprietario     | Lato inverso    | Sì            |

---

## 5. Esercizio — CRUD Completo con DB

### Obiettivo

Completare la migrazione del progetto `Run` dalla lista in memoria al database H2, implementando tutti e quattro gli endpoint CRUD, testandoli con Postman e verificando la persistenza tramite H2 Console.

---

### Prerequisiti

- Progetto `Run` funzionante dal Giorno 4 con `RunController` e `Run` come record
- Maven configurato con Spring Web già presente

---

### Step 1 — Aggiungere le dipendenze JPA e H2

Apri `pom.xml` e aggiungi, all'interno della sezione `<dependencies>`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

Salva e lascia che Maven scarichi le dipendenze (IntelliJ: clicca sul popup **Load Maven Changes**; VS Code: attendi il build automatico).

---

### Step 2 — Configurare `application.properties`

Sostituisci il contenuto di `src/main/resources/application.properties` con:

```properties
spring.application.name=run

spring.datasource.url=jdbc:h2:mem:rundb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.h2.console.enabled=true
```

---

### Step 3 — Convertire `Run` da Record a `@Entity`

Sostituisci il file `Run.java` (o `Run` record) con la classe annotata `@Entity` mostrata nella sezione 2, con:

- `@Entity` e `@Table(name = "runs")`
- `@Id` e `@GeneratedValue(strategy = GenerationType.IDENTITY)` sul campo `id`
- `@Column` su ogni campo
- `@Enumerated(EnumType.STRING)` sul campo `location`
- Costruttore no-arg `protected Run() {}`
- Costruttore completo con validazione
- Getter e setter per tutti i campi

---

### Step 4 — Creare `RunRepository`

Crea il file `src/main/java/com/example/run/repository/RunRepository.java`:

```java
package com.example.run.repository;

import com.example.run.model.Run;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunRepository extends JpaRepository<Run, Integer> {
    // Spring genera l'implementazione automaticamente
}
```

---

### Step 5 — Aggiornare `RunController`

Aggiorna `RunController` per usare `RunRepository` al posto della lista in memoria:

1. Rimuovi il campo `private final List<Run> runs = ...`
2. Aggiungi il campo `private final RunRepository runRepository`
3. Inietta `RunRepository` tramite costruttore
4. Aggiorna i metodi esistenti (`findAll`, `findById`) per usare il repository
5. Aggiungi i metodi `create`, `update`, `delete` come mostrato nella sezione 3

---

### Step 6 — Creare `data.sql` con dati iniziali

Crea il file `src/main/resources/data.sql`:

```sql
INSERT INTO runs (title, started_on, completed_on, miles, location)
VALUES ('Corsa mattutina', '2024-03-01 07:00:00', '2024-03-01 07:45:00', 5.0, 'OUTDOOR');

INSERT INTO runs (title, started_on, completed_on, miles, location)
VALUES ('Allenamento indoor', '2024-03-02 18:00:00', '2024-03-02 18:30:00', 3.1, 'INDOOR');
```

---

### Step 7 — Avviare e testare con Postman

Avvia l'applicazione con `./mvnw spring-boot:run` e verifica nella console che Hibernate abbia creato la tabella.

**Test 1 — GET /api/runs (lista tutte le corse)**

- Metodo: `GET`
- URL: `http://localhost:8080/api/runs`
- Risultato atteso: array JSON con le 2 corse del `data.sql`

**Test 2 — GET /api/runs/{id} (singola corsa)**

- Metodo: `GET`
- URL: `http://localhost:8080/api/runs/1`
- Risultato atteso: oggetto JSON con la prima corsa
- Bonus: prova con un ID inesistente (es. `/api/runs/999`) — dovresti ricevere `404 Not Found`

**Test 3 — POST /api/runs (crea nuova corsa)**

- Metodo: `POST`
- URL: `http://localhost:8080/api/runs`
- Header: `Content-Type: application/json`
- Body (JSON raw):

```json
{
  "title": "Corsa serale",
  "startedOn": "2024-03-10T19:00:00",
  "completedOn": "2024-03-10T19:40:00",
  "miles": 4.5,
  "location": "OUTDOOR"
}
```

- Risultato atteso: `201 Created` con l'oggetto creato (campo `id` valorizzato dal DB)

**Test 4 — PUT /api/runs/{id} (aggiorna)**

- Metodo: `PUT`
- URL: `http://localhost:8080/api/runs/1`
- Header: `Content-Type: application/json`
- Body: stessa struttura del POST, con valori modificati (es. `"miles": 6.0`)
- Risultato atteso: `200 OK` con i dati aggiornati

**Test 5 — DELETE /api/runs/{id} (elimina)**

- Metodo: `DELETE`
- URL: `http://localhost:8080/api/runs/2`
- Risultato atteso: `204 No Content` (nessun body)
- Verifica: esegui GET /api/runs — la corsa con id 2 non deve più essere presente

---

### Step 8 — Verificare con H2 Console

1. Apri `http://localhost:8080/h2-console` nel browser
2. Connettiti con JDBC URL `jdbc:h2:mem:rundb`, username `sa`, password vuota
3. Esegui:

```sql
SELECT * FROM runs;
```

Verifica che le righe corrispondano allo stato attuale del database dopo le operazioni Postman.

---

### Domande di verifica

1. Cosa succede al contenuto del database se riavvii l'applicazione con `ddl-auto=create-drop`? E con `ddl-auto=update`?
2. Perché `JpaRepository` usa `Optional<T>` nel metodo `findById()` invece di restituire direttamente `T`?
3. Come fa Spring Data JPA a sapere quale SQL generare per il metodo `findByMilesGreaterThan(double miles)` senza che tu scriva alcuna query?
4. Qual è la differenza tra `save()` applicato a un'entità senza `id` e `save()` applicato a un'entità con `id` già esistente?
5. Perché un'entità JPA non può essere un `record` Java?

---

## Riepilogo del Giorno 5

| Concetto                  | Annotazione/Classe                     | Ruolo                                              |
|---------------------------|----------------------------------------|----------------------------------------------------|
| Mappatura ORM             | `@Entity`                              | Collega la classe Java alla tabella del DB         |
| Chiave primaria           | `@Id` + `@GeneratedValue`              | Campo univoco con generazione automatica           |
| Colonna personalizzata    | `@Column`                              | Nome, nullable, lunghezza della colonna            |
| Enum come stringa         | `@Enumerated(EnumType.STRING)`         | Salva "INDOOR"/"OUTDOOR" invece di 0/1             |
| Nome tabella              | `@Table(name = "...")`                 | Sovrascrive il nome di default                     |
| CRUD automatico           | `JpaRepository<T, ID>`                 | 18+ metodi pronti all'uso senza SQL                |
| Query da nome             | `findByXxx()`, `findByXxxGreaterThan()`| Query generate automaticamente dal nome del metodo |
| Query personalizzata      | `@Query("SELECT r FROM Run r WHERE ...")`| JPQL o SQL nativo esplicito                     |
| Configurazione DB         | `application.properties`              | Datasource, ddl-auto, show-sql, H2 console         |
| Dati iniziali             | `data.sql`                             | SQL eseguito all'avvio per popolare il DB          |
| Relazione molti→uno       | `@ManyToOne` + `@JoinColumn`           | FK nel lato "molti", punta all'entità padre        |
| Relazione uno→molti       | `@OneToMany(mappedBy = "...")`         | Collection nel lato "uno", lato inverso            |
| Relazione uno a uno       | `@OneToOne` + `@JoinColumn`            | FK univoca, lato dipendente                        |
| Relazione molti a molti   | `@ManyToMany` + `@JoinTable`           | Tabella di join intermedia generata da Hibernate   |
| Caricamento dati          | `FetchType.LAZY` / `EAGER`             | Lazy: su richiesta; Eager: subito nella query      |
| Propagazione operazioni   | `CascadeType.ALL` / `PERSIST` / ecc.  | Operazioni JPA propagate automaticamente ai figli  |

> **Passaggio chiave del giorno:** si abbandona la lista in memoria e si adotta una soluzione di persistenza reale. Da questo momento, i dati sopravvivono alle chiamate HTTP e — con `ddl-auto=update` — anche ai riavvii dell'applicazione.
