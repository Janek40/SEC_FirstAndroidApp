package production.janek.androidproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Stopwatch extends AppCompatActivity {

    private long startTime;
    private long endTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
    }

    private void myMain()
    {
        startTime = System.currentTimeMillis();
    }
}
