package com.example.samamab.networkdiscover;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.PortScan;
import com.stealthcopter.networktools.ping.PingResult;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    TextView textView, textViewDevices;
    Button buttonScanDevices;
    ListView listView;
    ArrayList<String> strArr = new ArrayList<String>();

    private ArrayAdapter<String> adapter;

    public final static String ID_EXTRA = "com.example.samamab.networkdiscover._ID";

    StringBuilder allDevices = new StringBuilder();
    Vector<String> devices = new Vector<>();
    Thread myThread;
    CircularProgressView progressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        textView = (TextView) findViewById(R.id.devices);
        textViewDevices = (TextView) findViewById(R.id.textViewDevices);
        buttonScanDevices = (Button) findViewById(R.id.buttonScan);
        progressView = (CircularProgressView) findViewById(R.id.progress_view);
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strArr);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

public void onItemClick(AdapterView<?>parent, View view, int position,long id){

    Intent intent =new Intent(MainActivity.this,MainActivity2.class);
    intent.putExtra("IP", strArr.get(position));
    startActivity(intent);

}});






    }




    public void getDevices() throws InterruptedException {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String localhost = getCurrentIp().getHostAddress();
                // final String LH =localhost+254;
               localhost = splitIP(localhost);
                for(int i = 1 ; i < 255; i++) {
                    try {

                        Ping.onAddress(localhost + i).doPing(new Ping.PingListener() {
                            @Override
                            public void onResult(PingResult pingResult) {

                                if(pingResult.getAddress().getHostName().matches("192.168.8.254")){
                                    updateFinal();

                                }

                                if(pingResult.isReachable()){
                                    allDevices.append(pingResult.getAddress().getHostName() + "\n");


                                    //Saving in vector for later use.
                                    devices.add(pingResult.getAddress().getHostName());
                                    updateUI();
                                }
                            }

                            @Override
                            public void onFinished() {

                            }
                        });
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        myThread = new Thread(runnable);
        myThread.start();

    }

    private void updateFinal() {
        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textViewDevices.setText("DEVICES");
                        buttonScanDevices.setText("Scan complete");
                        progressView.stopAnimation();
                        progressView.resetAnimation();
                        progressView.setEnabled(false);
                        progressView.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }.start();
    }

    private void updateUI() {
        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        strArr.add(devices.lastElement());
                    }
                });
            }
        }.start();
    }

    public void scanDevices(View view) throws InterruptedException {
        textViewDevices.setText("Please wait");
        progressView.startAnimation();
        textView.setText("");
        buttonScanDevices.setText("Scanning...");
        buttonScanDevices.setEnabled(false);

        getDevices();
    }

    public InetAddress getCurrentIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) networkInterfaces
                        .nextElement();
                Enumeration<InetAddress> nias = ni.getInetAddresses();
                while(nias.hasMoreElements()) {
                    InetAddress ia= (InetAddress) nias.nextElement();
                    if (!ia.isLinkLocalAddress()
                            && !ia.isLoopbackAddress()
                            && ia instanceof Inet4Address) {
                        return ia;
                    }
                }
            }
        } catch (SocketException e) {
            e.getMessage();
        }
        return null;
    }

    public String splitIP(String ip){
        int i = 1;
        while(ip.charAt(ip.length() - i) != '.'){
            i++;
        }

        return ip.substring(0, (ip.length()-i) + 1);
    }








}
