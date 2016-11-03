package com.sinyuk.jianyi.data;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sinyuk.jianyi.utils.AvatarHelper;
import com.sinyuk.jianyi.utils.glide.CropCircleTransformation;
import com.sinyuk.jianyi.widgets.TextDrawable;
import com.sinyuk.myutils.resource.StringUtils;

/**
 * Created by Sinyuk on 16.11.3.
 */

public final class BindingMethods {
    @BindingAdapter({"avatar", "name"})
    public static void loadAvatar(ImageView imageView, String avatar, String name) {
        avatar = StringUtils.valueOrDefault(avatar, " ");
        name = StringUtils.valueOrDefault(name, " ");
        final TextDrawable placeHolder = AvatarHelper.createTextDrawable(name, imageView.getContext());

        Glide.with(imageView.getContext())
                .load(avatar)
                .dontAnimate()
                .placeholder(placeHolder)
                .error(placeHolder)
                .bitmapTransform(new CropCircleTransformation(imageView.getContext()))
                .into(imageView);
    }
}
