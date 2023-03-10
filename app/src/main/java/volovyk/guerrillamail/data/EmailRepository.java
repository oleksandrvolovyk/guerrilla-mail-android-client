package volovyk.guerrillamail.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import volovyk.guerrillamail.data.model.Email;
import volovyk.guerrillamail.data.remote.GuerrillaEmailDatabase;

@Singleton
public class EmailRepository {
    private final LiveData<String> assignedEmail;
    private final LiveData<List<Email>> emails;

    private final GuerrillaEmailDatabase guerrillaEmailDatabase;

    @Inject
    public EmailRepository(GuerrillaEmailDatabase guerrillaEmailDatabase) {
        this.guerrillaEmailDatabase = guerrillaEmailDatabase;
        this.assignedEmail = guerrillaEmailDatabase.getAssignedEmail();
        this.emails = guerrillaEmailDatabase.getEmails();
    }

    public LiveData<String> getAssignedEmail() {
        return assignedEmail;
    }

    public LiveData<List<Email>> getEmails() {
        return emails;
    }
}
