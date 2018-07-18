/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.support.android.designlibdemo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CheeseListFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); //lets fragment to retain its previous state after screen config changes
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_cheese_list, container, false);

    }

    private ProgressBar progressBar;
    private TextView tvForErrorMsg;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarAtCheeseFrag);
        tvForErrorMsg = (TextView) view.findViewById(R.id.tvForErrorMsgATCheeseFrag);
        setupRecyclerView(recyclerView);

    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) { //to restore the fragment's view state after screen config changed
        super.onViewStateRestored(savedInstanceState);
        onCreate(savedInstanceState);
    }

    private Context context;

    @Override
    public void onAttach(Context context) { //fetching the context on fragment attached to the activity is the best practice to avoid null Contexts
        super.onAttach(context);
        this.context = context;
    }




    private void setupRecyclerView(final RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(context, new ArrayList<Cheese>()));//setting up recycler adapter with empty list as default adapter or
        // else recycler won't attach to adapter skipping layout if we get any error prior setting up cheese data to recycler
        new AsyncTask<String, Void, List<Cheese>>() { //executing the cheese list preparation in background using AsyncTask

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
                tvForErrorMsg.setVisibility(View.GONE);
            }

            @Override
            protected List<Cheese> doInBackground(String... strings) {
                try {
                    List<Cheese> cheeses = CheeseApi.listCheeses(30);
                    return cheeses;
                } catch (final IOException exception) {
                    // Ignore.
                    tvForErrorMsg.post(new Runnable() { //taking the advantage of accessing ui from thread using View.post(Runnable) for setting error message
                        @Override
                        public void run() {
                            tvForErrorMsg.setText(exception.getMessage());
                        }
                    });
                }
                return new ArrayList<Cheese>();//returning empty list if exception occured in preparing cheese list
            }

            @Override
            protected void onPostExecute(List<Cheese> cheeses) {
                super.onPostExecute(cheeses);
                progressBar.setVisibility(View.GONE);
                if (cheeses.isEmpty()) tvForErrorMsg.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(context, cheeses));
            }
        }.execute();

    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Cheese> mValues;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public Cheese mBoundItem;

            public final View mView;
            public final ImageView mImageView;
            public final TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(android.R.id.text1);

                mView.setOnClickListener(new View.OnClickListener() { //setting onclicklistner in Viewholder avoids multiple onclicklistener object creations than at onBindViewHolder
                    @Override
                    public void onClick(View v) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, CheeseDetailActivity.class);
                        intent.putExtra(CheeseDetailActivity.EXTRA_CHEESE, mValues.get(getLayoutPosition()));

                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }

        public Cheese getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<Cheese> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mBoundItem = mValues.get(position);
            holder.mTextView.setText(holder.mBoundItem.getName());


            Glide.with(holder.mImageView.getContext())
                    .load(holder.mBoundItem.getDrawableResId())
                    .fitCenter()
                    .into(holder.mImageView);

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
