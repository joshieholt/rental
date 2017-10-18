package com.libertymutual.goforcode.sparkapp;

import static spark.Spark.*;

import org.mindrot.jbcrypt.BCrypt;

import com.libertymutual.goforcode.sparkapp.controllers.ApartmentApiController;
import com.libertymutual.goforcode.sparkapp.controllers.ApartmentController;
import com.libertymutual.goforcode.sparkapp.controllers.HomeController;
import com.libertymutual.goforcode.sparkapp.controllers.SessionApiController;
import com.libertymutual.goforcode.sparkapp.controllers.SessionController;
import com.libertymutual.goforcode.sparkapp.controllers.UserApiController;
import com.libertymutual.goforcode.sparkapp.controllers.UserController;
import com.libertymutual.goforcode.sparkapp.filters.SecurityFilters;
import com.libertymutual.goforcode.sparkapp.models.Apartment;
import com.libertymutual.goforcode.sparkapp.models.ApartmentsUsers;
import com.libertymutual.goforcode.sparkapp.models.User;
import com.libertymutual.goforcode.sparkapp.utilities.AutoCloseableDB;

public class Application {

    public static void main(String[] args) {

        String encryptedPassword = BCrypt.hashpw("password", BCrypt.gensalt());

        try (AutoCloseableDB db = new AutoCloseableDB()) {
            ApartmentsUsers.deleteAll();
            User.deleteAll();
            User user = new User("curtis.schlak@theironyard.com", encryptedPassword, "Curtis", "Schlak");
            user.saveIt();
            Apartment.deleteAll();
            Apartment apartment;
            apartment = new Apartment(3700, 1, 0, 350, "123 Main St", "San Francisco", "CA", "95125", true);
            user.add(apartment);
            apartment.saveIt();
            apartment = new Apartment(1900, 5, 6, 4000, "123 Cowboy Way", "Houston", "TX", "77006", false);
            user.add(apartment);
            apartment.saveIt();
        }

        before("/*", SecurityFilters.hasValidCSRF);
        
        path("/apartments", () -> {
            before("/new", SecurityFilters.isAuthenticated);
            get("/new", ApartmentController.newForm);
            
            before("/mine", SecurityFilters.isAuthenticated);
            get("/mine", ApartmentController.index);
            
            before("/:id/like", SecurityFilters.isAuthenticated);
            post("/:id/like", ApartmentController.like);
            
            before("/:id/activations", SecurityFilters.isAuthenticated);
            post("/:id/activations", ApartmentController.activate);
            
            before("/:id/deactivations", SecurityFilters.isAuthenticated);
            post("/:id/deactivations", ApartmentController.deactivate);
            
            get("/:id", ApartmentController.details);
            
            before("", SecurityFilters.isAuthenticated);
            post("", ApartmentController.create);            
        });
        
        get("/", HomeController.index);
        get("/login", SessionController.newForm);
        post("/login", SessionController.create);
        
        before("/logout", SecurityFilters.isAuthenticated);
        post("/logout", SessionController.destroy);
        
        get("/users/new", UserController.newForm);
        
        post("/users", UserController.create);
        
        path("/api", () -> {
            post("/login", SessionApiController.create);
            post("/logout", SessionApiController.destroy);
    
            post("/apartments/:id/like", ApartmentApiController.like);
            post("/apartments/:id/activations", ApartmentApiController.activate);
            post("/apartments/:id/deactivations", ApartmentApiController.deactivate);
            get("/apartments/:id", ApartmentApiController.details);
            post("/apartments", ApartmentApiController.create);
            
            post("/users", UserApiController.create);
        });
    }

}