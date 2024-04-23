package com.example.stickynotes.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stickynotes.Model.Note;
import com.example.stickynotes.R;
import com.example.stickynotes.databinding.DeleteMessageLayoutBinding;
import com.example.stickynotes.databinding.ItemNoteBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class AdapterNotes extends RecyclerView.Adapter<AdapterNotes.ViewHolder> {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refNotes = database.getReference("Notes");

    Context context;
    List<Note> mNotes;
    private OnItemClickListener mListener;

    public AdapterNotes(Context context, List<Note> notes, OnItemClickListener mListener) {
        this.context = context;
        this.mNotes = notes;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemNoteBinding binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    @SuppressLint("SimpleDateFormat")
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = mNotes.get(position);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(note.getCreatedDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());


        holder.binding.year.setText(yearFormat.format(date));
        holder.binding.day.setText(dayFormat.format(date));
        holder.binding.month.setText(monthFormat.format(date));


        if (note.getColor().equals("Color1")) {
            holder.binding.dateLayout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color1)));
        } else if (note.getColor().equals("Color2")) {
            holder.binding.dateLayout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color2)));
        } else if (note.getColor().equals("Color3")) {
            holder.binding.dateLayout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color3)));
        } else if (note.getColor().equals("Color4")) {
            holder.binding.dateLayout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color4)));
        } else {
            holder.binding.dateLayout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color1)));
        }

        holder.binding.noteTitle.setText(note.getNoteTitle());
        holder.binding.noteDescription.setText(note.getNoteDescription());

        holder.binding.delete.setOnClickListener(v -> {
            showDialogDeleteMsgLayout(context.getString(R.string.are_your_sure_delete), note);
        });
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItem_Click(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemNoteBinding binding;

        public ViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItem_Click(position);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showDialogDeleteMsgLayout(String msg, Note note) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_message_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));

        // Initialize ViewBinding for the layout
        DeleteMessageLayoutBinding deleteMessageBinding = DeleteMessageLayoutBinding.bind(dialogView);
        deleteMessageBinding.inputMessage.setText(msg);
        deleteMessageBinding.buttonYes.setOnClickListener(v -> {
            refNotes.child(note.getUID()).child(note.getID()).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toasty.success(context, context.getString(R.string.delete), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            });
        });

        deleteMessageBinding.buttonNo.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }
}
