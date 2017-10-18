package com.libertymutual.goforcode.sparkapp.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.libertymutual.goforcode.sparkapp.models.Apartment;
import com.libertymutual.goforcode.sparkapp.models.User;
import com.libertymutual.goforcode.sparkapp.utilities.AutoCloseableDB;
import com.libertymutual.goforcode.sparkapp.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

public class ApartmentController {

    public static final Route details = (Request req, Response res) -> {
        boolean currentUserIsLister = false;
        boolean currentUserHasLiked = false;
        try (AutoCloseableDB db = new AutoCloseableDB()) {
            User currentUser = req.session().attribute("currentUser");
            Apartment apartment = Apartment.findById(Integer.parseInt(req.params("id")));
            User apartmentLister = apartment.parent(User.class);
            Map<String, Object> model = new HashMap<String, Object>();
            List<User> likers = apartment.getAll(User.class);
            if (apartmentLister.equals(currentUser)) {
                currentUserIsLister = true;
            }

            for (User u : likers) {
                if (u.equals(currentUser)) {
                    currentUserHasLiked = true;
                }
            }

            model.put("apartment", apartment);
            model.put("creator", apartmentLister.getEmail());
            model.put("listed", currentUserIsLister);
            model.put("numberOfLikes", likers.size());
            model.put("likers", likers);
            model.put("notLiked", !currentUserHasLiked && !currentUserIsLister);
            model.put("user", currentUser);
            model.put("isActive", apartment.getIsActive());
            model.put("notActive", !apartment.getIsActive());
            return MustacheRenderer.getInstance().render("apartments/details.html", model, req);
        }
    };

    public static final Route newForm = (Request req, Response res) -> {
        Map<String, Object> model = new HashMap<String, Object>();
        return MustacheRenderer.getInstance().render("apartments/newForm.html", model, req);
    };

    public static final Route create = (Request req, Response res) -> {
        Apartment apartment = new Apartment(Integer.parseInt(req.queryParams("rent")), Integer.parseInt(req.queryParams("number_of_bedrooms")), Double.parseDouble(req.queryParams("number_of_bathrooms")), Integer.parseInt(req.queryParams("square_footage")), req.queryParams("address"), req.queryParams("city"), req.queryParams("state"), req.queryParams("zip_code"), true);
        User currentUser = req.session().attribute("currentUser");
        try (AutoCloseableDB db = new AutoCloseableDB()) {
            if (currentUser != null) {
                currentUser.add(apartment);
                apartment.saveIt();
                res.redirect("/apartments/mine");
                return "";
            } else {
                res.status(403);
                return "";
            }
        }
    };

    public static final Route index = (Request req, Response res) -> {
        try (AutoCloseableDB db = new AutoCloseableDB()) {
            User user = req.session().attribute("currentUser");

            List<Apartment> activeApartments = user.get(Apartment.class, "is_active = ?", true);
            List<Apartment> inactiveApartments = user.get(Apartment.class, "is_active = ?", false);

            Map<String, Object> model = new HashMap<String, Object>();
            model.put("activeApartments", activeApartments);
            model.put("inactiveApartments", inactiveApartments);
            return MustacheRenderer.getInstance().render("apartments/index.html", model, req);
        }
    };

    public static final Route activate = (Request req, Response res) -> {
        long id = Long.parseLong(req.params("id"));
        try (AutoCloseableDB db = new AutoCloseableDB()) {
            Apartment apartment = Apartment.findById(id);
            User apartmentLister = apartment.parent(User.class);
            User currentUser = req.session().attribute("currentUser");
            if (apartmentLister.equals(currentUser)) {
                apartment.setIsActive(true);
                apartment.saveIt();
                res.redirect("/apartments/" + id);
                return "";
            } else {
                res.status(403);
                return "";
            }
        }
    };

    public static final Route deactivate = (Request req, Response res) -> {
        long id = Long.parseLong(req.params("id"));
        try (AutoCloseableDB db = new AutoCloseableDB()) {
            Apartment apartment = Apartment.findById(id);
            User apartmentLister = apartment.parent(User.class);
            User currentUser = req.session().attribute("currentUser");
            if (apartmentLister.equals(currentUser)) {
                apartment.setIsActive(false);
                apartment.saveIt();
                res.redirect("/apartments/" + id);
                return "";
            } else {
                res.status(403);
                return "";
            }
        }
    };

    public static final Route like = (Request req, Response res) -> {
        long id = Long.parseLong(req.params("id"));
        boolean hasLiked = false;
        boolean isLister = false;
        try (AutoCloseableDB db = new AutoCloseableDB()) {
            User currentUser = req.session().attribute("currentUser");
            Apartment apartment = Apartment.findById(id);
            User apartmentLister = apartment.parent(User.class);
            List<User> likers = apartment.getAll(User.class);
            if (currentUser != null && !apartmentLister.equals(currentUser)) {
                isLister = true;
            }

            for (User u : likers) {
                if (u.equals(currentUser)) {
                    hasLiked = true;
                }
            }

            if (!isLister && !hasLiked) {
                apartment.add(currentUser);
                apartment.saveIt();
                currentUser.saveIt();
                res.redirect("/apartments/" + id);
                return "";
            } else {
                res.status(403);
                return "";
            }
        }
    };
}