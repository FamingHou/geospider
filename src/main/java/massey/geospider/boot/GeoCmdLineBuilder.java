/**
 * 
 */
package massey.geospider.boot;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import massey.geospider.global.GeoConstants;

/**
 * This class is responsible for defining options, parsing arguments and
 * creating GeoCmdLine object for later usage.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class GeoCmdLineBuilder implements GeoConstants {

    private static final Logger log = Logger.getLogger(GeoCmdLineBuilder.class);

    /**
     * 
     */
    private GeoCmdLineBuilder() {
    }

    /**
     * Defines options of the command line interface
     * 
     * @return
     */
    private static Options buildOptions() {

        Option keyword = Option.builder("k").required(true).hasArg(true).argName(KEYWORD_OPTION).longOpt(KEYWORD_OPTION)
                .desc("The keyword for searching on social media platforms").build();

        Option facebook = Option.builder("f").required(false).hasArg(false).longOpt(FACEBOOK_OPTION)
                .desc("Collecting geo places from Facebook").build();

        Option twitter = Option.builder("t").required(false).hasArg(false).longOpt(TWITTER_OPTION)
                .desc("Collecting geo places from Twitter").build();

        Option reddit = Option.builder("r").required(false).hasArg(false).longOpt(REDDIT_OPTION)
                .desc("Collecting geo places from Reddit").build();

        Option instagram = Option.builder("i").required(false).hasArg(false).longOpt(INSTAGRAM_OPTION)
                .desc("Collecting geo places from Instagram").build();

        Option youtube = Option.builder("y").required(false).hasArg(false).longOpt(YOUTUBE_OPTION)
                .desc("Collecting geo places from Youtube").build();

        Option flickr = Option.builder("l").required(false).hasArg(false).longOpt(FLICKR_OPTION)
                .desc("Collecting geo places from Flickr").build();
        
        Option isConcurrent = Option.builder("c").required(false).hasArg(false).longOpt(ISCONCURRENT_OPTION)
                .desc("Jobs will be done concurrently.").build();

        //GS-1001-1
        Option isOnlyEnglish = Option.builder("e").required(false).hasArg(false).longOpt(ISONLYENGLISH_OPTION)
                .desc("Messages are written in only English.").build();

        final Options options = new Options();
        options.addOption(keyword);
        options.addOption(facebook);
        options.addOption(twitter);
        options.addOption(reddit);
        options.addOption(instagram);
        options.addOption(youtube);
        options.addOption(flickr);
        options.addOption(isConcurrent);
        //GS-1001-1
        options.addOption(isOnlyEnglish);
        return options;
    }

    /**
     * Returns a GeoCmdLine object which contains argument values user typed
     * 
     * @param options
     * @param args
     * @return GeoCmdLine which contains a reference of CommandLine object.
     *         Returns null when a ParseException is thrown.
     */
    private static GeoCmdLine buildCommandLine(final Options options, final String[] args) {

        final CommandLineParser cliParser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            commandLine = cliParser.parse(options, args);
            return new GeoCmdLine(commandLine);

        } catch (ParseException parseException) {
            log.error(parseException.getMessage());
        }
        return null;
    }

    /**
     * Checks if geoCmdLine is valid.
     * 
     * @param geoCmdLine
     *            a GeoCommandLine object
     * @return true if geoCmdLine is valid, false otherwise
     */
    private static boolean validateArguments(final GeoCmdLine geoCmdLine) {
        if (geoCmdLine != null) {
            boolean f = geoCmdLine.isFacebookOption();
            boolean t = geoCmdLine.isTwitterOption();
            boolean r = geoCmdLine.isRedditOption();
            boolean i = geoCmdLine.isInstagramOption();
            boolean y = geoCmdLine.isYoutubeOption();
            boolean l = geoCmdLine.isFlickrOption();

            boolean result = f | t | r | i | y | l;
            // it is valid only when at least one social media was selected
            if (result)
                return true;
        }

        return false;
    }

    /**
     * 
     * @param options
     */
    private static void printUsage(final Options options) {
        HelpFormatter formatter = new HelpFormatter();
        final String header = "\nPlease choose at least one social media:\n\n";
        final String footer = "\nEXAMPLES\n"
                + "    The following command shows how to collect messages written in only English on Facebook and "
                + "Twitter filtered by the keyword \"Massey University\" concurrently.\n"
                + "        geospider -e -c -k \"Massey University\" -ft\n"
                + "\nPlease report issues to faming.hou@gmail.com\n\n";
        formatter.printHelp("geospider", header, options, footer, true);
    }

    /**
     * Creates a GeoCmdLine object
     * 
     * @param args
     * @return GeoCmdLine object, null when ParseException occurs or social
     *         media is not chosen.
     */
    public static GeoCmdLine build(String[] args) {

        Options options = buildOptions();

        GeoCmdLine geoCmdLine = buildCommandLine(options, args);

        boolean isValid = validateArguments(geoCmdLine);
        if (isValid) {
            log.info("Facebook is " + geoCmdLine.isFacebookOption());
            log.info("Twitter is " + geoCmdLine.isTwitterOption());
            log.info("Reddit is " + geoCmdLine.isRedditOption());
            log.info("Instagram is " + geoCmdLine.isInstagramOption());
            log.info("Youtube is " + geoCmdLine.isYoutubeOption());
            log.info("Flickr is " + geoCmdLine.isFlickrOption());
            log.info("isConcurrent is " + geoCmdLine.isConcurrentOption());
            //GS-1001-1
            log.info("isOnlyEnglish is " +geoCmdLine.isOnlyEnglish());
            return geoCmdLine;
        } else {
            printUsage(options);
            return null;
        }

    }

}
