package com.badia.examples.vertxdocker;

import java.io.FileInputStream;
import java.io.InputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    // vertx.createHttpServer().requestHandler(req -> {
    //   req.response()
    //     .putHeader("content-type", "text/plain")
    //     .end("Hello from Vert.x!");
    // }).listen(8888).onComplete(http -> {
    //   if (http.succeeded()) {
    //     startPromise.complete();
    //     System.out.println("HTTP server started on port 8888");
    //   } else {
    //     startPromise.fail(http.cause());
    //   }
    // });
    // FileInputStream serviceAccount = new FileInputStream("config/xeventsnotifications-firebase-adminsdk-3ny8e-e1061efeb8.json");
    
    InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("config/xeventsnotifications-firebase-adminsdk-3ny8e-e1061efeb8.json");
    if (serviceAccount == null) {
      System.err.println("Credentials file not found.");
      return;
    }

    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build();
    FirebaseApp.initializeApp(options);
    vertx.deployVerticle(new NotificationVerticle());
    vertx.deployVerticle(new HttpServerVerticle())
      .onComplete(ar -> {
        if (ar.succeeded()) {
          startPromise.complete();
        } else {
          startPromise.fail(ar.cause());
        }
      });
  }
}
