package net.suntec.pset.pictureview;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FolderListActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private final static int SCAN_OK = 1;
    private GridView mGroupGridView;
    private int mTotalPages;
    private int mCurrentPage = 1;
    private TextView mTextView;
    //private AutoMaticPageGridView mGroupGridView;
//    private MyAutoMaticPageAdapter adapter;
    private GroupAdapter adapter;
//    private List<Integer> myData;
    private HashMap<String, List<String>> mGruopMap = new HashMap<>();
    private List<ImageBean> list = new ArrayList<>();
    private Button mReturnBtn;
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case SCAN_OK:
//                    //关闭进度条
//                    //mProgressDialog.dismiss();
//
//                    adapter = new GroupAdapter(FolderListActivity.this, list = subGroupOfImage(mGruopMap), mGroupGridView);
//                    mGroupGridView.setAdapter(adapter);
//                    mTotalPages = list.size() / 4;
//                    mTextView.setText(mCurrentPage + "/" + mTotalPages);
//                    break;
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);
        mGroupGridView = (GridView) findViewById(R.id.main_grid);
        mTextView = (TextView) findViewById(R.id.pageView);
        mReturnBtn = (Button) findViewById(R.id.btn_return);
        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FolderListActivity.this,
                        DeviceChooseActivity.class);
                startActivity(intent);
            }
        });
//        getImages();
        Bundle bundle = getIntent().getBundleExtra("data");
        Serializable hashMap = bundle.getSerializable("HashMap");
        if(hashMap!=null) {
            mGruopMap = (HashMap<String, List<String>>)hashMap;
            adapter = new GroupAdapter(FolderListActivity.this, list = subGroupOfImage(mGruopMap), mGroupGridView);
            mGroupGridView.setAdapter(adapter);
        }
        else {
            Log.e("FolderListActivity", "cast serializable to haspmap fail!");
            finish();
        }


        //Click
        mGroupGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                      int position, long id) {
                List<String> childList = mGruopMap.get(list.get(position).getFolderName());

                Intent mIntent = new Intent(FolderListActivity.this, PictureListActivity.class);
                mIntent.putStringArrayListExtra("data", (ArrayList<String>)childList);
                startActivity(mIntent);
            }
        });
    }

    /**
     * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中
     * 所以需要遍历HashMap将数据组装成List
     *
     * @param mGruopMap , nothing
     * @return          , nothing
     */
    private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap){
        if(mGruopMap.size() == 0){
            return null;
        }
        List<ImageBean> list = new ArrayList<>();

        Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();

        for (Map.Entry<String, List<String>> entry : mGruopMap.entrySet()) {
            ImageBean mImageBean = new ImageBean();
            List<String> value = entry.getValue();
            String key = entry.getKey();
            mImageBean.setFolderName(key);
            mImageBean.setImageCounts(value.size());
            mImageBean.setTopImagePath(value.get(0));//获取该组的第一张图片
            list.add(mImageBean);
        }
        return list;
    }
}
