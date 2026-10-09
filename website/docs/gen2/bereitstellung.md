---
title: Bereitstellung durch uniWAHL OWS
sidebar_label: Bereitstellung
---

# Bereitstellung durch uniWAHL OWS

SmartLink richtet die Wahlleitung in uniWAHL ein; auf Ihrer Seite ist dafür nichts zu konfigurieren. Für Ihre Umsetzung zählen zwei feste Werte: Ein Token ist **90 Sekunden** ab seinem Zeitstempel gültig, und sein Zeitstempel darf der Plattformuhr höchstens **5 Sekunden** vorauslaufen. Jedes Wahlereignis – auch jede Testwahl – hat ein eigenes Shared Secret.

## Was Sie erhalten

Für jedes Wahlereignis übergibt Ihnen die Wahlleitung eine SmartLink-Zugangsdaten-Datei. Sie enthält:

- **electionID** – die Election-Event-ID, die in Realm-Namen, Pfad und Nachricht steht.
- **sharedSecret** – den HMAC-Schlüssel dieses Wahlereignisses.
- **domain** – den Rechnernamen der Plattform, gegebenenfalls mit Basispfad, ohne Schema.
- **Muster** der Anmelde-URL mit eingesetztem Realm sowie der Nachricht und des Tokens – mit Platzhaltern, ohne gültige Signatur.

:::danger Geheimnistragendes Dokument

Wer das Shared Secret kennt, kann für jede Person im Wählerverzeichnis einen gültigen SmartLink erzeugen. Übertragen Sie die Datei nur über einen gesicherten Kanal und legen Sie das Secret ausschließlich auf Ihren Backend-Servern ab.

:::

Die Election-Event-ID entsteht erst, wenn das Wahlereignis auf der Plattform angelegt ist. Steht in Ihrer Datei an ihrer Stelle noch der Platzhalter `<election-event-id>`, teilt Ihnen die Wahlleitung die ID nach dem Anlegen mit.

## Das Wählerverzeichnis

Die Autorisierung findet auf Seiten der Plattform statt. Jede Person, die per SmartLink wählen soll, muss deshalb im Wählerverzeichnis des Wahlereignisses enthalten sein – mit genau der user-id, die Ihr Backend später signiert. Das Wählerverzeichnis liefern Sie der Wahlleitung vor dem Anlegen des Wahlereignisses; uniWAHL übernimmt die Kennung unverändert als Keycloak-Benutzernamen. Ein SmartLink für eine unbekannte user-id schlägt fehl.

Ein SmartLink legt keine Wahlberechtigten an. Kommt eine Person nachträglich hinzu, muss sie zuerst in das Wählerverzeichnis der Plattform aufgenommen werden; wenden Sie sich dafür an die Wahlleitung.

## Ablauf der Einrichtung

1. Sie liefern der Wahlleitung das Wählerverzeichnis mit den user-ids (siehe [Empfehlungen zur user-id](./auth-token.md#empfehlungen-zur-user-id)).
2. Sie erhalten die SmartLink-Zugangsdaten-Datei und, falls dort noch nicht eingetragen, die Election-Event-ID.
3. Sie hinterlegen Secret, Domain und Election-Event-ID in Ihrem Backend und bauen den Link nach dem Muster aus [Die SmartLink-URL](./smartlink-url.md).
4. Sie prüfen die Anbindung mit einer Testperson in einer Testwahl (siehe [Test und Fehlersuche](./test-fehlersuche.md)).
5. Für die echte Wahl wiederholen Sie die Schritte 2 und 3: Sie ist ein eigenes Wahlereignis mit eigener Election-Event-ID und eigenem Secret.
