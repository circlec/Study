package com.zc.study.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者 zhouchao
 * @日期 2019/4/8
 * @描述
 */
public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;


    private List data;

    public MyAdapter() {
        data = new ArrayList();
    }

    @Override
    public int getItemViewType(int position) {
        if (position  ==0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size()+1;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {

        }
    }

    public void setHeaderView(View headerView) {

    }

    public void setFooterView(View footerView) {

    }

    public void removeFooterView() {

    }

    public void removeHeaderView() {

    }
}
