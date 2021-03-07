package com.example.firstapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.List;


@Dao
interface CounterDao {
    @Query("Select * From counter")
    List<Counter> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(Counter... counters);
}


@Database(entities = {Counter.class}, version = 1)
abstract class AppDatabase extends RoomDatabase {
    public abstract CounterDao cD();
}


@Entity
class Counter{
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "count")
    public Integer ticks;
}


class NumberReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context arg0, Intent arg1) {
        Integer counterValue = arg1.getIntExtra("Ticks", 0);
        String name = arg1.getStringExtra("Name");
        Log.d("", name + " -> counter stopped at -> " + counterValue.toString());
        Thread thread = new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(arg0, AppDatabase.class, "counterDB.sql").build();
            try {
                CounterDao cD = db.cD();
                Counter counter = new Counter();
                counter.ticks = counterValue;
                counter.name = name;
                cD.insertUsers(counter);
            } catch (Exception e){}
            db.close();
        });
        thread.start();
    }
}


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.firstapp.MESSAGE";
    private AppDatabase db;
    private CounterDao userDao;
    private IntentFilter filter = new IntentFilter("notification.counter");
    private NumberReceiver numberReceiver = new NumberReceiver();

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(numberReceiver, filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(numberReceiver, filter);
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "counterDB.sql").build();
        userDao = db.cD();
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = findViewById(R.id.personName);
        textView.setText(message);
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, MainActivity2.class);
        EditText editText = (EditText) findViewById(R.id.personName);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void startCounter(View view) {
        Intent intent = new Intent(this, firstService.class);
        EditText editText = (EditText) findViewById(R.id.personName);
        String message = editText.getText().toString();
        intent.putExtra("userName", message);
        this.startService(intent);
    }

    public void stopCounters(View view){
        Intent intent = new Intent(this, firstService.class);
        this.stopService(intent);
    }

    public void readDatabase(View view){
        Thread thread = new Thread(() -> {
            try {
                List<Counter> users = userDao.getAll();
                for (Counter user : users){
                    Log.d("",  user.ticks.toString());
                }
            }catch(Exception e){
            }
        });
        thread.start();
    }

    protected void onDestroy(){
        db.close();
        unregisterReceiver(numberReceiver);
        super.onDestroy();
    }

}