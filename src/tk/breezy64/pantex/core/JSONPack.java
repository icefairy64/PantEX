/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author icefairy64
 */
public class JSONPack {
    
    public static void load(Collection col, Reader reader, Function<String, EXImage> loader) {
        JsonParser p = new JsonParser();
        JsonObject root = p.parse(reader).getAsJsonObject();
        
        JsonArray t = root.getAsJsonArray("tags");
        List<Tag> tags = new ArrayList<>();
        t.forEach((x) -> tags.add(Tag.getOrCreate(x.getAsString())));
        
        JsonArray i = root.getAsJsonArray("images");
        i.forEach((x) -> {
            JsonObject obj = x.getAsJsonObject();
            EXImage img = loader.apply(obj.get("title").getAsString());
            JsonArray it = obj.getAsJsonArray("tags");
            it.forEach((y) -> img.tags.add(tags.get(y.getAsInt())));
            col.addImage(img);
        });
    }
    
    public static void write(Collection col, Writer writer, Consumer<EXImage> saver) throws IOException {
        JsonObject root = new JsonObject();
        JsonArray imgsArray = new JsonArray();
        root.add("images", imgsArray);
        JsonArray tagsArray = new JsonArray();
        root.add("tags", tagsArray);
        
        List<Tag> tags = new ArrayList<>();
        EXImage[] imgs = col.getImages();
        
        for (EXImage img : imgs) {
            JsonObject imgRoot = new JsonObject();
            imgsArray.add(imgRoot);
            JsonArray imgTagsArray = new JsonArray();
            imgRoot.add("tags", tagsArray);
            imgRoot.add("title", new JsonPrimitive(img.title));
            
            for (Tag tag : img.tags) {
                if (!tags.contains(tag)) {
                    tags.add(tag);
                    tagsArray.add(new JsonPrimitive(tag.title));
                }
                imgTagsArray.add(new JsonPrimitive(tags.indexOf(tag)));
            }
        }
        
        writer.write(root.toString());
        writer.close();
    }
    
}
