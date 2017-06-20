package com.example.owner.projekat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

/**
 * Created by Owner on 2/20/2017.
 */

public class DialogMessage extends DialogFragment {

    String message;

    static DialogMessage newInstance(String message){
        DialogMessage dm=new DialogMessage();
        dm.setMessage(message);
        return dm;
    }

    public void setMessage(String message){
        this.message=message;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //LayoutInflater inflater=getActivity().getLayoutInflater();

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setMessage(message).setTitle(R.string.newmap).setPositiveButton(R.string.ook, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
