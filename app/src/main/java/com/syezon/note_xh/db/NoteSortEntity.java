package com.syezon.note_xh.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 分类名数据库表
 */
@Table(name = "notesort")
public class NoteSortEntity implements Comparable<NoteSortEntity> {
    @Column(name = "id",isId = true)
    private int id;
    @Column(name = "sortname")
    private String sortName;//类名
    @Column(name = "firstletterasc")
    private int firstLetterAsc;//拼音首字母的Asc码值，用来排序用

    public int getFirstLetterAsc() {
        return firstLetterAsc;
    }

    public void setFirstLetterAsc(int firstLetterAsc) {
        this.firstLetterAsc = firstLetterAsc;
    }

    public NoteSortEntity(String sortname) {
        this.sortName = sortname;
    }

    public NoteSortEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    @Override
    public int compareTo(NoteSortEntity another) {
        return (this.getFirstLetterAsc()-another.getFirstLetterAsc());
    }
}
