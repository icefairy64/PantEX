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
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 *
 * @author icefairy64
 */
public class EXPack {

    //@Override
    public String getTitle() {
        return "EXPack";
    }
    
    //@Override
    public void load(File file, Collection res, BiConsumer<Integer, Integer> progressHandler) throws IOException, ImportException {
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException(String.format("EXPack file is not found at path %s", file.getPath()));
        }
        
        FileInputStream f = new FileInputStream(file);
        FileChannel ch = f.getChannel();
        
        if (!checkSignature(f)) {
            throw new ImportException("Signature check failed");
        }
        
        // Reading title
        String title = Util.readStr(f, SEPARATOR);
        res.title = title;
        
        // Reading tags
        int tagCount = Util.readInt(ch);
        Tag[] tags = new Tag[tagCount];
        
        for (int i = 0; i < tagCount; i++) {
            tags[i] = Tag.create(Util.readStr(f, SEPARATOR));
        }
        
        // Reading images
        int imgCount = Util.readInt(ch);

        int[] imgSizes = new int[imgCount];
        int[] thumbSizes = new int[imgCount];
        List<List<Tag>> imgTags = new ArrayList<>(imgCount);
        String[] imgTitles = new String[imgCount];
        
        for (int i = 0; i < imgCount; i++) {
            // Size
            imgSizes[i] = Util.readInt(ch);
            thumbSizes[i] = Util.readInt(ch);
            
            // Tags
            int imgTagCount = Util.readInt(ch);
            
            List<Tag> imgTagList = new ArrayList<>(imgTagCount);
            imgTags.add(imgTagList);
                
            for (int j = 0; j < imgTagCount; j++) {
                imgTagList.add(tags[Util.readInt(ch)]);
            }
            
            // Title
            imgTitles[i] = Util.readStr(f, SEPARATOR);
            
            if (progressHandler != null) {
                progressHandler.accept(i + 1, imgCount);
            }
        }
        
        // Creating images
        long offset = ch.position();
        
        for (int i = 0; i < imgCount; i++) {
            EXImage img = new StreamImage(new ImageStream(ch, offset, imgSizes[i]), res, imgTitles[i], imgSizes[i], imgTags.get(i));
            res.addImage(img);
            
            offset += imgSizes[i];
        }
        
        EXImage[] imgs = res.getImages();
        for (int i = 0; i < imgCount; i++) {
            if (thumbSizes[i] > 0)
                imgs[i].thumb = new StreamImage(new ImageStream(ch, offset, thumbSizes[i]), null, null, thumbSizes[i], null);
            offset += thumbSizes[i];
        }
    }
    
    public void write(Collection col, File file, BiConsumer<Integer, Integer> progressHandler) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        FileChannel ch = out.getChannel();
        
        out.write(SIGNATURE);
        Util.writeStr(out, col.title, SEPARATOR);
        
        java.util.Collection<EXImage> imgs = Arrays.stream(col.getImages()).collect(Collectors.toList());
        List<Tag> tags = Tag.idMap.values().stream().filter((t) -> imgs.stream().anyMatch((i) -> i.tags.contains(t))).collect(Collectors.toList());
        Map<Tag, Integer> mtags = new HashMap<>();
        for (int i = 0; i < tags.size(); i++) {
            mtags.put(tags.get(i), i);
        } 
        
        Util.writeInt(ch, tags.size());
        for (Tag tag : tags) {
            Util.writeStr(out, tag.title, SEPARATOR);
        }
        
        Util.writeInt(ch, imgs.size());
        int i = 0;
        for (EXImage img : imgs) {
            Util.writeInt(ch, img.getImageSize());
            EXImage th = img.getThumb();
            Util.writeInt(ch, th == null ? 0 : th.getImageSize());
            
            List<Tag> itags = img.tags;
            Util.writeInt(ch, itags.size());
            
            for (Tag tag : itags) {
                Util.writeInt(ch, mtags.get(tag));
            }
            
            Util.writeStr(out, img.title, SEPARATOR);
            if (progressHandler != null) {
                progressHandler.accept(++i, imgs.size());
            }
        }
        
        for (EXImage img : imgs) {
            img.writeImage(out);
        }
        
        for (EXImage img : imgs) {
            EXImage th = img.getThumb();
            if (th != null)
                th.writeImage(out);
        }
        
        out.close();
    }
    
    // Static
    
    private static final byte[] SIGNATURE = { 0x50, 0x41, 0x4e, 0x54, 0x53, 0x55, 0x45, 0x58 };
    private static final int SEPARATOR = 0x00;
    private final static EXPack INSTANCE = new EXPack();
    
    public static void loadCollection(File file, Collection res) throws IOException, ImportException {
        INSTANCE.load(file, res, null);
    }
    
    public static EXPack getInstance() {
        return INSTANCE;
    }
    
    public static void writeCollection(Collection res, File file) throws IOException {
        INSTANCE.write(res, file, null);
    }
    
    private static boolean checkSignature(InputStream s) throws IOException {
        for (int i = 0; i < SIGNATURE.length; i++) {
            if (s.read() != SIGNATURE[i]) {
                return false;
            }
        }
        
        return true;
    }
}
