

package com.rmc33.polybook.polyback.controllers;


import java.io.IOException;
import java.util.logging.Logger;
import com.rmc33.polybook.polyback.service.FirestoreSession;
import com.rmc33.polybook.polyback.models.UserDataRequest;
import com.rmc33.polybook.polyback.models.UserDataResponse;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;

@RestController
@RequestMapping("/userdata")
public class UserDataController  {
  private static final Logger logger = Logger.getLogger(UserDataController.class.getName());
  private static final FirestoreSession firestoreSesion = FirestoreSession.getInstance();
  Pattern resourcePattern = Pattern.compile(".*_\\d+");

  @PostMapping(produces = "application/json", consumes = "application/json")
  public UserDataResponse updateRequest(@RequestBody UserDataRequest req) throws IOException  {

    UserDataResponse userDataResponse = new UserDataResponse();

    if (req.getSessionNum() == null) {
        logger.info("no sessionNum");
        return userDataResponse;
    }

    if (req.getUserData() == null) {
        logger.info("no userData");
        return userDataResponse;
    }

    Map<String,Object> sessionData = null;
    try {
        sessionData = firestoreSesion.loadSessionNum(req.getSessionNum());
        String sessionUserId = (String) sessionData.get("userId");
        if (sessionUserId != null) {
            firestoreSesion.updateUserData(sessionUserId, req.getUserData());
        }
        else {
            logger.info("session invalid");
            return userDataResponse;
        }
    } catch (Exception e) {
        logger.info("firestoreSesion error:" + e);
        return userDataResponse;
    }
    logger.info("sessionData:" + sessionData);
    userDataResponse.setUserData(req.getUserData());
    return userDataResponse;
  }

  @GetMapping(value="/{sessionId}", produces = "application/json")
  public UserDataResponse getRequest(@PathVariable String sessionId) throws IOException {
    logger.info("sessionId =" + sessionId);
    Matcher matcher = resourcePattern.matcher(sessionId);
    UserDataResponse userDataResponse = new UserDataResponse();
    if (matcher.find()) {
        sessionId = matcher.group(0);
        System.out.println("found: " + sessionId);
        try {
            Map<String,Object> data = firestoreSesion.loadSessionNum(sessionId);
            String sessionUserId = (String) data.get("userId");
            data = firestoreSesion.loadUserData(sessionUserId);
            String userData = (String) data.get("userdata");
            userDataResponse.setUserData(userData);
        } catch (Exception e) {
            logger.info("firestoreSesion loadUserData error:" + e);
        }
    }
    return userDataResponse;
  }
}