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

import com.example.tim.onsdomeinga.R;
import com.example.tim.onsdomeinga.model.Cluster;
import com.example.tim.onsdomeinga.model.Device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClustersActivity extends AppCompatActivity {
    private ArrayList<Cluster> mClusters = MainActivity.clusterList;

    private ClustersAdapter mAdapter;
    private SearchView searchView;
    private boolean isInEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clusters);

        Intent intent = getIntent();
        isInEditMode = intent.getBooleanExtra("isInEditMode", false);

        FloatingActionButton clusterToevoegen = findViewById(R.id.cluster_toevoegen);
        clusterToevoegen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toClusterActivityIntent("", true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        buildRecyclerView();
        sorteerAZ(mClusters);
    }

    private void toClusterActivityIntent(String clusterName, Boolean willEdit) {
        Intent intent = new Intent(ClustersActivity.this, ClusterActivity.class);
        intent.putExtra("huidigCluster", clusterName);
        intent.putExtra("willEdit", willEdit);
        startActivity(intent);
    }

    private String getClusterNameForPosition(int position) {
        return mClusters.get(position).getName();
    }

    public void buildRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.clustersLijst);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new ClustersAdapter(mClusters, isInEditMode);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ClustersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                searchView.setQuery("", false);
                toClusterActivityIntent(getClusterNameForPosition(position), false);
            }

            @Override
            public void onEditClick(int position) {
                if (isInEditMode) {
                    searchView.setQuery("", false);
                    toClusterActivityIntent(getClusterNameForPosition(position), true);
                } else {
                    Cluster cluster = mClusters.get(position);
                    cluster.setSwitchedOn(!cluster.getSwitchedOn());

                    for (Device dev : cluster.getDevicesInCluster()) {
                        System.out.println("BEFORE: Apparaat " + dev.getName() + " staat aan = " + dev.getSwitchedOn());
                        dev.setSwitchedOn(cluster.getSwitchedOn());
                        System.out.println("AFTER: Apparaat " + dev.getName() + " staat aan = " + dev.getSwitchedOn());
                    }

                    mAdapter.notifyItemChanged(position);
                }
            }
        });
    }

    public void sorteerAZ(ArrayList<Cluster> clustersLijst) {
        Collections.sort(clustersLijst, new Comparator<Cluster>() {
            @Override
            public int compare(Cluster o1, Cluster o2) {
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
            setTitle("Clusters bewerken");
        } else {
            inflater.inflate(R.menu.menu_search_edit, menu);
            setTitle("Clusters");
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