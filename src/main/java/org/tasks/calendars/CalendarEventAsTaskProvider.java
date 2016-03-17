package org.tasks.calendars;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import com.google.api.services.tasks.model.TaskList;
import com.todoroo.astrid.data.Task;

import org.tasks.injection.ForApplication;
import org.tasks.preferences.PermissionChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static android.provider.BaseColumns._ID;

/**
 * Created by dev on 16/03/10.
 */
public class CalendarEventAsTaskProvider extends CalendarEventProvider {

    private static final String[] CALENDAR_COLUMNS = {
            _ID,                                                // 0
            CalendarContract.Calendars.NAME,                    // 1
            CalendarContract.Calendars.CALENDAR_COLOR,          // 2
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,   // 3
            CalendarContract.Calendars.VISIBLE,                 // 4
            CalendarContract.Calendars.CALENDAR_TIME_ZONE,      // 5
    };

    private static final String[] EVENT_COLUMNS = {
            _ID,                                        // 0
            CalendarContract.Events.DTSTART,            // 1
            CalendarContract.Events.DTEND,              // 2
            CalendarContract.Events.TITLE,              // 3
            CalendarContract.Events.DESCRIPTION,        // 4
            CalendarContract.Events.ALL_DAY,            // 5
            CalendarContract.Events.ORIGINAL_ID,        // 6
    };

    @Inject
    public CalendarEventAsTaskProvider(@ForApplication Context context, PermissionChecker permissionChecker) {
        super(context, permissionChecker, null);
    }

    public List<Task> getEventsAsTaskBetween(long calendar, long start, long end) {
        return getCalendarEvents(
                CalendarContract.Events.CONTENT_URI,
                CalendarContract.Events.CALENDAR_ID + " = ? AND " +
                CalendarContract.Events.DTSTART + " > ? AND " +
                        CalendarContract.Events.DTSTART + " < ?"
                ,
                new String[] { Long.toString(calendar), Long.toString(start), Long.toString(end) });
    }

    private String createExtraFields(long id,long original_id) {
        String fields = String.format("[CALENDAR:%d, %d]", id, original_id);
        return fields;
    }


    public List<TaskList> getCalendarTaskList(long start, long end) {
        if (!permissionChecker.canAccessCalendars()) {
            return Collections.emptyList();
        }
        Cursor cursor = null;
        List<TaskList> lists = new ArrayList<TaskList>();
        try {
            cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, CALENDAR_COLUMNS, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                int idIndex = cursor.getColumnIndex(_ID);
                int nameIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.NAME);
                int colorIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_COLOR);
                int displayNameIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME);
                int visibleIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.VISIBLE);
                int timeZoneIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_TIME_ZONE);
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idIndex);
                    if (cursor.getInt(visibleIndex) == 1) {
                        TaskList list = new TaskList();
                        list.setTitle(cursor.getString(nameIndex));
                        List<Task> taskList = getEventsAsTaskBetween(id, start, end);
                        // todo list.
                        lists.add(list);
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return lists;
    }


    private List<Task> getCalendarEvents(Uri uri, String selection, String[] selectionArgs) {
        if (!permissionChecker.canAccessCalendars()) {
            return Collections.emptyList();
        }

        TaskList list = new TaskList();
        list.setId("list");
        list.setTitle("Test Tasks");

        List<Task> events = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, EVENT_COLUMNS, selection, selectionArgs, null);
            if (cursor != null && cursor.getCount() > 0) {
                int idIndex = cursor.getColumnIndex(_ID);
                int startIndex = cursor.getColumnIndexOrThrow(CalendarContract.Events.DTSTART);
                int endIndex = cursor.getColumnIndexOrThrow(CalendarContract.Events.DTEND);
                int titleIndex = cursor.getColumnIndexOrThrow(CalendarContract.Events.TITLE);
                int descriptionIndex = cursor.getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION);
                int allDayIndex = cursor.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY);
                int originalIdIndex = cursor.getColumnIndexOrThrow(CalendarContract.Events.ORIGINAL_ID);
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idIndex);
//                    if (cursor.getString(allDayIndex).equals("true")) {
                        Task task = new Task();
                        task.setTitle(cursor.getString(titleIndex));
                        task.setDueDate(cursor.getLong(startIndex));
                        task.setNotes(cursor.getString(descriptionIndex) +
                                createExtraFields(cursor.getLong(idIndex), cursor.getLong(originalIdIndex)));
                        events.add(task);
//                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return events;
    }
}
