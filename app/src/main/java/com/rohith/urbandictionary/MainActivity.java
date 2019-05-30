package com.rohith.urbandictionary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rohith.urbandictionary.dto.Definition;
import com.rohith.urbandictionary.dto.DefinitionList;

import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Instance state data
    private static final String KEY_CURRENT_SORT = "KEY_CURRENT_SORT";
    public static final String KEY_DEFINITION = "KEY_DEFINITION";

    private UDViewModel mUDViewModel;
    private DefinitionAdapter mDefinitionAdapter;
    private EditText mDefineTermField;
    private TextView mLblSortBy;
    private List<Definition> mDefinitions;
    private DefinitionSort mCurrentSort = DefinitionSort.THUMBSUP_DESC;

    // BottomSheet
    private LinearLayout mBottomSheetLayout;
    private BottomSheetBehavior mBottomSheetBehavior;
    private View mBottomSheetBackground;
    private TextView mLblBottomSheetThumbsUp, mLblBottomSheetThumbsDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setBottomSheetBehavior();
        initViewModel();

        // used both saved Instance and SharedPreferences to satisfy the requirements
        if (savedInstanceState != null) {
            mCurrentSort = (DefinitionSort)savedInstanceState.get(KEY_CURRENT_SORT);
        }

        // Retrieve definitions from Preferences if the network is not connected
        if (!isNetworkConnected()) {
            String definitionsJSONString =
                    getPreferences(MODE_PRIVATE).getString(KEY_DEFINITION,null);
            if (definitionsJSONString != null) {
                Type type = new TypeToken<List<Definition>>() {
                }.getType();
                mDefinitions = new Gson().fromJson(definitionsJSONString,type);

                if (mDefinitions != null) {
                    showToast("Definitions are loaded from the disk");
                    syncUi(mDefinitions,mCurrentSort);
                }
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable(KEY_CURRENT_SORT,mCurrentSort);
    }

    private void initViews() {
        mBottomSheetBackground = findViewById(R.id.bottom_sheet_background);
        mBottomSheetLayout = findViewById(R.id.bottom_sheet);
        mLblBottomSheetThumbsUp = findViewById(R.id.sortby_thumbsup);
        mLblBottomSheetThumbsDown = findViewById(R.id.sortby_thumbsdown);
        mLblSortBy = findViewById(R.id.lbl_sortby);
        mDefineTermField = findViewById(R.id.et_define_term);
        mDefineTermField.setOnEditorActionListener((textView,actionId,keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!isNetworkConnected()) {
                    showToast("Please check your internet connection");
                    return true;
                }
                performSearch();

                // hide keyboard
                InputMethodManager imm = (InputMethodManager)textView.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(),0);
                return true;
            }
            return false;
        });

        mLblBottomSheetThumbsUp.setOnClickListener(this);
        mLblBottomSheetThumbsDown.setOnClickListener(this);
        mLblSortBy.setOnClickListener(this);

        mDefinitionAdapter = new DefinitionAdapter();
        RecyclerView recyclerView = findViewById(R.id.list_definitions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        recyclerView.setAdapter(mDefinitionAdapter);
    }

    private void initViewModel() {
        mUDViewModel = ViewModelProviders.of(this,
                new ViewModelProvider.NewInstanceFactory()).get(UDViewModel.class);
    }

    private void setBottomSheetBehavior() {
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet,int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        //Reduces the visibility of Bottom Sheet background.
                        mBottomSheetBackground.animate().alpha(0.2f).setDuration(300);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        //Sets the visibility of Bottom Sheet background to normal.
                        mBottomSheetBackground.animate().alpha(1f).setDuration(300);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet,float slideOffset) {
                // Not used
            }
        });
    }

    private void setBottomSheetFields() {
        mBottomSheetLayout.invalidate();
        mBottomSheetLayout.requestLayout();
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBackground.animate().alpha(0.2f).setDuration(300);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public boolean isBottomSheetExpanded() {
        return mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }

    private void performSearch() {
        String searchTerm = mDefineTermField.getText().toString();

        // Set up progress before call
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setTitle("loading definitions");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDoalog.show();

        mUDViewModel.getDefinitions(searchTerm).observe(this,definitions -> {
            progressDoalog.dismiss();
            if (definitions != null) {
                mDefinitions = definitions;
                syncUi(mDefinitions,DefinitionSort.THUMBSUP_DESC);
                saveDefinitionsInPrefs();
            } else {
                Toast.makeText(MainActivity.this,"Error loading definitions",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveDefinitionsInPrefs() {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString(KEY_DEFINITION,new Gson().toJson(mDefinitions));
        editor.apply();
    }

    private void syncUi(@NonNull List<Definition> definitions,@NonNull DefinitionSort definitionSort) {
        mCurrentSort = definitionSort;
        resetBottomSheetLabelDrawables();
        switch (definitionSort) {
            case THUMBSDOWN_ASC:
                Collections.sort(definitions,Definition.THUMBSDOWN_ASC);
                showToast("Definitions are sorted by Least downvoted");
                mLblSortBy.setText("Sorted By ThumbsDown ASC");
                mLblBottomSheetThumbsDown.setCompoundDrawablesWithIntrinsicBounds(
                        android.R.drawable.arrow_up_float,0,0,0);
                break;
            case THUMBSDOWN_DESC:
                Collections.sort(definitions,Definition.THUMBSDOWN_DESC);
                showToast("Definitions are sorted by Most downvoted");
                mLblSortBy.setText("Sorted By ThumbsDown DESC");
                mLblBottomSheetThumbsDown.setCompoundDrawablesWithIntrinsicBounds(
                        android.R.drawable.arrow_down_float,0,0,0);
                break;
            case THUMBSUP_ASC:
                Collections.sort(definitions,Definition.THUMBSUP_ASC);
                showToast("Definitions are sorted by Least upvoted");
                mLblSortBy.setText("Sorted By ThumbsUp DESC");
                mLblBottomSheetThumbsUp.setCompoundDrawablesWithIntrinsicBounds(
                        android.R.drawable.arrow_up_float,0,0,0);
                break;
            case THUMBSUP_DESC:
            default:
                Collections.sort(definitions,Definition.THUMBSUP_DESC);
                showToast("Definitions are sorted by Most upvoted");
                mLblSortBy.setText("Sorted By ThumbsUp DESC");
                mLblBottomSheetThumbsUp.setCompoundDrawablesWithIntrinsicBounds(
                        android.R.drawable.arrow_down_float,0,0,0);
                break;
        }

        mLblSortBy.setVisibility(View.VISIBLE);
        // Set data to adapter
        mDefinitionAdapter.setDefinitions(definitions);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lbl_sortby:
                mBottomSheetLayout.setVisibility(View.VISIBLE);
                setBottomSheetFields();
                break;
            case R.id.sortby_thumbsdown:
                if (mCurrentSort == DefinitionSort.THUMBSDOWN_DESC) {
                    syncUi(mDefinitions,DefinitionSort.THUMBSDOWN_ASC);
                } else {
                    syncUi(mDefinitions,DefinitionSort.THUMBSDOWN_DESC);
                }
                if (isBottomSheetExpanded()) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
            case R.id.sortby_thumbsup:
                if (mCurrentSort == DefinitionSort.THUMBSUP_DESC) {
                    syncUi(mDefinitions,DefinitionSort.THUMBSUP_ASC);
                } else {
                    syncUi(mDefinitions,DefinitionSort.THUMBSUP_DESC);
                }
                if (isBottomSheetExpanded()) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
        }
    }

    // region Utils
    private void resetBottomSheetLabelDrawables() {
        mLblBottomSheetThumbsUp.setCompoundDrawablesWithIntrinsicBounds(
                android.R.drawable.arrow_down_float,0,0,0);
        mLblBottomSheetThumbsDown.setCompoundDrawablesWithIntrinsicBounds(
                android.R.drawable.arrow_up_float,0,0,0);
    }

    private void showToast(String toastMessage) {
        Toast.makeText(this,toastMessage,Toast.LENGTH_SHORT).show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // endregion

    enum DefinitionSort {
        THUMBSUP_ASC,
        THUMBSUP_DESC,
        THUMBSDOWN_ASC,
        THUMBSDOWN_DESC
    }
}
