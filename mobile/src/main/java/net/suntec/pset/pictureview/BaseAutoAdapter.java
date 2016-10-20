package net.suntec.pset.pictureview;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wangzhanfei on 16-10-17.
 */

public abstract class BaseAutoAdapter {

    public abstract int getCounts(); //返回数据数量

    public abstract Object getItem(int position); //当前Item的数据

    public abstract View getItemView(int position, ViewGroup parent); //返回Item的布局

}
