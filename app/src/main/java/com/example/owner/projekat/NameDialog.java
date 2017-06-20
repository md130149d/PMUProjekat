package com.example.owner.projekat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


public class NameDialog extends DialogFragment {

    private EditText name;
    private PlayerNameCallback player;
    private String mapname;

    static NameDialog getInstance(PlayerNameCallback player, String mapname){
        NameDialog nd=new NameDialog();
        nd.player=player;
        nd.mapname=mapname;
        return nd;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View v=inflater.inflate(R.layout.save_dialog, null);
        name= (EditText) v.findViewById(R.id.mapname);
        name.setText("Player");
        builder.setView(v).setTitle(mapname).setPositiveButton(R.string.savebutton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mapn=name.getText().toString();
                if(mapn==null || mapn.isEmpty()){
                    return;
                }
                player.saveScore(mapn);
            }
        });
        return builder.create();
    }

    public interface PlayerNameCallback {
        void saveScore(String name);
    }
}
