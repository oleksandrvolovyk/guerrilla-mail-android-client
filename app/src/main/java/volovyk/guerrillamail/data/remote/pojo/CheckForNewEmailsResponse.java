package volovyk.guerrillamail.data.remote.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import volovyk.guerrillamail.data.model.Email;

public class CheckForNewEmailsResponse {
    @SerializedName("list")
    private List<Email> emails;
    @SerializedName("sid_token")
    private String sidToken;

    public List<Email> getEmails() {
        return emails;
    }

    public String getSidToken() {
        return sidToken;
    }
}
