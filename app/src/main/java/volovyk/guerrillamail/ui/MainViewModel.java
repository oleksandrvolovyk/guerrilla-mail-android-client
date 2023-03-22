package volovyk.guerrillamail.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import volovyk.guerrillamail.data.EmailRepository;
import volovyk.guerrillamail.data.model.Email;

@HiltViewModel
public class MainViewModel extends ViewModel {
    private final LiveData<String> assignedEmail;
    private final LiveData<List<Email>> emails;

    @Inject
    public MainViewModel(EmailRepository emailRepository) {
        this.assignedEmail = emailRepository.getAssignedEmail();
        this.emails = emailRepository.getEmails();
    }

    public LiveData<String> getAssignedEmail() {
        return assignedEmail;
    }

    public LiveData<List<Email>> getEmails() {
        return emails;
    }
}
