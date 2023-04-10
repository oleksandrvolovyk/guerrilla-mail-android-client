package volovyk.guerrillamail.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import volovyk.guerrillamail.data.EmailRepository;
import volovyk.guerrillamail.data.SingleEvent;
import volovyk.guerrillamail.data.model.Email;

@HiltViewModel
public class MainViewModel extends ViewModel {
    private final EmailRepository emailRepository;
    private final LiveData<String> assignedEmail;
    private final LiveData<List<Email>> emails;
    private final LiveData<Boolean> refreshing;

    private final LiveData<SingleEvent<String>> errorLiveData;

    @Inject
    public MainViewModel(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
        this.assignedEmail = emailRepository.getAssignedEmail();
        this.emails = emailRepository.getEmails();
        this.refreshing = emailRepository.getRefreshing();
        this.errorLiveData = emailRepository.getErrorLiveData();
    }

    public LiveData<String> getAssignedEmail() {
        return assignedEmail;
    }

    public LiveData<List<Email>> getEmails() {
        return emails;
    }

    public LiveData<Boolean> getRefreshing(){ return refreshing;}

    public LiveData<SingleEvent<String>> getErrorLiveData() {
        return errorLiveData;
    }

    public void deleteEmail(Email email) {
        emailRepository.deleteEmail(email);
    }
}
