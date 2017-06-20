package com.example.owner.projekat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class SaveDialog extends DialogFragment {

    private EditText name;
    private SaveDialogCallback sdc;

    public void setSaveDialogCallback(SaveDialogCallback saveDialogCallback){
        sdc=saveDialogCallback;
    }

    static SaveDialog newInstance(SaveDialogCallback saveDialogCallback){
        SaveDialog sd=new SaveDialog();
        sd.setSaveDialogCallback(saveDialogCallback);
        return sd;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View v=inflater.inflate(R.layout.save_dialog, null);
        name= (EditText) v.findViewById(R.id.mapname);
        builder.setView(v).setTitle(R.string.newmap).setPositiveButton(R.string.savebutton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mapn=name.getText().toString();
                sdc.saveMap(mapn);
            }
        }).setNegativeButton(R.string.canclle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();//super.onCreateDialog(savedInstanceState);
    }

    public interface SaveDialogCallback{
        void saveMap(String name);
    }
}
