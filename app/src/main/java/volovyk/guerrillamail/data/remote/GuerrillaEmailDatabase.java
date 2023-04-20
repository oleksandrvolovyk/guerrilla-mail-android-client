package volovyk.guerrillamail.data.remote;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import volovyk.guerrillamail.data.SingleEvent;
import volovyk.guerrillamail.data.model.Email;
import volovyk.guerrillamail.data.remote.pojo.CheckForNewEmailsResponse;
import volovyk.guerrillamail.data.remote.pojo.GetEmailAddressResponse;

@Singleton
public class GuerrillaEmailDatabase {
    private static final String TAG = "GuerrillaEmailDatabase";
    private final MutableLiveData<String> assignedEmail = new MutableLiveData<>();
    private final MutableLiveData<List<Email>> emails = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> refreshing = new MutableLiveData<>(false);

    private final MutableLiveData<SingleEvent<String>> errorLiveData = new MutableLiveData<>();

    private String sidToken;
    private Integer seq = 0;

    APIInterface apiInterface;

    private final int REFRESH_INTERVAL = 5000; // 5 seconds
    private final Handler mHandler;

    private boolean gotEmailAssigned = false;

    @Inject
    public GuerrillaEmailDatabase() {
        apiInterface = APIClient.getClient().create(APIInterface.class);

        mHandler = new Handler();
        startRepeatingTask();
    }

    Runnable refresher = new Runnable() {
        @Override
        public void run() {
            try {
                refreshing.postValue(true);
                if (!gotEmailAssigned) {
                    getEmailAddress();
                } else {
                    checkForNewEmails();
                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(refresher, REFRESH_INTERVAL);
            }
        }
    };

    private void startRepeatingTask() {
        refresher.run();
    }

    private void getEmailAddress() {
        Call<GetEmailAddressResponse> call = apiInterface.getEmailAddress();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<GetEmailAddressResponse> call, Response<GetEmailAddressResponse> response) {
                Log.d(TAG, response.code() + "");
                GetEmailAddressResponse getEmailAddressResponse = response.body();

                if (getEmailAddressResponse != null) {
                    sidToken = getEmailAddressResponse.getSidToken();
                    assignedEmail.postValue(getEmailAddressResponse.getEmailAddress());
                    gotEmailAssigned = true;
                    refreshing.postValue(false);

                    Log.d(TAG, "Assigned email: " + getEmailAddressResponse.getEmailAddress());
                }
            }

            @Override
            public void onFailure(Call<GetEmailAddressResponse> call, Throwable t) {
                setError(t.getLocalizedMessage());
            }
        });
    }

    private void checkForNewEmails() {
        Call<CheckForNewEmailsResponse> call = apiInterface.checkForNewEmails(sidToken, seq);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<CheckForNewEmailsResponse> call, Response<CheckForNewEmailsResponse> response) {
                Log.d(TAG, response.code() + "");
                CheckForNewEmailsResponse checkForNewEmailsResponse = response.body();

                if (checkForNewEmailsResponse != null) {
                    if (checkForNewEmailsResponse.getEmails() != null) {
                        if (!checkForNewEmailsResponse.getEmails().isEmpty()) {
                            fetchAllEmails(checkForNewEmailsResponse.getEmails());
                        }
                        refreshing.postValue(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckForNewEmailsResponse> call, Throwable t) {
                setError(t.getLocalizedMessage());
            }
        });
    }

    private void fetchAllEmails(List<Email> emailsList) {
        for (Email email : emailsList) {
            Call<Email> call = apiInterface.fetchEmail(sidToken, email.getId());

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Email> call, Response<Email> response) {
                    Log.d(TAG, response.code() + "");

                    Email fullEmail = response.body();

                    if (fullEmail != null) {
                        fullEmail.setBody(formatEmailBody(fullEmail.getBody()));

                        emails.setValue(List.of(fullEmail));

                        seq = Math.max(seq, fullEmail.getId());
                    }
                }

                @Override
                public void onFailure(Call<Email> call, Throwable t) {
                    setError(t.getLocalizedMessage());
                }
            });
        }
    }

    private String formatEmailBody(String body) {
        return body.replaceAll("\\r\\n", "<br>");
    }

    public LiveData<String> getAssignedEmail() {
        return assignedEmail;
    }

    public LiveData<List<Email>> getEmails() {
        return emails;
    }

    public LiveData<Boolean> getRefreshing() {
        return refreshing;
    }

    public LiveData<SingleEvent<String>> getErrorLiveData() {
        return errorLiveData;
    }

    private void setError(String errorMessage) {
        errorLiveData.setValue(new SingleEvent<>(errorMessage));
    }
}
