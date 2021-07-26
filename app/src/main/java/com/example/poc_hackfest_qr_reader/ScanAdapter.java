package com.example.poc_hackfest_qr_reader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ViewHolder> {

    private final ArrayList<PassportScan> passportScans;
    private final ListItemClickListener onListItemClickListener;

    public ScanAdapter(ArrayList<PassportScan> dataSet, ListItemClickListener listener) {
        this.passportScans = dataSet;
        this.onListItemClickListener = listener;
    }

    @Override
    public ScanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_passport_vaccinal, parent, false);
        return new ViewHolder(view, this.onListItemClickListener);
    }

    @Override
    public void onBindViewHolder(ScanAdapter.ViewHolder viewHolder, int position) {
        PassportScan passportScan = passportScans.get(position);
        PassportVaccinal passportVaccinal = passportScan.getPassport();
        viewHolder.getLblScanDate().setText(passportVaccinal.getScanDate());
        viewHolder.getLblFirstName().setText(passportVaccinal.getFirstName());
        viewHolder.getLblLastName().setText(passportVaccinal.getFamilyName());
    }

    @Override
    public int getItemCount() {
        return this.passportScans.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView lblScanDate;
        private final TextView lblFirstName;
        private final TextView lblLastName;
        private ListItemClickListener mListener;

        public ViewHolder(View view, ListItemClickListener listener) {
            super(view);

            this.lblScanDate = view.findViewById(R.id.lblScanDate);
            this.lblFirstName = view.findViewById(R.id.lblFirstName);
            this.lblLastName = view.findViewById(R.id.lblLastName);
            this.mListener = listener;

            view.setOnClickListener(this);
        }

        public TextView getLblScanDate() { return this.lblScanDate; }
        public TextView getLblFirstName() { return this.lblFirstName; }
        public TextView getLblLastName() { return this.lblLastName; }

        @Override
        public void onClick(View view) {
            this.mListener.onListItemClickListener(getAdapterPosition());
        }
    }

    public interface ListItemClickListener {
        void onListItemClickListener(int position);
    }
}
