package com.sinyuk.jianyi.api.oauth;

import com.sinyuk.yuk.ui.login.DribbbleLoginActivity;
import com.sinyuk.yuk.ui.oauth.DribbleOauthActivity;

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
        void inject(DribbleOauthActivity target);
}
