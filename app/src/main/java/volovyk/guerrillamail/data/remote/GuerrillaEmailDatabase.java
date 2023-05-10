package volovyk.guerrillamail.data.remote;

import android.os.Handler;

import androidx.annotation.NonNull;
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
import volovyk.guerrillamail.data.remote.pojo.SetEmailAddressResponse;

@Singleton
public class GuerrillaEmailDatabase {
    private final MutableLiveData<String> assignedEmail = new MutableLiveData<>();
    private final MutableLiveData<List<Email>> emails = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> refreshing = new MutableLiveData<>(false);

    private final MutableLiveData<SingleEvent<String>> errorLiveData = new MutableLiveData<>();

    private String sidToken;
    private Integer seq = 0;

    APIInterface apiInterface;

    private final int REFRESH_INTERVAL = 5000; // 5 seconds
    private final String SITE = "guerrillamail.com";
    private final String LANG = "en";
    private final Handler mHandler;

    private boolean gotEmailAssigned = false;
    private boolean needNewEmailAddress = false;
    private String requestedEmailAddress;

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
                } else if (needNewEmailAddress) {
                    makeSetEmailAddressRequest(requestedEmailAddress);
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

    public void getNewAddress() {
        refreshing.postValue(true);
        gotEmailAssigned = false;
        assignedEmail.postValue(null);
    }

    public void setEmailAddress(String requestedEmailAddress) {
        refreshing.postValue(true);
        needNewEmailAddress = true;
        assignedEmail.postValue(null);
        this.requestedEmailAddress = requestedEmailAddress;
    }

    private void makeSetEmailAddressRequest(String requestedEmailAddress) {
        Call<SetEmailAddressResponse> call = apiInterface
                .setEmailAddress(sidToken,
                        LANG,
                        SITE,
                        requestedEmailAddress);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<SetEmailAddressResponse> call,
                                   @NonNull Response<SetEmailAddressResponse> response) {
                SetEmailAddressResponse setEmailAddressResponse = response.body();

                if (setEmailAddressResponse != null) {
                    if (setEmailAddressResponse.getSidToken() != null) {
                        sidToken = setEmailAddressResponse.getSidToken();
                    }
                    if (setEmailAddressResponse.getEmailAddress() != null) {
                        String newEmailAddress = setEmailAddressResponse.getEmailAddress();
                        assignedEmail.postValue(newEmailAddress);
                        needNewEmailAddress = false;
                        gotEmailAssigned = true;
                    }
                    refreshing.postValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SetEmailAddressResponse> call,
                                  @NonNull Throwable t) {
                setError(t.getLocalizedMessage());
            }
        });
    }

    private void getEmailAddress() {
        Call<GetEmailAddressResponse> call = apiInterface.getEmailAddress();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<GetEmailAddressResponse> call,
                                   @NonNull Response<GetEmailAddressResponse> response) {
                GetEmailAddressResponse getEmailAddressResponse = response.body();

                if (getEmailAddressResponse != null) {
                    sidToken = getEmailAddressResponse.getSidToken();
                    assignedEmail.postValue(getEmailAddressResponse.getEmailAddress());
                    gotEmailAssigned = true;
                    refreshing.postValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetEmailAddressResponse> call, @NonNull Throwable t) {
                setError(t.getLocalizedMessage());
            }
        });
    }

    private void checkForNewEmails() {
        Call<CheckForNewEmailsResponse> call = apiInterface.checkForNewEmails(sidToken, seq);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CheckForNewEmailsResponse> call,
                                   @NonNull Response<CheckForNewEmailsResponse> response) {
                CheckForNewEmailsResponse checkForNewEmailsResponse = response.body();

                if (checkForNewEmailsResponse != null) {
                    if (checkForNewEmailsResponse.getSidToken() != null) {
                        sidToken = checkForNewEmailsResponse.getSidToken();
                    }
                    if (checkForNewEmailsResponse.getEmails() != null) {
                        if (!checkForNewEmailsResponse.getEmails().isEmpty()) {
                            fetchAllEmails(checkForNewEmailsResponse.getEmails());
                        }
                        refreshing.postValue(false);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CheckForNewEmailsResponse> call,
                                  @NonNull Throwable t) {
                setError(t.getLocalizedMessage());
            }
        });
    }

    private void fetchAllEmails(List<Email> emailsList) {
        for (Email email : emailsList) {
            Call<Email> call = apiInterface.fetchEmail(sidToken, email.getId());

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Email> call,
                                       @NonNull Response<Email> response) {
                    Email fullEmail = response.body();

                    if (fullEmail != null) {
                        fullEmail.setBody(formatEmailBody(fullEmail.getBody()));

                        emails.setValue(List.of(fullEmail));

                        seq = Math.max(seq, fullEmail.getId());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Email> call, @NonNull Throwable t) {
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
