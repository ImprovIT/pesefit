package fr.intech.pesefit;

import android.app.IntentService;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Date;
import java.util.List;

/**
 * Created by Alexandre on 14/09/2016.
 */
public class ActivityRecognizedService extends IntentService {

    private int _currentState;
    private int _previousState;


    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }


    @Override
    protected void onHandleIntent( Intent intent ) {
        //check if the intent contains an ActivityRecognitionResult
        if ( ActivityRecognitionResult.hasResult( intent ) )
        {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            //DetectedActivity detectedActivity = result.getMostProbableActivity();
            //Log.d("Activité", String.valueOf(detectedActivity.getType()));
/*            if(detectedActivity.getType() == DetectedActivity.STILL){
                Log.d("Activité : ", "IMMOBILE");
            }
            if(detectedActivity.getType() == DetectedActivity.WALKING){
                Log.d("Activité : ", "IMMOBILE");
            }*/
            long time = result.getTime();

          /*  String formattedDate = DateUtils.formatDateTime(getApplicationContext(),
                    time, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR);
            Log.d("Time", formattedDate);
          */

            handleDetectedActivity(result.getMostProbableActivity(), time);



        }
    }

    private void handleDetectedActivity(DetectedActivity activity, long time)
    {
            switch( activity.getType() )
            {
                case DetectedActivity.IN_VEHICLE: {
                    Log.d( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.d( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.d( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.d( "ActivityRecogition", "Still: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.d( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                    builder.setDefaults(Notification.DEFAULT_VIBRATE);
                    builder.setContentText( "Are you walking? " + activity.getConfidence() );
                    builder.setSmallIcon( R.mipmap.ic_launcher );
                    builder.setContentTitle( getString( R.string.app_name ) );
                    NotificationManagerCompat.from(this).notify(0, builder.build());
                    break;
                }
            }

        _currentState = activity.getType();

        if (_previousState != _currentState)
        {
            // Nouvelle ligne en bdd et mettre à jour la dernière
        }
        else
        {
            // On check la date dans la dernière ligne de la bdd.
            // Si la current_date - date_bdd >= 2mn
        }



        _previousState = _currentState;
    }
}
