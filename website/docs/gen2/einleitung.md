---
title: Was ist SmartLink?
sidebar_label: Einführung
slug: /gen2
---

# SmartLink in der zweiten Generation

Diese Dokumentation beschreibt den SmartLink-Authentifizierungsmechanismus in der zweiten Generation (GEN2) der uniWAHL OWS-Plattform: was er ist, welchen Nutzen er bietet, wie er funktioniert und wie er in eine bestehende Anwendung eingebunden wird.

Diese Seite und [Funktionsweise](./funktionsweise.md) erklären Nutzen und Ablauf fachlich. Ab [Die SmartLink-URL](./smartlink-url.md) folgen die technischen Angaben für die Umsetzung im Backend der externen Anwendung; lauffähigen Code finden Sie unter [Referenzimplementierungen](./referenzimplementierungen.mdx). Den Umstieg von einer bestehenden Integration beschreibt [Umstieg von GEN1 auf GEN2](./umstieg-gen1.md).

## Was ist SmartLink?

SmartLink ist ein Single-Sign-on-Verfahren (SSO) der uniWAHL OWS-Plattform auf Basis eines schlüsselgebundenen HMAC-Authentifizierungstokens. Damit können Wählerinnen und Wähler, die in Ihrer eigenen Anwendung – Mitgliederportal, Intranet, Campus-System – bereits angemeldet sind, direkt in die Wahlkabine einer bestimmten Wahl geleitet werden, ohne sich ein zweites Mal anmelden zu müssen.

Ihr Backend erzeugt dazu ein kurzlebiges Token, das mit einem nur zwischen Ihren Servern und uniWAHL OWS bekannten **Shared Secret** signiert ist. Die Wählerin oder der Wähler folgt einem Link, der dieses Token trägt; uniWAHL OWS prüft es und meldet die Person an.

### Authentifizierung, nicht Autorisierung

Die Rollenteilung: Ihre Anwendung übernimmt die **Authentifizierung** – sie entscheidet, *wer* die Person ist. uniWAHL OWS übernimmt die **Autorisierung** – es entscheidet anhand des Wählerverzeichnisses, ob diese Person *stimmberechtigt* ist und welche Stimmzettel ausgegeben werden. Daraus folgt: Die Wählerin oder der Wähler muss vor dem Aufruf des SmartLinks bereits im Wählerverzeichnis der Onlinewahl mit der zugehörigen Stimmzettelzuweisung enthalten sein. Ein SmartLink legt keine neuen Wahlberechtigten an.

:::info Weitere SSO-Verfahren

Die Plattform unterstützt außerdem IdP-initiiertes SSO nach SAML 2.0 sowie OpenID Connect. Betreiben Sie bereits einen SAML-Identity-Provider, ist in der Regel dieses Verfahren die passendere Wahl.

:::

## Nutzen

Jede Online-Wahl muss den Zugang regeln: Wahlberechtigte müssen eindeutig als berechtigt erkennbar sein, ohne dass ihre Stimmabgabe rückverfolgbar wird. Ein etablierter Weg dorthin ist die Übergabe bzw. der Versand individueller Zugangsdaten. SmartLink bietet dafür einen alternativen Weg.

| Aspekt | Wirkung in der Praxis |
|---|---|
| Kein zweiter Anmeldevorgang | Die Wählerin oder der Wähler ist bereits in Ihrem Portal angemeldet. Ein Klick auf „Abstimmen“ genügt – die Wahlkabine öffnet sich angemeldet. |
| Kein Versand von Zugangsdaten | Keine Wahlbenachrichtigung mit TAN, kein Nachversand. Druck-, Porto- und Supportkosten entfallen ebenso wie die Rückfragen „Ich habe keine Zugangsdaten erhalten“. |
| Datensparsamkeit | Als user-id genügt ein salted SHA-256-Hash Ihrer internen Kennung. Die Plattform erhält damit keine Klarnamen oder Mitgliedsnummern, sondern nur ein Pseudonym. |
| Bestehende Identitäten | Ihre vorhandene Benutzerverwaltung bleibt führend. Die pseudonymen Kennungen erreichen das Wahlsystem einmalig mit dem Wählerverzeichnis, das Sie der Wahlleitung liefern. |
| Geringer Integrationsaufwand | Zu implementieren ist ein HMAC über eine Zeichenkette aus fünf Feldern. Es sind weder eine SAML-Föderation noch die Registrierung eines OIDC-Clients erforderlich. [Referenzimplementierungen](./referenzimplementierungen.mdx) in vier Sprachen stehen bereit. |
| Standardkryptographie | HMAC-SHA256 nach FIPS 198-1 – in jeder gängigen Programmiersprache Teil der Standardbibliothek. Kein proprietäres Verfahren, keine zusätzliche Bibliothek. |
| Investitionsschutz | Das Tokenformat ist stabil. Bestehende Generatoren behalten ihre HMAC-Logik; anzupassen sind Zieladresse und die verwendete Election-Event-ID (siehe [Umstieg von GEN1 auf GEN2](./umstieg-gen1.md)). |

### Wann SmartLink nicht das richtige Verfahren ist

Zwei Konstellationen sprechen gegen SmartLink. Erstens: Ihre Anwendung könnte den Link ausschließlich im Browser per JavaScript erzeugen. Das Shared Secret muss serverseitig bleiben – ohne eigenes Backend ist SmartLink nicht einsetzbar. Zweitens: Sie betreiben bereits einen standardbasierten Identity-Provider. Dann ist die SAML- oder OIDC-Anbindung der direktere Weg, sofern nicht ein bestehender SmartLink-Vertrag beibehalten werden soll.
