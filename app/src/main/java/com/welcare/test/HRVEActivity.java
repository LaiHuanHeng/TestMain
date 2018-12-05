package com.welcare.test;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.welcare.hrvemotionmanage.hrvinterface.HRVEmotionUtile;
import com.welcare.hrvemotionmanage.hrvinterface.HTTPInterface;
import com.welcare.hrvemotionmanage.hrvinterface.RequestCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HRVEActivity extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hrve);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
//        WaveChartView waveChartView = this.findViewById(R.id.wave);
//        waveChartView.startRun();
//        waveChartView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this,TestSlideMenuActivity.class);
//                startActivity(intent);
//            }
//        });
        textView = findViewById(R.id.textView);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        HTTPInterface.setTest(true);
    }

    public void onClickView(View view)
    {
        byte[] datas = new byte[1];
        try {

            InputStream inputStream = getResources().getAssets().open("ecg3.bin");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = inputStream.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            inputStream.close();
            bos.close();
            datas = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

//        File file = new File("file:////android_asset/ecg3.bin");
        HRVEmotionUtile.getInstance().getHRVEmotion("123456789111111", new RequestCallback() {

            @Override
            public void onSuccess(String result) {
                // TODO Auto-generated method stub
                textView.append("success:"+result+"\n");
                System.out.println("success:"+result+"");


            }

            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub
                textView.append("error:"+error+"\n");
                System.out.println("error:"+error+"");

            }
        },datas);
    }
}
