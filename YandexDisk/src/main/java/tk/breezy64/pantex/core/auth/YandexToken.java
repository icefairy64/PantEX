/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core.auth;

import tk.breezy64.pantex.core.auth.Token;

/**
 *
 * @author icefairy64
 */
public class YandexToken extends Token {
    
    private String token;
    private String uid;

    public YandexToken(String token) {
        this.token = token;
    }
    
    public YandexToken(String token, String uid) {
        this.token = token;
        this.uid = uid;
    }
    
    public String getToken() {
        return token;
    }

    public String getUid() {
        return uid;
    }

    @Override
    public String save() {
        return token;
    }

    @Override
    public void load(String s) {
        token = s;
    }
    
}
