package ru.zerotime.translator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by zeburek on 19.03.2017.
 */

public class SettingsCustomAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TSettingsListItem> arrayList;

    public SettingsCustomAdapter(Context context,ArrayList<TSettingsListItem> arrayList)
    {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*Setting my own view of Item in Settings ListView*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater vi =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.custom_row_settings, null);
        }
        final TSettingsListItem listItem = arrayList.get(position);
        if (listItem != null)
            ((TextView) convertView.findViewById(R.id.mainNameSettingsView)).setText(listItem.getMainText());
            ((TextView) convertView.findViewById(R.id.descNameView)).setText(listItem.getDescText());

        return convertView;
    }
}
