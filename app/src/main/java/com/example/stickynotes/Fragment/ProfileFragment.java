package com.example.stickynotes.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.stickynotes.Auth.LoginActivity;
import com.example.stickynotes.Model.User;
import com.example.stickynotes.R;
import com.example.stickynotes.databinding.FragmentProfileBinding;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import es.dmoral.toasty.Toasty;


public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refUsers = database.getReference("Users");
    User currentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        getUserData();

        binding.btnSave.setOnClickListener(v -> {
            validationInput();
        });

        binding.logout.setOnClickListener(v -> {
            auth.signOut();
            Toasty.warning(requireContext(), getString(R.string.logout), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            getActivity().finishAffinity();
        });

        return binding.getRoot();
    }

    void getUserData() {
        binding.progressCircle.setVisibility(View.VISIBLE);
        refUsers.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    currentUser = user;
                    binding.inputEmail.setText(user.getuEmail());
                    binding.inputUserName.setText(user.getUserName());
                }
                binding.progressCircle.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.progressCircle.setVisibility(View.GONE);
            }
        });
    }

    void validationInput() {

        binding.progressCircle.setVisibility(View.VISIBLE);

        String inputUserName = Objects.requireNonNull(binding.inputUserName.getText()).toString().trim();
        if (inputUserName.isEmpty()) {
            binding.inputUserName.setError(getString(R.string.nameIsRequired));
            binding.inputUserName.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }
        currentUser.setUserName(inputUserName);
        updateUser(currentUser);
    }

    void updateUser(User user) {
        refUsers.child(user.getuID()).setValue(user).addOnSuccessListener(unused -> {
            binding.progressCircle.setVisibility(View.INVISIBLE);
            Toasty.success(requireContext(), getString(R.string.update_successfully), Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(e -> {
            binding.progressCircle.setVisibility(View.INVISIBLE);
            if (e instanceof FirebaseNetworkException) {
                Toast.makeText(requireContext(), getString(R.string.noConnection), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Exception -> " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}