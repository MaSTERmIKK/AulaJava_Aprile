# Giorno 1 — Setup & Architettura Spring Boot

> **Obiettivo:** Capire cos'è Spring Boot, creare il primo progetto e configurarne le proprietà base.

---
<https://meet.masamune.it/meeting/?key=pvldty846698>

## Indice

1. [Cos'è Spring Boot — Teoria e Architettura](#1-cosè-spring-boot--teoria-e-architettura)
2. [Inizializzare un progetto con Spring Initializr](#2-inizializzare-un-progetto-con-spring-initializr)
3. [application.properties & Configurazione Base](#3-applicationproperties--configurazione-base)
4. [Esercizio Pratico — Hello Spring](#4-esercizio-pratico--hello-spring)

---

## 1. Cos'è Spring Boot — Teoria e Architettura

### Teoria

#### Storia: da Spring Framework a Spring Boot

**Spring Framework** nasce nel 2003 come alternativa leggera a Java EE. Risolveva molti problemi dell'enterprise Java, ma col tempo divenne esso stesso complesso: richiedeva file XML di configurazione voluminosi, setup manuale di ogni componente e il deploy su server applicativi esterni (come Tomcat o JBoss).

**Spring Boot** nasce nel 2014 con un obiettivo preciso: eliminare tutta quella complessità di configurazione. Il motto è *"just run"* — scrivi il codice, avvia, funziona.

```
Timeline:
2003 → Spring Framework 1.0 (XML-based config)
2013 → Spring Framework 4.0 (annotation-based config)
2014 → Spring Boot 1.0 (auto-configuration, embedded server)
2018 → Spring Boot 2.0 (Spring 5, Reactive)
2023 → Spring Boot 3.x (Java 17+, GraalVM native)
```

---

#### Differenza tra Spring e Spring Boot

| Caratteristica | Spring Framework | Spring Boot |
|---|---|---|
| Configurazione | Manuale (XML o Java config) | Automatica (auto-configuration) |
| Server | Esterno (deploy WAR) | Embedded (Tomcat incluso) |
| Dipendenze | Gestite singolarmente | Bundle via "Starter" |
| Avvio | Complesso | `main()` standard Java |
| Tempo setup | Ore | Minuti |
| Curva apprendimento | Ripida | Graduale |

> 💡 **In breve:** Spring Boot **non sostituisce** Spring Framework — lo **avvolge** e lo rende usabile immediatamente senza dover configurare ogni dettaglio.

---

#### Auto-configurazione e Convention over Configuration

Spring Boot si basa sul principio **"Convention over Configuration"**: invece di richiedere che tu specifichi tutto esplicitamente, fornisce valori predefiniti sensati che funzionano nella maggior parte dei casi. Tu intervieni solo quando vuoi cambiare il comportamento default.

**Come funziona l'auto-configurazione:**

1. All'avvio, Spring Boot scansiona le dipendenze presenti nel classpath
2. Per ogni dipendenza riconosciuta, attiva automaticamente la configurazione corrispondente
3. Se hai `spring-boot-starter-web` → configura automaticamente Tomcat, Spring MVC, Jackson (JSON)
4. Se hai `spring-boot-starter-data-jpa` → configura automaticamente Hibernate, DataSource, TransactionManager

Il meccanismo è guidato dall'annotazione `@SpringBootApplication` sulla classe principale, che è una combinazione di tre annotazioni:

```
@SpringBootApplication
    ├── @Configuration        → la classe può definire Bean
    ├── @EnableAutoConfiguration → attiva l'auto-config di Spring Boot
    └── @ComponentScan        → scansiona i package alla ricerca di componenti Spring
```

---

#### Starter Dependencies

Gli **Starter** sono dipendenze "bundle" che aggregano tutto ciò che serve per una funzionalità specifica. Invece di aggiungere 5-10 dipendenze separate con le versioni giuste, aggiungi un solo starter.

**Esempi di Starter comuni:**

| Starter | Cosa include |
|---|---|
| `spring-boot-starter-web` | Spring MVC, Tomcat embedded, Jackson JSON |
| `spring-boot-starter-data-jpa` | Hibernate, Spring Data JPA, JDBC |
| `spring-boot-starter-security` | Spring Security (autenticazione/autorizzazione) |
| `spring-boot-starter-test` | JUnit 5, Mockito, AssertJ |
| `spring-boot-starter-validation` | Hibernate Validator (Bean Validation) |
| `spring-boot-devtools` | Hot reload in sviluppo |

---

#### Server Embedded: Tomcat Integrato

Nei progetti tradizionali, dovevi:

1. Installare un server Tomcat separatamente
2. Compilare il progetto in un file `.WAR`
3. Copiare il WAR nella cartella `webapps/` di Tomcat
4. Avviare Tomcat

Con Spring Boot, **Tomcat è incluso dentro il JAR** del tuo progetto. L'applicazione è autosufficiente:

```
# Avvio tradizionale (complesso)
catalina.sh start  →  copia WAR  →  deploy

# Avvio Spring Boot (semplice)
java -jar myapp.jar   ← tutto incluso
```

Il risultato è un **fat JAR** (o "uber JAR"): un singolo file `.jar` che contiene il tuo codice, le dipendenze e il server web. Basta copiarlo su qualsiasi macchina con Java installato e farlo girare.

---

#### Ecosistema Spring (spring.io/projects)

Spring non è solo Spring Boot. È un ecosistema di progetti correlati:

| Progetto | Scopo |
|---|---|
| **Spring Boot** | Base per applicazioni standalone veloci |
| **Spring Data** | Accesso a DB (JPA, MongoDB, Redis...) |
| **Spring Security** | Autenticazione e autorizzazione |
| **Spring Cloud** | Microservizi, service discovery, config server |
| **Spring Batch** | Elaborazione batch di grandi volumi di dati |
| **Spring WebFlux** | Programmazione reattiva (non-blocking) |

---

### Esempio Pratico

La classe principale di ogni applicazione Spring Boot:

```java
package com.esempio.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication   // ← annotazione "tutto in uno"
public class DemoApplication {

    public static void main(String[] args) {
        // Avvia il contesto Spring e il server embedded
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

Output atteso in console dopo l'avvio:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

Started DemoApplication in 1.823 seconds (process running for 2.1)
Tomcat started on port(s): 8080 (http)
```

---

## 2. Inizializzare un Progetto con Spring Initializr

### Teoria

#### Spring Initializr — start.spring.io

**Spring Initializr** è uno strumento web ufficiale di Spring che genera automaticamente la struttura base di un progetto Spring Boot. Disponibile su [https://start.spring.io](https://start.spring.io).

Evita di dover creare manualmente decine di file di configurazione. In pochi click ottieni un progetto pronto per essere importato nell'IDE e subito compilabile.

---

#### Maven vs Gradle

Entrambi sono **build tool**: strumenti che gestiscono dipendenze, compilazione, test ed eseguono il packaging dell'applicazione.

| Aspetto | Maven | Gradle |
|---|---|---|
| File di config | `pom.xml` (XML) | `build.gradle` (Groovy/Kotlin) |
| Sintassi | Verbosa ma standard | Più concisa e flessibile |
| Velocità | Più lenta | Più veloce (build incrementali) |
| Diffusione | Molto diffuso in enterprise | Cresce rapidamente (Android, ecc.) |
| Curva apprendimento | Facile | Leggermente più complessa |

> **Nel corso usiamo Maven** — è lo standard nella maggior parte delle aziende italiane ed è più semplice da leggere per chi inizia.

---

#### Struttura delle Cartelle Generate

Dopo aver generato il progetto, la struttura sarà:

```
demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/esempio/demo/
│   │   │       └── DemoApplication.java   ← classe principale
│   │   └── resources/
│   │       ├── application.properties     ← configurazione
│   │       ├── static/                    ← file statici (CSS, JS)
│   │       └── templates/                 ← template HTML (Thymeleaf)
│   └── test/
│       └── java/
│           └── com/esempio/demo/
│               └── DemoApplicationTests.java
├── pom.xml                                ← dipendenze Maven
├── mvnw                                   ← Maven Wrapper (Linux/Mac)
├── mvnw.cmd                               ← Maven Wrapper (Windows)
└── .gitignore
```

**Spiegazione delle cartelle principali:**

- `src/main/java/` — il codice sorgente Java della tua applicazione
- `src/main/resources/` — file di configurazione, template, risorse statiche
- `src/test/java/` — i test automatici
- `pom.xml` — il "manifesto" del progetto: dipendenze, versioni, plugin

---

#### Analisi del pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <!-- Eredita la configurazione base di Spring Boot -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <!-- Coordinate del progetto -->
    <groupId>com.esempio</groupId>       <!-- azienda/organizzazione -->
    <artifactId>demo</artifactId>        <!-- nome del progetto -->
    <version>0.0.1-SNAPSHOT</version>    <!-- versione corrente -->

    <properties>
        <java.version>17</java.version>  <!-- versione Java da usare -->
    </properties>

    <!-- Le dipendenze del progetto -->
    <dependencies>

        <!-- Starter per API REST (include Tomcat + Spring MVC + Jackson) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Riavvio automatico durante lo sviluppo -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <!-- Dipendenze per i test (JUnit 5, Mockito) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>

    <!-- Plugin per creare il fat JAR eseguibile -->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

> 💡 **Nota:** Non è necessario specificare le versioni delle singole dipendenze Spring Boot — vengono ereditate automaticamente dal `spring-boot-starter-parent`.

---

#### Maven Wrapper (mvnw)

Il **Maven Wrapper** risolve un problema comune: "Ho Maven installato sul mio PC, ma il collega no. Come fa a compilare il progetto?"

Con `mvnw`, il progetto include al suo interno uno script che scarica automaticamente la versione corretta di Maven se non è presente sulla macchina. Ogni membro del team usa esattamente la stessa versione di Maven, senza doverla installare manualmente.

```bash
# Con Maven installato globalmente
mvn spring-boot:run

# Con Maven Wrapper (non richiede installazione)
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

---

### Esempio Pratico

**Passaggi su start.spring.io:**

```
1. Apri https://start.spring.io
2. Configura:
   - Project:      Maven
   - Language:     Java
   - Spring Boot:  3.2.x (o l'ultima stabile)
   - Group:        com.esempio
   - Artifact:     demo
   - Packaging:    Jar
   - Java:         17
3. Aggiungi dipendenze:
   - Spring Web
   - Spring Boot DevTools
4. Clicca "GENERATE"
5. Decomprimi lo ZIP nella tua cartella di lavoro
6. Apri con VS Code
```

**Prima esecuzione del progetto:**

```bash
# Spostati nella cartella del progetto
cd demo

# Avvia l'applicazione
./mvnw spring-boot:run

# Oppure, compila prima e poi esegui il JAR
./mvnw clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

---

## 3. application.properties & Configurazione Base

### Teoria

#### Ruolo e Posizione del File

Il file `application.properties` è il **file di configurazione centrale** di Spring Boot. Si trova in:

```
src/main/resources/application.properties
```

Contiene coppie `chiave=valore` che controllano il comportamento dell'applicazione: porta del server, URL del database, livelli di log, e molto altro. Spring Boot carica questo file automaticamente all'avvio.

In alternativa, esiste il formato **YAML** (`application.yml`) che usa una sintassi più leggibile con indentazione:

```yaml
# application.yml — stesso contenuto, sintassi diversa
server:
  port: 8081
spring:
  application:
    name: mia-app
```

> Nel corso utilizziamo il formato `.properties` per semplicità.

---

#### Proprietà Fondamentali

**Cambio della porta del server:**

```properties
# Default: 8080. Cambiamo a 8081 per evitare conflitti
server.port=8081
```

**Nome dell'applicazione:**

```properties
# Appare nei log e viene usato in ambienti cloud (es. Spring Cloud)
spring.application.name=demo-application
```

**Livello di logging:**

```properties
# Livelli disponibili: TRACE, DEBUG, INFO, WARN, ERROR, OFF
# Default: INFO — mostra messaggi informativi e superiori
logging.level.root=INFO

# Impostare DEBUG solo per un package specifico (più utile)
logging.level.com.esempio.demo=DEBUG
```

---

#### Profili: application-dev.properties e application-prod.properties

I **profili Spring** permettono di avere configurazioni diverse per ambienti diversi (sviluppo, test, produzione) senza cambiare il codice.

**Come funziona:**

```
application.properties          ← configurazione comune a tutti gli ambienti
application-dev.properties      ← sovrascrive/aggiunge config per lo sviluppo
application-prod.properties     ← sovrascrive/aggiunge config per la produzione
```

**Esempio di configurazione per profilo:**

```properties
# application.properties (configurazione comune)
spring.application.name=gestionale

# application-dev.properties (sviluppo)
server.port=8081
logging.level.root=DEBUG
spring.h2.console.enabled=true

# application-prod.properties (produzione)
server.port=80
logging.level.root=WARN
spring.datasource.url=jdbc:postgresql://prod-db:5432/gestionale
```

**Attivare un profilo:**

```properties
# In application.properties, specifica quale profilo attivare
spring.profiles.active=dev
```

Oppure da riga di comando:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

---

#### Gestione Errore Porta Occupata

Un errore comune all'avvio:

```
***************************
APPLICATION FAILED TO START
***************************

Description:
Web server failed to start. Port 8080 was already in use.

Action:
Identify and stop the process that's listening on port 8080 or
configure this application to listen on another port.
```

**Soluzioni:**

```bash
# Soluzione 1: Trovare e terminare il processo che usa la porta (Linux/Mac)
lsof -i :8080
kill -9 <PID>

# Soluzione 2: Trovare il processo su Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Soluzione 3 (più semplice): Cambiare porta in application.properties
server.port=8081
```

---

### Esempio Pratico

File `application.properties` completo per lo sviluppo:

```properties
# ========================
# CONFIGURAZIONE GENERALE
# ========================
spring.application.name=corso-spring-demo
server.port=8081

# ========================
# LOGGING
# ========================
# Log di livello INFO per tutta l'applicazione
logging.level.root=INFO
# Log di livello DEBUG solo per il nostro codice (utile in sviluppo)
logging.level.com.esempio=DEBUG

# ========================
# PROFILO ATTIVO
# ========================
spring.profiles.active=dev
```

---

### Esercizio

**Obiettivo:** Configurare correttamente l'ambiente creando profili distinti per sviluppo e produzione.

**Consegna:**

1. Crea (o modifica) il file `application.properties` impostando il profilo attivo su `dev`
2. Crea il file `application-dev.properties` con:
   - Porta: `9090`
   - Livello di log per `com.esempio`: `DEBUG`
3. Crea il file `application-prod.properties` con:
   - Porta: `8080`
   - Livello di log per `com.esempio`: `WARN`
4. Avvia l'applicazione e verifica in console che parta sulla porta `9090`
5. Modifica il profilo attivo su `prod`, riavvia e verifica che ora parta sulla porta `8080`

**File attesi:**

```
src/main/resources/
├── application.properties          ← spring.profiles.active=dev
├── application-dev.properties      ← porta 9090, log DEBUG
└── application-prod.properties     ← porta 8080, log WARN
```

---

## 4. Esercizio Pratico — Hello Spring

### Obiettivo

Creare da zero un progetto Spring Boot funzionante, configurarlo e verificarne il corretto avvio.

### Passaggi

**Step 1 — Crea il progetto**

Vai su [start.spring.io](https://start.spring.io) e configura:

```
Project:    Maven
Language:   Java
Group:      com.corso
Artifact:   hello-spring
Java:       17
Dependencies:
  ✅ Spring Web
  ✅ Spring Boot DevTools
```

Scarica, decomprimi e apri con il tuo IDE.

---

**Step 2 — Modifica la porta**

Nel file `src/main/resources/application.properties`:

```properties
spring.application.name=hello-spring
server.port=8081
```

---

**Step 3 — Verifica la struttura del progetto**

Controlla che siano presenti:

```
hello-spring/
├── src/main/java/com/corso/hellospring/
│   └── HelloSpringApplication.java    ← deve esistere con @SpringBootApplication
├── src/main/resources/
│   └── application.properties         ← deve avere server.port=8081
└── pom.xml                            ← deve includere spring-boot-starter-web
```

---

**Step 4 — Avvia e verifica**

```bash
cd hello-spring
./mvnw spring-boot:run
```

**Output atteso in console (cerca queste righe):**

```
Tomcat started on port(s): 8081 (http) with context path ''
Started HelloSpringApplication in 1.9 seconds
```

> ✅ Se vedi la porta `8081` nei log, l'esercizio è completato con successo!

---

**Step 5 — Test manuale (bonus)**

Apri il browser e vai su: `http://localhost:8081`

Al momento vedrai un errore `Whitelabel Error Page` — è corretto! Significa che Spring Boot è in esecuzione ma non hai ancora definito nessun endpoint. Nei prossimi giorni questo cambierà.

```
Whitelabel Error Page
This application has no explicit mapping for /error,
so you are seeing this as a fallback.
```

---

## Riepilogo Giorno 1

| Argomento | Concetto chiave |
|---|---|
| Spring Boot | Wrapper opinionato su Spring Framework con auto-config e server embedded |
| Convention over Configuration | Valori default sensati, configuri solo ciò che vuoi cambiare |
| Starter | Bundle di dipendenze per una funzionalità specifica |
| Spring Initializr | Generatore web del progetto base su start.spring.io |
| pom.xml | File Maven: contiene dipendenze, versioni e plugin del progetto |
| mvnw | Maven Wrapper: garantisce stessa versione Maven per tutto il team |
| application.properties | File di configurazione centrale: porta, log, profili, DB, ecc. |
| Profili Spring | Config separata per dev/prod, attivabile con `spring.profiles.active` |

---

> 📌 **Prossimo passo — Giorno 2:** IoC (Inversion of Control), Bean Spring e Dependency Injection.
