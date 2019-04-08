package com.example.computer.notificationscheduler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v4.app.NotificationCompat;

public class NotificationJobService extends JobService {
    private NotificationManager mNotifyManager;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    /**
     * Called to indicate that the job has begun executing.  Override this method with the
     * logic for your job.  Like all other component lifecycle callbacks, this method executes
     * on your application's main thread.
     * <p>
     * Return {@code true} from this method if your job needs to continue running.  If you
     * do this, the job remains active until you call
     * {@link #jobFinished(JobParameters, boolean)} to tell the system that it has completed
     * its work, or until the job's required constraints are no longer satisfied.  For
     * example, if the job was scheduled using
     * {@link JobInfo.Builder#setRequiresCharging(boolean) setRequiresCharging(true)},
     * it will be immediately halted by the system if the user unplugs the device from power,
     * the job's {@link #onStopJob(JobParameters)} callback will be invoked, and the app
     * will be expected to shut down all ongoing work connected with that job.
     * <p>
     * The system holds a wakelock on behalf of your app as long as your job is executing.
     * This wakelock is acquired before this method is invoked, and is not released until either
     * you call {@link #jobFinished(JobParameters, boolean)}, or after the system invokes
     * {@link #onStopJob(JobParameters)} to notify your job that it is being shut down
     * prematurely.
     * <p>
     * Returning {@code false} from this method means your job is already finished.  The
     * system's wakelock for the job will be released, and {@link #onStopJob(JobParameters)}
     * will not be invoked.
     *
     * @param params Parameters specifying info about this job, including the optional
     *               extras configured with {@link JobInfo.Builder#setExtras(PersistableBundle).
     *               This object serves to identify this specific running job instance when calling
     *               {@link #jobFinished(JobParameters, boolean)}.
     * @return {@code true} if your service will continue running, using a separate thread
     * when appropriate.  {@code false} means that this job has completed its work.
     */
    @Override
    public boolean onStartJob(JobParameters params) {
        createNotificationChannel();

        //Set up the notification content intent to launch the app when clicked
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(getString(R.string.job_service))
                .setContentText(getString(R.string.job_content_text))
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_job_running)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        mNotifyManager.notify(0, builder.build());
        return false;
    }

    /**
     * This method is called if the system has determined that you must stop execution of your job
     * even before you've had a chance to call {@link #jobFinished(JobParameters, boolean)}.
     *
     * <p>This will happen if the requirements specified at schedule time are no longer met. For
     * example you may have requested WiFi with
     * {@link JobInfo.Builder#setRequiredNetworkType(int)}, yet while your
     * job was executing the user toggled WiFi. Another example is if you had specified
     * {@link JobInfo.Builder#setRequiresDeviceIdle(boolean)}, and the phone left its
     * idle maintenance window. You are solely responsible for the behavior of your application
     * upon receipt of this message; your app will likely start to misbehave if you ignore it.
     * <p>
     * Once this method returns, the system releases the wakelock that it is holding on
     * behalf of the job.</p>
     *
     * @param params The parameters identifying this job, as supplied to
     *               the job in the {@link #onStartJob(JobParameters)} callback.
     * @return {@code true} to indicate to the JobManager whether you'd like to reschedule
     * this job based on the retry criteria provided at job creation-time; or {@code false}
     * to end the job entirely.  Regardless of the value returned, your job must stop executing.
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription(getString(R.string.channel_description));

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }
}
