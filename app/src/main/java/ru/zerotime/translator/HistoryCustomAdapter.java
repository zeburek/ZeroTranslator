package ru.zerotime.translator;

/**
 * Created by zeburek on 19.03.2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryCustomAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<THistoryListItem> arrayList;

    public HistoryCustomAdapter(Context context, ArrayList<THistoryListItem> arrayList)
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

    /*Setting my own view of Item in History ListView*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater vi =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.custom_row, null);
        }
        final THistoryListItem listItem = arrayList.get(position);
        if (listItem != null)
            ((TextView) convertView.findViewById(R.id.mainNameView)).setText(listItem.getMainText());
            ((TextView) convertView.findViewById(R.id.translatedNameView)).setText(listItem.getTranslateText());
            ((TextView) convertView.findViewById(R.id.mainLangPairView)).setText(listItem.getLangPair());
            if(listItem.getIsBookmark()){
                ((ImageView) convertView.findViewById(R.id.isBookmarked)).setImageResource(R.drawable.icon_on_bookmarks);
            }else {
                ((ImageView) convertView.findViewById(R.id.isBookmarked)).setImageResource(R.drawable.icon_off_bookmarks);
            }

        return convertView;
    }
}
