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
import android.view.View;
import android.widget.ListView;

/**
 * Sub-class of {@link android.support.v4.widget.SwipeRefreshLayout} for use in this
 * {@link android.support.v4.app.ListFragment}. The reason that this is needed is because
 * {@link android.support.v4.widget.SwipeRefreshLayout} only supports a single child, which it
 * expects to be the one which triggers refreshes. In our case the layout's child is the content
 * view returned from
 * {@link android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}
 * which is a {@link android.view.ViewGroup}.
 *
 * <p>To enable 'swipe-to-refresh' support via the {@link android.widget.ListView} we need to
 * override the default behavior and properly signal when a gesture is possible. This is done by
 * overriding {@link #canChildScrollUp()}.
 */

public abstract class ListFragmentSwipeRefreshLayout extends SwipeRefreshLayout {

    public ListFragmentSwipeRefreshLayout(Context context) {
        super(context);
    }

    public abstract ListView getListView();

    /**
     * As mentioned above, we need to override this method to properly signal when a
     * 'swipe-to-refresh' is possible.
     *
     * @return true if the {@link android.widget.ListView} is visible and can scroll up.
     */
    @Override
    public boolean canChildScrollUp() {
        ListView listView = getListView();
        if (listView == null) return false;
        if (listView.getVisibility() == View.VISIBLE) {
            return canListViewScrollUp(listView);
        } else {
            return false;
        }
    }

    /**
     * Utility method to check whether a {@link ListView} can scroll up from it's current position.
     * Handles platform version differences, providing backwards compatible functionality where
     * needed.
     */
    private static boolean canListViewScrollUp(ListView listView) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            // For ICS and above we can call canScrollVertically() to determine this
            return ViewCompat.canScrollVertically(listView, -1);
        } else {
            // Pre-ICS we need to manually check the first visible item and the child view's top
            // value
            return listView.getChildCount() > 0 &&
                    (listView.getFirstVisiblePosition() > 0
                            || listView.getChildAt(0).getTop() < listView.getPaddingTop());
        }
    }
}