package com.badia.examples.vertxdocker;

import io.vertx.ext.web.Router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.BodyHandler;

public class HttpServerVerticle extends AbstractVerticle{
        private static final Logger logger = LoggerFactory.getLogger(HttpServerVerticle.class);

    @Override
    public void start() {
        Router router = Router.router(vertx);

        router.post("/notify").handler(BodyHandler.create()).handler(ctx -> {
            JsonObject eventData = ctx.getBodyAsJson();
            vertx.eventBus().send("event.notifications", eventData);
            ctx.response().setStatusCode(200).end("Notificación recibida");
        });

        router.route().failureHandler(ctx -> {
            Throwable failure = ctx.failure();
            if (failure != null) {
                logger.error("Unhandled error in router: ", failure);
            }
            ctx.response().setStatusCode(500).end("Something went wrong");
        });

        vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
            if (http.succeeded()) {
                System.out.println("Servidor HTTP iniciado en el puerto 8888");
            } else {
                System.out.println("Error al iniciar el servidor HTTP: " + http.cause());
            }
        });
    }
}
