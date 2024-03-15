package com.apptmyz.assessment.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.model.RankLevelsModel;

import org.w3c.dom.Text;

import java.util.List;

public class RecyclerAdapterRanks extends RecyclerView.Adapter<RecyclerAdapterRanks.MyViewHolder> {

    List<RankLevelsModel> rankLevelsModels;

    public RecyclerAdapterRanks(List<RankLevelsModel> rankLevelsModels) {
        this.rankLevelsModels = rankLevelsModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_recycler,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RankLevelsModel currentItem = rankLevelsModels.get(position);

        holder.leaderboard.setText(String.valueOf(currentItem.getStudentRank() + "/"));
        holder.group.setText(currentItem.getRankLevel());
        holder.percentage.setText(currentItem.getPercentile() + "%");
        holder.rankTotal.setText(String.valueOf(currentItem.getTotalStudents()));

        // Set background color
        holder.back.setCardBackgroundColor(Color.parseColor(currentItem.getHexColor()));

    }

    @Override
    public int getItemCount() {
        return rankLevelsModels.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView group, percentage, leaderboard, rankTotal;
        CardView back;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            group = itemView.findViewById(R.id.group);
            percentage = itemView.findViewById(R.id.percentage);
            leaderboard = itemView.findViewById(R.id.rank);
            rankTotal = itemView.findViewById(R.id.rankTotal);
            back = itemView.findViewById(R.id.background);
        }
    }
}
