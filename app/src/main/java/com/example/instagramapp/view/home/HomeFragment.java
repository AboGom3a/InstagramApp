package com.example.instagramapp.view.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.instagramapp.R;
import com.example.instagramapp.model.Post;
import com.example.instagramapp.model.User;
import com.example.instagramapp.utils.OnLikeClicked;
import com.example.instagramapp.view.home.post.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment  implements OnLikeClicked {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ArrayList<Post> arrposts;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private Query query;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ;
        setupView(view);
        getPosts();
        return view;
    }

    private void setupView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);

        arrposts = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Posts");

        postAdapter = new PostAdapter(HomeFragment.this,arrposts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(postAdapter);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
    }

    private void getPosts() {
// Read from the database
/*
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=user.getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        query = db.child("Posts").orderByChild("userId").equalTo(current_uid);
*/
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();
                    final Post post = snapshot.getValue(Post.class);
                    post.setId(id);
                    DatabaseReference usersRef = database.getReference("Users").child(post.getUserId());
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            post.setUser(user);
                            arrposts.add(post);
                            postAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
    }

    @Override
    public void onLikeClicked(final int position) {
            Post post = arrposts.get(position);

            final DatabaseReference likeRef = database.getReference("UserLikes").child(firebaseUser.getUid()).child(post.getId()).child("didLike");

            final DatabaseReference myRef = database.getReference("Posts").child(post.getId()).child("numberOfLikes");
            final int numberOfLiked = post.getNumberOfLikes();

            likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean didLike = dataSnapshot.getValue(Boolean.class);
                    if (didLike != null && didLike) {
                        myRef.setValue(numberOfLiked - 1);
                        arrposts.get(position).setNumberOfLikes(numberOfLiked - 1);

                        likeRef.setValue(false);

                    } else {
                        myRef.setValue(numberOfLiked + 1);
                        arrposts.get(position).setNumberOfLikes(numberOfLiked + 1);

                        likeRef.setValue(true);
                    }
                    postAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


}
