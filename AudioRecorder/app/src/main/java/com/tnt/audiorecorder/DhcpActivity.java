package com.tnt.audiorecorder;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.BatchUpdateException;

public class DhcpActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "dhcp";

    private Button mQuit ;
    private TextView mIpInfoTV ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dhcp);

        mQuit = findViewById(R.id.test_back) ;
        mIpInfoTV = findViewById(R.id.show_ip) ;
        mQuit.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        getIPs() ;
    }

    private void getIPs() {

        StringBuilder info = new StringBuilder();
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        Log.i(TAG, "dhcpInfo="+dhcpInfo);
        info.append("dhcpInfo=");
        info.append(dhcpInfo+"").append("\n");
        // ipaddr
        // gateway
        // netmask
        // dns1
        // dns2
        // DHCP server
        // lease
        int ip = dhcpInfo.serverAddress;
        //此处获取ip为整数类型，需要进行转换
        final String strIp = intToIp(ip); // 172.20.160.1 ip --->< 27268268
        Log.i(TAG,strIp + " ip --->< " + ip);
        info.append("str Ip =").append(strIp).append("\n");
        info.append("ip =").append(ip).append("\n");

        mIpInfoTV.setText(info.toString());

    }



    private String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
                + ((i >> 24) & 0xFF);
    }


    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.test_back){
            finish();

        }
    }


}
