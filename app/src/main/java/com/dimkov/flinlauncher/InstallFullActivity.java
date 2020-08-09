package com.dimkov.flinlauncher;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ir.mahdi.mzip.zip.ZipArchive;

import static android.support.v4.content.FileProvider.getUriForFile;

public class InstallFullActivity extends AppCompatActivity {


    DownloadManager downloadManager;
    TextView tvMessage;
    TextView textViewProgressType;
    TextView textViewGPU;
    TextView textViewInfoInstall;
    int dl_progress;
    ProgressBar mProgressBar;
    Long did;
    String TAG = "Dimkov";
    Integer TypeDownload;
    int TypeCache;
    String url = "http://d1.flin-rp.su/lite_power.zip";
    Button btn;


    AlertDialog.Builder builderSelectChooseAPKinstall;
    boolean typeID = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_full);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);


        builderSelectChooseAPKinstall = new AlertDialog.Builder(InstallFullActivity.this);
        builderSelectChooseAPKinstall.setTitle("Выбор");
        builderSelectChooseAPKinstall.setMessage("Установить APK вручную или автоматическом режиме?");
        builderSelectChooseAPKinstall.setPositiveButton("Авто", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btn.setVisibility(View.GONE);
                InstallApk();
            }
        });
        ;
        builderSelectChooseAPKinstall.setNegativeButton("Вручную", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InstallFullActivity.this);
                builder.setTitle("Ручная установка")
                        .setMessage("Откройте проводник, затем найдите папку Download, откройте ее и запустите файл flinrp_full.apk")
                        .setIcon(R.mipmap.ic_launcher)
                        .setCancelable(false)
                        .setNegativeButton("Понял",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        Uri selectedUri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/");
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(selectedUri, "resource/folder");

                                        if (intent.resolveActivityInfo(getPackageManager(), 0) != null)
                                        {
                                            startActivity(intent);
                                        }

                                        btn.setVisibility(View.GONE);
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

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
                builderSelectHelp = new AlertDialog.Builder(InstallFullActivity.this);
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


        btn = (Button) findViewById(R.id.buttonStartSampFull);
        textViewInfoInstall = (TextView) findViewById(R.id.textViewInfoInstallFull);
        // textViewInfoInstall.setVisibility(View.GONE);

        tvMessage = (TextView) findViewById(R.id.textViewProgressFull);
        // tvMessage.setVisibility(View.GONE);
        textViewProgressType = (TextView) findViewById(R.id.textViewProgressTypeFull);
        // textViewProgressType.setVisibility(View.GONE);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBarFullInstal);
        mProgressBar.getIndeterminateDrawable().setColorFilter(0xFFFF0000,android.graphics.PorterDuff.Mode.MULTIPLY);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeID == true) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            textViewInfoInstall.setText(R.string.text_loading_warning);
                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "gta_full.zip");
                            if (file.exists()) {
                                file.length();
                                if (file.length() == 1892861287) {
                                  /*  Toast toast = Toast.makeText(getApplicationContext(),
                                            "MD5 совпал!",
                                            Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();*/
                                    DownloadFiles("http://d1.flin-rp.su/flinrp_full.apk", "flinrp_full.apk");
                                    TypeDownload = 2;
                                    btn.setVisibility(View.GONE);
                                    typeID = false;
                                    textViewProgressType.setText("[2/6] Загрузка Full APK");
                                } else {
                                    file.delete();
                                    Log.d(TAG, "Permission is granted");
                                    TypeCache = 0;
                                    TypeDownload = 1;
                                    textViewProgressType.setText("[1/6] Загрузка кэша для игры");
                                    textViewInfoInstall.setText(R.string.text_loading_warning);
                                    btn.setVisibility(View.GONE);
                                    typeID = false;
                                    DownloadFiles("http://d1.flin-rp.su/gta_full.zip", "gta_full.zip");
                                }
                            } else {
                                Log.d(TAG, "Permission is granted");
                                TypeCache = 0;
                                TypeDownload = 1;
                                textViewProgressType.setText("[1/6] Загрузка кэша для игры");
                                textViewInfoInstall.setText(R.string.text_loading_warning);
                                btn.setVisibility(View.GONE);
                                typeID = false;
                                DownloadFiles("http://d1.flin-rp.su/gta_full.zip", "gta_full.zip");
                            }

                        } else {
                            Log.d(TAG, "Permission is revoked");
                            ActivityCompat.requestPermissions(InstallFullActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    } else {
                    }
                } else {
                    AlertDialog alert = builderSelectChooseAPKinstall.create();
                    alert.show();
                }
            }});
    }


    public void DownloadFiles(String URL, String FileName) {
        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + FileName);//
        sdPath.delete();

        String urlDownload = URL;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlDownload));

        request.setDescription("Flin");
        request.setTitle("Download");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationUri(Uri.parse("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + FileName));
        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        final long downloadId = manager.enqueue(request);



        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean downloading = true;
                while (downloading) {
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadId);
                    Cursor cursor = manager.query(q);
                    cursor.moveToFirst();
                    int bytes_downloaded = cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                    }
                    dl_progress = (int) ((double)bytes_downloaded / (double)bytes_total * 100f);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress((int) dl_progress);
                            tvMessage.setText(dl_progress + "%");
                            if (dl_progress == 100) {
                                dl_progress = 0;
                                switch (TypeDownload) {
                                    case 1: {
                                        DownloadFiles("http://d1.flin-rp.su/flinrp_full.apk", "flinrp_full.apk");
                                        TypeDownload = 2;
                                        textViewProgressType.setText("[2/6] Загрузка Full APK");
                                        break;
                                    }
                                    case 2: {
                                        DownloadFiles("http://d1.flin-rp.su/files_full.zip", "files_full.zip");
                                        TypeDownload = 3;
                                        textViewProgressType.setText("[2/6] Загрузка Файлов для APK");
                                        break;
                                    }
                                    case 3: {
                                        UnZipFiles unZipFiles = new UnZipFiles();
                                        unZipFiles.execute();
                                        TypeDownload = 4;
                                        textViewProgressType.setText("[3/6] Распаковка Файлов для APK");
                                        break;
                                    }
                                }
                            }
                        }
                    });
                    cursor.close();
                }
            }
        }).start();


    }


    private class UnZipFiles extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ZipArchive zipArchive = new ZipArchive();
            zipArchive.unzip(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/files_full.zip", Environment.getExternalStorageDirectory() + "/Android/data/com.rockstargames.gtasa/", "");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            TypeDownload = 4;
            textViewProgressType.setText("[4/6] Распаковка Файлов игры");
            mProgressBar.setIndeterminate(true);
            mProgressBar.setProgress((int) 0);
            tvMessage.setText("Подождите примерно 5 минут");
                UnZipFullGTA unZipFiles = new UnZipFullGTA();
                unZipFiles.execute();

        }
    }


/*
    private class UpdateTimeTask extends TimerTask {
        public void run() {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "main.8.com.rockstargames.gtasa.obb" );
            File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "patch.8.com.rockstargames.gtasa.obb" );
            long mdb = 0;
            long  mdb2 =file.length()+file2.length();
            Log.d("DIMKOV", "Байтов: "+(file.length()+file2.length()));
           // tvMessage.setText("Байтов" +all);
        }
    }
*/
    private class UnZipFullGTA extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            ZipArchive zipArchive = new ZipArchive();
            zipArchive.unzip(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/gta_full.zip", Environment.getExternalStorageDirectory() + "/Android/obb/com.rockstargames.gtasa/", "");
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressBar.setIndeterminate(false);
            mProgressBar.setProgress((int) 100);
            btn.setText("Выбрать режим");
            btn.setVisibility(View.VISIBLE);
            typeID = false;
            textViewProgressType.setText("Выбор режима");
            AlertDialog alert = builderSelectChooseAPKinstall.create();
            alert.show();
        }
    }

    public void InstallApk() {
        final Uri uri = Uri.parse("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "flinrp_full.apk");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = (Uri) FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID, new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "flinrp_full.apk"));
            Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
            openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            openFileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            openFileIntent.setData(contentUri);
            startActivity(openFileIntent);

        } else {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            install.setDataAndType(uri,
                    "application/vnd.android.package-archive");
            startActivity(install);

        }

    }
}
