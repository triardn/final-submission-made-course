package com.buruhkoding.secondsubmission;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    Switch reminder_open_app_toggle = null;
    Switch reminder_relase_movie_toggle = null;

    private NotificationReceiver notificationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.alarm_setting));
        }

        notificationReceiver = new NotificationReceiver();

        reminder_open_app_toggle = findViewById(R.id.reminder_open_app);
        reminder_relase_movie_toggle = findViewById(R.id.reminder_release_movie);

        if (notificationReceiver.isAlarmSet(SettingActivity.this, NotificationReceiver.TYPE_OPEN_APP)) {
            reminder_open_app_toggle.setChecked(true);
        }

        if (notificationReceiver.isAlarmSet(SettingActivity.this, NotificationReceiver.TYPE_RELEASE_MOVIE)) {
            reminder_relase_movie_toggle.setChecked(true);
        }

        reminder_open_app_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int i = isChecked ? 1 : 0;
                switch (i) {
                    case 1 :
                        notificationReceiver.setAlarm(SettingActivity.this, NotificationReceiver.TYPE_OPEN_APP);
                        Toast.makeText(SettingActivity.this, "Turn on Open App notification", Toast.LENGTH_SHORT).show();
                        break;

                    case 0 :
                        notificationReceiver.cancelAlarmNotification(SettingActivity.this, NotificationReceiver.TYPE_OPEN_APP);
                        Toast.makeText(SettingActivity.this, "turn off Open App notification", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        reminder_relase_movie_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int i = isChecked ? 1 : 0;
                switch (i) {
                    case 1 :
                        notificationReceiver.setAlarm(SettingActivity.this, NotificationReceiver.TYPE_RELEASE_MOVIE);
                        Toast.makeText(SettingActivity.this, "Turn on Release Movie notification", Toast.LENGTH_SHORT).show();
                        break;

                    case 0 :
                        notificationReceiver.cancelAlarmNotification(SettingActivity.this, NotificationReceiver.TYPE_RELEASE_MOVIE);
                        Toast.makeText(SettingActivity.this, "turn off Release Movie notification", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}
