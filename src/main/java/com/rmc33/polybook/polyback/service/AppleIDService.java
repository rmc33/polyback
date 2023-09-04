package com.rmc33.polybook.polyback.service;

import java.util.logging.Logger;

import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;


public class AppleIDService implements IDService {

    private static final Logger logger = Logger.getLogger(AppleIDService.class.getName());

    public boolean verifyIDToken(String idToken, String userId) {
        HttpsJwks httpsJkws = new HttpsJwks("https://appleid.apple.com/auth/keys");

        HttpsJwksVerificationKeyResolver httpsJwksKeyResolver = new HttpsJwksVerificationKeyResolver(httpsJkws);
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setVerificationKeyResolver(httpsJwksKeyResolver)
                .setExpectedIssuer("https://appleid.apple.com")
                .setExpectedAudience("org.reactjs.native.example.BibleLingo")
                .build();
        
        try {
            JwtClaims jwtClaims = jwtConsumer.processToClaims(idToken);
        } catch (Exception e) {
            logger.info(String.format("exception %s", e));
            logger.info(String.format("tokenId not verified %s", idToken));
            return false;
        }
        return true;
    }
}