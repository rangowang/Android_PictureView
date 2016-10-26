package net.suntec.pset.pictureview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
    private ArrayList<String> list;
    private PictureAdapter adapter;
    private Button mReturnBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_list);
        if(savedInstanceState!=null) {
            Log.d("PictureListActivity", "onCreate: savedInstanceState");
            list = savedInstanceState.getStringArrayList("PictureList");
        }
        else {
            Log.d("PictureListActivity", "onCreate: getIntent");
            list = getIntent().getStringArrayListExtra("data");
        }

        mReturnBtn = (Button) findViewById(R.id.btn_return);
        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mGridView = (GridView) findViewById(R.id.picture_gridView);

        adapter = new PictureAdapter(this, list, mGridView);
        mGridView.setAdapter(adapter);
        //点击事件
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 图片的路径
//                String path = list.get(position);
                // 展示图片
                Intent intent = new Intent(PictureListActivity.this, PictureView.class);
                intent.putStringArrayListExtra("PictureList", list);
                System.out.println("Show the ImageList before put:" + list);

                intent.putExtra("index", position);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("PicturcList", list);
    }

}
