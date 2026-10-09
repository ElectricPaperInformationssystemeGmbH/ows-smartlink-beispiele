---
title: Die SmartLink-URL
---

# Die SmartLink-URL

Der SmartLink hat folgenden Aufbau:

```text
https://<domain>/realms/<realm>/election/<election-id>/public/login?auth-token=<auth-token>
```

Der Login-Pfad behält die aus GEN1 bekannte Form `/election/<election-id>/public/login`. Neu ist die vorangestellte Namensraum-Angabe `/realms/<realm>`, weil Keycloak die Anmeldesitzung führt. Der Parameter `auth-token` ist byteweise kompatibel zur ersten Generation.

Der Platzhalter `<domain>` ist die Angabe **domain** aus der SmartLink-Zugangsdaten-Datei (siehe [Bereitstellung](./bereitstellung.md)), ohne Schema. Sie kann einen Basispfad enthalten, unter dem die Plattform Keycloak betreibt, zum Beispiel `wahl.example.org/auth`. Übernehmen Sie den Wert unverändert: Fehlt der Basispfad, zeigt der Link auf einen Realm, den es unter dieser Adresse nicht gibt.

Der Platzhalter `<realm>` ist der Event-Realm in Keycloak in der Form `tenant-<mandant>-event-<election-event-id>`. Mandant und Election-Event-ID sind UUIDs, zum Beispiel `tenant-9b2c4e61-7a35-4f0d-8c1e-2d6a9f3b7e05-event-047e91d3-bab7-4007-bb13-5926fef505b7`. Die `<election-id>` im Pfad ist die Election-Event-ID aus dem Realm-Namen, im Beispiel also `047e91d3-bab7-4007-bb13-5926fef505b7`. Zusammensetzen müssen Sie den Realm nicht: Die Zugangsdaten-Datei enthält das URL-Muster mit eingesetztem Realm.

Ein vollständiger SmartLink sieht damit so aus (in einer Zeile):

```text
https://wahl.example.org/realms/tenant-9b2c4e61-7a35-4f0d-8c1e-2d6a9f3b7e05-event-047e91d3-bab7-4007-bb13-5926fef505b7/election/047e91d3-bab7-4007-bb13-5926fef505b7/public/login?auth-token=khmac%3A%2F%2F%2Fsha-256%3B299b0291a3322b0434cfbec45cee4d52eba7fbca31e069fa0f1b779cf62347c8%2F071f16580fa1dc2406c49cce19f026fbdedbca896bdcc6b2eb873ee3f9fdbc7f%3AAuthEvent%3A047e91d3-bab7-4007-bb13-5926fef505b7%3Avote%3A1791532800
```

Für dieselbe Wahl unterscheiden sich die SmartLinks verschiedener Personen nur im auth-token.

:::warning URI-Kodierung

Das auth-token ist ein Query-Parameter und muss URI-kodiert werden, so wie es `encodeURIComponent()` im Browser oder `urllib.parse.quote(…, safe="")` in Python tun. Ein Leerzeichen muss dabei zu `%20` werden, nicht zu `+`: Verfahren nach Formular-Kodierung – Javas `URLEncoder`, PHPs `urlencode()`, Gos `url.QueryEscape` – erzeugen ein `+`, die signierte Nachricht kommt verändert an, und die Anmeldung scheitert mit einer generischen 401. Die [Referenzimplementierungen](./referenzimplementierungen.mdx) berücksichtigen das.

:::

:::note Optionaler redirect_uri-Parameter

Der Endpunkt akzeptiert zusätzlich einen optionalen Parameter `redirect_uri`. Ist er gesetzt, muss er vom konfigurierten Keycloak-Client zugelassen sein; andernfalls schlägt die Anmeldung mit dem generischen Fehler fehl. Für die Anbindung über uniWAHL ist der Parameter nicht erforderlich; ohne ihn landet die Person in der Wahlkabine des Wahlereignisses.

:::
