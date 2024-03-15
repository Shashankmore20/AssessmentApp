package com.apptmyz.assessment.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.apptmyz.assessment.R;
import com.apptmyz.assessment.activity.ViewTargets;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.database.DataSource;
import com.apptmyz.assessment.model.TargetModel;

import java.util.ArrayList;

public class TargetAdapter extends RecyclerView.Adapter<TargetAdapter.TargetViewHolder> {

    private ArrayList<TargetModel> targetList;
    private DataSource dataSource;
    private Context context;

    public TargetAdapter(Context context, ArrayList<TargetModel> targetList, DataSource dataSource) {
        this.context = context;
        this.targetList = targetList;
        this.dataSource = dataSource;
    }

    @NonNull
    @Override
    public TargetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.target_list_items_cards, parent, false);
        return new TargetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TargetViewHolder holder, int position) {
        TargetModel targetModel = targetList.get(position);

        // Bind data to your ViewHolder views
        holder.targetNameTextView.setText(targetModel.getTargetName());
        Log.d("target", targetModel.getTargetName());
        holder.targetSubject.setText(dataSource.subjects.getSubjectName(targetModel.getSubjectId()));
        holder.targetTime.setText(targetModel.getEndDate());
        holder.targetId.setText(targetModel.getId().toString());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    TargetModel targetModel = targetList.get(adapterPosition);
                    ViewTargets.deleteTarget(view.getContext(), targetModel.getId(), new ViewTargets.DeleteCallback() {
                        @Override
                        public void onDeleteSuccess() {
                            targetList.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                            notifyItemRangeChanged(adapterPosition, targetList.size());
                        }

                        @Override
                        public void onDeleteFailed(String errorMessage) {

                            Toast.makeText(context.getApplicationContext(), "Delete failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d("MyAPP", "Target id of delete is: " + targetModel.getId());
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetailsDialog(targetModel, view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return targetList.size();
    }

    private void showDetailsDialog(TargetModel targetModel, View view) {
        if (view == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.RoundedCornersDialog);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_target_details, null);
        builder.setView(dialogView);

        TextView dialogTargetName = dialogView.findViewById(R.id.dialogTargetName);
        TextView dialogTargetStart = dialogView.findViewById(R.id.dialogTargetStart);
        TextView dialogTargetEnd = dialogView.findViewById(R.id.dialogTargetEnd);
        TextView dialogTargetSubject = dialogView.findViewById(R.id.dialogTargetSubject);
        TextView dialogTargetTopic = dialogView.findViewById(R.id.dialogTargetTopic);
        TextView dialogTargetTestCount = dialogView.findViewById(R.id.dialogTargetTestCount);
        TextView dialogTargetDay = dialogView.findViewById(R.id.dialogTargetDay);

        //Setting Data to the views.
        if (targetModel.getTargetName() == null || targetModel.getTargetName().isEmpty()) {
            dialogTargetName.setText("No Target Name");
        } else {
            dialogTargetName.setText(targetModel.getTargetName());
        }
        dialogTargetSubject.setText(dataSource.subjects.getSubjectName(targetModel.getSubjectId()));
        dialogTargetEnd.setText(targetModel.getEndDate());
        dialogTargetStart.setText(targetModel.getStartDate());
        dialogTargetTopic.setText(dataSource.topics.getTopicName(targetModel.getTopicId()));
        if (targetModel.getTestCount() == 0) {
            dialogTargetTestCount.setText("No test Scheduled");
        } else {
            dialogTargetTestCount.setText(targetModel.getTestCount().toString() + "  Test's Scheduled");
        }
        if (targetModel.getRepeatingType() == 0) {
            dialogTargetTestCount.setText("Does not Repeat");
        } else {
            int repeatingType = targetModel.getRepeatingType();
            switch (repeatingType) {
                case 1:
                    dialogTargetDay.setText("Repeats Every Day");
                    break;
                case 2:
                    dialogTargetDay.setText("Repeats every " + (dataSource.days.getDayName(Long.valueOf(targetModel.getRepeatingDay()))));
                    break;
                case 3:
                    dialogTargetDay.setText("Repeats Every Week");
                    break;
                case 4:
                    dialogTargetDay.setText("Repeats Every " + (dataSource.weeks.getWeekName(Long.valueOf(targetModel.getRepeatingWeek()))));
                    break;
                case 5:
                    dialogTargetDay.setText("Repeats Every Month");
                    break;
                case 6:
                    dialogTargetDay.setText("Repeats Every " + (dataSource.months.getMonthName(Long.valueOf(targetModel.getRepeatingMonth()))));
                    break;
                default:
                    dialogTargetDay.setText("Not Defined");
                    break;
            }
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    public class TargetViewHolder extends RecyclerView.ViewHolder {
        TextView targetNameTextView, targetId, targetTime, targetSubject;
        ImageView delete;

        public TargetViewHolder(@NonNull View itemView) {
            super(itemView);
            targetNameTextView = itemView.findViewById(R.id.targetName);
            targetSubject = itemView.findViewById(R.id.targetSubject);
            targetTime = itemView.findViewById(R.id.targetTime);
            targetId = itemView.findViewById(R.id.targetId);
            delete = itemView.findViewById(R.id.targetDelete);
        }
    }

}
