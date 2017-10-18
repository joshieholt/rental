
package com.libertymutual.goforcode.sparkapp.controllers;

import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.libertymutual.goforcode.sparkapp.models.User;
import com.libertymutual.goforcode.sparkapp.utilities.AutoCloseableDB;
import com.libertymutual.goforcode.sparkapp.utilities.JsonHelper;

import spark.Request;
import spark.Route;
import spark.Response;

public class UserApiController {

    public static final Route create = (Request req, Response res) -> {
        User user = new User();
        Map<String, String> map = JsonHelper.toMap(req.body());
        map.put("password", BCrypt.hashpw(map.get("password"), BCrypt.gensalt()));
        user.fromMap(map);
        
        try (AutoCloseableDB db = new AutoCloseableDB()) {
            user.save();
            res.status(201);
            return user.toJson(true);
        }
    };
}
