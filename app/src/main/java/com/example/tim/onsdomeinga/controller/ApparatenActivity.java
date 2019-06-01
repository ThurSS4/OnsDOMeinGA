package com.example.tim.onsdomeinga.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.example.tim.onsdomeinga.R;
import com.example.tim.onsdomeinga.interfaces.Readable;
import com.example.tim.onsdomeinga.model.Device;
import com.example.tim.onsdomeinga.model.DigitalDevice;
import com.example.tim.onsdomeinga.model.ReadableDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ApparatenActivity extends AppCompatActivity {
    private ArrayList<Device> mApparaten = MainActivity.deviceList;

    private ApparatenAdapter mAdapter;
    private SearchView searchView;
    private boolean isInEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apparaten);

        Intent intent = getIntent();
        isInEditMode = intent.getBooleanExtra("isInEditMode", false);

        FloatingActionButton apparaatToevoegen = findViewById(R.id.apparaat_toevoegen);
        apparaatToevoegen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApparatenActivity.this, ApparaatActivity.class);
                intent.putExtra("huidigApparaat", "");
                intent.putExtra("willEdit", true);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        buildRecyclerView();
        sorteerAZ(mApparaten);
    }

    public void buildRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.apparatenLijst);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new ApparatenAdapter(mApparaten, isInEditMode);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ApparatenAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                searchView.setQuery("", false);

                Device dev = mApparaten.get(position);
                String name = dev.getName();

                Intent intent = new Intent(ApparatenActivity.this, ApparaatActivity.class);
                intent.putExtra("huidigApparaat", name);
                intent.putExtra("willEdit", false);
                startActivity(intent);
            }

            @Override
            public void onEditClick(int position) {
                if (isInEditMode) {
                    searchView.setQuery("", false);

                    Device dev = mApparaten.get(position);
                    String name = dev.getName();

                    Intent intent = new Intent(ApparatenActivity.this, ApparaatActivity.class);
                    intent.putExtra("huidigApparaat", name);
                    intent.putExtra("willEdit", true);
                    startActivity(intent);
                } else {
                    Device dev = mApparaten.get(position);

                    if (dev.isActivated() && dev instanceof DigitalDevice) {
                        dev.setSwitchedOn(!dev.getSwitchedOn());
//                        if (dev.getSwitchedOn()) {
//                            dev.switchOff();
//                        } else {
//                            dev.switchOn();
//                        }
                    } else if (dev instanceof ReadableDevice) {
                        Toast.makeText(ApparatenActivity.this, "Readable devices can't be switched on or off.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ApparatenActivity.this, "Please activate the device first.", Toast.LENGTH_SHORT).show();
                    }

                    mAdapter.notifyItemChanged(position);
                }
            }
        });
    }

    public void sorteerAZ(ArrayList<Device> apparatenLijst) {
        Collections.sort(apparatenLijst, new Comparator<Device>() {
            @Override
            public int compare(Device o1, Device o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    private void reloadActivity() {
        Intent intent = getIntent();
        intent.putExtra("isInEditMode", isInEditMode);
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        } else if (id == R.id.toggleEdit) {
            System.out.println("isInEditMode pre - " + isInEditMode);
            isInEditMode = !isInEditMode;
            reloadActivity();
            System.out.println("isInEditMode post - " + isInEditMode);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (isInEditMode) {
            inflater.inflate(R.menu.menu_search_doneedit, menu);
            setTitle("Apparaten bewerken");
        } else {
            inflater.inflate(R.menu.menu_search_edit, menu);
            setTitle("Apparaten");
        }

        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }
}
