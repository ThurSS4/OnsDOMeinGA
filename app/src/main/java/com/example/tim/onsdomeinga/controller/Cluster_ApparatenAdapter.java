package com.example.tim.onsdomeinga.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tim.onsdomeinga.R;
import com.example.tim.onsdomeinga.model.Device;

import java.util.ArrayList;

public class Cluster_ApparatenAdapter extends RecyclerView.Adapter<Cluster_ApparatenAdapter.ApparaatViewHolder> {
    private ArrayList<Device> mApparaten;
    private ArrayList<Device> geselecteerdeApparaten;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onSelectClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ApparaatViewHolder extends RecyclerView.ViewHolder {
        public ImageView mApparaatIcon;
        public TextView mApparaatNaam;
        public TextView mApparaatOmschrijving;
//        public ImageView mEditImage;
        public CheckBox selectApparaat;

        public ApparaatViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mApparaatIcon = itemView.findViewById(R.id.cluster_apparaatIcoon);
            mApparaatNaam = itemView.findViewById(R.id.cluster_apparaatNaam);
            mApparaatOmschrijving = itemView.findViewById(R.id.cluster_apparaatOmschrijving);
            //mEditImage = itemView.findViewById(R.id.cluster_add_apparaat);
            selectApparaat = itemView.findViewById(R.id.cluster_select_apparaat);

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

            selectApparaat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onSelectClick(position);
                        }
                    }
                }
            });
        }
    }

    public Cluster_ApparatenAdapter(ArrayList<Device> apparaten, ArrayList<Device> geselecteerdeApparaten) {
        mApparaten = apparaten;
        this.geselecteerdeApparaten = geselecteerdeApparaten;
    }

    @Override
    public ApparaatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cluster_apparaat, parent, false);
        ApparaatViewHolder evh = new ApparaatViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ApparaatViewHolder holder, int position) {
        Device currentItem = mApparaten.get(position);

        holder.mApparaatIcon.setImageResource(android.R.drawable.ic_menu_edit);
        holder.mApparaatNaam.setText(currentItem.getName());
        holder.mApparaatOmschrijving.setText("Apparaat omschrijving");

        /*
        if (geselecteerdeApparaten.contains(currentItem)) {
//            holder.setEditImage(R.drawable.ic_delete_black);
            holder.selectApparaat.setChecked(true);
        } else {
//            holder.setEditImage(R.drawable.ic_add_black);
            holder.selectApparaat.setChecked(false);
        }
        */

        for (Device dev : geselecteerdeApparaten) {
            if (dev.getName().equals(currentItem.getName())) {
                holder.selectApparaat.setChecked(true);
                break;
            } else {
                holder.selectApparaat.setChecked(false);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mApparaten.size();
    }
}