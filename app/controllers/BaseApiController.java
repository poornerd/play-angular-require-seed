package controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import models.ApiResponse;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.io.StringWriter;


public class BaseApiController extends Controller {
    static JavaRestResourceUtil ru = new JavaRestResourceUtil();

    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML = "application/soap+xml";

    public static ObjectMapper mapper = new ObjectMapper();

    public static Result response(Object obj, String contentType, String operation) {
        switch(contentType) {
            case CONTENT_TYPE_XML:
                int code = 200;
                if(obj instanceof ApiResponse) {
                    ApiResponse response = (ApiResponse) obj;
                    code = response.getCode();
                }
                return promiseResponse(obj, code, null, operation, contentType).map(
                                new F.Function<Result, Result>() {
                                    public Result apply(Result response) {
                                        return response;
                                    }
                                }

                ).get(10000);
            case CONTENT_TYPE_JSON:
                return JsonResponse(obj);
            default:
                return JsonResponse(obj);
        }
    }

    public static Result JsonResponse(Object obj) {
        int code = 200;
        if(obj instanceof ApiResponse) {
            ApiResponse response = (ApiResponse) obj;
            code = response.getCode();
        }

        return JsonResponse(obj, code, null);
    }

    public static Promise<Result> JsonPromiseResponse(Object obj) {
        int code = 200;
        if(obj instanceof ApiResponse) {
            ApiResponse response = (ApiResponse) obj;
            code = response.getCode();
        }

        return JsonPromiseResponse(obj, code, null);
    }


    public static Result JsonResponse(Object obj, FilterProvider filters) {
        return JsonResponse(obj, 200, filters);
    }

    public static Result JsonResponse(Object obj, final int code, FilterProvider filters) {

        return
                JsonPromiseResponse(obj, code, filters).map(
                        new F.Function<Result, Result>() {
                            public Result apply(Result response) {
                                return response;
                            }
                        }

        ).get(10000);

    }

    public static Promise<Result> promiseResponse(Object obj, final int code,
                                                  FilterProvider filters, final String operation,
                                                  final String contentType) {
        final StringWriter w = new StringWriter();
        Promise<Result> resultPromise;

        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // try committing the database stuff before returning - just in case of database errors.
        try {
            if (code == 200) {
                if (JPA.em().isOpen() && JPA.em().getTransaction().isActive()) {
                    Logger.debug("commiting Transaction");
                    JPA.em().getTransaction().commit();
                    JPA.em().getTransaction().begin();
                }
            } else {
                if (JPA.em().isOpen() && JPA.em().getTransaction().isActive()) {
                    Logger.debug("rolling back Transaction");
                    JPA.em().getTransaction().rollback();
                    JPA.em().getTransaction().begin();
                }
            }
        } catch (Throwable t) {
            Logger.error("========");
            Logger.error(t.getLocalizedMessage());
            Logger.error("========");
            if (JPA.em().isOpen()) {
                if(JPA.em().getTransaction() != null) {
                    if (JPA.em().getTransaction().isActive()) {
                        JPA.em().getTransaction().rollback();
                    }
                    JPA.em().clear();
                    JPA.em().getTransaction().begin();
                }
            }
            Throwable cause = t;
            while(cause.getCause() != null) {
                cause = cause.getCause();
            }
            t = cause;
            String errorID = java.util.UUID.randomUUID().toString();
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setCode(400);
            apiResponse.setMessage("Database constraint error");
            apiResponse.setReferenceMessage(t.getMessage() + " (" + java.util.UUID.randomUUID().toString() + ")");
            Logger.error("Error: " + errorID, t);
            try {
                mapper.writeValue(w, apiResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }

            resultPromise = Promise.promise(new F.Function0<Result>() {
                @Override
                public Result apply() throws Throwable {
                    return badRequest(getResponseString(operation, w.toString(), "400",
                            contentType));
                }
            });
            return resultPromise;
        }

        try {
            if (filters == null) {
                mapper.writeValue(w, obj);
            } else {
                Logger.debug("Applying Jackson Filter: " + filters.toString());
                mapper.writer(filters).writeValue(w, obj);
            }
        } catch (Exception e) {
            // TODO: handle proper return code
            e.printStackTrace();
        }

        response().setContentType(contentType);
// not needed at the moment, and should never be activated in the Bank Version
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader("X-UA-Compatible","IE=Edge,chrome=1");
        response().setHeader("Expires","Thu, 01 Dec 1983 20:00:00 GMT");
        response().setHeader("Cache-Control", "no-cache, no-store, private, max-age=0");
        response().setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        response().setHeader("Access-Control-Allow-Headers",
                "Content-Type, api_key, Authorization");

        switch (code) {
            case 200:
                resultPromise = Promise.promise(new F.Function0<Result>() {
                    @Override
                    public Result apply() throws Throwable {
                        return ok(getResponseString(operation, w.toString(), "200", contentType));
                    }
                });
                return resultPromise;
            case 400:
                resultPromise = Promise.promise(new F.Function0<Result>() {
                    @Override
                    public Result apply() throws Throwable {
                        return badRequest(getResponseString(operation, w.toString(), "400",
                                contentType));
                    }
                });
                return resultPromise;
        }

        resultPromise = Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                return status(code, getResponseString(operation, w.toString(),
                        Integer.toString(code), contentType));
            }
        });
        return resultPromise;
    }

    public static Promise<Result> JsonPromiseResponse(Object obj, final int code, FilterProvider filters) {
        return promiseResponse(obj, code, filters, null, CONTENT_TYPE_JSON);
    }

    private static String getResponseString(String operation, String jsonResponse, String code,
                                            String contentType) {
        switch (contentType) {
            case CONTENT_TYPE_JSON:
                return jsonResponse;
            default:
                return jsonResponse;
        }
    }

}

