package com.welcare.test;

import android.app.Activity;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vise.baseble.callback.IConnectCallback;
import com.vise.baseble.core.DeviceMirror;
import com.vise.baseble.exception.BleException;
import com.vise.baseble.model.BluetoothLeDevice;
import com.welcare.hjkblelibrary.utile.BleServiceManager;
import com.welcare.hjkblelibrary.utile.LogUtile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BleConnectActivity extends AppCompatActivity {


    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.mac)
    TextView mac;
    @BindView(R.id.state)
    TextView state;
    @BindView(R.id.sendData)
    Button sendData;
    @BindView(R.id.expandable_list_view)
    ExpandableListView expandableListView;
    @BindView(R.id.uuid_service_view)
    LinearLayout uuidServiceView;
    @BindView(R.id.container)
    LinearLayout container;

    MenuItem connectMenuItem, pbrMenuItem;
    private boolean isConnect = false;
    private BluetoothLeDevice device;
    public static Activity sendDataActivity;
    private MyExpandableListAdapter myExpandableListAdapter;

    private IConnectCallback mIConnectCallback = new IConnectCallback() {
        @Override
        public void onConnectSuccess(final DeviceMirror deviceMirror) {
            isConnect = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbrMenuItem.setActionView(null);
                    connectMenuItem.setTitle("disconnect");
                    state.setText("deviceState:connect");
                    sendData.setVisibility(View.VISIBLE);

                    myExpandableListAdapter.bluetoothGattServices.clear();
                    myExpandableListAdapter.bluetoothGattServices.addAll(deviceMirror.getGattServiceList());
                    myExpandableListAdapter.notifyDataSetChanged();

                }
            });


        }

        @Override
        public void onConnectFailure(BleException exception) {
            Toast.makeText(BleConnectActivity.this, "连接异常,请待会再进行连接,异常:" + exception.toString(), Toast.LENGTH_LONG).show();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbrMenuItem.setActionView(null);
                }
            });
        }

        @Override
        public void onDisconnect(boolean isActive) {
            if (sendDataActivity != null) {
                sendDataActivity.finish();
                sendDataActivity = null;
            }
            isConnect = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    connectMenuItem.setTitle("connect");
                    pbrMenuItem.setActionView(null);
                    state.setText("deviceState:disconnect");
                    sendData.setVisibility(View.GONE);
                    myExpandableListAdapter.bluetoothGattServices.clear();
                    myExpandableListAdapter.notifyDataSetChanged();

                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_connect);
        ButterKnife.bind(this);
        init();

    }

    private void init() {

        device = getIntent().getParcelableExtra("device");
        if (device == null) {
            Toast.makeText(this, "设备不存在", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        name.setText("Name:" + device.getName());
        mac.setText("Mac:" + device.getAddress());
        myExpandableListAdapter = new MyExpandableListAdapter();
        expandableListView.setAdapter(myExpandableListAdapter);
        BleServiceManager.getInstance(this).setiConnectCallback(mIConnectCallback);
//        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                return false;
//            }
//        });
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BleConnectActivity.this,SendDataActivity.class);
                intent.putExtra("device",device);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_connect, menu);
        connectMenuItem = menu.findItem(R.id.connectDevice);
        pbrMenuItem = menu.findItem(R.id.menu_refresh);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connectDevice:
                if (isConnect) {
                    disconnect();
                } else {
                    connect();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void connect() {

        pbrMenuItem.setActionView(R.layout.actionbar_indeterminate_progress);
        BleServiceManager.getInstance(this).connect(device);
    }

    private void disconnect() {

        pbrMenuItem.setActionView(R.layout.actionbar_indeterminate_progress);
        if (isConnect) {
            BleServiceManager.getInstance(this).disConnect();
        }
    }

    private class MyExpandableListAdapter extends BaseExpandableListAdapter {

        ArrayList<BluetoothGattService> bluetoothGattServices = new ArrayList<>();


        @Override
        public int getGroupCount() {
            return bluetoothGattServices.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return bluetoothGattServices.get(groupPosition).getCharacteristics().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return bluetoothGattServices.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return bluetoothGattServices.get(groupPosition).getCharacteristics().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupChildViewHolder groupChildViewHolder = null;
            if(convertView == null)
            {
                groupChildViewHolder = new GroupChildViewHolder();
                groupChildViewHolder.textView = new TextView(BleConnectActivity.this);
                groupChildViewHolder.textView.setTextSize(16);
                groupChildViewHolder.textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                convertView = groupChildViewHolder.textView;
                convertView.setTag(groupChildViewHolder);

            }else
                {
                    groupChildViewHolder = (GroupChildViewHolder) convertView.getTag();
                }
            groupChildViewHolder.textView.setText("\t\t\t\t\t\t"+bluetoothGattServices.get(groupPosition).getUuid().toString()+"");

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            GroupChildViewHolder groupChildViewHolder = null;
            if(convertView == null)
            {
                groupChildViewHolder = new GroupChildViewHolder();
                groupChildViewHolder.textView = new TextView(BleConnectActivity.this);
                groupChildViewHolder.textView.setTextSize(12);
                groupChildViewHolder.textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                convertView = groupChildViewHolder.textView;
                convertView.setTag(groupChildViewHolder);

            }else
            {
                groupChildViewHolder = (GroupChildViewHolder) convertView.getTag();
            }
            groupChildViewHolder.textView.setText("\t\t\t\t\t\t\t\t"+bluetoothGattServices.get(groupPosition).getCharacteristics().get(childPosition).getUuid().toString()+"");

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }


        private class  GroupChildViewHolder
        {
            TextView textView;
        }
    }

}
