/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package construction.thesquare.shared.veriphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.TextTools;

@SuppressWarnings("deprecation")
public class SmsInterceptor extends BroadcastReceiver {
    private static final String TAG = "SmsInterceptor";
    private Pattern pattern;
    private OnSmsReceivedListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            pattern = Pattern.compile("\\d{4}");
            SmsMessage[] smsMessages;
            String messageSender;
            String messageBody;
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus == null) return;
                    smsMessages = new SmsMessage[pdus.length];

                    for (int i = 0; i < smsMessages.length; i++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String format = bundle.getString("format");
                            smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                        } else {
                            smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        }

                        messageSender = smsMessages[i].getOriginatingAddress();
                        messageBody = smsMessages[i].getMessageBody();
                        Matcher matcher = pattern.matcher(messageBody);

                        if (matcher.find() && messageBody.toLowerCase().contains("square")) {
                            messageBody = matcher.group();

                            if (listener != null) listener.onSmsReceived(messageBody);

                            String toLog = "New Message: " + messageSender + " - " + messageBody;
                            TextTools.log(TAG, toLog);
                        } else {
                            TextTools.log(TAG, "Not needed message. Must ignore it. ->" + messageBody);
                            return;
                        }
                    }
                } catch (Exception e) {
                    CrashLogHelper.logException(e);
                }
            }
        }
    }

    public void setListener(OnSmsReceivedListener listener) {
        this.listener = listener;
    }
}

