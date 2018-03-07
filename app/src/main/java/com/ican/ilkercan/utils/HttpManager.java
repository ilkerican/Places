package com.ican.ilkercan.utils;

/**
 * Created by ilkercan on 02/11/16.
 */

        import android.content.Context;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import java.io.OutputStreamWriter;
        import java.io.UnsupportedEncodingException;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.URL;
        import java.net.URLEncoder;
        import java.util.List;

        import cz.msebera.android.httpclient.NameValuePair;




/**
 * Created by hp on 26.8.2016.
 */
public class HttpManager {

    public static boolean isConnectingToInternet(Context ctx){
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null)

                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    try
                    {
                        HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                        urlc.setRequestProperty("User-Agent", "Test");
                        urlc.setRequestProperty("Connection", "close");
                        urlc.setConnectTimeout(500); //choose your own timeframe
                        urlc.setReadTimeout(500); //choose your own timeframe
                        urlc.connect();
                        int networkcode2 = urlc.getResponseCode();
                        return (urlc.getResponseCode() == 200);
                    } catch (IOException e)
                    {
                        return (false);  //connectivity exists, but no internet.
                    }
                }

        }
        return false;
    }

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        } else {
            return false;

        }
    }

    public static String getData(String urlString) throws IOException {

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuffer buffer = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            buffer = new StringBuffer();

            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
            return Constants.ERROR;
        } catch (IOException e) {
            e.printStackTrace();
            return Constants.ERROR;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return Constants.ERROR;
            }

        }

        if (buffer != null) {
            return buffer.toString();
        } else

        {
            return Constants.ERROR;
        }

    }


    public static String postData(String urlString, List<NameValuePair> params) throws IOException {

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuffer buffer = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            buffer = new StringBuffer();

            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (buffer != null) {
            return buffer.toString();
        } else

        {
            return "";
        }

    }

    private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
