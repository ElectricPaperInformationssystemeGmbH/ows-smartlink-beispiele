---
title: Test und Fehlersuche
---

# Test und Fehlersuche

Der Endpunkt lässt sich ohne Ihre Anwendung prüfen:

1. Von der Wahlleitung die Zugangsdaten einer Testwahl und die user-id einer Testperson aus deren Wählerverzeichnis erhalten.
2. Ein Token mit dem nachstehenden Befehl oder einer [Referenzimplementierung](./referenzimplementierungen.mdx) erzeugen, mit dem aktuellen Zeitstempel, und es URI-kodiert in das URL-Muster einsetzen.
3. Die entstandene URL im Browser öffnen. Erwartet wird die Weiterleitung in die Wahlkabine – angemeldet und bereit zur Stimmabgabe.

```bash
USER_ID="<user-id>"
EVENT_ID="<election-event-id>"
M="$USER_ID:AuthEvent:$EVENT_ID:vote:$(date +%s)"
CODE=$(printf '%s' "$M" | openssl dgst -sha256 -hmac "$SMART_LINK_SECRET" -r \
  | cut -d' ' -f1)
TOKEN="khmac:///sha-256;$CODE/$M"
python3 -c 'import sys,urllib.parse; print(urllib.parse.quote(sys.argv[1], safe=""))' \
  "$TOKEN"
```

Schlägt der Aufruf fehl, zeigt der Browser nur eine 404 oder eine generische 401. Die genaue Ursache steht im Serverprotokoll der Plattform, etwa in der Form `SmartLink HMAC rejected: error=TOKEN_EXPIRED`. Nennen Sie der Wahlleitung Zeitpunkt und betroffene user-id, dann lässt sich der Eintrag zuordnen.

## Typische Fehlerbilder

| Symptom | Wahrscheinliche Ursache | Zu prüfen |
|---|---|---|
| 404 vom SmartLink-Endpunkt | Realm abgeschaltet oder fehlkonfiguriert | Domain, Realm und Pfad zeichengenau nach der Zugangsdaten-Datei; stimmen sie, liegt die Ursache in der Konfiguration des Wahlereignisses (Wahlleitung). |
| Generische 401 | Token- oder Personenprüfung fehlgeschlagen | Fehlercode im Serverprotokoll der Plattform, über die Wahlleitung. |
| `MISMATCHED_EVENT` | Pfad oder Token führen eine andere Kennung als das Wahlereignis | Pfad und Nachricht müssen dieselbe Election-Event-ID tragen wie der Realm-Name. |
| `INVALID_SIGNATURE` | Falsches Secret oder nachträglich veränderte Nachricht | Exakte UTF-8-Nachricht, Secret des richtigen Wahlereignisses und URI-Kodierung (Leerzeichen als `%20`) prüfen. |
| `TOKEN_EXPIRED` / `TOKEN_IN_FUTURE` | Token zu früh erzeugt oder Uhren laufen auseinander | Erst beim Klick erzeugen; NTP prüfen. |
| `USER_NOT_FOUND` | user-id nicht im Wählerverzeichnis des Event-Realms | user-id zeichengleich mit der Kennung im gelieferten Wählerverzeichnis abgleichen; E-Mail-Adressen werden nicht ausgewertet. |
| `USER_DISABLED` | Person im Wählerverzeichnis gesperrt | Bei der Wahlleitung klären, ob die Person deaktiviert wurde, etwa nach einer Stimmabgabe auf anderem Weg. |
| `CLIENT_NOT_FOUND` / `REDIRECT_NOT_ALLOWED` | Unzulässige `redirect_uri` oder Konfiguration des Wahlereignisses | `redirect_uri` weglassen; besteht der Fehler weiter, die Wahlleitung informieren. |
