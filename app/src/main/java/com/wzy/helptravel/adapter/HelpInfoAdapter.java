package com.wzy.helptravel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.model.Text;
import com.wzy.helptravel.R;
import com.wzy.helptravel.base.UniversalImageLoader;
import com.wzy.helptravel.bean.HelpInfo;

import java.util.List;

/**
 * Created by wzy on 2016/7/30.
 */
public class HelpInfoAdapter extends RecyclerView.Adapter<HelpInfoAdapter.MyViewHolder> implements View.OnClickListener {


    private List<HelpInfo> infoList;
    private Context mContext;
    private OnRecyclerViewItemClickListener listener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, String id);
    }

    public HelpInfoAdapter(List<HelpInfo> infoList, Context mContext) {
        this.infoList = infoList;
        this.mContext = mContext;
    }

    public void addData(List<HelpInfo> list) {
        infoList.clear();
        infoList.addAll(list);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_info, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        new UniversalImageLoader().load(holder.infoImg, infoList.get(position).getPicture(), R.mipmap.default_camera, null);
        holder.infoTitle.setText(infoList.get(position).getTitle());
        holder.time.setText(infoList.get(position).getUpdatedAt());
        holder.itemView.setTag(infoList.get(position).getObjectId());
    }


    @Override
    public int getItemCount() {
        return infoList.size();
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onItemClick(view, (String) view.getTag());
        }
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView infoImg;
        TextView infoTitle;
        TextView time;


        public MyViewHolder(View itemView) {
            super(itemView);
            infoImg = (ImageView) itemView.findViewById(R.id.info_img);
            infoTitle = (TextView) itemView.findViewById(R.id.info_title);
            time = (TextView) itemView.findViewById(R.id.time);

        }
    }
}
