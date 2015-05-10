package com.stephenfacentedev.com.monkeydefense;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Stephen on 2/28/2015.
 */
public class CustomAdapter extends BaseAdapter{
    private ArrayList<lineItem> _data;
    Context _c;
    Field[] drawables = android.R.drawable.class.getFields();

    public CustomAdapter (ArrayList<lineItem> data, Context c)
    {
        _data = data;
        _c = c;
    }

    public int getCount()
    {
        return _data.size();
    }

    public Object getItem(int position)
    {
        return _data.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Typeface font = Typeface.createFromAsset(_c.getAssets(), "fonts/font.otf");

        View v = convertView;
        if (v==null)
        {
            LayoutInflater vi = (LayoutInflater)_c.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
            v = vi.inflate(R
                    .layout.score, null);
        }
        TextView uname = (TextView) v.findViewById(R.id.username);
        TextView score = (TextView)v.findViewById(R.id.score);
        TextView index = (TextView) v.findViewById(R.id.index);

        lineItem lineItem = (lineItem)_data.get(position);
        uname.setText(lineItem.getUsername());
        score.setText(lineItem.getScore()+" ");
        index.setText(lineItem.getIndex()+" ");
        applyFonts(v, font);

        return v;
    }

    public static void applyFonts(final View v, Typeface fontToSet)
    {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    applyFonts(child, fontToSet);
                }
            } else if (v instanceof TextView) {
                ((TextView)v).setTypeface(fontToSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
    }

}