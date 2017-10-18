package com.libertymutual.goforcode.sparkapp.controllers;

import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.libertymutual.goforcode.sparkapp.models.User;
import com.libertymutual.goforcode.sparkapp.utilities.AutoCloseableDB;
import com.libertymutual.goforcode.sparkapp.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

public class UserController {

    public static final Route newForm = (Request req, Response res) -> {
        Map<String, Object> model = new HashMap<String, Object>();
        return MustacheRenderer.getInstance().render("users/signup.html", model, req);
    };
    
    public static final Route create = (Request req, Response res) -> {
        User user = new User(req.queryParams("email"),
                BCrypt.hashpw(req.queryParams("password"), BCrypt.gensalt()),
                req.queryParams("first_name"),
                req.queryParams("last_name"));
        try (AutoCloseableDB db = new AutoCloseableDB()) {
            user.saveIt();
            req.session().attribute("currentUser", user);
            res.redirect("/");
            return "";
        }
        
    };
}
