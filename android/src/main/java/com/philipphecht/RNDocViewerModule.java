package com.philipphecht;

import com.facebook.common.file.FileUtils;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.views.webview.ReactWebViewManager;

/* bridge react native
int size();
boolean isNull(int index);
boolean getBoolean(int index);
double getDouble(int index);
int getInt(int index);
String getString(int index);
ReadableArray getArray(int index);
ReadableMap getMap(int index);
ReadableType getType(int index);
*/
//Third Libraries
import java.io.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import android.support.v4.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import android.util.Log;
import android.webkit.WebView;

public class RNDocViewerModule extends ReactContextBaseJavaModule {
    public static final int ERROR_NO_HANDLER_FOR_DATA_TYPE = 53;
    public static final int ERROR_FILE_NOT_FOUND = 2;
    public static final int ERROR_UNKNOWN_ERROR = 1;
    private final ReactApplicationContext reactContext;
    private static final String[][] MIME_MapTable = {
            //{后缀名， MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };

    public RNDocViewerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNDocViewer";
    }

    @ReactMethod
    public void openDoc(ReadableArray args, Callback callback) {
        final ReadableMap arg_object = args.getMap(0);
        try {
            if (arg_object.getString("url") != null && arg_object.getString("fileName") != null) {
                // parameter parsing
                System.out.println("openDoc--》" + args);
                final String url = arg_object.getString("url");
                final String fileName = arg_object.getString("fileName");
                final String fileType = arg_object.getString("fileType");
                final Boolean cache = arg_object.getBoolean("cache");
                final byte[] bytesData = new byte[0];
                // Begin the Download Task
                new FileDownloaderAsyncTask(callback, url, cache, fileName, fileType, bytesData).execute();
            } else {
                System.out.println("openDoc null");
                callback.invoke(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.invoke(e.getMessage());
        }
    }


    @ReactMethod
    public void openDocb64(ReadableArray args, Callback callback) {
        final ReadableMap arg_object = args.getMap(0);
        try {
            if (arg_object.getString("base64") != null && arg_object.getString("fileName") != null && arg_object.getString("fileType") != null) {
                // parameter parsing
                final String base64 = arg_object.getString("base64");
                final String fileName = arg_object.getString("fileName");
                final String fileType = arg_object.getString("fileType");
                final Boolean cache = arg_object.getBoolean("cache");
                //Bytes
                final byte[] bytesData = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
                System.out.println("BytesData" + bytesData);
                // Begin the Download Task
                new FileDownloaderAsyncTask(callback, "", cache, fileName, fileType, bytesData).execute();
            } else {
                callback.invoke(false);
            }
        } catch (Exception e) {
            callback.invoke(e.getMessage());
        }


    }

    @ReactMethod
    public void openDocBinaryinUrl(ReadableArray args, Callback callback) {
        final ReadableMap arg_object = args.getMap(0);
        try {
            if (arg_object.getString("url") != null && arg_object.getString("fileName") != null && arg_object.getString("fileType") != null) {
                // parameter parsing
                final String url = arg_object.getString("url");
                final String fileName = arg_object.getString("fileName");
                final String fileType = arg_object.getString("fileType");
                final Boolean cache = arg_object.getBoolean("cache");
                final byte[] bytesData = new byte[0];
                // Begin the Download Task
                new FileDownloaderAsyncTask(callback, url, cache, fileName, fileType, bytesData).execute();
            } else {
                callback.invoke(false);
            }
        } catch (Exception e) {
            callback.invoke(e.getMessage());
        }
    }

    // used for all downloaded files, so we can find and delete them again.
    private final static String FILE_TYPE_PREFIX = "PP_";

    /**
     * downloads the file from the given url to external storage.
     *
     * @param url
     * @return
     */
    private File downloadFile(String url, String fileName, Boolean cache, String fileType, byte[] bytesData, Callback callback) {

        try {
            Context context = getReactApplicationContext().getBaseContext();
            File outputDir = context.getCacheDir();
            if (bytesData.length > 0) {
                // use cache
                File f = cache != null && cache ? new File(outputDir, fileName) : File.createTempFile(FILE_TYPE_PREFIX, "." + fileType,
                        outputDir);
                System.out.println("Bytes will be creating a file");
                final FileOutputStream fileOutputStream;
                try {
                    fileOutputStream = new FileOutputStream(f);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }

                final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                        fileOutputStream);
                try {
                    bufferedOutputStream.write(bytesData);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    try {
                        bufferedOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return f;
            } else {
                String extension = MimeTypeMap.getFileExtensionFromUrl(url);
                System.out.println("Extensions DownloadFile " + extension);
                if (extension.equals("") && fileType.equals("")) {
                    extension = "pdf";
                    System.out.println("extension (default): " + extension);
                }

                if (fileType != "" && extension.equals("")) {
                    extension = fileType;
                    System.out.println("extension (default): " + extension);
                }

                // check has extension
                if (fileName.indexOf("\\.") == -1) {
                    fileName = fileName + '.' + extension;
                }
                // if use cache, check exist
                if (cache != null && cache) {
                    File existFile = new File(outputDir, fileName);
                    if (existFile.exists()) {
                        return existFile;
                    }
                }

                // get an instance of a cookie manager since it has access to our
                // auth cookie
                CookieManager cookieManager = CookieManager.getInstance();

                // get the cookie string for the site.
                String auth = null;
                if (cookieManager.getCookie(url) != null) {
                    auth = cookieManager.getCookie(url).toString();
                }

                URL url2 = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
                File f;
                try {
                    if (auth != null) {
                        conn.setRequestProperty("Cookie", auth);
                    }
                    InputStream reader = conn.getInputStream();

                    // use cache
                    f = cache != null && cache ? new File(outputDir, fileName)
                            : File.createTempFile(FILE_TYPE_PREFIX, "." + extension, outputDir);

                    // make sure the receiving app can read this file
                    f.setReadable(true, false);
                    System.out.println(f.getPath());
                    FileOutputStream outStream = new FileOutputStream(f);

                    //GET Connection Content length
                    int fileLength = conn.getContentLength();
                    /*int readBytes = reader.read(buffer);
                    while (readBytes > 0) {
                        outStream.write(buffer, 0, readBytes);
                        readBytes = reader.read(buffer);
                    }*/
                    byte[] buffer = new byte[4096];
                    long total = 0;
                    int readBytes = reader.read(buffer);
                    while (readBytes > 0) {
                        total += readBytes;
                        // publishing the progress....
                        if (fileLength > 0) // only if total length is known
                            System.out.println((int) (total * 100 / fileLength));
                        outStream.write(buffer, 0, readBytes);
                        readBytes = reader.read(buffer);
                    }
                    reader.close();
                    outStream.close();
                    if (f.exists()) {
                        System.out.println("File exists");
                    } else {
                        System.out.println("File doesn't exist");
                    }

                    return f;
                } catch (Exception err) {
                    err.printStackTrace();
                } finally {
                    conn.disconnect();
                }

                return null;
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            callback.invoke(ERROR_FILE_NOT_FOUND);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            callback.invoke(ERROR_UNKNOWN_ERROR);
            return null;
        }
    }

    private File copyFile(InputStream in, String fileName, Boolean cache, String fileType, byte[] bytesData, Callback callback) {

        try {
            Context context = getReactApplicationContext().getBaseContext();
            File outputDir = context.getCacheDir();
            if (bytesData.length > 0) {
                // use cache
                File f = cache != null && cache ? new File(outputDir, fileName) : File.createTempFile(FILE_TYPE_PREFIX, "." + fileType,
                        outputDir);
                System.out.println("Bytes will be creating a file");
                final FileOutputStream fileOutputStream;
                try {
                    fileOutputStream = new FileOutputStream(f);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }

                final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                        fileOutputStream);
                try {
                    bufferedOutputStream.write(bytesData);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    try {
                        bufferedOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return f;
            } else {
                String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
                System.out.println("Extensions DownloadFile " + extension);
                if (extension.equals("") && fileType.equals("")) {
                    extension = "pdf";
                    System.out.println("extension (default): " + extension);
                }

                if (fileType != "" && extension.equals("")) {
                    extension = fileType;
                    System.out.println("extension (default): " + extension);
                }

                // check has extension
                if (fileName.indexOf("\\.") == -1) {
                    fileName = fileName + '.' + extension;
                }
                // if use cache, check exist
                if (cache != null && cache) {
                    File existFile = new File(outputDir, fileName);
                    if (existFile.exists()) {
                        return existFile;
                    }
                }


                File f;
                try {
                    // use cache
                    f = cache != null && cache ? new File(outputDir, fileName)
                            : File.createTempFile(FILE_TYPE_PREFIX, "." + extension, outputDir);

                    // make sure the receiving app can read this file
                    f.setReadable(true, false);
                    System.out.println(f.getPath());

                    try {
                        FileOutputStream outStream = new FileOutputStream(f);
                        try {
                            // Transfer bytes from in to out
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                outStream.write(buf, 0, len);
                            }
                        } finally {
                            outStream.close();
                        }
                    } finally {
                        in.close();
                    }

                    if (f.exists()) {
                        System.out.println("File exists");
                    } else {
                        System.out.println("File doesn't exist");
                    }

                    return f;
                } catch (Exception err) {
                    err.printStackTrace();
                }

                return null;
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            callback.invoke(ERROR_FILE_NOT_FOUND);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            callback.invoke(ERROR_UNKNOWN_ERROR);
            return null;
        }
    }

    /**
     * Returns the MIME Type of the file by looking at file name extension in
     * the URL.
     *
     * @param url
     * @return
     */
    private static String getMimeType(String url) {
        String mimeType = null;
        System.out.println("Url: " + url);
        url = url.replaceAll(" ", "");
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        if (mimeType == null) {
            mimeType = "application/pdf";
            System.out.println("Mime Type (default): " + mimeType);
        }

        return mimeType;
    }

    private class FileDownloaderAsyncTask extends AsyncTask<Void, Void, File> {
        private final Callback callback;
        private final String url;
        private final String fileName;
        private final Boolean cache;
        private final String fileType;
        private final byte[] bytesData;

        public FileDownloaderAsyncTask(Callback callback,
                                       String url, Boolean cache, String fileName, String fileType, byte[] bytesData) {
            super();
            this.callback = callback;
            this.url = url;
            this.fileName = fileName;
            this.cache = cache;
            this.fileType = fileType;
            this.bytesData = bytesData;
        }

        @Override
        protected File doInBackground(Void... arg0) {
            if (url.startsWith("content://")) {
                System.out.println("doInBackground content to download" + url);
                File file = null;
                try {
                    InputStream in = getCurrentActivity().getContentResolver().openInputStream(Uri.parse(url));
                    file = copyFile(in, fileName, cache, fileType, bytesData, callback);
                } catch (FileNotFoundException e) {
                    System.out.println(e);
                }
                return file;
            } else if (!url.startsWith("file://")) {
                System.out.println("doInBackground file Url to download" + url);
                return downloadFile(url, fileName, cache, fileType, bytesData, callback);
            } else {
                System.out.println("doInBackground new file Url to download" + url);
                return new File(url.replace("file://", ""));
            }
        }

        @Override
        protected void onPostExecute(File result) {
            System.out.println("onPostExecute 1");
            if (result == null) {
                // Has already been handled
                return;
            }
            System.out.println("onPostExecute 2");
            Context context = getCurrentActivity();
            String mimeType;
            // mime type of file data
//            if (fileType != null) {
            // If file type is already specified, should just take the mimeType from it
            mimeType = getMIMEType(url);
            System.out.println("onPostExecute 3" + mimeType);
            System.out.println("onPostExecute 3" + context);
//            } else {
//              mimeType = getMimeType(url);
//                System.out.println("onPostExecute 4"+ mimeType);
//            }
            if (mimeType == null || context == null) {
                return;
            }
            System.out.println("onPostExecute 5");
            try {

                Uri contentUri = FileProvider.getUriForFile(context, reactContext.getPackageName() + ".docViewer_provider", result);
                System.out.println("ContentUri");
                System.out.println(contentUri);
                System.out.println("onPostExecute 6");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(contentUri, getMIMEType(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                    // Thread-safe.
                    callback.invoke(null, fileName);
                } else {
                    activityNotFoundMessage("Activity not found to handle: " + contentUri.toString() + " (" + mimeType + ")");
                }
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                activityNotFoundMessage(e.getMessage());
            }

        }

        private void activityNotFoundMessage(String message) {
            System.out.println("ERROR");
            System.out.println(message);
            callback.invoke(message);
            //e.printStackTrace();
        }
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     */
    public static String getMIMEType(String fName) {
        String type = "*/*";

        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }

        /* 获取文件的后缀名 */
        String end = fName.substring(dotIndex, fName.length()).toLowerCase(Locale.getDefault());
        if (TextUtils.isEmpty(end)) {
            return type;
        }

        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }
}
