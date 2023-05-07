package com.example.yogawiththeyogamom;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class SubscriptionTypeAdapter extends RecyclerView.Adapter<SubscriptionTypeAdapter.ViewHolder> implements Filterable {
    private ArrayList<SubscriptionType> mSubscriptionTypeData;
    private final ArrayList<SubscriptionType> mSubscriptionTypeDataAll;
    private final Context mContext;
    private int lastPosition = -1;
    private final Filter subscriptionFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<SubscriptionType> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0) {
                results.count = mSubscriptionTypeDataAll.size();
                results.values = mSubscriptionTypeDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (SubscriptionType type : mSubscriptionTypeDataAll) {
                    if (type.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(type);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mSubscriptionTypeData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    public SubscriptionTypeAdapter(Context context, ArrayList<SubscriptionType> typeData) {
        this.mSubscriptionTypeData = typeData;
        this.mSubscriptionTypeDataAll = typeData;
        this.mContext = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SubscriptionTypeAdapter.ViewHolder holder, int position) {
        SubscriptionType currentSubType = mSubscriptionTypeData.get(position);

        holder.bindTo(currentSubType);

        if (holder.getBindingAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getBindingAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mSubscriptionTypeData.size();
    }

    @Override
    public Filter getFilter() {
        return subscriptionFilter;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTitleText;
        private final TextView mInfoText;
        private final TextView mPriceText;
        private final ImageView mSubImage;
        private final RatingBar mRatingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleText = itemView.findViewById(R.id.ItemTitle);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mPriceText = itemView.findViewById(R.id.price);
            mSubImage = itemView.findViewById(R.id.itemImage);
            mRatingBar = itemView.findViewById(R.id.ratingBar);


        }

        public void bindTo(SubscriptionType currentSubType) {
            mTitleText.setText(currentSubType.getName());
            mInfoText.setText(currentSubType.getInfo());
            mPriceText.setText(currentSubType.getPrice());
            mRatingBar.setRating(currentSubType.getRatedInfo());

            Glide.with(mContext).load(currentSubType.getImageResource()).into(mSubImage);
            itemView.findViewById(R.id.buy_button).setOnClickListener(view -> {
                Log.d("Activity", "Buy button clicked");
                ((SubscriptionListActivity) mContext).updateAlerts(currentSubType);
            });
            itemView.findViewById(R.id.delete_button).setOnClickListener(view ->
                    ((SubscriptionListActivity) mContext).deleteItem(currentSubType));

        }
    }
}