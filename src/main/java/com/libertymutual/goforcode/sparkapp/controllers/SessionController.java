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

public class SessionController {

    public static final Route newForm = (Request req, Response res) -> {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("returnPath", req.queryParams("returnPath"));
        return MustacheRenderer.getInstance().render("session/login.html", model, req);
    };

    public static final Route destroy = (Request req, Response res) -> {
        req.session().attribute("currentUser", null);
        res.redirect("/");
        return "";
    };

    public static final Route create = (Request req, Response res) -> {
        String email = req.queryParams("email");
        String password = req.queryParams("password");
        try (AutoCloseableDB db = new AutoCloseableDB()) {
            User user = User.findFirst("email = ?", email);

            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                req.session().attribute("currentUser", user);
            }
        }
        res.redirect(req.queryParamOrDefault("returnPath", "/"));
        return "";
    };
}
