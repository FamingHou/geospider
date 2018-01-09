/**
 * 
 */
package massey.geospider.probe.facebook.concurrent;

import massey.geospider.message.facebook.FacebookMessage;
import massey.geospider.probe.facebook.FacebookRepliesProbe;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookRepliesProbeConcurrent extends FacebookRepliesProbe {

    /**
     * @param fbParent
     */
    public FacebookRepliesProbeConcurrent(FacebookMessage fbParent) {
        super(fbParent);
    }

}
