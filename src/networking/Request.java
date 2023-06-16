// Yarin Magdaci - 207702994
// Lital Kraft - 314806647

package networking;

import org.json.JSONObject;

/**
 * Represents an HTTP request with an HTTP method, body, and code.
 * Provides methods to set and retrieve the request components
 * as well as convert the request to/from a JSON string representation.
 */
public class Request {
    // The HTTP method of the request
    private HttpMethod method;

    // The body of the request
    private JSONObject body;

    // The code associated with the request
    private int code;

    // Constructs a new Request object with the specified HTTP method, body, and code
    public Request(HttpMethod method, JSONObject body, int code) {
        setMethod(method);
        setBody(body);
        setCode(code);
    }

    // Sets the HTTP method of the request
    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    // Sets the body of the request
    public void setBody(JSONObject body) {
        this.body = body;
    }

    // Sets the code associated with the request
    public void setCode(int code) {
        this.code = code;
    }

    // Convert a Request object to a JSON string representation
    public static String convertRequestToString(Request request) {
        JSONObject json = new JSONObject();

        // Add the method, body, and code of the request to the JSON object
        json.put("method", request.getMethod());
        json.put("body", request.getBody());
        json.put("code", request.getCode());

        // Return the JSON string representation
        return json.toString();
    }

    // Convert a JSON string representation to a Request object
    public static Request convertStringToRequest(String jsonString) {
        JSONObject json = new JSONObject(jsonString);

        // Extract the method, body, and code fields from the JSON object
        HttpMethod method = HttpMethod.valueOf(json.getString("method"));
        JSONObject body = json.getJSONObject("body");
        int code = json.getInt("code");

        // Create and return a new Request object with the extracted fields
        return new Request(method, body, code);
    }

    // Return the HTTP method of the Request
    public HttpMethod getMethod() {
        return method;
    }

    // Return the body of the Request
    public JSONObject getBody() {
        return body;
    }

    // Return the code associated with the Request
    public int getCode() {
        return code;
    }
}
