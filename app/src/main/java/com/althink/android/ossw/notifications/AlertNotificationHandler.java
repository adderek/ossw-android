package com.althink.android.ossw.notifications;

import android.util.Log;

import com.althink.android.ossw.notifications.message.AlertNotificationMessageBuilder;
import com.althink.android.ossw.notifications.message.NotificationMessageBuilder;
import com.althink.android.ossw.notifications.model.Notification;
import com.althink.android.ossw.notifications.model.SimpleNotification;
import com.althink.android.ossw.service.OsswService;
import com.althink.android.ossw.service.OsswServiceProvider;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by krzysiek on 25/07/15.
 */
public class AlertNotificationHandler {

    private String TAG = this.getClass().getSimpleName();

    private static Notification lastNotification;

    private static Timer timer;

    private OsswServiceProvider provider;

    public AlertNotificationHandler(OsswServiceProvider provider) {
        this.provider = provider;
    }

    public void handleNotificationStart(Notification notification) {
        Log.i(TAG, "HANDLE notification START: " + notification);
        if (!(notification instanceof SimpleNotification)) {
            Log.i(TAG, "HANDLE notification START - SKIP");
            return;
        }

        if (lastNotification != null) {
            Log.i(TAG, "SKIP, other alert notification in progress");
            return;
        }
        lastNotification = notification;

        OsswService osswBleService = provider.getService();
        if (osswBleService != null) {
            int vibration_pattern = (6 << 28) | (100 << 16) | (44 << (16 - 6));
            NotificationMessageBuilder builder = new AlertNotificationMessageBuilder(notification.getCategory(), ((SimpleNotification) notification).getTitle(), ((SimpleNotification) notification).getText(), notification.getOperations());
            notification.setExternalId(osswBleService.uploadNotification(notification.getType(), builder.build(), vibration_pattern, 5000, new AlertNotificationFunctionHandler(notification, osswBleService)));
            Log.i(TAG, "Start notification: " + lastNotification.getId() + ", " + lastNotification.getExternalId());

            startNotificationExtender(notification.getExternalId(), osswBleService);
        }
    }

    public void handleNotificationStop(String notificationId) {
        Log.i(TAG, "HANDLE notification STOP: " + notificationId);
        if (lastNotification != null && lastNotification.getId().equals(notificationId)) {

            stopNotificationExtender();

            OsswService osswService = provider.getService();
            if (osswService != null) {
                Log.i(TAG, "Close notification: " + lastNotification.getId() + ", " + lastNotification.getExternalId());
                osswService.closeAlertNotification(lastNotification.getExternalId());
            }

            lastNotification = null;
        }
    }

    private void startNotificationExtender(int notificationId, OsswService osswBleService) {
        Log.i(TAG, "Start notification extender: " + notificationId);
        stopNotificationExtender();
        timer = new Timer();
        timer.schedule(new NotificationExtenderTimer(notificationId, osswBleService), 1000, 1000);
    }

    private void stopNotificationExtender() {
        Log.i(TAG, "Stop notification extender");
        if (timer != null) {
            //Log.i(TAG, "Cancel timer");
            timer.cancel();
            timer = null;
        }
    }

    class NotificationExtenderTimer extends TimerTask {

        private int notificationId;
        private OsswService osswBleService;

        public NotificationExtenderTimer(int notificationId, OsswService osswBleService) {
            this.notificationId = notificationId;
            this.osswBleService = osswBleService;
        }

        @Override
        public void run() {
            Log.i(TAG, "Extend notification: " + lastNotification.getId() + ", " + lastNotification.getExternalId());
            osswBleService.extendAlertNotification(notificationId, 5000);
        }
    }
}