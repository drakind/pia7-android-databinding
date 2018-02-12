package com.rakangsoftware.databinding;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rakangsoftware.databinding.model.User;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = MainViewModel.class.getSimpleName();

    UserClickListener mUserClickListener = new UserClickListener() {
        @Override
        public void onUserClicked(final User u) {
            Log.d(TAG, "onUserClicked() called with: user = [" + user + "]");
            user.postValue(u);
        }
    };

    public MutableLiveData<List<User>> items       = new MutableLiveData<>();
    public ItemBinding<User>           itemBinding =
            ItemBinding.<User>of(BR.user, R.layout.list_item)
            .bindExtra(BR.listener, mUserClickListener);

    public MutableLiveData<User> user = new MutableLiveData<>();

    private Executor mExecutor = Executors.newSingleThreadExecutor();

    public MainViewModel(Application application) {
        super(application);

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                InputStream raw  = getApplication().getResources().openRawResource(R.raw.users);
                Reader      rd   = new BufferedReader(new InputStreamReader(raw));
                Gson        gson = new Gson();
                Type listType = new TypeToken<List<User>>() {
                }.getType();
                List<User> obj = gson.fromJson(rd, listType);
                items.postValue(obj);
            }
        });

        items.setValue(new ArrayList<User>());
    }

}