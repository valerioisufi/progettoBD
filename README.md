# Progetto Basi di Dati - Language School
Applicazione CLI in Java per la gestione di corsi di lingua inglese.

## Requisiti di Sistema
Per poter compilare ed eseguire correttamente il progetto, sono necessari:
- **Java 17** o versione successiva
- **Maven** per la gestione delle dipendenze e l'esecuzione

## Compilazione ed Esecuzione
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="io.github.valerioisufi.Main"
````


### Avvio del Database (Docker)
Il progetto include un file `docker-compose.yml` che avvia un container MariaDB ed esegue automaticamente lo script `schema.sql` per creare le tabelle e popolare i dati iniziali.
```bash
docker compose up -d
```