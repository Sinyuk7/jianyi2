package com.sinyuk.jianyi.ui.player;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.transition.ArcMotion;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.ui.BaseActivity;
import com.sinyuk.jianyi.utils.ImeUtils;
import com.sinyuk.jianyi.utils.morph.MorphButtonToDialog;
import com.sinyuk.jianyi.utils.morph.MorphDialogToButton;
import com.sinyuk.jianyi.utils.morph.MorphDialogToFab;
import com.sinyuk.jianyi.utils.morph.MorphFabToDialog;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16/9/26.
 */

public class MessageView extends BaseActivity {
    private static final String USERNAME = "username";
    private static final String TEL = "tel";
    //    private static final String IS_MORPH_BUTTON = "is_morph_button";
    @BindView(R.id.send_to_hint)
    TextView sendToHint;
    @BindView(R.id.send_to_tv)
    TextView sendToTv;
    @BindView(R.id.message_et)
    TextInputEditText messageEt;
    @BindView(R.id.message_input_layout)
    TextInputLayout messageInputLayout;
    @BindView(R.id.send_btn)
    Button sendBtn;
    @BindView(R.id.cancel_btn)
    Button cancelBtn;
    @BindView(R.id.container)
    RelativeLayout container;
    @BindColor(R.color.grey_600)
    int colorDisable;
    @BindColor(R.color.colorAccent)
    int colorEnable;

    private String toName;
    private String toTel;

    public static Intent newIntent(Context context, String username, String tel) {
        Intent starter = new Intent(context, MessageView.class);
        starter.putExtra(USERNAME, username);
        starter.putExtra(TEL, tel);
        return starter;
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_massager;
    }

    @Override
    protected void beforeInflating() {
        toName = getIntent().getStringExtra(USERNAME);
        toTel = getIntent().getStringExtra(TEL);
    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {
        if (!TextUtils.isEmpty(toName)) {
            sendToTv.setText(toName);
        }
        setupSharedElementTransitions1();
        addSubscription(RxTextView.textChanges(messageEt)
                .skip(1)
                .map(TextUtils::isEmpty)
                .subscribe(this::toggleSendButton));

    }

    private void toggleSendButton(Boolean isEmpty) {
        if (isEmpty) {
            messageInputLayout.setError(getString(R.string.hint_empty_content));
        } else {
            messageInputLayout.setError(null);
        }
        int textColor = isEmpty ? colorDisable : colorEnable;
        sendBtn.setTextColor(textColor);
        sendBtn.setClickable(!isEmpty);
        sendBtn.setEnabled(!isEmpty);
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    public void dismiss() {
        setResult(Activity.RESULT_CANCELED);
        supportFinishAfterTransition();
    }

    @OnClick({R.id.container, R.id.cancel_btn})
    public void cancel() {
        delayDismiss();
    }

    @OnClick(R.id.send_btn)
    public void toSend() {
        if (!TextUtils.isEmpty(toTel)) {
            sendSMS(toTel, messageEt.getText().toString());
        }
        delayDismiss();
        Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();

    }

    private void delayDismiss() {
        ImeUtils.hideIme(container);
        container.postDelayed(this::dismiss, 400);
    }


    /**
     * 直接调用短信接口发短信，不含发送报告和接受报告
     *
     * @param phoneNumber
     * @param message
     */
    public void sendSMS(String phoneNumber, String message) {
        String username = "某个人";
        if (!TextUtils.isEmpty(toName)) {
            username = toName;
        }
        message = "来自简易上的 \"" + username + "\":" + message;        // 获取短信管理器

        SmsManager smsManager = SmsManager.getDefault();
        // 拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, null, null);
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setupSharedElementTransitions1() {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        Interpolator easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);

        MorphFabToDialog sharedEnter = new MorphFabToDialog();
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);

        MorphDialogToFab sharedReturn = new MorphDialogToFab();
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);

        if (container != null) {
            sharedEnter.addTarget(container);
            sharedReturn.addTarget(container);
        }
        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupSharedElementTransitions2() {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        Interpolator easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);

        MorphButtonToDialog sharedEnter = new MorphButtonToDialog();
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);

        MorphDialogToButton sharedReturn = new MorphDialogToButton();
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);

        if (container != null) {
            sharedEnter.addTarget(container);
            sharedReturn.addTarget(container);
        }
        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }
}
