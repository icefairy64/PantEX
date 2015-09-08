/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author icefairy64
 */
public final class Util {
    
    private static final ByteBuffer intBuf = ByteBuffer.allocate(4);
    private static final int bufferSize = 4096;
    private static final Map<String, OkHttpClient> sessions = new HashMap<>();
    private static final String defaultSession = "common";
    
    public static final String userAgent = "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:39.0) Gecko/20100101 Firefox/39.0";
    
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
        ch.read(intBuf);
        intBuf.rewind();
        int res = intBuf.getInt();
        intBuf.rewind();
        return res;
    }
    
    public static void writeInt(FileChannel ch, int value) throws IOException {
        intBuf.putInt(value);
        intBuf.rewind();
        ch.write(intBuf);
        intBuf.rewind();
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        int len = 0;
        byte[] buf = new byte[bufferSize];
        while ((len = in.read(buf)) >= 0) {
            out.write(buf, 0, len);
        }
    }
    
    public static void copy(InputStream in, OutputStream out, int size) throws IOException {
        int len = 0;
        int read = 0;
        byte[] buf = new byte[bufferSize];
        while ((len = in.read(buf, 0, Math.min(bufferSize, size - read))) >= 0 && size != read) {
            out.write(buf, 0, len);
            read += len;
        }
    }
    
    public static String readStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader r = new InputStreamReader(in);
        char[] buf = new char[bufferSize];
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
                .addHeader("User-Agent", userAgent);
        for (String[] head : headers) {
            reqb.addHeader(head[0], head[1]);
        }
        Request req = reqb.build();
        
        return client.newCall(req).execute();
    }

    public static InputStream fetchHttpStream(String url, String session, String[]... headers) throws IOException {
        OkHttpClient client;
        if (session == null) {
            client = new OkHttpClient();
        }
        else {
            if (!sessions.containsKey(session)) {
                sessions.put(session, new OkHttpClient());
            }
            client = sessions.get(session);
        }
        
        return getHttpResponse(url, client, headers).body().byteStream();
    }
    
    public static InputStream fetchHttpStream(String url, String[]... headers) throws IOException {
        return fetchHttpStream(url, defaultSession, headers);
    }

    public static String fetchHttpContent(String url, String[]... headers) throws IOException {
        if (!sessions.containsKey(defaultSession)) {
            sessions.put(defaultSession, new OkHttpClient());
        }
        
        return getHttpResponse(url, sessions.get(defaultSession), headers).body().string();
    }
}
