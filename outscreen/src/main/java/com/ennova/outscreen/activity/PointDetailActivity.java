package com.ennova.outscreen.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ennova.outscreen.BaseActivity;
import com.ennova.outscreen.R;
import com.ennova.outscreen.adapter.PointDetailAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PointDetailActivity extends BaseActivity {

    @BindView(R.id.rv)
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_detail);
        ButterKnife.bind(this);
        initRecyclerView();
    }

    private void initRecyclerView() {
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 1;
            }
        });
        rv.setLayoutManager(manager);
        PointDetailAdapter adapter = new PointDetailAdapter();
        rv.setAdapter(adapter);
        adapter.setItemClickListener(new PointDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                toast("position:" + position);
            }
        });
    }
}
