package com.libertymutual.goforcode.sparkapp.utilities;

import java.io.StringWriter;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

import spark.Request;

public class MustacheRenderer {

    private static final MustacheRenderer instance = new MustacheRenderer("templates");
    private DefaultMustacheFactory factory;
    
    private MustacheRenderer(String folderName) {
        factory = new DefaultMustacheFactory(folderName);
    }
    
    public static MustacheRenderer getInstance() {
        return instance;
    }
    
    public String render(String templatePath, Map<String, Object> model, Request req) {
        Mustache mustache = factory.compile(templatePath);
        StringWriter writer = new StringWriter();
        model.put("currentUser", req.session().attribute("currentUser"));
        model.put("noUser", req.session().attribute("currentUser") == null);
        model.put("csrf", req.session().attribute("csrf"));
        mustache.execute(writer,  model);
        return writer.toString();
    }
}
