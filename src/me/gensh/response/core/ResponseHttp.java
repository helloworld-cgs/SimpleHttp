package me.gensh.response.core;


import me.gensh.request.RequestHeader;
import me.gensh.request.RequestType;
import me.gensh.response.controllers.Errors;
import me.gensh.response.core.session.HttpSession;
import me.gensh.response.error.NotFoundError;
import me.gensh.tools.StringTools;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by cgs on 2015/12/31.
 */
public class ResponseHttp {
    String requestUrl;
    String BasePath = Config.BasePath;
    RequestHeader header;
    final RequestType requestType;

    public ResponseHttp(RequestHeader rh) {
        header = rh;
        requestUrl = rh.getRequestLineFirst().getRequestUri();
        requestType = rh.getRequestLineFirst().getRequestType();
    }

    public void startResponse(OutputStream outputStream) {
        if (requestType != RequestType.MEDIA) {
            BuiltTextResponse(outputStream);
        }else if (!requestUrl.startsWith(Config.AssetsFileStart)) {
            //medias generated dynamically
            generateMedia(outputStream);
        } else {  //medias  in assets or
            BuiltMediaResponse(outputStream);
        }
    }

    private void BuiltMediaResponse(OutputStream os) {
        File file = new File(BasePath + requestUrl);
        if (!file.exists()) {
            new NotFoundError(os);
            return;
        }
        try {
            long lastModify = file.lastModified();
            if (CheckModify(os, lastModify)) {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                byte byt[] = new byte[2048];
                int length;
                os.write(("HTTP/1.1 200 OK\r\nLast-Modified: " + StringTools.formatModify(lastModify) + "\r\n\r\n").getBytes());
                while ((length = bis.read(byt)) != -1) {
                    os.write(byt, 0, length);
                }
                bis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("send finished\t" + requestUrl);
    }

    private void generateMedia(OutputStream os) {
        Router router = new Router(requestUrl);
        HttpSession session = new HttpSession(header.getHeaderValueByKey(HttpSession.Cookie));
        try {
            Class c = Class.forName(Config.ControllerConfig.ControllerPackage + router.controller);
            Constructor constructor = c.getDeclaredConstructor(OutputStream.class, RequestHeader.class, HttpSession.class);
            Object obj = constructor.newInstance(os, header, session);
            Method method = c.getDeclaredMethod(router.action + Config.ControllerConfig.Media);
            method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
            new NotFoundError(os);
        }
    }

    final byte[] newline = {'\r', '\n'};

    private void BuiltTextResponse(OutputStream os) {
        if (requestType == RequestType.HTML) {
            RenderHtml(os);
            return;
        }

        File file = new File(BasePath + requestUrl);
        if (!file.exists()) {
            new NotFoundError(os);
            return;
        }
        try {
            long lastModify = file.lastModified();
            if (CheckModify(os, lastModify)) {
                InputStreamReader is_r = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader br_r = new BufferedReader(is_r);
                String line;
                os.write(("HTTP/1.1 200 OK\r\nLast-Modified: " + StringTools.formatModify(lastModify) + "\r\n\r\n").getBytes());
                while ((line = br_r.readLine()) != null) {
                    os.write(line.getBytes("UTF-8"));
                    os.write(newline);
                }
                br_r.close();
                is_r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("send finished\t" + requestUrl);
    }

    private void RenderHtml(OutputStream os) {
        Router router = new Router(requestUrl);
        HttpSession session = new HttpSession(header.getHeaderValueByKey(HttpSession.Cookie));
        try {
            Class c = Class.forName(Config.ControllerConfig.ControllerPackage + router.controller);
            Constructor constructor = c.getDeclaredConstructor(OutputStream.class, RequestHeader.class, HttpSession.class);
            Object obj = constructor.newInstance(os, header, session);
            Method method = c.getDeclaredMethod(router.action + Config.ControllerConfig.Action);
            method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
            Errors error404 = new Errors(os, header, session);
            error404.notFoundAction(requestUrl, true);
        }
    }

    private boolean CheckModify(OutputStream os, long lastModify) {
        if (!StringTools.CheckModify(header.getHeaderValueByKey("If-Modified-Since"), lastModify)) {
            try {
                os.write(("HTTP/1.1 304 Not Modified\r\n\r\n").getBytes());
                return false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}