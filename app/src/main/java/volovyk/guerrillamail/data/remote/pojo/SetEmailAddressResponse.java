package volovyk.guerrillamail.data.remote.pojo;

import com.google.gson.annotations.SerializedName;

public class SetEmailAddressResponse {
    @SerializedName("sid_token")
    private String sidToken;
    @SerializedName("email_addr")
    private String emailAddress;

    public String getSidToken() {
        return sidToken;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
