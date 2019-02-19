package com.team6.krafty;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.update_fragment_profile, container, false);
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FillFields ff = new FillFields();
        ff.execute();
        Button b = view.findViewById(R.id.btnSubmit);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: UPDATE USER INFO IF CHANGED
            }
        });
    }

    private class FillFields extends AsyncTask<Void, Void, User> {

        @Override
        public User doInBackground(Void...args){
            User  profile = new User();
            DBManager dbManager = new DBManager();
            String token = SessionManager.getToken(getView().getContext());
            profile.parseJson(dbManager.getUser(token, ""));
            return profile;
        }

        @Override
        public void onPostExecute(User profile){
            profile.getUserType();
            if(profile.getUserType()!= 2) {
                FrameLayout fl = getView().findViewById(R.id.krafterFrame);
                View v = getLayoutInflater().inflate(R.layout.fragment_update_krafter_profile, fl, true);
                //must set these fields
            }

            TextView txtUsername = getView().findViewById(R.id.txtUsername);
            txtUsername.setText(profile.getUsername());

            ImageView imgProfile = getView().findViewById(R.id.imgProfile);
            if (profile.getBmp() != null) {
                imgProfile.setImageBitmap(profile.getBmp());
            } else {
                imgProfile.setBackgroundColor(Color.rgb(188, 225, 232));
            }

            TextView tvDateJoined = getView().findViewById(R.id.txtDateJoined);
            if (profile.getDateJoined() != null) {
                tvDateJoined.setText("Since " + profile.getDateJoined());
            } else {
                tvDateJoined.setText("Since ");
            }

            EditText etFirst = getView().findViewById(R.id.etFirst);
            if (profile.getFirst() != null) {
                etFirst.setText(profile.getFirst());
            } else {
                etFirst.setText("");
            }

            EditText etLast = getView().findViewById(R.id.etLast);
            if (profile.getLast() != null) {
                etLast.setText(profile.getLast());
            } else {
                etLast.setText("");
            }

            EditText etCity = getView().findViewById(R.id.etCity);
            if (profile.getCity() != null) {
                etCity.setText(profile.getCity());
            } else {
                etCity.setText("");
            }

            EditText etState = getView().findViewById(R.id.etState);
            if(profile.getState() != null){
                etState.setText(profile.getState());
            } else {
                etState.setText("");
            }

            EditText etBio = getView().findViewById(R.id.etBio);
            if(profile.getBio() != null){
                etBio.setText(profile.getBio());
            } else {
                etBio.setText("");
            }
        }
    }
}
