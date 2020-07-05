package com.example.instragramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instragramclone.Model.Story;
import com.example.instragramclone.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter = 0;
    long presstime = 0L;
    long limit = 500L;

    StoriesProgressView storiesProgressView;
    ImageView image, storyphoto;
    TextView story_username;

    LinearLayout r_seen;
    TextView seen_number;
    ImageView story_delete;

    List<String> images;
    List<String> storyids;
    String userid;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    presstime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;

                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - presstime;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        r_seen = findViewById(R.id.rseen);
        story_delete = findViewById(R.id.storydelete);
        seen_number = findViewById(R.id.seennumber);

        storiesProgressView = findViewById(R.id.stories);
        image = findViewById(R.id.image);
        storyphoto = findViewById(R.id.storyphoto);
        story_username = findViewById(R.id.storieusername);

        r_seen.setVisibility(View.GONE);
        story_delete.setVisibility(View.GONE);

        userid = getIntent().getStringExtra("userid");
        if (userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            r_seen.setVisibility(View.VISIBLE);
            story_delete.setVisibility(View.VISIBLE);
        }

        Log.d("ddddd", "" + userid);

        getStories(userid);
        userInfo(userid);

        View reverse = findViewById(R.id.reverse);
        View skip = findViewById(R.id.skip);

        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });

        reverse.setOnTouchListener(onTouchListener);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });

        skip.setOnTouchListener(onTouchListener);

        r_seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoryActivity.this, FollowerActivity.class);
                intent.putExtra("id", userid);
                intent.putExtra("storyid", storyids.get(counter));
                intent.putExtra("title", "views");
                startActivity(intent);

            }
        });

        story_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Story")
                        .child(userid).child(storyids.get(counter));
                databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(StoryActivity.this, "Deleted.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    private void addView(String storyid) {
        FirebaseDatabase.getInstance().getReference("Story").child(userid)
                .child(storyid).child("views").child(FirebaseAuth.getInstance().getCurrentUser()
                .getUid()).setValue(true);
    }

    private void seennumber(String storyid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Story")
                .child(userid).child(storyid).child("views");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                seen_number.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onNext() {
        Glide.with(getApplicationContext()).load(images.get(++counter)).into(image);

        addView(storyids.get(counter));
        seennumber(storyids.get(counter));

    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        Glide.with(getApplicationContext()).load(images.get(--counter)).into(image);

        seennumber(storyids.get(counter));

    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    private void getStories(String userid) {
        images = new ArrayList<>();
        storyids = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                images.clear();
                storyids.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Story story = snapshot.getValue(Story.class);
                    long timecurrent = System.currentTimeMillis();
                    if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                        images.add(story.getImageurl());
                        storyids.add(story.getStoryid());
                    }
                }

                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(StoryActivity.this);
                storiesProgressView.startStories(counter);

                Glide.with(getApplicationContext()).load(images.get(counter))
                        .into(image);

                addView(storyids.get(counter));
                seennumber(storyids.get(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void userInfo(String userid) {
        Log.d("dddd", "userid:" + userid);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl())
                        .into(storyphoto);
                story_username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
