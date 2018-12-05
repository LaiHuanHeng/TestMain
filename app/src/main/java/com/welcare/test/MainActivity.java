package com.welcare.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.adups.fota.nv.NvWriter;
import com.welcare.hrvemotionmanage.hrvinterface.HRVEmotionUtile;
import com.welcare.hrvemotionmanage.hrvinterface.HTTPInterface;
import com.welcare.hrvemotionmanage.hrvinterface.RequestCallback;
import com.welcare.test.view.WaveChartView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ButtonBaseAdapter buttonBaseAdapter;
    private ArrayList<String> strings = new ArrayList<>();
    private ArrayList<String> strings2 = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        intOther();
        initData();
    }
    private void initView()
    {
        listView = this.findViewById(R.id.list_item);
        buttonBaseAdapter = new ButtonBaseAdapter();
        listView.setAdapter(buttonBaseAdapter);
//        final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

//        System.out.println("开始取SN号");
//        NvWriter.getInstance().writeFlagNV(3, NvWriter.PASS);
//        String snValue = NvWriter.getInstance().readFlagNV();
//        System.out.println(snValue);
//        System.out.println("结束取SN号");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    for(int i = 0;i<100000;i++)
//                    {
//                        final int c = i;
//                        String s = tm.getDeviceId(c)+"";
//                        if(!strings.contains(s))
//                        {
//                            strings.add(s);
//                            strings2.add(i+"");
//                        }
////                        System.out.println(s);
//                    }
//                    StringBuffer stringBuffer = new StringBuffer();
//
//                    for (int i = 0;i<strings.size();i++)
//                    {
//                        stringBuffer.append("\nimei"+strings2.get(i)+":"+strings.get(i));
//
//                    }
//                    System.out.println(stringBuffer.toString());
//                }catch (Exception e){e.printStackTrace();}
//
//            }
//        }).start();

    }
    private void intOther()
    {
        buttonBaseAdapter.addButton("情绪分析管理");
        buttonBaseAdapter.addButton("拍照");
        buttonBaseAdapter.addButton("测试蓝牙SDK");
        buttonBaseAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerOnClick(v.getId());
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handlerOnClick(position);
            }
        });
    }

    private void initData(){}
    private void handlerOnClick(int id)
    {
        Intent intent = null;
        switch (id)
        {
            case 0:
                intent = new Intent(this,HRVEActivity.class);
                break;
            case 1:
                intent = new Intent(this,PhotographActivity.class);
                break;
            case 2:
                intent = new Intent(this,BleScanActivity.class);
                break;


        }
        if(intent!=null)
        {
            startActivity(intent);
        }
    }

    private class ButtonBaseAdapter extends BaseAdapter
    {

        List<String> buttonTexts = new ArrayList<>();
        View.OnClickListener onClickListener;

        public void addButton(String s)
        {
            buttonTexts.add(s);
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return buttonTexts.size();
        }

        @Override
        public Object getItem(int position) {
            return buttonTexts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHodler viewHodler = null;
            if(convertView == null)
            {
                convertView = View.inflate(MainActivity.this,R.layout.list_item,null);
                viewHodler = new ViewHodler();
                viewHodler.btn = convertView.findViewById(R.id.btn1);
                convertView.setTag(viewHodler);

            }else
            {
                viewHodler = (ViewHodler) convertView.getTag();
            }
            viewHodler.btn.setText(buttonTexts.get(position));
            viewHodler.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onClickListener!= null)
                    {
                        v.setId(position);
                        onClickListener.onClick(v);
                    }
                }
            });

            return convertView;
        }
        public class  ViewHodler
        {
            Button btn;
        }
    }

}
