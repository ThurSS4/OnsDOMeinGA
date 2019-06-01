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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClustersActivity extends AppCompatActivity {
    private ArrayList<Cluster> mClusters = MainActivity.clusterList;

    private ClustersAdapter mAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clusters);

        setTitle("Clusters");

        FloatingActionButton clusterToevoegen = findViewById(R.id.cluster_toevoegen);
        clusterToevoegen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClustersActivity.this, ClusterActivity.class);
                intent.putExtra("huidigCluster", "");
                intent.putExtra("willEdit", true);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        buildRecyclerView();
        sorteerAZ(mClusters);
    }

    public void buildRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.clustersLijst);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new ClustersAdapter(mClusters);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ClustersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                searchView.setQuery("", false);

                Cluster cluster = mClusters.get(position);
                String name = cluster.getName();

                Intent intent = new Intent(ClustersActivity.this, ClusterActivity.class);
                intent.putExtra("huidigCluster", name);
                intent.putExtra("willEdit", false);
                startActivity(intent);
            }

            @Override
            public void onEditClick(int position) {
                searchView.setQuery("", false);

                Cluster cluster = mClusters.get(position);
                String name = cluster.getName();

                Intent intent = new Intent(ClustersActivity.this, ClusterActivity.class);
                intent.putExtra("huidigCluster", name);
                intent.putExtra("willEdit", true);
                startActivity(intent);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_edit, menu);

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