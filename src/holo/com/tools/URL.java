package holo.com.tools;


import holo.com.response.core.Config;

/**
 * Created by 根深 on 2016/2/19.
 */
public class URL {
    static String Base = "http://"+ Network.HOST + (Network.HttpPort == 80 ? "" : ":" + Network.HttpPort);

    public static String url(String controller, String action) {
        return Base + "/" + controller + "/" + action + ".html";
    }
}