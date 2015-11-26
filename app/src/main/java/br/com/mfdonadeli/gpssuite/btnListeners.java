package br.com.mfdonadeli.gpssuite;

import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by mfdonadeli on 10/30/15.
 */
public class btnListeners implements View.OnClickListener {
    private final Context context;

    public btnListeners(Context context){
        this.context = context;
    }
    @Override
    public void onClick(View view) {

        int a;
        int b;

        a = R.id.buttonManage;
        b = view.getId();

        if(view.getId() == R.id.buttonManage){
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.expandable);
            dialog.setTitle("Places");

            final ExpandableListView expandableListView = (ExpandableListView) dialog.findViewById(R.id.expandablePlaces);
            final Button btnClose = (Button) dialog.findViewById(R.id.btnClose);

            String[] sArr = AstroCalc.readPlacesFile(context);

            if(sArr == null)
                return;


            ArrayList<String> list = new ArrayList<String>();
            for(int i=0; i<sArr.length; i++)
                list.add(sArr[i]);

            final ExpandableListAdapter adapter = new ExpandableListAdapter(context, list);
            expandableListView.setAdapter(adapter);

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> listFinal = adapter.getList();
                    String s = "";

                    for(int i=0; i<listFinal.size(); i++)
                        s+=listFinal.get(i)+"\n";

                    AstroCalc.saveToPlacesFile(context, s, 1);
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
        else if(view.getId() == R.id.buttonSave){
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_places);
            dialog.setTitle("Places");

            final EditText tvNome = (EditText) dialog.findViewById(R.id.editNome);
            final EditText tvLatitude = (EditText) dialog.findViewById(R.id.editLatitude);
            final EditText tvLongitude = (EditText) dialog.findViewById(R.id.editLongitude);
            Button btnOk = (Button) dialog.findViewById(R.id.btnOK);
            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

            tvLatitude.setText(MainActivity.tvLatitude.getText());
            tvLongitude.setText(MainActivity.tvLongitude.getText());

            tvLatitude.setHint("0.00");
            tvLongitude.setHint("0.00");

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s = tvNome.getText() + ";" + tvLatitude.getText() + ";" + tvLongitude.getText() + "\n";
                    AstroCalc.saveToPlacesFile(context, s, 0);

                    Toast.makeText(context, "Information Saved", Toast.LENGTH_SHORT);

                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }
}
