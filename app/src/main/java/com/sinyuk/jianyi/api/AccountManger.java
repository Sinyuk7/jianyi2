package com.sinyuk.jianyi.api;

import android.support.annotation.Nullable;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.sinyuk.jianyi.api.oauth.OauthService;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.utils.PrefsKeySet;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Sinyuk on 16/9/9.
 */
public class AccountManger {
    private static final Integer INVALID_ID = 0;
    private RxSharedPreferences mRxSharedPreferences;
    private OauthService mOauthService;
    private Preference<String> userName;
    private Preference<String> userAvatar;
    private Preference<Integer> userId;
    private Player mCurrentUser;

    public AccountManger(RxSharedPreferences rxSharedPreferences) {
        // Nope
        this.mRxSharedPreferences = rxSharedPreferences;

        userId = mRxSharedPreferences.getInteger(PrefsKeySet.KEY_USER_ID);
        userName = mRxSharedPreferences.getString(PrefsKeySet.KEY_USER_NAME);
        userAvatar = mRxSharedPreferences.getString(PrefsKeySet.KEY_USER_AVATAR);

    }

    public boolean isLogin() {
        return userId.isSet() && !userId.get().equals(userId.defaultValue());
    }


    public Observable<Player> getFakePlayer() {
        Player player = new Player();
        player.setName("Sinyuk");
        player.setAvatar("http://tva3.sinaimg.cn/crop.5.0.458.458.180/b29e155ajw8eymqeunme2j20dc0dc74q.jpg");
        player.setTel("15757161279");
        player.setSchoolName("浙江传媒学院");
        return Observable.just(player)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Nullable
    public String getAvatar() {
        return userAvatar.get();
    }

    public Observable<Player> getCurrentUser() {
        return Observable.just(new Player()).map(user -> {
            user.setName(userName.get());
            user.setAvatar(userAvatar.get());
            user.setId(userId.get());
            return user;
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread());
    }


    private void saveInPreference(Player player) {
        Timber.d("saveInPreference");
        userId.set(player.getId());
        userName.set(player.getName());
        userAvatar.set(player.getAvatar());
    }

    public Observable<Void> logout() {
        return Observable.empty().map((Func1<Object, Void>) o -> {

            userId.delete();
            userName.delete();

            userAvatar.delete();

            return null;
        }).doOnNext(aVoid -> {
            // TODO: post a event
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread());
    }
}
