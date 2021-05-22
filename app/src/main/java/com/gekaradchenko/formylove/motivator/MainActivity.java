package com.gekaradchenko.formylove.motivator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Document document;
    private Thread secThread;
    private Runnable runnable;

    private RecyclerView recyclerView;
    private Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Motivation> motivations;

    private SharedPreferences sharedPreferences;

    private Date date;
    private SimpleDateFormat format;

    private String dateFormat;

    private TextView textView;
    private Button buttonBefore, buttonNext;

    private Boolean boolActivity;

    private Elements all;

    private MenuItem menuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        threadStart();

    }

    private void init() {
        motivations = new ArrayList<>();

        dateFormat = "dd:MM:yyyy";

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new Adapter();
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        sharedPreferences = getSharedPreferences(getString(R.string.shared_name), MODE_PRIVATE);

        format = new SimpleDateFormat(dateFormat);

        textView = findViewById(R.id.textView);
        buttonBefore = findViewById(R.id.buttonBefore);
        buttonNext = findViewById(R.id.buttonNext);

        textView.setVisibility(View.INVISIBLE);
        buttonBefore.setVisibility(View.INVISIBLE);
        buttonNext.setVisibility(View.INVISIBLE);


        boolActivity = false;

        menuItem = findViewById(R.id.menuItem);


    }

    private void threadStart() {
        runnable = new Runnable() {
            @Override
            public void run() {
                getWeb();
            }
        };
        secThread = new Thread(runnable);
        secThread.start();
    }

    private void getWeb() {
        try {
            document = Jsoup.connect(getString(R.string.web_site)).get();

            Elements table = document.getElementsByTag("ol");
            Element ourTable = table.get(0);
            all = ourTable.children();

            for (int i = 0; i < all.size(); i++) {
                motivations.add(new Motivation(i, all.get(i).text()));
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.setMotivations(motivations);

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void getTodayMotivation(MenuItem item) {
        try {
            checkBool();
            checkSharedFirst();
            checkSharedDayToday();
            Log.d("SSS", "Shared Date: " + sharedPreferences.getString(getString(R.string.shared_day), getString(R.string.shared_first_day)));
            Log.d("SSS", "SharedID: " + sharedPreferences.getInt(getString(R.string.shared_id), -1));

            setTextView();
        } catch (Exception e) {
            Toast.makeText(this, "Подождите", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkBool() {
        if (boolActivity == true) {
            boolActivity = false;

            recyclerView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.INVISIBLE);
            buttonBefore.setVisibility(View.INVISIBLE);
            buttonNext.setVisibility(View.INVISIBLE);
        } else {
            boolActivity = true;

            recyclerView.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
            buttonBefore.setVisibility(View.VISIBLE);
            buttonNext.setVisibility(View.VISIBLE);
        }
    }


    private void checkSharedFirst() {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String date = sharedPreferences.getString(getString(R.string.shared_day), getString(R.string.shared_first_day));
        int id = sharedPreferences.getInt(getString(R.string.shared_id), -1);
        if (date.equals(getString(R.string.shared_first_day))) {

            editor.putString(getString(R.string.shared_day), format.format(new Date()));
            editor.putInt(getString(R.string.shared_id), 0);
        }
        if (id >= all.size()) {
            editor.putInt(getString(R.string.shared_id), 0);
        }
        editor.apply();
    }

    private void checkSharedDayToday() {
        String dateNow = format.format(new Date());
        String dataShared = sharedPreferences.getString(getString(R.string.shared_day), getString(R.string.shared_first_day));
        if (dateNow.equals(dataShared)) {
            Log.d("SSS", "checkSharedDayToday: equals");
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Log.d("SSS", "checkSharedDayToday: NO NO NO  equals");
            editor.putString(getString(R.string.shared_day), format.format(new Date()));

            int i = sharedPreferences.getInt(getString(R.string.shared_id), -1);
            ++i;
            editor.putInt(getString(R.string.shared_id), i);

            editor.apply();

        }
    }

    private void setTextView() {
        int i = sharedPreferences.getInt(getString(R.string.shared_id), -1);
        if (i == -1) {
            Toast.makeText(this, "Не получилось загрузить", Toast.LENGTH_SHORT).show();
            return;
        }
        textView.setText(i + 1 + ": " + all.get(i).text());
    }

    public void goBefore(View view) {


        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int id = sharedPreferences.getInt(getString(R.string.shared_id), -1);
            --id;
            if (id < 0) {
                editor.putInt(getString(R.string.shared_id), (all.size() - 1));
            } else {
                editor.putInt(getString(R.string.shared_id), id);
            }
            editor.apply();
            setTextView();
        } catch (Exception e) {
        }

    }

    public void goNext(View view) {

        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            int id = sharedPreferences.getInt(getString(R.string.shared_id), -1);
            ++id;
            if (id >= all.size()) {
                editor.putInt(getString(R.string.shared_id), 0);
            } else {
                editor.putInt(getString(R.string.shared_id), id);
            }
            editor.apply();
            setTextView();
        } catch (Exception e) {
        }

    }
}