

package com.rmc33.polybook.actions;


import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.rmc33.polybook.service.FirestoreSession;
import com.google.gson.Gson;
import com.rmc33.polybook.models.UserDataResponse;
import java.util.HashMap;
import java.util.Map;


@WebServlet(
    name = "UserDataServlet",
    urlPatterns = {"/userdata"})
public class UserDataServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(UserDataServlet.class.getName());
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

  private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException  {

    String sessionNum = (String) req.getParameter("sessionNum");
    if (sessionNum == null) {
        resp.getWriter().write("no sessionNum");
        return;
    }

    String userId = (String) req.getParameter("userId");
    if (userId == null) {
        resp.getWriter().write("no userId");
        return;
    }

    String userData = (String) req.getParameter("userData");
    if (userData == null) {
        resp.getWriter().write("no userData");
        return;
    }

    Map<String,Object> sessionData = null;
    try {
        sessionData = firestoreSesion.loadSessionNum(sessionNum);
        String sessionUserId = (String) sessionData.get("userId");
        if (sessionUserId != null && sessionUserId.equals(userId)) {
            //firestoreSesion.updateUserData(sessionUserId, userData);
        }
    } catch (Exception e) {
        logger.info("firestoreSesion error:" + e);
        resp.getWriter().write("could not set userdata");
        return;
    }

    logger.info("sessionData:" + gson.toJson(sessionData));

    UserDataResponse userDataResponse = new UserDataResponse();
    userDataResponse.setCode("success");
    resp.getWriter().write(gson.toJson(userDataResponse));
  }


}