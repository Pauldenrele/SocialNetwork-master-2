package com.example.paul.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView SearchResultList;
    private EditText SearchInputText;
    private ImageButton SearchButton;
    private DatabaseReference allUsersDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

/**
 * allUserDatabaseRef gets the users registered
 */
        allUsersDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Users");
       allUsersDatabaseRef.keepSynced(true);

        /**
         * Toolbar
         */
        mToolbar = (Toolbar)findViewById(R.id.find_friends_appbar_layout);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

        /**
         * recyclerView for the search result
         */

        SearchResultList=(RecyclerView)findViewById(R.id.search_result_list);
       SearchResultList.setHasFixedSize(true);
       SearchResultList.setLayoutManager(new LinearLayoutManager(this));

        /**
         * Init for the search button and editText
         */
       SearchInputText=(EditText)findViewById(R.id.search_box_input);
       SearchButton=(ImageButton)findViewById(R.id.search_people_friends_button);


       SearchButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String SearchBoxInput= SearchInputText.getText().toString();
               SearchPeopleAndFriends(SearchBoxInput);
           }
       });


    }


    private void SearchPeopleAndFriends(String searchBoxInput) {


        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();

        /**
         * Query for the search button
         */
        Query searchPeopleandFriendsQuery =allUsersDatabaseRef.orderByChild("fullname")
                .startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");

        /**
         * FirebaseRecycleradapter which is holding the findfriends class and the viewHolder
         */
        FirebaseRecyclerAdapter<FindFriends ,FindFriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(

                        /**
                         * In a recyclerAdapter
                         *
                         * The class for the(getter and setter)
                         *
                         * The layout for the user
                         *
                         * The ViewHolder class
                         *
                         * the Query in connection with the firebase users
                         */
                        FindFriends.class,
                        R.layout.all_user_display_layout,
                        FindFriendsViewHolder.class,
                        searchPeopleandFriendsQuery

                ) {
                    @Override
                    protected void populateViewHolder(FindFriendsViewHolder viewHolder, FindFriends model, final int position) {

                        /**
                         * The viewHolder that holds the layout using the itemview and gets the value from the findfriends class
                         */
                        viewHolder.setFullname(model.getFullname());
                        viewHolder.setStatus(model.getStatus());
                        viewHolder.setProfileimage(getApplicationContext() , model.getProfileimage());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_user_id = getRef(position).getKey();
                                Intent profileIntent = new Intent(FindFriendsActivity.this , PersonProfileActivity.class);
                                profileIntent.putExtra("visit_user_id" , visit_user_id);
                                startActivity(profileIntent);
                            }
                        });
                    }
                };
            SearchResultList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FindFriendsViewHolder extends  RecyclerView.ViewHolder{
        View mView;


        public FindFriendsViewHolder(View itemView) {
            super(itemView);

   mView=itemView;

        }
        public void setProfileimage(final Context ctx , final String profileimage){
            final CircleImageView myImage = (CircleImageView)mView.findViewById(R.id.all_users_profile_image);
            Picasso.with(ctx).load(profileimage).placeholder(R.drawable.profile).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(myImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(profileimage).placeholder(R.drawable.profile).into(myImage);
                        }
                    });

        }
        public void setFullname(String fullname) {
            TextView myName = (TextView)mView.findViewById(R.id.all_user_profile_full_name);
            myName.setText(fullname);
        }

        public void setStatus(String status) {
            TextView myStatus = (TextView)mView.findViewById(R.id.all_user_status);
            myStatus.setText(status);
        }




    }
}
