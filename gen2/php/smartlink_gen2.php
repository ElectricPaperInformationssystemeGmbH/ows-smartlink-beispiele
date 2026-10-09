<?php
// uniWAHL OWS – SmartLink-Generator für die zweite Generation (GEN2).
//
// Alle Angaben stehen in der SmartLink-Zugangsdaten-Datei, die Sie von der Wahlleitung erhalten:
// domain, electionID, sharedSecret und das URL-Muster mit dem Realm.
// Den Link erst erzeugen, wenn die Person auf "Abstimmen" klickt: Er ist 90 Sekunden gültig.

function authToken(string $userId, string $electionEventId, int $timestamp, string $sharedSecret): string {
    $message = $userId . ":AuthEvent:" . $electionEventId . ":vote:" . $timestamp;
    return "khmac:///sha-256;" . hash_hmac("sha256", $message, $sharedSecret) . "/" . $message;
}

function smartLink(string $domain, string $realm, string $electionEventId, string $authToken): string {
    // rawurlencode() kodiert nach RFC 3986: ein Leerzeichen wird %20, nicht + wie bei urlencode().
    return "https://" . $domain . "/realms/" . $realm . "/election/" . $electionEventId
        . "/public/login?auth-token=" . rawurlencode($authToken);
}

// Prüft die Implementierung gegen einen festen Beispielwert.
function selbsttest(): void {
    $token = authToken("w-4711", "047e91d3-bab7-4007-bb13-5926fef505b7", 1791532800, "example shared secret");
    $erwartet = "khmac:///sha-256;bd825c7c62e39debd3f5ee9604005d687e5f9d2128014f90140852d6c0a1a6e8"
        . "/w-4711:AuthEvent:047e91d3-bab7-4007-bb13-5926fef505b7:vote:1791532800";
    if ($token !== $erwartet) {
        throw new RuntimeException("Selbsttest fehlgeschlagen: " . $token);
    }
    echo "Selbsttest bestanden\n";
    echo smartLink("wahl.example.org",
        "tenant-9b2c4e61-7a35-4f0d-8c1e-2d6a9f3b7e05-event-047e91d3-bab7-4007-bb13-5926fef505b7",
        "047e91d3-bab7-4007-bb13-5926fef505b7", $token), "\n";
}

selbsttest();

$userId          = "";  // Kennung aus dem Wählerverzeichnis
$sharedSecret    = "";  // sharedSecret
$electionEventId = "";  // electionID
$realm           = "";  // tenant-<mandant>-event-<electionID>, siehe URL-Muster
$domain          = "";  // domain, z. B. wahl.example.org oder wahl.example.org/auth

if ($sharedSecret === "") {
    echo "Bitte die Angaben aus der SmartLink-Zugangsdaten-Datei eintragen.\n";
    exit;
}
$token = authToken($userId, $electionEventId, time(), $sharedSecret);
echo smartLink($domain, $realm, $electionEventId, $token), "\n";
