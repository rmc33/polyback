package com.rmc33.polybook.service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.logging.Logger;
import java.util.Collections;

public class GoogleIDService  implements IDService {

    private static final Logger logger = Logger.getLogger(GoogleIDService.class.getName());

    public boolean verifyIDToken(String idToken, String userId) {

        JsonFactory jsonFactory = new GsonFactory();
        NetHttpTransport transport = new NetHttpTransport();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            // Specify the CLIENT_ID of the app that accesses the backend:
            //https://console.cloud.google.com/apis/credentials/oauthclient/
            .setAudience(Collections.singletonList("824870600508-f3hqh05m2kujcicp4iq916lpbr1ds84p.apps.googleusercontent.com"))
            // Or, if multiple clients access the backend:
            //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
            .build();

        // (Receive idTokenString by HTTPS POST)
        try {
            GoogleIdToken token = verifier.verify(idToken);
            if (token != null) {
                return true;
            }
        }
        catch (Exception e) {
            logger.info(String.format("exception %s", e));
        }
        logger.info(String.format("tokenId not verified %s", idToken));
        return false;
    }
}