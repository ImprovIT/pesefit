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

    //Interval for update when same activity
    private static final int INTERVAL = 2;

    private int _currentState;
    private static int _previousState;

    private static boolean _started = false;

    private UserDataManager _userDataManager;


    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
        _userDataManager= new UserDataManager(this);
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

          /*  String formattedDate = DateUtils.formatDateTime(getApplicationContext(),
                    time, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR);
            Log.d("Time", formattedDate);
          */

            handleDetectedActivity(result.getMostProbableActivity(), result.getTime());

        }
    }

    private void handleDetectedActivity(DetectedActivity activity, long time)
    {
            /*switch( activity.getType() )
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
*/
        _currentState = activity.getType();

/*        _userDataManager= new UserDataManager(this);
        _userDataManager.open();

        long value = _userDataManager.addUserData(new UserData(time,time, activity.getType()));
        Log.d("Value after insert : ", String.valueOf(value));

        long value2 = _userDataManager.addUserData(new UserData(time,55555, activity.getType()));
        Log.d("Value after insert : ", String.valueOf(value2));

        UserData lastUserData = _userDataManager.getLastUserData();
        Log.d("Date : ", String.valueOf(lastUserData.getDate()));
        Log.d("Durée : ", String.valueOf(lastUserData.getDuration()));
        Log.d("Activité : ", String.valueOf(lastUserData.getActivity()));

        lastUserData.setDuration(88888);

        int valueUpdate = _userDataManager.updateUserData(lastUserData);
        Log.d("Value after update : ", String.valueOf(valueUpdate));

        lastUserData = _userDataManager.getLastUserData();
        Log.d("Date : ", String.valueOf(lastUserData.getDate()));
        Log.d("Durée : ", String.valueOf(lastUserData.getDuration()));
        Log.d("Activité : ", String.valueOf(lastUserData.getActivity()));

        _userDataManager.close();
*/

        if(_started) {
            if (_previousState != _currentState) {
                // Nouvelle ligne en bdd et mettre à jour la dernière
                _userDataManager.open();

                UserData lastUserData = _userDataManager.getLastUserData();

                lastUserData.setDuration( time - lastUserData.getDate());
                _userDataManager.updateUserData(lastUserData);

                _userDataManager.addUserData(new UserData( time, 0, _currentState) );

                _userDataManager.close();

            } else {
                // On check la date dans la dernière ligne de la bdd.
                // Si la current_date - date_bdd >= 2mn
                _userDataManager.open();

                UserData lastUserData = _userDataManager.getLastUserData();
               if( (time - lastUserData.getDate() ) >= (INTERVAL * 60 * 1000) ){
                   lastUserData.setDuration( time - lastUserData.getDate());
                   _userDataManager.updateUserData(lastUserData);
                   _userDataManager.close();
                }

            }
        }
        else{
            _userDataManager.open();
            _userDataManager.addUserData(new UserData( time, 0, _currentState) );
            _userDataManager.close();
            _started = true;
        }

        _previousState = _currentState;
    }
}
