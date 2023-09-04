

package com.rmc33.polybook.polyback.service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;

import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.FirebaseOptions;

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
        logger.info("failed to create FirestoreSession instance: " + e);
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
    sessions.document(sessionNum).set(sessionMap).get();
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

  public Map<String, Object> loadUserData(String sessionUserId)
    throws ExecutionException, InterruptedException {
    DocumentSnapshot userdata = firestore.collection("userdata").document(sessionUserId).get().get();
    Map<String, Object> data = userdata.getData();
    if (data == null) {
      data = Maps.newHashMap();
    }
    return data;
  }

}