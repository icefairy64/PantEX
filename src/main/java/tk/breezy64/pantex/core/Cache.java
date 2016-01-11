/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import ro.fortsoft.pf4j.Extension;

/**
 *
 * @author icefairy64
 */
public class Cache {
    
    public int size = 2000;
    public boolean enabled = true;
    
    private Map<Long, String> cacheMap;
    
    public Cache() {
        cacheMap = new LinkedHashMap<>();
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
    }
    
    public Optional<EXImage> find(EXImage proto) {
        String name = cacheMap.get(proto.id);
        return name != null && enabled
                ? Optional.of(new FileImage(new File(cacheDir, name), proto.collection, proto.title, proto.tags)) 
                : Optional.empty();
    }
    
    public EXImage tryFind(EXImage proto) throws IOException {
        Optional<EXImage> img = find(proto);
        if (img.isPresent()) {
            return img.get();
        }
        else {
            store(proto);
            return proto;
        }
    }
    
    public void store(EXImage img) throws IOException {
        if (cacheMap.size() >= size) {
            Long id = cacheMap.keySet().iterator().next();
            File f = new File(cacheDir, cacheMap.get(id));
            f.delete();
            cacheMap.remove(id);
        }
        
        OutputStream o = new FileOutputStream(new File(cacheDir, img.title));
        img.writeImage(o);
        o.close();
        cacheMap.put(img.id, img.title);
    }
    
    // Static
    
    private static Cache instance;
    private final static File cacheDir = new File(System.getProperty("user.dir"), "cache");
    
    public static Cache getInstance() {
        return instance == null ? instance = new Cache() : instance;
    }
    
    @Extension
    public static class CacheConfig extends ConfigSection {

        @Override
        public String getTitle() {
            return "cache";
        }

        @Override
        public void load(Map<String, Object> root) {
            Cache c = Cache.getInstance();
            if (root.containsKey("size")) {
                c.size = (Integer)root.get("size");
            }
            if (root.containsKey("enabled")) {
                c.enabled = (Boolean)root.get("enabled");
            }
        }

        @Override
        public Map<String, Object> save() {
            Cache c = Cache.getInstance();
            Map<String, Object> map = new HashMap<>();
            map.put("size", c.size);
            map.put("enabled", c.enabled);
            return map;
        } 
        
    }
    
}
