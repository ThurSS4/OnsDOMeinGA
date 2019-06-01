package com.example.tim.onsdomeinga.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tim.onsdomeinga.R;
import com.example.tim.onsdomeinga.model.Device;
import com.example.tim.onsdomeinga.model.Cluster;

import java.util.ArrayList;

public class ClusterActivity extends AppCompatActivity {
    private ArrayList<Device> mApparaten = MainActivity.deviceList;
    private ArrayList<Device> geselecteerdeApparaten;

    private TextView clusterTitel;
    private EditText edit_naam;
    private Switch isAan;

    private Cluster mCluster;
    private boolean isEditing;
    private boolean isNewCluster;
    private int clusterIndex = -1;
    private int menuToLoad = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster);

        getIntentInfo();
        connectUI();

        setupUIElements();
        setupListeners();
    }

    private void getIntentInfo() {
        Intent intent = getIntent();
        String clusterName = intent.getStringExtra("huidigCluster");

        if (clusterName.equals("")) {
            mCluster = new Cluster("");
            isEditing = true;
            geselecteerdeApparaten = mCluster.getDevicesInCluster();
        } else {
            for (int i = 0; i < MainActivity.clusterList.size(); i++) {
                if (MainActivity.clusterList.get(i).getName().equals(clusterName)) {
                    clusterIndex = i;
                    break;
                }
            }

            mCluster = MainActivity.clusterList.get(clusterIndex);
            isEditing = intent.getBooleanExtra("willEdit", false);
            geselecteerdeApparaten = mCluster.getDevicesInCluster();
        }
    }

    private void showKeyboard(boolean toShow) {
        if (toShow) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    private void connectUI() {
        int menuEditRemove = R.menu.menu_edit_remove;
        int menuSave = R.menu.menu_save;
        int menuCancelSave = R.menu.menu_cancel_save;

        if (isEditing && mCluster.getName().equals("")) {
            setTitle("Nieuwe cluster");
            showKeyboard(true);
            isNewCluster = true;
            menuToLoad = menuSave;
        } else if (isEditing) {
            setTitle("Cluster wijzigen");
            showKeyboard(true);
            isNewCluster = false;
            menuToLoad = menuCancelSave;
        } else {
            setTitle("Cluster");
            showKeyboard(false);
            isNewCluster = false;
            menuToLoad = menuEditRemove;
        }

        isAan = findViewById(R.id.switch_isAan);
        clusterTitel = findViewById(R.id.clusterTitel);
        edit_naam = findViewById(R.id.edit_clusternaam);

        buildRecyclerView();
    }

    private void setupUIElements() {
        edit_naam.setEnabled(isEditing);
        isAan.setEnabled(true);

        clusterTitel.setText(mCluster.getName());
        edit_naam.setText(mCluster.getName());
//        isAan.setChecked(mCluster.isSwitchedOn());
        isAan.setChecked(mCluster.requestCurrentValue());
    }

    private void setupListeners() {
        isAan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(" ");
                System.out.println("Aantal gesl. app. = " + geselecteerdeApparaten.size());
                for (Device dev : geselecteerdeApparaten) {
                    System.out.println("BEFORE: Apparaat " + dev.getName() + " staat aan = " + dev.getSwitchedOn());
                    dev.setSwitchedOn(isAan.isChecked());
                    System.out.println("AFTER: Apparaat " + dev.getName() + " staat aan = " + dev.getSwitchedOn());
                }
                mCluster.setSwitchedOn(isAan.isChecked());
            }
        });

        edit_naam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clusterTitel.setText(s);
                } else {
                    clusterTitel.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void buildRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.apparaatLijst);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        Cluster_ApparatenAdapter adapter = new Cluster_ApparatenAdapter(mApparaten, geselecteerdeApparaten);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if (isEditing) {
            adapter.setOnItemClickListener(new Cluster_ApparatenAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                }

                @Override
                public void onSelectClick(int position) {
                    if (geselecteerdeApparaten.contains(mApparaten.get(position))) {
                        geselecteerdeApparaten.remove(mApparaten.get(position));
                    } else {
                        geselecteerdeApparaten.add(mApparaten.get(position));
                    }
                }
            });
        }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException npe) {
            System.out.println("Fontys - Error hiding keyboard.");
        }
    }

    private void saveCluster() {
        String name = edit_naam.getText().toString();

        if (!mCluster.checkStringLength(name)) {
            Toast.makeText(this, "Please enter a valid name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isNewCluster) {
            if (!mCluster.changeName(name)) {
                Toast.makeText(this, "Please enter a valid name.", Toast.LENGTH_SHORT).show();
                return;
            }

            MainActivity.clusterList.add(new Cluster(name));

            int tempIndex = MainActivity.clusterList.size() - 1;
            Cluster tempCluster = MainActivity.clusterList.get(tempIndex);

            for (Device apparaat : geselecteerdeApparaten) {
                tempCluster.addDeviceToCluster(mApparaten.get(mApparaten.indexOf(apparaat)));
            }

//            if (isAan.isChecked()) {
//                tempCluster.switchOn();
//            } else {
//                tempCluster.switchOff();
//            }

            Toast.makeText(this, "Cluster \'" + tempCluster.getName() + "\' with " + tempCluster.getDevicesInCluster().size() + " devices was added.", Toast.LENGTH_SHORT).show();
        } else if (isEditing) {
            Cluster tempCluster = MainActivity.clusterList.get(clusterIndex);

            if (!tempCluster.changeName(name)) {
                Toast.makeText(this, "Please enter a valid name.", Toast.LENGTH_SHORT).show();
                return;
            }

            System.out.println(" ");
            System.out.println("SAVE - isAan checked = " + isAan.isChecked());
//            if (isAan.isChecked()) {
//                tempCluster.setSwitchedOn(true);
//                tempCluster.switchOn();
//            } else {
//                tempCluster.setSwitchedOn(false);
//                tempCluster.switchOff();
//            }

            Toast.makeText(this, "Updated cluster \'" + name + "\'.", Toast.LENGTH_SHORT).show();
        }

        this.finish();
    }

    private void removeClusterAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(ClusterActivity.this);
        alert.setTitle("Cluster verwijderen?");
        alert.setMessage("Weet u zeker dat u deze cluster wilt verwijderen?");
        alert.setPositiveButton("Ja, verwijder!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeCluster();
            }
        });
        alert.setNegativeButton("Nee, toch niet.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ClusterActivity.this, "Deleting cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setCancelable(false);
        alert.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_delete_black));

        alert.create().show();
    }

    private void removeCluster() {
        String name = edit_naam.getText().toString();

        MainActivity.clusterList.remove(mCluster);
        Toast.makeText(this, "Removed cluster \'" + name + "\'.", Toast.LENGTH_SHORT).show();

        System.out.println("Fontys - Size Clusterlist = " + MainActivity.clusterList.size());
        this.finish();
    }

    private void reloadActivity(boolean willEdit) {
        Intent intent = getIntent();
        intent.putExtra("willEdit", willEdit);
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menuToLoad != 0) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(menuToLoad, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideKeyboard();

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.cancelChange:
                reloadActivity(false);
                return true;
            case R.id.edit:
                reloadActivity(true);
                return true;
            case R.id.remove:
                removeClusterAlert();
                return true;
            case R.id.save:
                saveCluster();
                return true;
            case R.id.saveChange:
                saveCluster();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
