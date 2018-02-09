package com.syezon.note_xh.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import com.syezon.note_xh.R;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 *  Created by admin on 2016/7/13.
 */
public class StringUtils {
    /**
     * 字符转ASC
     *
     */
    public static int getAsc(String st) {
        byte[] gc = st.getBytes();
        int ascNum = (int) gc[0];
        return ascNum;
    }

    /**
     * ASC转字符
     *
     */
    public static char backchar(int backnum) {
        char strChar = (char) backnum;
        return strChar;
    }

    /**
     * 汉字转换位汉语拼音首字母小写，英文字符不变
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToFirstSpell(String chines){
        String pinyinName = "";
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }else{
                pinyinName += nameChar[i];
            }
        }
        return pinyinName;
    }

    /**
     * 标识字符串转为天气图标的iconstring字符串
     */
    public static String strToIconStr(Context context, String str) {
        switch (str){
            case "tianqi":
                return context.getResources().getString(R.string.tianqi);
            case "leidian":
                return context.getResources().getString(R.string.leidian);
            case "xiaoyu":
                return context.getResources().getString(R.string.xiaoyu);
            case "xue":
                return context.getResources().getString(R.string.xue);
            case "duoyun":
                return context.getResources().getString(R.string.duoyun);
            case "qingtian":
                return context.getResources().getString(R.string.qingtian);
            case "note_new":
                return context.getResources().getString(R.string.note_new);
            default:
                return null;
        }
    }


    /**
     * 天气图标的iconstring字符串转为标识字符串
     */
    public static String iconStrToStr(Context context, String str) {
        if(TextUtils.equals(str,context.getResources().getString(R.string.tianqi))){
            return "tianqi";
        }else if(TextUtils.equals(str,context.getResources().getString(R.string.leidian))){
            return "leidian";
        }else if(TextUtils.equals(str,context.getResources().getString(R.string.xiaoyu))){
            return "xiaoyu";
        }else if(TextUtils.equals(str,context.getResources().getString(R.string.xue))){
            return "xue";
        }else if(TextUtils.equals(str,context.getResources().getString(R.string.duoyun))){
            return "duoyun";
        }else if(TextUtils.equals(str,context.getResources().getString(R.string.qingtian))){
            return "qingtian";
        }
        return null;
    }

    /**
     * 复制文本
     */
    public static void copyStr(Context context, String content) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
//        cm.getText();//获取粘贴信息
    }

}
