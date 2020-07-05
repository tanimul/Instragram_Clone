package com.example.instragramclone.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instragramclone.AddStoryActivity;
import com.example.instragramclone.Model.Story;
import com.example.instragramclone.Model.User;
import com.example.instragramclone.R;
import com.example.instragramclone.StoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    private Context context;
    private List<Story> mstory;

    public StoryAdapter(Context context, List<Story> mstory) {
        this.context = context;
        this.mstory = mstory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        if (position == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.addstory_item, parent, false);
            return new StoryAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.story_item, parent, false);
            return new StoryAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Story story = mstory.get(position);
        userInfo(holder, story.getUserid(), position);
        if (holder.getAdapterPosition() != 0) {
            seenStory(holder, story.getUserid());
        }
        if (holder.getAdapterPosition() == 0) {
            mystory(holder.addstory_text, holder.story_plus, false);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() == 0) {
                    mystory(holder.addstory_text, holder.story_plus, true);
                } else {
                    Intent intent=new Intent(context, StoryActivity.class);
                    intent.putExtra("userid",story.getUserid());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mstory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView story_photo, story_plus, story_photoseen;
        public TextView story_username, addstory_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            story_photo = itemView.findViewById(R.id.story_photo);
            story_plus = itemView.findViewById(R.id.story_plus);
            story_photoseen = itemView.findViewById(R.id.story_photo_seen);
            story_username = itemView.findViewById(R.id.storyusername);
            addstory_text = itemView.findViewById(R.id.addstory_text);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    private void userInfo(final ViewHolder viewHolder, final String userid, final int position) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(context).load(user.getImageurl()).into(viewHolder.story_photo);
                if (position != 0) {
                    Glide.with(context).load(user.getImageurl()).into(viewHolder.story_photoseen);
                    viewHolder.story_username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void mystory(final TextView textView, final ImageView imageView, final boolean click) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                long timecurrent = System.currentTimeMillis();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Story story = dataSnapshot1.getValue(Story.class);
                    if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                        count += 1;
                    }
                }
                if (click) {
                    if (count > 0) {
                        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View Story",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent=new Intent(context, StoryActivity.class);
                                        intent.putExtra("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        context.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(context, AddStoryActivity.class);
                                        context.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });

                        alertDialog.show();
                    } else {
                        Intent intent = new Intent(context, AddStoryActivity.class);
                        context.startActivity(intent);
                    }
                } else {
                    if (count > 0) {
                        textView.setText("My Story");
                        imageView.setVisibility(View.GONE);
                    } else {
                        textView.setText("Add Story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seenStory(final ViewHolder viewHolder, final String userid) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Story").child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (!dataSnapshot1.child("views")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .exists() && System.currentTimeMillis() < dataSnapshot1.getValue(Story.class).getTimeend()) {
                        i++;
                    }
                }
                if (i > 0) {
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photoseen.setVisibility(View.GONE);
                } else {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photoseen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
