package com.syezon.note_xh.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.syezon.note_xh.R;

/**
 * 密码图标
 */
public class PasswordImageView extends ImageView{
    private String content = "";//实际内容
//    文本改变事件回调接口
    private OnTextChangedListener onTextChangedListener;

    /**
     * 构造方法
     * @param context
     * @param attrs
     */
    public PasswordImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 设置文本改变事件监听
     * @param onTextChangedListener
     */
    public void setOnTextChangedListener(OnTextChangedListener onTextChangedListener){
        this.onTextChangedListener = onTextChangedListener;
    }

    /**
     * 设置密码框显示的内容
     * @param text
     */
    public void setTextContent(String text){
        //设置显示的内容
        this.content = text;
        if(TextUtils.isEmpty(text)){
            this.setImageResource(R.mipmap.heng);
        }else {
            this.setImageResource(R.mipmap.yuan);
            if(onTextChangedListener!=null){
                onTextChangedListener.textChanged(content);
            }
        }
    }

    /**
     * 获取显示的内容
     * @return
     */
    public String getTextContent(){
        return content;
    }


    /**
     * 文本改变事件接口
     * @ClassName: OnTextChangedListener
     * @author haoran.shu
     * @date 2014年6月12日 上午11:37:17
     * @version 1.0
     *
     */
    public interface OnTextChangedListener{
        /**
         * 密码框文本改变时调用
         * @param content
         */
        void textChanged(String content);
    }

}
