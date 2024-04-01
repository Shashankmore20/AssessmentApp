package com.apptmyz.assessment.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.activity.ViewTargets;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.fragment.TodayScheduleFragment;
import com.apptmyz.assessment.model.TargetModel;
import com.apptmyz.assessment.model.TestScheduleModel;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private ArrayList<TestScheduleModel> scheduleList;
    private DataSource dataSource;
    private Context context;

    public ScheduleAdapter(Context context, ArrayList<TestScheduleModel> scheduleList, DataSource dataSource) {
        this.context = context;
        this.scheduleList = scheduleList;
        this.dataSource = dataSource;
    }

    @NonNull
    @Override
    public ScheduleAdapter.ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_items_cards, parent, false);
        return new ScheduleAdapter.ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleAdapter.ScheduleViewHolder holder, int position) {
        TestScheduleModel scheduleModel = scheduleList.get(position);

        // Bind data to your ViewHolder views
        holder.scheduleNameTextView.setText(scheduleModel.getTitle());
        Log.d("target", scheduleModel.getTitle());
        holder.scheduleSubject.setText(dataSource.subjects.getSubjectName(scheduleModel.getSubjectId()));
        holder.scheduleDate.setText(scheduleModel.getScheduledDateAndTime());
        holder.scheduleTopic.setText(dataSource.topics.getTopicName(scheduleModel.getTopicId()));
        holder.scheduleId.setText(scheduleModel.getId().toString());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    TestScheduleModel scheduleModel = scheduleList.get(adapterPosition);
                    TodayScheduleFragment.deleteSchedule(context, scheduleModel.getId(), new TodayScheduleFragment.DeleteCallback() {
                        @Override
                        public void onDeleteSuccess() {
                            scheduleList.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                            notifyItemRangeChanged(adapterPosition, scheduleList.size());
                        }

                        @Override
                        public void onDeleteFailed(String errorMessage) {

                            Toast.makeText(context, "Delete failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d("MyAPP", "Target id of delete is: " + scheduleModel.getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView scheduleNameTextView, scheduleId, scheduleDate, scheduleSubject, scheduleTopic;
        ImageView delete;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            scheduleNameTextView = itemView.findViewById(R.id.scheduleName);
            scheduleSubject = itemView.findViewById(R.id.scheduleSubject);
            scheduleDate = itemView.findViewById(R.id.scheduleDate);
            scheduleTopic = itemView.findViewById(R.id.scheduleTopic);
            scheduleId = itemView.findViewById(R.id.scheduleId);
            delete = itemView.findViewById(R.id.scheduleDelete);
        }
    }

    public void clearData() {
        scheduleList.clear();
        notifyDataSetChanged();
    }

}