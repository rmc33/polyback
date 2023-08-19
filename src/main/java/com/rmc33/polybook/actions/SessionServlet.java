
package com.rmc33.polybook.actions;


import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rmc33.polybook.models.SessionResponse;
import com.rmc33.polybook.service.FirestoreSession;
import com.rmc33.polybook.service.AppleIDService;
import com.rmc33.polybook.service.GoogleIDService;
import com.rmc33.polybook.service.IDService;
import com.google.gson.Gson;

@WebServlet(
    name = "SessionServlet",
    urlPatterns = {"/session"})
public class SessionServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(SessionServlet.class.getName());
  private static final FirestoreSession firestoreSesion = FirestoreSession.getInstance();
  private static final Gson gson = new Gson();


  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    processRequest(req, resp);
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