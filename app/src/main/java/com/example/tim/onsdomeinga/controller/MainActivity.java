package com.example.tim.onsdomeinga.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tim.onsdomeinga.R;
import com.example.tim.onsdomeinga.model.Device;
import com.example.tim.onsdomeinga.model.DimmableDevice;
import com.example.tim.onsdomeinga.model.GebruikersApplicatie;
import com.example.tim.onsdomeinga.model.ReadableDevice;
import com.example.tim.onsdomeinga.model.SwitchableDevice;
import com.example.tim.onsdomeinga.model.Cluster;
import com.example.tim.onsdomeinga.persistance.SerializeHandler;
import com.example.tim.onsdomeinga.proxy.OnsProxy;
import com.onsdomein.proxy.ProxyOnsDomein;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements KnowHcDialog.KnowHcDialogListener {
    private static GebruikersApplicatie gebruikersApplicatie = new GebruikersApplicatie();
    public static ArrayList<Device> deviceList = (ArrayList) gebruikersApplicatie.getDeviceList();
    public static ArrayList<Cluster> clusterList = new ArrayList<>();

    //    public static ProxyOnsDomein proxyOnsDomein = new ProxyOnsDomein();
    public static OnsProxy proxyOnsDomein = new OnsProxy();

    private int requestForId;
    private TextView huiscentrale;
    private Button apparaten;
    private Button clusters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onViewCreated();

        apparaten = findViewById(R.id.apparaten);
        clusters = findViewById(R.id.clusters);
        huiscentrale = findViewById(R.id.huiscentrale);

        checkForKnownHc();
        gebruikersApplicatie.loadClusterList(this);
        clusterList = (ArrayList) gebruikersApplicatie.getClusterList();

        for (Cluster cluster : clusterList) {
            System.out.println("Cluster " + cluster.getName() + " has " + cluster.getDevicesInCluster().size() + " devices.");
        }

        apparaten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ApparatenActivity.class);
                startActivity(intent);
            }
        });

        clusters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ClustersActivity.class);
                startActivity(intent);
            }
        });

        Toast.makeText(this, "Welcome back \'User\'!", Toast.LENGTH_SHORT).show();
    }

    public static GebruikersApplicatie getGa() {
        return gebruikersApplicatie;
    }

    public void onViewCreated() {
        connectToServer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gebruikersApplicatie.setDeviceList(deviceList);

        try {
            gebruikersApplicatie.getDcom().pushConfigToServer();
        } catch (Exception e) {
            System.out.println("Push to server failed, is there a connection?");
            e.printStackTrace();
        }

        SerializeHandler sh = new SerializeHandler();
        sh.saveAsSerializedData(this);
    }

    private void connectToServer() {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {

            // TODO: Absoluut niet de beste methode (Networking op de Main/UI-thread) maar hij werkt voor nu.
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            proxyOnsDomein.connectClientToServer("1234");
        } catch (Exception e) {
            System.out.println("Failed to connect.");
            e.printStackTrace();
        }
    }

    private void checkForKnownHc() {
        SharedPreferences prefs = getSharedPreferences("DOM_PREFS", MODE_PRIVATE);
        requestForId = prefs.getInt("hcCode", -1);

        if (requestForId == -1) {
            showKnowHcDialog();
        } else {
            huiscentrale.setText("Huiscentrale: " + requestForId);
            setButtonsEnabled();
        }
    }

    private void showKnowHcDialog() {
        KnowHcDialog knowHcDialog = new KnowHcDialog();
        knowHcDialog.show(getSupportFragmentManager(), "know hc dialog");
    }

    private void setButtonsEnabled() {
        if (requestForId == -1) {
            apparaten.setEnabled(false);
            apparaten.setBackground(getResources().getDrawable(R.drawable.button_rounded_gray));
            clusters.setEnabled(false);
            clusters.setBackground(getResources().getDrawable(R.drawable.button_rounded_gray));
        } else {
            apparaten.setEnabled(true);
            apparaten.setBackground(getResources().getDrawable(R.drawable.button_rounded_blue));
            clusters.setEnabled(true);
            clusters.setBackground(getResources().getDrawable(R.drawable.button_rounded_blue));
        }
    }

    @Override
    public void applyHcCode(int hcCode) {
        requestForId = hcCode;
        huiscentrale.setText("Huiscentrale: " + requestForId);
        setButtonsEnabled();

        SharedPreferences prefs = getSharedPreferences("DOM_PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("hcCode", hcCode);
        editor.apply();

        connectToServer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.settings:
                showKnowHcDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }
}