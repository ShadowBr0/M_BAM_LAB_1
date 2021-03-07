package com.example.firstapp;

import android.database.Cursor;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class CounterDao_Impl implements CounterDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Counter> __insertionAdapterOfCounter;

  public CounterDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCounter = new EntityInsertionAdapter<Counter>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `Counter` (`uid`,`name`,`count`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Counter value) {
        stmt.bindLong(1, value.uid);
        if (value.name == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.name);
        }
        if (value.ticks == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindLong(3, value.ticks);
        }
      }
    };
  }

  @Override
  public void insertUsers(final Counter... counters) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfCounter.insert(counters);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<Counter> getAll() {
    final String _sql = "Select * From counter";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfTicks = CursorUtil.getColumnIndexOrThrow(_cursor, "count");
      final List<Counter> _result = new ArrayList<Counter>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Counter _item;
        _item = new Counter();
        _item.uid = _cursor.getInt(_cursorIndexOfUid);
        _item.name = _cursor.getString(_cursorIndexOfName);
        if (_cursor.isNull(_cursorIndexOfTicks)) {
          _item.ticks = null;
        } else {
          _item.ticks = _cursor.getInt(_cursorIndexOfTicks);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
