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

    public static void main(String[] args) {
        log.info("========================================");
        log.info("|| Geospider is going to work...      ||");
        log.info("========================================");

        GeoCmdLine geoCmdLine = GeoCmdLineBuilder.build(args);
        if (geoCmdLine != null)
            GeoController.getSingleton().dispatch(geoCmdLine);

        log.info("================");
        log.info("|| All done!  ||");
        log.info("================");
    }

}
