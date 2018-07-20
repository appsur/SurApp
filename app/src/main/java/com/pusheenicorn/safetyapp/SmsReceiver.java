package com.pusheenicorn.safetyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class SmsReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }
                String sender = messages[0].getOriginatingAddress();
                String message = sb.toString();
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                // prevent any other broadcast receivers from receiving broadcast
                // abortBroadcast();
            }
        }
    }
//        String phoneNumbers = PreferenceManager.getDefaultSharedPreferences(
//                context).getString("phone_entries", "");
//        StringTokenizer tokenizer = new StringTokenizer(phoneNumbers, ",");
//        Set<String> phoneEnrties = new HashSet<String>();
//        while (tokenizer.hasMoreTokens()) {
//            phoneEnrties.add(tokenizer.nextToken().trim());
//        }
//        Bundle bundle = intent.getExtras();
//        Object[] pdus = (Object[]) bundle.get("pdus");
//        SmsMessage[] messages = new SmsMessage[pdus.length];
//        for (int i = 0; i < messages.length; i++) {
//            messages[i]= SmsMessage.createFromPdu((byte[]) pdus[i]);
//            String address = messages[i].getOriginatingAddress();
//            if (phoneEnrties.contains(address)) {
//                Intent newintent = new Intent(context, ChatActivity.class);
//                newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                newintent.putExtra("address", address);
//                newintent.putExtra("message",
//                        messages[i].getDisplayMessageBody());
//                context.startActivity(newintent);
//            }
//        }
//    }
}
