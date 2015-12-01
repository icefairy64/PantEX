/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 *
 * @author icefairy64
 */
public class SavedStream extends InputStream {

    private OutputStream out;
    private DynamicByteArray data;
    
    public SavedStream() {
        out = new Writer();
        data = new DynamicByteArray(0x400);
    }
    
    public OutputStream getWriter() {
        return out;
    }
    
    public void endWriting() {
        data.position = 0;
    }
    
    @Override
    public int read() throws IOException {
        return data.position >= data.length ?
                -1 :
                Byte.toUnsignedInt(data.read());
    }

    @Override
    public int read(byte[] b) throws IOException {
        return data.read(b, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return data.read(b, off, len);
    }

    @Override
    public synchronized void reset() throws IOException {
        endWriting();
    }

    @Override
    public int available() throws IOException {
        return data.length - data.position;
    }

    private class Writer extends OutputStream {

        @Override
        public void write(byte[] b) throws IOException {
            data.append(b, b.length);
        }

        @Override
        public void write(int b) throws IOException {
            data.append((byte)b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            data.append(b, off, len);
        }
        
    }
    
    private static class DynamicByteArray {
        
        public byte[] data;
        public int position = 0;
        public int capacity;
        public int length;
        
        public DynamicByteArray(int capacity) {
            data = new byte[capacity];
            this.capacity = capacity;
        }
        
        public void prepare(int newSize) {
            if (newSize >= capacity) {
                capacity *= 2;
                byte[] newArray = Arrays.copyOf(data, capacity);
                data = newArray;
            }
        }
        
        public void append(byte src) {
            prepare(position + 1);
            data[position++] = src;
            length++;
        }
        
        public void append(byte[] src, int len) {
            append(src, 0, len);
        }
        
        public void append(byte[] src, int start, int len) {
            prepare(position + len - start);
            for (int i = start; i < len; i++) {
                data[position++] = src[i];
            }
            length += len - start;
        }
        
        public byte read() {
            return data[position++];
        }
        
        public int read(byte[] dest, int len) {
            return read(dest, 0, len);
        }
        
        public int read(byte[] dest, int offset, int len) {
            int rl = position + len - offset >= length ? 
                    length - position :
                    len;
            
            if (rl == 0)
                return -1;
            
            for (int i = offset; i < offset + rl; i++) {
                dest[i] = data[position++];
            }
            
            return rl;
        }
        
    }
    
}
