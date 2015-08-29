/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author icefairy64
 */
public class Tag {
    public int id;
    public String title;
    
    protected Tag(int id, String title) {
        this.id = id;
        this.title = title.toLowerCase();
    }
    
    public static Tag create(String title) {
        Tag tag = new Tag(maxId++, title);
        
        idMap.put(tag.id, tag);
        tagMap.put(tag.title, tag);
        
        return tag;
    }
    
    public static Map<Integer, Tag> idMap = new HashMap<>();
    public static Map<String, Tag> tagMap = new HashMap<>();
    public static int maxId = 0;
}
