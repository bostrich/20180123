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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.syezon.note_xh.R;
import com.syezon.note_xh.bean.BaseNoteBean;
import com.syezon.note_xh.event.SelectedEvent;
import com.syezon.note_xh.utils.DateUtils;
import com.syezon.note_xh.utils.StringUtils;
import com.syezon.note_xh.view.RoundCornerTransform;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class TimeNewAdapter extends RecyclerView.Adapter<TimeNewAdapter.MyViewHolder> implements View.OnClickListener, View.OnLongClickListener {
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

    public TimeNewAdapter(Context context, List<BaseNoteBean> list) {
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
            itemView=LayoutInflater.from(mContext).inflate(R.layout.item_new_note_image, null);
//            itemView.setOnClickListener(this);
//            itemView.setOnLongClickListener(this);
            return new MyViewHolder(itemView,Type_HASIMA);
        }else {
            itemView=LayoutInflater.from(mContext).inflate(R.layout.item_new_note_noimage, null);
//            itemView.setOnClickListener(this);
//            itemView.setOnLongClickListener(this);
            return new MyViewHolder(itemView,Type_NOIMA);
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        BaseNoteBean bean = mList.get(position);
        holder.tvWeather.setText(StringUtils.strToIconStr(mContext,bean.getWeather()));
        holder.tvMonthAndDay.setText(DateUtils.getTimeByTimeStamp( bean.getTime(),"M-dd"));
        holder.tvDayInWeek.setText(DateUtils.getTimeByTimeStamp( bean.getTime(),"EEEE"));
        holder.tvYear.setText(DateUtils.getTimeByTimeStamp( bean.getTime(),"yyyy"));
        if(bean.isCompleted()){
            holder.imgCompleted.setVisibility(View.VISIBLE);
        }else {
            holder.imgCompleted.setVisibility(View.GONE);
        }
        if(bean.isCollected()){
            holder.imgCollected.setVisibility(View.VISIBLE);
        }else{
            holder.imgCollected.setVisibility(View.INVISIBLE);
        }
        if(bean.hasImage()){
            String url=bean.getPicUrl();
            if (!TextUtils.isEmpty(url)) {
                Uri uri=Uri.parse(url);
                Glide.with(mContext).load(uri).crossFade().into(holder.imgContent);
            }
        }else{
            holder.tvTitle.setText(bean.getTitle());
            holder.tvContent.setText(bean.getDesc());
        }

        holder.llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SelectedEvent(SelectedEvent.DELETE, position));
            }
        });
        if(editState==EDITED){
            holder.cbCover.setChecked(false);
            holder.cbCover.setVisibility(View.VISIBLE);
            holder.imgSelected.setVisibility(View.GONE);
            holder.cbCover.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        holder.imgSelected.setVisibility(View.VISIBLE);
                        holder.imgSelected.setImageResource(R.mipmap.img_note_selected);
                        selectedPositionList.add(holder.getAdapterPosition());
                    }else {
                        holder.imgSelected.setVisibility(View.GONE);
                        Iterator<Integer> sListIterator = selectedPositionList.iterator();
                        while(sListIterator.hasNext()){
                            int e = sListIterator.next();
                            if(e==holder.getAdapterPosition()){
                                sListIterator.remove();
                            }
                        }
                    }
                    EventBus.getDefault().post(new SelectedEvent(SelectedEvent.SELECTED));
                }
            });
        }else{
            holder.cbCover.setChecked(false);
            holder.cbCover.setVisibility(View.GONE);
            holder.imgSelected.setVisibility(View.GONE);
        }
        holder.flMain.setOnClickListener(this);
        holder.flMain.setOnLongClickListener(this);
        holder.flMain.setTag(position);
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
        TextView tvWeather;
        TextView tvMonthAndDay;
        TextView tvDayInWeek;
        TextView tvYear;
        ImageView imgCollected;
        ImageView imgSelected;
        ImageView imgCompleted;
        CheckBox cbCover;
        ImageView imgContent;
        TextView tvTitle;
        TextView tvContent;
        FrameLayout flMain;
        LinearLayout llDelete;

        public MyViewHolder (View itemView,int types) {
            super(itemView);
            tvWeather = (TextView) itemView.findViewById(R.id.tv_weather);
            tvMonthAndDay = (TextView) itemView.findViewById(R.id.tv_monthAndDay);
            tvDayInWeek = (TextView) itemView.findViewById(R.id.tv_dayInWeek);
            tvYear = (TextView) itemView.findViewById(R.id.tv_year);
            imgCollected = (ImageView) itemView.findViewById(R.id.img_collect);
            imgSelected = (ImageView) itemView.findViewById(R.id.img_select);
            imgCompleted = (ImageView) itemView.findViewById(R.id.img_complete);
            cbCover = (CheckBox) itemView.findViewById(R.id.cb_edit);
            flMain = (FrameLayout) itemView.findViewById(R.id.fl_main);
            llDelete = (LinearLayout) itemView.findViewById(R.id.ll_delete);
            if(types == Type_HASIMA){
                imgContent = (ImageView) itemView.findViewById(R.id.img_content);
            }else if(types == Type_NOIMA){
                tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
                tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            }
            Typeface iconfont = Typeface.createFromAsset(mContext.getAssets(), "iconfont.ttf");
            tvWeather.setTypeface(iconfont);
        }

    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
}
