package bookaugmenter.ohmaker.com.bookaugmenter.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bookaugmenter.ohmaker.com.bookaugmenter.R;

/**
 * 初期選択画面
 */
public class MainFragment extends BaseFragment {


    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //本をよむ
        view.findViewById(R.id.bt_read_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragment.this.replaceFragment(FragmentTag.READ_BOOK, null);
            }
        });

        //タグをつくる
        view.findViewById(R.id.bt_create_tag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragment.this.replaceFragment(FragmentTag.CREATE_TAG, null);
            }
        });

        return view;
    }
}
