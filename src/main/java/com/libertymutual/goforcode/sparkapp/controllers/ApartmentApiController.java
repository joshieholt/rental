package com.libertymutual.goforcode.sparkapp.controllers;

import com.libertymutual.goforcode.sparkapp.models.Apartment;
import com.libertymutual.goforcode.sparkapp.models.User;
import com.libertymutual.goforcode.sparkapp.utilities.AutoCloseableDB;
import com.libertymutual.goforcode.sparkapp.utilities.JsonHelper;

import static spark.Spark.notFound;

import java.util.List;

import spark.Request;
import spark.Response;
import spark.Route;

public class ApartmentApiController {

    public static final Route details = (Request req, Response res) -> {
        try (AutoCloseableDB db = new AutoCloseableDB()) {
            String idAsString = req.params("id");
            int id = Integer.parseInt(idAsString);
            Apartment apartment = Apartment.findById(id);
            if (apartment != null) {
                res.header("Content-Type", "application/json");
                return apartment.toJson(true);
            }
            notFound("Did not find that.");
            return "";
        }
    };

    public static final Route create = (Request req, Response res) -> {
        Apartment apartment = new Apartment();
        apartment.fromMap(JsonHelper.toMap(req.body()));
        User user = req.session().attribute("currentUser");
        if (user != null) {
            try (AutoCloseableDB db = new AutoCloseableDB()) {
                apartment.save();
                res.status(201);
                return apartment.toJson(true);
            }
        }
        res.status(403);
        return "";
    };

    public static final Route activate = (Request req, Response res) -> {
        long id = Long.parseLong(req.params("id"));
        try (AutoCloseableDB db = new AutoCloseableDB()) {
            User user = req.session().attribute("currentUser");
            Apartment apartment = Apartment.findById(id);
            User apartmentLister = apartment.parent(User.class);
            if (apartmentLister.equals(user)) {
                apartment.setIsActive(true);
                res.status(200);
                return apartment.toJson(true);
            }
            res.status(403);
            return "";
        }
    };

    public static final Route deactivate = (Request req, Response res) -> {
        long id = Long.parseLong(req.params("id"));
        try (AutoCloseableDB db = new AutoCloseableDB()) {
            User user = req.session().attribute("currentUser");
            Apartment apartment = Apartment.findById(id);
            User apartmentLister = apartment.parent(User.class);
            if (apartmentLister.equals(user)) {
                apartment.setIsActive(false);
                ;
                res.status(200);
                return apartment.toJson(true);
            }
            res.status(403);
            return "";
        }
    };

    public static final Route like = (Request req, Response res) -> {
        long id = Long.parseLong(req.params("id"));
        boolean hasLiked = false;
        boolean isLister = false;
        try (AutoCloseableDB db = new AutoCloseableDB()) {
            User user = req.session().attribute("currentUser");
            Apartment apartment = Apartment.findById(id);
            User lister = apartment.parent(User.class);
            List<User> likers = apartment.getAll(User.class);
            if (user != null && !lister.equals(user)) {
                isLister = true;
            }

            if (!isLister && !hasLiked) {
                apartment.add(user);
                apartment.saveIt();
                user.saveIt();
                res.status(200);
                return apartment.toJson(true);
            } else {
                res.status(403);
                return "";
            }
        }
    };
}
