package volovyk.guerrillamail.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import volovyk.guerrillamail.data.local.LocalEmailDatabase;
import volovyk.guerrillamail.data.model.Email;
import volovyk.guerrillamail.data.remote.GuerrillaEmailDatabase;

@Singleton
public class EmailRepository implements LifecycleOwner {
    private final LifecycleRegistry lifecycleRegistry;
    private final LiveData<String> assignedEmail;
    private final LiveData<List<Email>> emails;
    private final LiveData<Boolean> refreshing;
    private final LiveData<SingleEvent<String>> errorLiveData;

    private final LocalEmailDatabase localEmailDatabase;

    @Inject
    public EmailRepository(GuerrillaEmailDatabase guerrillaEmailDatabase,
                           LocalEmailDatabase localEmailDatabase) {
        this.localEmailDatabase = localEmailDatabase;

        this.assignedEmail = guerrillaEmailDatabase.getAssignedEmail();
        this.emails = localEmailDatabase.emailDao().getAll();
        LiveData<List<Email>> remoteEmails = guerrillaEmailDatabase.getEmails();
        this.refreshing = guerrillaEmailDatabase.getRefreshing();
        this.errorLiveData = guerrillaEmailDatabase.getErrorLiveData();

        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
        lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);

        remoteEmails.observe(this, this::insertAllToLocalDatabase);
    }

    public LiveData<String> getAssignedEmail() {
        return assignedEmail;
    }

    public LiveData<List<Email>> getEmails() {
        return emails;
    }

    public void deleteEmail(Email email) {
        LocalEmailDatabase.databaseExecutorService.execute(() ->
                localEmailDatabase.emailDao().delete(email));
    }

    // Must be called on a non-UI thread or Room will throw an exception.
    public void insertAllToLocalDatabase(Collection<Email> emails) {
        LocalEmailDatabase.databaseExecutorService.execute(() ->
                localEmailDatabase.emailDao().insertAll(emails));
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    public LiveData<Boolean> getRefreshing() {
        return refreshing;
    }

    public LiveData<SingleEvent<String>> getErrorLiveData() {
        return errorLiveData;
    }
}
