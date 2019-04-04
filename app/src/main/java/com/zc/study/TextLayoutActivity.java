package com.zc.study;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TextLayoutActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private String testText = "测试用文字多几个！测试用文字多几个！测试用文字多几个！测试用文字多几个！测试用文字多几个！测试用文字多几个！测试用文字多几个！测试用文字多几个！";
    private boolean expended;
    private int maxLine = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_layout);
        final TextView tv = findViewById(R.id.tv);
        final RelativeLayout rl = findViewById(R.id.rl_content);
        final ImageView imageView = findViewById(R.id.iv);
        final TextView viewAfter = findViewById(R.id.tv1);
        tv.setText(testText);
        setLineEnd(tv, viewAfter, rl);
//        setAfterText(tv, imageView, rl);
    }

    /**
     * 展开折叠示例，未处理padding和margin
     *
     * @param tv
     * @param viewAfter
     * @param rl
     */
    private void setLineEnd(TextView tv, TextView viewAfter, RelativeLayout rl) {
        ViewTreeObserver viewTreeObserver = tv.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(() -> {
            if (expended) return;
            RelativeLayout.LayoutParams lastLayoutParams = (RelativeLayout.LayoutParams) viewAfter.getLayoutParams();
            int topMargin = lastLayoutParams.topMargin;
            int leftMargin = lastLayoutParams.leftMargin;
            if (topMargin != 0 || leftMargin != 0) return;
            Layout layout = tv.getLayout();
            int lineCount = tv.getLineCount();
            if (lineCount < maxLine + 1) return;
            int lineEnd = layout.getLineEnd(maxLine - 1);
            tv.setText(testText.substring(0, lineEnd - 5) + "....");
            viewAfter.setVisibility(View.VISIBLE);
            viewAfter.setOnClickListener(v -> {
                if (expended) {
                    expended = false;
                    tv.setText(testText);
                    viewAfter.setText("测试文字");
                } else {
                    expended = true;
                    if (viewAfter.getWidth() + layout.getLineWidth(lineCount - 1) > rl.getWidth()) {
                        tv.setText(testText + "\n");
                    } else {
                        tv.setText(testText);
                    }
                    viewAfter.setText("收起");
                }
            });
        });
    }

    /**
     * 动态布局设置view在textview末尾文字后显示
     *
     * @param tv
     * @param viewAfter
     * @param rl
     */
    private void setAfterText( TextView tv,  View viewAfter, RelativeLayout rl) {
        ViewTreeObserver viewTreeObserver = tv.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(() -> {
            RelativeLayout.LayoutParams lastLayoutParams = (RelativeLayout.LayoutParams) viewAfter.getLayoutParams();
            int topMargin = lastLayoutParams.topMargin;
            int leftMargin = lastLayoutParams.leftMargin;
            if (topMargin != 0 || leftMargin != 0) return;
            Layout layout = tv.getLayout();
            int lineCount = tv.getLineCount();
            if (lineCount < 1) return;
            int tvHeight = tv.getHeight();
            int lineHeight = tvHeight / lineCount;
            int viewAfterHeight = viewAfter.getHeight();
            int spaceHeight = (lineCount - 1) * lineHeight + (lineHeight - viewAfterHeight) / 2;
            int lastLineWidth = (int) layout.getLineWidth(lineCount - 1);
            int afterWidth = viewAfter.getWidth();
            int rlWidth = rl.getWidth();
            int emptyWidth = rlWidth - lastLineWidth;
            if (emptyWidth > afterWidth) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewAfter.getLayoutParams();
                layoutParams.setMargins(lastLineWidth, spaceHeight, 0, 0);
                viewAfter.setLayoutParams(layoutParams);
            } else {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewAfter.getLayoutParams();
                layoutParams.setMargins(0, tvHeight, 0, 0);
                viewAfter.setLayoutParams(layoutParams);
            }
        });
    }
}
