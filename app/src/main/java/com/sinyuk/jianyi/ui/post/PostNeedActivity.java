package com.sinyuk.jianyi.ui.post;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.api.AccountManger;
import com.sinyuk.jianyi.api.oauth.OauthModule;
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

public class PostNeedActivity extends FormActivity {
    @BindView(R.id.description_et)
    EditText descriptionEt;
    @BindView(R.id.description_input_area)
    TextInputLayout descriptionInputArea;
    @BindView(R.id.tel_et)
    EditText telEt;
    @BindView(R.id.tel_input_area)
    TextInputLayout telInputArea;
    @BindView(R.id.price_et)
    EditText priceEt;
    @BindView(R.id.price_input_area)
    TextInputLayout priceInputArea;
    @BindView(R.id.check_btn)
    ImageView checkBtn;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Inject
    Lazy<AccountManger> accountMangerLazy;
    @Inject
    ToastUtils toastUtils;

    private Observer<String> postObserver = new Observer<String>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            showError();
            toastUtils.toastShort(e.getLocalizedMessage());
        }

        @Override
        public void onNext(String s) {
            if (!TextUtils.isEmpty(s)) {
                showSucceed(getString(R.string.hint_post_succeed));
            } else {
                showError();
            }
        }
    };

    public static void start(Context context, Rect rect) {
        Intent starter = new Intent(context, PostNeedActivity.class);
        starter.putExtra(KEY_LEFT, rect.left);
        starter.putExtra(KEY_TOP, rect.top);
        starter.putExtra(KEY_RIGHT, rect.right);
        starter.putExtra(KEY_BOTTOM, rect.bottom);
        context.startActivity(starter);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_post_need;
    }

    @Override
    protected void beforeInflating() {
        super.beforeInflating();
        App.get(this).getAppComponent().plus(new OauthModule()).inject(this);
    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {
        super.finishInflating(savedInstanceState);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Observable<CharSequence> descriptionObservable = RxTextView.textChanges(descriptionEt);
        Observable<CharSequence> telObservable = RxTextView.textChanges(telEt);
        Observable<CharSequence> priceObservable = RxTextView.textChanges(priceEt).skip(1);

        addSubscription(Observable.combineLatest(
                descriptionObservable, telObservable, priceObservable,
                (description, tel, price) -> {
                    if (description.length() > getResources().getInteger(R.integer.max_description_count)) {
                        descriptionInputArea.setError("简单点,说话的方式简单点");
                        return false;
                    } else {
                        descriptionInputArea.setError(null);
                    }

                    if (TextUtils.isEmpty(tel.toString())) {
                        telInputArea.setError("手机号码不正确");
                        return false;
                    } else {
                        telInputArea.setError(null);
                    }

                    if (!Validator.isPrice(price.toString())) {
                        priceInputArea.setError("只能带一位小数");
                        return false;
                    } else {
                        priceInputArea.setError(null);
                    }
                    return true;
                }).subscribe(super::toggleActionButton));

        addSubscription(RxTextView.editorActions(priceEt)
                .map(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(done -> {
                    if (done) {
                        onPost();
                    }
                }));

        telEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                final String tel = accountMangerLazy.get().getTel();
                if (!TextUtils.isEmpty(tel)) {
                    telEt.setText(tel);
                }
            }
        });

        toggleActionButton(false);
    }

    @Override
    public void toggleActionButton(boolean activated) {
        super.toggleActionButton(activated);
        checkBtn.setClickable(activated);
        checkBtn.setEnabled(activated);
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
        animateChildIn(descriptionInputArea, 1).start();
        animateChildIn(telInputArea, 2).start();
        animateChildIn(priceInputArea, 3).start();
        animateChildIn(actionButton, 4).withEndAction(this::raiseChildrenUp).start();
    }

    private void raiseChildrenUp() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        raise(descriptionInputArea, 1);
        raise(telInputArea, 2);
        raise(priceInputArea, 3);
    }

    @OnClick(R.id.action_btn)
    public void onClickActionButton() {
        onPost();
    }

    @OnClick(R.id.check_btn)
    public void onPost() {
        descriptionInputArea.setError(null);
        telInputArea.setError(null);
        priceInputArea.setError(null);
        actionButton.setProgress(0);

        ImeUtils.hideIme(actionButton);

        final String description = descriptionEt.getText().toString();
        final String price = priceEt.getText().toString();

        addSubscription(accountMangerLazy.get().postNeeds(description, price)
                .doOnSubscribe(this::showProgress)
                .subscribe(postObserver));
    }

}
