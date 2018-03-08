package com.syezon.note_xh.fragment;

import android.animation.ValueAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.syezon.note_xh.Config.AdConfig;
import com.syezon.note_xh.Config.AppSwitch;
import com.syezon.note_xh.Config.Conts;
import com.syezon.note_xh.R;
import com.syezon.note_xh.adapter.NewsAdapter;
import com.syezon.note_xh.bean.BaseNewInfo;
import com.syezon.note_xh.utils.DisplayUtils;
import com.syezon.note_xh.utils.ParseUtil;
import com.syezon.note_xh.view.refreshview.RefreshRecyclerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment implements View.OnClickListener{

    public static final int MES_GET_NEWS_OK = 1;

    public static final int MES_GET_NEWS_FAIL = 2;

    private static final int INIT = 10;
    private static final int REFRESH = 11;
    private static final int LOAD_MORE = 12;

    public static final String NEWS_SOURCE = "source";

    private int pageNum = 1;

    private String mId = "";
    private ViewSwitcher mViewSwitcherNewsLoading;
    private ViewSwitcher mViewSwitcherNewsError;
    private OnFragmentInteractionListener mListener;
    private RefreshRecyclerView mRefreshRecyclerView;
    private TextView mTvReLoad;
    private List<BaseNewInfo> mNewsInfo = new ArrayList<>();
    private NewsAdapter mNewsAdapter;
    private ImageView img_loading_rotation;
    private TextView tv_refresh_tip;

    private boolean isFirstVisibleToUser = true;

    private boolean mIsLoadData;
    private String newsSource;


    private Handler mDataHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MES_GET_NEWS_FAIL:
                    mRefreshRecyclerView.reset();
                    displayNewsErrorView();
                    break;
                default:
                    break;
            }
        }
    };

    private void showGetNewsView(int num) {
        if (mNewsInfo != null && mNewsInfo.size() > 0) {
            mIsLoadData = true;
            displayNewsDataOkViw();
            mNewsAdapter = new NewsAdapter(getContext(), mNewsInfo);
            mRefreshRecyclerView.setAdapter(mNewsAdapter);
            mRefreshRecyclerView.reset();
            //tip动画
            tv_refresh_tip.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = tv_refresh_tip.getLayoutParams();
            layoutParams.height = (int)(DisplayUtils.getScreenDensity(getContext()) * 40 +0.5F);
            tv_refresh_tip.setLayoutParams(layoutParams);
            tv_refresh_tip.setText(String.format("已为您更新%d条数据", num));
            tv_refresh_tip.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getContext() != null && tv_refresh_tip != null) {
                        int height = DisplayUtils.dip2px(getContext().getApplicationContext(), 40);
                        ValueAnimator valueAnimator = ValueAnimator.ofInt(height, 0);
                        valueAnimator.setDuration(500);
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                ViewGroup.LayoutParams layoutParams = tv_refresh_tip.getLayoutParams();
                                layoutParams.height = (int) animation.getAnimatedValue();
                                tv_refresh_tip.setLayoutParams(layoutParams);
                                if (animation.getAnimatedFraction() >= 1.0) {
                                    tv_refresh_tip.setVisibility(View.GONE);
                                }
                            }
                        });
                        valueAnimator.start();
                    }
                }
            }, 1500);
        } else {
            mDataHandler.sendEmptyMessage(MES_GET_NEWS_FAIL);
        }

    }


    public NewsFragment() {

    }


    public static NewsFragment newInstance(String source) {
        NewsFragment fragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(NEWS_SOURCE, source);
        fragment.setArguments(bundle);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        newsSource = bundle.getString(NEWS_SOURCE) != null ? bundle.getString(NEWS_SOURCE): AdConfig.TYPE_NEWS_SOURCE_TT.getName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_news, container, false);
        initViews(contentView);
        loadData();
        ViewGroup parent = (ViewGroup) contentView.getParent();
        if (parent != null) {
            parent.removeView(contentView);
        }
        return contentView;
    }

    private void initViews(View contentView) {

        mViewSwitcherNewsError = (ViewSwitcher) contentView.findViewById(R.id.viewSwitcherSurfingHotNewsError);
        mViewSwitcherNewsLoading = (ViewSwitcher) contentView.findViewById(R.id.viewSwitcherSurfingJokeLoading);
        mRefreshRecyclerView = (RefreshRecyclerView) contentView.findViewById(R.id.refreshRecyclerViewNews);
        mRefreshRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRefreshRecyclerView.setLoadingListener(new RefreshLoadListener());
        mTvReLoad = (TextView) contentView.findViewById(R.id.tvReload);
        mTvReLoad.setOnClickListener(this);
        img_loading_rotation = (ImageView) contentView.findViewById(R.id.img_rotation);

        tv_refresh_tip = (TextView) contentView.findViewById(R.id.tv_refresh_tip);
        tv_refresh_tip.setVisibility(View.GONE);
    }


    /**
     * 展示新闻数据加载错误页面
     */
    private void displayNewsErrorView() {
        mViewSwitcherNewsError.setDisplayedChild(1);
    }

    /**
     * 展示新闻数据加载页面
     */
    private void displayNewsLoadingView() {
        mViewSwitcherNewsError.setDisplayedChild(0);
        mViewSwitcherNewsLoading.setDisplayedChild(1);
        img_loading_rotation.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.anim_rotate));
    }

    /**
     * 展示新闻数据加载正常页面
     */
    private void displayNewsDataOkViw() {
        mViewSwitcherNewsError.setDisplayedChild(0);
        mViewSwitcherNewsLoading.setDisplayedChild(0);
        img_loading_rotation.clearAnimation();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * 刷新新闻数据
     */
    private void refreshNews(final int type, final boolean isFirstTime) {
        if(newsSource.equals(AdConfig.TYPE_NEWS_SOURCE_BL.getName())){
            String url = AppSwitch.showAdInNotes ? Conts.URL_BL_NEWS : Conts.URL_BL_NEWS_NORMAL;
            x.http().get(new RequestParams(url + pageNum++), new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    List<BaseNewInfo> news = new ArrayList<>();
                    try {
                        news = ParseUtil.parseBLNews(result);
                    } catch (Exception e) {
                        if(isFirstTime) mDataHandler.sendEmptyMessage(MES_GET_NEWS_FAIL);
                    }
                    switch(type){
                        case REFRESH:
                            for (int i = news.size() -1 ; i >= 0; i--) {
                                mNewsInfo.add(0, news.get(i));
                            }
                            showGetNewsView(news.size());
                            break;
                        default:
                            mNewsInfo.addAll(news);
                            showGetNewsView(news.size());
                            break;
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    displayNewsErrorView();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }else if(newsSource.equals(AdConfig.TYPE_NEWS_SOURCE_TT.getName())){
            String url = "";
            if(type == INIT){
                url = Conts.URL_TT_NEWS;
            }else{
                url = Conts.URL_TT_NEWS_DYNAMIC;
            }
            x.http().get(new RequestParams(url), new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    List<BaseNewInfo> news = new ArrayList<>();
                    try {
                        news = ParseUtil.parseTTNews(result, AppSwitch.showAdInNotes);
                    } catch (Exception e) {
                        if(isFirstTime) mDataHandler.sendEmptyMessage(MES_GET_NEWS_FAIL);
                    }
                    switch(type){
                        case REFRESH:
                            for (int i = news.size() -1 ; i >= 0; i--) {
                                mNewsInfo.add(0, news.get(i));
                            }
                            showGetNewsView(news.size());
                            break;
                        default:
                            mNewsInfo.addAll(news);
                            showGetNewsView(news.size());
                            break;
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    displayNewsErrorView();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }

    }

    private void loadData() {
        displayNewsLoadingView();
        refreshNews(INIT, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvReload:
                loadData();
                break;
            default:
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class RefreshLoadListener implements RefreshRecyclerView.LoadingListener {

        @Override
        public void onRefresh() {
            refreshNews(REFRESH, false);
        }

        @Override
        public void onLoadMore() {
            refreshNews(LOAD_MORE, false);
        }
    }
}
