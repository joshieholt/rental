package com.libertymutual.goforcode.sparkapp.filters;

import static spark.Spark.halt;

import java.util.UUID;

import spark.Filter;
import spark.Request;
import spark.Response;

public class SecurityFilters {

    public static Filter isAuthenticated = (Request req, Response res) -> {
        if (req.session().attribute("currentUser") == null) {
            res.redirect("/login?returnPath=" + req.pathInfo());
            halt();
        }
    };

    public static Filter hasValidCSRF = (Request req, Response res) -> {
        String sessionUUID = "";
        if (req.session().attribute("csrf") == null) {
            req.session().attribute("csrf", UUID.randomUUID());
        }

        sessionUUID = req.session().attribute("csrf").toString();
      
        if (req.requestMethod() == "POST") {
            if (req.queryParams("csrf") == null) {
                res.status(403);
                halt();
            }
            String requestUUID = req.queryParams("csrf");
            if (sessionUUID.compareTo(requestUUID) != 0) {
                res.status(403);
                halt();
            }
        }
    };

}
