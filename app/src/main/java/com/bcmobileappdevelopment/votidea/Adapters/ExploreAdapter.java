package com.bcmobileappdevelopment.votidea.Adapters;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetMasterVotingRecordsExploreResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.LoginResponse;
import com.bcmobileappdevelopment.votidea.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.hawk.Hawk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import mehdi.sakout.fancybuttons.FancyButton;

public class ExploreAdapter extends BaseQuickAdapter<GetMasterVotingRecordsExploreResponse.MasterVotingExploreListBean,BaseViewHolder> {

    RequestOptions requestOptions,profilePicRequestOptions;
    public ExploreAdapter(int layout, List data){
        super(layout,data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, GetMasterVotingRecordsExploreResponse.MasterVotingExploreListBean item) {



        Hawk.init(mContext).build();
        ImageView iv1Check = viewHolder.getView(R.id.iv1Check);
        ImageView iv2Check = viewHolder.getView(R.id.iv2Check);
        ImageView iv3Check = viewHolder.getView(R.id.iv3Check);
        ImageView iv4Check = viewHolder.getView(R.id.iv4Check);
        FancyButton btVoteOptions = viewHolder.getView(R.id.btVoteOptions);
        FancyButton btComments = viewHolder.getView(R.id.btComments);
        FancyButton btVoteCount = viewHolder.getView(R.id.btVoteCount);
        ImageView ivLike1 = viewHolder.getView(R.id.ivLike1);
        ImageView ivLike2 = viewHolder.getView(R.id.ivLike2);
        ImageView ivLike3 = viewHolder.getView(R.id.ivLike3);
        ImageView ivLike4 = viewHolder.getView(R.id.ivLike4);

        int voteCount = 0;

        TextView tv1Perc = viewHolder.getView(R.id.tv1perc);
        TextView tv2Perc = viewHolder.getView(R.id.tv2perc);
        TextView tv3Perc = viewHolder.getView(R.id.tv3perc);
        TextView tv4Perc = viewHolder.getView(R.id.tv4perc);
        TextView tvDesc = viewHolder.getView(R.id.tvDesc);

        ImageView iv1 = viewHolder.getView(R.id.iv1);
        ImageView iv2 = viewHolder.getView(R.id.iv2);
        ImageView iv3 = viewHolder.getView(R.id.iv3);
        ImageView iv4 = viewHolder.getView(R.id.iv4);

        iv1.setVisibility(View.VISIBLE);
        iv2.setVisibility(View.VISIBLE);

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
        iv1Check.setVisibility(View.INVISIBLE);
        iv2Check.setVisibility(View.INVISIBLE);
        iv3Check.setVisibility(View.INVISIBLE);
        iv4Check.setVisibility(View.INVISIBLE);
        tv1Perc.setVisibility(View.INVISIBLE);
        tv2Perc.setVisibility(View.INVISIBLE);
        tv3Perc.setVisibility(View.INVISIBLE);
        tv4Perc.setVisibility(View.INVISIBLE);

        LoginResponse loggedUser = Hawk.get("loggedUser");

        if (loggedUser.getLoginView().getID() == (item.getMasterVotingView().getOwnerUserID())){
            btVoteOptions.setVisibility(View.INVISIBLE);
        }
        else
        {
            btVoteOptions.setVisibility(View.VISIBLE);
        }
        requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new RoundedCorners(20));
        profilePicRequestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop();

        tvDesc.setVisibility(View.VISIBLE);
        if (item.getMasterVotingView().getDescription().isEmpty())
        {
            tvDesc.setVisibility(View.GONE);
        }

        if (!item.getMasterVotingView().isIsCommentAllowed()){
            btComments.setText(mContext.getResources().getString(R.string.commentsDisabled));
        }
        else
        {
            viewHolder.addOnClickListener(R.id.btComments);
            if(item.getMasterVotingView().getCommentCount() == 0)
            {
                btComments.setText(mContext.getResources().getString(R.string.thereAreNoComments));
            }
            else{
                btComments.setText(mContext.getResources().getString(R.string.seeComments) + " (" +item.getMasterVotingView().getCommentCount()+")");
            }
        }

        try{
            //if(item.getMasterVotingView().isIsOwnerHiddenProfile()){
            //    viewHolder.setText(R.id.tvNameSurname,mContext.getResources().getString(R.string.hidden_profile));
            //    Glide.with(mContext).load(R.drawable.com_facebook_profile_picture_blank_square).apply(profilePicRequestOptions).into((ImageView) viewHolder.getView(R.id.ivProfilePic));
            //}
            if(item.getMasterVotingView().isIsAnonymous()){
               viewHolder.setText(R.id.tvNameSurname,mContext.getResources().getString(R.string.anonymousRecord));
               Glide.with(mContext).load(R.drawable.com_facebook_profile_picture_blank_square).apply(profilePicRequestOptions).into((ImageView) viewHolder.getView(R.id.ivProfilePic));
            }
            else{
                viewHolder.setText(R.id.tvNameSurname,item.getMasterVotingView().getOwnerUsername());
                Glide.with(mContext).load(item.getMasterVotingView().getOwnerPicURL()).apply(profilePicRequestOptions).into((ImageView) viewHolder.getView(R.id.ivProfilePic));
            }

            if (item.getMasterVotingView().getChoiceCount() == 2){
                Glide.with(mContext).load(item.getChoiceList().get(0).getPictureURL()).apply(requestOptions).into(iv1);
                Glide.with(mContext).load(item.getChoiceList().get(1).getPictureURL()).apply(requestOptions).into(iv2);
                //iv3.refreshDrawableState();
                //iv4.refreshDrawableState();

                voteCount = item.getChoiceList().get(0).getClickedCount() + item.getChoiceList().get(1).getClickedCount();

                //Glide.with(mContext).load(R.drawable.white_mspaint).apply(requestOptions).into((ImageView) viewHolder.getView(R.id.iv3));
                //Glide.with(mContext).load(R.drawable.white_mspaint).apply(requestOptions).into((ImageView) viewHolder.getView(R.id.iv4));

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
            else if(item.getMasterVotingView().getChoiceCount() == 3) {
                Glide.with(mContext).load(item.getChoiceList().get(0).getPictureURL()).apply(requestOptions).into(iv1);
                Glide.with(mContext).load(item.getChoiceList().get(1).getPictureURL()).apply(requestOptions).into(iv2);
                Glide.with(mContext).load(item.getChoiceList().get(2).getPictureURL()).apply(requestOptions).into(iv3);
                //Glide.with(mContext).load(R.drawable.white_mspaint).apply(requestOptions).into((ImageView) viewHolder.getView(R.id.iv4));

                voteCount = item.getChoiceList().get(0).getClickedCount() + item.getChoiceList().get(1).getClickedCount() + item.getChoiceList().get(2).getClickedCount();


                iv3.setVisibility(View.VISIBLE);
                iv4.setVisibility(View.GONE);

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
                Glide.with(mContext).load(item.getChoiceList().get(0).getPictureURL()).apply(requestOptions).into(iv1);
                Glide.with(mContext).load(item.getChoiceList().get(1).getPictureURL()).apply(requestOptions).into(iv2);
                Glide.with(mContext).load(item.getChoiceList().get(2).getPictureURL()).apply(requestOptions).into(iv3);
                Glide.with(mContext).load(item.getChoiceList().get(3).getPictureURL()).apply(requestOptions).into(iv4);

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

            btVoteCount.setText(String.valueOf(voteCount) + " "+mContext.getResources().getString(R.string.vote_count));
            //Glide.with(mContext).load(item.getMasterVotingView().getOwnerCountry()).into((ImageView) viewHolder.getView(R.id.ivCountry));
            Glide.with(mContext).load(mContext.getResources().getIdentifier(item.getMasterVotingView().getFlagCode(), "drawable", mContext.getPackageName())).into((ImageView) viewHolder.getView(R.id.ivCountry));
        }
        catch (OutOfMemoryError e)
        {
            Log.d("Tarja","Glide out of memory "+e.getMessage());
        }

        viewHolder.addOnClickListener(R.id.iv1)
                .addOnClickListener(R.id.iv2)
                .addOnClickListener(R.id.iv3)
                .addOnClickListener(R.id.iv4)
                .addOnClickListener(R.id.ivProfilePic)
                .addOnClickListener(R.id.btVoteOptions)
                .addOnClickListener(R.id.tvNameSurname);


        if (item.getSelectedChoice() == 0 && item.getMasterVotingView().getOwnerUserID() != loggedUser.getLoginView().getID()){// oylama yapılmadıysa
            pBar1.setVisibility(View.GONE);
            pBar2.setVisibility(View.GONE);
            pBar3.setVisibility(View.GONE);
            pBar4.setVisibility(View.GONE);
            tv1Perc.setVisibility(View.GONE);
            tv2Perc.setVisibility(View.GONE);
            tv3Perc.setVisibility(View.GONE);
            tv4Perc.setVisibility(View.GONE);
            iv1Check.setVisibility(View.GONE);
            iv2Check.setVisibility(View.GONE);
            iv3Check.setVisibility(View.GONE);
            iv4Check.setVisibility(View.GONE);
            ivLike1.setVisibility(View.GONE);
            ivLike2.setVisibility(View.GONE);
            ivLike3.setVisibility(View.GONE);
            ivLike4.setVisibility(View.GONE);
            viewHolder
                    .addOnLongClickListener(R.id.iv1)
                    .addOnLongClickListener(R.id.iv2)
                    .addOnLongClickListener(R.id.iv3)
                    .addOnLongClickListener(R.id.iv4);
        }
        else if(item.getSelectedChoice() == 1){
            ivLike1.setVisibility(View.VISIBLE);
            ivLike2.setVisibility(View.GONE);
            ivLike3.setVisibility(View.GONE);
            ivLike4.setVisibility(View.GONE);
        }
        else if(item.getSelectedChoice() == 2){
            ivLike1.setVisibility(View.GONE);
            ivLike2.setVisibility(View.VISIBLE);
            ivLike3.setVisibility(View.GONE);
            ivLike4.setVisibility(View.GONE);
        }
        else if(item.getSelectedChoice() == 3){
            ivLike1.setVisibility(View.GONE);
            ivLike2.setVisibility(View.GONE);
            ivLike3.setVisibility(View.VISIBLE);
            ivLike4.setVisibility(View.GONE);
        }
        else if(item.getSelectedChoice() == 4){
            ivLike1.setVisibility(View.GONE);
            ivLike2.setVisibility(View.GONE);
            ivLike3.setVisibility(View.GONE);
            ivLike4.setVisibility(View.VISIBLE);
        }
        else// kendi oylamamızsa
        {
            ivLike1.setVisibility(View.GONE);
            ivLike2.setVisibility(View.GONE);
            ivLike3.setVisibility(View.GONE);
            ivLike4.setVisibility(View.GONE);
        }

        //Locale locale = new Locale("en", "US");
        //Locale locale = new Locale("tr", "TR");

        //String date = item.getMasterVotingView().getCreationDate();

        try {
            //Locale current = mContext.getResources().getConfiguration().locale;
            //DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, current);
            //Date myDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(item.getMasterVotingView().getCreationDate());
            //String date = dateFormat.format(myDate); // 8 Eki 2018, Oct 8, 2018
            //String time = new SimpleDateFormat(" HH:mm").format(myDate); //08:18
            //viewHolder.setText(R.id.tvDate,date+time);

            Locale current = mContext.getResources().getConfiguration().locale;
            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, current);
            dateFormat.setTimeZone(TimeZone.getDefault());
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = simpleDateFormat1.parse(item.getMasterVotingView().getCreationDate());
            String date = dateFormat.format(myDate); // 8 Eki 2018, Oct 8, 2018
            String time = new SimpleDateFormat(" HH:mm").format(myDate); //08:18
            viewHolder.setText(R.id.tvDate,date+time);
        }catch (Exception ex){
            Log.d("tarja","exception"+ex.getMessage());
        }


        viewHolder.setText(R.id.tvDesc,item.getMasterVotingView().getDescription());
    }
}
