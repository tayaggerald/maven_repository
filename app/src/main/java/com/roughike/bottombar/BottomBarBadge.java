package com.roughike.bottombar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/*
 * BottomBar library for Android
 * Copyright (c) 2016 Iiro Krankka (http://github.com/roughike).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class BottomBarBadge extends AppCompatTextView {
    private int count;
    private boolean isVisible = false;
    private static int badgeSize = 0;
    private final static double OFFSET = 1.75; //1.25

    BottomBarBadge(Context context) {
        super(context);
    }

    /**
     * Set the unread / new item / whatever count for this Badge.
     *
     * @param count the value this Badge should show.
     */
    void setCount(int count) {
        this.count = count;
        setText(String.valueOf(count));
    }

    /**
     * Get the currently showing count for this Badge.
     *
     * @return current count for the Badge.
     */
    int getCount() {
        return count;
    }

    /**
     * Shows the badge with a neat little scale animation.
     */
    void show() {
        isVisible = true;
        ViewCompat.animate(this)
                .setDuration(150)
                .alpha(1)
                .scaleX(1)
                .scaleY(1)
                .start();
    }

    /**
     * Hides the badge with a neat little scale animation.
     */
    void hide() {
        isVisible = false;
        ViewCompat.animate(this)
                .setDuration(150)
                .alpha(0)
                .scaleX(0)
                .scaleY(0)
                .start();
    }

    /**
     * Is this badge currently visible?
     *
     * @return true is this badge is visible, otherwise false.
     */
    boolean isVisible() {
        return isVisible;
    }

    void attachToTab(BottomBarTab tab, int backgroundColor) {
        isAttached = true;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        setLayoutParams(params);
        setGravity(Gravity.CENTER);
        MiscUtils.setTextAppearance(this, R.style.BB_BottomBarBadge_Text);

        setColoredCircleBackground(backgroundColor);
        wrapTabAndBadgeInSameContainer(tab);
    }

    void setColoredCircleBackground(int circleColor) {
//        int innerPadding = MiscUtils.dpToPixel(getContext(), 1);
//        ShapeDrawable backgroundCircle = BadgeCircle.make(innerPadding * 3, circleColor);
//        setPadding(innerPadding, innerPadding, innerPadding, innerPadding);
//        setBackgroundCompat(backgroundCircle);
        if(circleColor == R.color.bb_badgeYellowBackgroundColor)
            setBackgroundResource(R.drawable.badge_yellow);
        else
            setBackgroundResource(R.drawable.badge);
    }

    private void wrapTabAndBadgeInSameContainer(final BottomBarTab tab) {
        ViewGroup tabContainer = (ViewGroup) tab.getParent();
        tabContainer.removeView(tab);

        final BadgeContainer badgeContainer = new BadgeContainer(getContext());
        badgeContainer.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        badgeContainer.addView(tab);
        BadgeContainer badgeAndTabContainer = (BadgeContainer) getParent();
        if (badgeAndTabContainer != null) {
            badgeAndTabContainer.removeView(this);
        }
        badgeContainer.addView(this);
        tabContainer.addView(badgeContainer, tab.getIndexInTabContainer());

        badgeContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    badgeContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    badgeContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                adjustPositionAndSize(tab);
            }
        });
    }

    boolean isAttached;

    boolean isAttached() {
        return isAttached;
    }

    void removeFromTab(BottomBarTab tab) {
        isAttached = false;
        BadgeContainer badgeAndTabContainer = (BadgeContainer) getParent();
        if (badgeAndTabContainer != null) {
            ViewGroup originalTabContainer = (ViewGroup) badgeAndTabContainer.getParent();

            badgeAndTabContainer.removeView(tab);
            if (originalTabContainer != null) {
                originalTabContainer.removeView(badgeAndTabContainer);
                originalTabContainer.addView(tab, tab.getIndexInTabContainer());
            }
        }

    }

    void adjustPositionAndSize(final BottomBarTab tab) {
        AppCompatImageView iconView = tab.getIconView();
        float xOffset = (float) (iconView.getWidth() / OFFSET);

        setX(iconView.getX() + xOffset);
        setTranslationY(10);

//        ViewGroup.LayoutParams params = getLayoutParams();
//
//        int size = Math.max(getWidth(), getHeight());
//        if (size > 0)
//            badgeSize = size;
//        else {
//            size = badgeSize == 0 ? MiscUtils.dpToPixel(getContext(), 22) : badgeSize;
//        }
//        if (params.width != size || params.height != size) {
//            params.width = size;
//            params.height = size;
//            setLayoutParams(params);
//        }
    }

    @SuppressWarnings("deprecation")
    private void setBackgroundCompat(Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(background);
        } else {
            setBackgroundDrawable(background);
        }
    }
}
