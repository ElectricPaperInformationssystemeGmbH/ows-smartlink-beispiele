---
title: Umstieg von GEN1 auf GEN2
---

# Umstieg von GEN1 auf GEN2

Für Integrationen, die bereits gegen die erste Generation arbeiten, ist die gute Nachricht kurz: Das Token bleibt, wie es ist. Umschlag, HMAC-Verfahren, Feldreihenfolge und die Vorgabe von 90 Sekunden Gültigkeit sind unverändert. Die HMAC-Logik eines bestehenden Generators kann in aller Regel eins zu eins übernommen werden.

![Was sich zwischen GEN1 und GEN2 ändert – und was nicht](/img/gen2/gen1-gen2.png)

*Abbildung 3: Was sich zwischen GEN1 und GEN2 ändert – und was nicht*

Ein reiner DNS-Umzug ist es dennoch nicht. Die folgenden Punkte sind zu prüfen, bevor einer Integration bestätigt wird, dass sie einfach auf GEN2 zeigen kann:

| Bereich | Erste Generation | Zweite Generation |
|---|---|---|
| Login-URL | `https://<host>/election/<election-id>/public/login?auth-token=…` | `https://<domain>/realms/tenant-<mandant>-event-<election-event-id>/election/<election-event-id>/public/login?auth-token=…` |
| Zuständigkeit | IAM und Alt-Oberfläche | Keycloak-Realm-Ressource und OIDC-Client der Wahlkabine |
| election-id | IAM-AuthEvent-ID, meist numerisch | Election-Event-ID (UUID), Teil des Realm-Namens; steht in der Zugangsdaten-Datei |
| Zeitprüfung | Ablaufprüfung | Ablaufprüfung, zusätzlich Abweisung von Token mit Zeitstempel in der Zukunft |
| Zusatzfelder | IAM-Feld- und Pipeline-Mechanik | Entfallen |
| Weitere Prüfungen | IAM-spezifisch, unter anderem Eltern-Wahl und Stimmenzähler | Nicht durch diesen Endpunkt; er authentifiziert in Keycloak und übergibt an die Wahlkabine |
| Fehler | IAM-Authentifizierungsfehler | 404 bei abgeschaltetem oder fehlkonfiguriertem Realm, generische 401 bei Authentifizierungsfehlern |

## Wann eine bestehende Integration ohne Weiteres umziehen kann

Umstiegsfähig ist eine Integration, die ausschließlich das Standard-HMAC-Token erzeugt, eine stabile und im Wählerverzeichnis vorhandene user-id verwendet und auf keine der GEN1-spezifischen Prüfungen angewiesen ist – also nicht auf OTP-Felder, Passwörter, Captchas, Bild- oder Dictionary-Felder, IAM-Pipelines oder das Verhalten von Eltern-Wahlen. In diesem Fall genügen die neue URL und die Umstellung auf die Election-Event-ID.

## Checkliste für die Umstellung

1. Die SmartLink-Zugangsdaten-Datei des neuen Wahlereignisses von der Wahlleitung entgegennehmen (electionID, sharedSecret, domain, URL-Muster).
2. Election-Event-ID im Pfad `/election/<election-id>/` und im dritten Feld der signierten Nachricht eintragen – beide Werte müssen übereinstimmen. Die ID ist eine UUID mit Bindestrichen; ein Generator, der die election-id als Zahl führt, muss auf Zeichenkette umgestellt werden.
3. Die URL-Bildung um das Segment `/realms/<realm>` ergänzen und die Angabe domain unverändert übernehmen, auch wenn sie einen Basispfad enthält. Die HMAC-Berechnung selbst bleibt unverändert.
4. URI-Kodierung des auth-token sicherstellen, falls sie bisher unterblieben ist – Leerzeichen als `%20`, nicht als `+`.
5. Prüfen, ob GEN1-Zusatzfelder verwendet wurden. Sie entfallen in GEN2 ersatzlos; die Anmeldung stützt sich allein auf das Token.
6. Zeitsynchronisation der Backend-Server prüfen – GEN2 weist zusätzlich zu weit in der Zukunft liegende Token ab.
7. Das neue Shared Secret hinterlegen und den Ablauf mit einer Testperson in einer Testwahl vollständig durchspielen.

:::note Wenn die alte Adresse erhalten bleiben muss

Soll der bisherige Wurzelpfad ohne `/realms`-Segment weiter funktionieren, ist eine Weiterleitung erforderlich, die die öffentliche URL auf die Realm-URL des jeweiligen Wahlereignisses abbildet. Ob und wie das möglich ist, klären Sie vorab mit uniWAHL OWS.

:::
