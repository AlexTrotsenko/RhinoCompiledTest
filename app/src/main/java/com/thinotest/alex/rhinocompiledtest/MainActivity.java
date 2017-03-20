package com.thinotest.alex.rhinocompiledtest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.faendir.rhino_android.RhinoAndroidHelper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Context context;
    private Scriptable scope;

    private Function function;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String iterations = ((EditText) findViewById(R.id.editText)).getText().toString();
                final String startingMessage = "Starting Pi computation with " + iterations + " iterations";
                Snackbar.make(view, startingMessage, Snackbar.LENGTH_LONG).show();

                try {
                    final long averageExecutionTime = computePiByJs(iterations);

                    Snackbar.make(view, "Average execution time: " + averageExecutionTime, Snackbar.LENGTH_LONG).show();
                } catch (Throwable t) {
                    final String errorMsg = "JS failed ";

                    Log.e(TAG, errorMsg, t);
                    Snackbar.make(view, errorMsg + t.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });


        context = new RhinoAndroidHelper(this).enterContext();
        context.setOptimizationLevel(-1);
        scope = context.initStandardObjects();
    }

    /**
     * @return average execution time
     */
    private long computePiByJs(String iterations) throws IOException {
        final InputStream open = this.getAssets().open("calculatePi.js");
        final String jsSourceCode = CharStreams.toString(new InputStreamReader(open, Charsets.UTF_8));

        final Object[] args = {iterations};
        if (function == null) {
            Date firstStarted = new Date();

            function = context.compileFunction(scope, jsSourceCode, "<hello_world>", 1, null);
            final Object result = function.call(context, scope, scope, args);

            Date firstFinished = new Date();

            Log.e(TAG, "1st invocation & compilation took: " + (firstFinished.getTime() - firstStarted.getTime()
                    + ", JS did " + iterations + " iterations"
                    + " optimization level: " + context.getOptimizationLevel()
                    + ", result: " + result));
        }

        Date compiledExecutionStarted = new Date();

        //repeat execution of the compiled function several times in order to get average execution time
        final int repeatTimes = 100;
        for (int i = 0; i < repeatTimes; i++) {
            final Object result = function.call(context, scope, scope, args);
            Log.w(TAG, "Finished " + i + ", result: " + result);
        }

        Date compiledExecutionFinished = new Date();

        final long averageExecutionTime = (compiledExecutionFinished.getTime() - compiledExecutionStarted.getTime()) / repeatTimes;

        Log.e(TAG, "Average execution of 'compiled' JS took: "
                    + averageExecutionTime
                    + ", JS did " + iterations + " iterations"
                    + ", Java repeatTimes: " + repeatTimes);

        return averageExecutionTime;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
