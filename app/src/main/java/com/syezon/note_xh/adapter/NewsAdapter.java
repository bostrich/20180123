package com.syezon.note_xh.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.syezon.note_xh.R;
import com.syezon.note_xh.bean.BaseNewInfo;
import com.syezon.note_xh.utils.DisplayUtils;
import com.syezon.note_xh.utils.WebHelper;


import java.util.List;

/**
 * Created by Administrator on 2016/7/26.
 */
public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int NEWS_TYPE_PIC_TEXT = 1;
    public static final int NEWS_TYPE_BIG_PIC = 2;
    public static final int NEWS_SHOW_THREE_PIC = 3;

    private Context mContext;
    private List<BaseNewInfo> mNewsInfos;


    private int screenWidth;

    public NewsAdapter(Context context, List<BaseNewInfo> list) {
        this.mContext = context;
        this.mNewsInfos = list;
        screenWidth = DisplayUtils.getScreenWidth(mContext);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case NEWS_TYPE_BIG_PIC:
                return new BigPicItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_news_bigpic, parent, false));
            case NEWS_TYPE_PIC_TEXT:
                return new PicTextItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false));
            case NEWS_SHOW_THREE_PIC:
                return new SmallPicItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_hot_news, parent, false));
            default:
                break;
        }
        return new DefaultItemViewHolder(new View(mContext));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int po = position;
        if (holder != null) {
            final BaseNewInfo newsInfo = mNewsInfos.get(position);
            if (holder instanceof BigPicItemViewHolder) {
                ((BigPicItemViewHolder) holder).tvNewsContent.setText(newsInfo.getTitle());
                if (newsInfo.getImages() != null && newsInfo.getImages().size() > 0 ) {
                    String imgUrl = newsInfo.getImages().get(0);
                    Glide.with(mContext).load(imgUrl).into(((BigPicItemViewHolder) holder).imgBigPic);
                }
                ((BigPicItemViewHolder) holder).contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToWeb(mContext, newsInfo, po);
                    }
                });
            } else if (holder instanceof SmallPicItemViewHolder) {
                ((SmallPicItemViewHolder) holder).tvNewsContent.setText(newsInfo.getTitle());
                if (newsInfo.getImages() != null && newsInfo.getImages().size() >= 3) {
                    Glide.with(mContext).load(newsInfo.getImages().get(0)).into(((SmallPicItemViewHolder) holder).imgLeft);
                    Glide.with(mContext).load(newsInfo.getImages().get(1)).into(((SmallPicItemViewHolder) holder).imgCenter);
                    Glide.with(mContext).load(newsInfo.getImages().get(2)).into(((SmallPicItemViewHolder) holder).imgRight);
                }

                if(newsInfo.getSource().contains("众联广告")){
                    ((SmallPicItemViewHolder) holder).tvNewsTuiGuang.setVisibility(View.VISIBLE);
                    ((SmallPicItemViewHolder) holder).tvAuthor.setVisibility(View.GONE);
                }else{
                    ((SmallPicItemViewHolder) holder).tvNewsTuiGuang.setVisibility(View.GONE);
                    ((SmallPicItemViewHolder) holder).tvAuthor.setVisibility(View.VISIBLE);
                }
                ((SmallPicItemViewHolder) holder).tvAuthor.setText(newsInfo.getSource());
                ((SmallPicItemViewHolder) holder).tvDate.setVisibility(View.VISIBLE);
                ((SmallPicItemViewHolder) holder).tvDate.setText(newsInfo.getDate());
                ((SmallPicItemViewHolder) holder).contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToWeb(mContext, newsInfo,po);
                    }
                });
            } else if (holder instanceof SimpleTextItemViewHolder) {
                ((SimpleTextItemViewHolder) holder).tvNewsContent.setText(newsInfo.getTitle());
                ((SimpleTextItemViewHolder) holder).contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToWeb(mContext, newsInfo,po);
                    }
                });
            } else if (holder instanceof PicTextItemViewHolder) {
                ((PicTextItemViewHolder) holder).tvNewsContent.setText(newsInfo.getTitle());
                if (newsInfo.getImages() != null && newsInfo.getImages().size() > 0 ) {
                    String imgUrl = newsInfo.getImages().get(0);
                    Glide.with(mContext).load(imgUrl).into(((PicTextItemViewHolder) holder).imgPic);
                }

                if(newsInfo.getSource().contains("众联广告")){
                    ((PicTextItemViewHolder) holder).tvTuiGuang.setVisibility(View.VISIBLE);
                    ((PicTextItemViewHolder) holder).tvAuthor.setVisibility(View.GONE);
                }else{
                    ((PicTextItemViewHolder) holder).tvTuiGuang.setVisibility(View.GONE);
                    ((PicTextItemViewHolder) holder).tvAuthor.setText(newsInfo.getSource());
                    ((PicTextItemViewHolder) holder).tvAuthor.setVisibility(View.VISIBLE);
                }
                ((PicTextItemViewHolder) holder).tvDate.setText(newsInfo.getDate());
                ((PicTextItemViewHolder) holder).tvDate.setVisibility(View.VISIBLE);
                ((PicTextItemViewHolder) holder).contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToWeb(mContext, newsInfo,po);
                    }
                });

            }

        }
    }

    private void goToWeb(Context context, BaseNewInfo info, int position){
        WebHelper.showAdDetail(context, info.getTitle(), info.getUrl(), new WebHelper.SimpleWebLoadCallBack(){
            @Override
            public void loadComplete(String url) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mNewsInfos == null ? 0 : mNewsInfos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mNewsInfos.get(position).getShowType();
    }



    class BigPicItemViewHolder extends RecyclerView.ViewHolder {
        View contentView;
        TextView tvNewsContent;
        ImageView imgBigPic;
        TextView tvNewsTuiGuang;
        TextView tvAuthor;

        public BigPicItemViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            tvNewsContent = (TextView) itemView.findViewById(R.id.item_news_content);
            imgBigPic = (ImageView) itemView.findViewById(R.id.item_news_img);
            DisplayUtils.adapterScreen(imgBigPic, screenWidth, (float) 0.42);
            tvNewsTuiGuang = (TextView) itemView.findViewById(R.id.item_news_tuiguang);
            tvAuthor = (TextView) itemView.findViewById(R.id.item_news_source);
        }
    }

    class SmallPicItemViewHolder extends RecyclerView.ViewHolder {
        View contentView;
        TextView tvNewsContent;
        ImageView imgLeft, imgCenter, imgRight;
        TextView tvNewsTuiGuang;
        TextView tvAuthor;
        TextView tvDate;

        public SmallPicItemViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            int width = (screenWidth - DisplayUtils.dip2px(mContext, 40)) / 3;
            tvNewsContent = (TextView) itemView.findViewById(R.id.item_news_content);
            imgLeft = (ImageView) itemView.findViewById(R.id.news_image_left);
            imgCenter = (ImageView) itemView.findViewById(R.id.news_image_center);
            imgRight = (ImageView) itemView.findViewById(R.id.news_image_right);
            tvNewsTuiGuang = (TextView) itemView.findViewById(R.id.item_news_tuiguang);
            tvAuthor = (TextView) itemView.findViewById(R.id.item_news_source);
            tvDate = (TextView) itemView.findViewById(R.id.item_news_date);
            DisplayUtils.adapterScreen(imgLeft, width, (float) 0.75);
            DisplayUtils.adapterScreen(imgCenter, width, (float) 0.75);
            DisplayUtils.adapterScreen(imgRight, width, (float) 0.75);
        }
    }

    class SimpleTextItemViewHolder extends RecyclerView.ViewHolder {
        View contentView;
        TextView tvNewsContent;

        public SimpleTextItemViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            tvNewsContent = (TextView) itemView.findViewById(R.id.item_news_content);
        }
    }

    class PicTextItemViewHolder extends RecyclerView.ViewHolder {
        View contentView;
        TextView tvNewsContent;
        ImageView imgPic;
        TextView tvTuiGuang;
        TextView tvAuthor;
        TextView tvDate;

        public PicTextItemViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            tvNewsContent = (TextView) itemView.findViewById(R.id.item_news_content);
            imgPic = (ImageView) itemView.findViewById(R.id.item_news_img);
            tvTuiGuang = (TextView) itemView.findViewById(R.id.item_news_tuiguang);
            tvAuthor = (TextView) itemView.findViewById(R.id.item_news_source);
            tvDate = (TextView) itemView.findViewById(R.id.item_news_date);
            int width = (screenWidth - DisplayUtils.dip2px(mContext, 40)) / 3;
            DisplayUtils.adapterScreen(imgPic, width, (float) 0.75);
        }
    }

    class DefaultItemViewHolder extends RecyclerView.ViewHolder {
        public DefaultItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
