package com.dimkov.flinlauncher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SelectInstallActivity extends AppCompatActivity {


    ImageView imageViewFull;
    ImageView imageViewLite;
    Boolean ImgFull;
    Boolean ImgLite;

    int TypeCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_install);

        ImgFull = true;
        ImgLite = false;

        imageViewFull= (ImageView) findViewById(R.id.imageSelectFuel);
        imageViewLite = (ImageView) findViewById(R.id.imageSelectLite);


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
        ImageView imgQuest = (ImageView) findViewById(R.id.imageSelectQuestion);
        imgQuest.setClickable(true);
        imgQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builderSelectHelp;
                builderSelectHelp = new AlertDialog.Builder(SelectInstallActivity.this);
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

        Button buttonCClient;
        //Обновление клиента
        buttonCClient = (Button) findViewById(R.id.buttonCClient);
        buttonCClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ImgFull) {
                    //   Intent intent = new Intent(MainActivity.this, InstallLActivity.class);
                    Intent intent = new Intent(SelectInstallActivity.this, InstallFullActivity.class);
                    startActivity(intent);
                }
                if(ImgLite) {
                    Intent intent = new Intent(SelectInstallActivity.this, InstallLActivity.class);
                    startActivity(intent);
                }
            }
        });


        imageViewFull.setClickable(true);
        imageViewFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ImgFull){
                    imageViewFull.setImageResource(R.drawable.fullfalse);
                    ImgFull = false;

                    imageViewLite.setImageResource(R.drawable.litetrue);
                    ImgLite = true;
                }
                else {
                    imageViewFull.setImageResource(R.drawable.fulltrue);
                    ImgFull = true;

                    imageViewLite.setImageResource(R.drawable.litefalse);
                    ImgLite = false;
                }
            }
        });


        imageViewLite.setClickable(true);
        imageViewLite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ImgLite){

                    imageViewLite.setImageResource(R.drawable.litefalse);
                    ImgLite = false;

                    imageViewFull.setImageResource(R.drawable.fulltrue);
                    ImgFull = true;

                }
                else {
                    imageViewLite.setImageResource(R.drawable.litetrue);
                    ImgLite = true;

                    imageViewFull.setImageResource(R.drawable.fullfalse);
                    ImgFull = false;

                }
            }
        });

    }
}
