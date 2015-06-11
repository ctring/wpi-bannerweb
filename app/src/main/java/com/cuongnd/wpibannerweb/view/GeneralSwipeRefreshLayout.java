/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cuongnd.wpibannerweb.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * Sub-class of {@link SwipeRefreshLayout} for use in this
 * {@link android.support.v4.app.ListFragment}. The reason that this is needed is because
 * {@link SwipeRefreshLayout} only supports a single child, which it
 * expects to be the one which triggers refreshes. In our case the layout's child is the content
 * view returned from
 * {@link android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}
 * which is a {@link android.view.ViewGroup}. Also canScrollVertically is not supported in pre-ICS API so it has
 * to be implemented manually.
 *
 * TODO: Implement this class if need to support pre-ICS versions
 */

public abstract class GeneralSwipeRefreshLayout extends SwipeRefreshLayout {

    private static final String TAG = GeneralSwipeRefreshLayout.class.getSimpleName();

    private OnChildScrollUpListener mScrollUpListener;

    public interface OnChildScrollUpListener {
        boolean canChildScrollUp();
    }

    public GeneralSwipeRefreshLayout(Context context) {
        super(context);
    }

    public GeneralSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnChildScrollUpListener(OnChildScrollUpListener onChildScrollUpListener) {
        mScrollUpListener = onChildScrollUpListener;
    }

     /* We need to override this method to properly signal when a
     * 'swipe-to-refresh' is possible.
     *
     * @return true if the {@link ListView} is visible and can scroll up.
     */
    @Override
    public boolean canChildScrollUp() {
        if (mScrollUpListener == null) {
            Log.e(TAG, "OnChildScrollUpListener is not set!");
            return false;
        }
        return mScrollUpListener.canChildScrollUp();
        /*ListView listView = getListView();
        if (listView == null) return false;
        if (listView.getVisibility() == View.VISIBLE) {
            return canListViewScrollUp(listView);
        } else {
            return false;
        }*/
    }

    /**
     * Utility class to check whether a {@link ListView} can scroll up from it's current position.
     * Handles platform version differences, providing backwards compatible functionality where
     * needed.
     */
    public static class ListViewScrollUpListener implements OnChildScrollUpListener {
        ListView mListView;

        public ListViewScrollUpListener(ListView listView) {
            mListView = listView;
        }

        public boolean canChildScrollUp() {
            if (mListView.getVisibility() != View.VISIBLE) {
                return false;
            }
            if (android.os.Build.VERSION.SDK_INT >= 14) {
                // For ICS and above we can call canScrollVertically() to determine this
                return ViewCompat.canScrollVertically(mListView, -1);
            } else {
                // Pre-ICS we need to manually check the first visible item and the child view's top
                // value
                return mListView.getChildCount() > 0 &&
                        (mListView.getFirstVisiblePosition() > 0
                                || mListView.getChildAt(0).getTop() < mListView.getPaddingTop());
            }
        }
    }

    public static class RecyclerViewLinearLayoutScrollUpListener implements OnChildScrollUpListener {
        RecyclerView mRecyclerView;

        public RecyclerViewLinearLayoutScrollUpListener(RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
        }

        @Override
        public boolean canChildScrollUp() {
            if (mRecyclerView.getVisibility() != View.VISIBLE) {
                return false;
            }
            if (android.os.Build.VERSION.SDK_INT >= 14) {
                // For ICS and above we can call canScrollVertically() to determine this
                return ViewCompat.canScrollVertically(mRecyclerView, -1);
            } else {
                // Pre-ICS we need to manually check the first visible item and the child view's top
                // value
                LinearLayoutManager layout =
                        (LinearLayoutManager) mRecyclerView.getLayoutManager();
                return mRecyclerView.getChildCount() > 0 &&
                        (layout.findFirstVisibleItemPosition() > 0
                                || mRecyclerView.getChildAt(0).getTop() < mRecyclerView.getPaddingTop());
            }
        }
    }

    // TODO: do support for scrollview

}