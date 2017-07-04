package com.andy.zhoukao3;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.maxwin.view.XListView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements XListView.IXListViewListener{
    private XListView mXListView;
    private int page=1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Gson gson=new Gson();
            Data data = gson.fromJson(msg.obj.toString(),Data.class);
            list.addAll(data.getList());
            mAdapter.notifyDataSetChanged();

            mXListView.stopRefresh();
            mXListView.stopLoadMore();
            mXListView.setRefreshTime("刚才");


            mXListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(list.size()>0&&position<list.size()){
                        View v = View.inflate(MainActivity.this,R.layout.item_dialog2,null);
                        ImageView image1 = (ImageView) v.findViewById(R.id.image1);
                        ImageView image2 = (ImageView) v.findViewById(R.id.image2);
                        TextView cancle = (TextView)v.findViewById(R.id.cancle);
                        TextView submit = (TextView)v.findViewById(R.id.submit);

                        AlertDialog
                                .Builder builder=new AlertDialog.Builder(MainActivity.this)
                                .setView(v);
                        final Dialog dialog = builder.create();

                        Data.ListBean bean = list.get(position-1);
                        if(bean.getType()==1){
                            image2.setVisibility(View.GONE);
                            mImageLoader.displayImage(bean.getPic(),image1,mOptions);
                        }else if(bean.getType()==2){
                            String pic = bean.getPic();
                            String [] urlPaths =pic.split("\\|");
                            if(urlPaths.length>=2){
                                mImageLoader.displayImage(urlPaths[0],image1,mOptions);
                                mImageLoader.displayImage(urlPaths[1],image2,mOptions);
                            }
                        }
                        cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                }
            });
        }
    };
    private List<Data.ListBean> list;
    private MyAdapter mAdapter;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        loadData();
    }
    private void initView() {
        mXListView= (XListView) findViewById(R.id.xlistview);
        mXListView.setPullLoadEnable(true);
        mXListView.setXListViewListener(this);
        list=new ArrayList<>();
        mAdapter=new MyAdapter(this,list);

        mXListView.setAdapter(mAdapter);

        mImageLoader=ImageLoader.getInstance();
        File file= new File(Environment.getExternalStorageDirectory(),"Bwei");
        if(!file.exists())
            file.mkdirs();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .diskCache(new UnlimitedDiskCache(file))
                .build();

        mImageLoader.init(configuration);

        mOptions=new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .cacheOnDisk(true)
                .build();

    }
    private void loadData() {
//        OkHttpClient client =new OkHttpClient();
        OkHttpClient client =new  OkHttpClient.Builder()
                .connectTimeout(3000, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url("http://qhb.2dyt.com/Bwei/news?type=1&postkey=1503d&page=1")
                .build();

        Call call=client.newCall(request);
        //同步方法 call.execute();
        //异步方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("onFailure", "onFailure: "+e.getMessage() );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Message msg = Message.obtain();
                msg.what=1;
                msg.obj=result;
                mHandler.sendMessage(msg);
                Log.e("onResponse", "onResponse: "+result);
            }
        });
    }

    @Override
    public void onRefresh() {
        page=1;
        list.clear();
        loadData();

    }

    @Override
    public void onLoadMore() {
        page++;
        loadData();
    }
}
