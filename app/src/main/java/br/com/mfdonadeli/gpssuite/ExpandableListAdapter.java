package br.com.mfdonadeli.gpssuite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mfdonadeli on 11/4/15.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private ArrayList<String> list;
    private Context context;

    public ExpandableListAdapter(Context context, ArrayList<String> list)
    {
        this.list = list;
        this.context = context;
    }
    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return list.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return list.get(i);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String position = (String) getGroup(i);
        String[] list = position.split(";");

        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_1, null);
        }

        TextView tv = (TextView)view.findViewById(android.R.id.text1);
        tv.setText(list[0]);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String position = (String) getChild(i, i1);
        String[] sList = position.split(";");
        final int iPos = i;

        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dialog_places, null);
        }

        final EditText edit1 = (EditText) view.findViewById(R.id.editNome);
        final EditText edit2 = (EditText) view.findViewById(R.id.editLatitude);
        final EditText edit3 = (EditText) view.findViewById(R.id.editLongitude);
        Button button1 = (Button) view.findViewById(R.id.btnOK);
        Button button2 = (Button) view.findViewById(R.id.btnCancel);

        button2.setText(context.getResources().getString(R.string.delete));

        edit1.setText(sList[0]);
        edit2.setText(sList[1]);
        edit3.setText(sList[2]);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = edit1.getText() + ";" + edit2.getText() + ";" + edit3.getText();
                list.set(iPos, s);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(iPos);
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    public ArrayList<String> getList()
    {
        return this.list;
    }
}
