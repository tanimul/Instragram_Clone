package com.example.instragramclone.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instragramclone.Fragment.PostDetailesFragment;
import com.example.instragramclone.Model.Post;
import com.example.instragramclone.R;

import java.util.List;

public class MyphotoAdapter extends RecyclerView.Adapter<MyphotoAdapter.ViewHolder> {
    private Context context;
    private List<Post> mposts;

    public MyphotoAdapter(Context context, List<Post> mposts) {
        this.context = context;
        this.mposts = mposts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photositem, parent, false);
        return new MyphotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post = mposts.get(position);
        Glide.with(context).load(post.getPostimage()).into(holder.post_Image);

        holder.post_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PostDetailesFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mposts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView post_Image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_Image = itemView.findViewById(R.id.postimage);
        }
    }
}
