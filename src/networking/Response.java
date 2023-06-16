// Yarin Magdaci - 207702994
// Lital Kraft - 314806647

package networking;

import org.json.JSONObject;

/**
 * Represents an HTTP response with a response body and status code.
 * Provides methods to set and retrieve the response components
 * as well as convert the response to/from a JSON string representation.
 */
public class Response {
    // The body of the response
    private final JSONObject body;

    // The status code of the response
    private final int statusCode;


    // Constructs a new Response object with the specified response body and status code
    public Response(JSONObject body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }


    // Convert a Response object to a JSON string representation
    public static String convertResponseToString(Response response) {
        JSONObject json = new JSONObject();

        // Add the body and status code of the response to the JSON object
        json.put("body", response.getBody());
        json.put("statusCode", response.getStatusCode());

        // Return the JSON string representation
        return json.toString();
    }

    // Convert a JSON string representation to a Response object
    public static Response convertStringToResponse(String jsonString) {
        JSONObject json = new JSONObject(jsonString);

        // Extract the body and status code fields from the JSON object
        JSONObject body = json.getJSONObject("body");
        int statusCode = json.getInt("statusCode");

        // Create and return a new Response object with the extracted fields
        return new Response(body, statusCode);
    }

    // Retrieve the body of the Response
    public JSONObject getBody() {
        return body;
    }

    // Retrieve the status code of the Response
    public int getStatusCode() {
        return statusCode;
    }
}

