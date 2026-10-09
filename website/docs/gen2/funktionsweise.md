---
title: Funktionsweise
---

# Wie es funktioniert

Der Ablauf besteht aus drei fachlichen Schritten: Die Wählerin oder der Wähler ist in Ihrer Anwendung angemeldet, löst den Button, zum Beispiel „Abstimmen“, aus und wird über den SmartLink an uniWAHL OWS übergeben. Dort werden Authentifizierung und Autorisierung geprüft; bei Erfolg öffnet sich die Wahlkabine unmittelbar – ist die Person für mehrere Wahlen des Wahlereignisses berechtigt, zunächst mit der Auswahl der Wahlen.

![Ablauf einer SmartLink-Anmeldung](/img/gen2/ablauf.png)

*Abbildung 1: Ablauf einer SmartLink-Anmeldung*

Die einzige Interaktion der Wählerin oder des Wählers – der Klick auf „Abstimmen“ – löst die Erzeugung des Tokens in Ihrem Backend aus. Geprüft wird es in Keycloak, das die Person anschließend anmeldet und an die Wahlkabine übergibt.

## Warum der Link auf Keycloak zeigt

In dieser Generation der Plattform liegt die Identitätsverwaltung in Keycloak, mit einem eigenen Realm je Wahlereignis (Election Event). Shared Secret, Wählerverzeichnis – die Benutzer des Realms – und die Anmeldesitzung liegen dort. Folglich wird das Token auch dort geprüft. Die Wahlkabine ist lediglich ein OIDC-Client und bekommt das Shared Secret zu keinem Zeitpunkt zu sehen.

## Das auth-token im Überblick

Das auth-token wird in der externen Anwendung erzeugt und von uniWAHL OWS geprüft. Es überträgt drei Angaben kryptographisch gesichert: die Kennung der wählenden Person (user-id), die Kennung des Wahlereignisses (election-id) und den Zeitpunkt der Erzeugung (timestamp).

Diese Angaben werden mit einem schlüsselgebundenen HMAC signiert. Ein HMAC nutzt die Eigenschaft von Hashfunktionen, Einwegfunktionen zu sein: leicht in eine Richtung zu berechnen, praktisch nicht umkehrbar. Gehasht werden zwei Dinge gemeinsam – die Nachricht und das Shared Secret, das nur Ihre Anwendung und uniWAHL OWS kennen.

Weil das Secret in den Hash eingeht, wirkt der Hash als Signatur: uniWAHL OWS berechnet den HMAC über die mitgelieferte Nachricht mit dem eigenen Exemplar des Secrets neu. Stimmt das Ergebnis mit dem übermittelten Code überein, steht fest, dass die Nachricht von einer Stelle mit Zugriff auf das Secret erzeugt wurde – also von Ihrem Backend.

![Aufbau von SmartLink-URL und auth-token](/img/gen2/aufbau-url-token.png)

*Abbildung 2: Aufbau von SmartLink-URL und auth-token*

:::warning Das Secret verlässt das Backend nicht

Das auth-token darf nicht im Webclient erzeugt werden, weil das Shared Secret dabei auf das Gerät der wählenden Person gelangen würde. Es gehört ausschließlich auf Ihre Backend-Server – und auf Seiten der Plattform ebenso ausschließlich in den Serverteil.

:::
