/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

import com.yandex.disk.rest.RestClient;
import tk.breezy64.pantex.core.auth.YandexToken;

/**
 *
 * @author breezy
 */
public class YandexDiskImportConfiguration {
    
    public String path;
    public final RestClient client;
    public final YandexToken token;

    public YandexDiskImportConfiguration(RestClient client, YandexToken token) {
        this.client = client;
        this.token = token;
    }
    
    public YandexDiskImportConfiguration(String path, RestClient client, YandexToken token) {
        this.path = path;
        this.client = client;
        this.token = token;
    }
    
}
