package volovyk.guerrillamail.data.remote;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import volovyk.guerrillamail.data.model.Email;
import volovyk.guerrillamail.data.placeholder.PlaceholderContent;
@Singleton
public class GuerrillaEmailDatabase {
    private final MutableLiveData<String> assignedEmail = new MutableLiveData<>();
    private final MutableLiveData<List<Email>> emails = new MutableLiveData<>();

    private static volatile GuerrillaEmailDatabase INSTANCE;

    @Inject
    public GuerrillaEmailDatabase() {
        this.assignedEmail.setValue("myemail@example.com"); //TODO: replace placeholders
        this.emails.setValue(PlaceholderContent.ITEMS);
    }

    public LiveData<String> getAssignedEmail() {
        return assignedEmail;
    }

    public LiveData<List<Email>> getEmails() {
        return emails;
    }
}
