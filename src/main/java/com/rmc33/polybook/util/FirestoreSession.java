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

package com.rmc33.polybook.util;

import com.rmc33.polybook.actions.SessionServlet;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;

public class FirestoreSession  {
  private static final SimpleDateFormat dtf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
  private static final Logger logger = Logger.getLogger(SessionServlet.class.getName());
  private static Firestore firestore;
  private static CollectionReference sessions;

  public FirestoreSession() {
    init();
  }

  public void init() {
    try {
    FileInputStream serviceAccount =
        new FileInputStream("/Users/rc/workspace/polyback/serviceAccountKey.json");

       // Initialize local copy of datastore session variables.
    firestore = FirestoreOptions.getDefaultInstance().toBuilder()
        .setProjectId("strong-imagery-341902")
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build().getService();
    sessions = firestore.collection("sessions");

      // Delete all sessions unmodified for over two days.
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.add(Calendar.HOUR, -48);
      Date twoDaysAgo = Calendar.getInstance().getTime();
      QuerySnapshot sessionDocs =
          sessions.whereLessThan("lastModified", dtf.format(twoDaysAgo)).get().get();
      for (QueryDocumentSnapshot snapshot : sessionDocs.getDocuments()) {
        snapshot.getReference().delete();
      }
    } catch (InterruptedException | ExecutionException e) {
      logger.info(String.format("Exception initializing FirestoreSessionFilter: %s", e));
    } catch (java.io.IOException ioe) {
      logger.info(String.format("Firestore error: %s", ioe));
    }
  }

  public String createSession(String userId)
      throws IOException {

    String sessionNum = new BigInteger(130, new SecureRandom()).toString(32);
    Map<String, Object> sessionMap = new HashMap<>();
    sessionMap.put("sessionNum", sessionNum);
    sessionMap.put("userId", userId);
    logger.info("Saving data to " + sessionNum + " for userId:" + userId);
    firestore.runTransaction((ob) -> sessions.document(sessionNum).set(sessionMap));
    return sessionNum;
  }

  /**
   * Take an HttpServletRequest, and copy all of the current session variables over to it
   *
   * @param req Request from which to extract session.
   * @return a map of strings containing all the session variables loaded or an empty map.
   */
  private Map<String, Object> loadSessionVariables(String sessionNum)
      throws ExecutionException, InterruptedException {
    Map<String, Object> datastoreMap = new HashMap<>();
    if (sessionNum.equals("")) {
      return datastoreMap;
    }

    return firestore
        .runTransaction(
            (ob) -> {
              DocumentSnapshot session = sessions.document(sessionNum).get().get();
              Map<String, Object> data = session.getData();
              if (data == null) {
                data = Maps.newHashMap();
              }
              return data;
            })
        .get();
  }

}