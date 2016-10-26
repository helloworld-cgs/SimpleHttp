package me.gensh.response.core;

/**
 * Created by gensh on 2016/2/11.
 */
public class Config {
    public final static String BasePath = "F:/HttpFiles";
    public final static String TempPath = "F:/HttpFiles/temp/";
    public final static String AssetsFilePrefix = "/public";

    public class Router {
        final static String defaultController = "Index";
        final static String defaultAction = "index";
    }

    public class ControllerConfig {
        final static String ControllerPackage = "holo.com.response.controllers.";
        final static String Action = "Action"; //can't change!
        final static String Media = "Media"; //can't change!
    }

    public class View {
        public final static String VIEW = "F:/HttpFiles/core/views/";
        public final static String LAYOUT = "F:/HttpFiles/core/views/layout/";
    }


}
