package net.suntec.pset.pictureview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PictureListActivity extends AppCompatActivity {

//    private AutoMaticPageGridView automatic;
//    private PictureListAdapter adapter;
//    private List<String> myData;

    private GridView mGridView;
    private List<String> list;
    private PictureAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_list);

        mGridView = (GridView) findViewById(R.id.picture_gridView);
        list = getIntent().getStringArrayListExtra("data");

        adapter = new PictureAdapter(this, list, mGridView);
        mGridView.setAdapter(adapter);
        //点击事件
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String path = list.get(position); // 图片的路径
                // 展示图片
                Intent mIntent = new Intent(PictureListActivity.this, PictureView.class);
                mIntent.putExtra("data", path);
                startActivity(mIntent);

            }
        });
    }

}
