import spark.Request;
import spark.Response;
import units.ConversionException;
import units.Result;

import static spark.Spark.*;
import static units.Converter.ConvertToSI;

public final class Main {
    public static void main(String[] args) {
        get("/units/si", Main::handler);
    }

    private static Object handler(final Request req, final Response res) {
        final String query = req.queryParams("units");
        if (query == null) {
            res.status(400);
            return "Missing query parameter";
        }

        final Result result;
        try {
            result = ConvertToSI(query);
        } catch (final ConversionException e) {
            res.status(400);
            return "Invalid query string";
        }

        res.header("Content-Type", "application/json");
        res.status(200);
        return result.toJson();
    }
}
