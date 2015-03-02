package cl.iluminadoschile.pako.floatingdiv;

/*
Copyright 2011 jawsware international

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class CustomOverlayService extends OverlayService implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String LOG_TAG = "CustomOverlayService";

	public static CustomOverlayService instance;

	private CustomOverlayView overlayView;

    Handler handler;

    private int milisecsToWait = 100;

    Thread monitor_for_ingress;
    private boolean monitor_for_ingress_status;

    private String activation_method;

    @Override
	public void onCreate() {
		super.onCreate();

		instance = this;
        //instance.startForeground(1337, this.foregroundNotification(1337));
        overlayView = new CustomOverlayView(this);
        Toast.makeText(this, "Service gogress running", Toast.LENGTH_SHORT).show();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        activation_method = preferences.getString(Constants.SETTINGS.ACTIVATION_METHOD_KEY,
                Constants.SETTINGS.ACTIVATION_METHOD_ALONGWITHINGRESS
                );

        monitor_for_ingress_status = false;
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT){
            createMonitorIngressRunning();
        }

        Notification notification = foregroundNotification(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);
	}

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (Constants.SETTINGS.ACTIVATION_METHOD_KEY.equals(key)){
            activation_method = prefs.getString(Constants.SETTINGS.ACTIVATION_METHOD_KEY,
                    Constants.SETTINGS.ACTIVATION_METHOD_ALONGWITHINGRESS
            );
            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT){
                if(activation_method.equals( Constants.SETTINGS.ACTIVATION_METHOD_ALONGWITHINGRESS) ){
                    startMonitorIngressRunning();
                }else{
                    stoptMonitorIngressRunning();
                }
            }
            Toast.makeText(CustomOverlayService.instance, "Service needs to restart to reflect changes", Toast.LENGTH_SHORT).show();
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);

            Notification notification = foregroundNotification(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
            mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        }
    }

    private void createMonitorIngressRunning() {
        handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                //Toast.makeText(CustomOverlayService.instance, "timeout!", Toast.LENGTH_SHORT).show();
            }

        };

        monitor_for_ingress = new Thread(){
            public void run() {
                // TODO Auto-generated method stub
                while(true)
                {
                    synchronized (this) {
                        try {
                            wait(milisecsToWait);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if( !monitor_for_ingress_status )
                                        return;
                                    if (ingress_running_in_foreground()) {
                                        if (!overlayView.isVisible()) {
                                            overlayView.setVisible();
                                            overlayView.setText("HI!\nIngress is running");
                                        } else {
                                            /*
                                            Log.i("julin", "overlay es visible e ingress esta en fg");
                                            */
                                        }
                                    } else {
                                        if (overlayView.isVisible()) {
                                            overlayView.setInvisible();
                                            overlayView.setText("HI!\nIngress is NOT running");
                                        } else {
                                            /*
                                            Log.i("julin", "overlay es invisible e ingress no esta en fg");
                                            try {
                                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                                intent.setClassName("com.nianticproject.ingress", "com.nianticproject.ingress.NemesisActivity");
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }catch (Exception e){
                                                Log.e("julin", e.getMessage());
                                            }
                                            */
                                        }
                                    }
                                }
                            });
                            handler.sendEmptyMessage(1);

                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

            }
        };
    }

    private void startMonitorIngressRunning() {
        if(monitor_for_ingress != null) {
            Log.i(LOG_TAG, "starting monitoring ");
            Log.i(LOG_TAG, "Estado actual thread " + monitor_for_ingress.getState());
            monitor_for_ingress.start();
            monitor_for_ingress_status = true;
        }
    }
    private void stoptMonitorIngressRunning() {
        if(monitor_for_ingress != null) {
            Log.i(LOG_TAG, "Estado actual thread " + monitor_for_ingress.getState());
            Log.i(LOG_TAG, "stopping monitoring ");
            monitor_for_ingress_status = false;
        }
    }

    private void runOnUiThread(Runnable runnable){
        handler.post(runnable);
    }

    private boolean ingress_running_in_background(){
        String ingress_app_name = this.getApplicationContext().getString(R.string.ingress_process_name);

        ActivityManager activityManager = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        boolean running = false;
        for(int i = 0; i < procInfos.size(); i++){
            String processName = procInfos.get(i).processName;
            if(processName.equals(ingress_app_name))
            {
                running = true;
            }
        }

        return running;
    }

    private boolean ingress_running_in_foreground(){
        String ingress_app_name = this.getApplicationContext().getString(R.string.ingress_process_name);

        ActivityManager am = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
        // The first in the list of RunningTasks is always the foreground task.
        int index2ask = 0;
        if( overlayView.isVisible() ){
            index2ask = 1;
        }
        ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(index2ask+1).get(0);
        String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
        PackageManager pm = this.getPackageManager();
        PackageInfo foregroundAppPackageInfo = null;
        try {
            foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();
        Log.i("julin", foregroundTaskAppName + " " +index2ask + " " + (overlayView.isVisible()?"SI":"NO"));
        return foregroundTaskAppName.equals("Ingress");
    }

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (overlayView != null) {
            instance.stopForeground(true);
			overlayView.destory();
		}

	}
	
	static public void stop() {
		if (instance != null) {
            instance.stopForeground(true);
			instance.stopSelf();
		}
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || null == intent.getAction() ){
            Log.i(LOG_TAG, "Received null Foreground Intent ");

            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                if (activation_method.equals( Constants.SETTINGS.ACTIVATION_METHOD_ALONGWITHINGRESS) ){
                    startMonitorIngressRunning();
                }
            }

        } else if ( intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION) ){
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            overlayView.setVisible();

            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                if (activation_method.equals( Constants.SETTINGS.ACTIVATION_METHOD_ALONGWITHINGRESS) ){

                    startMonitorIngressRunning();
                }
            }

        } else if (intent.getAction().equals(
                Constants.ACTION.SETTINGS_ACTION)) {
            Log.i(LOG_TAG, "Received Settings Intent");

            Intent settingsIntent = new Intent(Intent.ACTION_MAIN);
            settingsIntent.setClassName(Constants.ACTION.prefix, Constants.ACTION.prefix + ".SettingsActivity");
            settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(settingsIntent);

        } else if (intent.getAction().equals(
                Constants.ACTION.PAUSEFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Pause Foreground Intent");

            overlayView.setInvisible();

        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");

            overlayView.setInvisible();
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

	@Override
	protected Notification foregroundNotification(int notificationId) {
		Notification notification;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
            activation_method.equals( Constants.SETTINGS.ACTIVATION_METHOD_MANUAL) ){
            Intent settingsIntent = new Intent(Intent.ACTION_MAIN);
            settingsIntent.setClassName(Constants.ACTION.prefix, Constants.ACTION.prefix + ".SettingsActivity");
            settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    settingsIntent, 0);

            Intent startFgIntent = new Intent(this, CustomOverlayService.class);
            startFgIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            PendingIntent pstartFgIntent = PendingIntent.getService(this, 0,
                    startFgIntent, 0);

            Intent pauseFgIntent = new Intent(this, CustomOverlayService.class);
            pauseFgIntent.setAction(Constants.ACTION.PAUSEFOREGROUND_ACTION);
            PendingIntent ppauseFgIntent = PendingIntent.getService(this, 0,
                    pauseFgIntent, 0);

            Intent stopFgIntent = new Intent(this, CustomOverlayService.class);
            stopFgIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            PendingIntent pstopFgIntent = PendingIntent.getService(this, 0,
                    stopFgIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher);

            notification = new NotificationCompat.Builder(this)
                    .setContentTitle( getString(R.string.title_notification) )
                    .setTicker( getString(R.string.title_notification) )
                    .setContentText( getString(R.string.message_notification) )
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setLargeIcon(
                            Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .addAction(android.R.drawable.ic_media_play,
                            "Start", pstartFgIntent)
                    .addAction(android.R.drawable.ic_media_pause,
                            "Pause", ppauseFgIntent)
                    .addAction(android.R.drawable.ic_delete,
                            "Stop", pstopFgIntent)
                    .build();

        }else {
            notification = new Notification(R.drawable.ic_launcher, getString(R.string.title_notification), System.currentTimeMillis());

            notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_ONLY_ALERT_ONCE;

            notification.setLatestEventInfo(this, getString(R.string.title_notification), getString(R.string.message_notification_manual), notificationIntent());
        }
		return notification;
	}


	private PendingIntent notificationIntent() {
		Intent intent = new Intent(this, CustomOverlayHideActivity.class);

		PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		return pending;
	}

}
