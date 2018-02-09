package com.syezon.note_xh.utils;

import com.syezon.note_xh.adapter.NewsAdapter;
import com.syezon.note_xh.bean.NewsBLBean;
import com.syezon.note_xh.bean.BaseNewInfo;
import com.syezon.note_xh.bean.NewsTTBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class ParseUtil {

    public static List<BaseNewInfo> parseBLNews(String result) throws JSONException {
        List<BaseNewInfo> news = new ArrayList<>();
        JSONObject obj = new JSONObject(result);
        JSONArray ary = obj.optJSONArray("data");
        for (int i = 0; i < ary.length(); i++) {
            JSONObject temp = ary.optJSONObject(i);
            NewsBLBean bean = new NewsBLBean();
            bean.setDate(temp.optString("date"));
            bean.setUrl(temp.optString("url"));
            bean.setDescription(temp.optString("description"));
            bean.setTitle(temp.optString("title"));
            bean.setSource(temp.optString("source"));
            List<String> list = new ArrayList<>();
            JSONArray imgs = temp.optJSONArray("images");
            if(imgs != null && imgs.length() > 0) {
                for (int k = 0; k < imgs.length(); k++) {
                    String imgUrl = imgs.optString(k);
                    list.add(imgUrl);
                }
            }
            bean.setImages(list);
            if(bean.getImages().size() >= 3){
                bean.setShowType(NewsAdapter.NEWS_SHOW_THREE_PIC);
            }else{
                bean.setShowType(NewsAdapter.NEWS_TYPE_PIC_TEXT);
            }
            news.add(bean);
        }
        return news;
    }

    public static List<BaseNewInfo> parseTTNews(String result, boolean isShowAdd) throws JSONException {
        List<BaseNewInfo> news = new ArrayList<>();
        JSONObject json = new JSONObject(result).optJSONObject("data");
        if (json != null) {
            JSONArray newsArray = json.optJSONArray("recommend");
            if (newsArray != null && newsArray.length() > 0) {
                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject newsObject = newsArray.optJSONObject(i);
                    NewsTTBean newsInfo = new NewsTTBean();
                    newsInfo.setSource(newsObject.optString("author"));
                    newsInfo.setTitle(newsObject.optString("title"));
                    newsInfo.setShowType(newsObject.optInt("infoType"));
                    newsInfo.setUrl(newsObject.optString("url"));
                    newsInfo.setDate(newsObject.optString("date"));
                    newsInfo.setSourceType(newsObject.optInt("type"));
                    List<String> list = new ArrayList<>();
                    JSONArray imgs = newsObject.optJSONArray("imgs");
                    if ((newsInfo.getShowType() == 3 || newsInfo.getShowType() == 2) && imgs != null && imgs.length() > 0) {
                        for (int k = 0; k < imgs.length(); k++) {
                            JSONObject imgObject = imgs.optJSONObject(k);
                            String img1 = imgObject.optString("url");
                            list.add(img1);
                        }
                    }
                    if (list.size() <= 0) {
                        list.add(newsObject.optString("picture"));
                    }
                    newsInfo.setImages(list);
                    if(!isShowAdd){
                        if (newsInfo.getSourceType() == 0) {
                            continue;
                        }
                    }
                    if (newsInfo.isDataValid()) {//过滤掉不完整数
                        news.add(newsInfo);
                    }
                }
            }
        }
        return news;
    }
}
