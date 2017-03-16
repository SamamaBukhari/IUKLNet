package com.example.samamab.networkdiscover;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.stealthcopter.networktools.PortScan;

import java.util.ArrayList;

/**
 * Created by hp on 8/15/2016.
 */
public class MainActivity2 extends  MainActivity {
    private String ip;
    private TextView resultText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ip = getIntent().getStringExtra("IP");

        resultText = (TextView) findViewById(R.id.resultText);
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    doPortScan();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    private void doPortScan() throws Exception{

        appendResultsText("PortScanning IP: "+ ip);
        ArrayList<Integer> openPorts = PortScan.onAddress(ip).setPort(21).doScan();

        PortScan.onAddress(ip).setTimeOutMillis(1000).setPortsAll().doScan(new PortScan.PortListener() {
            @Override
            public void onResult(int portNo, boolean open) {
                if (open){
                    Log.d("OPEN", String.valueOf(portNo));
                    appendResultsText("Open: "+portNo);
                }
            }

            @Override
            public void onFinished(ArrayList<Integer> openPorts) {
                appendResultsText("Open Ports: "+openPorts.size());
            }
        });



    }


    private void appendResultsText(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultText.append(text+"\n");
            }
        });
    }

}
