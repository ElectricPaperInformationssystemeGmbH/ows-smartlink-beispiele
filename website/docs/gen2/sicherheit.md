---
title: Sicherheitshinweise
---

# Sicherheitshinweise

| Grundsatz | Begründung und Umsetzung |
|---|---|
| Im Backend erzeugen, nie im Browser | Das Shared Secret darf das Endgerät der wählenden Person nie erreichen. Das Token entsteht serverseitig über eine authentifizierte, TLS-geschützte Anfrage. |
| Token sind Bearer-Token | Wer den Link besitzt, kann ihn bis zum Ablauf verwenden. Links gehören daher nicht in Protokolle, Analysewerkzeuge oder Referrer, und das Gültigkeitsfenster bleibt kurz – die Vorgabe von 90 Sekunden ist empfohlen. |
| Erst beim Klick erzeugen | Links werden nicht auf Vorrat erzeugt, sondern erst, wenn die Person handelt. So wird der Link deutlich innerhalb seiner Gültigkeit eingelöst. |
| Uhren synchron halten | Abgelaufene ebenso wie zu weit in der Zukunft liegende Token werden abgewiesen. Auf beiden Seiten sollte NTP eingesetzt werden. |
| Ein Secret je Wahlereignis | Jedes Wahlereignis – auch jede Testwahl – erhält von uniWAHL OWS ein eigenes Secret. Besteht der Verdacht einer Offenlegung, informieren Sie umgehend die Wahlleitung; das Secret wird dann plattformseitig gewechselt, und Sie erhalten den neuen Wert. |
| Fehlermeldungen sind bewusst unspezifisch | Abgeschaltete oder fehlkonfigurierte Realms liefern 404, Authentifizierungsfehler eine generische 401. So wird nicht offengelegt, welche Prüfung fehlgeschlagen ist. Die genaue Ursache steht im Serverprotokoll der Plattform; uniWAHL OWS kann sie auf Anfrage auswerten. |
