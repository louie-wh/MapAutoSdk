package com.baidu.mapautosdk.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    private BackHandleInterface backHandleInterface;

    public abstract boolean onBackPressed();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof BackHandleInterface) {
            this.backHandleInterface = (BackHandleInterface) getActivity();
        } else {
            throw new ClassCastException("Hosting Activity must implement BackHandledInterface");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        backHandleInterface.onSelectedFragment(this);
    }
}
