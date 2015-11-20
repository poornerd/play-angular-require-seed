import models.User;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.db.jpa.JPA;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.F;
import scala.Option;
import utils.InitialData;

import javax.persistence.EntityManager;

public class Global extends GlobalSettings {

//    private EntityManager mEm;

//    @Override
//    public void onStart(Application application) {
//
//        startJPATransaction(application);
//
//        // load the demo data in dev mode
//        if (Play.isDev() && (User.getList().size() == 0)) {
//            InitialData.loadInitialData();
//        }
//        closeJPATransaction();
//
//        super.onStart(application);
//    }

    public void onStart(Application app) {

        JPA.withTransaction(new F.Callback0() {
            @Override
            public void invoke() throws Throwable {
                play.Logger.debug("First JPA call");

                if (Play.isDev() && (User.getList().size() == 0)) {
                    InitialData.loadInitialData();
                }
            }
        });


    }

    public void onStop(Application app) {
    }

//    private void closeJPATransaction() {
//        if (mEm.isOpen() && mEm.getTransaction().isActive()) {
//            Logger.debug("commiting Transaction");
//            mEm.getTransaction().commit();
//            mEm.getTransaction().begin();
//        }
//    }
//
//    private void startJPATransaction(Application app) {
//        Option<JPAApi> jpaPlugin = app.getWrappedApplication().plugin(JPAApi.class);
//        mEm = jpaPlugin.get().em("default");
//        JPA.bindForSync(mEm);
//    }
//
//    @Override
//    public void onStop(Application app) {
//        if (mEm.isOpen()) {
//            mEm.close();
//        }
//        JPA.bindForSync(null);
//    }

}