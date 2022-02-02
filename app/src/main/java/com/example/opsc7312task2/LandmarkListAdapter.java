package com.example.opsc7312task2;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import User.UserFavouriteLandmarks;

//-----------------------------------------------------------------------------------------------------------------------
//The login activity, where the user will gain access to the rest of the application
public class LandmarkListAdapter extends ArrayAdapter<UserFavouriteLandmarks> {

    //variables
    private ArrayList<UserFavouriteLandmarks> data;
    private Activity activity;
    public int resID;

    //-----------------------------------------------------------------------------------------------------------------------
    //Constructor
    public LandmarkListAdapter(Activity activity, ArrayList<UserFavouriteLandmarks> data, int resID) {
        super(activity, resID, data);
        this.data = data;
        this.activity = activity;
        this.resID = resID;
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //creating each listview row
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View row = view;
        ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(resID, parent, false);

        //stores the data in the ViewHolder class, to display
        holder = new ViewHolder();
        holder.landmark = data.get(position);
        holder.landmarkTitle = (TextView) row.findViewById(R.id.tvTitle);
        holder.landmarkDirectionsBtn = (Button) row.findViewById(R.id.btnFavLandmarkDirections);
        holder.deleteLandmarkBtn = (Button) row.findViewById(R.id.btnDeleteFavLandmark);


        //onclick for delete button
        holder.deleteLandmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItemFromDb(data.get(position)); //remove from db
                data.remove(position); //remove from list
                clear(); //clear list as it will be updated with new content
                notifyDataSetChanged(); //notify list of changes
            }
        });

        //setting the tag as the landmark, to be accessed later
        row.setTag(holder);
        holder.landmarkDirectionsBtn.setTag(holder.landmark);
        setupItem(holder, position);
        return row;
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //method to setup the item values in the row of the list
    public void setupItem(ViewHolder holder, int position) {
        holder.landmarkTitle.setText(holder.landmark.getTitle());
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //method to remove the selected landmark
    public void removeItemFromDb(UserFavouriteLandmarks landmark) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://opsc7312-poe-8e798-default-rtdb.europe-west1.firebasedatabase.app/");

        //firebase
        DatabaseReference mDatabase = database.getReference();
        FirebaseUser user = auth.getCurrentUser();

        mDatabase.child("UserFavouriteLandmarks").child(user.getUid()).child(landmark.getTitle()).setValue(null)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Landmark Deleted!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    //-----------------------------------------------------------------------------------------------------------------------
    //public class to contain the data of the inflated XML elements;
    public static class ViewHolder {
        UserFavouriteLandmarks landmark;
        public TextView landmarkTitle;
        public Button landmarkDirectionsBtn, deleteLandmarkBtn;
    }
}
//-------------------------------------------------End Of File----------------------------------------------------------