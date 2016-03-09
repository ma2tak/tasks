package com.todoroo.astrid.gtasks.sync;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.services.tasks.model.TaskList;
import com.todoroo.andlib.utility.DateUtilities;
import com.todoroo.astrid.gcal.CalendarReminderActivity;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Calendar;

/**
 * Created by dev on 16/03/10.
 */
public class GtasksSyncCalenderProvider {

    private static final String TAG = "GtasksSyncCalenderProvider";

    private static String[] CALENDAR_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE,
            CalendarContract.Calendars.VISIBLE
    };


    public static List<TaskList> getTaskList(Context context) {
        ArrayList result = new ArrayList<TaskList>();

        ContentResolver cr = context.getContentResolver();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return TODO;
        }
        Cursor cursor = cr.query(CalendarContract.Calendars.CONTENT_URI, CALENDAR_PROJECTION, null, null, null);
        try {
            for (boolean hasNext = cursor.moveToFirst(); hasNext; hasNext = cursor.moveToNext()) {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String accountName = cursor.getString(2);
                String accountType = cursor.getString(3);
                String visible = cursor.getString(4);

                Log.i(TAG, id + ":" + name);
                Log.i(TAG, accountName);
                Log.i(TAG, accountType);
                Log.i(TAG, visible);
                Log.i(TAG, "----------------------------");

                if (isVisibleCalendar(visible)) {
                }
            }
        } finally {
            cursor.close();
        }


        return result;
    }

    private static boolean isVisibleCalendar(String str) {
        if (str.equals("1")) {
            return true;
        }
        return false;
    }

    public static final String[] INSTANCE_PROJECTION = new String[] {
            CalendarContract.Instances.EVENT_ID,      // 0
            CalendarContract.Instances.BEGIN,         // 1
            CalendarContract.Instances.TITLE          // 2
    };

    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_BEGIN_INDEX = 1;
    private static final int PROJECTION_TITLE_INDEX = 2;

    static private void processEvents(Context context) {

        ContentResolver cr = context.getContentResolver();
        Uri calendarContentUri = Calendars.getCalendarContentUri(CalendarContractCalendars.CALENDAR_CONTENT_CALENDARS);

        // Specify the date range you want to search for recurring
        // event instances
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2011, 9, 23, 8, 0);
        long startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2011, 10, 24, 8, 0);
        long endMillis = endTime.getTimeInMillis();

        Cursor cur = null;


        // The ID of the recurring event whose instances you are searching
        // for in the Instances table
        String selection = CalendarContract.Instances.EVENT_ID + " = ?";
        String[] selectionArgs = new String[] {"207"};

        // Construct the query with the desired date range.
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        CalendarContract.ContentUris.appendId(builder, startMillis);
        CalendarContract.ContentUris.appendId(builder, endMillis);

    // Submit the query
        cur =  cr.query(builder.build(),
        INSTANCE_PROJECTION,
        selection,
        selectionArgs,
                null);

        while (cur.moveToNext()) {
            String title = null;
            long eventID = 0;
            long beginVal = 0;

            // Get the field values
            eventID = cur.getLong(PROJECTION_ID_INDEX);
            beginVal = cur.getLong(PROJECTION_BEGIN_INDEX);
            title = cur.getString(PROJECTION_TITLE_INDEX);

            // Do something with the values.
            Log.i(DEBUG_TAG, "Event:  " + title);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(beginVal);
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            Log.i(DEBUG_TAG, "Date: " + formatter.format(calendar.getTime()));
        }
}




}
