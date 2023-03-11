package volovyk.guerrillamail.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import volovyk.guerrillamail.data.remote.pojo.CheckForNewEmailsResponse;
import volovyk.guerrillamail.data.remote.pojo.GetEmailAddressResponse;

public interface APIInterface {
    @GET("ajax.php?f=get_email_address")
    Call<GetEmailAddressResponse> getEmailAddress();

    @GET("ajax.php?f=check_email&seq=0")
    Call<CheckForNewEmailsResponse> checkForNewEmails(@Query("sid_token") String sidToken);
}
