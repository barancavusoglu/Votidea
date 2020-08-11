package com.bcmobileappdevelopment.votidea.Adapters;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetVotingRecordsProfileResponse;
import com.bcmobileappdevelopment.votidea.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import mehdi.sakout.fancybuttons.FancyButton;

public class ProfileVotingAdapter extends BaseQuickAdapter<GetVotingRecordsProfileResponse.ProfileVotingListBean,BaseViewHolder> {

    RequestOptions requestOptions,profilePicRequestOptions;
    public ProfileVotingAdapter(int layout, List data){
        super(layout,data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, GetVotingRecordsProfileResponse.ProfileVotingListBean item) {

        ImageView iv1Check = viewHolder.getView(R.id.iv1Check);
        ImageView iv2Check = viewHolder.getView(R.id.iv2Check);
        ImageView iv3Check = viewHolder.getView(R.id.iv3Check);
        ImageView iv4Check = viewHolder.getView(R.id.iv4Check);

        FancyButton tvState = viewHolder.getView(R.id.tvState);
        FancyButton tvIsAnonymous = viewHolder.getView(R.id.tvIsAnonymous);
        FancyButton btVoteCount = viewHolder.getView(R.id.btVoteCount);
        View line = viewHolder.getView(R.id.line);

        int voteCount = 0;

        FancyButton btComments = viewHolder.getView(R.id.btComments);
        TextView tvDesc = viewHolder.getView(R.id.tvDesc);

        TextView tv1Perc = viewHolder.getView(R.id.tv1perc);
        TextView tv2Perc = viewHolder.getView(R.id.tv2perc);
        TextView tv3Perc = viewHolder.getView(R.id.tv3perc);
        TextView tv4Perc = viewHolder.getView(R.id.tv4perc);

        ImageView iv3 = viewHolder.getView(R.id.iv3);
        ImageView iv4 = viewHolder.getView(R.id.iv4);

        RoundCornerProgressBar pBar1 = viewHolder.getView(R.id.pBar1);
        pBar1.setProgressColor(Color.parseColor("#61FF59"));
        pBar1.setProgressBackgroundColor(Color.parseColor("#757575"));
        RoundCornerProgressBar pBar2 = viewHolder.getView(R.id.pBar2);
        pBar2.setProgressColor(Color.parseColor("#61FF59"));
        pBar2.setProgressBackgroundColor(Color.parseColor("#757575"));
        RoundCornerProgressBar pBar3 = viewHolder.getView(R.id.pBar3);
        pBar3.setProgressColor(Color.parseColor("#61FF59"));
        pBar3.setProgressBackgroundColor(Color.parseColor("#757575"));
        RoundCornerProgressBar pBar4 = viewHolder.getView(R.id.pBar4);
        pBar4.setProgressColor(Color.parseColor("#61FF59"));
        pBar4.setProgressBackgroundColor(Color.parseColor("#757575"));
        pBar1.setVisibility(View.VISIBLE);
        pBar2.setVisibility(View.VISIBLE);
        pBar3.setVisibility(View.VISIBLE);
        pBar4.setVisibility(View.VISIBLE);
        iv1Check.setVisibility(View.GONE);
        iv2Check.setVisibility(View.GONE);
        iv3Check.setVisibility(View.GONE);
        iv4Check.setVisibility(View.GONE);
        tv1Perc.setVisibility(View.GONE);
        tv2Perc.setVisibility(View.GONE);
        tv3Perc.setVisibility(View.GONE);
        tv4Perc.setVisibility(View.GONE);

        requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new RoundedCorners(20));
        //profilePicRequestOptions = new RequestOptions()
        //        .placeholder(R.drawable.yukleniyorgif)
        //        .circleCrop();
        tvDesc.setVisibility(View.VISIBLE);
        if (item.getProfileVoting().getDescription().isEmpty())
        {
            tvDesc.setVisibility(View.GONE);
        }

        if (!item.getProfileVoting().isIsCommentAllowed()){
            btComments.setText(mContext.getResources().getString(R.string.commentsDisabled));
        }
        else
        {
            viewHolder.addOnClickListener(R.id.btComments);
            if(item.getProfileVoting().getCommentCount() == 0)
            {
                btComments.setText(mContext.getResources().getString(R.string.thereAreNoComments));
            }
            else{
                btComments.setText(mContext.getResources().getString(R.string.seeComments) + " (" +item.getProfileVoting().getCommentCount()+")");
            }
        }

        viewHolder
                .addOnClickListener(R.id.iv1)
                .addOnClickListener(R.id.iv2)
                .addOnClickListener(R.id.iv3)
                .addOnClickListener(R.id.iv4)
                .addOnClickListener(R.id.btVoteOptions);


        if (item.getProfileVoting().getStateID() == 1){
            tvState.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }
        else if(item.getProfileVoting().getStateID() == 2){
            tvState.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            tvState.setText(mContext.getString(R.string.rejected));
            tvState.setTextColor(mContext.getResources().getColor(R.color.red));
            tvState.setIconResource(R.drawable.ic_rejected_red);
            tvState.setCustomTextFont("");
        }
        else if(item.getProfileVoting().getStateID() == 3){
            tvState.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            tvState.setText(mContext.getString(R.string.waiting_approval));
            tvState.setIconResource(R.drawable.ic_waiting_grey);
            tvState.setTextColor(mContext.getResources().getColor(R.color.grey));
        }
        else if(item.getProfileVoting().getStateID() == 4){
            tvState.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            tvState.setText(mContext.getString(R.string.unpublished));
            tvState.setIconResource(R.drawable.ic_unpublish_black);
            tvState.setTextColor(mContext.getResources().getColor(R.color.real_Black));
        }
        if (item.getProfileVoting().isIsAnonymous())
        {
            tvIsAnonymous.setTextColor(mContext.getResources().getColor(R.color.real_Black));
            tvIsAnonymous.setIconResource(R.drawable.ic_incognito);
            tvIsAnonymous.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        }
        else{
            tvIsAnonymous.setVisibility(View.GONE);
        }

        try {
            //Locale current = mContext.getResources().getConfiguration().locale;
            //DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, current);
            //Date myDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(item.getProfileVoting().getCreationDate());
            //String date = dateFormat.format(myDate); // 8 Eki 2018, Oct 8, 2018
            //String time = new SimpleDateFormat(" HH:mm").format(myDate); //08:18
            //viewHolder.setText(R.id.tvDate,date+time);

            Locale current = mContext.getResources().getConfiguration().locale;
            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, current);
            dateFormat.setTimeZone(TimeZone.getDefault());
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = simpleDateFormat1.parse(item.getProfileVoting().getCreationDate());
            String date = dateFormat.format(myDate); // 8 Eki 2018, Oct 8, 2018
            String time = new SimpleDateFormat(" HH:mm").format(myDate); //08:18
            viewHolder.setText(R.id.tvDate,date+time);

        }catch (Exception ex){
            Log.d("tarja","exception"+ex.getMessage());
        }
        try{
            //Glide.with(mContext).load(item.getMasterVotingView().getOwnerPicURL()).apply(profilePicRequestOptions).into((ImageView) viewHolder.getView(R.id.ivProfilePic));
            if (item.getChoiceList().size() == 2){
                Glide.with(mContext).load(item.getChoiceList().get(0).getPictureURL()).apply(requestOptions).into((ImageView) viewHolder.getView(R.id.iv1));
                Glide.with(mContext).load(item.getChoiceList().get(1).getPictureURL()).apply(requestOptions).into((ImageView) viewHolder.getView(R.id.iv2));
                //iv3.refreshDrawableState();
                //iv4.refreshDrawableState();
                Glide.with(mContext).load("").apply(requestOptions).into((ImageView) viewHolder.getView(R.id.iv3));
                Glide.with(mContext).load("").apply(requestOptions).into((ImageView) viewHolder.getView(R.id.iv4));

                voteCount = item.getChoiceList().get(0).getClickedCount() + item.getChoiceList().get(1).getClickedCount();

                iv3.setVisibility(View.GONE);
                iv4.setVisibility(View.GONE);


                int iv1Percentage = (int)item.getChoiceList().get(0).getPercentage();
                int iv2Percentage = (int)item.getChoiceList().get(1).getPercentage();

                pBar1.setProgress((float)iv1Percentage);
                pBar2.setProgress((float)iv2Percentage);
                pBar3.setVisibility(View.GONE);
                pBar4.setVisibility(View.GONE);

                tv1Perc.setText("%"+String.valueOf(iv1Percentage));
                tv2Perc.setText("%"+String.valueOf(iv2Percentage));
                tv1Perc.setVisibility(View.VISIBLE);
                tv2Perc.setVisibility(View.VISIBLE);

                if (iv1Percentage > iv2Percentage){
                    iv1Check.setVisibility(View.VISIBLE);
                }
                else if(iv2Percentage > iv1Percentage){
                    iv2Check.setVisibility(View.VISIBLE);
                }
            }
            else if(item.getChoiceList().size() == 3) {
                Glide.with(mContext).load(item.getChoiceList().get(0).getPictureURL()).apply(requestOptions).into((ImageView) viewHolder.getView(R.id.iv1));
                Glide.with(mContext).load(item.getChoiceList().get(1).getPictureURL()).apply(requestOptions).into((ImageView) viewHolder.getView(R.id.iv2));
                Glide.with(mContext).load(item.getChoiceList().get(2).getPictureURL()).apply(requestOptions).into((ImageView) viewHolder.getView(R.id.iv3));
                //iv4.refreshDrawableState();
                Glide.with(mContext).load("").apply(requestOptions).into((ImageView) viewHolder.getView(R.id.iv4));

                voteCount = item.getChoiceList().get(0).getClickedCount() + item.getChoiceList().get(1).getClickedCount() + item.getChoiceList().get(2).getClickedCount();

                iv3.setVisibility(View.VISIBLE);
                iv4.setVisibility(View.VISIBLE);

                int iv1Percentage = (int)item.getChoiceList().get(0).getPercentage();
                int iv2Percentage = (int)item.getChoiceList().get(1).getPercentage();
                int iv3Percentage = (int)item.getChoiceList().get(2).getPercentage();

                pBar1.setProgress((float)iv1Percentage);
                pBar2.setProgress((float)iv2Percentage);
                pBar3.setProgress((float)iv3Percentage);
                pBar4.setVisibility(View.GONE);

                tv1Perc.setText("%"+String.valueOf(iv1Percentage));
                tv2Perc.setText("%"+String.valueOf(iv2Percentage));
                tv3Perc.setText("%"+String.valueOf(iv3Percentage));
                tv1Perc.setVisibility(View.VISIBLE);
                tv2Perc.setVisibility(View.VISIBLE);
                tv3Perc.setVisibility(View.VISIBLE);

                if (iv1Percentage > iv2Percentage && iv1Percentage> iv3Percentage){
                    iv1Check.setVisibility(View.VISIBLE);
                }
                else if (iv2Percentage > iv1Percentage && iv2Percentage> iv3Percentage){
                    iv2Check.setVisibility(View.VISIBLE);
                }
                else if (iv3Percentage > iv1Percentage && iv3Percentage> iv2Percentage){
                    iv3Check.setVisibility(View.VISIBLE);
                }
            }
            else {
                Glide.with(mContext).load(item.getChoiceList().get(0).getPictureURL()).apply(requestOptions).into((ImageView) viewHolder.getView(R.id.iv1));
                Glide.with(mContext).load(item.getChoiceList().get(1).getPictureURL()).apply(requestOptions).into((ImageView) viewHolder.getView(R.id.iv2));
                Glide.with(mContext).load(item.getChoiceList().get(2).getPictureURL()).apply(requestOptions).into((ImageView) viewHolder.getView(R.id.iv3));
                Glide.with(mContext).load(item.getChoiceList().get(3).getPictureURL()).apply(requestOptions).into((ImageView) viewHolder.getView(R.id.iv4));

                voteCount = item.getChoiceList().get(0).getClickedCount() + item.getChoiceList().get(1).getClickedCount() + item.getChoiceList().get(2).getClickedCount() + item.getChoiceList().get(3).getClickedCount();

                iv3.setVisibility(View.VISIBLE);
                iv4.setVisibility(View.VISIBLE);

                int iv1Percentage = (int)item.getChoiceList().get(0).getPercentage();
                int iv2Percentage = (int)item.getChoiceList().get(1).getPercentage();
                int iv3Percentage = (int)item.getChoiceList().get(2).getPercentage();
                int iv4Percentage = (int)item.getChoiceList().get(3).getPercentage();

                pBar1.setProgress((float)iv1Percentage);
                pBar2.setProgress((float)iv2Percentage);
                pBar3.setProgress((float)iv3Percentage);
                pBar4.setProgress((float)iv4Percentage);

                tv1Perc.setText("%"+String.valueOf(iv1Percentage));
                tv2Perc.setText("%"+String.valueOf(iv2Percentage));
                tv3Perc.setText("%"+String.valueOf(iv3Percentage));
                tv4Perc.setText("%"+String.valueOf(iv4Percentage));
                tv1Perc.setVisibility(View.VISIBLE);
                tv2Perc.setVisibility(View.VISIBLE);
                tv3Perc.setVisibility(View.VISIBLE);
                tv4Perc.setVisibility(View.VISIBLE);

                if (iv1Percentage > iv2Percentage && iv1Percentage > iv3Percentage && iv1Percentage > iv4Percentage){
                    iv1Check.setVisibility(View.VISIBLE);
                }
                else if(iv2Percentage > iv1Percentage && iv2Percentage > iv3Percentage && iv2Percentage > iv4Percentage){
                    iv2Check.setVisibility(View.VISIBLE);
                }
                else if(iv3Percentage > iv1Percentage && iv3Percentage > iv2Percentage && iv3Percentage > iv4Percentage){
                    iv3Check.setVisibility(View.VISIBLE);
                }
                else if(iv4Percentage > iv1Percentage && iv4Percentage > iv2Percentage && iv4Percentage > iv3Percentage){
                    iv4Check.setVisibility(View.VISIBLE);
                }
            }
        }
        catch (OutOfMemoryError e)
        {
            Log.d("Tarja","Glide out of memory "+e.getMessage());
        }

        //Locale locale = new Locale("en", "US");
        //Locale locale = new Locale("tr", "TR");
        //DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        //String date = item.getProfileVoting().getCreationDate();
        //date = dateFormat.format(new Date());
        //String pattern = ", HH:mm";
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        //date += simpleDateFormat.format(new Date());
        btVoteCount.setText(String.valueOf(voteCount) + " "+mContext.getResources().getString(R.string.vote_count));
        viewHolder.setText(R.id.tvDesc,item.getProfileVoting().getDescription());
    }
}
