package com.libertymutual.goforcode.sparkapp.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.libertymutual.goforcode.sparkapp.models.Apartment;
import com.libertymutual.goforcode.sparkapp.utilities.AutoCloseableDB;
import com.libertymutual.goforcode.sparkapp.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

// keeping the following two imports for reference on how to use the velocity templating engine
//import spark.ModelAndView;
//import spark.template.velocity.VelocityTemplateEngine;

public class HomeController {

    public static final Route index = (Request req, Response res) -> {
        try (AutoCloseableDB db = new AutoCloseableDB()) {
            List<Apartment> apartments = Apartment.where("is_active = ?", true);
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("apartments", apartments);
            return MustacheRenderer.getInstance().render("home/index.html", model, req);
            
//            keeping the following line here for reference on how to use the velocity templating engine
//            return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/home/index.vm"));
        }
    };
}
