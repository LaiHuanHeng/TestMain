package com.welcare.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vise.baseble.callback.scan.IScanCallback;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.baseble.model.BluetoothLeDeviceStore;
import com.welcare.hjkblelibrary.utile.BleServiceManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BleScanActivity extends AppCompatActivity {
    private static final String TAG = "BleScanActivity";
    private static final  int NAME_RES_ID = 100001;
    private static final  int MAC_RES_ID = 100002;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private Context mContext;
    private boolean isScan = false;
    private Menu mMenu;
    private MenuItem scanMenuItem, pbrMenuItem;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private IScanCallback mIScanCallback = new IScanCallback() {
        @Override
        public void onDeviceFound(final BluetoothLeDevice bluetoothLeDevice) {
            //搜索到的设备会在此显示
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myRecyclerViewAdapter.addItem(bluetoothLeDevice);
                }
            });
        }

        @Override
        public void onScanFinish(BluetoothLeDeviceStore bluetoothLeDeviceStore) {
            //停止扫描后会把搜索到过的设备会在此显示
            scanOrStop(true);

        }

        @Override
        public void onScanTimeout() {
            //扫描超时
            scanOrStop(true);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scan);
        ButterKnife.bind(this);
        mContext = this;
        BleServiceManager.getInstance(this).init();
        BleServiceManager.getInstance(this).addIScanCallback(mIScanCallback);
        init();
//                new IScanCallback() {
//            @Override
//            public void onDeviceFound(BluetoothLeDevice bluetoothLeDevice) {

//                LogUtile.i(TAG,bluetoothLeDevice.toString());

//
//                BleServiceManager.getInstance(mContext).connect(DeviceType.getType(bluetoothLeDevice.getName()), bluetoothLeDevice, new IConnectCallback() {
//                    @Override
//                    public void onConnectSuccess(DeviceMirror deviceMirror) {
//                            //连接成功,可以取设备模型进行各种操作
//                    }
//
//                    @Override
//                    public void onConnectFailure(BleException exception) {
//                            //连接失败
//                    }
//
//                    @Override
//                    public void onDisconnect(boolean isActive) {
//                        //断开连接
//
//                    }
//                }, new BraceletDataCallback() {
//                    @Override
//                    public void onBindDeviceState(boolean success) {
//                            //调用绑定命令后会在此应答绑定成功或失败
//                    }
//
//                    @Override
//                    public void onLoginDeviceState(boolean success) {
//                            //调用登录命令后会在此应答登录成功或失败
//                    }
//
//                    @Override
//                    public void onHeart(HeartData heart) {
//                            //在连接状态下在手环测量心率时或者调用获取当前的心率数据时会在此应答,
//                    }
//
//                    @Override
//                    public void onStep(StepData step) {
//                        //在连接状态下在手环运动数据回调时或者调用获取当前的运动数据时会在此应答,
//                    }
//
//                    @Override
//                    public void onOXY(OxyData oxy) {
//                            //在连接状态下在手环测量心率时或者调用获取当前的心率数据时会在此应答血氧数据,
//                    }
//
//                    @Override
//                    public void onBloodPressure(BloodPressure bloodPressure) {
//                        //在连接状态下在手环测量心率时或者调用获取当前的心率数据时会在此应答血压数据,
//                    }
//
//                    @Override
//                    public void onSleep(SleepData sleepData) {
//                        //在连接状态下在调用获取当前的睡眠数据时会在此应答
//                    }
//
//                    @Override
//                    public void onHeartOXYBPHistory(ArrayList<HeartData> hearts, ArrayList<OxyData> oxys, ArrayList<BloodPressure> bloodPressures) {
//                        //在连接状态下在调用获取心率血压血氧当前历史记录时会在此应答,需要在此方法加上接收成功的命令让手环去掉此历史记录
//                        BleServiceManager.getInstance(mContext).sendData(BraceletInstructions.getDZ800BPOXYHEARTHistorySuccessInstructions());
//
//                    }
//
//                    @Override
//                    public void onStepHistory(ArrayList<StepData> steps) {
//                        //在连接状态下在调用获取当前运动的历史记录时会在此应答,需要在此方法加上接收成功的命令让手环去掉此历史记录
//                        BleServiceManager.getInstance(mContext).sendData(BraceletInstructions.getDZ800MotionHistorySuccessInstructions());
//                    }
//
//                    @Override
//                    public void onElectricQuantity(int electricQuantity, int state) {
//                        //在连接状态下在调用获取电量命令时在此回调,
//                    }
//
//                    @Override
//                    public void onVersion(String version) {
//                        //在连接状态下在调用获取版本信息命令时在此回调,
//                    }
//
//                    @Override
//                    public void onSuccess(byte[] data, BluetoothGattChannel bluetoothGattChannel, BluetoothLeDevice bluetoothLeDevice) {
//                            //接收的原始数据,开发者可参考文档,自己解析
//                    }
//
//                    @Override
//                    public void onFailure(BleException exception) {
//                            //接收失败
//                    }
//                });


//            }
//
//            @Override
//            public void onScanFinish(BluetoothLeDeviceStore bluetoothLeDeviceStore) {
//                //停止扫描后会把搜索到过的设备会在此显示
//            }
//
//            @Override
//            public void onScanTimeout() {
//                //扫描超时
//            }
//        });
//        BleServiceManager.getInstance(mContext).disConnect();
//        BleServiceManager.getInstance(this).setScanTime(10*1000);

    }

    /**
     * 初始化右上角菜单 重写onCreateOptionMenu(Menu menu)方法，当菜单第一次被加载时调用
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        mMenu = menu;
        scanMenuItem = menu.findItem(R.id.scan);
        pbrMenuItem = menu.findItem(R.id.menu_refresh);
        return true;
    }


    /**
     * 菜单点击事件 重写OptionsItemSelected(MenuItem item)来响应菜单项(MenuItem)的点击事件（根据id来区分是哪个item）
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan:
                scanOrStop();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**开始扫描或者停止
     * @param booleans
     */
    private void scanOrStop(boolean... booleans) {
        boolean scan = isScan;
        if (booleans != null && booleans.length >= 1) {
            scan = booleans[0];
        }
        if (scan) {
            //停止扫描
            scanMenuItem.setTitle("scan");
            isScan = false;
            pbrMenuItem.setActionView(null);
            BleServiceManager.getInstance(this).stopScan();
        } else {
            //开始扫描
            scanMenuItem.setTitle("stop");
            isScan = true;
            BleServiceManager.getInstance(this).scanName();
            pbrMenuItem.setActionView(R.layout.actionbar_indeterminate_progress);
        }

    }

    /**
     * 初始化
     */
    private void init() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerViewAdapter = new MyRecyclerViewAdapter();
        recyclerView.setAdapter(myRecyclerViewAdapter);
    }

    private class  MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>
    {
        private ArrayList<BluetoothLeDevice> devices = new ArrayList<>();
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout linearLayout = new LinearLayout(BleScanActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            lp.setMargins(10,10,10,10);
            linearLayout.setLayoutParams(lp);
            linearLayout.setPadding(10,10,10,10);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView nameView = new TextView(BleScanActivity.this);
            nameView.setId(NAME_RES_ID);
            nameView.setTextSize(18);
            linearLayout.addView(nameView,lp);
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView macView = new TextView(BleScanActivity.this);
            macView.setId(MAC_RES_ID);
            macView.setTextSize(14);
            linearLayout.addView(macView,lp);
            return new ViewHolder(linearLayout);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.name.setText("Name:\t"+devices.get(position).getName()+"");
            holder.mac.setText("\t\tMac:\t"+devices.get(position).getAddress()+"");
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BleScanActivity.this,BleConnectActivity.class);
                    intent.putExtra("device",devices.get(position));
                    startActivity(intent);
                }
            });

        }

        /**
         * 添加设备
         */
        public void addItem(BluetoothLeDevice device)
        {
            for(BluetoothLeDevice b:devices)
            {
                   if(b.getAddress().equalsIgnoreCase(device.getAddress()))
                   {
                       return;
                   }
            }
            devices.add(device);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView mac;
            View view;

            public ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(NAME_RES_ID);
                mac = itemView.findViewById(MAC_RES_ID);
                view = itemView;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mMenu!= null)
        scanOrStop(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMenu!= null)
        scanOrStop(false);
    }
}
