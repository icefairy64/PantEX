/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author icefairy64
 */
public final class Util {
    
    private static final ByteBuffer INT_BUF = ByteBuffer.allocate(4);
    private static final int BUFFER_SIZE = 4096;
    private static final Map<String, OkHttpClient> SESSIONS = new HashMap<>();
    private static final String DEFAULT_SESSION = "common";
    
    public static final String USER_AGENT = "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:39.0) Gecko/20100101 Firefox/39.0";
    
    public static String readStr(InputStream s, int separator) throws IOException {
        int c = s.read();
        StringBuilder sb = new StringBuilder();
        do {
            sb.append((char)c);
        } while ((c = s.read()) != separator);
        
        return sb.toString();
    }
    
    public static void writeStr(OutputStream out, String s, int separator) throws IOException {
        for (char c : s.toCharArray()) {
            out.write(c);
        }
        out.write(separator);
    }
    
    public static int readInt(FileChannel ch) throws IOException {
        ch.read(INT_BUF);
        INT_BUF.rewind();
        int res = INT_BUF.getInt();
        INT_BUF.rewind();
        return res;
    }
    
    public static void writeInt(FileChannel ch, int value) throws IOException {
        INT_BUF.putInt(value);
        INT_BUF.rewind();
        ch.write(INT_BUF);
        INT_BUF.rewind();
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        int len;
        byte[] buf = new byte[BUFFER_SIZE];
        while ((len = in.read(buf)) >= 0) {
            out.write(buf, 0, len);
        }
    }
    
    public static void copy(InputStream in, OutputStream out, int size) throws IOException {
        int len;
        int read = 0;
        byte[] buf = new byte[BUFFER_SIZE];
        while ((len = in.read(buf, 0, Math.min(BUFFER_SIZE, size - read))) >= 0 && size != read) {
            out.write(buf, 0, len);
            read += len;
        }
    }
    
    public static String readStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader r = new InputStreamReader(in);
        char[] buf = new char[BUFFER_SIZE];
        int len = 0;
        
        while ((len = r.read(buf)) >= 0) {
            sb.append(buf);
        }
        
        in.close();
        return sb.toString();
    }
    
    private static Response getHttpResponse(String url, OkHttpClient client, String[]... headers) throws IOException {
        Request.Builder reqb = new Request.Builder()
                .url(url)
                .get()
                .addHeader("User-Agent", USER_AGENT);
        for (String[] head : headers) {
            reqb.addHeader(head[0], head[1]);
        }
        Request req = reqb.build();
        
        return client.newCall(req).execute();
    }

    private static OkHttpClient getClient(String session) {
        OkHttpClient client;
        if (session == null) {
            session = DEFAULT_SESSION;
        }
        
        if (!SESSIONS.containsKey(session)) {
            OkHttpClient c = new OkHttpClient();
            if (session.equals(DEFAULT_SESSION)) {
                CookieManager cookieManager = new CookieManager();
                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
                c.setCookieHandler(cookieManager);
            }
            SESSIONS.put(session, c);
        }
        client = SESSIONS.get(session);
        
        return client;
    }
    
    public static InputStream fetchHttpStream(String url, String session, String[]... headers) throws IOException {
        return getHttpResponse(url, getClient(session), headers).body().byteStream();
    }
    
    public static String post(String url, Map<String, String> params) throws IOException {
        OkHttpClient client = getClient(DEFAULT_SESSION);
        FormEncodingBuilder b = new FormEncodingBuilder();
        for (Map.Entry<String, String> e : params.entrySet()) {
            b.add(e.getKey(), e.getValue());
        }
        
        Request req = new Request.Builder()
                .post(b.build())
                .url(url)
                .build();
        
        Response r = client.newCall(req).execute();
        System.out.println("Result: " + r.toString());
        return r.body().string();
    }
    
    public static InputStream fetchHttpStream(String url, String[]... headers) throws IOException {
        return fetchHttpStream(url, DEFAULT_SESSION, headers);
    }

    public static String fetchHttpContent(String url, String[]... headers) throws IOException {
        return getHttpResponse(url, getClient(DEFAULT_SESSION), headers).body().string();
    }
    
    public static String fetchHttpContent(String url, String session, String[]... headers) throws IOException {
        return getHttpResponse(url, getClient(session), headers).body().string();
    }
    
    public static <T> T getExtensionClass(Class<T> base, String className) {
        return Static.pluginManager.getExtensions(base)
                .stream()
                .filter(x -> x.getClass().getName().equals(className))
                .findFirst()
                .get();
    }
    
}
