package volovyk.guerrillamail.data.remote;

import android.util.Log;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import volovyk.guerrillamail.data.model.Email;
import volovyk.guerrillamail.data.remote.pojo.CheckForNewEmailsResponse;
import volovyk.guerrillamail.data.remote.pojo.GetEmailAddressResponse;

@Singleton
public class GuerrillaEmailDatabase {
    private static final String TAG = "GuerrillaEmailDatabase";
    private final MutableLiveData<String> assignedEmail = new MutableLiveData<>();
    private final MutableLiveData<List<Email>> emails = new MutableLiveData<>(new ArrayList<>());

    private String sidToken;

    APIInterface apiInterface;

    @Inject
    public GuerrillaEmailDatabase() {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        getEmailAddress();
    }

    private void getEmailAddress() {
        Call<GetEmailAddressResponse> call = apiInterface.getEmailAddress();
        call.enqueue(new Callback<GetEmailAddressResponse>() {
            @Override
            public void onResponse(Call<GetEmailAddressResponse> call, Response<GetEmailAddressResponse> response) {
                Log.d(TAG,response.code()+"");
                GetEmailAddressResponse getEmailAddressResponse = response.body();

                sidToken = getEmailAddressResponse.getSidToken();
                assignedEmail.postValue(getEmailAddressResponse.getEmailAddress());

                Log.d(TAG, "Assigned email: " + getEmailAddressResponse.getEmailAddress());

                checkForNewEmails();

            }

            @Override
            public void onFailure(Call<GetEmailAddressResponse> call, Throwable t) {

            }
        });
    }

    private void checkForNewEmails() {
        Call<CheckForNewEmailsResponse> call = apiInterface.checkForNewEmails(sidToken);
        call.enqueue(new Callback<CheckForNewEmailsResponse>() {
            @Override
            public void onResponse(Call<CheckForNewEmailsResponse> call, Response<CheckForNewEmailsResponse> response) {
                Log.d(TAG,response.code()+"");
                CheckForNewEmailsResponse checkForNewEmailsResponse = response.body();

                if (!checkForNewEmailsResponse.getEmails().isEmpty()) {
                    emails.getValue().addAll(checkForNewEmailsResponse.getEmails());
                    notifyEmailsObservers();
                }
            }

            @Override
            public void onFailure(Call<CheckForNewEmailsResponse> call, Throwable t) {

            }
        });
    }

    private void notifyEmailsObservers() {
        emails.postValue(emails.getValue());
    }

    public LiveData<String> getAssignedEmail() {
        return assignedEmail;
    }

    public LiveData<List<Email>> getEmails() {
        return emails;
    }
}
