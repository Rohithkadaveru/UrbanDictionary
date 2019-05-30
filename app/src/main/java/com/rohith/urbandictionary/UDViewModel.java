package com.rohith.urbandictionary;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rohith.urbandictionary.dto.DefinitionList;
import com.rohith.urbandictionary.dto.Definition;
import com.rohith.urbandictionary.network.RetrofitManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UDViewModel extends ViewModel {
    private final String TAG = getClass().getSimpleName();

    private MutableLiveData<List<Definition>> mDefinitionResponse;

    public UDViewModel() {
        mDefinitionResponse = new MutableLiveData<>();
    }

    LiveData<List<Definition>> getDefinitions(@NonNull String searchTerm) {
        loadDefinitions(searchTerm);
        return mDefinitionResponse;
    }

    private void loadDefinitions(@NonNull String term) {
        Call<DefinitionList> call = RetrofitManager.getInstance()
                .getClient().getDefinitions(term);
        call.enqueue(new Callback<DefinitionList>() {
            @Override
            public void onResponse(@NonNull Call<DefinitionList> call,
                    @NonNull Response<DefinitionList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG,response.body().toString());
                    mDefinitionResponse.postValue(response.body().getList());
                } else {
                    mDefinitionResponse.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DefinitionList> call,@NonNull Throwable t) {
                Log.d(TAG,t.toString());
                mDefinitionResponse.postValue(null);
            }
        });
    }
}
