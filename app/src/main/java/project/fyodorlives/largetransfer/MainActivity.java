package project.fyodorlives.largetransfer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


public class MainActivity extends ActionBarActivity {
    private final String ip = "192.168.42.1";
    private final int port = 3034;
    Long startTime;
    commandDevice response = new commandDevice();
    Long endTime;
    Button bt;

    public static boolean copyFile(FileInputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
                Log.i("sending", "sne");
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d("error", e.toString());
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //response.task = this;
        setContentView(R.layout.activity_main);
        bt = (Button) findViewById(R.id.tb);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cmd("send");
            }
        });
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

    public void cmd(String command) {
        new commandDevice().execute(command);
    }

    public class commandDevice extends AsyncTask<String, String, Double> {
        //public AsyncResponse task = null;
        Double result;
        Long timeTak;
        private TextView tv;

        protected void onPostExecute(Double result) {
            tv = (TextView) findViewById(R.id.textView);
            tv.setText(result.toString());
        }

        @Override
        protected Double doInBackground(String... commands) {

            String command = commands[0];
            //File myile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"file.pdf");
            File myile = new File("/mnt/extSdCard/" + "file.pdf");
            if (myile.exists()) {
                Log.i("size", "exists");
            }

            Log.d("send", "Command sending:" + command);
            if (command == "send") {
                try {
                    Socket socket = new Socket(ip, port);
                    startTime = System.nanoTime();
                    byte[] mybytearray = new byte[(int) myile.length()];
                    FileInputStream fis = new FileInputStream(myile);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(mybytearray, 0, mybytearray.length);
                    OutputStream os = socket.getOutputStream();
                    System.out.println("Sending " + "(" + mybytearray.length + " bytes)");
                    os.write(mybytearray, 0, mybytearray.length);
                    os.flush();
                    System.out.println("Done.");
                    socket.close();
                    endTime = System.nanoTime();
                    timeTak = endTime - startTime;
                    double last = (double) timeTak / 1000000000.0;
                    result = last;
                    Log.i("Sending", result.toString());
                } catch (IOException ioe) {
                    Log.d("Error", ioe.toString());
                }
            }
            return result;
        }


    }
}
