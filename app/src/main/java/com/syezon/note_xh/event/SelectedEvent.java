package com.syezon.note_xh.event;

/**
 * Created by June on 2018/3/7.
 */

public class SelectedEvent {
    public static final int SELECTED = 1;
    public static final int DELETE = 2;
    private int type;
    private int deletePosition;

    public SelectedEvent(int type) {
        this.type = type;
    }

    public SelectedEvent(int type, int deletePosition) {
        this.type = type;
        this.deletePosition = deletePosition;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDeletePosition() {
        return deletePosition;
    }

    public void setDeletePosition(int deletePosition) {
        this.deletePosition = deletePosition;
    }
}
