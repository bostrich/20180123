package com.syezon.note_xh.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.syezon.note_xh.R;
import com.syezon.note_xh.bean.BaseNoteBean;
import com.syezon.note_xh.utils.DateUtils;
import com.syezon.note_xh.utils.StringUtils;
import com.syezon.note_xh.view.RoundCornerTransform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class TimeAndAdRecyclerViewAdapter extends RecyclerView.Adapter<TimeAndAdRecyclerViewAdapter.MyViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private Context mContext;
    private List<BaseNoteBean> mList;
    private List<Integer> selectedPositionList;

    private static final int UNEDITED = 1;//非编辑状态
    private static final int EDITED = 2;//编辑状态
    private int editState=UNEDITED;
    private int Type_HASIMA = 1;
    private int Type_NOIMA = 2;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;

    public TimeAndAdRecyclerViewAdapter(Context context, List<BaseNoteBean> list) {
        this.mContext = context;
        this.mList = list;
        this.selectedPositionList=new ArrayList<>();
    }

    public List<Integer> getSelectedPositionList() {
        return selectedPositionList;
    }

    public int getEditState() {
        return editState;
    }

    public void setEditState(int editState) {
        this.editState = editState;
    }


    public void setmOnItemClickListener(OnRecyclerViewItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setmOnItemLongClickListener(OnRecyclerViewItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).hasImage() ? Type_HASIMA:Type_NOIMA;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView=null;
        if(viewType==Type_HASIMA){
            itemView=LayoutInflater.from(mContext).inflate(R.layout.item_showpage_time2, null);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            return new MyViewHolder(itemView,Type_HASIMA);
        }else {
            itemView=LayoutInflater.from(mContext).inflate(R.layout.item_showpage_time, null);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            return new MyViewHolder(itemView,Type_NOIMA);
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        BaseNoteBean bean = mList.get(position);
        holder.gvTimeTv.setText(DateUtils.getTimeByTimeStamp( bean.getTime(),"yyyy-MM-dd"));
        holder.gvWeatherTv.setText(StringUtils.strToIconStr(mContext,bean.getWeather()));
        holder.gvTitleTv.setText(bean.getTitle());
        holder.gvContentTv.setText(bean.getDesc());
        holder.gvCollectTv.setVisibility(bean.isCollected() ? View.VISIBLE:View.GONE);
        if(bean.hasImage()){
            String url=bean.getPicUrl();
            if (!TextUtils.isEmpty(url)) {
                Uri uri=Uri.parse(url);
                Glide.with(mContext).load(uri).crossFade().into(holder.picIv);
            }
        }
        holder.completeIv.setVisibility(bean.isCompleted() ? View.VISIBLE:View.GONE);
        if(editState==EDITED){
            holder.shadowCb.setChecked(false);
            holder.shadowCb.setVisibility(View.VISIBLE);
            holder.checkedIv.setVisibility(View.VISIBLE);
            holder.checkedIv.setImageResource(R.mipmap.item_uncheck);

            holder.shadowCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        holder.checkedIv.setImageResource(R.mipmap.item_check);
                        selectedPositionList.add(holder.getAdapterPosition());
                    }else {
                        holder.checkedIv.setImageResource(R.mipmap.item_uncheck);
                        Iterator<Integer> sListIterator = selectedPositionList.iterator();
                        while(sListIterator.hasNext()){
                            int e = sListIterator.next();
                            if(e==holder.getAdapterPosition()){
                                sListIterator.remove();
                            }
                        }
                    }
                }
            });
        }else {
            holder.shadowCb.setChecked(false);
            holder.shadowCb.setVisibility(View.GONE);
            holder.checkedIv.setVisibility(View.GONE);
        }
        holder.itemView.setTag(position);
        bean.show(mContext);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        mOnItemClickListener.onItemClick(v, position);
    }

    @Override
    public boolean onLongClick(View v) {
        mOnItemLongClickListener.onItemLongClick(v, (Integer) v.getTag());
        return false;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        int type;

        TextView gvTimeTv;
        TextView gvWeatherTv;
        TextView gvTitleTv;
        TextView gvContentTv;
        TextView gvCollectTv;
        CheckBox shadowCb;
        ImageView picIv;
        ImageView checkedIv;
        ImageView completeIv;

        public MyViewHolder (View itemView,int types) {
            super(itemView);
            type=types;

            gvTimeTv= (TextView) itemView.findViewById(R.id.gv_time_tv);
            gvWeatherTv= (TextView) itemView.findViewById(R.id.gv_weather_tv);
            gvTitleTv= (TextView) itemView.findViewById(R.id.gv_title_tv);
            gvContentTv= (TextView) itemView.findViewById(R.id.gv_content_tv);
            gvCollectTv= (TextView) itemView.findViewById(R.id.gv_collect_tv);
            shadowCb= (CheckBox) itemView.findViewById(R.id.time_shadow_cb);
            checkedIv=(ImageView) itemView.findViewById(R.id.iv_selectedItem);
            completeIv =(ImageView) itemView.findViewById(R.id.iv_complete);
            if(type==Type_HASIMA){
                picIv= (ImageView) itemView.findViewById(R.id.pic_iv);
            }

            Typeface iconfont = Typeface.createFromAsset(mContext.getAssets(), "iconfont.ttf");
            gvWeatherTv.setTypeface(iconfont);
            gvCollectTv.setTypeface(iconfont);
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
}
