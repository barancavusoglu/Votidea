package com.bcmobileappdevelopment.votidea.Adapters;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcmobileappdevelopment.votidea.GsonResponse.GetCommentsResponse;
import com.bcmobileappdevelopment.votidea.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CommentAdapter extends BaseQuickAdapter<GetCommentsResponse.CommentViewListBean,BaseViewHolder> {


    public CommentAdapter(int layout, List data)
    {
        super(layout,data);
    }

    RequestOptions requestOptions;

    @Override
    protected void convert(BaseViewHolder viewHolder, GetCommentsResponse.CommentViewListBean item) {
        viewHolder
                .addOnLongClickListener(R.id.list_item_comment_layout)
                .addOnClickListener(R.id.ivProfilePic)
                .addOnClickListener(R.id.tvUsernameButton);
        TextView tvComment = viewHolder.getView(R.id.tvComment);
        TextView tvUsernameButton = viewHolder.getView(R.id.tvUsernameButton);

        String username = item.getUsername();
        String comment = item.getDescription();
        byte[] data = Base64.decode(comment, Base64.DEFAULT);
        try{
            String emojiCommment = new String(data, "UTF-8");
            SpannableString spannableString = new SpannableString(username);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD),0,spannableString.length(),0);
            tvUsernameButton.setText(spannableString);
            tvComment.setText(spannableString);
            tvComment.append(" ");
            tvComment.append(emojiCommment);
        }
        catch (Exception ex){

        }
        requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new CircleCrop());

        viewHolder.setText(R.id.tvDate,item.getDate());

        Glide.with(mContext).load(item.getProfilePicURL()).apply(requestOptions).into((ImageView) viewHolder.getView(R.id.ivProfilePic));

        try {
            //Locale current = mContext.getResources().getConfiguration().locale;
            //DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, current);
            //Date myDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(item.getDate());
            //String date = dateFormat.format(myDate); // 8 Eki 2018, Oct 8, 2018
            //String time = new SimpleDateFormat(" HH:mm").format(myDate); //08:18
            //viewHolder.setText(R.id.tvDate,date+time);

            Locale current = mContext.getResources().getConfiguration().locale;
            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, current);
            dateFormat.setTimeZone(TimeZone.getDefault());
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = simpleDateFormat1.parse(item.getDate());
            String date = dateFormat.format(myDate); // 8 Eki 2018, Oct 8, 2018
            String time = new SimpleDateFormat(" HH:mm").format(myDate); //08:18
            viewHolder.setText(R.id.tvDate,date+time);

        }catch (Exception ex){
            Log.d("tarja","exception"+ex.getMessage());
        }
    }
}
