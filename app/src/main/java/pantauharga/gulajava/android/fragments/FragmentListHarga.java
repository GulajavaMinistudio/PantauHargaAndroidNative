package pantauharga.gulajava.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pantauharga.gulajava.android.R;

/**
 * Created by Gulajava Ministudio on 11/5/15.
 */
public class FragmentListHarga extends Fragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_kosong, container, false);

        return view;
    }
}
