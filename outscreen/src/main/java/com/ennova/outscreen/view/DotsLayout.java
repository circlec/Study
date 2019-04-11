package com.ennova.outscreen.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ennova.outscreen.utils.DensityUtil;

/**
 * @作者 zhouchao
 * @日期 2019/4/1
 * @描述
 */
public class DotsLayout extends LinearLayout {
    private static final int MAX_SHOW_DOTS = 14;
    private Drawable normalDrawable;
    private Drawable selectedDrawable;
    private int mCurrDot = -1;
    private int mTotalDots;
    private int selectedColor = Color.WHITE;
    private int normalColor = Color.WHITE;
    private LayoutParams params;

    public DotsLayout(Context context) {
        super(context);
        init();
    }

    public DotsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }
        setOrientation(HORIZONTAL);

        GradientDrawable dot_normal = new GradientDrawable();
        dot_normal.setSize(DensityUtil.dip2px(getContext(), 8), DensityUtil.dip2px(getContext(), 2));
        dot_normal.setShape(GradientDrawable.RECTANGLE);
        dot_normal.setColor(Color.TRANSPARENT);
        dot_normal.setStroke(DensityUtil.dip2px(getContext(), 0.5f), normalColor);
        normalDrawable = dot_normal;

        GradientDrawable dot_focus = new GradientDrawable();
        dot_focus.setColor(selectedColor);
        dot_focus.setShape(GradientDrawable.RECTANGLE);
        dot_focus.setSize(DensityUtil.dip2px(getContext(), 8), DensityUtil.dip2px(getContext(), 2));
        selectedDrawable = dot_focus;

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = DensityUtil.dip2px(getContext(), 3);
        params.rightMargin = DensityUtil.dip2px(getContext(), 3);
    }

    public void setDotRes(int normalResId, int selectedResId) {
        normalDrawable = getResources().getDrawable(normalResId);
        selectedDrawable = getResources().getDrawable(selectedResId);
        invalidate();
    }

    public void setDot(int currDot, int totalDots) {
        if (totalDots <= 1) {
            return;
        }
        ImageView imgView = null;
        if (totalDots > MAX_SHOW_DOTS) {
            if (currDot > MAX_SHOW_DOTS - 1) {
                if (mCurrDot == MAX_SHOW_DOTS - 1) {
                    return;
                }
                imgView = (ImageView) getChildAt(mCurrDot);
                imgView.setImageDrawable(normalDrawable);

                mCurrDot = MAX_SHOW_DOTS - 1;
                imgView = (ImageView) getChildAt(mCurrDot);
                imgView.setImageDrawable(selectedDrawable);
            } else {
                if (currDot != mCurrDot) {
                    imgView = (ImageView) getChildAt(mCurrDot);
                    imgView.setImageDrawable(normalDrawable);

                    imgView = (ImageView) getChildAt(currDot);
                    imgView.setImageDrawable(selectedDrawable);
                }
                mCurrDot = currDot;
            }
            mTotalDots = MAX_SHOW_DOTS;
            return;
        }

        for (int i = mTotalDots; i < totalDots; i++) {
            imgView = new ImageView(getContext());
            imgView.setImageDrawable(normalDrawable);
            addView(imgView, params);
        }

        if (currDot != mCurrDot) {
            if (mCurrDot != -1) {
                imgView = (ImageView) getChildAt(mCurrDot);
                imgView.setImageDrawable(normalDrawable);
            }

            imgView = (ImageView) getChildAt(currDot);
            imgView.setImageDrawable(selectedDrawable);
        }

        mCurrDot = currDot;
        mTotalDots = totalDots;
    }
}
