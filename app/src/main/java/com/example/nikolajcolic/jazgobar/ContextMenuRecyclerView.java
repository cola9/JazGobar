package com.example.nikolajcolic.jazgobar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;

/**
 * Created by nikolajcolic on 04/04/17.
 */

public class ContextMenuRecyclerView extends RecyclerView {


    public ContextMenuRecyclerView(Context context) {
        super(context);
    }

    public ContextMenuRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContextMenuRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private ContextMenu.ContextMenuInfo mContextMenuInfo = null;

    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return mContextMenuInfo;
    }






    public static class RecyclerContextMenuInfo implements ContextMenu.ContextMenuInfo {

        public RecyclerContextMenuInfo(int position, long id, View targetView) {
            this.position = position;
            this.id = id;
            this.targetView = targetView;
        }

        public int position;
        public long id;
        public View targetView;
    }
}