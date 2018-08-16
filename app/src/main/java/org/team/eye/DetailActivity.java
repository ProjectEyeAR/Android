package org.team.eye;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private int mCurrentItem;
    private List<ImageMemo> mImageMemoList = new ArrayList<>();

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            mCurrentItem = bundle.getInt("currentItem", 0);
            mImageMemoList = bundle.getParcelableArrayList("imageMemoList");
        }

        mViewPager = findViewById(R.id.DetailActivity_ViewPager);
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return new ImageMemoFragment();
            }

            @Override
            public int getCount() {
                return mImageMemoList.size();
            }
        });

        mViewPager.setCurrentItem(mCurrentItem);
    }
}
