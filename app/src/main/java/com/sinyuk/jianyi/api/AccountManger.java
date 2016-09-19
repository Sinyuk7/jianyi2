package com.sinyuk.jianyi.api;

import android.support.annotation.Nullable;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.sinyuk.jianyi.api.oauth.OauthService;
import com.sinyuk.jianyi.api.service.JianyiService;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.ui.events.LoginEvent;
import com.sinyuk.jianyi.ui.events.LogoutEvent;
import com.sinyuk.jianyi.utils.PrefsKeySet;

import org.greenrobot.eventbus.EventBus;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16/9/9.
 */
public class AccountManger {
    private RxSharedPreferences mRxSharedPreferences;
    private OauthService mOauthService;
    private JianyiService jianyiService;
    private Preference<String> userName;
    private Preference<String> userAvatar;
    private Preference<Integer> userId;
    private Preference<Integer> gamount;
    private Preference<String> tel;
    private Preference<Integer> school;
    private Preference<Integer> currentSchool;
    private Preference<String> schoolName;
    private Preference<Integer> sex;
    private Player mCurrentUser;

    public AccountManger(JianyiService jianyiService, OauthService oauthService, RxSharedPreferences rxSharedPreferences) {
        // Nope
        this.jianyiService = jianyiService;
        this.mRxSharedPreferences = rxSharedPreferences;
        this.mOauthService = oauthService;
        userId = mRxSharedPreferences.getInteger(PrefsKeySet.KEY_USER_ID);
        userName = mRxSharedPreferences.getString(PrefsKeySet.KEY_USER_NAME);
        userAvatar = mRxSharedPreferences.getString(PrefsKeySet.KEY_USER_AVATAR);
        gamount = mRxSharedPreferences.getInteger(PrefsKeySet.KEY_GAMOUNT);
        tel = mRxSharedPreferences.getString(PrefsKeySet.KEY_TEl);
        school = mRxSharedPreferences.getInteger(PrefsKeySet.KEY_SCHOOL);
        currentSchool = mRxSharedPreferences.getInteger(PrefsKeySet.KEY_CURRENT_SCHOOL);
        schoolName = mRxSharedPreferences.getString(PrefsKeySet.KEY_SCHOOL_NAME);
        sex = mRxSharedPreferences.getInteger(PrefsKeySet.KEY_SEX);
    }

    public boolean isLoggedIn() {
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
        return Observable.just(new Player())
                .map(user -> {
                    user.setName(userName.get());
                    user.setAvatar(userAvatar.get());
                    user.setId(userId.get());
                    user.setGamount(gamount.get());
                    user.setTel(tel.get());
                    user.setSex(sex.get());
                    user.setSchool(school.get());
                    user.setCurrentSchool(currentSchool.get());
                    user.setSchoolName(schoolName.get());
                    return user;
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void saveInPreference(Player player) {
        userId.set(player.getId());
        userName.set(player.getName());
        userAvatar.set(player.getAvatar());
        gamount.set(player.getGamount());
        tel.set(player.getTel());
        sex.set(player.getSex());
        school.set(player.getSchool());
        currentSchool.set(player.getCurrentSchool());
        schoolName.set(player.getSchoolName());
    }

    public Observable login(String tel, String password) {
        return jianyiService.login(tel, password)
                .map(new HttpResultFunc<Player>() {
                    @Override
                    public Player call(HttpResult<Player> httpResult) {
                        return httpResult.getData();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this::saveInPreference)
                .doOnNext(player -> EventBus.getDefault().post(new LoginEvent(player)));
    }


    public Observable<Void> logout() {
        return Observable.empty()
                .map((Func1<Object, Void>) o -> {
                    userId.delete();
                    userName.delete();
                    userAvatar.delete();
                    gamount.delete();
                    tel.delete();
                    sex.delete();
                    school.delete();
                    currentSchool.delete();
                    schoolName.delete();
                    return null;
                })
                .doOnCompleted(() -> EventBus.getDefault().post(new LogoutEvent()))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
