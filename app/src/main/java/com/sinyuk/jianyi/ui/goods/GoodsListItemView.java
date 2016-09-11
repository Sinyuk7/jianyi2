package com.sinyuk.jianyi.ui.goods;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.api.JianyiApi;
import com.sinyuk.jianyi.data.goods.Goods;
import com.sinyuk.jianyi.utils.Preconditions;
import com.sinyuk.jianyi.utils.StringUtils;
import com.sinyuk.jianyi.widgets.LabelView;
import com.sinyuk.jianyi.widgets.RatioImageView;
import com.sinyuk.jianyi.widgets.TextDrawable;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sinyuk on 16/9/10.
 */
public class GoodsListItemView extends LinearLayout {
    @BindView(R.id.shot_iv)
    RatioImageView mShotIv;
    @BindView(R.id.price_label_view)
    LabelView mPriceLabelView;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.user_name_tv)
    TextView mUserNameTv;
    @BindColor(R.color.grey_600)
    int grey600;


    public GoodsListItemView(Context context) {
        this(context, null);
    }

    public GoodsListItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoodsListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void bindTo(Goods data,
                       DrawableRequestBuilder<String> shotBuilder,
                       DrawableRequestBuilder<String> avatarBuilder) {
        Preconditions.checkNotNull(data, "Can't bind to a null good");

        setText(mTitleTv, data.getName(), null);
        setText(mUserNameTv, data.getUsername(), data.getUid() + "");
        if (!TextUtils.isEmpty(data.getPrice())) {
            mPriceLabelView.setText(data.getPrice());
        } else {
            mPriceLabelView.setVisibility(INVISIBLE);
        }


         /* avatar*/
        final String username = StringUtils.valueOrDefault(data.getUsername(), " ");

        // use a TextDrawable as a placeholder
        final char firstLetter = username.charAt(0);

        final TextDrawable textDrawable = TextDrawable.builder()
                .buildRound(firstLetter + "", grey600);

        avatarBuilder.load(data.getHeadImg())
                .placeholder(textDrawable)
                .error(textDrawable)
                .into(mAvatar);

           /*加载图片*/
        shotBuilder.load(JianyiApi.BASE_URL + data.getPic()).into(mShotIv);
    }

    private void setText(TextView textView, String input, String defaultValue) {
        if (textView == null) return;
        if (TextUtils.isEmpty(input) && TextUtils.isEmpty(defaultValue)) {
            textView.setVisibility(INVISIBLE);
        } else {
            textView.setText(StringUtils.valueOrDefault(input, defaultValue));
        }

    }
}
