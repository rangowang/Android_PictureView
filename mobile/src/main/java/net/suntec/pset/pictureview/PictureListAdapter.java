package net.suntec.pset.pictureview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by wangzhanfei on 16-10-17.
 */

public class PictureListAdapter extends BaseAutoAdapter {
    private Context context;
    private List<Integer> myData;

    public PictureListAdapter(Context context, List<Integer> myData) {
        this.context = context;
        this.myData = myData;
    }

    @Override
    public int getCounts() {
        return myData.size();
    }

    @Override
    public Object getItem(int position) {
        return myData.get(position);
    }

    @Override
    public View getItemView(int position, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_picture_list, null);

        return view;
    }
}
