package com.coding.tester.noteapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coding.tester.noteapp.events.DeleteRecordEvent;
import com.github.marlonlom.utilities.timeago.TimeAgo;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import com.coding.tester.noteapp.R;
import com.coding.tester.noteapp.callback.IClickListener;
import com.coding.tester.noteapp.databinding.RecordItemBinding;
import com.coding.tester.noteapp.events.ClickToRecordEvent;


public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private File[] listFile;


    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {

        holder.binding.fileName.setText(ellipsize(listFile[position].getName()));
        holder.binding.date.setText(TimeAgo.from(listFile[position].lastModified()));

        holder.setListener((view, position1) ->
                EventBus.getDefault().postSticky(new ClickToRecordEvent(listFile[position])));

        holder.binding.imgDelete.setOnClickListener(view ->
                EventBus.getDefault().postSticky(new DeleteRecordEvent(listFile[position])));

    }


    public void seRecordList(File[] files){
        this.listFile = files;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return listFile.length;
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        RecordItemBinding binding;
        private IClickListener listener;

        public void setListener(IClickListener listener) {
            this.listener = listener;
        }

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecordItemBinding.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }


    private String ellipsize(String input) {
        if (input == null || input.length() < 28) {
            return input;
        }
        return input.substring(0, 28) + "...";
    }


}
