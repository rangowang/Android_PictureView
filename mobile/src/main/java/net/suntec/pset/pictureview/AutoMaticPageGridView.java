package net.suntec.pset.pictureview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ouhimehime on 16/5/4.
 * ----------自动分页的GridView----------
 */
public class AutoMaticPageGridView extends RelativeLayout {

    //分页所用
    private ViewPager viewPager;
    //导航点
    private RadioGroup radioGroup;
    //自定义行数
    private int lines = 0;
    //自定义列数
    private int column = 0;
    //自定义按钮样式
    private int btn_res;
    //自定义属性是否显示导航点
    private boolean btn_isvisible;
    //页数-需要动态计算
    private int pages = 0;
    //适配器
    private BaseAutoAdapter adapter;
    //Item的高度
    private int itemHeight = 0;

    //点击事件的接口
    public interface OnItemClickCallBack {
        void OnItemClicked(int position, Object object);
    }

    private OnItemClickCallBack onItemClickCallBack;

    public void setOnItemClickListener(OnItemClickCallBack onItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack;
    }


    public AutoMaticPageGridView(Context context) {
        super(context);
    }

    public AutoMaticPageGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutoMaticPageGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoMaticPageGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }


    //初始化控件
    private void init(Context context, AttributeSet attrs) {
        //加载自定义属性
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AutoMaticPageGridView);
        lines = array.getInteger(R.styleable.AutoMaticPageGridView_auto_lines, 2);//行数
        column = array.getInteger(R.styleable.AutoMaticPageGridView_auto_column, 5);//列数
        btn_res = array.getResourceId(R.styleable.AutoMaticPageGridView_auto_button, 0);//btn的样式
        btn_isvisible = array.getBoolean(R.styleable.AutoMaticPageGridView_auto_button_visible, true);//默认true
        array.recycle();
        //分页用
        viewPager = new ViewPager(context);
        viewPager.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        addView(viewPager);
        //导航点用
        radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        radioGroup.setLayoutParams(params);
        addView(radioGroup);
        //如果不显示的话就隐藏
        if (!btn_isvisible) {
            radioGroup.setVisibility(GONE);
        }
    }

    //设置适配器
    public void setAdapter(BaseAutoAdapter baseAdapter) {
        this.adapter = baseAdapter;
        //计算页数
        if ((adapter.getCounts() / (column * lines)) > 0) {
            pages = (adapter.getCounts() / (column * lines)) + 1; //多一页
        } else {
            pages = adapter.getCounts();
        }
        //添加radioButton
        addRadioButton(pages);
        this.post(new Runnable() {
            @Override
            public void run() {
                itemHeight = getMeasuredHeight() / lines;
                //显示viewpager
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getContext());
                viewPager.setAdapter(viewPagerAdapter);
                //设置联动
                initLinkAgeEvent();
            }
        });
    }

    //添加RadioButton
    private void addRadioButton(int pages) {
        for (int i = 0; i < pages; i++) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            radioButton.setPadding(5, 5, 5, 5);//间距
            radioButton.setId(i);//设置Id,方便联动
            radioButton.setClickable(false);
            if (btn_res != 0) {  //设置按钮样式
                radioButton.setButtonDrawable(btn_res);
            }
            radioGroup.addView(radioButton);
        }
    }

    //给当前页计算数据数量
    private List<View> getAdapterData(int position) {
        List<View> cerrent = new ArrayList<>();
        if (position == pages - 1) { //如果等于最后一页
            for (int i = position * (lines * column); i < adapter.getCounts(); i++) {
                cerrent.add(adapter.getItemView(i, null));
            }
        } else {
            for (int i = position * (lines * column); i < position * (lines * column) + (lines * column); i++) {
                cerrent.add(adapter.getItemView(i, null));
            }
        }
        return cerrent;
    }

    //ViewPager适配器
    private class ViewPagerAdapter extends PagerAdapter {

        private Context context;

        public ViewPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return pages;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            CustomGridView gridView = new CustomGridView(context);
            gridView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.MATCH_PARENT));
            gridView.setNumColumns(column);//设置列数
            gridView.setColumnWidth(GridView.AUTO_FIT);
            GridViewAdapter adapter = new GridViewAdapter(getAdapterData(position), position);
            gridView.setAdapter(adapter);
            container.addView(gridView);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GridViewAdapter adapter1 = (GridViewAdapter) parent.getAdapter();
                    onItemClickCallBack.OnItemClicked(adapter1.currentPage * (lines * column) + position, view);
                }
            });
            return gridView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((GridView) object);
        }
    }

    //GridView的适配器
    private class GridViewAdapter extends BaseAdapter {

        private List<View> views;//数据量
        public int currentPage; //当前页

        public GridViewAdapter(List<View> counts, int currentPage) {
            this.views = counts;
            this.currentPage = currentPage;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = views.get(position);
                convertView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
            }
            return convertView;
        }

    }

    //自定义GridView,禁止滑动
    private class CustomGridView extends GridView {

        public CustomGridView(Context context) {
            super(context);
        }

        public CustomGridView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomGridView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public CustomGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                return true;//禁止GridView进行滑动
            }
            return super.dispatchTouchEvent(ev);
        }
    }

    //初始化联动联动事件
    private void initLinkAgeEvent() {
        //默认选中第一个
        viewPager.setCurrentItem(0);
        radioGroup.check(radioGroup.getChildAt(0).getId());
        //滑动换页事件
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                radioGroup.check(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
