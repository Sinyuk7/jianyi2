package com.sinyuk.jianyi.api;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.sinyuk.jianyi.api.oauth.OauthService;
import com.sinyuk.jianyi.api.service.JianyiService;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.ui.events.LoginEvent;
import com.sinyuk.jianyi.ui.events.LogoutEvent;
import com.sinyuk.jianyi.ui.post.PostResult;
import com.sinyuk.jianyi.utils.Compressor;
import com.sinyuk.jianyi.utils.PrefsKeySet;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16/9/9.
 */
public class AccountManger {
    private static final String TAG = "AccountManger";
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

    private Preference<String> password;
    private Player mCurrentUser;
    private Context applicationContext;

    public AccountManger(Context context,
                         JianyiService jianyiService,
                         OauthService oauthService,
                         RxSharedPreferences rxSharedPreferences) {
        // Nope
        this.applicationContext = context;
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

        password = mRxSharedPreferences.getString(PrefsKeySet.KEY_PASSWORD);
    }

    public boolean isLoggedIn() {
        return userId.isSet() && !userId.get().equals(userId.defaultValue());
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

    private void saveInPreference(Player player, String psw) {
        Log.d(TAG, "saveInPreference: " + player.toString());
        userId.set(player.getId());
        userName.set(player.getName());
        userAvatar.set(player.getAvatar());
        gamount.set(player.getGamount());
        tel.set(player.getTel());
        sex.set(player.getSex());
        school.set(player.getSchool());
        currentSchool.set(player.getCurrentSchool());
        schoolName.set(/*player.getSchoolName()*/"这里我要说一句大傻逼吴结巴");
        password.set(psw);
    }

    public Observable<Player> login(String tel, String password) {
        return jianyiService.login(tel, password)
                .map(new HttpResultFunc<Player>() {
                    @Override
                    public Player call(HttpResult<Player> httpResult) {
                        return httpResult.getData();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(player -> saveInPreference(player, password))
                .doOnNext(player -> EventBus.getDefault().post(new LoginEvent(player)));
    }


    public void logout() {
        Log.d(TAG, "Delete prefs");
        userId.delete();
        userName.delete();
        userAvatar.delete();
        gamount.delete();
        tel.delete();
        sex.delete();
        school.delete();
        currentSchool.delete();
        schoolName.delete();
        EventBus.getDefault().post(new LogoutEvent());
    }


    public Observable<String> upload(File file) {
        return Observable.just(file)
                .flatMap(new Func1<File, Observable<File>>() {
                    @Override
                    public Observable<File> call(File file) {
                        return Compressor.compress(applicationContext, file);
                    }
                })
                .map(compressed -> RequestBody.create(MediaType.parse("image/jpg"), compressed))
                .map(requestBody -> MultipartBody.Part.createFormData("pic", file.getName(), requestBody))
                .flatMap(new Func1<MultipartBody.Part, Observable<String>>() {
                    @Override
                    public Observable<String> call(MultipartBody.Part part) {
                        return jianyiService.uploadPic(part)
                                .map(new HttpResultFunc<String>() {
                                    @Override
                                    public String call(HttpResult<String> httpResult) {
                                        return httpResult.getData();
                                    }
                                });
                    }
                });
    }

    private boolean hasPassword() {
        return password.isSet() && !TextUtils.isEmpty(password.get());
    }

    public Observable<PostResult> postGoods(String title,
                                            String parentSort,
                                            String childSort,
                                            String price,
                                            String detail,
                                            List<String> pics) {

        switch (pics.size()) {
            case 1:
                return getCurrentUser().flatMap(new Func1<Player, Observable<PostResult>>() {
                    @Override
                    public Observable<PostResult> call(Player player) {
                        if (player == null || !hasPassword()) {
                            return Observable.error(new Exception("用户信息过期,请重新登录"));
                        }

                        return jianyiService.postGoods(
                                tel.get(),
                                password.get(),
                                title,
                                parentSort,
                                childSort,
                                price,
                                detail,
                                pics.get(0)
                        ).map(new HttpResultFunc<PostResult>() {
                            @Override
                            public PostResult call(HttpResult<PostResult> httpResult) {
                                return httpResult.getData();
                            }
                        }).subscribeOn(Schedulers.io());
                    }
                }).subscribeOn(Schedulers.io());


            case 2:
                return getCurrentUser().flatMap(new Func1<Player, Observable<PostResult>>() {
                    @Override
                    public Observable<PostResult> call(Player player) {
                        if (player == null || !hasPassword()) {
                            return Observable.error(new Exception("用户信息过期,请重新登录"));
                        }

                        return jianyiService.postGoods(
                                tel.get(),
                                password.get(),
                                title,
                                parentSort,
                                childSort,
                                price,
                                detail,
                                pics.get(0),
                                pics.get(1)
                        ).map(new HttpResultFunc<PostResult>() {
                            @Override
                            public PostResult call(HttpResult<PostResult> httpResult) {
                                return httpResult.getData();
                            }
                        }).subscribeOn(Schedulers.io());
                    }
                }).subscribeOn(Schedulers.io());


            case 3:
                return getCurrentUser().flatMap(new Func1<Player, Observable<PostResult>>() {
                    @Override
                    public Observable<PostResult> call(Player player) {
                        if (player == null || !hasPassword()) {
                            return Observable.error(new Exception("用户信息过期,请重新登录"));
                        }

                        return jianyiService.postGoods(
                                tel.get(),
                                password.get(),
                                title,
                                parentSort,
                                childSort,
                                price,
                                detail,
                                pics.get(0),
                                pics.get(1),
                                pics.get(2)
                        ).map(new HttpResultFunc<PostResult>() {
                            @Override
                            public PostResult call(HttpResult<PostResult> httpResult) {
                                return httpResult.getData();
                            }
                        }).subscribeOn(Schedulers.io());
                    }
                }).subscribeOn(Schedulers.io());
        }
        return Observable.error(new Exception("未知错误"));
    }


}
