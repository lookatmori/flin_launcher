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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ir.mahdi.mzip.zip.ZipArchive;

public class InstallLActivity extends AppCompatActivity {


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
    Button btn;

    String url = "http://d1.flin-rp.su/lite_power.zip";

    AlertDialog.Builder builderSelectChooseAPKinstall;

    private ConstraintLayout rlRoot;
    private String mVendor;
    private GLSurfaceView mGlSurfaceView;

    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    boolean typeID = true;
    private GLSurfaceView.Renderer mGlRenderer = new GLSurfaceView.Renderer() {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.d(TAG, "gl renderer: " + gl.glGetString(GL10.GL_RENDERER));
            Log.d(TAG, "gl vendor: " + gl.glGetString(GL10.GL_VENDOR));
            Log.d(TAG, "gl version: " + gl.glGetString(GL10.GL_VERSION));
            Log.d(TAG, "gl extensions: " + gl.glGetString(GL10.GL_EXTENSIONS));


            editor = prefs.edit();
            editor.putString("RENDERER", gl.glGetString(GL10.GL_RENDERER));
            editor.putString("VENDOR", gl.glGetString(GL10.GL_VENDOR));
            editor.putString("VERSION", gl.glGetString(GL10.GL_VERSION));
            editor.putString("EXTENSIONS", gl.glGetString(GL10.GL_EXTENSIONS));
            editor.commit();

            mVendor = gl.glGetString(GL10.GL_RENDERER);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    textViewGPU.setText("Ваш GPU:" + mVendor);
                    rlRoot.removeView(mGlSurfaceView);
                    //  tvVendor.setVisibility(View.GONE);

                }
            });
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

        }

        @Override
        public void onDrawFrame(GL10 gl) {


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getSharedPreferences("GPUinfo", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_install_l);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        rlRoot = (ConstraintLayout) findViewById(R.id.rlRoot);
        textViewGPU = (TextView) findViewById(R.id.textViewGPUFull);
        mGlSurfaceView = new GLSurfaceView(this);
        mGlSurfaceView.setRenderer(mGlRenderer);
        rlRoot.addView(mGlSurfaceView);
        SharedPreferences prefs = getSharedPreferences("GPUinfo", Context.MODE_PRIVATE);
        String renderer = prefs.getString("RENDERER", null);
        Log.d(TAG, "Render Misha: " + renderer);


        builderSelectChooseAPKinstall = new AlertDialog.Builder(InstallLActivity.this);
        builderSelectChooseAPKinstall.setTitle("Выбор");
        builderSelectChooseAPKinstall.setMessage("Установить APK вручную или автоматическом режиме?");
        builderSelectChooseAPKinstall.setPositiveButton("Авто", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InstallApk();
            }
        });
        builderSelectChooseAPKinstall.setNegativeButton("Вручную", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InstallLActivity.this);
                builder.setTitle("Ручная установка")
                        .setMessage("Откройте проводник, затем найдите папку Download, откройте ее и запустите файл flinrp_lite.apk")
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
                builderSelectHelp = new AlertDialog.Builder(InstallLActivity.this);
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
        textViewGPU.setVisibility(View.GONE);

        tvMessage = (TextView) findViewById(R.id.textViewProgressFull);
        // tvMessage.setVisibility(View.GONE);
        textViewProgressType = (TextView) findViewById(R.id.textViewProgressTypeFull);
        // textViewProgressType.setVisibility(View.GONE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeID == true) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "Permission is granted");
                            TypeCache = 0;
                            TypeDownload = 1;

                            textViewProgressType.setText("[1/6] Загрузка файлов для APK");
                            textViewInfoInstall.setText(R.string.text_loading_warning);
                            //textViewInfoInstall.setVisibility(View.INVISIBLE);
                            //tvMessage.setVisibility(View.INVISIBLE);
                            //textViewProgressType.setVisibility(View.INVISIBLE);

                            btn.setVisibility(View.GONE);
                            typeID = false;

                            DownloadFiles("http://d1.flin-rp.su/files_lite.zip", "files_lite.zip");
                        } else {
                            Log.d(TAG, "Permission is revoked");
                            //запрашиваем разрешение
                            ActivityCompat.requestPermissions(InstallLActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    } else {
                    }
                }
                else
                {
                    textViewProgressType.setText("Выбор режима");
                    AlertDialog alert = builderSelectChooseAPKinstall.create();
                    alert.show();
                }
            }
        });
        try {
            File file = new File("test.txt");
            System.out.println(file.length());
        } catch (Exception e) {
        }
    }

    public void DownloadFiles(String URL, String FileName) {
        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + FileName);//
        sdPath.delete();

        String urlDownload = URL;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlDownload));

        request.setDescription("Testando");
        request.setTitle("Download");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationUri(Uri.parse("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + FileName));
        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        final long downloadId = manager.enqueue(request);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBarFullInstal);

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
                                        DownloadFiles("http://d1.flin-rp.su/flinrp_lite.apk", "flinrp_lite.apk");
                                        TypeDownload = 2;
                                        textViewProgressType.setText("[2/6] Загрузка LITE APK");
                                        break;
                                    }
                                    case 2: {
                                        UnZipFiles unZipFiles = new UnZipFiles();
                                        unZipFiles.execute();
                                        TypeDownload = 3;
                                        textViewProgressType.setText("[3/6] Распаковка архива");
                                        break;
                                    }
                                    case 4: {
                                        UnZipCache unZipFiles = new UnZipCache();
                                        unZipFiles.execute();
                                        TypeDownload = 5;
                                        textViewProgressType.setText("[5/6] Распаковка архива с кэшем");
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
            zipArchive.unzip(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/files_lite.zip", Environment.getExternalStorageDirectory() + "/Android/data/com.rockstargames.gtasa/", "");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String str = mVendor;
            if (str.charAt(0) == 'A') {
                DownloadFiles("http://d1.flin-rp.su/lite_adreno.zip", "lite_adreno.zip");
                TypeDownload = 4;
                TypeCache = 1;
                textViewProgressType.setText("[4/6] Скачивание кэша Adreno");
            }
            if (str.charAt(0) == 'P') {
                DownloadFiles("http://d1.flin-rp.su/lite_power.zip", "lite_power.zip");
                TypeDownload = 4;
                TypeCache = 2;
                textViewProgressType.setText("[4/6] Скачивание кэша Power");
            }
            if (str.charAt(0) == 'M') {
                DownloadFiles("http://d1.flin-rp.su/lite_mali.zip", "lite_mali.zip");
                TypeDownload = 4;
                TypeCache = 3;
                textViewProgressType.setText("[4/6] Скачивание кэша Mali");
            }

        }
    }

    private class UnZipCache extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            switch (TypeCache) {
                case 0: {
                    textViewProgressType.setText("Произошла ошибка при распаковке");
                    break;
                }
                case 1: {
                    ZipArchive zipArchive = new ZipArchive();
                    zipArchive.unzip(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/lite_adreno.zip", Environment.getExternalStorageDirectory() + "/Android/data/com.rockstargames.gtasa/", "");
                    break;
                }
                case 2: {
                    ZipArchive zipArchive = new ZipArchive();
                    zipArchive.unzip(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/lite_power.zip", Environment.getExternalStorageDirectory() + "/Android/data/com.rockstargames.gtasa/", "");
                    break;
                }
                case 3: {
                    ZipArchive zipArchive = new ZipArchive();
                    zipArchive.unzip(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/lite_mali.zip", Environment.getExternalStorageDirectory() + "/Android/data/com.rockstargames.gtasa/", "");
                    break;
                }
            }

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


