package com.example.nguyentuanthanh_705105110.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Chào mừng bạn đến với Thư Viện Số!");
    }

    public LiveData<String> getText() {
        return mText;
    }
} 