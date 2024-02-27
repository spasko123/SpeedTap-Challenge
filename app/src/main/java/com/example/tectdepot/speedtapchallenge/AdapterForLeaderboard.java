package com.example.tectdepot.speedtapchallenge;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterForLeaderboard extends RecyclerView.Adapter<AdapterForLeaderboard.ViewHolder> {

    private Context context;
    private List<String> namesList;
    private List<String> scoresList;


    public AdapterForLeaderboard(Context context, List<String> namesList, List<String> scoresList) {
        this.context = context;
        this.namesList = namesList;
        this.scoresList = scoresList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_view_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position + 1;
        if(position == 0){
            holder.conLayout.setBackgroundResource(R.drawable.rounded_border_yellow);
            holder.txtUsername.setTextColor(Color.BLACK);
            holder.txtScore.setTextColor(Color.BLACK);
            holder.txtPlace.setTextColor(Color.BLACK);
        }
        String pos2 = String.valueOf(pos);
        holder.txtPlace.setText("" + pos2);
        holder.txtUsername.setText(namesList.get(position));
        holder.txtScore.setText(scoresList.get(position));
        holder.conLayout.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recycler_view_item_animation));
    }

    @Override
    public int getItemCount() {
        return namesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtPlace;
        private TextView txtUsername;
        private TextView txtScore;
        private ConstraintLayout conLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPlace = itemView.findViewById(R.id.txtPlace);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtScore = itemView.findViewById(R.id.txtScore);
            conLayout = itemView.findViewById(R.id.conLayout);
        }

    }
}
