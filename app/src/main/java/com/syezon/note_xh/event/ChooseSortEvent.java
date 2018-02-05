package com.syezon.note_xh.event;

/**
 * 选择分类事件
 */
public class ChooseSortEvent {
    private String name;

    public ChooseSortEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
