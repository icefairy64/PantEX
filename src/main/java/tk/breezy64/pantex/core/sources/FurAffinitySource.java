/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.breezy64.pantex.core.sources;

import java.io.IOException;
import java.util.List;
import tk.breezy64.fur.BrowseFeedIterator;
import tk.breezy64.fur.Client;
import tk.breezy64.fur.Submission;
import tk.breezy64.fur.SubmissionIterator;
import tk.breezy64.pantex.core.EXImage;
import tk.breezy64.pantex.core.FurAffinityImage;
import tk.breezy64.pantex.core.RemoteImage;

/**
 *
 * @author icefairy64
 */
public class FurAffinitySource extends ImageSource {

    private SubmissionIterator iter;
    
    public FurAffinitySource() {
        iter = new BrowseFeedIterator(faClient);
    }
    
    @Override
    protected void load(int page) throws IOException {
        List<Submission> list = iter.next();
        cache = new EXImage[list.size()];
        int i = 0;
        for (Submission s : list) {
            cache[i++] = new FurAffinityImage(s);
        }
    }
    
    @Override
    public String getWindowTitle() {
        return "FurAffinity browser";
    }
    
    // Static members
    
    private static final Client faClient = new Client();
    
}
