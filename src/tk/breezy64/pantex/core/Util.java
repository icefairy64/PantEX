/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author icefairy64
 */
public final class Util {
    
    private static final ByteBuffer intBuf = ByteBuffer.allocate(4);
    
    private static final int bufferSize = 4096;
    
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

    public static InputStream fetchStream(String url) {
        try {
            CloseableHttpClient hc = HttpClients.createDefault();
            HttpGet hr = new HttpGet(url);
            hr.addHeader("User-Agent", Util.userAgent);
            CloseableHttpResponse resp = hc.execute(hr);
            InputStream in = resp.getEntity().getContent();
            return in;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static String fetch(String url) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream in = Util.fetchStream(url);
            int len = 0;
            byte[] buf = new byte[bufferSize];
            while ((len = in.read(buf)) > 0) {
                for (int i = 0; i < len; i++) {
                    sb.append((char) buf[i]);
                }
            }
            in.close();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
