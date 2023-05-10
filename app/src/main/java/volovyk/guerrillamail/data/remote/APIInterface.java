package volovyk.guerrillamail.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import volovyk.guerrillamail.data.model.Email;
import volovyk.guerrillamail.data.remote.pojo.SetEmailAddressResponse;
import volovyk.guerrillamail.data.remote.pojo.CheckForNewEmailsResponse;
import volovyk.guerrillamail.data.remote.pojo.GetEmailAddressResponse;

public interface APIInterface {
    @GET("ajax.php?f=get_email_address")
    Call<GetEmailAddressResponse> getEmailAddress();

    @GET("ajax.php?f=check_email")
    Call<CheckForNewEmailsResponse> checkForNewEmails(@Query("sid_token") String sidToken,
                                                      @Query("seq") Integer seq);

    @GET("ajax.php?f=fetch_email")
    Call<Email> fetchEmail(@Query("sid_token") String sidToken, @Query("email_id") Integer id);

    @GET("ajax.php?f=set_email_user")
    Call<SetEmailAddressResponse> setEmailAddress(@Query("sid_token") String sidToken,
                                                  @Query("lang") String lang,
                                                  @Query("site") String site,
                                                  @Query("email_user") String newAddress);
}
