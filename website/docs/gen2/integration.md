---
title: Integration in die externe Anwendung
sidebar_label: Integration
---

# Integration in die externe Anwendung

Umzusetzen sind auf Ihrer Seite die Schritte 1 bis 5 aus [Abbildung 1](./funktionsweise.md): die Wahlaktion anzeigen, den SmartLink anfordern, ihn im Backend erzeugen und zurückgeben, ihn laden.

## Die Wahlaktion anzeigen

Ausgangspunkt ist eine in Ihrer Anwendung angemeldete Person. Ihr wird eine Aktion angeboten – eine Schaltfläche oder eine vergleichbare Darstellung –, die sich auf eine bestimmte Wahl bezieht und nur während des zugehörigen Wahlzeitraums aktiv sein sollte. Die SmartLink-Anmeldung selbst prüft den Wahlzeitraum nicht: Außerhalb des Wahlzeitraums ist die Person angemeldet, kann in der Wahlkabine aber nicht abstimmen.

Je nach Wahlkonfiguration darf eine Person mehrfach abstimmen; gewertet wird dann nur die zuletzt abgegebene Stimme. Das ist keine Sicherheitslücke, sondern eine übliche Maßnahme gegen Stimmenkauf und Nötigung. Für Ihre Anwendung bedeutet es, dass die Wahlaktion nach einer Stimmabgabe nicht verschwinden sollte, solange Stimmänderungen zugelassen sind. Ihre Anwendung muss also nicht nachhalten, ob bereits gewählt wurde – es genügt, die Schaltfläche während des Wahlzeitraums anzuzeigen.

## Den SmartLink anfordern

Klickt die Person auf die Wahlaktion, fordert Ihr Frontend den SmartLink beim eigenen Backend an. Dabei gilt:

- Der Link wird nicht im Voraus erzeugt oder vorgeladen. Er enthält ein Bearer-Token mit kurzer Lebensdauer.
- Der Link wird im Backend erzeugt, nicht per JavaScript im Browser – die Erzeugung setzt Zugriff auf das Shared Secret voraus.
- Die Verbindung zwischen Frontend und Backend ist Ende-zu-Ende verschlüsselt, üblicherweise über TLS.
- Die Anfrage wird mit den in Ihrer Anwendung üblichen Verfahren authentifiziert.

## Den SmartLink erzeugen und zurückgeben

Ihr Backend prüft zunächst die Authentifizierung der Anfrage. Wird das versäumt, entsteht eine Sicherheitslücke: Ein Angreifer könnte sich einen SmartLink ausstellen lassen und an fremder Stelle wählen. Erst danach wird geprüft, ob die Person in der angeforderten Wahl stimmberechtigt und die Wahl im Wahlzeitraum ist. Anschließend werden Nachricht und HMAC gebildet und die vollständige URL zurückgegeben. Lauffähigen Code finden Sie unter [Referenzimplementierungen](./referenzimplementierungen.mdx).

## Den SmartLink laden

Für das Öffnen des Links bieten sich je nach Art Ihrer Anwendung an:

- Laden in einem Browser-Widget, wenn es sich um eine native App handelt und die Person in der App bleiben soll.
- Laden in einem neuen Browserfenster, wenn der Wechsel in den Browser unproblematisch ist.
- Laden in einem neuen Tab, wenn Ihre Website geöffnet bleiben soll.
- Ersetzen der aktuellen Adresse über `window.location.href`, wenn das Verlassen der Website unproblematisch ist.
