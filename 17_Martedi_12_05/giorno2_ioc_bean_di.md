# Giorno 2 — IoC, Bean e Dependency Injection

> **Obiettivo:** Comprendere il cuore di Spring — come gestisce gli oggetti al posto tuo — e applicare la Dependency Injection nei suoi 3 modi, scegliendo quello corretto.

---

## Indice

1. [IoC — Inversion of Control](#1-ioc--inversion-of-control)
2. [Bean in Spring — @Component e Stereotipi](#2-bean-in-spring--component-e-stereotipi)
3. [Dependency Injection — 3 Tipi](#3-dependency-injection--3-tipi)
4. [Esercizio — Gestione Prodotti con DI](#4-esercizio--gestione-prodotti-con-di)

---

## 1. IoC — Inversion of Control

### Teoria

#### Il problema: accoppiamento forte

Nel Java tradizionale, quando una classe ha bisogno di un'altra classe, la **crea direttamente** con `new`. Questo genera quello che si chiama **accoppiamento forte** (*tight coupling*): le classi sono legate indissolubilmente tra loro e diventa difficile testarle, modificarle o sostituirle.

```
Senza IoC:
┌──────────────────┐        crea direttamente        ┌─────────────────────────┐
│   OrderService   │  ──────────── new ────────────▶  │   NotificationService   │
└──────────────────┘                                  └─────────────────────────┘

Problema: OrderService è "agganciato" a NotificationService.
Se vuoi usare EmailNotificationService o SmsNotificationService → devi riscrivere OrderService.
```

Considera questo codice problematico:

```java
// ❌ ACCOPPIAMENTO FORTE — da evitare
public class OrderService {

    // OrderService crea direttamente la sua dipendenza
    private NotificationService notificationService = new NotificationService();

    public void processOrder(String orderId) {
        // ... logica ordine ...
        notificationService.sendConfirmation(orderId); // dipende da implementazione concreta
    }
}
```

**Cosa c'è di sbagliato?**
- `OrderService` decide autonomamente *quale* `NotificationService` usare
- Impossibile testare `OrderService` in isolamento (nei test ti trascini anche `NotificationService`)
- Per cambiare l'implementazione devi modificare `OrderService`

---

#### La soluzione: Inversion of Control

**IoC** (Inversion of Control) è un principio di design: invece di essere la classe stessa a creare le proprie dipendenze, è il **framework** (Spring) a farsene carico.

Il "controllo" della creazione degli oggetti viene **invertito**: non è più il tuo codice a gestirlo, ma Spring.

```
Con IoC:
┌────────────────────────────────────────────────────────────┐
│                    Spring ApplicationContext               │
│                    (contenitore IoC)                       │
│                                                            │
│  Crea e gestisce:                                          │
│  ┌──────────────────┐     ┌─────────────────────────┐     │
│  │   OrderService   │     │   NotificationService   │     │
│  └──────────────────┘     └─────────────────────────┘     │
│           │                           ▲                    │
│           └───────── inietta ─────────┘                    │
└────────────────────────────────────────────────────────────┘
```

> 💡 **Analogia:** Pensa all'IoC come a un ristorante. Non sei tu (il cliente) a cucinare il piatto — lo fa il cuoco (Spring). Tu dici solo cosa vuoi, e te lo portano già pronto.

---

#### ApplicationContext: il contenitore Spring

L'`ApplicationContext` è il **cuore di Spring**: è il contenitore che conosce tutti i Bean (oggetti gestiti da Spring), li crea, li configura e li collega tra loro.

```
ApplicationContext
├── Scansiona il progetto alla ricerca di @Component, @Service, @Repository, ecc.
├── Istanzia ogni Bean trovato (una sola volta, di default — Singleton)
├── Risolve le dipendenze tra Bean (chi ha bisogno di chi)
├── Inietta le dipendenze nei punti richiesti
└── Gestisce il ciclo di vita di ogni Bean
```

---

#### Ciclo di vita di un Bean

```
1. ISTANZIAZIONE
   Spring trova la classe annotata e chiama il costruttore
          ↓
2. INIEZIONE DIPENDENZE
   Spring risolve e inietta tutte le dipendenze richieste
          ↓
3. @PostConstruct (opzionale)
   Se presente, viene eseguito il metodo di inizializzazione
          ↓
4. USO
   Il Bean è pronto ed è disponibile per tutta l'applicazione
          ↓
5. @PreDestroy (opzionale)
   Prima della distruzione, viene chiamato il metodo di cleanup
          ↓
6. DISTRUZIONE
   Al termine dell'applicazione, Spring distrugge il Bean
```

---

### Esempio pratico

```java
//  CON IoC — Spring gestisce la creazione e l'iniezione

// NotificationService è un Bean gestito da Spring
@Service
public class NotificationService {
    public void sendConfirmation(String orderId) {
        System.out.println("Conferma inviata per ordine: " + orderId);
    }
}

// OrderService riceve NotificationService dal contenitore Spring
@Service
public class OrderService {

    private final NotificationService notificationService;

    // Spring inietta automaticamente NotificationService qui
    public OrderService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void processOrder(String orderId) {
        System.out.println("Elaborazione ordine: " + orderId);
        notificationService.sendConfirmation(orderId);
    }
}
```

**Differenza chiave rispetto a prima:**

| | Senza IoC | Con IoC |
|---|---|---|
| Chi crea `NotificationService`? | `OrderService` stesso (con `new`) | Spring |
| Chi decide quale implementazione usare? | `OrderService` | Spring (configurabile) |
| Testabilità | Difficile | Facile (puoi iniettare un Mock) |
| Accoppiamento | Forte | Debole |

---

## 2. Bean in Spring — @Component e Stereotipi

### Teoria

#### Cos'è un Bean?

Un **Bean** è semplicemente un oggetto Java la cui creazione e gestione è affidata a Spring. Non è una classe speciale o magica — è una normale classe Java che Spring "conosce" e amministra all'interno del suo contenitore.

Per dire a Spring *"gestisci tu questa classe"*, si usano le **annotation**.

---

#### @Component — l'annotation base

`@Component` è l'annotation generica che segnala a Spring di registrare la classe come Bean nel contenitore IoC.

```java
@Component
public class WelcomeMessage {

    public String getMessage() {
        return "Benvenuto nell'applicazione!";
    }
}
```

Quando Spring avvia l'applicazione, fa una **component scan**: scansiona tutti i package a partire dal package principale (quello con `@SpringBootApplication`) e raccoglie tutte le classi annotate con `@Component` o i suoi stereotipi.

---

#### Stereotipi specializzati

Spring fornisce annotation più specifiche di `@Component`, chiamate **stereotipi**. Tecnicamente si comportano allo stesso modo (registrano il Bean), ma comunicano **l'intento** della classe e in alcuni casi abilitano comportamenti aggiuntivi.

| Annotation | Usata per | Layer |
|---|---|---|
| `@Component` | Classe generica gestita da Spring | Qualsiasi |
| `@Service` | Logica di business / elaborazione dati | Service Layer |
| `@Repository` | Accesso al database / persistenza | Repository Layer |
| `@Controller` | Gestione richieste HTTP (MVC classico) | Controller Layer |
| `@RestController` | Gestione richieste HTTP + risposta JSON | Controller Layer |

> 💡 **Regola pratica:** usa sempre lo stereotipo più specifico. Se la classe fa accesso al DB → `@Repository`. Se contiene logica → `@Service`. Rende il codice più leggibile e Spring può applicare comportamenti specifici (es. `@Repository` gestisce automaticamente le eccezioni JPA).

---

#### @Bean — definizione manuale con @Configuration

A volte devi registrare come Bean una classe che **non puoi annotare direttamente** (es. classi di librerie esterne, oppure classi che richiedono configurazione specifica). In questi casi si usa `@Bean` all'interno di una classe `@Configuration`.

```java
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        // RestTemplate è una classe di Spring, non possiamo annotarla noi
        return new RestTemplate();
    }
}
```

`@Configuration` dice a Spring: *"questa classe contiene definizioni di Bean"*.  
`@Bean` dice a Spring: *"il valore restituito da questo metodo è un Bean da registrare"*.

---

#### @PostConstruct — esecuzione dopo l'inizializzazione

`@PostConstruct` marca un metodo che deve essere eseguito **una sola volta**, subito dopo che Spring ha creato il Bean e iniettato tutte le dipendenze. È utile per inizializzare dati, fare log di avvio, o pre-caricare risorse.

```
Ciclo di vita con @PostConstruct:

costruttore() → iniezione dipendenze → @PostConstruct → pronto per l'uso
```

### Esempio pratico

```java
package com.esempio.demo.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

// @Component registra questa classe come Bean in Spring
@Component
public class WelcomeMessage {

    private String messaggio;

    // Viene eseguito dopo la creazione del Bean e l'iniezione delle dipendenze
    @PostConstruct
    public void init() {
        this.messaggio = "🚀 Applicazione avviata con successo!";
        System.out.println("[WelcomeMessage] Bean inizializzato.");
    }

    public String getMessaggio() {
        return messaggio;
    }
}
```

```java
package com.esempio.demo.service;

import org.springframework.stereotype.Service;

// @Service è uno stereotipo di @Component — semanticamente indica logica di business
@Service
public class SalutoService {

    public String saluta(String nome) {
        return "Ciao, " + nome + "! Benvenuto nel sistema.";
    }
}
```

```java
package com.esempio.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// @Configuration indica che questa classe definisce Bean manualmente
@Configuration
public class AppConfig {

    // RestTemplate non possiamo annotarla noi → la definiamo come @Bean
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

**Come verificare che i Bean sono stati creati:**  
In fase di sviluppo puoi aggiungere temporaneamente un `CommandLineRunner` per stampare i Bean registrati nel contenitore:

```java
package com.esempio.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(DemoApplication.class, args);

        // Stampa tutti i Bean registrati (utile per debug/apprendimento)
        System.out.println("=== BEAN REGISTRATI ===");
        String[] beanNames = ctx.getBeanDefinitionNames();
        for (String name : beanNames) {
            if (!name.contains("org.springframework")) { // filtra quelli interni di Spring
                System.out.println("  → " + name);
            }
        }
    }
}
```

---

## 3. Dependency Injection — 3 Tipi

### Teoria

#### Cos'è la Dependency Injection?

La **Dependency Injection** (DI) è il meccanismo con cui Spring mette in pratica il principio IoC: invece di lasciare che le classi creino le proprie dipendenze, è Spring a *iniettarle* dall'esterno.

```
SENZA DI:
OrderService costruisce le sue dipendenze  →  accoppiamento forte

CON DI:
Spring crea le dipendenze e le "inietta" in OrderService  →  accoppiamento debole
```

Spring supporta **3 tipi** di Dependency Injection:

---

#### Tipo 1 — Iniezione via Costruttore  (RACCOMANDATA)

Le dipendenze vengono passate come **parametri del costruttore**. È il metodo raccomandato dal team Spring e dalla comunità Java.

```java
@Service
public class OrderService {

    private final NotificationService notificationService; // final → immutabile

    // Spring vede il costruttore e inietta automaticamente NotificationService
    public OrderService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
```

> 💡 A partire da Spring 4.3, se una classe ha **un solo costruttore**, `@Autowired` è opzionale — Spring lo usa automaticamente.

**Perché è la scelta migliore:**

| Vantaggio | Spiegazione |
|---|---|
| **Immutabilità** | Il campo può essere `final` — non cambia mai dopo la costruzione |
| **Testabilità** | Nei test puoi passare un Mock direttamente nel costruttore |
| **Dipendenze esplicite** | Guardando il costruttore sai subito di cosa ha bisogno la classe |
| **Fail-fast** | Se manca una dipendenza, Spring fallisce all'avvio (non a runtime) |

---

#### Tipo 2 — Iniezione via Setter

Le dipendenze vengono iniettate tramite **metodi setter** annotati con `@Autowired`.

```java
@Service
public class OrderService {

    private NotificationService notificationService; // NON può essere final

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
```

**Quando usarla:** solo per dipendenze **opzionali**, che potrebbero non essere disponibili. In tutti gli altri casi, preferisci il costruttore.

---

#### Tipo 3 — Iniezione via Campo con @Autowired ⚠️ (da evitare in produzione)

Le dipendenze vengono iniettate direttamente sul **campo** della classe tramite `@Autowired`.

```java
@Service
public class OrderService {

    @Autowired  // Spring inietta direttamente nel campo
    private NotificationService notificationService; // NON può essere final
}
```

**Perché evitarla:**

| Problema | Spiegazione |
|---|---|
| **Non testabile facilmente** | Per iniettare un Mock nei test serve Reflection o framework appositi |
| **Campo non può essere `final`** | Perde l'immutabilità |
| **Dipendenze nascoste** | Non capisci di cosa ha bisogno la classe senza leggere tutto il corpo |
| **IntelliJ la evidenzia** | L'IDE stesso avverte che è sconsigliata |

> ⚠️ **Regola d'oro:** usa sempre l'iniezione via **costruttore**. L'iniezione via campo può essere usata in esempi veloci o prototipi, ma non in codice di produzione.

---

#### Confronto riepilogativo

```
┌──────────────────────┬───────────────┬──────────────┬───────────────────── ┐
│ Tipo                 │ Immutabilità  │ Testabilità  │ Consigliato          │
├──────────────────────┼───────────────┼──────────────┼───────────────────── ┤
│ Costruttore          │               │              │   Sì, sempre         │
│ Setter               │      ❌       │              │ ⚠️ Solo se opzionale│
│ Campo (@Autowired)   │      ❌       │      ❌      │  ❌ No, evitare     │
└──────────────────────┴───────────────┴──────────────┴───────────────────── ┘
```

---

### Esempio pratico

Esempio completo: `NotificationService` iniettato in `OrderService` via costruttore.

```java
// --- NotificationService.java ---
package com.esempio.demo.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendConfirmation(String orderId) {
        System.out.println("📧 Notifica inviata — Ordine confermato: " + orderId);
    }
}
```

```java
// --- OrderService.java ---
package com.esempio.demo.service;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

    // final → immutabile, buona pratica con iniezione via costruttore
    private final NotificationService notificationService;

    //  Iniezione via costruttore — Spring fornisce NotificationService automaticamente
    public OrderService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void processOrder(String orderId) {
        System.out.println("⚙️  Elaborazione ordine in corso: " + orderId);
        notificationService.sendConfirmation(orderId);
        System.out.println(" Ordine completato: " + orderId);
    }
}
```

```java
// --- DemoApplication.java ---
package com.esempio.demo;

import com.esempio.demo.service.OrderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // CommandLineRunner: eseguito all'avvio, utile per testare la logica da console
    @Bean
    public CommandLineRunner demo(OrderService orderService) {
        return args -> {
            orderService.processOrder("ORD-001");
            orderService.processOrder("ORD-002");
        };
    }
}
```

**Output atteso in console:**
```
⚙️  Elaborazione ordine in corso: ORD-001
📧 Notifica inviata — Ordine confermato: ORD-001
 Ordine completato: ORD-001
⚙️  Elaborazione ordine in corso: ORD-002
📧 Notifica inviata — Ordine confermato: ORD-002
 Ordine completato: ORD-002
```

---

## 4. Esercizio — Gestione Prodotti con DI

### Obiettivo

Creare un mini-sistema di gestione prodotti che mette insieme tutti i concetti imparati:
- `PrezzoService` — calcola il prezzo scontato
- `ProdottoService` — gestisce la lista prodotti, usa `PrezzoService` via DI
- `@PostConstruct` — inizializza i dati alla partenza
- `CommandLineRunner` — stampa i risultati in console

---

### Struttura del progetto

```
src/main/java/com/esempio/demo/
├── DemoApplication.java
└── service/
    ├── PrezzoService.java
    └── ProdottoService.java
```

---

### Traccia

**Step 1 — Crea `PrezzoService`**

Crea un `@Service` con un metodo `calcolaPrezzioScontato(double prezzoOriginale, int percentualeSconto)` che restituisce il prezzo dopo lo sconto.

**Step 2 — Crea `ProdottoService`**

Crea un `@Service` che:
- Ha una `List<String>` per i nomi prodotti e una `Map<String, Double>` per i prezzi
- Inietta `PrezzoService` via costruttore
- Usa `@PostConstruct` per popolare la lista con almeno 3 prodotti e i loro prezzi
- Ha un metodo `stampaCatalogo(int percentualeSconto)` che stampa ogni prodotto con il prezzo scontato

**Step 3 — Avvia da `CommandLineRunner`**

In `DemoApplication`, usa un `CommandLineRunner` per chiamare `stampaCatalogo(20)` (sconto del 20%).

---

### Soluzione

```java
// --- PrezzoService.java ---
package com.esempio.demo.service;

import org.springframework.stereotype.Service;

@Service
public class PrezzoService {

    /**
     * Calcola il prezzo dopo aver applicato uno sconto percentuale.
     *
     * @param prezzoOriginale  Prezzo prima dello sconto
     * @param percentualeSconto Sconto da applicare (es. 20 = 20%)
     * @return Prezzo finale scontato
     */
    public double calcolaPrezzoScontato(double prezzoOriginale, int percentualeSconto) {
        double moltiplicatore = 1.0 - (percentualeSconto / 100.0);
        return Math.round(prezzoOriginale * moltiplicatore * 100.0) / 100.0;
    }
}
```

```java
// --- ProdottoService.java ---
package com.esempio.demo.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProdottoService {

    // Dipendenza iniettata via costruttore 
    private final PrezzoService prezzoService;

    private List<String> prodotti;
    private Map<String, Double> prezzi;

    //  Iniezione via costruttore — Spring fornisce PrezzoService automaticamente
    public ProdottoService(PrezzoService prezzoService) {
        this.prezzoService = prezzoService;
    }

    // Viene eseguito UNA SOLA VOLTA dopo la creazione del Bean
    @PostConstruct
    public void init() {
        prodotti = new ArrayList<>();
        prezzi = new HashMap<>();

        // Popola il catalogo iniziale
        prodotti.add("Laptop Pro 15\"");
        prezzi.put("Laptop Pro 15\"", 1299.99);

        prodotti.add("Mouse Wireless");
        prezzi.put("Mouse Wireless", 49.90);

        prodotti.add("Tastiera Meccanica");
        prezzi.put("Tastiera Meccanica", 129.50);

        prodotti.add("Monitor 4K 27\"");
        prezzi.put("Monitor 4K 27\"", 549.00);

        System.out.println("[ProdottoService] Catalogo inizializzato con " + prodotti.size() + " prodotti.");
    }

    /**
     * Stampa il catalogo prodotti con il prezzo originale e quello scontato.
     *
     * @param percentualeSconto Percentuale di sconto da applicare
     */
    public void stampaCatalogo(int percentualeSconto) {
        System.out.println("\n========================================");
        System.out.println("  CATALOGO PRODOTTI — Sconto: " + percentualeSconto + "%");
        System.out.println("========================================");

        for (String prodotto : prodotti) {
            double prezzoOriginale = prezzi.get(prodotto);
            double prezzoScontato = prezzoService.calcolaPrezzoScontato(prezzoOriginale, percentualeSconto);

            System.out.printf("  %-25s  %.2f€  →  %.2f€%n",
                    prodotto, prezzoOriginale, prezzoScontato);
        }

        System.out.println("========================================\n");
    }
}
```

```java
// --- DemoApplication.java ---
package com.esempio.demo;

import com.esempio.demo.service.ProdottoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // CommandLineRunner viene eseguito subito dopo il boot dell'applicazione
    @Bean
    public CommandLineRunner avvia(ProdottoService prodottoService) {
        return args -> {
            prodottoService.stampaCatalogo(20); // Applica sconto del 20%
        };
    }
}
```

**Output atteso:**

```
[ProdottoService] Catalogo inizializzato con 4 prodotti.

========================================
  CATALOGO PRODOTTI — Sconto: 20%
========================================
  Laptop Pro 15"            1299.99€  →  1039.99€
  Mouse Wireless              49.90€  →    39.92€
  Tastiera Meccanica         129.50€  →   103.60€
  Monitor 4K 27"             549.00€  →   439.20€
========================================
```

---

### Domande di verifica

Prima di passare al Giorno 3, assicurati di saper rispondere a queste domande:

1. Cosa significa "accoppiamento forte" e perché è un problema?
2. Qual è la differenza tra `@Component`, `@Service` e `@Repository`?
3. In quali casi usi `@Bean` invece di `@Component`?
4. Perché l'iniezione via costruttore è preferita a quella via campo (`@Autowired` sul campo)?
5. Quando viene eseguito un metodo annotato con `@PostConstruct`?
6. Cos'è il `ApplicationContext` e che ruolo ha?

---

## Riepilogo Giorno 2

| Argomento | Concetto chiave |
|---|---|
| **IoC** | Il controllo della creazione degli oggetti è invertito: Spring gestisce tutto al posto tuo |
| **ApplicationContext** | Il contenitore IoC di Spring: crea, configura e collega tutti i Bean |
| **Bean** | Qualsiasi oggetto Java la cui gestione è delegata a Spring |
| **@Component** | Registra una classe come Bean generico nel contenitore Spring |
| **@Service** | Stereotipo di `@Component` per classi con logica di business |
| **@Repository** | Stereotipo di `@Component` per classi che accedono al database |
| **@Bean + @Configuration** | Definizione manuale di un Bean per classi esterne o che richiedono setup |
| **@PostConstruct** | Metodo eseguito una sola volta dopo la creazione e l'iniezione del Bean |
| **DI via costruttore** | Il modo corretto: dipendenze `final`, esplicite, testabili |
| **DI via setter** | Accettabile solo per dipendenze opzionali |
| **DI via campo** | Sconsigliata in produzione: non testabile, non immutabile |

---

> 📌 **Prossimo passo — Giorno 3:** Java Records e Modellazione del Dominio — come rappresentare in modo pulito le entità del sistema con il minimo boilerplate.
