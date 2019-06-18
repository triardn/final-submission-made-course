package com.buruhkoding.secondsubmission;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import static android.content.Context.ALARM_SERVICE;

public class NotificationReceiver extends BroadcastReceiver {
    public static final String TYPE_OPEN_APP = "OpenApp";
    public static final String TYPE_RELEASE_MOVIE = "ReleaseMovie";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_TYPE = "type";

    private final int ID_OPENAPP = 100;
    private final int ID_RELEASEMOVIE = 101;

    private static final String API_KEY = BuildConfig.TMDB_API_KEY;

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String title = intent.getStringExtra(EXTRA_TITLE);
        final String message = intent.getStringExtra(EXTRA_MESSAGE);
        String type = intent.getStringExtra(EXTRA_TYPE);

        final int notifId = type.equalsIgnoreCase(TYPE_OPEN_APP) ? ID_OPENAPP : ID_RELEASEMOVIE;

        if (notifId != 100) {
            AsyncHttpClient client = new AsyncHttpClient();
            String url = "https://api.themoviedb.org/3/movie/upcoming?api_key=" + API_KEY + "&language=en-US";

            client.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String result = new String(responseBody);
                        JSONObject responseObject = new JSONObject(result);
                        JSONArray list = responseObject.getJSONArray("results");

                        Date c = Calendar.getInstance().getTime();
                        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
                        String currentDateString = currentDate.format(c);

                        for (int i = 0 ; i < list.length() ; i++) {
                            JSONObject movie = list.getJSONObject(i);
                            String date = movie.getString("release_date");

                            if (date.equalsIgnoreCase(currentDateString)) {
                                Log.d("BEST MATCH", "MATCH NEHHH!");
                                String notifTitle = movie.getString("title") + " sudah tayang!";
                                String notifMessage = "Ayo cek detail filmnya di Movie Finder! App sekarang!";

                                showAlarmNotification(context, notifTitle, notifMessage, notifId, movie);
                            } else {
                                Log.d("Gagal", "gak match nih tanggal "+currentDateString+ " sama "+movie.getString("release_date"));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d(TYPE_RELEASE_MOVIE, "onFailure: FAILED"+error.getMessage());
                }
            });
        } else {
            showAlarmNotification(context, title, message, notifId, null);
        }
    }

    public void setAlarm (Context context, String type) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        int notifId = 0;

        Calendar calendar = Calendar.getInstance();

        if (type.equalsIgnoreCase(TYPE_OPEN_APP)) {
            calendar.set(Calendar.HOUR_OF_DAY, 7);
            notifId = ID_OPENAPP;
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            notifId = ID_RELEASEMOVIE;
        }

        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NotificationReceiver.EXTRA_TITLE, "Hai bosku, lama tak jumpa ya!");
        intent.putExtra(NotificationReceiver.EXTRA_MESSAGE, "Yuk kunjungi lagi Movie Finder! siapa tau film yang kamu tunggu-tunggu sudah tayang~");
        intent.putExtra(NotificationReceiver.EXTRA_TYPE, type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notifId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void showAlarmNotification(Context context, String title, String message, int notifId, JSONObject movie) {
        String CHANNEL_ID = "Channel_1";
        String CHANNEL_NAME = "AlarmManager channel";

        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_star_yellow_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound)
                .setAutoCancel(true);

        if (movie != null) {
            Intent notificationIntent = new Intent(context, MovieDetailActivity.class);
            try {
//                notificationIntent.putExtra(MovieDetailActivity.EXTRA_ID, movie.getInt("id"));
//                notificationIntent.putExtra(MovieDetailActivity.EXTRA_TITLE, movie.getString("title"));
//                notificationIntent.putExtra(MovieDetailActivity.EXTRA_DESC, movie.getString("overview"));
//                notificationIntent.putExtra(MovieDetailActivity.EXTRA_IMG, "https://image.tmdb.org/t/p/w342" + movie.getString("poster_path"));
//                notificationIntent.putExtra(MovieDetailActivity.EXTRA_VOTEAVG, movie.getDouble("vote_average"));
//                notificationIntent.putExtra(MovieDetailActivity.EXTRA_VOTECOUNT, movie.getInt("vote_count"));
                Movie movieData = new Movie();
                movieData.setId(movie.getInt("id"));
                movieData.setTitle(movie.getString("title"));
                movieData.setDesc(movie.getString("overview"));
                movieData.setImg("https://image.tmdb.org/t/p/w342" + movie.getString("poster_path"));
                movieData.setVoteCount(movie.getInt("vote_count"));
                movieData.setVoteAverage(movie.getDouble("vote_average"));
                notificationIntent.putExtra(MovieDetailActivity.EXTRA_MOVIE, movieData);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
        } else {
            Intent notificationIntent = new Intent(context, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});

            builder.setChannelId(CHANNEL_ID);

            if (notificationManagerCompat != null) {
                notificationManagerCompat.createNotificationChannel(channel);
            }
        }

        Notification notification = builder.build();

        if (notificationManagerCompat != null) {
            notificationManagerCompat.notify(notifId, notification);
        }
    }

    public void cancelAlarmNotification(Context context, String type) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        int requestCode = type.equalsIgnoreCase(TYPE_OPEN_APP) ? ID_OPENAPP : ID_RELEASEMOVIE;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        pendingIntent.cancel();

        if(alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public boolean isAlarmSet(Context context, String type) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        int requestCode = type.equalsIgnoreCase(TYPE_OPEN_APP) ? ID_OPENAPP : ID_RELEASEMOVIE;

        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE) != null;
    }
}
