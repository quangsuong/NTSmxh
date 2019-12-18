package net.blogsv.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import net.blogsv.model.User;
import net.blogsv.ntsmxh.R;
import net.blogsv.ui.Activities.UserActivity;

import java.util.List;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.SearchUserHolder> {
    private List<User> userList;
    private Activity activity;

    public SearchUserAdapter(List<User> userList, Activity activity) {
        this.userList = userList;
        this.activity = activity;
    }

    @Override
    public SearchUserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_search, null);
        SearchUserHolder mh = new SearchUserHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SearchUserHolder holder, final int position) {
        if (!userList.get(position).getImage().isEmpty()) {
            Picasso.get().load(userList.get(position).getImage()).error(R.drawable.profile).placeholder(R.drawable.profile).into(holder.image_view_follow_iten);
        } else {
            Picasso.get().load(R.drawable.profile).error(R.drawable.profile).placeholder(R.drawable.profile).into(holder.image_view_follow_iten);
        }
        holder.text_view_follow_itme_label.setText(userList.get(position).getName());
        holder.relative_layout_subscribte_item.setOnClickListener(v -> {
            Intent intent = new Intent(activity.getApplicationContext(), UserActivity.class);
            intent.putExtra("id", userList.get(position).getId());
            intent.putExtra("image", userList.get(position).getImage());
            intent.putExtra("name", userList.get(position).getName());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.enter, R.anim.exit);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class SearchUserHolder extends RecyclerView.ViewHolder {

        private final ImageView image_view_follow_iten;
        private final TextView text_view_follow_itme_label;
        private final RelativeLayout relative_layout_subscribte_item;

        public SearchUserHolder(View itemView) {
            super(itemView);
            this.relative_layout_subscribte_item = (RelativeLayout) itemView.findViewById(R.id.relative_layout_subscribte_item);
            this.image_view_follow_iten = (ImageView) itemView.findViewById(R.id.image_view_follow_iten);
            this.text_view_follow_itme_label = (TextView) itemView.findViewById(R.id.text_view_follow_itme_label);
        }
    }
}


