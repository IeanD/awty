package edu.washington.ieand.arewethereyet

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val receiver = AWTYReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }

        // Register BroadcastReceiver
        val filter = IntentFilter()
        filter.addAction(BROADCAST)
        registerReceiver(receiver, filter)

        // Set up variables
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val messageInput: EditText = findViewById(R.id.editText_main_smsMessageInput)
        val phoneNumInput: EditText = findViewById(R.id.editText_main_targetPhoneNumberInput)
        val intervalInput: EditText = findViewById(R.id.editText_main_timeInput)
        val startStopBtn: Button = findViewById(R.id.button_main_startStopService)
        var buttonToggle = true

        // Set button listener
        startStopBtn.setOnClickListener {
            if (buttonToggle) {
                when {
                    messageInput.text.toString().isBlank() -> showErrorToast("Message to send can't be blank!")
                    phoneNumInput.text.toString().length < 4 -> showErrorToast("Phone number must be standard length (between four and fifteen digits).")
                    phoneNumInput.text.toString().length > 15 -> showErrorToast("Phone number must be standard length (between four and fifteen digits).")
                    intervalInput.text.toString().isBlank() -> showErrorToast("You must enter a time (in minutes) to pause between messages.")
                    intervalInput.text.toString() == "0" -> showErrorToast("The time between messages cannot be zero.")
                    else -> {
                        val intent = Intent(BROADCAST)
                                .putExtra(AWTYReceiver.MESSAGE, messageInput.text.toString())
                                .putExtra(AWTYReceiver.TAR_PHONE_NUM, phoneNumInput.text.toString())
                        val pendingIntent = PendingIntent.getBroadcast(
                                applicationContext,
                                0,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                5000,
                                (intervalInput.text.toString().toLong() * 60 * 1000),
                                pendingIntent
                        )
                        startStopBtn.text = getString(R.string.button_main_stop)
                        buttonToggle = !buttonToggle
                    }
                }
            }
            else {
                alarmManager.cancel(PendingIntent.getBroadcast(applicationContext, 0,
                        Intent(BROADCAST), PendingIntent.FLAG_UPDATE_CURRENT))
                startStopBtn.text = getString(R.string.button_main_start)
                buttonToggle = !buttonToggle
            }
        }
    }

    override fun onStop() {
        unregisterReceiver(receiver)
        super.onStop()
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    companion object {
        const val TAG = "MainActivity"
        const val BROADCAST = "edu.washington.ieand.arewethereyet.BROADCAST"
    }
}
