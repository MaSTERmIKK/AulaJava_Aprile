# Giorno 6 — Architettura a Layer, Validazioni & Gestione Errori

---

## Indice

1. [Architettura Controller → Service → Repository](#1-architettura-controller--service--repository)
2. [Validazione Input con Bean Validation](#2-validazione-input-con-bean-validation)
3. [Gestione Errori Globale](#3-gestione-errori-globale)
4. [DTO — Data Transfer Object](#4-dto--data-transfer-object)

---

## 1. Architettura Controller → Service → Repository

### Teoria

Un'applicazione Spring Boot ben progettata separa le responsabilità in **tre layer distinti**,
ognuno con un compito preciso. Questo approccio rispetta il principio **Single Responsibility
Principle (SRP)**: ogni classe ha una sola ragione per cambiare.

| Layer | Annotazione | Responsabilità |
|---|---|---|
| **Controller** | `@RestController` | Riceve la richiesta HTTP, delega al Service, restituisce la risposta |
| **Service** | `@Service` | Contiene la logica di business e l'orchestrazione |
| **Repository** | `@Repository` | Accede al database (via JPA o implementazione custom) |

### Flusso completo di una richiesta

```
HTTP Request
     │
     ▼
┌─────────────┐
│  Controller  │  ← @RestController: solo routing + risposta HTTP
└──────┬──────┘
       │ chiama
       ▼
┌─────────────┐
│   Service   │  ← @Service: logica di business
└──────┬──────┘
       │ chiama
       ▼
┌─────────────┐
│ Repository  │  ← @Repository / JpaRepository: query DB
└──────┬──────┘
       │
       ▼
    Database
       │
       ▼ (dati risalgono lo stack)
HTTP Response
```

**Perché separare i layer?**

- **Manutenibilità**: modificare la logica di business non tocca il Controller.
- **Testabilità**: ogni layer può essere testato in isolamento con mock.
- **Leggibilità**: il codice è più facile da navigare e capire.
- **Riusabilità**: lo stesso Service può essere usato da più Controller (es. REST + WebSocket).

### Esempio pratico — Refactoring di RunController

#### Situazione di partenza (tutto nel Controller)

```java
@RestController
@RequestMapping("/api/runs")
public class RunController {

    // La lista è gestita direttamente nel Controller — SBAGLIATO
    private final List<Run> runs = new ArrayList<>();

    @GetMapping
    public List<Run> findAll() {
        return runs;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Run> findById(@PathVariable Integer id) {
        return runs.stream()
                .filter(r -> r.id().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
```

#### RunRepository — accesso ai dati

```java
package com.example.run;

import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<Entità, TipoChiavePrimaria>
public interface RunRepository extends JpaRepository<Run, Integer> {
    // findAll(), findById(), save(), deleteById() sono già disponibili
}
```

#### RunService — logica di business

```java
package com.example.run;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RunService {

    private final RunRepository runRepository;

    // Iniezione via costruttore (raccomandata)
    public RunService(RunRepository runRepository) {
        this.runRepository = runRepository;
    }

    public List<Run> findAll() {
        return runRepository.findAll();
    }

    public Optional<Run> findById(Integer id) {
        return runRepository.findById(id);
    }

    public Run save(Run run) {
        return runRepository.save(run);
    }

    public void deleteById(Integer id) {
        runRepository.deleteById(id);
    }

    public Run update(Integer id, Run updatedRun) {
        Run existing = runRepository.findById(id)
                .orElseThrow(() -> new RunNotFoundException(id));

        Run run = new Run(
                existing.id(),
                updatedRun.title(),
                updatedRun.startedOn(),
                updatedRun.completedOn(),
                updatedRun.miles(),
                updatedRun.location()
        );
        return runRepository.save(run);
    }
}
```

#### RunController — solo routing e risposta HTTP

```java
package com.example.run;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/runs")
public class RunController {

    private final RunService runService;

    public RunController(RunService runService) {
        this.runService = runService;
    }

    @GetMapping
    public List<Run> findAll() {
        return runService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Run> findById(@PathVariable Integer id) {
        return runService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Run create(@RequestBody Run run) {
        return runService.save(run);
    }

    @PutMapping("/{id}")
    public Run update(@PathVariable Integer id, @RequestBody Run run) {
        return runService.update(id, run);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        runService.deleteById(id);
    }
}
```

> **Nota:** Il Controller non contiene più **nessuna logica di business**. Si limita a
> chiamare il Service e a restituire la risposta HTTP corretta.

### Esercizio

**Obiettivo:** Eseguire il refactoring della tua `RunController` separando correttamente i
layer.

**Passi:**

1. Crea la classe `RunService` nel package `com.example.run` con annotazione `@Service`.
2. Sposta tutta la logica (ricerca per id, salvataggio, aggiornamento) da `RunController`
   a `RunService`.
3. Inietta `RunService` in `RunController` tramite costruttore.
4. Verifica che `RunController` non contenga più chiamate dirette a `RunRepository`.
5. Testa con Postman tutti gli endpoint (`GET`, `POST`, `PUT`, `DELETE`) e verifica che il
   comportamento sia invariato rispetto a prima del refactoring.

**Domande di verifica:**

- Cosa succederebbe se la logica di business rimanesse nel Controller?
- Perché si inietta il `RunRepository` nel `RunService` e non direttamente nel Controller?
- Come cambieresti il codice se volessi aggiungere una logica di caching nel Service?

---

## 2. Validazione Input con Bean Validation

### Teoria

**Bean Validation** è lo standard Java (JSR-380) per validare i dati in ingresso. Spring Boot
integra l'implementazione **Hibernate Validator** tramite la dipendenza
`spring-boot-starter-validation`.

Senza validazione, un client potrebbe inviare dati incompleti o inconsistenti (titolo vuoto,
chilometri negativi, date mancanti) che verrebbero salvati direttamente nel database. Le
annotazioni di Bean Validation permettono di bloccare questi dati **prima** che entrino nella
logica applicativa.

#### Aggiungere la dipendenza

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

#### Principali annotazioni di validazione

| Annotazione | Descrizione | Esempio |
|---|---|---|
| `@NotNull` | Il campo non può essere `null` | `@NotNull Integer id` |
| `@NotBlank` | Stringa non nulla e non solo spazi | `@NotBlank String title` |
| `@NotEmpty` | Lista o stringa non vuota | `@NotEmpty List<String> tags` |
| `@Size(min, max)` | Lunghezza stringa o dimensione collezione | `@Size(min=3, max=100)` |
| `@Min(value)` | Valore numerico minimo | `@Min(1) int miles` |
| `@Max(value)` | Valore numerico massimo | `@Max(100) int miles` |
| `@Positive` | Numero strettamente positivo | `@Positive double miles` |
| `@Email` | Formato email valido | `@Email String email` |
| `@Pattern(regexp)` | Regex personalizzata | `@Pattern(regexp="[A-Z]+")` |
| `@Future` | Data nel futuro | `@Future LocalDateTime end` |
| `@Past` | Data nel passato | `@Past LocalDateTime start` |

#### Attivare la validazione nel Controller

L'annotazione `@Valid` sul parametro `@RequestBody` **attiva la validazione** prima di
eseguire il metodo. Se uno o più vincoli non sono rispettati, Spring restituisce
automaticamente un errore **400 Bad Request** con i dettagli degli errori.

### Esempio pratico — Validazione su Run

#### Entità Run con vincoli di validazione

```java
package com.example.run;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "runs")
public class Run {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Il titolo non può essere vuoto")
    @Size(min = 3, max = 100, message = "Il titolo deve avere tra 3 e 100 caratteri")
    private String title;

    @NotNull(message = "La data di inizio è obbligatoria")
    private LocalDateTime startedOn;

    @NotNull(message = "La data di fine è obbligatoria")
    private LocalDateTime completedOn;

    @Positive(message = "I chilometri devono essere un valore positivo")
    @Max(value = 200, message = "Non si possono registrare più di 200 miglia")
    private int miles;

    @NotNull(message = "La location è obbligatoria")
    @Enumerated(EnumType.STRING)
    private Location location;

    // Costruttori, getter, setter...
}
```

#### Controller con @Valid

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public Run create(@Valid @RequestBody Run run) {
    // Se la validazione fallisce, questo codice non viene mai eseguito.
    // Spring restituisce automaticamente un 400 Bad Request.
    return runService.save(run);
}

@PutMapping("/{id}")
public Run update(@PathVariable Integer id,
                  @Valid @RequestBody Run run) {
    return runService.update(id, run);
}
```

#### Esempio di risposta automatica in caso di errore (400)

```json
{
  "timestamp": "2024-05-10T16:30:00.123+00:00",
  "status": 400,
  "error": "Bad Request",
  "path": "/api/runs"
}
```

> Spring include automaticamente nel log la lista dei campi non validi.
> Per personalizzare la risposta, si usa `@ControllerAdvice` (vedi sezione successiva).

#### Gestire gli errori con BindingResult (opzionale)

`BindingResult` permette di catturare gli errori di validazione **nel metodo del Controller**
e gestirli manualmente, invece di lasciare che Spring li gestisca automaticamente.

```java
@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody Run run,
                                 BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
        // Costruisce una mappa campo → messaggio d'errore
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(runService.save(run));
}
```

**Risposta personalizzata con BindingResult:**

```json
{
  "title": "Il titolo deve avere tra 3 e 100 caratteri",
  "miles": "I chilometri devono essere un valore positivo"
}
```

### Esercizio

**Obiettivo:** Aggiungere vincoli di validazione al modello `Run` e verificarne il
comportamento.

**Passi:**

1. Aggiungi la dipendenza `spring-boot-starter-validation` al `pom.xml`.
2. Annota i campi dell'entità `Run` con i vincoli appropriati:
   - `title`: non vuoto, tra 3 e 100 caratteri.
   - `startedOn` e `completedOn`: non nulli.
   - `miles`: valore positivo, massimo 200.
   - `location`: non nulla.
3. Aggiungi `@Valid` ai metodi `create` e `update` del Controller.
4. Testa con Postman inviando richieste `POST` non valide (es. titolo vuoto, miglia negative)
   e verifica che la risposta sia `400 Bad Request`.
5. **(Bonus)** Implementa `BindingResult` per restituire una mappa degli errori strutturata.

**Domande di verifica:**

- Cosa succede se ometti `@Valid` nel Controller anche se i campi sono annotati?
- Qual è la differenza tra `@NotNull`, `@NotBlank` e `@NotEmpty`?
- In quale situazione useresti `@Pattern` invece di `@Email`?

---

## 3. Gestione Errori Globale

### Teoria

In un'applicazione Spring Boot, la gestione degli errori può avvenire a due livelli:

- **`@ExceptionHandler`**: gestisce eccezioni all'interno di un **singolo Controller**.
- **`@ControllerAdvice`**: gestisce eccezioni a livello **globale**, intercettando le
  eccezioni di tutta l'applicazione in un unico posto.

L'approccio professionale prevede di centralizzare la gestione degli errori in una classe
`GlobalExceptionHandler` annotata con `@ControllerAdvice`, così il codice dei Controller
rimane pulito e le risposte di errore hanno un formato **coerente**.

#### Struttura di una risposta di errore standard

```json
{
  "message": "Run con id 42 non trovato",
  "status": 404,
  "timestamp": "2024-05-10T16:45:00"
}
```

### Esempio pratico — Eccezione custom + Handler globale

#### Passo 1: Creare l'eccezione custom RunNotFoundException

```java
package com.example.run;

// Estende RuntimeException: eccezione non verificata (unchecked)
public class RunNotFoundException extends RuntimeException {

    public RunNotFoundException(Integer id) {
        super("Run con id " + id + " non trovato");
    }
}
```

> Si preferisce estendere `RuntimeException` (non verificata) per non obbligare ogni
> metodo che chiama il Service a gestire l'eccezione con `try-catch` o `throws`.

#### Passo 2: Lanciare l'eccezione nel Service

```java
@Service
public class RunService {

    private final RunRepository runRepository;

    public RunService(RunRepository runRepository) {
        this.runRepository = runRepository;
    }

    public Run findById(Integer id) {
        // orElseThrow lancia l'eccezione se l'Optional è vuoto
        return runRepository.findById(id)
                .orElseThrow(() -> new RunNotFoundException(id));
    }

    public void deleteById(Integer id) {
        // Verifica che la corsa esista prima di eliminarla
        if (!runRepository.existsById(id)) {
            throw new RunNotFoundException(id);
        }
        runRepository.deleteById(id);
    }
}
```

#### Passo 3: Creare il record per la risposta di errore

```java
package com.example.run;

import java.time.LocalDateTime;

// Un Record è perfetto per oggetti immutabili come la risposta di errore
public record ErrorResponse(
        String message,
        int status,
        LocalDateTime timestamp
) {}
```

#### Passo 4: GlobalExceptionHandler con @ControllerAdvice

```java
package com.example.run;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Gestisce RunNotFoundException → 404 Not Found
    @ExceptionHandler(RunNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRunNotFound(RunNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Gestisce IllegalArgumentException → 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Fallback: gestisce qualsiasi altra eccezione non prevista → 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                "Si è verificato un errore interno al server",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

#### Differenza tra @ExceptionHandler locale e @ControllerAdvice

```java
// Approccio locale: il metodo è solo in questo Controller
@RestController
public class RunController {

    @ExceptionHandler(RunNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(RunNotFoundException ex) {
        // Gestisce SOLO le eccezioni lanciate da QUESTO controller
        ...
    }
}

// Approccio globale: gestisce le eccezioni di TUTTA l'applicazione
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RunNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(RunNotFoundException ex) {
        // Intercetta l'eccezione da qualsiasi Controller
        ...
    }
}
```

#### Esempio di risposta con @ControllerAdvice attivo

**Request:** `GET /api/runs/999`

**Response (404 Not Found):**

```json
{
  "message": "Run con id 999 non trovato",
  "status": 404,
  "timestamp": "2024-05-10T16:45:22"
}
```

### Esercizio

**Obiettivo:** Implementare la gestione degli errori globale per il Run Tracker.

**Passi:**

1. Crea la classe `RunNotFoundException` che estende `RuntimeException`, con un messaggio
   che include l'id della corsa non trovata.
2. Modifica `RunService.findById()` per usare `orElseThrow()` lanciando
   `RunNotFoundException`.
3. Crea il record `ErrorResponse` con i campi `message`, `status` e `timestamp`.
4. Crea la classe `GlobalExceptionHandler` con `@ControllerAdvice` e i metodi:
   - Handler per `RunNotFoundException` → risposta `404`.
   - Handler generico per `Exception` → risposta `500`.
5. Testa con Postman:
   - `GET /api/runs/9999` — verifica risposta `404` con corpo JSON strutturato.
   - **(Bonus)** Prova a lanciare un'`IllegalArgumentException` nel Service e aggiungi
     il relativo handler.

**Domande di verifica:**

- Cosa succede se non si definisce nessun `@ExceptionHandler` per una certa eccezione?
- Perché si preferisce `RuntimeException` a `Exception` per le eccezioni custom?
- Qual è il vantaggio di avere un `ErrorResponse` con `timestamp`?

---

## 4. DTO — Data Transfer Object

### Teoria

Il **DTO (Data Transfer Object)** è un pattern architetturale che separa il modello interno
dell'applicazione (l'entità JPA) dall'interfaccia pubblica dell'API.

Senza DTO, l'API espone direttamente la struttura interna del database. Questo crea tre
problemi principali:

1. **Sicurezza**: campi sensibili (es. password, token) potrebbero essere esposti
   accidentalmente.
2. **Accoppiamento**: una modifica al modello del database rompe immediatamente l'API.
3. **Flessibilità**: non è possibile restituire forme diverse dello stesso dato in
   endpoint differenti.

#### Tipi di DTO comuni

| DTO | Scopo |
|---|---|
| **RunRequest** | Riceve i dati in input dall'utente (no `id`, no campi auto-generati) |
| **RunResponse** | Restituisce i dati in output verso il client (forma pubblica dell'entità) |

#### Flusso con DTO

```
Client
  │
  │  POST /api/runs  →  RunRequest (solo i campi accettati in input)
  ▼
Controller
  │  converte RunRequest → Run (entità)
  ▼
Service → Repository → Database
  │
  │  converte Run (entità) → RunResponse (solo i campi da esporre)
  ▼
Client  ←  RunResponse
```

### Esempio pratico — Introduzione di RunRequest e RunResponse

#### RunRequest — DTO di input

```java
package com.example.run;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

// Record immutabile: perfetto come DTO di input
public record RunRequest(

        @NotBlank(message = "Il titolo non può essere vuoto")
        @Size(min = 3, max = 100)
        String title,

        @NotNull(message = "La data di inizio è obbligatoria")
        LocalDateTime startedOn,

        @NotNull(message = "La data di fine è obbligatoria")
        LocalDateTime completedOn,

        @Positive(message = "Le miglia devono essere positive")
        int miles,

        @NotNull(message = "La location è obbligatoria")
        Location location
) {}
```

> **Nota:** `RunRequest` non ha il campo `id` — l'id viene generato automaticamente dal
> database e non deve mai essere accettato in input.

#### RunResponse — DTO di output

```java
package com.example.run;

import java.time.LocalDateTime;

// Espone solo i campi che il client deve vedere
public record RunResponse(
        Integer id,
        String title,
        LocalDateTime startedOn,
        LocalDateTime completedOn,
        int miles,
        String location  // String invece di enum: più leggibile per il client
) {}
```

#### RunService — mapping manuale Entity ↔ DTO

```java
@Service
public class RunService {

    private final RunRepository runRepository;

    public RunService(RunRepository runRepository) {
        this.runRepository = runRepository;
    }

    public List<RunResponse> findAll() {
        return runRepository.findAll()
                .stream()
                .map(this::toResponse)  // converte ogni Run in RunResponse
                .toList();
    }

    public RunResponse findById(Integer id) {
        Run run = runRepository.findById(id)
                .orElseThrow(() -> new RunNotFoundException(id));
        return toResponse(run);
    }

    public RunResponse save(RunRequest request) {
        Run run = toEntity(request);   // converte RunRequest in Run
        Run saved = runRepository.save(run);
        return toResponse(saved);      // restituisce RunResponse
    }

    // Mapping: RunRequest → Run (entità JPA)
    private Run toEntity(RunRequest request) {
        Run run = new Run();
        run.setTitle(request.title());
        run.setStartedOn(request.startedOn());
        run.setCompletedOn(request.completedOn());
        run.setMiles(request.miles());
        run.setLocation(request.location());
        return run;
    }

    // Mapping: Run (entità JPA) → RunResponse
    private RunResponse toResponse(Run run) {
        return new RunResponse(
                run.getId(),
                run.getTitle(),
                run.getStartedOn(),
                run.getCompletedOn(),
                run.getMiles(),
                run.getLocation().name()  // converte enum in stringa
        );
    }
}
```

#### RunController aggiornato con DTO

```java
@RestController
@RequestMapping("/api/runs")
public class RunController {

    private final RunService runService;

    public RunController(RunService runService) {
        this.runService = runService;
    }

    @GetMapping
    public List<RunResponse> findAll() {
        return runService.findAll();
    }

    @GetMapping("/{id}")
    public RunResponse findById(@PathVariable Integer id) {
        return runService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RunResponse create(@Valid @RequestBody RunRequest request) {
        return runService.save(request);
    }
}
```

#### Confronto: API senza DTO vs con DTO

```json
// Senza DTO — espone tutta la struttura interna
{
  "id": 1,
  "title": "Morning Run",
  "startedOn": "2024-05-10T07:00:00",
  "completedOn": "2024-05-10T08:00:00",
  "miles": 5,
  "location": "INDOOR",
  "createdAt": "2024-05-01T12:00:00",   // campo interno, non dovrebbe essere esposto
  "updatedAt": "2024-05-10T08:05:00",   // campo interno, non dovrebbe essere esposto
  "version": 3                          // campo interno di versioning JPA
}

// Con DTO — espone solo ciò che il client deve vedere
{
  "id": 1,
  "title": "Morning Run",
  "startedOn": "2024-05-10T07:00:00",
  "completedOn": "2024-05-10T08:00:00",
  "miles": 5,
  "location": "INDOOR"
}
```

### Esercizio

**Obiettivo:** Introdurre i DTO nel Run Tracker e aggiornare il Controller e il Service.

**Passi:**

1. Crea il record `RunRequest` con i campi necessari in input (senza `id`) e le annotazioni
   di validazione.
2. Crea il record `RunResponse` con i campi da esporre al client.
3. Aggiorna `RunService`:
   - Il metodo `findAll()` restituisce `List<RunResponse>`.
   - Il metodo `findById()` restituisce `RunResponse`.
   - Il metodo `save()` accetta `RunRequest` e restituisce `RunResponse`.
   - Implementa i metodi privati `toEntity()` e `toResponse()` per il mapping.
4. Aggiorna `RunController` per usare `RunRequest` al posto di `Run` come `@RequestBody`.
5. Testa con Postman:
   - `GET /api/runs` — verifica che la risposta sia nella forma di `RunResponse`.
   - `POST /api/runs` — invia un `RunRequest` valido e verifica la risposta `201`.
   - `POST /api/runs` — invia un body senza `title` e verifica il `400 Bad Request`.

**Domande di verifica:**

- Perché è importante che `RunRequest` non abbia il campo `id`?
- Cosa succederebbe se l'entità `Run` aggiungesse un campo `passwordHash` senza DTO?
- In quale situazione avresti bisogno di più DTO di risposta per la stessa entità?

---

## Riepilogo del Giorno 6

| Argomento | Concetti chiave | Annotazioni / Classi |
|---|---|---|
| **Architettura a Layer** | SRP, separazione delle responsabilità, flusso HTTP→DB | `@Service`, `@Repository`, `@RestController` |
| **Bean Validation** | Vincoli sui dati in ingresso, risposta 400 automatica | `@Valid`, `@NotBlank`, `@Size`, `@Positive`, `@NotNull` |
| **Gestione Errori** | Eccezioni custom, risposta JSON strutturata, handler globale | `@ExceptionHandler`, `@ControllerAdvice` |
| **DTO** | Separazione modello interno/API pubblica, mapping Entity↔DTO | `RunRequest`, `RunResponse`, mapping manuale |

### Struttura finale del progetto dopo il Giorno 6

```
src/main/java/com/example/run/
├── Run.java                    ← @Entity (modello JPA)
├── Location.java               ← Enum (INDOOR / OUTDOOR)
├── RunRepository.java          ← JpaRepository<Run, Integer>
├── RunService.java             ← @Service (logica di business)
├── RunController.java          ← @RestController (routing HTTP)
├── RunRequest.java             ← DTO input (Record)
├── RunResponse.java            ← DTO output (Record)
├── RunNotFoundException.java   ← Eccezione custom
├── ErrorResponse.java          ← DTO risposta di errore (Record)
└── GlobalExceptionHandler.java ← @ControllerAdvice
```
