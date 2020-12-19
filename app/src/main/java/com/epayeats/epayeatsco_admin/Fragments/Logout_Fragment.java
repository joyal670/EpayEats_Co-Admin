package com.epayeats.epayeatsco_admin.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epayeats.epayeatsco_admin.Activity.Login_Activity;
import com.epayeats.epayeatsco_admin.R;
import com.google.firebase.auth.FirebaseAuth;


public class Logout_Fragment extends Fragment {

    SharedPreferences sharedPreferences;

    public Logout_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logout_, container, false);

        try {

            sharedPreferences = getActivity().getSharedPreferences("data", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(getContext(), Login_Activity.class);
            startActivity(intent);
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}