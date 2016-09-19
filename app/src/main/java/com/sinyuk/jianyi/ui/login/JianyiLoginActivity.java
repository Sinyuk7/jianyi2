package com.sinyuk.jianyi.ui.login;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.api.AccountManger;
import com.sinyuk.jianyi.api.ApiException;
import com.sinyuk.jianyi.api.oauth.OauthModule;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.ui.BaseActivity;
import com.sinyuk.jianyi.ui.sweetalert.SweetAlertDialog;
import com.sinyuk.jianyi.utils.ImeUtils;
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
public class JianyiLoginActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.user_name_tv)
    TextInputEditText mUserNameEt;
    @BindView(R.id.user_name_input_layout)
    TextInputLayout mUserNameInputLayout;
    @BindView(R.id.password_et)
    TextInputEditText mPasswordEt;
    @BindView(R.id.password_input_layout)
    TextInputLayout mPasswordInputLayout;
    @BindView(R.id.forget_psw_btn)
    TextView mForgetPswBtn;
    @BindView(R.id.login_btn)
    Button mLoginBtn;

    @Inject
    Lazy<AccountManger> accountMangerLazy;

    private SweetAlertDialog mDialog;

    @Override
    protected int getContentViewID() {
        return R.layout.activity_jianyi_login;
    }

    @Override
    protected void beforeInflating() {
        App.get(this).getAppComponent().plus(new OauthModule()).inject(this);
    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {
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
        }).subscribe(JianyiLoginActivity.this::toggleLoginButton));

        toggleLoginButton(false);
    }

    private void showProgress() {
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(ContextCompat.getColor(this, R.color.colorAccent));
        mDialog.setTitleText("登录中");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    private void hideProgress() {
        mDialog.dismissWithAnimation();
    }

    public void showSucceed() {
        if (mDialog == null) {
            return;
        }
        mDialog.setTitleText(getString(R.string.login_succeed))
                .setConfirmText(getString(R.string.action_alright))
                .setConfirmClickListener(dialog -> {
                    dialog.dismissWithAnimation();
                    mLoginBtn.postDelayed(this::finish,250);
                })
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        ;
    }


    private void showFailed(String message) {
        if (mDialog == null) {
            return;
        }
        mDialog.setTitleText(message)
                .setConfirmText(getString(R.string.action_alright))
                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                .changeAlertType(SweetAlertDialog.WARNING_TYPE);
    }

    private void showError(String message) {
        if (mDialog == null) {
            return;
        }
        mDialog.setTitleText(message)
                .setConfirmText(getString(R.string.action_alright))
                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }

    @OnClick(R.id.login_btn)
    public void onLogin() {
        mPasswordEt.setError(null);
        mUserNameEt.setError(null);
        final String phoneNum = mUserNameEt.getText().toString();
        final String password = mPasswordEt.getText().toString();
        ImeUtils.hideIme(mLoginBtn);

        addSubscription(accountMangerLazy.get()
                .login(phoneNum, password)
                .doOnSubscribe(this::showProgress)
                .subscribe(new Observer<Player>() {
                    @Override
                    public void onCompleted() {
                        showSucceed();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof ApiException) {
                            showFailed(e.getLocalizedMessage());
                        } else {
                            showError(e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onNext(Player player) {
                        Log.d(TAG, "onNext: " + player.toString());
                    }
                }));
    }

    private void toggleLoginButton(boolean activated) {
        mLoginBtn.setActivated(activated);
        mLoginBtn.setClickable(activated);
    }
}
