

package com.rmc33.polybook.service;

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
import com.google.cloud.firestore.DocumentReference;
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
import com.google.api.core.ApiFuture;
import java.io.IOException;

import com.google.cloud.firestore.WriteResult;

public class FirestoreSession  {
  private static final SimpleDateFormat dtf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
  private static final Logger logger = Logger.getLogger(FirestoreSession.class.getName());
  private static Firestore firestore;
  private static CollectionReference sessions;
  private static FirestoreSession instance;

  private FirestoreSession() {

  }

  public static FirestoreSession getInstance() {
    if (instance == null) {
      instance = new FirestoreSession();
      try {
        instance.init();
      }
      catch(Exception e) {
        logger.info("failed to create firesession instance: " + e);
      }
    }
    return instance;
  }

  public void init() throws InterruptedException, ExecutionException, IOException {

      FileInputStream serviceAccount = 
        new FileInputStream("/Users/rc/workspace/polyback/serviceAccountKey.json");
      firestore = FirestoreOptions.getDefaultInstance().toBuilder()
          .setProjectId("strong-imagery-341902")
          .setCredentials(GoogleCredentials.fromStream(serviceAccount))
          .build().getService();
      sessions = firestore.collection("sessions");
  }

  public String createSession(String userId)
      throws IOException, ExecutionException, InterruptedException {

    String sessionNum = new BigInteger(130, new SecureRandom()).toString(32);
    Date today = Calendar.getInstance().getTime();
    Map<String, Object> sessionMap = new HashMap<>();
    sessionMap.put("sessionNum", sessionNum);
    sessionMap.put("userId", userId);
    sessionMap.put("lastModified", dtf.format(today));
    logger.info("Saving data to " + sessionNum + " for userId:" + userId);
    ApiFuture<WriteResult> future = sessions.document(sessionNum).set(sessionMap);
    return sessionNum;
  }


  public Map<String, Object> loadSessionNum(String sessionNum)
      throws ExecutionException, InterruptedException {

    DocumentSnapshot session = sessions.document(sessionNum).get().get();
    Map<String, Object> data = session.getData();
    if (data == null) {
      data = Maps.newHashMap();
    }
    return data;
  }

}