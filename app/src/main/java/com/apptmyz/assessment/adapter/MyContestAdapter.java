package com.apptmyz.assessment.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.activity.ReviewActivity;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.model.ContestListModel;

import java.util.List;

public class MyContestAdapter extends RecyclerView.Adapter<MyContestAdapter.MyContestViewHolder> {

    private final Context context;
    private final List<ContestListModel> data;
    //    private final List<UserTestClass> data;
    private String catname;


    public MyContestAdapter(Context context, List<ContestListModel> data) {
        this.context = context;
        this.data = data;
        this.catname = catname;
    }

    @NonNull
    @Override
    public MyContestAdapter.MyContestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.my_test_item_list, parent, false);
        return new MyContestAdapter.MyContestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyContestAdapter.MyContestViewHolder holder, int position) {
        final ContestListModel test = data.get(position);

        if (test.getTitle() != null) holder.title.setText(test.getTitle());
        if (test.getStarttesttime() != null)
            holder.subtitle.setText(App.dateFun(test.getStarttesttime()));
        if (test.getStarttesttime() != null)
            holder.relativeTime.setText(App.getRelationTime(test.getStarttesttime()));
        Log.d("test", test.getStarttesttime());

        holder.itemView.setOnClickListener(v -> {
            // Toast.makeText(context, catname + " was clicked", Toast.LENGTH_SHORT).show();
            Intent intent;
            intent = new Intent(v.getContext(), ReviewActivity.class);
            intent.putExtra("from", "basic");
            intent.putExtra("question", String.valueOf(test.getUsertestid()));
            v.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyContestViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLogo;
        TextView title, subtitle, relativeTime;

        public MyContestViewHolder(View itemView) {
            super(itemView);

            imgLogo = itemView.findViewById(R.id.imgLogo);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            relativeTime = itemView.findViewById(R.id.relativeTime);

        }
    }
}