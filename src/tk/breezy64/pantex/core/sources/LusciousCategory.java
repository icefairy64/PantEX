/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core.sources;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author icefairy64
 */
public class LusciousCategory {
    public final String title;
    public final String name;
    
    public LusciousCategory(String name, String title) {
        this.title = title;
        this.name = name;
    }

    @Override
    public String toString() {
        return title;
    }
    
    public static final List<LusciousCategory> list = init();
    
    private static List<LusciousCategory> init() {
        List<LusciousCategory> tmp = new LinkedList<>();
        
        tmp.add(new LusciousCategory("hentai_manga", "Hentai manga"));
        tmp.add(new LusciousCategory("hentai", "Hentai"));
        tmp.add(new LusciousCategory("adult", "Adult"));
        tmp.add(new LusciousCategory("western_manga", "Western manga"));
        tmp.add(new LusciousCategory("furries", "Furries"));
        tmp.add(new LusciousCategory("futanari_manga", "Futanari manga"));
        tmp.add(new LusciousCategory("western_hentai", "Western hentai"));
        tmp.add(new LusciousCategory("furry_comics", "Furry comics"));
        tmp.add(new LusciousCategory("futanari", "Futanari hentai"));
        tmp.add(new LusciousCategory("video_games", "Video games"));
        tmp.add(new LusciousCategory("superhero_manga", "Superhero manga"));
        tmp.add(new LusciousCategory("superheroes", "Superheroes"));
        tmp.add(new LusciousCategory("3d_hentai", "3D hentai"));
        tmp.add(new LusciousCategory("tentacle_hentai", "Tentacles"));
        tmp.add(new LusciousCategory("yuri_manga", "Yuri manga"));
        tmp.add(new LusciousCategory("monster_girls", "Monster girls"));
        tmp.add(new LusciousCategory("incest_manga", "Incest manga"));
        tmp.add(new LusciousCategory("cosplay", "Cosplay"));
        tmp.add(new LusciousCategory("shemales", "Shemales"));
        tmp.add(new LusciousCategory("cumshot", "Cumshot"));
        tmp.add(new LusciousCategory("ethnic_girls", "Ethnic girls"));
        tmp.add(new LusciousCategory("luscious", "Other"));
        tmp.add(new LusciousCategory("bdsm", "BDSM"));
        tmp.add(new LusciousCategory("bbw", "BBW"));
        tmp.add(new LusciousCategory("trap_manga", "Trap manga"));
        tmp.add(new LusciousCategory("my_little_pony_fim", "My Little Pony: FIM"));
        tmp.add(new LusciousCategory("lesbian_pictures", "Lesbians"));
        tmp.add(new LusciousCategory("traps", "Traps"));
        tmp.add(new LusciousCategory("yaoi", "Yaoi"));
        tmp.add(new LusciousCategory("erotica", "Erotica"));
        tmp.add(new LusciousCategory("monster_girl_manga", "Monster girls manga"));
        tmp.add(new LusciousCategory("video_game_manga", "Video game manga"));
        tmp.add(new LusciousCategory("interracial", "Interracial"));
        tmp.add(new LusciousCategory("hentai_movies", "Hentai movies"));
        tmp.add(new LusciousCategory("pregnant", "Pregnant"));
        tmp.add(new LusciousCategory("milfs", "MILFs"));
        tmp.add(new LusciousCategory("cyber_sex", "Cyber sex"));
        tmp.add(new LusciousCategory("roleplaying", "Roleplaying"));
        tmp.add(new LusciousCategory("luscious_artists", "Luscious artists"));
        tmp.add(new LusciousCategory("verified_amateurs", "Selfies"));
        tmp.add(new LusciousCategory("water_sports", "Water sports"));
        tmp.add(new LusciousCategory("interracial_comics", "Interracial comics"));
        tmp.add(new LusciousCategory("celebrity_fakes", "Celebrity fakes"));
        tmp.add(new LusciousCategory("fan_fiction", "Fan fiction"));
        tmp.add(new LusciousCategory("my_little_pony_manga", "My Little Pony manga"));
        tmp.add(new LusciousCategory("muscular_babes", "Myscular babes"));
        tmp.add(new LusciousCategory("foot_fetish", "Foot fetish"));
        tmp.add(new LusciousCategory("men", "Men"));
        
        return tmp;
    }
}
