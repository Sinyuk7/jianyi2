package com.sinyuk.jianyi.ui.login;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.ui.BaseActivity;
import com.sinyuk.jianyi.utils.ImeUtils;
import com.sinyuk.jianyi.utils.Validator;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by Sinyuk on 16/9/18.
 */
public class JianyiLoginActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
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
    @BindView(R.id.login_btn)
    Button mLoginBtn;

    @Override
    protected int getContentViewID() {
        return R.layout.activity_jianyi_login;
    }

    @Override
    protected void beforeInflating() {

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

        Observable<CharSequence> passwordObservable = RxTextView.textChanges(mPasswordEt).skip(5);
        Observable<CharSequence> phoneNumObservable = RxTextView.textChanges(mUserNameEt).skip(10);

        addSubscription(Observable.combineLatest(passwordObservable, phoneNumObservable, (password, phoneNum) -> {
            if (!Validator.isMobile(phoneNum.toString())) {
                mUserNameEt.setError("写错了");
                return false;
            }
            if (!Validator.isPassword(password.toString())) {
                mPasswordEt.setError("写错了");
                return false;
            }
            return true;
        }).subscribe(JianyiLoginActivity.this::toggleLoginButton));

        toggleLoginButton(false);
    }

    @OnClick(R.id.login_btn)
    public void onLogin() {
        mPasswordEt.setError(null);
        mUserNameEt.setError(null);
        final String phoneNum = mUserNameEt.getText().toString();
        final String password = mPasswordEt.getText().toString();
        ImeUtils.hideIme(mLoginBtn);
    }

    private void toggleLoginButton(boolean activated) {
        mLoginBtn.setActivated(activated);
        mLoginBtn.setClickable(activated);
    }
}
