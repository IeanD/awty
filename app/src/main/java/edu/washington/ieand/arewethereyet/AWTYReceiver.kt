package edu.washington.ieand.arewethereyet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast

class AWTYReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.i(MainActivity.TAG, "Broadcast received")


        when (intent?.action) {
            MainActivity.BROADCAST -> {
                val smsManager = SmsManager.getDefault()
                val phoneNum = intent.extras[TAR_PHONE_NUM] as String
                val msg = intent.extras[MESSAGE] as String
                var toastPhoneNum = phoneNum
                if (phoneNum.length == 10) {
                    toastPhoneNum = "(" + phoneNum.substring(0..2) + ") " + phoneNum.substring(3..5) + "-" + phoneNum.substring(6)
                }
                smsManager.sendTextMessage(phoneNum, null, msg, null, null)
                Toast.makeText(context, "The following message was sent to $toastPhoneNum: \"$msg\"", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val MESSAGE = "edu.washington.ieand.arewethereyet.SMS_BODY"
        const val TAR_PHONE_NUM = "edu.washington.ieand.arewethereyet.TARGET_PHONE_NUMBER"
    }
}