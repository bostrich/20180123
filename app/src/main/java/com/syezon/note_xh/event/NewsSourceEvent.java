package com.syezon.note_xh.event;

/**
 *
 */
public class NewsSourceEvent {

    private String source;

    public NewsSourceEvent(){}

    public NewsSourceEvent(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


}
