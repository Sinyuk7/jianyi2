package com.sinyuk.jianyi.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.ui.BaseActivity;

import java.util.ArrayList;

import br.com.mauker.materialsearchview.MaterialSearchView;
import butterknife.BindView;

/**
 * Created by Sinyuk on 16/9/13.
 */
public class SearchActivity extends BaseActivity {
    @BindView(R.id.search_view)
    MaterialSearchView searchView;

    private void setupSearchView() {

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchResultActivity.start(SearchActivity.this, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewOpened() {
                // Do something once the view is open.
            }

            @Override
            public void onSearchViewClosed() {
                // Do something once the view is closed.
                finish();
                overridePendingTransition(0, 0);
            }
        });

        searchView.setOnItemClickListener((parent, view, position, id) -> {
            // Do something when the suggestion list is clicked.
            String suggestion = searchView.getSuggestionAtPosition(position);
            searchView.setQuery(suggestion, true);
        });

        //        searchView.setTintAlpha(200);
        searchView.adjustTintAlpha(0.8f);

        final Context context = this;

        searchView.setOnVoiceClickedListener(new MaterialSearchView.OnVoiceClickedListener() {
            @Override
            public void onVoiceClicked() {
                Toast.makeText(context, "Voice clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isOpen()) {
            // Close the search on the back button press.
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        searchView.clearSuggestions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchView.activityResumed();
        String[] arr = getResources().getStringArray(R.array.suggestions);
        searchView.addSuggestions(arr);
    }

    private void clearHistory() {
        searchView.clearHistory();
    }

    private void clearSuggestions() {
        searchView.clearSuggestions();
    }

    private void clearAll() {
        searchView.clearAll();
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_search;
    }

    @Override
    protected void beforeInflating() {

    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {
        setupSearchView();
        searchView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                searchView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                searchView.openSearch();
            }
        });
    }
}
