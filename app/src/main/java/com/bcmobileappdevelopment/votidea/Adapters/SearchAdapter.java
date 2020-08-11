package com.bcmobileappdevelopment.votidea.Adapters;

import android.widget.ImageView;
import android.widget.TextView;

import com.bcmobileappdevelopment.votidea.GsonResponse.SearchUserResponse;
import com.bcmobileappdevelopment.votidea.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class SearchAdapter extends BaseQuickAdapter<SearchUserResponse.UserListBean,BaseViewHolder> {

    public SearchAdapter(int layout, List data)
    {
        super(layout,data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, SearchUserResponse.UserListBean item) {
        viewHolder.addOnClickListener(R.id.searchLayout);
        TextView tvUsername, tvNameSurname;
        ImageView ivProfilePic;

        tvNameSurname = viewHolder.getView(R.id.tvNameSurname);
        tvUsername = viewHolder.getView(R.id.tvUsername);
        ivProfilePic = viewHolder.getView(R.id.ivProfilePic);

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new CircleCrop());

        tvNameSurname.setText(item.getNameSurname());
        tvUsername.setText(item.getUsername());

        Glide.with(mContext).load(item.getProfilePicURL()).apply(requestOptions).into(ivProfilePic);
    }
}
