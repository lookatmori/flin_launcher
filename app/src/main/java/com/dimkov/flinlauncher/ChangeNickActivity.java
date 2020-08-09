package com.dimkov.flinlauncher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

public class ChangeNickActivity extends AppCompatActivity {

    String nickName;
    EditText editTextName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nick);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        try {
            OpenNick();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Лого
        ImageView imgFavorite = (ImageView) findViewById(R.id.imageButtonLogoFull);
        imgFavorite.setClickable(true);
        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://flin-rp.su/"));
                startActivity(browserIntent);

            }
        });


        //Помощь
        ImageView imgQuest = (ImageView) findViewById(R.id.imageSelectViewFull);
        imgQuest.setClickable(true);
        imgQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builderSelectHelp;
                builderSelectHelp = new AlertDialog.Builder(ChangeNickActivity.this);
                builderSelectHelp.setTitle("Помощь!");
                builderSelectHelp.setMessage("Возникли проблемы с установкой? Попробуйте в ручном режиме");
                builderSelectHelp.setPositiveButton("Перейти на сайт", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/@flin_rp-kak-nachat-igrat-otvet-est"));
                        startActivity(browserIntent);
                    }
                });
                ;
                builderSelectHelp.setNegativeButton("Закрыть", null);
                AlertDialog alert = builderSelectHelp.create();
                alert.show();

            }
        });


        //Сохранение ника
        editTextName = (EditText) findViewById(R.id.editTextChangeName);
        Button buttonSaveNick = (Button) findViewById(R.id.buttonSaveNick);
        buttonSaveNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    SaveNick();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Вам необходимо сначала установить клиент",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Intent intent = new Intent(ChangeNickActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        });;

    }

    public void OpenNick() throws IOException {
        Wini ini = new Wini(new File(Environment.getExternalStorageDirectory() + "/Android/data/com.rockstargames.gtasa/files/SAMP/settings.ini"));
        nickName = ini.get("client", "name");
        //Log.d(TAG, "Ваш ник: "+nickName);
    }

    public void SaveNick() throws IOException {
        Wini ini = new Wini(new File(Environment.getExternalStorageDirectory() + "/Android/data/com.rockstargames.gtasa/files/SAMP/settings.ini"));
        nickName = editTextName.getText().toString();
        ini.put("client", "name", nickName);
        ini.store();

        Toast toast = Toast.makeText(getApplicationContext(),
                "Ваш ник: "+nickName+"   Успешно сохранен!",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        Intent intent = new Intent(ChangeNickActivity.this, MainActivity.class);
        startActivity(intent);

    }
}
