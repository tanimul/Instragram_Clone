package com.example.instragramclone.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.instragramclone.Adapter.PostAdapter;
import com.example.instragramclone.Adapter.StoryAdapter;
import com.example.instragramclone.Model.Post;
import com.example.instragramclone.Model.Story;
import com.example.instragramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;
    private List<String> followingList;
    ProgressBar progressBar;

    private RecyclerView recyclerView_story;
    private List<Story>storylist;
    private StoryAdapter storyAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postLists);
        recyclerView.setAdapter(postAdapter);


        recyclerView_story = view.findViewById(R.id.recycler_view_stories);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false);

        recyclerView_story.setLayoutManager(linearLayoutManager1);
        storylist = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storylist);
        recyclerView_story.setAdapter(storyAdapter);

        progressBar=view.findViewById(R.id.progress_circular);

        checkfollwing();
        return view;
    }

    private void checkfollwing() {
        followingList = new ArrayList<>();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    followingList.add(snapshot.getKey());
                }
                readPosts();
                readStory();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postLists.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Post post = dataSnapshot1.getValue(Post.class);
                    for (String id : followingList) {
                        if (post.getPublisher().equals(id)) {
                            postLists.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void  readStory(){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Story");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long timecurrent=System.currentTimeMillis();
                storylist.clear();
                storylist.add(new Story("",0,0,"",FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for (String id :followingList){
                    Story story=null;
                    int countstory=0;
                    for (DataSnapshot dataSnapshot1:dataSnapshot.child(id).getChildren()){
                        story=dataSnapshot1.getValue(Story.class);
                        if(timecurrent>story.getTimestart() && timecurrent<story.getTimeend()){
                            countstory++;
                        }
                    }
                    if(countstory>0){
                        storylist.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
