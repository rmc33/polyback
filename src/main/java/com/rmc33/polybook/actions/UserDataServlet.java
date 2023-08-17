/* Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
  private static final FirestoreSession firestoreSesion = new FirestoreSession();
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

    String userData = (String) req.getParameter("userData");
    if (userData == null) {
        resp.getWriter().write("no userData");
        return;
    }

    Map<String,Object> sessionData = null;
    try {
        sessionData = firestoreSesion.loadSessionNum(sessionNum);
    } catch (Exception e) {
        logger.info("error:" + e);
    }

    logger.info("sessionData:" + gson.toJson(sessionData));

    UserDataResponse userDataResponse = new UserDataResponse();
    userDataResponse.setCode("success");
    resp.getWriter().write(gson.toJson(userDataResponse));
  }


}