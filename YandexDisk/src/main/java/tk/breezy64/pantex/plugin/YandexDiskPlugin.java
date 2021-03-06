/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.plugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.json.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;
import ro.fortsoft.pf4j.Extension;
import ro.fortsoft.pf4j.Plugin;
import ro.fortsoft.pf4j.PluginWrapper;
import tk.breezy64.pantex.core.Collection;
import tk.breezy64.pantex.core.CollectionImportRecord;
import tk.breezy64.pantex.core.ConfigSection;
import tk.breezy64.pantex.core.EXImage;
import tk.breezy64.pantex.core.ExportException;
import tk.breezy64.pantex.core.Exporter;
import tk.breezy64.pantex.core.ImportException;
import tk.breezy64.pantex.core.Importer;
import tk.breezy64.pantex.core.JSONPack;
import tk.breezy64.pantex.core.Static;
import tk.breezy64.pantex.core.Util;
import tk.breezy64.pantex.core.YandexDiskImportConfiguration;
import tk.breezy64.pantex.core.YandexImage;
import tk.breezy64.pantex.core.auth.AuthException;
import tk.breezy64.pantex.core.auth.Authorizer;
import tk.breezy64.pantex.core.auth.Token;
import tk.breezy64.pantex.gui.auth.WebWindowController;
import tk.breezy64.pantex.core.auth.YandexToken;
import tk.breezy64.pantex.gui.FXStatic;
import tk.breezy64.pantex.gui.MainController;

/**
 *
 * @author icefairy64
 */
public class YandexDiskPlugin extends Plugin {

    protected static YandexAuthorizer authorizer = null;
    
    public YandexDiskPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     *
     * @author icefairy64
     */
    @Extension
    public static class YandexDiskLoader implements Importer, Exporter {

        private static int collectionLimit = 10000;
        private static String downloadUrl = "https://cloud-api.yandex.net/v1/disk/resources/download?path=%s&token=";
        
        private Optional<Consumer<Collection>> importHandler = Optional.empty();
        private Optional<Consumer<Collection>> exportHandler = Optional.empty();
        private Optional<BiConsumer<Integer, Integer>> importProgressHandler = Optional.empty();
        private Optional<BiConsumer<Integer, Integer>> exportProgressHandler = Optional.empty();

        @Override
        public String getTitle() {
            return "Yandex.Disk";
        }

        public static String getFileUrl(String path, YandexToken token) throws IOException {
            String content = Util.fetchHttpContent(String.format(downloadUrl, path), new String[] { "Authorization", "OAuth " + token.getToken() });
            JsonObject root = new JsonParser().parse(content)
                    .getAsJsonObject();
            try {
                return root.get("href").getAsString();
            }
            catch (NullPointerException e) {
                System.out.println(content);
                throw new RuntimeException("No URL received", e);
            }
        }
        
        private static InputStream getFileStream(String path, YandexToken token) throws IOException {
            return Util.fetchHttpStream(getFileUrl(path, token), new String[] { "Authorization", "OAuth " + token.getToken() });
        }

        @Override
        public Object createMinimalImportConfiguration() {
            YandexAuthorizer auth = getAuthorizer();
            try {
                YandexToken token = auth.getOrCreateToken();
                RestClient client = new RestClient(new Credentials(token.getUid(), token.getToken()));
                return new YandexDiskImportConfiguration(client, token);
            } 
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object createImportConfiguration() {
            YandexDiskImportConfiguration conf = (YandexDiskImportConfiguration)createMinimalImportConfiguration();
            
            try {
                // Getting list of directories
                Resource res = conf.client.getResources(new ResourcesArgs.Builder().setLimit(collectionLimit).setPath("app:/").build());
                List<String> dirs = res.getResourceList().getItems()
                        .stream()
                        .filter((x) -> x.isDir())
                        .map((x) -> x.getName())
                        .collect(Collectors.toList());

                // Choosing directory
                ChoiceDialog<String> dialog = new ChoiceDialog<>(dirs.get(0), dirs);
                dialog.getDialogPane().getStylesheets().add(MainController.class.getResource("/src/main/resources/css/default.css").toString());
                dialog.getDialogPane().getStyleClass().add("dialog");
                Optional<String> r = dialog.showAndWait();
                conf.path = r.orElse("");
            }
            catch (Exception e) {
                FXStatic.handleException(e);
            }
            
            return conf;
        }
        
        @Override
        public void load(Collection col, Object conf) throws IOException, ImportException {
            YandexDiskImportConfiguration c = (YandexDiskImportConfiguration)conf;
            load(col, new CollectionImportRecord(getClass().getName(), c.path), conf);
        }

        @Override
        public void load(Collection col, CollectionImportRecord ir, Object conf) throws IOException, ImportException {
            YandexDiskImportConfiguration c = (YandexDiskImportConfiguration) conf;
            if (!ir.collectionDescriptor.isEmpty()) {
                ir.title = ir.collectionDescriptor;
                col.title = ir.collectionDescriptor;
                col.importRecord = ir;
                String rpath = URLEncoder.encode(ir.collectionDescriptor);
                String path = "app:/" + rpath + "/collection.json";
                JSONPack.load(col, new InputStreamReader(getFileStream(path, c.token)),
                        (title) -> {
                            EXImage img = new YandexImage("app:/" + rpath + "/" + title, c.token, col, title, null, c.client);
                            img.title = title;
                            img.imported = true;
                            return img;
                        }, importProgressHandler.orElse(null));
                
                importHandler.ifPresent(x -> x.accept(col));
            }
        }
        
        private static List<Resource> getResources(RestClient client, String path) throws IOException, ServerIOException {
            return client.getResources(new ResourcesArgs.Builder().setPath(path).setLimit(collectionLimit).build())
                    .getResourceList().getItems();
        }

        @Override
        public void export(Collection col) throws IOException, ExportException {
            Platform.runLater(() -> {
                YandexAuthorizer auth = getAuthorizer();
                try {
                    YandexToken token = auth.getOrCreateToken();
                    if (token != null) {
                        FXStatic.executor.submit(() -> {
                            try {
                                RestClient client = new RestClient(new Credentials(token.getUid(), token.getToken()));
                                String path = "app:/" + col.title;
                                if (!getResources(client, "app:/").stream().anyMatch((x) -> x.getName().equals(col.title))) {
                                    client.makeFolder(path);
                                }
                                
                                /*List<String> ignore = getResources(client, path)
                                        .stream().map((x) -> x.getName())
                                        .collect(Collectors.toList());*/
                                
                                /*List<String> thumbIgnore = getResources(client, path + "/thumbs")
                                        .stream().map((x) -> x.getName())
                                        .collect(Collectors.toList());*/
                                
                                File j = File.createTempFile("PantEX", ".json");
                                j.deleteOnExit();
                                JSONPack.write(col, new FileWriter(j), (x) -> {
                                    try {
                                        try {
                                            if (!x.imported /*!ignore.contains(x.title)*/) {
                                                Link link = client.getUploadLink(path + "/" + x.title, false);
                                                File f = File.createTempFile("PantEX", "_" + x.title);
                                                f.deleteOnExit();
                                                FileOutputStream w = new FileOutputStream(f);
                                                x.writeImage(w);
                                                w.close();
                                                client.uploadFile(link, true, f, null);
                                            }
                                            
                                            /*if (!thumbIgnore.contains(x.title)) {
                                                Link link = client.getUploadLink(path + "/thumbs/" + x.title + "." + EXImage.THUMB_FORMAT, false);
                                                File f = File.createTempFile("PantEX", "_t" + x.title);
                                                f.deleteOnExit();
                                                FileOutputStream w = new FileOutputStream(f);
                                                x.getThumb().writeImage(w);
                                                w.close();
                                                client.uploadFile(link, true, f, null);
                                            }*/
                                        }
                                        catch (ServerException e) {
                                        }
                                    }
                                    catch (Exception e) {
                                        FXStatic.handleException(e);
                                    }
                                }, exportProgressHandler.orElse(null));
                                client.uploadFile(client.getUploadLink(path + "/collection.json", true), true, j, null);
                                j.delete();
                            }
                            catch (Exception e) {
                                FXStatic.handleException(e);
                            }
                            exportHandler.ifPresent((x) -> x.accept(col));
                        });
                    } else {
                        throw new ExportException("Failed to request a token");
                    }
                } catch (Exception e) {
                    FXStatic.handleException(e);
                }
            });
        }

        @Override
        public Importer afterImport(Consumer<Collection> handler) {
            importHandler = Optional.of(handler);
            return this;
        }

        @Override
        public Exporter afterExport(Consumer<Collection> handler) {
            exportHandler = Optional.of(handler);
            return this;
        }

        @Override
        public Importer onImportProgress(BiConsumer<Integer, Integer> handler) {
            importProgressHandler = handler == null ? Optional.empty() : Optional.of(handler);
            return this;
        }

        @Override
        public Exporter onExportProgress(BiConsumer<Integer, Integer> handler) {
            exportProgressHandler = handler == null ? Optional.empty() : Optional.of(handler);
            return this;
        }

    }

    /**
     *
     * @author icefairy64
     */
    @Extension
    public static class YandexAuthorizer extends Authorizer<YandexToken> {

        @Override
        public String getTitle() {
            return "Yandex.Disk";
        }

        @Override
        public YandexToken performAuth() throws AuthException {
            try {
                Stage st = null; 
                Scene s = new Scene(FXMLLoader.load(WebWindowController.class.getResource("/src/main/resources/fxml/WebWindow.fxml")));
                s.setUserData("https://oauth.yandex.ru/authorize?response_type=token&client_id=99c2514b20a245fea2e011a2b0d40cfa");
                st = new Stage();
                st.setScene(s);
                st.showAndWait();
                Optional<String> res = (Optional<String>) s.getUserData();
                if (res != null && res.isPresent()) {
                    Matcher t = WebWindowController.tokenPattern.matcher(res.get());
                    Matcher u = Pattern.compile("uid=([0-9]+)").matcher(res.get());
                    t.find();
                    return u.find() ? new YandexToken(t.group(1)) : new YandexToken(t.group(1), u.group(1));
                }
                throw new AuthException("An error occured while authorizing");
            } catch (IOException e) {
                throw new AuthException(e);
            }
        }

        @Override
        public boolean testToken(YandexToken token) {
            try {
                RestClient cl = new RestClient(new Credentials(token.getUid(), token.getToken()));
                cl.getApiVersion();
            }
            catch (Exception e) {
                return false;
            }
            return true;
        }
    }
    
    @Extension
    public static class YandexConfig extends ConfigSection {

        @Override
        public String getTitle() {
            return "yandex";
        }
        
        private YandexToken token;

        public YandexToken getToken() {
            return token;
        }
        
        @Override
        public void load(Map<String, Object> root) {
            if (root.containsKey("token")) {
                Map<String, Object> t = (Map<String, Object>)root.get("token");
                token = new YandexToken((String)t.get("value"), (String)t.get("uid"));
                getAuthorizer().testAndSetToken(token);
            }
        }

        @Override
        public Map<String, Object> save() {
            Map<String, Object> root = new HashMap<>();
            Authorizer a = getAuthorizer();
            if (a.getToken().isPresent()) {
                Map<String, Object> t = new HashMap<>();
                root.put("token", t);
                t.put("value", getAuthorizer().getToken().get().getToken());
                t.put("uid", getAuthorizer().getToken().get().getUid());
            }
            return root;
        }
        
    }
    
    private static YandexAuthorizer getAuthorizer() {
        if (authorizer == null) {
            for (Authorizer<? extends Token> a : Static.pluginManager.getExtensions(Authorizer.class)) {
                if (a instanceof YandexAuthorizer) {
                    authorizer = (YandexAuthorizer)a;
                    break;
                }
            }
        }
        return authorizer;
    }
    
}
