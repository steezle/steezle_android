package com.steezle.e_com.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.steezle.e_com.R;
import com.steezle.e_com.model.TinderItem;
import com.steezle.e_com.utils.Constant;
import com.steezle.e_com.view.ProductDetailActivity;

@Layout(R.layout.tinder_item)
public class TinderCard {

    private static int count;

    @View(R.id.profileImageView)
    public ImageView profileImageView;

    @View(R.id.tinder_productname)
    private TextView productname;

    @View(R.id.tinder_brandname)
    private TextView brandname;

    @View(R.id.tinder_price)
    private TextView price;


    private Context context;
    private TinderItem tinderItem;
    private OnAccept onAccept;
    private OnReject onReject;
    SpannableStringBuilder mSSBuilder;
    String mText="Yung2";
    @Click(R.id.profileImageView)

    private void onClick() {

        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra(Constant.ProductDetail.PRODUCT_ID, tinderItem.getId());
        context.startActivity(intent);
        Log.d("DEBUG", "profileImageView");
    }

    public TinderCard(Context context, TinderItem tinderItem,
                      OnAccept onAccept, OnReject onReject) {
        this.context = context;
        this.tinderItem = tinderItem;
        this.onAccept = onAccept;
        this.onReject = onReject;
    }

    @Resolve
    private void onResolve() {

        if (tinderItem != null) {

            Glide.with( context )
                    .load( tinderItem.getMain_image() )
                    .thumbnail( 0.1f )
                    .apply( new RequestOptions()
                            .placeholder( R.drawable.empty_menu )
                            .error( R.drawable.empty_menu ) )
                    .into( profileImageView );

            productname.setText( tinderItem.getTitle() );


            if (tinderItem.getBrandname().equalsIgnoreCase( mText )) {
                // Initialize a new SpannableStringBuilder instance
                mSSBuilder = new SpannableStringBuilder( mText );
                // Initialize a new SuperscriptSpan instance
                SuperscriptSpan superscriptSpan = new SuperscriptSpan();

// Apply the SuperscriptSpan
                mSSBuilder.setSpan(
                        superscriptSpan,
                        mText.indexOf( "2" ),
                        mText.indexOf( "2" ) + String.valueOf( "2" ).length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                showSmallSizeText( "2" );
                brandname.setText( mSSBuilder );
//            holder.tv_brandName.setText(  "Yung"+ Html.fromHtml(context.getResources().getString(R.string.super_2)),);
            } else
                brandname.setText( tinderItem.getBrandname() );
            price.setText( tinderItem.getPrice() );
        } else {
            profileImageView.setImageDrawable( context.getResources().getDrawable( R.drawable.noproductavailable ) );
        }
    }

    protected void showSmallSizeText(String textToSmall){
        // Initialize a new RelativeSizeSpan instance
        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(.5f);

        // Apply the RelativeSizeSpan to display small text
        mSSBuilder.setSpan(
                relativeSizeSpan,
                mText.indexOf(textToSmall),
                mText.indexOf(textToSmall) + String.valueOf(textToSmall).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

    }

    @SwipeOut
    private void onSwipeOut() {
        onReject.onReject(tinderItem);
        //Toast.makeText(context, "onSwipeOut" ,Toast.LENGTH_LONG).show();
        Log.d("EVENT", "onSwipedIn");
    }

    @SwipeIn
    private void onSwipeIn() {
        onAccept.onAccept(tinderItem);
        //Toast.makeText(context, "onSwipeIn" ,Toast.LENGTH_LONG).show();
        Log.d("EVENT", "onSwipedIn");
    }

   /* @SwipeInState
    private void onSwipeInState(){
        Toast.makeText(context, "onSwipeInState" ,Toast.LENGTH_LONG).show();
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Toast.makeText(context, "onSwipeOutState" ,Toast.LENGTH_LONG).show();
        Log.d("EVENT", "onSwipeOutState");
    }*/

    public interface OnAccept {
        public void onAccept(TinderItem tinderItem);
    }

    public interface OnReject {
        public void onReject(TinderItem tinderItem);
    }

}