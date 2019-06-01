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
import com.example.tim.onsdomeinga.model.Device;
import com.example.tim.onsdomeinga.model.DigitalDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ApparatenAdapter extends RecyclerView.Adapter<ApparatenAdapter.ApparaatViewHolder> implements Filterable {
    private ArrayList<Device> mApparaten;
    private ArrayList<Device> mApparatenVolledig;
    private OnItemClickListener mListener;
    private boolean isInEditMode;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onEditClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ApparaatViewHolder extends RecyclerView.ViewHolder {
        public ImageView mApparaatIcon;
        public TextView mApparaatNaam;
        public TextView mApparaatOmschrijving;
        public ImageView mEditImage;

        public ApparaatViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mApparaatIcon = itemView.findViewById(R.id.apparaatIcoon);
            mApparaatNaam = itemView.findViewById(R.id.apparaatNaam);
            mApparaatOmschrijving = itemView.findViewById(R.id.apparaatOmschrijving);
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

    public ApparatenAdapter(ArrayList<Device> apparaten, boolean isInEditMode) {
        mApparaten = apparaten;
        mApparatenVolledig = new ArrayList<>(MainActivity.deviceList);
        this.isInEditMode = isInEditMode;
    }

    @Override
    public ApparaatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apparaat, parent, false);
        ApparaatViewHolder evh = new ApparaatViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ApparaatViewHolder holder, int position) {
        Device currentItem = mApparaten.get(position);

        holder.mApparaatIcon.setImageResource(R.drawable.ic_power_black);
        holder.mApparaatNaam.setText(currentItem.getName());
        holder.mApparaatOmschrijving.setText("Apparaat omschrijving");

        if (isInEditMode) {
            holder.mEditImage.setImageResource(R.drawable.ic_edit_black);
        } else if (currentItem instanceof DigitalDevice){
            if (currentItem.getSwitchedOn() && currentItem.isActivated()) {
                holder.mEditImage.setImageResource(R.drawable.ic_lightbulb_green);
            } else if (!currentItem.getSwitchedOn() && currentItem.isActivated()) {
                holder.mEditImage.setImageResource(R.drawable.ic_lightbulb_black);
            } else {
                holder.mEditImage.setImageResource(R.drawable.ic_block_red);
            }
        } else {
            holder.mEditImage.setImageResource(R.drawable.ic_videocam_black);
        }
    }

    @Override
    public int getItemCount() {
        return mApparaten.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Device> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                MainActivity.deviceList.addAll(mApparatenVolledig);
                filteredList.addAll(mApparatenVolledig);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Device item : mApparatenVolledig) {
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
            mApparaten.clear();
            mApparaten.addAll((ArrayList) results.values);

            // Filter-resultaten sorteren van A -> Z
            Collections.sort(mApparaten, new Comparator<Device>() {
                @Override
                public int compare(Device o1, Device o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            notifyDataSetChanged();
        }
    };
}