package br.com.mfdonadeli.gpssuite;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mfdonadeli on 10/22/15.
 */
public class ListGraphAdapter extends BaseAdapter {

    TextView txtFirst;
    TextView txtSecond;
    BarDrawView txtThird;
    TextView txtFourth;
    TextView txtFifth;
    Activity activity;
    ArrayList<String> list;
    int layout;

    public ListGraphAdapter(Activity activity,ArrayList<String> list,int layout) {
        super();
        this.activity=activity;
        this.list=list;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = activity.getLayoutInflater();

        if(view == null && layout == R.layout.list_item)
        {
            view = inflater.inflate(layout, null);

            txtFirst = (TextView) view.findViewById(R.id.textPrn);
            txtSecond = (TextView) view.findViewById(R.id.textFlag);
            txtThird = (BarDrawView) view.findViewById(R.id.barSnr);
            txtFourth = (TextView) view.findViewById(R.id.textElevation);
            txtFifth = (TextView) view.findViewById(R.id.textAzimuth);
        }
        else if(view == null && layout == R.layout.list_item_places)
        {
            view = inflater.inflate(layout, null);

            txtFirst = (TextView) view.findViewById(R.id.textPlace);
            txtSecond = (TextView) view.findViewById(R.id.textDistance);
            txtFourth = (TextView) view.findViewById(R.id.textDirection);
        }

        if(layout == R.layout.list_item) {
            String sItem = this.list.get(i);
            String[] sItems = sItem.split("-");

            txtFirst.setText(sItems[0]);
            txtSecond.setText(setFlag(sItems[0]));
            txtFourth.setText(sItems[2]);
            txtFifth.setText(sItems[3]);

            float i2 = Float.parseFloat(sItems[1]);
            if (i2 < 20) {
                txtThird.setColor(Color.RED);
            } else if (i2 > 30) {
                txtThird.setColor(Color.GREEN);
            } else
                txtThird.setColor(Color.YELLOW);

            txtThird.setSize((int) i2);
            txtThird.setText(sItems[1]);

            Log.d("LGA", sItems[0] + " " + sItems[1] + " " + sItems[2] + " " + sItems[3]);

            //DrawBar(sItems[2]);

            //txtFirst.setText(map.get(FIRST_COLUMN));
            //txtSecond.setText(map.get(SECOND_COLUMN));
            //txtThird.setText(map.get(THIRD_COLUMN));
            //txtFourth.setText(map.get(FOURTH_COLUMN));
        }
        else if(layout == R.layout.list_item_places)
        {
            String sItem = this.list.get(i);
            String[] sItems = sItem.split(";");

            txtFirst.setText(sItems[0]);
            txtSecond.setText(sItems[3]);
            txtFourth.setText(sItems[4]);

        }

        return view;
    }

    String setFlag(String s)
    {
        int i1 = (int)Float.parseFloat(s);

        if(i1 > 0 && i1 < 65)
            return "U";
        else if (i1 < 1 || i1 > 64)
        {
            if (i1 >= 65 && i1 <= 89)
            {
                return "R";
            }
            if (i1 < 120 || i1 > 138)
            {
                if (i1 >= 173 && i1 <= 193)
                {
                    return "C";
                }
                return i1 < 201 || i1 > 237 ? "N" : "O";
            }
        }
        return "-";
    }
}
