package volovyk.guerrillamail.data.model;

public class Email {
    private final String from;
    private final String subject;
    private final String body;

    private final String date;

    public Email(String from, String subject, String body, String date) {
        this.from = from;
        this.subject = subject;
        this.body = body;
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }
}
