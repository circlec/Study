package com.zc.study.pager;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zc.study.R;

import java.util.ArrayList;

public class PagersActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<String> tab_title_list = new ArrayList<>();//存放标签页标题
    private ArrayList<Fragment> fragment_list = new ArrayList<>();//存放ViewPager下的Fragment
    private Fragment fragment1, fragment2, fragment3, fragment4;
    private MyFragmentPagerAdapter adapter;//适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagers);
        tabLayout = findViewById(R.id.my_tablayout);
        viewPager = findViewById(R.id.my_viewpager);
        tab_title_list.add("页面1");
        tab_title_list.add("页面2");
        tab_title_list.add("页面3");
        tab_title_list.add("页面4");
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(2)));
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(3)));
        fragment1 = TestFragment.newInstance("页面1");
        fragment2 = TestFragment.newInstance("页面2");
        fragment3 = TestFragment.newInstance("页面3");
        fragment4 = TestFragment.newInstance("页面4");
        fragment_list.add(fragment1);
        fragment_list.add(fragment2);
        fragment_list.add(fragment3);
        fragment_list.add(fragment4);
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), tab_title_list, fragment_list);
        viewPager.setAdapter(adapter);//给ViewPager设置适配器
        tabLayout.setupWithViewPager(viewPager);//将TabLayout与Viewpager联动起来
        TabLayout.Tab tab =tabLayout.getTabAt(0);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_tab_my,null);
        TextView tv = view.findViewById(R.id.tv_tab);
        tv.setText(tab.getText());
        tab.setCustomView(view);
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = LayoutInflater.from(PagersActivity.this).inflate(R.layout.layout_tab_my,null);
                TextView tv = view.findViewById(R.id.tv_tab);
                tv.setText(tab.getText());
                tab.setCustomView(view);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(null);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setPageMargin(10);//控制两幅图之间的间距
        viewPager.setPageTransformer(true,new ZoomOutPageTransformer());
        viewPager.setOffscreenPageLimit(3);
    }
    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        //自由控制缩放比例
        private static final float MAX_SCALE = 1f;
        private static final float MIN_SCALE = 0.85f;//0.85f

        @Override
        public void transformPage(View page, float position) {

            if (position <= 1) {

                float scaleFactor = MIN_SCALE + (1 - Math.abs(position)) * (MAX_SCALE - MIN_SCALE);

                page.setScaleX(scaleFactor);

                if (position > 0) {
                    page.setTranslationX(-scaleFactor * 2);
                } else if (position < 0) {
                    page.setTranslationX(scaleFactor * 2);
                }
                page.setScaleY(scaleFactor);
            } else {

                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            }
        }

    }
}
