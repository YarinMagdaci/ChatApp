package clientside;
import networking.*;
import org.json.JSONObject;
/**
 * Concrete implementation of the Command interface that encapsulates the "send" operation
 * as a command object, following the Command design pattern. It represents the request to send a message
 * and executes the operation by forwarding the message to the specified recipient through the connection proxy.
 */
public class SendMessageCommand implements Command{
    private final String message;
    private final String sendTo;
    private final String nickName;
    private final ConnectionProxy connectionProxy;

    public SendMessageCommand(String message, String sendTo, String nickName, ConnectionProxy connectionProxy) {
        this.message = message;
        this.sendTo = sendTo;
        this.nickName = nickName;
        this.connectionProxy = connectionProxy;
    }

    // Executes the send operation by creating a request with the appropriate parameters,
    // converting it to a string, and forwarding it to the connection proxy for consumption.
    @Override
    public void execute(SimpleClientGUI simpleClientGUI) {
        int requestCode = (sendTo.equals("Everyone") ? 2 : 3);

        JSONObject body = new JSONObject();
        body.put("nick-name", nickName);
        body.put("sender", nickName);
        body.put("send-to", sendTo);
        body.put("message", message);

        Request request = new Request(HttpMethod.POST, body, requestCode);
        simpleClientGUI.getConnectionProxy().consume(Request.convertRequestToString(request));
    }
}
