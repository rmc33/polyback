
package com.rmc33.polybook.polyback.controllers;


import java.io.IOException;
import java.util.logging.Logger;

import com.rmc33.polybook.polyback.models.SessionResponse;
import com.rmc33.polybook.polyback.models.SessionRequest;
import com.rmc33.polybook.polyback.service.FirestoreSession;
import com.rmc33.polybook.polyback.service.AppleIDService;
import com.rmc33.polybook.polyback.service.GoogleIDService;
import com.rmc33.polybook.polyback.service.IDService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;

@RestController
@RequestMapping("/session")
public class SessionController {
  private static final Logger logger = Logger.getLogger(SessionController.class.getName());
  private static final FirestoreSession firestoreSession = FirestoreSession.getInstance();
  Pattern resourcePattern = Pattern.compile(".*_\\d+");

  @GetMapping(value="/{sessionId}", produces = "application/json")
  public SessionResponse getSessionRequest(@PathVariable String sessionId) throws IOException {
    logger.info("sessionId =" + sessionId);
    Matcher matcher = resourcePattern.matcher(sessionId);
    SessionResponse sessionResponse = new SessionResponse();
    if (matcher.find()) {
        sessionId = matcher.group(0);
        System.out.println("found: " + sessionId);
        try {
            Map<String,Object> sessionData = firestoreSession.loadSessionNum(sessionId);
            String sessionNum = (String) sessionData.get("sessionNum");
            sessionResponse.setSessionNum(sessionNum);
        } catch (Exception e) {
            logger.info("firestoreSesion getSession error:" + e);
        }
    }
    return sessionResponse;
  }

  @PostMapping(produces = "application/json", consumes = "application/json")
  public SessionResponse sessionCreateRequest(@RequestBody SessionRequest req) throws IOException {

    SessionResponse sessionResponse = new SessionResponse();

    if (req.getUserId() == null) {
        logger.info("no userId");
        return sessionResponse;
    }

    if (req.getProvider() == null) {
        logger.info("no provider");
        return sessionResponse;
    }

    if (req.getIdToken() == null) {
        logger.info("no auth");
        return sessionResponse;
    }

    String sessionNum = null;
    IDService idService = null;
    try {
        if ("apple".equals(req.getProvider())) {
            idService = new AppleIDService();
        }
        else if ("google".equals(req.getProvider())) {
            idService = new GoogleIDService();
        }
        else {
            logger.info("invalid provider");
            return sessionResponse;
        }
        boolean verifyResult = idService.verifyIDToken(req.getIdToken(), req.getUserId());
        if (verifyResult == false) {
            logger.info("id error");
            return sessionResponse;
        }
        sessionNum = firestoreSession.createSession(req.getUserId());
    } catch (Exception e) {
        logger.info("firestoreSesion error:" + e);
    }

    sessionResponse.setSessionNum(sessionNum);

    logger.info("Writing response " + req.toString());
    return sessionResponse;
  }


}