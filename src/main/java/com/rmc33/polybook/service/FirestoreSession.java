

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
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import com.google.cloud.firestore.DocumentReference;
import java.util.logging.Logger;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.FileInputStream;
import com.google.api.core.ApiFuture;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.ImpersonatedCredentials;

import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.FirebaseOptions;

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
        e.printStackTrace();
      }
    }
    return instance;
  }

  public void init() throws InterruptedException, ExecutionException, IOException {

    GoogleCredentials sourceCredentials = GoogleCredentials.getApplicationDefault();
    ImpersonatedCredentials credentials =
        ImpersonatedCredentials.create(
            sourceCredentials,
            "firebase-adminsdk-wpl4a@strong-imagery-341902.iam.gserviceaccount.com",
            null,
            Arrays.asList("https://www.googleapis.com/auth/cloud-platform", 
              "https://www.googleapis.com/auth/datastore"),
            1000);
    credentials.refreshIfExpired();
    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(credentials)
        .setProjectId("strong-imagery-341902")
        .build();
    FirebaseApp.initializeApp(options);
    firestore = FirestoreClient.getFirestore();
    sessions = firestore.collection("sessions");
  }

  public String createSession(String userId)
      throws IOException, ExecutionException, InterruptedException {

    Date today = Calendar.getInstance().getTime();
    String sessionNum = new BigInteger(130, new SecureRandom()).toString(32) + "_" + today.getTime();
    Map<String, Object> sessionMap = new HashMap<>();
    sessionMap.put("sessionNum", sessionNum);
    sessionMap.put("userId", userId);
    sessionMap.put("lastModified", dtf.format(today));
    logger.info("Saving data to " + sessionNum + " for userId:" + userId);
    WriteResult result = sessions.document(sessionNum).set(sessionMap).get();
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

  public void updateUserData(String sessionUserId, String userData) 
    throws ExecutionException, InterruptedException {
    Map<String, Object> data = new HashMap<>();
    Date today = Calendar.getInstance().getTime();
    data.put("userdata", userData);
    data.put("lastModified", dtf.format(today));
    firestore.collection("userdata").document(sessionUserId).set(data);
  }

}