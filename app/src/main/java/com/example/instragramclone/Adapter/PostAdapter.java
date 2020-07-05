package com.example.instragramclone.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.DefaultTaskExecutor;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instragramclone.CommentActivity;
import com.example.instragramclone.FollowerActivity;
import com.example.instragramclone.Fragment.PostDetailesFragment;
import com.example.instragramclone.Fragment.ProfileFragment;
import com.example.instragramclone.Model.Post;
import com.example.instragramclone.Model.User;
import com.example.instragramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context mcontext;
    public List<Post> mpost;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mcontext, List<Post> mpost) {
        this.mcontext = mcontext;
        this.mpost = mpost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.postitem, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mpost.get(position);
        Glide.with(mcontext).load(post.getPostimage())
                .apply(new RequestOptions().placeholder(R.drawable.ic_photo)).into(holder.postImage);

        if (post.getDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }
        PublisherInfo(holder.imageprofile, holder.username, holder.publisher, post.getPublisher());
        isLikes(post.getPostid(), holder.like);
        nlikes(holder.likes, post.getPostid());
        getcomments(post.getPostid(), holder.comments);
        issaved(post.getPostid(), holder.save);

        holder.imageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity) mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity) mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        holder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity) mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());
                editor.apply();

                ((FragmentActivity) mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PostDetailesFragment()).commit();
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).removeValue();
                }
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotifications(post.getPublisher(), post.getPostid());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, CommentActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                mcontext.startActivity(intent);
            }
        });
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, CommentActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                mcontext.startActivity(intent);
            }
        });

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, FollowerActivity.class);
                intent.putExtra("id", post.getPostid());
                intent.putExtra("title", "likes");
                mcontext.startActivity(intent);
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mcontext, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                editpost(post.getPostid());
                                return true;
                            case R.id.delete:
                                FirebaseDatabase.getInstance().getReference("Posts")
                                        .child(post.getPostid()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(mcontext, "deleted.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                return true;

                            case R.id.report:
                                Toast.makeText(mcontext, "report clcik", Toast.LENGTH_SHORT).show();
                                return true;

                            default:
                                return false;

                        }
                    }
                });

                popupMenu.inflate(R.menu.post_menu);
                if(!post.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mpost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageprofile, postImage, like, comment, save, more;
        public TextView username, likes, publisher, description, comments;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageprofile = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.postimage);
            comments = itemView.findViewById(R.id.comments);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            username = itemView.findViewById(R.id.username);
            save = itemView.findViewById(R.id.save);
            likes = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            more = itemView.findViewById(R.id.more);
        }
    }

    private void getcomments(String postid, final TextView comments) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("View All" + dataSnapshot.getChildrenCount() + "Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isLikes(String postid, final ImageView imageView) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_fav_foreground);
                    imageView.setTag("liked");

                } else {
                    imageView.setImageResource(R.drawable.ic_favorite);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNotifications(String userid, String postid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "liked your post");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        databaseReference.push().setValue(hashMap);

    }

    private void nlikes(final TextView likes, String postid) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(postid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount() + "likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void PublisherInfo(final ImageView imgprofile, final TextView username, final TextView publisher, final String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mcontext).load(user.getImageurl()).into(imgprofile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void issaved(final String postid, final ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()) {
                    imageView.setImageResource(R.drawable.ic_bookmark2);
                    imageView.setTag("saved");


                } else {
                    imageView.setImageResource(R.drawable.ic_bookmark);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void editpost(final String postid) {
        AlertDialog.Builder alertdialoge = new AlertDialog.Builder(mcontext);
        alertdialoge.setTitle("Edit Post");

        final EditText editText = new EditText(mcontext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(layoutParams);
        alertdialoge.setView(editText);

        gettext(postid, editText);

        alertdialoge.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("description", editText.getText().toString());

                FirebaseDatabase.getInstance().getReference("Posts")
                        .child(postid).updateChildren(hashMap);
            }
        });
        alertdialoge.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertdialoge.show();


    }

    private void gettext(String postid, final EditText editText) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(Post.class).getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
