# Giorno 4 — REST Controller & HTTP Methods

> Modulo: Costruzione API REST con Spring Boot

---

## Indice

1. [HTTP & REST — Fondamenta](#1-http--rest--fondamenta)
2. [@RestController e Mapping delle Richieste](#2-restcontroller-e-mapping-delle-richieste)
3. [ResponseEntity\<T\>](#3-responseentityt)
4. [Test con Postman](#4-test-con-postman)
5. [Esercizio — RunController Completo](#5-esercizio--runcontroller-completo)

---

## 1. HTTP & REST — Fondamenta

### Teoria

**HTTP (HyperText Transfer Protocol)** è il protocollo di comunicazione alla base del Web. Definisce come i messaggi vengono formattati e trasmessi tra client e server.

Il flusso di una comunicazione HTTP segue questo schema:

```
Client  →  Request  →  Server
Client  ←  Response ←  Server
```

Ogni **Request** è composta da:

- **Metodo** (es. GET, POST)
- **URL** (es. `/api/runs/1`)
- **Headers** (es. `Content-Type: application/json`)
- **Body** (opzionale, presente in POST/PUT)

Ogni **Response** è composta da:

- **Status Code** (es. `200`, `404`)
- **Headers**
- **Body** (il dato restituito, spesso in formato JSON)

---

### Metodi HTTP

| Metodo   | Azione              | Esempio URL              |
|----------|---------------------|--------------------------|
| `GET`    | Leggi una risorsa   | `GET /api/runs`          |
| `POST`   | Crea una risorsa    | `POST /api/runs`         |
| `PUT`    | Sostituisce risorse | `PUT /api/runs/1`        |
| `PATCH`  | Aggiorna parziale   | `PATCH /api/runs/1`      |
| `DELETE` | Elimina una risorsa | `DELETE /api/runs/1`     |

---

### Status Code HTTP più comuni

| Codice | Significato              | Quando usarlo                                    |
|--------|--------------------------|--------------------------------------------------|
| `200`  | OK                       | Risposta generica di successo (GET)              |
| `201`  | Created                  | Risorsa creata con successo (POST)               |
| `204`  | No Content               | Operazione riuscita senza corpo (DELETE)         |
| `400`  | Bad Request              | Dati inviati dal client non validi               |
| `404`  | Not Found                | Risorsa non trovata                              |
| `500`  | Internal Server Error    | Errore generico lato server                      |

---

### REST — Representational State Transfer

REST è uno **stile architetturale** (non un protocollo) che definisce come progettare API scalabili e uniformi. I principi fondamentali sono:

- **Stateless**: ogni richiesta è indipendente, il server non mantiene lo stato del client tra una chiamata e l'altra.
- **Resource-based**: le risorse (oggetti del dominio) sono identificate tramite URL. Es: `Utente` → `/api/users`.
- **Uniform Interface**: le operazioni sulle risorse usano i metodi HTTP standard (GET, POST, PUT, DELETE).
- **Rappresentazione**: le risorse vengono trasferite in una rappresentazione (tipicamente JSON).

**Esempi di URL design RESTful:**

```
GET    /api/runs              → lista di tutte le corse
GET    /api/runs/{id}         → singola corsa per ID
POST   /api/runs              → crea una nuova corsa
PUT    /api/runs/{id}         → aggiorna una corsa esistente
DELETE /api/runs/{id}         → elimina una corsa
GET    /api/users/{id}/orders → ordini di uno specifico utente
```

> **Buona pratica**: usa sostantivi al plurale per gli URL delle risorse. Evita verbi come `/getUsers` o `/createRun`.

---

## 2. @RestController e Mapping delle Richieste

### Teoria

In Spring Boot, la classe che gestisce le richieste HTTP in arrivo è chiamata **Controller**. L'annotazione `@RestController` è una composizione di due annotazioni:

```java
@RestController = @Controller + @ResponseBody
```

- `@Controller` → registra la classe come componente Spring che gestisce le richieste web
- `@ResponseBody` → indica che il valore restituito dai metodi deve essere serializzato direttamente nel corpo della risposta HTTP (tipicamente in JSON), senza passare per una view template

---

### Annotazioni di Mapping

| Annotazione        | Metodo HTTP | Uso                                  |
|--------------------|-------------|--------------------------------------|
| `@RequestMapping`  | Tutti       | Mapping generale (su classe o metodo)|
| `@GetMapping`      | GET         | Leggere dati                         |
| `@PostMapping`     | POST        | Creare una risorsa                   |
| `@PutMapping`      | PUT         | Aggiornare completamente             |
| `@PatchMapping`    | PATCH       | Aggiornare parzialmente              |
| `@DeleteMapping`   | DELETE      | Eliminare una risorsa                |

---

### Annotazioni sui Parametri

| Annotazione       | Descrizione                                      | Esempio URL                        |
|-------------------|--------------------------------------------------|------------------------------------|
| `@PathVariable`   | Estrae un valore variabile dall'URL              | `/api/runs/{id}` → `id`            |
| `@RequestParam`   | Legge parametri dalla query string               | `/api/runs?page=1&size=10`         |
| `@RequestBody`    | Deserializza il corpo JSON della richiesta       | `POST /api/runs` con body JSON     |

---

### Esempio pratico

```java
package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/runs")   // prefisso comune per tutti gli endpoint di questa classe
public class RunController {

    // Dati in memoria (simulazione, verrà sostituito da DB al Giorno 5)
    private List<String> runs = new ArrayList<>(List.of("Run A", "Run B", "Run C"));

    // GET /api/runs → restituisce la lista completa
    @GetMapping
    public List<String> findAll() {
        return runs;
    }

    // GET /api/runs/{id} → restituisce un singolo elemento per indice
    @GetMapping("/{id}")
    public String findById(@PathVariable int id) {
        return runs.get(id);
    }

    // POST /api/runs → aggiunge un nuovo elemento
    // Il corpo della richiesta JSON viene deserializzato in una String
    @PostMapping
    public String create(@RequestBody String newRun) {
        runs.add(newRun);
        return newRun;
    }

    // PUT /api/runs/{id} → sostituisce un elemento esistente
    @PutMapping("/{id}")
    public String update(@PathVariable int id, @RequestBody String updatedRun) {
        runs.set(id, updatedRun);
        return updatedRun;
    }

    // DELETE /api/runs/{id} → elimina un elemento
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        runs.remove(id);
    }

    // GET /api/runs/search?keyword=Run → ricerca per query string
    @GetMapping("/search")
    public List<String> search(@RequestParam String keyword) {
        return runs.stream()
                   .filter(r -> r.contains(keyword))
                   .toList();
    }
}
```

**Come funziona il flusso:**

1. Il client invia una richiesta HTTP a `/api/runs`
2. Spring individua il metodo corretto grazie alle annotazioni di mapping
3. I parametri `@PathVariable`, `@RequestParam`, `@RequestBody` vengono estratti automaticamente
4. Il valore restituito dal metodo viene serializzato in JSON e inviato come risposta

---

## 3. ResponseEntity\<T\>

### Teoria

Per impostazione predefinita, Spring restituisce sempre uno status code `200 OK` anche quando, ad esempio, non trova una risorsa. Per avere pieno controllo sulla risposta HTTP (status code, headers, body), si usa `ResponseEntity<T>`.

`ResponseEntity<T>` è un wrapper che incapsula:

- **Body**: il dato da restituire (di tipo `T`)
- **Status Code**: il codice HTTP da inviare (`HttpStatus`)
- **Headers**: header HTTP personalizzati (opzionale)

---

### Costruzione di una ResponseEntity

```java
// Risposta con solo status code
ResponseEntity.ok()                           // 200 OK senza body
ResponseEntity.notFound().build()             // 404 Not Found senza body
ResponseEntity.noContent().build()            // 204 No Content

// Risposta con body e status code
ResponseEntity.ok(oggetto)                    // 200 OK + body
ResponseEntity.status(HttpStatus.CREATED).body(oggetto)  // 201 Created + body

// Metodo statico generico
ResponseEntity<String> res = ResponseEntity
    .status(HttpStatus.NOT_FOUND)
    .body("Risorsa non trovata");
```

---

### Esempio pratico

```java
package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private List<String> products = new ArrayList<>(List.of("Laptop", "Mouse", "Keyboard"));

    // GET /api/products → 200 OK con lista
    @GetMapping
    public ResponseEntity<List<String>> findAll() {
        return ResponseEntity.ok(products);
    }

    // GET /api/products/{id} → 200 OK se trovato, 404 se non trovato
    @GetMapping("/{id}")
    public ResponseEntity<String> findById(@PathVariable int id) {
        if (id < 0 || id >= products.size()) {
            // Restituisce 404 Not Found senza body
            return ResponseEntity.notFound().build();
        }
        // Restituisce 200 OK con il prodotto trovato
        return ResponseEntity.ok(products.get(id));
    }

    // POST /api/products → 201 Created con la nuova risorsa
    @PostMapping
    public ResponseEntity<String> create(@RequestBody String newProduct) {
        products.add(newProduct);
        // Restituisce 201 Created con il prodotto appena creato
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    // DELETE /api/products/{id} → 204 No Content se eliminato, 404 se non trovato
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (id < 0 || id >= products.size()) {
            return ResponseEntity.notFound().build();
        }
        products.remove(id);
        // Restituisce 204 No Content (operazione riuscita, nessun body)
        return ResponseEntity.noContent().build();
    }
}
```

**Perché usare `ResponseEntity` invece di restituire direttamente l'oggetto?**

| Ritorno diretto         | `ResponseEntity<T>`                                 |
|-------------------------|-----------------------------------------------------|
| Sempre `200 OK`         | Status code semantico (201, 204, 404...)            |
| Nessun controllo header | Possibilità di aggiungere header custom             |
| Meno verboso            | Esplicito e più leggibile per chi consuma le API    |

---

## 4. Test con Postman

### Teoria

**Postman** è uno strumento grafico che permette di costruire, inviare e ispezionare richieste HTTP verso le API. È fondamentale durante lo sviluppo per verificare che gli endpoint funzionino correttamente senza dover scrivere codice di test o usare `curl` da terminale.

Con Postman puoi:

- Selezionare il metodo HTTP (GET, POST, PUT, DELETE...)
- Inserire l'URL dell'endpoint
- Aggiungere headers (es. `Content-Type: application/json`)
- Scrivere un body JSON per le richieste POST/PUT
- Ispezionare la risposta (body, status code, tempo di risposta)
- Organizzare le richieste in **Collection** riutilizzabili

---

### Configurazione iniziale

1. Scarica Postman da [postman.com](https://www.postman.com/downloads/)
2. Apri l'applicazione e crea un account (o accedi come guest)
3. Crea una nuova **Collection** chiamata `SpringBoot Corso`
4. Aggiungi le richieste alla collection per il riutilizzo

---

### Come testare gli endpoint del corso

**Test GET — Lista di tutte le corse:**

```
Metodo:  GET
URL:     http://localhost:8080/api/runs
Headers: (nessuno necessario)
Body:    (vuoto)
```

Risposta attesa: `200 OK` con un array JSON

---

**Test GET — Singola corsa per ID:**

```
Metodo:  GET
URL:     http://localhost:8080/api/runs/1
Headers: (nessuno necessario)
Body:    (vuoto)
```

Risposta attesa: `200 OK` con l'oggetto JSON della corsa, oppure `404 Not Found`

---

**Test POST — Creare una nuova corsa:**

```
Metodo:  POST
URL:     http://localhost:8080/api/runs
Headers: Content-Type: application/json
Body (raw JSON):
{
  "title": "Morning Run",
  "miles": 5.2
}
```

Risposta attesa: `201 Created` con l'oggetto appena creato

---

**Test DELETE — Eliminare una corsa:**

```
Metodo:  DELETE
URL:     http://localhost:8080/api/runs/1
Headers: (nessuno necessario)
Body:    (vuoto)
```

Risposta attesa: `204 No Content`

---

### Leggere e interpretare la risposta

Nell'interfaccia di Postman, dopo aver inviato una richiesta, osserva:

- **Status**: il codice HTTP (es. `200 OK`, `404 Not Found`) — in alto a destra nel pannello Response
- **Body**: il contenuto JSON restituito — nella tab "Body"
- **Time**: il tempo di risposta in millisecondi
- **Size**: la dimensione del payload

> **Consiglio**: usa il tab **"Pretty"** nel pannello Body per visualizzare il JSON formattato e leggibile.

---

## 5. Esercizio — RunController Completo

### Obiettivo

Costruire un `RunController` completo che gestisca una lista di oggetti `Run` in memoria, utilizzando il Record Java definito al Giorno 3. L'esercizio integra tutto ciò che è stato visto nel Giorno 4.

---

### Setup — Il Record Run

Prima di tutto, assicurati di avere il Record `Run` (definito al Giorno 3) nel tuo progetto:

```java
// src/main/java/com/example/demo/model/Run.java
package com.example.demo.model;

import java.time.LocalDateTime;

public record Run(
    Integer id,
    String title,
    LocalDateTime startedOn,
    LocalDateTime completedOn,
    Double miles,
    Location location
) {
    // Validazione nel costruttore canonico
    public Run {
        if (miles != null && miles < 0) {
            throw new IllegalArgumentException("I chilometri non possono essere negativi");
        }
        if (completedOn != null && startedOn != null && completedOn.isBefore(startedOn)) {
            throw new IllegalArgumentException("La data di fine non può precedere quella di inizio");
        }
    }
}
```

```java
// src/main/java/com/example/demo/model/Location.java
package com.example.demo.model;

public enum Location {
    INDOOR,
    OUTDOOR
}
```

---

### Il Controller da Implementare

```java
// src/main/java/com/example/demo/controller/RunController.java
package com.example.demo.controller;

import com.example.demo.model.Run;
import com.example.demo.model.Location;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/runs")
public class RunController {

    // Lista in memoria (simula un database)
    private final List<Run> runs = new ArrayList<>();

    // Dati iniziali caricati nel costruttore
    public RunController() {
        
    }

    // GET /api/runs → lista di tutte le corse
    @GetMapping
    public ResponseEntity<List<Run>> findAll() {
        
    }

    // GET /api/runs/{id} → singola corsa per ID
    @GetMapping("/{id}")
    public ResponseEntity<Run> findById(@PathVariable Integer id) {
       
    }

    // POST /api/runs → crea una nuova corsa
    @PostMapping
    public ResponseEntity<Run> create(@RequestBody Run newRun) {
        
    }

    // PUT /api/runs/{id} → aggiorna una corsa esistente
    @PutMapping("/{id}")
    public ResponseEntity<Run> update(@PathVariable Integer id, @RequestBody Run updatedRun) {
        
    }

    // DELETE /api/runs/{id} → elimina una corsa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Optional<Run> existing = runs.stream()
                                     .filter(r -> r.id().equals(id))
                                     .findFirst();

        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        runs.remove(existing.get());
        return ResponseEntity.noContent().build();
    }
}
```

---

### Punti chiave da verificare

Prima di testare con Postman, controlla che:

1. L'applicazione sia in esecuzione (`./mvnw spring-boot:run`)
2. La porta predefinita sia `8080` (verificalo in `application.properties`)
3. Non ci siano errori in console al momento dell'avvio

---

### Test con Postman — Sequenza Consigliata

Esegui i test in questo ordine per verificare tutti gli endpoint:

**Step 1 — Leggi la lista iniziale**

```
GET http://localhost:8080/api/runs
→ Atteso: 200 OK, array con 2 corse
```

**Step 2 — Cerca per ID**

```
GET http://localhost:8080/api/runs/1
→ Atteso: 200 OK, oggetto Run con id=1

GET http://localhost:8080/api/runs/99
→ Atteso: 404 Not Found
```

**Step 3 — Crea una nuova corsa**

```
POST http://localhost:8080/api/runs
Content-Type: application/json

{
  "id": 3,
  "title": "Weekend Long Run",
  "startedOn": "2024-05-04T08:00:00",
  "completedOn": "2024-05-04T09:30:00",
  "miles": 10.5,
  "location": "OUTDOOR"
}
→ Atteso: 201 Created con l'oggetto appena creato
```

**Step 4 — Verifica l'inserimento**

```
GET http://localhost:8080/api/runs
→ Atteso: 200 OK, array con 3 corse
```

**Step 5 — Elimina una corsa**

```
DELETE http://localhost:8080/api/runs/2
→ Atteso: 204 No Content

DELETE http://localhost:8080/api/runs/99
→ Atteso: 404 Not Found
```

**Step 6 — Verifica l'eliminazione**

```
GET http://localhost:8080/api/runs
→ Atteso: 200 OK, array con 2 corse (id=1 e id=3)
```

---

### Domande di riflessione

Al termine dell'esercizio, rispondi a queste domande per consolidare la comprensione:

1. Perché usiamo `Optional<Run>` invece di restituire direttamente `null`?
2. Qual è la differenza tra `@GetMapping` su classe e su metodo?
3. Perché `@PostMapping` restituisce `201 Created` invece di `200 OK`?
4. Cosa succede se il client invia un JSON malformato nel body di una POST?
5. Perché `@DeleteMapping` restituisce `204 No Content` (senza body)?

---

> **Preview Giorno 5**: I dati attualmente salvati nella lista in memoria vengono persi ogni volta che l'applicazione si riavvia. Nel Giorno 5 collegheremo questo controller a un database reale tramite **Spring Data JPA**, rendendo la persistenza dei dati permanente.
