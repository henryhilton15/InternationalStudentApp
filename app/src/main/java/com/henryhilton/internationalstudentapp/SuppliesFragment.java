package com.henryhilton.internationalstudentapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by henryhilton on 11/9/17.
 */

public class SuppliesFragment extends Fragment {

        private static final String TAG = "SuppliesFragment";

        private Button btnTEST;
        private CheckBox mAddPillow;
        private CheckBox mAddSheets;
        private CheckBox mAddBlanket;
        private CheckBox mAddShampoo;
        private Button mSaveToBoxButton;


    @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_supplies,container,false);
            mSaveToBoxButton = (Button) view.findViewById(R.id.saveToBoxButton);

            mAddPillow = view.findViewById(R.id.addPillow);
            mAddSheets = view.findViewById(R.id.addSheets);
            mAddBlanket = view.findViewById(R.id.addBlanket);
            mAddShampoo = view.findViewById(R.id.addShampoo);

            downloadSupplies();

            mSaveToBoxButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateFirebaseSupplies();
                    Toast.makeText(getActivity(), "Saving Supplies to Box",Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }

        public void updateFirebaseSupplies(){

            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference pillow = database.getReference("supplies/pillow");
            pillow.setValue(mAddPillow.isChecked());

            DatabaseReference sheets = database.getReference("supplies/sheets");
            sheets.setValue(mAddSheets.isChecked());

            DatabaseReference blanket = database.getReference("supplies/blanket");
            blanket.setValue(mAddBlanket.isChecked());

            DatabaseReference shampoo = database.getReference("supplies/shampoo");
            shampoo.setValue(mAddShampoo.isChecked());
        }

    public void downloadSupplies(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference buddies = database.getReference("supplies");

        buddies.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SuppliesData temp = dataSnapshot.getValue(SuppliesData.class);
                setCheckBoxStates(temp);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Network Connection Error - Choices will not save",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setCheckBoxStates(SuppliesData supplies){

        if (supplies.getPillow()) mAddPillow.setChecked(true);
        else mAddPillow.setChecked(false);

        if (supplies.getSheets()) mAddSheets.setChecked(true);
        else mAddSheets.setChecked(false);


        if (supplies.getBlanket()) mAddBlanket.setChecked(true);
        else mAddBlanket.setChecked(false);

        if (supplies.getShampoo())  mAddShampoo.setChecked(true);
        else mAddShampoo.setChecked(false);
    }

}
