/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core;

/**
 *
 * @author breezy
 */
public class CollectionImportRecord {
    
    public final String importerClassName;
    public final String collectionDescriptor;
    public String title;

    public CollectionImportRecord(String className, String desc) {
        this.importerClassName = className;
        this.collectionDescriptor = desc;
    }
    
    public CollectionImportRecord(String className, String desc, String title) {
        this.importerClassName = className;
        this.collectionDescriptor = desc;
        this.title = title;
    }

    @Override
    public String toString() {
        return String.format("%s\n%s\n%s", title, importerClassName, collectionDescriptor);
    }
    
    
    
}
