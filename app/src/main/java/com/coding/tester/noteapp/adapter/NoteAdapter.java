package com.coding.tester.noteapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.coding.tester.noteapp.NoteActivity;
import com.coding.tester.noteapp.R;
import com.coding.tester.noteapp.callback.IClickListener;
import com.coding.tester.noteapp.database.Note;
import com.coding.tester.noteapp.databinding.NoteLayoutBinding;
import com.coding.tester.noteapp.events.NoteClickEvent;
import com.coding.tester.noteapp.events.NoteDeleteEvent;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> noteList = new ArrayList<>();
    private final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private final Calendar calendar = Calendar.getInstance();
    private final Context context;


    public NoteAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {

        calendar.setTimeInMillis(Long.parseLong(noteList.get(position).getTime()));

        holder.layoutBinding.txtTime.setText(format.format(calendar.getTimeInMillis()));
        holder.layoutBinding.txtTitle.setText(noteList.get(position).getTitle());
        holder.layoutBinding.txtDescription.setText(ellipsize(noteList.get(position).getDescription()));

        holder.setOnClickListener((view, pos) -> {
            EventBus.getDefault().postSticky(new NoteClickEvent(noteList.get(pos)));
            context.startActivity(new Intent(context, NoteActivity.class));
        });

        holder.layoutBinding.imgDelete.setOnClickListener(view ->
                EventBus.getDefault().postSticky(new NoteDeleteEvent(noteList.get(position))));

    }

    public void seNoteList(List<Note> notes){
        noteList = notes;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        NoteLayoutBinding layoutBinding;
        private IClickListener onClickListener;

        public void setOnClickListener(IClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutBinding = NoteLayoutBinding.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onClickListener.onClick(view, getAdapterPosition());
        }

    }

    private String ellipsize(String input) {
        if (input == null || input.length() < 30) {
            return input;
        }
        return input.substring(0, 30) + "...";
    }


}
