/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author icefairy64
 */
public class EXPack {
    
    private static final byte[] signature = { 0x50, 0x41, 0x4e, 0x54, 0x53, 0x55, 0x45, 0x58 };
    private static final int separator = 0x00;
    
    public static Collection load(File file, Collection res) throws IOException, EXPackException {
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException(String.format("EXPack is not found at path %s", file.getPath()));
        }
        
        FileInputStream f = new FileInputStream(file);
        FileChannel ch = f.getChannel();
        
        if (!checkSignature(f)) {
            throw new EXPackException("Signature check failed");
        }
        
        // Reading title
        String title = Util.readStr(f, separator);
        res.title = title;
        //DefaultCollection res = new DefaultCollection(title);
        
        // Reading tags
        int tagCount = Util.readInt(ch);
        Tag[] tags = new Tag[tagCount];
        
        for (int i = 0; i < tagCount; i++) {
            tags[i] = Tag.create(Util.readStr(f, separator));
        }
        
        // Reading images
        int imgCount = Util.readInt(ch);

        int[] imgSizes = new int[imgCount];
        List<List<Tag>> imgTags = new ArrayList<>(imgCount);
        String[] imgTitles = new String[imgCount];
        
        for (int i = 0; i < imgCount; i++) {
            // Size
            imgSizes[i] = Util.readInt(ch);
            
            // Tags
            int imgTagCount = Util.readInt(ch);
            
            List<Tag> imgTagList = new ArrayList<>(imgTagCount);
            imgTags.add(imgTagList);
                
            for (int j = 0; j < imgTagCount; j++) {
                imgTagList.add(tags[Util.readInt(ch)]);
            }
            
            // Title
            imgTitles[i] = Util.readStr(f, separator);
        }
        
        // Creating images
        long offset = ch.position();
        
        for (int i = 0; i < imgCount; i++) {
            EXImage img = new StreamImage(new ImageStream(ch, offset, imgSizes[i]), res, imgTitles[i], imgSizes[i], imgTags.get(i));
            res.addImage(img);
            
            offset += imgSizes[i];
        }
        
        return res;
    }
    
    public static void write(Collection col, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        FileChannel ch = out.getChannel();
        
        out.write(signature);
        Util.writeStr(out, col.title, separator);
        
        java.util.Collection<EXImage> imgs = Arrays.stream(col.getImages()).collect(Collectors.toList());
        List<Tag> tags = Tag.idMap.values().stream().filter((t) -> imgs.stream().anyMatch((i) -> i.tags.contains(t))).collect(Collectors.toList());
        Map<Tag, Integer> mtags = new HashMap<>();
        for (int i = 0; i < tags.size(); i++) {
            mtags.put(tags.get(i), i);
        } 
        
        Util.writeInt(ch, tags.size());
        for (Tag tag : tags) {
            Util.writeStr(out, tag.title, separator);
        }
        
        Util.writeInt(ch, imgs.size());
        for (EXImage img : imgs) {
            Util.writeInt(ch, img.getImageSize());
            
            List<Tag> itags = img.tags;
            Util.writeInt(ch, itags.size());
            
            for (Tag tag : itags) {
                Util.writeInt(ch, mtags.get(tag));
            }
            
            Util.writeStr(out, img.title, separator);
        }
        
        for (EXImage img : imgs) {
            img.writeImage(out);
        }
        
        out.close();
    }
    
    private static boolean checkSignature(InputStream s) throws IOException {
        for (int i = 0; i < signature.length; i++) {
            if (s.read() != signature[i]) {
                return false;
            }
        }
        
        return true;
    }
}
