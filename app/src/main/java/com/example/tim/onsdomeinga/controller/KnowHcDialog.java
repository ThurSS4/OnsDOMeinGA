package com.example.tim.onsdomeinga.controller;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.tim.onsdomeinga.R;

public class KnowHcDialog extends AppCompatDialogFragment {
    private EditText hcCode;
    private KnowHcDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (KnowHcDialogListener) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException(context.toString() + "Must implement KnownHcDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_hcknown_dailog, null);

        builder.setView(view)
                .setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_power_black))
                .setTitle("HC Connection")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int enteredHcCode;

                        try {
                            enteredHcCode = Integer.parseInt(hcCode.getText().toString());
                        } catch (NumberFormatException nfe) {
                            enteredHcCode = -1;
                        }

                        listener.applyHcCode(enteredHcCode);
                    }
                })
                .setCancelable(false);

        hcCode = view.findViewById(R.id.editText_hcCode);

        return builder.create();
    }

    public interface KnowHcDialogListener {
        void applyHcCode(int hcCode);
    }
}
