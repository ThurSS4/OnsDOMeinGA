package com.example.tim.onsdomeinga.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tim.onsdomeinga.R;
import com.example.tim.onsdomeinga.model.Cluster;
import com.example.tim.onsdomeinga.model.Device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClustersAdapter extends RecyclerView.Adapter<ClustersAdapter.ClusterViewHolder> implements Filterable {
    private ArrayList<Cluster> mClusters;
    private ArrayList<Cluster> mClustersVolledig;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onEditClick(int position);
    }

    // LET OP! ClustersAdapter maakt gebruik van dezelfde item_apparaat als ApparatenAdapter !!
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ClusterViewHolder extends RecyclerView.ViewHolder {
        public ImageView mClusterIcon;
        public TextView mClusterNaam;
        public TextView mClusterOmschrijving;
        public ImageView mEditImage;

        public ClusterViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mClusterIcon = itemView.findViewById(R.id.apparaatIcoon);
            mClusterNaam = itemView.findViewById(R.id.apparaatNaam);
            mClusterOmschrijving = itemView.findViewById(R.id.apparaatOmschrijving);
            mEditImage = itemView.findViewById(R.id.image_edit_apparaat);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            mEditImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onEditClick(position);
                        }
                    }
                }
            });
        }
    }

    public ClustersAdapter(ArrayList<Cluster> clusters) {
        mClusters = clusters;
        mClustersVolledig = new ArrayList<>(MainActivity.clusterList);
    }

    @Override
    public ClusterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apparaat, parent, false);
        ClusterViewHolder evh = new ClusterViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ClusterViewHolder holder, int position) {
        Cluster currentItem = mClusters.get(position);

        holder.mClusterIcon.setImageResource(R.drawable.ic_group_work);
        holder.mClusterNaam.setText(currentItem.getName());

        if (currentItem.getName().equals("The Big Bang")) {
            holder.mClusterOmschrijving.setText("It's not just a theory!");
        } else {

            String str = "";

            for (Device dev : currentItem.getDevicesInCluster()) {
                str += dev.getName() + ", ";
            }

            str = str.substring(0, str.length() - 2);
            holder.mClusterOmschrijving.setText(str);
        }
    }

    @Override
    public int getItemCount() {
        return mClusters.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Cluster> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mClustersVolledig);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Cluster item : mClustersVolledig) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mClusters.clear();
            mClusters.addAll((ArrayList) results.values);

            // Filter-resultaten sorteren van A -> Z
            Collections.sort(mClusters, new Comparator<Cluster>() {
                @Override
                public int compare(Cluster o1, Cluster o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            notifyDataSetChanged();

        }
    };
}