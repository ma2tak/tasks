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
import org.tasks.preferences.PermissionChecker;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.todoroo.andlib.utility.DateUtilities;
import com.todoroo.astrid.gcal.CalendarAlarmReceiver;


import org.tasks.calendars.AndroidCalendarEvent;
import org.tasks.calendars.CalendarEventAsTaskProvider;
import org.tasks.calendars.CalendarEventProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by dev on 16/03/10.
 */
public class GtasksSyncCalenderProvider {

    private static final String TAG = "GtasksSyncCalenderProvider";
    private CalendarEventAsTaskProvider provider;
    private final Context contex;
    private final PermissionChecker permissionChecker;

    public static final String[] INSTANCE_PROJECTION = new String[]{
            CalendarContract.Instances.EVENT_ID,      // 0
            CalendarContract.Instances.BEGIN,         // 1
            CalendarContract.Instances.TITLE          // 2
    };

    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_BEGIN_INDEX = 1;
    private static final int PROJECTION_TITLE_INDEX = 2;

    public GtasksSyncCalenderProvider(Context context){
        this.contex = context;
        permissionChecker = new PermissionChecker(context);
        provider = new CalendarEventAsTaskProvider(context, permissionChecker);
    }

    private long getTodayMills() {
        long currentTimeMillis = System.currentTimeMillis();
        Log.v("Test", String.valueOf(currentTimeMillis));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Log.v("Test",
                calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                        calendar.get(Calendar.MINUTE) + ":" +
                        calendar.get(Calendar.SECOND) + ":" +
                        calendar.get(Calendar.MILLISECOND));

        return calendar.getTimeInMillis();
    }



    public List<TaskList> getTaskList() {
        provider.getEventsAsTaskBetween(getTodayMills(), getTodayMills() + TimeUnit.DAYS.toMillis(1));

        ArrayList result = new ArrayList<TaskList>();

        return result;
    }


    private static Task processTask() {
        return null;
    }

    private static boolean isVisibleCalendar(String str) {
        if (str.equals("1")) {
            return true;
        }
        return false;
    }
}
