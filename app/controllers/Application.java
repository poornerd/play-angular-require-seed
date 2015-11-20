package controllers;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by bp on 20.11.15.
 */
public class Application extends Controller {
    @Transactional
    public static Result index () {
        return ok("index done");
    }
}
