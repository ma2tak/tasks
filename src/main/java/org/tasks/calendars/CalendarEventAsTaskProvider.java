package org.tasks.calendars;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import com.todoroo.astrid.data.Task;

import org.tasks.injection.ForApplication;
import org.tasks.preferences.PermissionChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static android.provider.BaseColumns._ID;

/**
 * Created by dev on 16/03/10.
 */
public class CalendarEventAsTaskProvider extends CalendarEventProvider {
    private static final String[] COLUMNS = {
            _ID,                                        // 0
            CalendarContract.Events.DTSTART,            // 1
            CalendarContract.Events.DTEND,              // 2
            CalendarContract.Events.TITLE,              // 3
            CalendarContract.Events.DESCRIPTION,        // 4
            CalendarContract.Events.ALL_DAY,            // 5
            CalendarContract.Events.ORIGINAL_ID,        // 6
    };


    public CalendarEventAsTaskProvider(@ForApplication Context context, PermissionChecker permissionChecker) {
        super(context, permissionChecker, null);
    }


    public List<Task> getEventsAsTaskBetween(long start, long end) {
        return getCalendarEvents(
                CalendarContract.Events.CONTENT_URI,
                CalendarContract.Events.DTSTART + " > ? AND " + CalendarContract.Events.DTSTART + " < ?",
                new String[] { Long.toString(start), Long.toString(end) });
    }

    private String createExtraFields(long id,long original_id) {
        String fields = String.format("[CALENDAR:%l, %l]", id, original_id);
        return fields;
    }

    private List<Task> getCalendarEvents(Uri uri, String selection, String[] selectionArgs) {
        if (!permissionChecker.canAccessCalendars()) {
            return Collections.emptyList();
        }

        List<Task> events = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, COLUMNS, selection, selectionArgs, null);
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
                    if (cursor.getString(allDayIndex).equals("true")) {
                        Task task = new Task();
                        task.setNotes(cursor.getString(descriptionIndex) +
                                createExtraFields(cursor.getLong(idIndex), cursor.getLong(originalIdIndex)));
                        task.setDueDate(cursor.getLong(startIndex));
                        task.setTitle(cursor.getString(titleIndex));
                        events.add(task);
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
        return events;
    }
}
