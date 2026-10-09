import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * uniWAHL OWS – SmartLink-Generator für die zweite Generation (GEN2).
 *
 * Alle Angaben stehen in der SmartLink-Zugangsdaten-Datei, die Sie von der Wahlleitung erhalten:
 * domain, electionID, sharedSecret und das URL-Muster mit dem Realm.
 * Den Link erst erzeugen, wenn die Person auf "Abstimmen" klickt: Er ist 90 Sekunden gültig.
 */
public class SmartLinkGeneratorGen2 {

    public static void main(String[] args) throws Exception {
        selbsttest();

        String userId          = "";  // Kennung aus dem Wählerverzeichnis
        String sharedSecret    = "";  // sharedSecret
        String electionEventId = "";  // electionID
        String realm           = "";  // tenant-<mandant>-event-<electionID>, siehe URL-Muster
        String domain          = "";  // domain, z. B. wahl.example.org oder wahl.example.org/auth

        if (sharedSecret.isEmpty()) {
            System.out.println("Bitte die Angaben aus der SmartLink-Zugangsdaten-Datei eintragen.");
            return;
        }
        long timestamp = System.currentTimeMillis() / 1000L;
        String token = authToken(userId, electionEventId, timestamp, sharedSecret);
        System.out.println(smartLink(domain, realm, electionEventId, token));
    }

    public static String authToken(String userId, String electionEventId, long timestamp, String sharedSecret)
            throws Exception {
        String message = userId + ":AuthEvent:" + electionEventId + ":vote:" + timestamp;
        return "khmac:///sha-256;" + hmacSha256Hex(sharedSecret, message) + "/" + message;
    }

    public static String smartLink(String domain, String realm, String electionEventId, String authToken) {
        return "https://" + domain + "/realms/" + realm + "/election/" + electionEventId
                + "/public/login?auth-token=" + uriEncode(authToken);
    }

    /** Wie encodeURIComponent(): ein Leerzeichen wird %20, nicht +. */
    static String uriEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    static String hmacSha256Hex(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder(raw.length * 2);
        for (byte b : raw) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    /** Prüft die Implementierung gegen einen festen Beispielwert. */
    static void selbsttest() throws Exception {
        String token = authToken("w-4711", "047e91d3-bab7-4007-bb13-5926fef505b7", 1791532800L,
                "example shared secret");
        String erwartet = "khmac:///sha-256;bd825c7c62e39debd3f5ee9604005d687e5f9d2128014f90140852d6c0a1a6e8"
                + "/w-4711:AuthEvent:047e91d3-bab7-4007-bb13-5926fef505b7:vote:1791532800";
        if (!token.equals(erwartet)) {
            throw new IllegalStateException("Selbsttest fehlgeschlagen: " + token);
        }
        System.out.println("Selbsttest bestanden");
        System.out.println(smartLink("wahl.example.org",
                "tenant-9b2c4e61-7a35-4f0d-8c1e-2d6a9f3b7e05-event-047e91d3-bab7-4007-bb13-5926fef505b7",
                "047e91d3-bab7-4007-bb13-5926fef505b7", token));
    }
}
