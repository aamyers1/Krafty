package com.team6.krafty;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FillFields ff = new FillFields();
        ff.execute();
    }


    private class FillFields extends AsyncTask<Void, Void, User> {

        @Override
        public User doInBackground(Void... args) {
            User profile = new User();
            DBManager dbManager = new DBManager();
            String token = SessionManager.getToken(getView().getContext());
            profile.parseJson(dbManager.getUser(token, ""));
            return profile;
        }

        @Override
        public void onPostExecute(User profile) {
            profile.getUserType();

            TextView txtUsername = getView().findViewById(R.id.Username);
            txtUsername.setText(profile.getUsername());

            ImageView imgProfile = getView().findViewById(R.id.imgProfile);
            if (profile.getBmp() != null) {
                imgProfile.setImageBitmap(profile.getBmp());
            } else {
                imgProfile.setBackgroundColor(Color.rgb(188, 225, 232));
            }

            TextView tvDateJoined = getView().findViewById(R.id.dateJoined);
            if (profile.getDateJoined() != null) {
                tvDateJoined.setText("Since " + profile.getDateJoined());
            } else {
                tvDateJoined.setText("");
            }

            TextView etFirst = getView().findViewById(R.id.Name);
            if (profile.getFirst() != null) {
                etFirst.setText(profile.getFirst() + " " + profile.getLast());
            } else {
                etFirst.setText("");
            }

            TextView etAddress = getView().findViewById(R.id.Address);
            if (profile.getState() != null) {
                etAddress.setText(profile.getCity() + ", " + profile.getState());
            } else {
                etAddress.setText("");
            }

            TextView etBio = getView().findViewById(R.id.Description);
            if (profile.getBio() != null) {
                etBio.setText(profile.getBio());
            } else {
                etBio.setText("Bio");
            }
        }
    }
}






