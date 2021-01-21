package com.example.recycle.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.recycle.R;

public class ServicioLogros extends Service {

    //Notificaciones logros
    public static NotificationManager notificationManager;
    static final String CANAL_ID = "logros";
    static final int NOTIFICACION_ID = 710;


    @Override
    public int onStartCommand(Intent intent, int flags, int idArranque) {
        notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CANAL_ID, "Mis Notificaciones",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Descripcion del canal");
            notificationManager.createNotificationChannel(notificationChannel);
        }
        return START_STICKY;
    }
    @Override public void onDestroy() {

    }
    @Override public IBinder onBind(Intent intencion) {
        return null;
    }

    @SuppressLint("MissingPermission")
    public static void mandarNotificacion(Activity activity, Context context, int progreso, int check, String nombreLogro) {
        if (progreso == check) {

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            long[] pattern = {Notification.DEFAULT_VIBRATE, Notification.DEFAULT_VIBRATE};
            NotificationCompat.Builder notificacion =
                    new NotificationCompat.Builder(context, CANAL_ID)
                            .setSmallIcon(R.mipmap.logoletrav2)
                            .setAutoCancel(true)
                            .setContentTitle("¡Logro desbloqueado!")
                            .setContentText("¡Felicidades! Has desbloqueado el logro " + "\"" + nombreLogro + "\"" + ". ¡Sigue así!");
            PendingIntent intencionPendiente = PendingIntent.getActivity(
                    context, 0, new Intent(context, ActividadLogros.class), 0);
            notificacion.setContentIntent(intencionPendiente);
            RingtoneManager.getRingtone(context, alarmSound).play();
            notificationManager.notify(NOTIFICACION_ID, notificacion.build());
        }
    }
}
