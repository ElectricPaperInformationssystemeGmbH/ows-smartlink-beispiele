// uniWAHL OWS – SmartLink-Generator für die zweite Generation (GEN2).
//
// Alle Angaben stehen in der SmartLink-Zugangsdaten-Datei, die Sie von der Wahlleitung erhalten:
// domain, electionID, sharedSecret und das URL-Muster mit dem Realm.
// Den Link erst erzeugen, wenn die Person auf "Abstimmen" klickt: Er ist 90 Sekunden gültig.
//
// Cargo.toml: hmac = "0.12", sha2 = "0.10"

use hmac::{Hmac, Mac};
use sha2::Sha256;
use std::time::{SystemTime, UNIX_EPOCH};

fn auth_token(user_id: &str, election_event_id: &str, timestamp: u64, shared_secret: &str) -> String {
    let message = format!("{}:AuthEvent:{}:vote:{}", user_id, election_event_id, timestamp);
    let mut mac = Hmac::<Sha256>::new_from_slice(shared_secret.as_bytes())
        .expect("HMAC akzeptiert Schlüssel jeder Länge");
    mac.update(message.as_bytes());
    let code: String = mac
        .finalize()
        .into_bytes()
        .iter()
        .map(|b| format!("{:02x}", b))
        .collect();
    format!("khmac:///sha-256;{}/{}", code, message)
}

fn smart_link(domain: &str, realm: &str, election_event_id: &str, auth_token: &str) -> String {
    format!(
        "https://{}/realms/{}/election/{}/public/login?auth-token={}",
        domain, realm, election_event_id, uri_encode(auth_token)
    )
}

// Kodiert alles außer A-Z a-z 0-9 - _ . ~ (RFC 3986); ein Leerzeichen wird %20.
fn uri_encode(value: &str) -> String {
    value
        .bytes()
        .map(|b| match b {
            b'A'..=b'Z' | b'a'..=b'z' | b'0'..=b'9' | b'-' | b'_' | b'.' | b'~' => (b as char).to_string(),
            _ => format!("%{:02X}", b),
        })
        .collect()
}

// Prüft die Implementierung gegen einen festen Beispielwert.
fn selbsttest() {
    let token = auth_token("w-4711", "047e91d3-bab7-4007-bb13-5926fef505b7", 1791532800, "example shared secret");
    let erwartet = "khmac:///sha-256;bd825c7c62e39debd3f5ee9604005d687e5f9d2128014f90140852d6c0a1a6e8\
                    /w-4711:AuthEvent:047e91d3-bab7-4007-bb13-5926fef505b7:vote:1791532800";
    assert_eq!(token, erwartet, "Selbsttest fehlgeschlagen");
    println!("Selbsttest bestanden");
    println!(
        "{}",
        smart_link(
            "wahl.example.org",
            "tenant-9b2c4e61-7a35-4f0d-8c1e-2d6a9f3b7e05-event-047e91d3-bab7-4007-bb13-5926fef505b7",
            "047e91d3-bab7-4007-bb13-5926fef505b7",
            &token
        )
    );
}

fn main() {
    selbsttest();

    let user_id = ""; // Kennung aus dem Wählerverzeichnis
    let shared_secret = ""; // sharedSecret
    let election_event_id = ""; // electionID
    let realm = ""; // tenant-<mandant>-event-<electionID>, siehe URL-Muster
    let domain = ""; // domain, z. B. wahl.example.org oder wahl.example.org/auth

    if shared_secret.is_empty() {
        println!("Bitte die Angaben aus der SmartLink-Zugangsdaten-Datei eintragen.");
        return;
    }
    let timestamp = SystemTime::now().duration_since(UNIX_EPOCH).unwrap().as_secs();
    let token = auth_token(user_id, election_event_id, timestamp, shared_secret);
    println!("{}", smart_link(domain, realm, election_event_id, &token));
}
