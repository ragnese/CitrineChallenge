import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;
import units.ConversionException;
import units.Result;

import static spark.Spark.*;
import static units.Converter.ConvertToSI;

public final class Main {
    private static final ObjectMapper mapper = new ObjectMapper();

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
            return e.getMessage();
        }

        try {
            final byte[] body = mapper.writeValueAsBytes(result);

            res.status(200);
            return body;
        } catch (final JsonProcessingException e) {
            res.status(500);
            return "Error creating result of computation";
        }
    }
}
