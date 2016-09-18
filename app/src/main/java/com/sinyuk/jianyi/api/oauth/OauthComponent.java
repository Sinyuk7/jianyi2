package com.sinyuk.jianyi.api.oauth;

import com.sinyuk.jianyi.ui.detail.DetailActivity;
import com.sinyuk.jianyi.ui.home.GuillotineMenu;
import com.sinyuk.jianyi.ui.login.JianyiLoginActivity;

import javax.inject.Singleton;

import dagger.Subcomponent;

/**
 * Created by Sinyuk on 16/8/23.
 */
@Singleton
@Subcomponent(
        modules = {
                OauthModule.class
        }
)
public interface OauthComponent {
        void inject(DetailActivity target);

        void inject(GuillotineMenu target);

        void inject(JianyiLoginActivity target);
}
