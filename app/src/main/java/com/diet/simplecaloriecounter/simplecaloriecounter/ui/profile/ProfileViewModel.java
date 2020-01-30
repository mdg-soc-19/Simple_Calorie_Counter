package com.diet.simplecaloriecounter.simplecaloriecounter.ui.profile;

import android.widget.EditText;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ProfileViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<EditText> mText;

    public ProfileViewModel() {
        mText = new MutableLiveData<>();

    }

    public LiveData<EditText>getText() {
        return mText;
    }

}
