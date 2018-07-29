/**
 * 
 */
package massey.geospider.boot;

import org.apache.commons.cli.CommandLine;

import massey.geospider.global.GeoConstants;

/**
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class GeoCmdLine implements GeoConstants {

    private CommandLine commandLine;

    /**
     * 
     */
    public GeoCmdLine(CommandLine commandLine) {
        this.commandLine = commandLine;

    }

    /**
     * Query to see if an option has been set.
     * 
     * @param opt
     *            short/long name of the option
     * @return true if set, false if not
     */
    private boolean hasOption(String opt) {
        if (commandLine != null)
            return commandLine.hasOption(opt);
        return false;
    }

    /**
     * 
     * @return
     */
    public boolean isFacebookOption() {
        return hasOption(FACEBOOK_OPTION);
    }

    public boolean isTwitterOption() {
        return hasOption(TWITTER_OPTION);
    }

    public boolean isRedditOption() {
        return hasOption(REDDIT_OPTION);
    }

    public boolean isInstagramOption() {
        return hasOption(INSTAGRAM_OPTION);
    }

    public boolean isYoutubeOption() {
        return hasOption(YOUTUBE_OPTION);
    }

    public boolean isFlickrOption() {
        return hasOption(FLICKR_OPTION);
    }

    public boolean isConcurrentOption() {
        return hasOption(ISCONCURRENT_OPTION);
    }

    /**
     * GS-1001-1
     * @return
     */
    public boolean isOnlyEnglish() {
        return hasOption(ISONLYENGLISH_OPTION);
    }
    
    /**
     * 
     * @return the value of keyword option
     */
    public String getKeywordOptionValue() {
        return commandLine.getOptionValue(KEYWORD_OPTION);
    }

}
