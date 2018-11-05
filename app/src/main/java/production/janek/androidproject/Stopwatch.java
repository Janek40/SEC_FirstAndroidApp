package production.janek.androidproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import android.widget.ListView;
import java.util.List;
import java.util.LinkedList;

public class Stopwatch extends AppCompatActivity {

    private volatile boolean startedTimer = false;
    private volatile boolean pauseTimer = false;
    private ExecutorService timerService;
    private Runnable timerCode;
    private volatile Object key = new Object();
    ArrayAdapter<String> adapter;
    private int numToggles = 0;

    private String lastTime="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        myMain();
    }

    private void myMain()
    {
        List<String> timerItems = new LinkedList<String>();
        ListView myList = findViewById(R.id.TimerList);
        adapter = new ArrayAdapter<String>(Stopwatch.this, R.layout.timer_list_item, timerItems);
        myList.setAdapter(adapter);

        timerService = new ThreadPoolExecutor(2, 2, 5000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(2));

        timerCode = new Runnable() {
            @Override
            public void run() {
                boolean endIt = false;
                synchronized(key)
                {
                    if(startedTimer)
                    {
                        endIt = true;
                    }
                }
                if(endIt)
                {
                    stopTimer();
                    return;
                }
                startTimer();
            }
        };

    }

    protected void pauseTimer(View myView)
    {
        synchronized (key)
        {
            if(pauseTimer)
            {
                key.notify();
                pauseTimer = false;
            }
            else {
                pauseTimer = true;
            }
        }
    }


   protected void toggleTimer(View myView)
   {
       if(numToggles<=1) {
           timerService.execute(timerCode);
       }
   }

    protected void stopTimer()
    {
        synchronized (key)
        {
            startedTimer = false;
        }
        numToggles = 0;
    }

    protected void startTimer()
    {
        long startTime = System.nanoTime()/1000000;
        startedTimer = true;
        long diff = 0;
        try {
            while (true) {
                synchronized (key)
                {
                    if(!startedTimer)
                    {
                        findViewById(android.R.id.content).post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.add(lastTime);
                            }
                        });
                        return;
                    }
                    if(pauseTimer)
                    {
                        key.wait();
                        startTime = System.nanoTime()/1000000-diff;
                    }
                }
                diff = (System.nanoTime()/1000000 - startTime);
                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SS");
                Date resultDate = new Date(diff);
                editTimerText(sdf.format(resultDate));
            }
        }
        catch(InterruptedException e)
        {

        }
    }


    private void editTimerText(final String value)
    {
        findViewById(android.R.id.content).post(new Runnable() {
            @Override
            public void run() {
                //update the text
                TextView textStuff = findViewById(R.id.TimeText);
                textStuff.setText("Time: " + value);

                //update the list
                lastTime = value;
            }
        });
    }
}
