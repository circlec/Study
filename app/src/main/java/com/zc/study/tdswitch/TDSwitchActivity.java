package com.zc.study.tdswitch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zc.study.R;
import com.zc.study.view.Image3DSwitchView;
import com.zc.study.view.Image3DView;

public class TDSwitchActivity extends AppCompatActivity implements View.OnClickListener {
    private Image3DSwitchView imageSwitchView;

    private Image3DView image1;
    private Image3DView image2;
    private Image3DView image3;
    private Image3DView image4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tdswitch);
        imageSwitchView = (Image3DSwitchView) findViewById(R.id.image_switch_view);
        image1 = (Image3DView) findViewById(R.id.image1);
        image2 = (Image3DView) findViewById(R.id.image2);
        image3 = (Image3DView) findViewById(R.id.image3);
        image4 = (Image3DView) findViewById(R.id.image4);
        imageSwitchView.setOnImageSwitchListener(new Image3DSwitchView.OnImageSwitchListener() {
            @Override
            public void onImageSwitch(int currentImage) {
                if(currentImage==0){
                    image3.setVisibility(View.INVISIBLE);
                    image4.setVisibility(View.INVISIBLE);
                }else if(currentImage==1){
                    image4.setVisibility(View.INVISIBLE);
                    image3.setVisibility(View.VISIBLE);
                    image1.setVisibility(View.VISIBLE);
                }else if(currentImage==2){
                    image4.setVisibility(View.VISIBLE);
                    image1.setVisibility(View.VISIBLE);
                    image3.setVisibility(View.VISIBLE);
                }else if(currentImage==3){
                    image1.setVisibility(View.INVISIBLE);
                }
            }
        });
        imageSwitchView.setCurrentImage(0);

        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
        image4.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageSwitchView.clear();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image1:
                if(imageSwitchView.getCurrentImage()==1){
                    imageSwitchView.scrollToPrevious();
                }
                break;
            case R.id.image2:
                if(imageSwitchView.getCurrentImage()==0){
                    imageSwitchView.scrollToNext();
                }else if(imageSwitchView.getCurrentImage()==2){
                    imageSwitchView.scrollToPrevious();
                }
                break;
            case R.id.image3:
                if(imageSwitchView.getCurrentImage()==1){
                    imageSwitchView.scrollToNext();
                }else if(imageSwitchView.getCurrentImage()==3){
                    imageSwitchView.scrollToPrevious();
                }
                break;
            case R.id.image4:
                if(imageSwitchView.getCurrentImage()==2){
                    imageSwitchView.scrollToNext();
                }
                break;

            default:
                break;
        }

    }
}
