package com.example.gioele.stufina;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.Process;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.concurrent.ExecutionException;


import static java.lang.Thread.sleep;


public class Stufina extends AppCompatActivity {


    private Button button;
    //private ArrayList<Integer> a;
    private double n;
    protected Handler handler;
    protected int value = 0;
    protected String file;
    protected VideoView video;
    protected TextView txt;
    protected boolean accesso = false;
    protected EditText pass;
    protected int visti[];
    protected int nVideo = -1;
    protected TextView mancanti;
    protected TextView temperatura;
    protected int contT = 0;
    protected String link = "https://andreaferrari454545.000webhostapp.com/vid";
    protected String linkBackup = "https://ohggio77.000webhostapp.com/vid";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stufina);




        file = "android.resource://" + getPackageName() + "/raw/vid1";



        button = (Button) findViewById(R.id.button);
        video = (VideoView) findViewById(R.id.videoView);
        pass = (EditText) findViewById(R.id.editText);
        txt = (TextView) findViewById(R.id.textView2);
        mancanti = (TextView) findViewById(R.id.textView);
        temperatura = (TextView) findViewById(R.id.textView3);

        mancanti.setText("");
        video.setVisibility(View.INVISIBLE);

        try {
            nVideo = serverVideo();
        }catch(Exception e){
            link = linkBackup;
            nVideo = serverVideo();
        }
        visti = new int[nVideo];
        azzera();

        MediaController vidControl = new MediaController(this);

        vidControl.setAnchorView(video);
        video.setMediaController(vidControl);
        GregorianCalendar gc = new GregorianCalendar();
        //txt.setText(""+gc.get(Calendar.MINUTE)+""+gc.get(Calendar.HOUR_OF_DAY)+""+gc.get(Calendar.DAY_OF_MONTH)+""+(gc.get(Calendar.MONTH)+1)+""+gc.get(Calendar.YEAR)+"");

        //File dir = new File("https://andreaferrari454545.000webhostapp.com/prova/");
        //txt.setText(dir.listFiles().length);

        //txt.setText(link+".txt");
        txt.setText("0");

/*
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        Sensor TempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mSensorManager.registerListener(temperatureSensor, TempSensor, SensorManager.SENSOR_DELAY_FASTEST);
*/
        temperatura.setText(getCpuTemp()+"");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GregorianCalendar gc = new GregorianCalendar();
                String a = gc.get(Calendar.MINUTE) + "" + gc.get(Calendar.HOUR_OF_DAY) + "" + gc.get(Calendar.DAY_OF_MONTH) + "" + (gc.get(Calendar.MONTH) + 1) + "" + gc.get(Calendar.YEAR) + "";
                int b = sommatoria(a);

                if (!accesso)
                    if (pass.getText().toString().length() > b && controllo(b, pass.getText().toString())) {
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        accesso = true;

                    }
                //txt.setText(a);
                //txt.setText(""+pass.getText());
                if (accesso) {

                    Random random = new Random();

                    //video.setVideoURI(Uri.parse(link + ((random.nextInt(3) + 1)) + ".mp4"));
                    int daVedere = genera();
                    try {
                        video.setVideoURI(Uri.parse(link + (daVedere) + ".mp4"));
                    }catch(Exception e){
                        link = linkBackup;
                        video.setVideoURI(Uri.parse(link + (daVedere) + ".mp4"));
                    }
                    video.setVisibility(View.VISIBLE);

                    video.start();
                    //Log.i("prova","prova");
                    new Surriscalda().execute();

                }
                pass.setText("");

            }

        });

    }
    public class Temp extends AsyncTask<Void,String,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            while(true) {
                this.publishProgress();
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


        }
        @Override
        protected void onProgressUpdate(String... values){
            super.onProgressUpdate(values);
            temperatura.setText(getCpuTemp()+"");
        }
    }

    private SensorEventListener temperatureSensor = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            float temp = event.values[0];
            Log.i("temperatura:",temp+"");
            temperatura.setText(temp+"");
        }
    };

    public float getCpuTemp() {
        Process p;
        try {
            p = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = reader.readLine();
            float temp = Float.parseFloat(line) / 1000.0f;

            return temp;

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }
    public int serverVideo() {

        int a=0;
        try {
            a = new prova().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return a;

    }

    class Surriscalda extends AsyncTask<Void,String,Void>{

        @Override
        protected Void doInBackground(Void... voids) {


            Random random = new Random();
            //Log.i("prova","prova");

            for (int i = 0; i < 1000000000; i++) {
                Math.sqrt(Math.sqrt(Math.sqrt(Math.sqrt(Math.sqrt(Math.sqrt(Math.sqrt(Math.sqrt(Math.sqrt(Math.sqrt(random.nextInt(10000000))))))))))) ;
                if((i%10000000)==0)
                    this.publishProgress();

                }


            new Surriscalda().execute();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values){
            contT++;
            super.onProgressUpdate(values);

            Double n = Double.parseDouble(txt.getText().toString());
            n= n+0.01;
            n = arrotonda(n,2);
            txt.setText(n+"");
            //Log.i("numero", n+"");
            temperatura.setText(getCpuTemp()+"");
        }
        double arrotonda(double d, int p)
        {
            return Math.rint(d*Math.pow(10,p))/Math.pow(10,p);
        }

    }

    class prova extends AsyncTask<Void,Void,Integer>{

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                URL url = new URL(link.substring(0,link.length()-3)+"php1.php");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {

                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setChunkedStreamingMode(0);

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader read = new BufferedReader(new InputStreamReader(in));
                    String a = read.readLine();
                    txt.setText(a);
                    return Integer.parseInt(a);
                }finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.i("eccezione", e.toString());


            }
            return null;
        }
    }

    public void azzera(){
        for (int i = 0; i < nVideo; i++)
            visti[i] = 0;
    }

    public int genera() {
        Random random = new Random();
        int numero;
        boolean trovato;
        int cont = 0;
        trovato = true;
        for(int i=0; i<nVideo; i++){
            if(visti[i] == 0)
                trovato = false;
        }
        if (trovato)
            azzera();
        do {
            cont=0;
            trovato = true;
            numero = (random.nextInt(nVideo) + 1);
            for (int i = 0; i < nVideo; i++) {
                if (numero == visti[i])
                    trovato = false;
                if (visti[i] == 0)
                    cont++;
            }

        } while (!trovato);
        for (int i = 0; i < nVideo; i++) { // in prima posizione libera aggiungo il video che sto stremmando
            if (visti[i] == 0) {
                visti[i] = numero;
                i = nVideo + 1;
            }
        }
        mancanti.setText("mancanti:" + cont);
        return numero;
    }

    public int sommatoria(String a) {
        int numero = 0;
        for (int i = 0; i < a.length(); i++)
            numero += Integer.parseInt(a.charAt(i) + "");
        String prova = numero + "";
        if (prova.length() >= 2)
            return sommatoria(prova);
        return numero;
    }

    public boolean controllo(int n, String pa) {
        GregorianCalendar gc = new GregorianCalendar();
        String somma2 = gc.get(Calendar.MINUTE)+"";
        int somma = sommatoria(somma2);
        int nConsentito = 64+somma;
        char charConsentito = (char)nConsentito;
        Log.i("carattere consentito",charConsentito+"");
        if(pa.charAt(n) != charConsentito)
            return false;

        Log.i("errore"," controllo posizione");
        for (int i = 0; i < pa.length(); i++)
            if(i!=n)
            if (pa.charAt(i) == charConsentito || pa.charAt(i) == Character.toLowerCase(charConsentito))
                return false;

        Log.i("return","true");
        return true;
    }


}