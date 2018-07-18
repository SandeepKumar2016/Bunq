package com.support.android.designlibdemo;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Data model class for all data related to a cheese.
 *
 * Created by wessel on 21/03/16.
 */
public class Cheese implements Serializable {
    /**
     * The resource ID of the {@link Cheese}'s drawable.
     */
    private int mDrawableResId;

    /**
     * The name of the {@link Cheese}.
     */
    private String mName;

    /**
     * Construct a new {@link Cheese}.
     *
     * @param drawableResId The resource ID of the {@link Cheese}'s drawable.
     * @param name The name of the {@link Cheese}.
     */
    public Cheese(int drawableResId, @NonNull String name) {
        mDrawableResId = drawableResId;
        mName = name;
    }

    /**
     * @return The resource ID of the {@link Cheese}'s drawable.
     */
    public int getDrawableResId() {
        return mDrawableResId;
    }

    /**
     * @return The name of the {@link Cheese}.
     */
    @NonNull
    public String getName() {
        return mName;
    }
}
