package com.baidu.mapautosdk.ui.fragment;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapautosdk.api.search.sug.SuggestionResult;
import com.baidu.mapautosdk.api.search.sug.SuggestionSearch;
import com.baidu.mapautosdk.api.search.sug.SuggestionSearchOption;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.mapautosdk.ui.presenter.SuggestionsPresenter;
import com.baidu.platform.comapi.location.CoordinateUtil;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * SearchInputFragment
 */
public class SearchInputFragment extends BaseFragment {
    private SuggestionsPresenter presenter;
    private EditText etStartNode;
    private Button btnInput;
    private int mFlag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFlag = bundle.getInt("flag");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etStartNode = view.findViewById(R.id.et_name);
        btnInput = view.findViewById(R.id.search);
        etStartNode.addTextChangedListener(startWatcher);
        presenter = new SuggestionsPresenter();
        presenter.initListView(view);
        presenter.setOnItemClickListener(itemClickListener);
        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(BroadcastConstants.ACTION.SELECT_NODE);
//                intent.putExtra("keyword", etStartNode.getText().toString());
//                intent.putExtra("cityId", 0);
//                intent.putExtra("subTitle", "");
//                intent.putExtra("flag", mFlag);
//                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
//                NavHostFragment.findNavController(SearchInputFragment.this).popBackStack();
            }
        });
    }

    private void suggestionSearch(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            presenter.setData(null);
            return;
        }
        SuggestionSearch suggestionSearch = SuggestionSearch.newInstance();
        suggestionSearch.setOnGetSuggestionResultListener(resultListener);
        suggestionSearch.requestSuggestion(new SuggestionSearchOption()
                .cityId(131)
                .keyword(keyword)
                .mapLoc(CoordinateUtil.gcj02Tobd09mc(116.307899, 40.057038))
                .mapLevel(14)
                .newSearchType(0));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private TextWatcher startWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            suggestionSearch(s.toString());
        }
    };

    private final OnGetSuggestionResultListener resultListener =
            new OnGetSuggestionResultListener() {
                @Override
                public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                    if (suggestionResult == null) {
                        return;
                    }
                    boolean successful = suggestionResult.isSuccessful();
                    if (successful) {
                        Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                        presenter.setData(suggestionResult);
                    } else {
                        Toast.makeText(getActivity(), "fail" + suggestionResult.getErrorCode(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Intent intent = new Intent(BroadcastConstants.ACTION.SELECT_NODE);
//            SuggestionResult result = presenter.getResultData();
//            String cityId = result.getCityid(position);
//            intent.putExtra("keyword", result.getPoiname(position));
//            intent.putExtra("cityId", TextUtils.isEmpty(cityId) ? 0 : Integer.valueOf(cityId));
//            intent.putExtra("subTitle", result.getSubtitle(position));
//            intent.putExtra("flag", mFlag);
//            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
//            NavHostFragment.findNavController(SearchInputFragment.this).popBackStack();
        }
    };
}
