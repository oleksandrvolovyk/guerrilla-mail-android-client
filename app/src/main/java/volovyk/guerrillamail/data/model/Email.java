package volovyk.guerrillamail.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Email {
    @SerializedName("mail_from")
    private final String from;
    @SerializedName("mail_subject")
    private final String subject;
    @SerializedName("mail_body")
    private String body;
    @SerializedName("mail_date")
    private final String date;
    @PrimaryKey
    @SerializedName("mail_id")
    private final Integer id;

    public Email(String from, String subject, String body, String date, Integer id) {
        this.from = from;
        this.subject = subject;
        this.body = body;
        this.date = date;
        this.id = id;
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

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getId() {
        return id;
    }
}
