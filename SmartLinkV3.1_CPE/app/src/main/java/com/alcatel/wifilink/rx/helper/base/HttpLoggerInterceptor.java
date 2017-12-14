package com.alcatel.wifilink.rx.helper.base;

import com.orhanobut.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;

public class HttpLoggerInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private volatile Level printLevel = Level.NONE;
    private String tag;

    public enum Level {
        NONE,       //不打印log
        BASIC,      //只打印 请求首行 和 响应首行
        HEADERS,    //打印请求和响应的所有 Header
        BODY        //所有数据全部打印
    }

    public HttpLoggerInterceptor(String tag) {
        this.tag = tag;
    }

    public void setPrintLevel(Level level) {
        if (printLevel == null)
            throw new NullPointerException("printLevel == null. Use Level.NONE instead.");
        printLevel = level;
    }

    private void log(String message) {
        Logger.t(tag).v(message);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (printLevel == Level.NONE) {
            return chain.proceed(request);
        }

        //请求日志拦截
        logForRequest(request, chain.connection());

        //执行请求，计算请求时间
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            log("<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        //响应日志拦截
        return logForResponse(response, tookMs);
    }

    private void logForRequest(Request request, Connection connection) throws IOException {
        boolean logBody = (printLevel == Level.BODY);
        boolean logHeaders = (printLevel == Level.BODY || printLevel == Level.HEADERS);
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage = "";
        try {
            requestStartMessage += "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
            if (logHeaders) {
                if (hasRequestBody) {
                    // Request body headers are only present when installed as a network interceptor. Force
                    // them to be included (when available) so there values are known.
                    if (requestBody.contentType() != null) {
                        requestStartMessage += System.getProperty("line.separator") + "\tContent-Type: " + requestBody.contentType();
                    }
                    if (requestBody.contentLength() != -1) {
                        requestStartMessage += System.getProperty("line.separator") + "\tContent-Length: " + requestBody.contentLength();
                    }
                }
                Headers headers = request.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    String name = headers.name(i);
                    // Skip headers from the request body as they are explicitly logged above.
                    if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                        requestStartMessage += System.getProperty("line.separator") + "\t" + name + ": " + headers.value(i);
                    }
                }

                requestStartMessage += System.getProperty("line.separator") + "";
                if (logBody && hasRequestBody) {
                    if (isPlaintext(requestBody.contentType())) {
                        requestStartMessage += bodyToString(request);
                    } else {
                        requestStartMessage += System.getProperty("line.separator") + "\tbody: maybe [binary body], omitted!";
                    }
                }
            }
        } catch (Exception e) {
            Logger.t("OKHTTP").v(e.getMessage());
        } finally {
            requestStartMessage += System.getProperty("line.separator") + "--> END " + request.method();
        }
        log(requestStartMessage);
    }

    private Response logForResponse(Response response, long tookMs) {
        Response.Builder builder = response.newBuilder();
        Response clone = builder.build();
        ResponseBody responseBody = clone.body();
        boolean logBody = (printLevel == Level.BODY);
        boolean logHeaders = (printLevel == Level.BODY || printLevel == Level.HEADERS);
        String responseEndMessage = "";
        try {
            responseEndMessage += "<-- " + clone.code() + ' ' + clone.message() + ' ' + clone.request().url() + " (" + tookMs + "ms）";
            if (logHeaders) {
                Headers headers = clone.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    responseEndMessage += System.getProperty("line.separator") + "\t" + headers.name(i) + ": " + headers.value(i);
                }
                responseEndMessage += System.getProperty("line.separator") + "";
                if (logBody && HttpHeaders.hasBody(clone)) {
                    if (responseBody == null)
                        return logForResponse(response, responseEndMessage);

                    if (isPlaintext(responseBody.contentType())) {
                        byte[] bytes = toByteArray(responseBody.byteStream());
                        MediaType contentType = responseBody.contentType();
                        String body = new String(bytes, getCharset(contentType));
                        responseEndMessage += System.getProperty("line.separator") + "\tbody:" + body;
                        responseBody = ResponseBody.create(responseBody.contentType(), bytes);
                        return logForResponse(response.newBuilder().body(responseBody).build(), responseEndMessage);
                    } else {
                        responseEndMessage += System.getProperty("line.separator") + "\tbody: maybe [binary body], omitted!";
                    }
                }
            }
        } catch (Exception e) {
            Logger.t("OKHTTP").v(e.getMessage());
        }
        return logForResponse(response, responseEndMessage);
    }

    private Response logForResponse(Response response, String responseEndMessage) {
        responseEndMessage += System.getProperty("line.separator") + "<-- END HTTP";
        log(responseEndMessage);
        return response;
    }

    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF8) : UTF8;
        if (charset == null)
            charset = UTF8;
        return charset;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private static boolean isPlaintext(MediaType mediaType) {
        if (mediaType == null)
            return false;
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        String subtype = mediaType.subtype();
        if (subtype != null) {
            subtype = subtype.toLowerCase();
            if (subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html")) //
                return true;
        }
        return false;
    }

    private String bodyToString(Request request) {
        String bodystr = "";
        try {
            Request copy = request.newBuilder().build();
            RequestBody body = copy.body();
            if (body == null)
                return bodystr;
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            Charset charset = getCharset(body.contentType());
            bodystr += System.getProperty("line.separator") + "\tbody:" + buffer.readString(charset);
        } catch (Exception e) {
            Logger.t("OKHTTP").v(e.getMessage());
        }

        return bodystr;
    }

    public byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        write(input, output);
        output.close();
        return output.toByteArray();
    }

    public void write(InputStream inputStream, OutputStream outputStream) throws IOException {
        int len;
        byte[] buffer = new byte[4096];
        while ((len = inputStream.read(buffer)) != -1)
            outputStream.write(buffer, 0, len);
    }

}
