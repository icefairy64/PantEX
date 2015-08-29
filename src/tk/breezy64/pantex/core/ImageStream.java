/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author icefairy64
 */
public class ImageStream extends InputStream {
    
    protected final long offset;
    protected final int size;
    protected final FileChannel handle;
    
    protected int position = 0;
    
    public ImageStream(FileChannel handle, long offset, int size) {
        this.handle = handle;
        this.offset = offset;
        this.size = size;
    }
    
    @Override
    public int read() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1);

        int rsize = handle.read(buffer, offset + position);
        position++;
        if (rsize < 0) {
            return -1;
        }

        buffer.rewind();
        return Byte.toUnsignedInt(buffer.get());
    }

    @Override
    public int read(byte[] b) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(b);
        int rsize = handle.read(buffer, offset + position);
        if (rsize > 0) {
            position += rsize;
        }

        return rsize;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(b, off, len);
        int rsize = handle.read(buffer, offset + position);
        if (rsize > 0) {
            position += rsize;
        }

        return rsize;
    }

    @Override
    public int available() throws IOException {
        return size - position;
    }

    @Override
    public synchronized void reset() throws IOException {
        position = 0;
    }
    
}
