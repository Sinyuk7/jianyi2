package com.sinyuk.jianyi.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.api.AccountManger;
import com.sinyuk.jianyi.api.ApiException;
import com.sinyuk.jianyi.api.oauth.OauthModule;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.ui.FormActivity;
import com.sinyuk.jianyi.utils.ImeUtils;
import com.sinyuk.jianyi.utils.ScreenUtils;
import com.sinyuk.jianyi.utils.ToastUtils;
import com.sinyuk.jianyi.utils.Validator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import dagger.Lazy;
import rx.Observable;
import rx.Observer;

/**
 * Created by Sinyuk on 16/9/18.
 */
public class JianyiLoginActivity extends FormActivity {
    private static final long TOOLBAR_OFFSET_DURATION = 200;
    @Inject
    Lazy<AccountManger> accountMangerLazy;
    @Inject
    ToastUtils toastUtils;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.user_name_et)
    TextInputEditText mUserNameEt;
    @BindView(R.id.user_name_input_layout)
    TextInputLayout mUserNameInputLayout;
    @BindView(R.id.password_et)
    TextInputEditText mPasswordEt;
    @BindView(R.id.password_input_layout)
    TextInputLayout mPasswordInputLayout;
    @BindView(R.id.forget_psw_btn)
    TextView mForgetPswBtn;


    public static void start(Context context, Rect rect) {
        Intent starter = new Intent(context, JianyiLoginActivity.class);
        starter.putExtra(KEY_LEFT, rect.left);
        starter.putExtra(KEY_TOP, rect.top);
        starter.putExtra(KEY_RIGHT, rect.right);
        starter.putExtra(KEY_BOTTOM, rect.bottom);
        context.startActivity(starter);
        ((Activity) context).overridePendingTransition(0, 0);
    }


    @OnClick(R.id.action_btn)
    public void onLogin() {
        mPasswordEt.setError(null);
        mUserNameEt.setError(null);
        actionButton.setProgress(0);

        final String phoneNum = mUserNameEt.getText().toString();
        final String password = mPasswordEt.getText().toString();
        ImeUtils.hideIme(actionButton);

        addSubscription(accountMangerLazy.get()
                .login(phoneNum, password)
                .doOnSubscribe(this::showProgress)
                .subscribe(new Observer<Player>() {
                    @Override
                    public void onCompleted() {
                        showSucceed(getString(R.string.login_succeed));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof ApiException) {
                            showFailed();
                        } else {
                            showError();
                        }
                        toastUtils.toastShort(e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(Player player) {
                    }
                }));


    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_jianyi_login;
    }

    @Override
    protected void beforeInflating() {
        super.beforeInflating();
        App.get(this).getAppComponent().plus(new OauthModule()).inject(this);
    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {
        super.finishInflating(savedInstanceState);

        addSubscription(RxTextView.editorActions(mPasswordEt)
                .map(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(done -> {
                    if (done) {
                        onLogin();
                    }
                }));

        Observable<CharSequence> passwordObservable = RxTextView.textChanges(mPasswordEt).skip(6);
        Observable<CharSequence> phoneNumObservable = RxTextView.textChanges(mUserNameEt).skip(10);

        addSubscription(Observable.combineLatest(passwordObservable, phoneNumObservable, (password, phoneNum) -> {
            if (!Validator.isMobile(phoneNum.toString())) {
                mUserNameInputLayout.setError("手机号码格式错误");
                return false;
            } else {
                mUserNameInputLayout.setError(null);
            }
            if (!Validator.isPassword(password.toString())) {
                mPasswordInputLayout.setError("密码格式错误");
                return false;
            } else {
                mPasswordInputLayout.setError(null);
            }
            return true;
        }).subscribe(super::toggleActionButton));


    }

    @Override
    protected void onCircularRevealEnd() {
        toolbar.setTranslationY(-ScreenUtils.dpToPxInt(this, 56));
        toolbar.animate()
                .withLayer()
                .withStartAction(() -> toolbar.setVisibility(View.VISIBLE))
                .setInterpolator(new FastOutSlowInInterpolator())
                .withEndAction(this::animateChildren)
                .translationY(0)
                .setDuration(TOOLBAR_OFFSET_DURATION)
                .start();
    }

    private void animateChildren() {
        animateChildIn(mUserNameInputLayout, 1).start();
        animateChildIn(mPasswordInputLayout, 2).start();
        animateChildIn(mForgetPswBtn, 3).start();
        animateChildIn(actionButton, 4).withEndAction(this::raiseChildrenUp).start();
    }

    private void raiseChildrenUp() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        raise(mUserNameInputLayout, 1);
        raise(mPasswordInputLayout, 2);
    }
}
