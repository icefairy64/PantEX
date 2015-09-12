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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONWriter;

/**
 *
 * @author icefairy64
 */
public class ConfigManager {
    
    public static void load(File file) throws IOException {
        JsonReader r = new JsonReader(new StringReader(Util.readStream(new FileInputStream(file))));
        r.setLenient(true);
        JsonObject json = new JsonParser().parse(r).getAsJsonObject();
        List<ConfigSection> x = Static.pluginManager.getExtensions(ConfigSection.class);
        for (ConfigSection cs : x) {
            if (json.has(cs.getTitle())) {
                cs.load(jsonObjectToMap(json.getAsJsonObject(cs.getTitle())));
            }
        }
    }
    
    public static void save(File file) throws IOException {
        JsonObject json = new JsonObject();
        for (ConfigSection cs : Static.pluginManager.getExtensions(ConfigSection.class)) {
            json.add(cs.getTitle(), mapToJsonObject(cs.save()));
        }
        Writer w = new FileWriter(file);
        w.write(json.toString());
        w.close();
    }
    
    private static Map<String, Object> jsonObjectToMap(JsonObject json) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> el : json.entrySet()) {
            map.put(el.getKey(), jsonElementToObject(el.getValue()));
        }
        return map;
    }
    
    private static JsonObject mapToJsonObject(Map<String, Object> map) {
        JsonObject obj = new JsonObject();
        for (Map.Entry<String, Object> el : map.entrySet()) {
            obj.add(el.getKey(), objectToJsonElement(el.getValue()));
        }
        return obj;
    }
    
    private static JsonElement objectToJsonElement(Object obj) {
        if (obj instanceof Map<?, ?>) {
            return mapToJsonObject((Map<String, Object>)obj);
        } else if (obj instanceof Number) {
            return new JsonPrimitive((Number)obj);
        } else if (obj instanceof String) {
            return new JsonPrimitive((String)obj);
        } else if (obj instanceof Boolean) {
            return new JsonPrimitive((Boolean)obj);
        } else if (obj instanceof List<?>) {
            JsonArray ar = new JsonArray();
            for (Object x : (List<?>)obj) {
                ar.add(objectToJsonElement(x));
            }
            return ar;
        }
        return null;
    }
    
    private static Object jsonElementToObject(JsonElement e) {
        if (e.isJsonObject()) {
            return jsonObjectToMap(e.getAsJsonObject());
        } else if (e.isJsonPrimitive()) {
            JsonPrimitive p = e.getAsJsonPrimitive();
            if (p.isNumber()) {
                return p.getAsInt();
            } else if (p.isString()) {
                return p.getAsString();
            } else if (p.isBoolean()) {
                return p.getAsBoolean();
            }
        } else if (e.isJsonArray()) {
            List<Object> list = new ArrayList<>();
            e.getAsJsonArray().forEach((x) -> list.add(jsonElementToObject(x)));
            return list;
        }
        return null;
    }
    
}
