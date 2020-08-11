package com.bcmobileappdevelopment.votidea.Adapters;

import android.widget.ImageView;
import android.widget.TextView;

import com.bcmobileappdevelopment.votidea.GsonResponse.GetTopUserResponse;
import com.bcmobileappdevelopment.votidea.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class TopVotersAdapter extends BaseQuickAdapter<GetTopUserResponse.TopResultsBean,BaseViewHolder> {

    public TopVotersAdapter(int layout, List data)
    {
        super(layout,data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, GetTopUserResponse.TopResultsBean item) {
        viewHolder.addOnClickListener(R.id.topVotersLayout);
        TextView tvUsername, tvNameSurname, tvCount;
        ImageView ivProfilePic;

        tvNameSurname = viewHolder.getView(R.id.tvNameSurname);
        tvUsername = viewHolder.getView(R.id.tvUsername);
        ivProfilePic = viewHolder.getView(R.id.ivProfilePic);
        tvCount = viewHolder.getView(R.id.tvCount);

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new CircleCrop());

        tvNameSurname.setText(item.getUser().getNameSurname());
        tvUsername.setText(item.getUser().getUsername());
        tvCount.setText(String.valueOf(item.getCount()));

        Glide.with(mContext).load(item.getUser().getProfilePicURL()).apply(requestOptions).into(ivProfilePic);
    }
}
