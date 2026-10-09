// uniWAHL OWS – SmartLink-Generator für die zweite Generation (GEN2).
//
// Alle Angaben stehen in der SmartLink-Zugangsdaten-Datei, die Sie von der Wahlleitung erhalten:
// domain, electionID, sharedSecret und das URL-Muster mit dem Realm.
// Den Link erst erzeugen, wenn die Person auf "Abstimmen" klickt: Er ist 90 Sekunden gültig.
package main

import (
	"crypto/hmac"
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"net/url"
	"strings"
	"time"
)

func AuthToken(userID, electionEventID string, timestamp int64, sharedSecret string) string {
	message := fmt.Sprintf("%s:AuthEvent:%s:vote:%d", userID, electionEventID, timestamp)
	mac := hmac.New(sha256.New, []byte(sharedSecret))
	mac.Write([]byte(message))
	return "khmac:///sha-256;" + hex.EncodeToString(mac.Sum(nil)) + "/" + message
}

func SmartLink(domain, realm, electionEventID, authToken string) string {
	return "https://" + domain + "/realms/" + realm + "/election/" + electionEventID +
		"/public/login?auth-token=" + uriEncode(authToken)
}

// Wie encodeURIComponent(): ein Leerzeichen wird %20, nicht +.
func uriEncode(value string) string {
	return strings.ReplaceAll(url.QueryEscape(value), "+", "%20")
}

// Prüft die Implementierung gegen einen festen Beispielwert.
func selbsttest() {
	token := AuthToken("w-4711", "047e91d3-bab7-4007-bb13-5926fef505b7", 1791532800, "example shared secret")
	erwartet := "khmac:///sha-256;bd825c7c62e39debd3f5ee9604005d687e5f9d2128014f90140852d6c0a1a6e8" +
		"/w-4711:AuthEvent:047e91d3-bab7-4007-bb13-5926fef505b7:vote:1791532800"
	if token != erwartet {
		panic("Selbsttest fehlgeschlagen: " + token)
	}
	fmt.Println("Selbsttest bestanden")
	fmt.Println(SmartLink("wahl.example.org",
		"tenant-9b2c4e61-7a35-4f0d-8c1e-2d6a9f3b7e05-event-047e91d3-bab7-4007-bb13-5926fef505b7",
		"047e91d3-bab7-4007-bb13-5926fef505b7", token))
}

func main() {
	selbsttest()

	userID := ""          // Kennung aus dem Wählerverzeichnis
	sharedSecret := ""    // sharedSecret
	electionEventID := "" // electionID
	realm := ""           // tenant-<mandant>-event-<electionID>, siehe URL-Muster
	domain := ""          // domain, z. B. wahl.example.org oder wahl.example.org/auth

	if sharedSecret == "" {
		fmt.Println("Bitte die Angaben aus der SmartLink-Zugangsdaten-Datei eintragen.")
		return
	}
	token := AuthToken(userID, electionEventID, time.Now().Unix(), sharedSecret)
	fmt.Println(SmartLink(domain, realm, electionEventID, token))
}
