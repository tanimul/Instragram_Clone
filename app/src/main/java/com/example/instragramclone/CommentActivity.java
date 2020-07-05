package com.example.instragramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.instragramclone.Adapter.CommentAdapter;
import com.example.instragramclone.Model.Comment;
import com.example.instragramclone.Model.User;
import com.example.instragramclone.databinding.ActivityCommentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    private ActivityCommentBinding binding;

    String postid, publisherid;
    FirebaseUser firebaseUser;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Comments");

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList,postid);
        binding.recyclerView.setAdapter(commentAdapter);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.addComment.getText().toString().equals("")) {
                    Toast.makeText(CommentActivity.this, "can't sent empty comment", Toast.LENGTH_SHORT).show();
                } else {
                    addcomment();
                }
            }
        });
        getImage();
        readcomment();
    }

    private void addcomment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        String commentid=reference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", binding.addComment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        hashMap.put("commentid", commentid);

        reference.child(commentid).setValue(hashMap);
        addNotifications();
        binding.addComment.setText("");
    }

    private void addNotifications(){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","commented"+binding.addComment.getText().toString());
        hashMap.put("postid",postid);
        hashMap.put("ispost",true);
        databaseReference.push().setValue(hashMap);

    }

    private void getImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(binding.imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readcomment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Comment comment = dataSnapshot1.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
