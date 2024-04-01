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
import com.apptmyz.assessment.activity.ParentRegistrationActivity;
import com.apptmyz.assessment.fragment.TodayScheduleFragment;
import com.apptmyz.assessment.model.TestScheduleModel;
import com.apptmyz.assessment.model.UserModelNew;

import java.util.ArrayList;
import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {
    private ArrayList<UserModelNew> userList;
    private Context context;

    //Interface to notify the activity
    public interface OnChildRemovedListener {
        void onChildRemoved(int position);
    }

    private OnChildRemovedListener listener;

    public void setOnChildRemovedListener(OnChildRemovedListener listener) {
        this.listener = listener;
    }

    public ChildAdapter(ArrayList<UserModelNew> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ChildAdapter.ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child_details, parent, false);
        return new ChildAdapter.ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildAdapter.ChildViewHolder holder, int position) {
        UserModelNew user = userList.get(position);

        holder.tvName.setText(user.getFirstName() + user.getLastName());
        holder.tvClass.setText(user.getUserClass().toString());
        holder.tvDateOfBirth.setText(user.getDob());

        holder.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // Remove the child from the userList
                    userList.remove(adapterPosition);
                    // Notify adapter about the item deletion
                    notifyItemRemoved(adapterPosition);
                    // Notify if dataset is changed
                    notifyItemRangeChanged(adapterPosition, userList.size());

                    if (listener != null) {
                        listener.onChildRemoved(position);
                    }
                    // Show a toast or perform any other action to indicate successful deletion
                    Toast.makeText(view.getContext(), "Child Removed", Toast.LENGTH_SHORT).show();
                    Log.d("UserModel", userList.toString());
                }
            }
        });

        holder.imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvClass, tvDateOfBirth;
        ImageView imageEdit, imageDelete;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvClass = itemView.findViewById(R.id.tvClass);
            tvDateOfBirth = itemView.findViewById(R.id.tvDateOfBirth);
            imageEdit = itemView.findViewById(R.id.imageEdit);
            imageDelete = itemView.findViewById(R.id.imageDelete);
        }
    }

    public void clearData() {
        userList.clear();
        notifyDataSetChanged();
    }
}
