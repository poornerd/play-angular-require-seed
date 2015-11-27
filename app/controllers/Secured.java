package controllers;

import controllers.SecurityController;
import controllers.routes;
import models.User;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by saeed on 30/June/14 AD.
 */
public class Secured extends Security.Authenticator {

    @Override
    @Transactional
    public String getUsername(Context ctx) {
        User user = null;
        String[] authTokenHeaderValues = ctx.request().headers().get(SecurityController.AUTH_TOKEN_HEADER);
        if (authTokenHeaderValues == null) {
            Http.Cookie cookie = ctx.request().cookie(SecurityController.AUTH_TOKEN);
            String authToken = cookie.value();
            user = models.User.findByAuthToken(authToken);
            if (user != null) {
                ctx.args.put("user", user);
                return user.getEmail();
            }
        }
        if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1) && (authTokenHeaderValues[0] != null)) {
            user = models.User.findByAuthToken(authTokenHeaderValues[0]);
            if (user != null) {
                ctx.args.put("user", user);
                return user.getEmail();
            }
        }

        return null;
    }

    @Transactional
    public static User getUser(Context ctx) {
        User user = null;
        String[] authTokenHeaderValues = ctx.request().headers().get(SecurityController.AUTH_TOKEN_HEADER);
        if (authTokenHeaderValues == null) {
            Http.Cookie cookie = ctx.request().cookie(SecurityController.AUTH_TOKEN);
            String authToken = cookie.value();
            user = models.User.findByAuthToken(authToken);
            if (user != null) {
                ctx.args.put("user", user);
                return user;
            }
        }
        if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1) && (authTokenHeaderValues[0] != null)) {
            user = models.User.findByAuthToken(authTokenHeaderValues[0]);
            if (user != null) {
                ctx.args.put("user", user);
                return user;
            }
        }

        return null;
    }

    @Override
    @Transactional
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.SecurityController.login());
    }
}