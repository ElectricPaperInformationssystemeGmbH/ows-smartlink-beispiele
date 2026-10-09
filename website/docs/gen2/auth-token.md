---
title: Format des auth-token
---

# Format des auth-token

Das Token hat den folgenden Aufbau:

```text
khmac:///sha-256;<code>/<message>
```

Der `<code>` ist der HMAC-SHA256 über `<message>`, gebildet mit dem Shared Secret und ausgegeben als Hexadezimalzeichenkette in Kleinbuchstaben, nach FIPS 198-1.

## Aufbau der Nachricht

Die signierte Nachricht besteht aus genau fünf durch Doppelpunkt getrennten Feldern:

```text
<user-id: String>:AuthEvent:<election-id: String>:vote:<timestamp: Int>
```

Beispiel:

```text
071f16580fa1dc2406c49cce19f026fbdedbca896bdcc6b2eb873ee3f9fdbc7f:AuthEvent:047e91d3-bab7-4007-bb13-5926fef505b7:vote:1791532800
```

Die Felder werden über ihre Position erkannt, nicht über Namen. Ein bestehender GEN1-Generator behält deshalb seinen Aufbau und ändert lediglich den Wert im dritten Feld.

| Feld | Bedeutung und Regeln |
|---|---|
| `user-id` | Eindeutige Kennung der wählenden Person. Sie muss **zeichengleich** der Kennung entsprechen, die Sie mit dem Wählerverzeichnis an die Wahlleitung geliefert haben; uniWAHL übernimmt diese Kennung unverändert als Benutzernamen in Keycloak. E-Mail-Adressen werden nicht ausgewertet. Die Kennung muss für eine Person und Wahl stabil sein und darf keinen Doppelpunkt enthalten, da dieser die signierten Felder trennt. |
| `AuthEvent` | Feste Zeichenkette. Muss exakt so erscheinen. |
| `election-id` | Bindet das Token an ein Wahlereignis: die Election-Event-ID, eine UUID wie `047e91d3-bab7-4007-bb13-5926fef505b7`. Sie steht in der Zugangsdaten-Datei als **electionID**. Der Wert muss mit dem Pfadsegment `/election/<election-id>/` und mit dem Ende des Realm-Namens übereinstimmen. Verarbeiten Sie ihn als Zeichenkette, nicht als Zahl. |
| `vote` | Feste Zeichenkette. Bezeichnet zusammen mit `AuthEvent` die erteilte Berechtigung. |
| `timestamp` | Unix-Zeitstempel in Sekunden zum Zeitpunkt der Erzeugung – eine ganze Zahl seit 1970-01-01 00:00:00 UTC. Keine lokale Datums- oder Zeitzeichenkette. Auf diesem Wert beruht der Ablauf des Tokens; die Uhren beider Seiten müssen daher hinreichend synchron laufen. |

## Empfehlungen zur user-id

Die user-id ist das Feld, über das die Zuordnung zur wahlberechtigten Person erfolgt. Ein falscher Wert bedeutet im schlimmsten Fall, dass jemand an fremder Stelle wählt. Für die Bildung dieses Werts gelten folgende Empfehlungen:

1. Verwenden Sie nicht die Klarkennung, sondern einen Hash einer internen, nur Ihrer Anwendung bekannten Kennung – etwa der Mitglieds- oder Matrikelnummer.
2. Verwenden Sie SHA-256; MD5 und SHA-1 sind aus Sicherheitsgründen zu vermeiden.
3. Hashen Sie die Kennung nicht direkt, sondern zusammen mit einem Salt. Der Salt ist ein weiteres Geheimnis, das ausschließlich auf Ihren Servern liegt.
4. Lassen Sie den Salt je Wahl variieren – zum Beispiel über eine eigene Wahlkennung Ihrer Anwendung. Die Election-Event-ID eignet sich dafür nicht: Sie entsteht erst, wenn das Wahlereignis angelegt ist, das Wählerverzeichnis mit den user-ids muss aber vorher vorliegen.
5. Der Wert muss innerhalb einer Wahl stabil bleiben, damit Stimmänderungen derselben Person zugeordnet werden können.
6. Verwenden Sie nur Kleinbuchstaben, Ziffern und Bindestriche – die Hex-Darstellung eines Hashes erfüllt das. Leerzeichen sind möglich, aber eine häufige Fehlerquelle bei der URI-Kodierung.
7. Werden SmartLink und PIN/TAN für dieselbe Wahl angeboten, ist die user-id zugleich die PIN, die auf dem Anschreiben der Person steht; geheim bleibt dann allein die TAN. Wählen Sie in diesem Fall eine Kennung, die sich abtippen lässt.
