package com.welcare.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotographActivity extends AppCompatActivity {

    private static final String TAG = "PhotographActivity";
    private static final  int TAKE_PHOTO = 10002;
    @BindView(R.id.btn_photograph)
    Button btnPhotograph;
    @BindView(R.id.imageView)
    ImageView imageView;
    int heigth,width;
    boolean openChick = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photograph);
        ButterKnife.bind(this);
        inttOther();
    }

    private void inttOther()
    {
        Display display = getWindowManager().getDefaultDisplay();
        heigth = display.getHeight();
        width = display.getWidth();
    }

    @OnClick(R.id.btn_photograph)
    public void onViewClicked() {
        //跳转到拍照的意图
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断系统中是否有照相机
        if (takePhotoIntent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePhotoIntent,TAKE_PHOTO);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openChick = true;
                    chickButton();
                }
            },5*1000);
        }

    }

    /**
     * 模拟点击事件
     */
    private void chickButton()
    {

    }

    /**
     * 处理数据
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //判断请求码和返回码
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            Bitmap bitmap = data.getParcelableExtra("data");
            imageView.setImageBitmap(bitmap);
            openChick = false;
        }
    }
}
