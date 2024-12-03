package serializacionDeserializacionTests.serializar;

public class GetTokenBody {
//    Campos body
    private String clientName;
    private String clientEmail;
//    Constructor
    public GetTokenBody(String clientName, String clientEmail) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
    }
//    Getters and setters
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
}
