package com.dimkov.flinlauncher;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button installButton;
    Button buttonStartSamp;
    Button buttonChangeName;
    Button buttonUpdateClient;
    String nickName = null;

    boolean typeID = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        //Лого
        ImageView imgFavorite = (ImageView) findViewById(R.id.imageButtonLogoFull);
        imgFavorite.setClickable(true);
        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( !isOnline(MainActivity.this) ){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Вы не подключены к интернету!",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://flin-rp.su/"));
                    startActivity(browserIntent);
                }
            }
        });


        //Обновление клиента
        buttonUpdateClient = (Button) findViewById(R.id.buttonUpdateClient);

        buttonUpdateClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /* if ( !isOnline(MainActivity.this) ){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Вы не подключены к интернету!",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Обновлений не найдено!",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
*/
                installButton = (Button) findViewById(R.id.buttonInstallClient);
                if(typeID) {
                    installButton.setVisibility(View.GONE);
                    typeID = false;
                }
                else {
                    installButton.setVisibility(View.VISIBLE);
                    typeID = true;
                }

            }
        });




        //Помощь
        ImageView imgQuest = (ImageView) findViewById(R.id.imageSelectViewFull);
        imgQuest.setClickable(true);
        imgQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builderSelectHelp;
                builderSelectHelp = new AlertDialog.Builder(MainActivity.this);
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



        //Установка
        installButton = (Button) findViewById(R.id.buttonInstallClient);
        installButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Intent intent = new Intent(MainActivity.this, InstallLActivity.class);
                if ( !isOnline(MainActivity.this) ){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Вы не подключены к интернету!",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, SelectInstallActivity.class);
                    startActivity(intent);
                }


            }
        });

        //Смена ника
        buttonChangeName = (Button) findViewById(R.id.buttonChangeName);
        buttonChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChangeNickActivity.class);
                startActivity(intent);
            }
        });



        buttonStartSamp = (Button) findViewById(R.id.buttonStartSampFull);
        buttonStartSamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OpenNick();
                } catch (IOException e) {
                    e.printStackTrace();

                }
                if(nickName == null || nickName == "YourNickName"){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Вы не установили свой ник!",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else {
                    android.content.Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.rockstargames.gtasa");
                    if (launchIntent != null) {
                        startActivity(launchIntent);//null pointer check in case package name was not found
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Приложение не установлено!",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    }
                }

            }
        });
    }


    public void OpenNick() throws IOException {
        Wini ini = new Wini(new File(Environment.getExternalStorageDirectory() + "/Android/data/com.rockstargames.gtasa/files/SAMP/settings.ini"));
        nickName = ini.get("client", "name");
        Log.d("DIMKOV", "Ваш ник: "+nickName);
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }
}