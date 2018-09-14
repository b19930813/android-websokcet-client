package com.example.youngtec.a20171201websocket;

import org.java_websocket.drafts.Draft;

/**
 * Created by Youngtec on 2017/12/26.
 */

public class DraftInfo {
    private final String draftName;
    final Draft draft;

    public DraftInfo(String draftName, Draft draft) {
        this.draftName = draftName;
        this.draft = draft;
    }
}