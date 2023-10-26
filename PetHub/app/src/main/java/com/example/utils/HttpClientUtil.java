package com.example.utils;

import com.example.vo.ServerResponse;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClientUtil {
    private static final int CONNECT_TIMEOUT = 6;
    private static final int READ_TIMEOUT = 60;
    private static final int WRITE_TIMEOUT = 6;

    private static final String ENCODE = "UTF-8";

    public static String doGet(String url, String path, Map<String, Object> mapParams) {
        String resultString = "";
        Response response = null;
        try {
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .build();
            String params = setUrlParams(mapParams);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(url);
            stringBuffer.append(path);
            stringBuffer.append(params);

            Request request = new Request.Builder()
                    .url(stringBuffer.toString())
                    .get()
                    .build();
            System.out.println(request);

            response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code" + response);
            }

            if (response.body() != null) {
                resultString = response.body().string();
            }
            System.out.println(resultString);
            return resultString;
        } catch (SocketTimeoutException e) {
            resultString = new ServerResponse(501, "网络请求超时，请检查网络连接").toString();
        } catch (IOException e) {
            resultString = new ServerResponse(504, "网络请求失败，请检查网络连接").toString();
        } catch (Exception e) {
            resultString = new ServerResponse(500, "发生未知错误").toString();
        }  finally {
            if (response != null) {
                if (response.body() != null) {
                    response.body().close();
                }
            }
        }
        return resultString;
    }

    public static String doPost(String url, String path, Map<String, Object> mapParams) {
        String resultString = "";
        Response response = null;
        try {
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .build();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(url);
            stringBuffer.append(path);
            FormBody.Builder body = new FormBody.Builder();
            for (Map.Entry<String, Object> entry : mapParams.entrySet()) {
                body = new FormBody.Builder()
                        .add(entry.getKey(), entry.getValue().toString());
            }

            Request request = new Request.Builder()
                    .url(stringBuffer.toString())
                    .post(body.build())
                    .build();

            response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code" + response);
            }

            resultString = response.body().string();
            return resultString;
        } catch (SocketTimeoutException e) {
            resultString = new ServerResponse(501, "网络请求超时，请检查网络连接").toString();
        } catch (IOException e) {
            resultString = new ServerResponse(504, "网络请求失败，请检查网络连接").toString();
        } catch (Exception e) {
            resultString = new ServerResponse(500, "发生未知错误").toString();
        } finally {
            if (response != null) {
                response.body().close();
            }
        }
        return resultString;
    }

    public static String setUrlParams(Map<String, Object> mapParams) {
        System.out.println(mapParams.get("username"));
        boolean hasSetQuestionMark = false;
        StringBuffer strParams = new StringBuffer("");
        if (mapParams != null) {
            Iterator<String> iterator = mapParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                try {
                    key = iterator.next().toString();
                    if (!hasSetQuestionMark) {
                        strParams.append("?").append(key).append("=")
                                .append(URLEncoder.encode(mapParams.get(key).toString(), "utf-8"));
                        hasSetQuestionMark = true;
                        continue;
                    }
                    strParams.append("&").append(key).append("=")
                            .append(URLEncoder.encode(mapParams.get(key).toString(), "utf-8"));
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }
        return strParams.toString();
    }
}
