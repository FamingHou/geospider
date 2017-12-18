package massey.geospider.boot;

import org.apache.log4j.Logger;

import massey.geospider.controller.GeoController;

/**
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class GeoSpiderApp {

    private static final Logger log = Logger.getLogger(GeoSpiderApp.class);

    private static final String[] BANNER = {
    "",
    "/\\\\       ____                  ____            _       _               \\ \\ \\ \\",     
    "(  )     / ___|   ___    ___   / ___|   _ __   (_)   __| |   ___   _ __   \\ \\ \\ \\",
    " /\\\\    | |  _   / _ \\  / _ \\  \\___ \\  | '_ \\  | |  / _` |  / _ \\ | '__|    ) ) ) )",
    "  '     | |_| | |  __/ | (_) |  ___) | | |_) | | | | (_| | |  __/ | |        ) ) ) )",
    "  '      \\____|  \\___|  \\___/  |____/  | .__/  |_|  \\__,_|  \\___| |_|     / / / /",
    "  '                                    |_|                               / / / /",
    ""};
    
    public static void main(String[] args) {

        printBanner();

        GeoCmdLine geoCmdLine = GeoCmdLineBuilder.build(args);
        if (geoCmdLine != null) {
            log.fatal("========================================");
            log.fatal("|| Geospider is going to work...      ||");
            log.fatal("========================================");

            GeoController.getSingleton().dispatch(geoCmdLine);

            log.fatal("================");
            log.fatal("|| All done!  ||");
            log.fatal("================");
        }
    }

    /**
     * Prints the top banner
     */
    public static void printBanner() {
        for (String line : BANNER) {
            System.out.println(line);
        }
    }
    
}
