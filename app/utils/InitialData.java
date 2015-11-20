package utils;

import models.*;
import org.joda.time.DateTime;
import play.Logger;

import java.util.ArrayList;
import java.util.Date;

public class InitialData {

    public static User user1;


    public static void loadInitialData() {

        Logger.info("Loading Initial Data");

        user1 = new User();
        user1.setEmail("test@test.de");
        user1.setPassword("test");
        user1.setFirstName("Fname");
        user1.setLastName("Lname");
        user1.setCreatedAt(new Date());
        user1.setUpdatedAt(new Date());
        user1.save();
    }

}