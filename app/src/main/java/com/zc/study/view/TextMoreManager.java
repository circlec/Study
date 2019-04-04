package com.zc.study.view;

import android.graphics.Color;
import android.os.Handler;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

/**
 * @作者 zhouchao
 * @日期 2019/3/26
 * @描述
 */
public class TextMoreManager {

    private int maxLine;
    private String text;
    private TextView textView;
    private View.OnClickListener clickListener;
    private String textMore;
    private String textHide;
    private String textMoreCclor;

    private TextMoreManager(Builder builder) {
        this.maxLine = builder.maxLine;
        this.text = builder.text;
        this.textView = builder.textView;
        this.textMore = builder.textMore;
        this.textHide = builder.textHide;
        this.clickListener = builder.clickListener;
        this.textMoreCclor = builder.textMoreColor;
    }

    public static Builder with(TextView textView) {
        if (textView == null)
            throw new IllegalStateException("TextMoreManager textView == null");
        return new Builder().textView(textView);
    }

    public void showTextMore() {
        textView.getLineCount();
        if (textView == null) return;
        int width = textView.getWidth();//在recyclerView和ListView中，由于复用的原因，这个TextView可能以前就画好了，能获得宽度
        if (width == 0) width = 1000;//获取textView的实际宽度，这里可以用各种方式（一般是dp转px写死）填入TextView的宽度
        int lastCharIndex = getLastCharIndexForLimitTextView(textView, text, width, maxLine);
        //返回-1表示没有达到行数限制
        if (lastCharIndex < 0) {
            //如果行数没超过限制
            textView.setText(text);
            return;
        }
        //如果超出了行数限制
        textView.setMovementMethod(LinkMovementMethod.getInstance());//this will deprive the recyclerView's focus
//        if (lastCharIndex > maxFirstShowCharCount || lastCharIndex < 0) {
//            lastCharIndex = maxFirstShowCharCount;
//        }
        //构造spannableString
        String explicitText = null;
        String explicitTextAll;
        if (text.charAt(lastCharIndex) == '\n') {//manual enter
            explicitText = text.substring(0, lastCharIndex);
        } else if (lastCharIndex > 4) {
            explicitText = text.substring(0, lastCharIndex - 4);
        }
        int sourceLength = explicitText.length();
        String showMore = textMore;
        explicitText = explicitText + "..." + showMore;
        final SpannableString mSpan = new SpannableString(explicitText);


        String dismissMore = textHide;
        explicitTextAll = text + "..." + dismissMore;

        final SpannableString mSpanALL = new SpannableString(explicitTextAll);
        mSpanALL.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
//                ds.setColor(textView.getResources().getColor(textMoreCclor));
                ds.setColor(Color.parseColor(textMoreCclor));
                ds.setAntiAlias(true);
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View widget) {
                textView.setText(mSpan);
                textView.setOnClickListener(null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (clickListener != null)
                            textView.setOnClickListener(clickListener);//prevent the double click
                    }
                }, 20);
            }
        }, text.length(), explicitTextAll.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mSpan.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
//                ds.setColor(textView.getResources().getColor(textMoreCclor));
                ds.setColor(Color.parseColor(textMoreCclor));
                ds.setAntiAlias(true);
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View widget) {//"...show more" click event
                textView.setText(mSpanALL);
                textView.setOnClickListener(null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (clickListener != null)
                            textView.setOnClickListener(clickListener);//prevent the double click
                    }
                }, 20);
            }
        }, sourceLength, explicitText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置为“显示更多”状态下的TextVie
        textView.setText(mSpan);
    }

    private int getLastCharIndexForLimitTextView(TextView textView, String content, int width, int maxLine) {
        TextPaint textPaint = textView.getPaint();
        StaticLayout staticLayout = new StaticLayout(content, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        if (staticLayout.getLineCount() > maxLine)
            return staticLayout.getLineStart(maxLine) - 1;//exceed
        else return -1;//not exceed the max line
    }

    public static class Builder {
        private int maxLine;
        private String text;
        private TextView textView;
        private View.OnClickListener clickListener;
        private String textMore;
        private String textHide;
        private String textMoreColor;

        public Builder() {
            this.maxLine = 2;
            this.textMore = "显示更多";
            this.textHide = "收起";
            this.textMoreColor = "#07B6C4";
            this.clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            };
        }

        public Builder maxLine(int maxLine) {
            this.maxLine = maxLine;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder textView(TextView textView) {
            this.textView = textView;
            return this;
        }

        public Builder textMore(String textMore) {
            this.textMore = textMore;
            return this;
        }

        public Builder textHide(String textHide) {
            this.textHide = textHide;
            return this;
        }

        public Builder textMoreColor(String textMoreColor) {
            this.textMoreColor = textMoreColor;
            return this;
        }

        public TextMoreManager build() {
            if (text == null || text.length() == 0)
                throw new IllegalStateException("text == null||text.length()==0");
            return new TextMoreManager(this);
        }

    }

}
