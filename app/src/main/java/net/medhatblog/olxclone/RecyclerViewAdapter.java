package net.medhatblog.olxclone;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by AndroidJSon.com on 6/18/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<AdUploadInfo> MainImageUploadInfoList;

    public RecyclerViewAdapter(Context context, List<AdUploadInfo> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AdUploadInfo UploadInfo = MainImageUploadInfoList.get(position);

        holder.priceView.setText(UploadInfo.adPrice+" EGP");
        holder.imageNameTextView.setText(UploadInfo.adTitle);
        Picasso.with(context).load(UploadInfo.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener{

        public ImageView imageView;
        public TextView imageNameTextView;
        public TextView priceView;
        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);

            imageNameTextView = (TextView) itemView.findViewById(R.id.ImageNameTextView);

            priceView = (TextView) itemView.findViewById(R.id.PriceTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            AdUploadInfo UploadInfo = MainImageUploadInfoList.get(getAdapterPosition());
            Intent intent = new Intent(v.getContext(),ViewAdDetails.class);
            intent.putExtra("AdDetails",UploadInfo);
            v.getContext().startActivity(intent);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
