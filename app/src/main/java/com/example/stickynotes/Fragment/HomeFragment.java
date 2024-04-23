package com.example.stickynotes.Fragment;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.stickynotes.Adapter.AdapterNotes;
import com.example.stickynotes.Model.Note;
import com.example.stickynotes.R;
import com.example.stickynotes.databinding.AddNoteBinding;
import com.example.stickynotes.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;


public class HomeFragment extends Fragment implements AdapterNotes.OnItemClickListener {

    FragmentHomeBinding binding;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refNotes = database.getReference("Notes");

    List<Note> noteList;
    AdapterNotes adapterNotes;

    public HomeFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        init();
        getNotes();
        onClickListener();

        return binding.getRoot();
    }

    void init() {
        noteList = new ArrayList<>();
        adapterNotes = new AdapterNotes(getContext(), noteList, this);
        binding.notesRecyclerView.setAdapter(adapterNotes);
    }

    void getNotes() {
        binding.progressBar.setVisibility(View.VISIBLE);
        refNotes.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noteList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Note note = snapshot.getValue(Note.class);
                    if (note != null) {
                        noteList.add(note);
                    }
                }
                binding.progressBar.setVisibility(View.GONE);
                adapterNotes.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void onClickListener() {
        binding.addNewNote.setOnClickListener(v -> {
            showDialogAddNoteLayout(null);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showDialogAddNoteLayout(Note currentNote) {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_note, null);
        dialogBuilder.setView(dialogView);
        androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));

        // Initialize ViewBinding for the layout
        AddNoteBinding addNoteBinding = AddNoteBinding.bind(dialogView);
        addNoteBinding.cardTitle.setText(currentNote == null ? getString(R.string.add_note) : getString(R.string.update_note));

        Note note;
        if (currentNote == null) {
            note = new Note();
            note.setUID(firebaseUser.getUid());
            note.setID(refNotes.push().getKey());
            note.setColor("Color1");
        } else {
            note = currentNote;
            addNoteBinding.inputNoteTitle.setText(note.getNoteTitle());
            addNoteBinding.inputNoteDescription.setText(note.getNoteDescription());

            if (note.getColor().equals("Color1")) {
                addNoteBinding.cardTitle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color1)));
                addNoteBinding.RBColor1.setChecked(true);
            } else if (note.getColor().equals("Color2")) {
                addNoteBinding.cardTitle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color2)));
                addNoteBinding.RBColor2.setChecked(true);
            } else if (note.getColor().equals("Color3")) {
                addNoteBinding.cardTitle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color3)));
                addNoteBinding.RBColor3.setChecked(true);
            } else if (note.getColor().equals("Color4")) {
                addNoteBinding.cardTitle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color4)));
                addNoteBinding.RBColor4.setChecked(true);
            } else {
                addNoteBinding.cardTitle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color1)));
                addNoteBinding.RBColor1.setChecked(true);
            }
        }

        addNoteBinding.colorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.RB_Color1) {
                    addNoteBinding.cardTitle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color1)));
                    note.setColor("Color1");
                } else if (checkedId == R.id.RB_Color2) {
                    addNoteBinding.cardTitle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color2)));
                    note.setColor("Color2");
                } else if (checkedId == R.id.RB_Color3) {
                    addNoteBinding.cardTitle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color3)));
                    note.setColor("Color3");
                } else if (checkedId == R.id.RB_Color4) {
                    addNoteBinding.cardTitle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color4)));
                    note.setColor("Color4");
                } else {
                    addNoteBinding.cardTitle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color1)));
                    note.setColor("Color1");
                }
            }
        });

        addNoteBinding.btnSave.setOnClickListener(v -> {

            String inputNoteTitle = Objects.requireNonNull(addNoteBinding.inputNoteTitle.getText()).toString().trim();
            if (inputNoteTitle.isEmpty()) {
                addNoteBinding.inputNoteTitle.setError(getString(R.string.note_title_is_required));
                addNoteBinding.inputNoteTitle.requestFocus();
                return;
            }
            String inputNoteDescription = Objects.requireNonNull(addNoteBinding.inputNoteDescription.getText()).toString().trim();
            if (inputNoteDescription.isEmpty()) {
                addNoteBinding.inputNoteDescription.setError(getString(R.string.note_description_is_required));
                addNoteBinding.inputNoteDescription.requestFocus();
                return;
            }

            note.setNoteTitle(inputNoteTitle);
            note.setNoteDescription(inputNoteDescription);

            refNotes.child(firebaseUser.getUid()).child(note.getID()).setValue(note).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toasty.success(requireContext(), getString(R.string.save_note_successfully), Toast.LENGTH_SHORT).show();
                }
            });
            alertDialog.dismiss();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItem_Click(int position) {
        Note note = noteList.get(position);
        showDialogAddNoteLayout(note);
    }
}