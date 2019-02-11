package com.henryhilton.internationalstudentapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Henry on 12/6/2017.
 */

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    TextView mEmail;
    TextView mName;
    TextView mCountry;
    ImageView mProfilePic;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        Button logOutButton = (Button) view.findViewById(R.id.logOut);
        mEmail = view.findViewById(R.id.profileEmail);
        mName = view.findViewById(R.id.profileName);
        mCountry = view.findViewById(R.id.profileCountry);
        mProfilePic = view.findViewById(R.id.userProfilePic);

        downloadProfileData();

        loadSnap();

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Logging Out",Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                launchLogOutIntent();
            }
        });

        return view;
    }

    public void downloadProfileData(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference buddies = database.getReference("profile");

        buddies.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ProfileData temp = dataSnapshot.getValue(ProfileData.class);
                mName.setText(temp.getFullName());
                mCountry.setText(temp.getCountry());
               // mEmail.setText(temp.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Network Connection Error - Cannot Load Profile Data",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadSnap() {

        // Load profile photo from internal storage
        try {

            FileInputStream fis = getContext().openFileInput("profile_photo.png");
            Bitmap bmap = BitmapFactory.decodeStream(fis);
            mProfilePic.setImageBitmap(bmap);
            fis.close();
        } catch (IOException e) {
            // Default profile photo if no photo saved before.
            mProfilePic.setImageResource(R.drawable.default_profile);
        }
    }

    public void launchLogOutIntent(){
        Intent intent = new Intent(getContext(), SignInExample.class);
        startActivity(intent);
        getActivity().finish();
    }

}
