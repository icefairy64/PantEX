/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 *
 * @author icefairy64
 */
public class FileImage extends EXImage {
    
    public File file;
    
    public FileImage(String fileName) {
        this(new File(fileName));
    }
    
    public FileImage(File file) {
        this(file, null, file.getName(), null);
    }
    
    public FileImage(String fileName, Collection collection, String title, List<Tag> tags) {
        this(new File(fileName), collection, title, tags);
    }
    
    public FileImage(File file, Collection collection, String title, List<Tag> tags) {
        super(collection, title, tags);
        
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException(new FileNotFoundException(String.format("EXImage is not found at path %s", file.getPath())));
        }
        this.file = file;
    }

    @Override
    public InputStream getImageStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public void writeImage(OutputStream out) throws IOException {
        InputStream in = new FileInputStream(file);
        Util.copy(in, out);
        in.close();
    }

    @Override
    public int getImageSize() {
        return (int)file.length();
    }
    
}
