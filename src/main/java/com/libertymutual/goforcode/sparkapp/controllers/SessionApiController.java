package com.libertymutual.goforcode.sparkapp.controllers;

import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.libertymutual.goforcode.sparkapp.models.User;
import com.libertymutual.goforcode.sparkapp.utilities.AutoCloseableDB;
import com.libertymutual.goforcode.sparkapp.utilities.JsonHelper;

import spark.Request;
import spark.Response;
import spark.Route;

public class SessionApiController {

    public static final Route create = (Request req, Response res) -> {
        Map<String, String> map = JsonHelper.toMap(req.body());
        String email = map.get("email");
        String password = map.get("password");
        try (AutoCloseableDB db = new AutoCloseableDB()) {
            User user = User.findFirst("email = ?", email);
            
            if (user != null && BCrypt.checkpw(password,  user.getPassword())) {
                req.session().attribute("currentUser", user);
                res.status(201);
            }
            return user.toJson(true);
        }
    };
    
    public static final Route destroy = (Request req, Response res) -> {
        req.session().attribute("currentUser", null);
        res.redirect("/");
        return "";
    };
}
