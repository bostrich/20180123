package com.syezon.note_xh.db;

import java.io.Serializable;

/**
 * 老便签的数据
 */
public class Note implements Serializable {
	private int id;// id
	private String date;// 日期
	private String week;// 星期
	private String time;// 时间
	private String content;// 内容

	public Note() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}


	@Override
	public String toString() {
		return "Note [id=" + id + ", date=" + date + ", week=" + week
				+ ", time=" + time + ", content=" + content + "]";
	}
}
