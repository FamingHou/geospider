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
    public boolean getFacebookOption() {
        return hasOption(FACEBOOK_OPTION);
    }

    public boolean getTwitterOption() {
        return hasOption(TWITTER_OPTION);
    }

    public boolean getRedditOption() {
        return hasOption(REDDIT_OPTION);
    }

    public boolean getInstagramOption() {
        return hasOption(INSTAGRAM_OPTION);
    }

    public boolean getYoutubeOption() {
        return hasOption(YOUTUBE_OPTION);
    }

    public boolean getFlickrOption() {
        return hasOption(FLICKR_OPTION);
    }

    /**
     * 
     * @return the value of keyword option
     */
    public String getKeywordOptionValue() {
        return commandLine.getOptionValue(KEYWORD_OPTION);
    }

}
