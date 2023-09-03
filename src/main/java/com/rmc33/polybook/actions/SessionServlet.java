
package com.rmc33.polybook.actions;


import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.rmc33.polybook.models.SessionResponse;
import com.rmc33.polybook.service.FirestoreSession;
import com.rmc33.polybook.service.AppleIDService;
import com.rmc33.polybook.service.GoogleIDService;
import com.rmc33.polybook.service.IDService;
import com.google.gson.Gson;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.HashMap;
import java.util.Map;

@WebServlet(
    name = "SessionServlet",
    urlPatterns = {"/session/*"})
public class SessionServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(SessionServlet.class.getName());
  private static final FirestoreSession firestoreSesion = FirestoreSession.getInstance();
  private static final Gson gson = new Gson();
  Pattern resourcePattern = Pattern.compile("/(.*_\\d+)");

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String pathInfo = req.getPathInfo();
    if (pathInfo == null) {
        processRequest(req, resp);
        return;
    }
    Matcher matcher = resourcePattern.matcher(pathInfo);
    String sessionId = null;
    if (matcher.find()) {
        sessionId = matcher.group(1);
        System.out.println("found: " + sessionId);
    }
    processGetSessionRequest(req, resp, sessionId);
  }

  private void processGetSessionRequest(HttpServletRequest req, HttpServletResponse resp, String sessionId) throws IOException {
    logger.info("sessionId =" + sessionId);
    String sessionNum = null;
    if (sessionId != null) {
        try {
            Map<String,Object> sessionData = firestoreSesion.loadSessionNum(sessionId);
            sessionNum = (String) sessionData.get("sessionNum");
        } catch (Exception e) {
            logger.info("firestoreSesion getSession error:" + e);
        }
        SessionResponse sessionResponse = new SessionResponse();
        sessionResponse.setSessionNum(sessionNum);
        logger.info("Writing response " + req.toString());
        resp.getWriter().write(gson.toJson(sessionResponse));
    }
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    processRequest(req, resp);
  }

  private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    String userId = (String) req.getParameter("userId");
    if (userId == null) {
        resp.getWriter().write("no userId");
        return;
    }

    String provider = (String) req.getParameter("provider");
    if (provider == null) {
        resp.getWriter().write("no provider");
        return;
    }

    String auth = (String) req.getHeader("authorization");
    if (auth == null) {
        auth = (String) req.getParameter("authorization");
    }
    if (auth == null) {
        resp.getWriter().write("no auth");
        return;
    }

    String sessionNum = null;
    IDService idService = null;
    try {
        if (provider.equals("apple")) {
            idService = new AppleIDService();
        }
        else if (provider.equals("google")){
            idService = new GoogleIDService();
        }
        else {
            logger.info("invalid provider");
            return;
        }
        boolean verifyResult = idService.verifyIDToken(auth, userId);
        if (verifyResult == false) {
            logger.info("id error");
            return;
        }
        sessionNum = firestoreSesion.createSession(userId);
    } catch (Exception e) {
        logger.info("firestoreSesion error:" + e);
    }

    SessionResponse sessionRespopnse = new SessionResponse();
    sessionRespopnse.setSessionNum(sessionNum);

    logger.info("Writing response " + req.toString());
    resp.getWriter().write(gson.toJson(sessionRespopnse));
  }


}