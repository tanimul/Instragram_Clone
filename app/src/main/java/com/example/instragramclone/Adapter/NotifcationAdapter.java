package com.example.instragramclone.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instragramclone.Fragment.PostDetailesFragment;
import com.example.instragramclone.Fragment.ProfileFragment;
import com.example.instragramclone.Model.Notification;
import com.example.instragramclone.Model.Post;
import com.example.instragramclone.Model.User;
import com.example.instragramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotifcationAdapter extends RecyclerView.Adapter<NotifcationAdapter.viewholder> {

    private Context context;
    private List<Notification> notifcations;

    public NotifcationAdapter(Context context, List<Notification> notifcations) {
        this.context = context;
        this.notifcations = notifcations;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notificationitem, parent, false);
        return new NotifcationAdapter.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        final Notification notification = notifcations.get(position);
        holder.text.setText(notification.getText());
        getuserInfo(holder.image_profile, holder.username, notification.getUserid());
        if (notification.isIspost()) {
            holder.post_image.setVisibility(View.VISIBLE);
            getpostimage(holder.post_image, notification.getPostid());
        }
        else {
            holder.post_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.isIspost()){
                    SharedPreferences.Editor editor=context.getSharedPreferences("PREPS",Context.MODE_PRIVATE).edit();
                    editor.putString("postid",notification.getPostid());
                    editor.apply();

                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new PostDetailesFragment()).commit();
                }else {
                    SharedPreferences.Editor editor=context.getSharedPreferences("PREPS",Context.MODE_PRIVATE).edit();
                    editor.putString("postid",notification.getUserid());
                    editor.apply();

                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifcations.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        public ImageView image_profile, post_image;
        public TextView username, text;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.postimage);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);
        }
    }

    private void getuserInfo(final ImageView imageView, final TextView username, String publisherid) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(context).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getpostimage(final ImageView imageView, String postid) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Glide.with(context).load(post.getPostimage()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
