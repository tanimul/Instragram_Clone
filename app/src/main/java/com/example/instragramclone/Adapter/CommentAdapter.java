package com.example.instragramclone.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instragramclone.MainActivity;
import com.example.instragramclone.Model.Comment;
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

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.Viewholder> {

    private Context mContext;
    private List<Comment> mComment;
    private FirebaseUser firebaseUser;
    private String postid;

    public CommentAdapter(Context mContext, List<Comment> mComment,String postid) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.postid = postid;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.commentitem, parent, false);
        return new CommentAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = mComment.get(position);
        holder.comment.setText(comment.getComment());
        getUserInfo(holder.imageprofile,holder.username,comment.getPublisher());

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherid",comment.getPublisher());
                mContext.startActivity(intent);
            }
        });
        holder.imageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherid",comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(comment.getPublisher().equals(firebaseUser.getUid())){
                    AlertDialog alertDialog=new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Do you want to delete");

                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          FirebaseDatabase .getInstance().getReference("Comments")
                                  .child(postid).child(comment.getCommentid())
                                  .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  if(task.isSuccessful()){
                                      Toast.makeText(mContext, "Deleted.", Toast.LENGTH_SHORT).show();
                                  }
                              }
                          });
                          dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        public ImageView imageprofile;
        public TextView username, comment;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            imageprofile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }


    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
