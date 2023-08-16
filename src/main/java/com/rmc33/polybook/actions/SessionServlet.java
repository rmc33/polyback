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

import com.rmc33.polybook.util.FirestoreSession;

@WebServlet(
    name = "helloworld",
    urlPatterns = {"/"})
public class SessionServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(SessionServlet.class.getName());
  private static final FirestoreSession firestoreSesion = new FirestoreSession(); 

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (!req.getServletPath().equals("/")) {
      resp.getWriter().write("wrong path");
      return;
    }
    // Get current values for the session.
    // If any attribute doesn't exist, add it to the session.
    String userId = (String) req.getParameter("userId");
    if (userId == null) {
      resp.getWriter().write("no userId");
      return;
    }

    String sessionNum = firestoreSesion.createSession(userId);

    logger.info("Writing response " + req.toString());
    resp.getWriter().write(String.format("%s sessionId for %s", sessionNum, userId));
  }


}