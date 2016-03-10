package com.todoroo.astrid.gtasks.sync;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.PendingIntent;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.todoroo.andlib.utility.DateUtilities;
import com.todoroo.astrid.gcal.CalendarAlarmReceiver;


import org.tasks.calendars.AndroidCalendarEvent;
import org.tasks.calendars.CalendarEventProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by dev on 16/03/10.
 */
public class GtasksSyncCalenderProvider {

    private static final String TAG = "GtasksSyncCalenderProvider";

    public static final String[] INSTANCE_PROJECTION = new String[] {
            CalendarContract.Instances.EVENT_ID,      // 0
            CalendarContract.Instances.BEGIN,         // 1
            CalendarContract.Instances.TITLE          // 2
    };

    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_BEGIN_INDEX = 1;
    private static final int PROJECTION_TITLE_INDEX = 2;

    /*
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2011, 9, 23, 8, 0);
        long startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2011, 10, 24, 8, 0);
        long endMillis = endTime.getTimeInMillis();

     */

    public static List<TaskList> getTaskList(Context context, CalendarEventProvider calendarEventProvider) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;//TODO;
        }

        ArrayList result = new ArrayList<TaskList>();

        long now = DateUtilities.now();
        long end = now + TimeUnit.DAYS.toMillis(1);

        for (final AndroidCalendarEvent event : calendarEventProvider.getEventsBetween(now, end)) {
            //           Intent eventAlarm = new Intent(context, CalendarAlarmReceiver.class) {{
            //               setAction(CalendarAlarmReceiver.BROADCAST_CALENDAR_REMINDER);
            //               setData(Uri.parse(URI_PREFIX + "://" + event.getId()));
            //           }};

//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
//                    CalendarAlarmReceiver.REQUEST_CODE_CAL_REMINDER, eventAlarm, PendingIntent.FLAG_CANCEL_CURRENT);

//            long reminderTime = event.getStart() - FIFTEEN_MINUTES;
//            alarmManager.wakeup(reminderTime, pendingIntent);
//           Timber.d("Scheduled reminder for %s at %s", event, reminderTime);
        }
    }


    private static Task processTask() {

    }

    private static boolean isVisibleCalendar(String str) {
        if (str.equals("1")) {
            return true;
        }
        return false;
    }
}
