package com.example.opsc7312task2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import User.UserFavouriteLandmarks;

public class FavouriteLandmarkFragment extends Fragment {

    //firebase
    private FirebaseDatabase database;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private static final String TAG = "FAVOURITE LANDMARKS";

    //listview
    private TextView emptyListTxt;
    private ArrayList<UserFavouriteLandmarks> userLandmarks = new ArrayList<UserFavouriteLandmarks>();
    private ListView favouriteLandmarksList;
    private LandmarkListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_landmark, container, false);

        //firebase database and user
        MainActivity activity = (MainActivity) getActivity();
        database = FirebaseDatabase.getInstance("https://opsc7312-poe-8e798-default-rtdb.europe-west1.firebasedatabase.app/");
        user = activity.getUser();

        //getting the landmarks and listview
        emptyListTxt = view.findViewById(R.id.tvEmptyList);
        favouriteLandmarksList = view.findViewById(R.id.lvFavLandmarks);
        favouriteLandmarksList.setEmptyView(emptyListTxt);
        getFavouriteLandmarks(activity);

        // Inflate the layout for this fragment
        return view;
    }

    //---------------------------------------------------------------------------------------------------------------
    //getting favourite landmarks for user
    public void getFavouriteLandmarks(MainActivity activity){
            DatabaseReference mDatabase = database.getReference("UserFavouriteLandmarks/"+user.getUid());
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //iterate through each saved landmark and retrieve the data
                    for (DataSnapshot landmarkData : snapshot.getChildren()){
                        UserFavouriteLandmarks landmark = landmarkData.getValue(UserFavouriteLandmarks.class);
                        //adding the landmark to the arraylist for the listview
                        userLandmarks.add(landmark);
                    }
                    adapter = new LandmarkListAdapter(activity, userLandmarks, R.layout.landmarklistview);
                    favouriteLandmarksList.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
    //--
}