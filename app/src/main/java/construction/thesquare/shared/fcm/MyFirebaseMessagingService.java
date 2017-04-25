package construction.thesquare.shared.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import construction.thesquare.R;
import construction.thesquare.employer.closejob.CloseJobActivity;
import construction.thesquare.employer.mygraftrs.WorkerDetailsActivity;
import construction.thesquare.employer.myjobs.activity.ViewWorkerProfileActivity;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.main.activity.MainActivity;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.worker.jobdetails.JobDetailActivity;

//import io.intercom.android.sdk.push.IntercomPushClient;

/**
 * Created by gherg on 3/1/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = "myFirebase: ";
    private String employerEmail;
    private String workerEmail;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        TextTools.log(TAG, "from: " + remoteMessage.getFrom());

        try {
            employerEmail = SharedPreferencesManager.getInstance(getApplicationContext())
                    .loadSessionInfoEmployer().getEmail();
            if (null != employerEmail) {
                TextTools.log(TAG, "employer email: " + employerEmail);
            }
            workerEmail = SharedPreferencesManager.getInstance(getApplicationContext())
                    .loadSessionInfoWorker().getEmail();
            if (null != workerEmail) {
                TextTools.log(TAG, "worker email: " + workerEmail);
            }
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }

        if (remoteMessage.getData().size() > 0) {
            TextTools.log(TAG, "payload: " + remoteMessage.getData());
            Map<String, String> body = remoteMessage.getData();
            if (null != body.get("email")) {
                String receivedEmail = body.get("email");
                if (null != workerEmail) {
                    //
                    if (workerEmail.equals(receivedEmail)) {
                        //
                        if (null != body.get("custom_screen")) {
                            if (body.get("custom_screen").equals("true")) {
                                //
                                if (null != body.get("job_id")) {
                                    //
                                    sendDeepLinkNotificationWorker((null != body.get("message")) ?
                                                    body.get("message") : "",
                                            Integer.valueOf(body.get("job_id")));
                                    //
                                } else {
                                    sendDataNotification((null != body.get("message")) ?
                                            body.get("message") : "");
                                }
                                //
                            } else {
                                sendDataNotification((null != body.get("message")) ?
                                        body.get("message") : "");
                            }
                        } else {
                            sendDataNotification((null != body.get("message")) ?
                                    body.get("message") : "");
                        }
                    }
                }
                if (null != employerEmail) {
                    //
                    if (employerEmail.equals(receivedEmail)) {
                        //
                        if (null != body.get("custom_screen")) {
                            if (body.get("custom_screen").equals("true")) {
                                //
                                if (null != body.get("job_id") && null != body.get("worker_id")) {
                                    //
                                    sendDeepLinkNotificationEmployer((null != body.get("message")) ?
                                            body.get("message") : "",
                                            Integer.valueOf(body.get("worker_id")),
                                            Integer.valueOf(body.get("job_id")));
                                    //
                                } else if (null != body.get("job_id") && null != body.get("confirm_close")) {
                                    //
                                    confirmClosingJob((null != body.get("message")) ?
                                            body.get("message") : "",
                                            Integer.valueOf(body.get("job_id")));
                                    //
                                } else {
                                    sendDataNotification((null != body.get("message")) ?
                                            body.get("message") : "");
                                }
                                //
                            } else {
                                sendDataNotification((null != body.get("message")) ?
                                        body.get("message") : "");
                            }
                        } else {
                            sendDataNotification((null != body.get("message")) ?
                                    body.get("message") : "");
                        }
                    }
                }
            }
        }
    }

    private void proceed(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            if (null != remoteMessage.getNotification().getBody()) {
                TextTools.log(TAG, "body: " + remoteMessage.getNotification().getBody());
                sendNotification(remoteMessage.getNotification().getBody());
            }
        }
    }

    private void sendDataNotification(String messageBody) {
        TextTools.log("push...:", "standard");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("The Square Construction")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setLargeIcon(BitmapFactory
                                .decodeResource(getResources(),
                                        R.drawable.ic_big_notification))
                        .setSmallIcon(R.drawable.ic_action_the_square_sq)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendDeepLinkNotificationWorker(String messageBody, int jobId) {
        Intent intent = new Intent(this, JobDetailActivity.class);
        intent.putExtra(Constants.KEY_JOB_ID_DETAILS, jobId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("The Square Construction")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setLargeIcon(BitmapFactory
                                .decodeResource(getResources(),
                                        R.drawable.ic_big_notification))
                        .setSmallIcon(R.drawable.ic_action_the_square_sq)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendDeepLinkNotificationEmployer(String messageBody, int workerId, int jobId) {
        Intent intent = new Intent(this, ViewWorkerProfileActivity.class);
        intent.putExtra(Constants.KEY_WORKER_ID, workerId);
        intent.putExtra(Constants.KEY_JOB_ID_DETAILS, jobId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("The Square Construction")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setLargeIcon(BitmapFactory
                                .decodeResource(getResources(),
                                        R.drawable.ic_big_notification))
                        .setSmallIcon(R.drawable.ic_action_the_square_sq)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                //.setSmallIcon(android.support.v7.appcompat.R.drawable.notification_template_icon_bg)
                .setContentTitle("The Square Construction")
                .setContentText(messageBody)
                .setAutoCancel(true)
                        .setLargeIcon(BitmapFactory
                                .decodeResource(getResources(),
                                        R.drawable.ic_big_notification))
                .setSmallIcon(R.drawable.ic_action_the_square_sq)
                        .setColor(Color.RED)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void confirmClosingJob(String messageBody,
                                   int jobId) {
        Intent intent = new Intent(this, CloseJobActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                .setContentTitle("The Square Construction")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_big_notification))
                .setSmallIcon(R.drawable.ic_action_the_square_sq)
                .setColor(Color.RED)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
