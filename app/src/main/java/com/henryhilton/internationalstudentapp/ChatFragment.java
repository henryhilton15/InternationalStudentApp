package com.henryhilton.internationalstudentapp;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by henryhilton on 11/9/17.
 */



public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";
    private LinearLayout mLayout;

    private List<BuddyData> mBuddyDataList = new ArrayList<BuddyData>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buddies,container,false);
        mLayout = view.findViewById(R.id.buddiesLayout);

        downloadBuddiesList();

        return view;
    }

    public void populateBuddiesData(){

        LinearLayout layout = mLayout;

        for (int i = 0; i < mBuddyDataList.size(); i++) {
            View cell = layout.getChildAt(i);

            if (cell instanceof ConstraintLayout){
                final ConstraintLayout subLayout = (ConstraintLayout) layout.getChildAt(i);

                if (mBuddyDataList.get(i).getChat() == false) {
                    subLayout.setVisibility(View.GONE);
                }
                else
                    subLayout.setVisibility(View.VISIBLE);

                for (int j = 0; j < subLayout.getChildCount(); j++) {
                    View v = subLayout.getChildAt(j);
                    if (v instanceof TextView) {
                        if (v.getId() == R.id.fullName){
                            ((TextView) v).setText(mBuddyDataList.get(i).getNameYear());
                        }
                        else if (v.getId() == R.id.activities){
                            ((TextView) v).setText(mBuddyDataList.get(i).getActivities());
                        }
                        else if (v.getId() == R.id.major){
                            ((TextView) v).setText(mBuddyDataList.get(i).getMajor());
                        }
                    }
                    else if (v instanceof ImageView) {
                        if (v.getId() == R.id.messageButton){

                            final int buddyID = i;
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Integer id = buddyID;
                                    sendMessage(id);
                                }});
                        }
                        else if (v.getId() == R.id.removeButton){

                            final int buddyID = i;

                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    subLayout.setVisibility(View.GONE);
                                    Integer id = buddyID;
                                    removeChat(id);

                                }});
                        }
                    }
                }
            }
        }
    }

    public void downloadBuddiesList(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference buddies = database.getReference("buddies");

        buddies.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mBuddyDataList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    BuddyData temp = data.getValue(BuddyData.class);
                    mBuddyDataList.add(temp);
                }
                Log.d("CHAT UPDATE", "Buddy List Size: " + mBuddyDataList.size());
                Log.d("CHAT UPDATE", "buddy 1 chat bool: " + mBuddyDataList.get(0).getChat());
                populateBuddiesData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void removeChat(Integer buddyID){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference b = database.getReference("buddies/" + buddyID + "/chat");
        b.setValue(false);
    }

    public void sendMessage(Integer buddyID){

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Sent from the Dartmouth International Student App");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }

}