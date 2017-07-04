package com.andy.zhoukao3;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.List;

/**
 * 类描述：
 * 创建人：yekh
 * 创建时间：2017/6/26 14:56
 */
public class MyAdapter extends BaseAdapter{
    private Context mContext;
    private List<Data.ListBean> list;
    private final int TYPE1 = 0;
    private final int TYPE2 = 1;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;

    public MyAdapter(Context context, List<Data.ListBean> list) {
        mContext = context;
        this.list = list;

        mImageLoader=ImageLoader.getInstance();
        File file= new File(Environment.getExternalStorageDirectory(),"Bwei");
        if(!file.exists())
            file.mkdirs();

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
                .diskCache(new UnlimitedDiskCache(file))
                .build();

        mImageLoader.init(configuration);

        mOptions=new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .cacheOnDisk(true)
                .build();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType()==1?TYPE1:TYPE2;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        int type=getItemViewType(position);

        if(convertView==null){
            holder=new ViewHolder();
            if(type==TYPE1){
                convertView=View.inflate(mContext,R.layout.item1,null);
                holder.id=(TextView)convertView.findViewById(R.id.id);
            }else if(type==TYPE2){
                convertView=View.inflate(mContext,R.layout.item2,null);
                holder.image1=(ImageView)convertView.findViewById(R.id.image1);
            }

            holder.image=(ImageView)convertView.findViewById(R.id.image);
            holder.title=(TextView)convertView.findViewById(R.id.title);

            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        Data.ListBean bean = list.get(position);
        holder.title.setText(bean.getTitle());

        if(type==TYPE1){
            holder.id.setText(bean.getId()+"");
            mImageLoader.displayImage(bean.getPic(),holder.image,mOptions);
        }else if(type==TYPE2){
            String pic = bean.getPic();
            String [] urlPaths =pic.split("\\|");
            if(urlPaths.length>=2){
                mImageLoader.displayImage(urlPaths[0],holder.image,mOptions);
                mImageLoader.displayImage(urlPaths[1],holder.image1,mOptions);
            }
        }

        return convertView;
    }

    class ViewHolder{
        TextView title,id;
        ImageView image,image1;
    }
}
