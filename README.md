# uniWAHL OWS – SmartLink-Beispiele

Referenzimplementierungen für die SmartLink-Anmeldung der uniWAHL OWS. Mit einem SmartLink gelangt eine Person, die in Ihrer Anwendung bereits angemeldet ist, ohne zweite Anmeldung in die Wahlkabine.

| Ordner | Plattform | Inhalt |
|---|---|---|
| [`gen2/`](gen2) | zweite Generation (GEN2) | Java, Go, Rust, PHP |

Die fachliche Beschreibung steht im Dokument „SmartLink Authentifizierung“ der jeweiligen Generation, das Sie von uniWAHL OWS erhalten.

## Zweite Generation (GEN2)

### Aufbau

```
https://<domain>/realms/<realm>/election/<electionID>/public/login?auth-token=<auth-token URI-kodiert>

auth-token = khmac:///sha-256;<hmac>/<nachricht>
nachricht  = <user-id>:AuthEvent:<electionID>:vote:<unix-zeit>
hmac       = HMAC-SHA256(sharedSecret, nachricht), kleingeschriebener Hex
realm      = tenant-<mandant>-event-<electionID>
```

`domain`, `electionID`, `sharedSecret` und das URL-Muster mit dem Realm stehen in der SmartLink-Zugangsdaten-Datei, die Sie von der Wahlleitung erhalten. Die `user-id` ist die Kennung der Person aus dem Wählerverzeichnis, das Sie der Wahlleitung geliefert haben.

### Ausführen

| Sprache | Ordner | Aufruf |
|---|---|---|
| Java (ab 11) | `gen2/java` | `java SmartLinkGeneratorGen2.java` |
| Go | `gen2/go` | `go run smartlink_gen2.go` |
| Rust | `gen2/rust` | `cargo run` |
| PHP (ab 8.0) | `gen2/php` | `php smartlink_gen2.php` |

Jedes Programm prüft sich zuerst selbst mit festen Beispielwerten und gibt dann `Selbsttest bestanden` und einen Beispiel-Link aus. Alle vier Implementierungen erzeugen dabei zeichengleich dieselbe Ausgabe. Danach tragen Sie die Angaben aus Ihrer Zugangsdaten-Datei in die leeren Felder ein.

### Regeln, an denen die Anmeldung sonst scheitert

1. **Link erst beim Klick erzeugen.** Ein Token ist 90 Sekunden gültig. Erzeugen Sie Links nie auf Vorrat und verschicken Sie sie nicht per E-Mail.
2. **`auth-token` URI-kodieren, Leerzeichen als `%20`.** Ein `+`, wie es Formular-Kodierer erzeugen, verändert die signierte Nachricht.
3. **Uhren synchron halten.** Ein Zeitstempel darf der Plattformuhr höchstens 5 Sekunden vorauslaufen. Setzen Sie NTP ein.

Das Shared Secret gehört ausschließlich auf Ihre Backend-Server, nie in den Browser.

Die früher verteilten Beispiele der ersten Generation bilden die GEN1-URL und gelten für GEN2 nicht.

## Lizenz

[MIT No Attribution](LICENSE) – Sie dürfen den Code ohne Namensnennung in eigene Anwendungen übernehmen.
