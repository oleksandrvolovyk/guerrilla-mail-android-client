package volovyk.guerrillamail.data.remote.pojo;

import com.google.gson.annotations.SerializedName;

public class GetEmailAddressResponse {
    @SerializedName("email_addr")
    private String emailAddress;
    @SerializedName("sid_token")
    private String sidToken;

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getSidToken() {
        return sidToken;
    }
}
