/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ro.fortsoft.pf4j.Extension;
import tk.breezy64.pantex.core.ConfigSection;

/**
 *
 * @author icefairy64
 */
@Extension
public class SourceBlacklister extends ConfigSection {

    public static List<String> blacklistedCategories = new ArrayList<>();
    
    @Override
    public String getTitle() {
        return "source-blacklist";
    }

    @Override
    public void load(Map<String, Object> root) {
        super.load(root);
        if (root.containsKey("categories")) {
            blacklistedCategories = (List<String>)root.get("categories");
        }
    }

    @Override
    public Map<String, Object> save() {
        Map<String, Object> map = super.save();
        map.put("categories", blacklistedCategories);
        return map;
    }
    
    
    
}
