package me.gensh.core.response;

import me.gensh.core.request.RequestHeader;
import me.gensh.core.request.data.GetData;
import me.gensh.core.request.data.PostData;
import me.gensh.core.response.render.DefaultHtmlRenderInstance;
import me.gensh.core.response.render.HtmlRender;
import me.gensh.core.response.session.HttpSession;
import me.gensh.core.utils.json.JSONObject;

import java.io.*;

/**
 * Created by 储根深 on 2016/2/12.
 */
public class Controller {
    private GetData data_get = null;
    public BufferedOutputStream bos;
    public ResponseHeader responseHead;
    public RequestHeader requestHeader;
    public HttpSession session;

    public Controller(OutputStream os, RequestHeader header, HttpSession session) {
        bos = new BufferedOutputStream(os);
        this.responseHead = new ResponseHeader();
        this.session = session;
        this.requestHeader = header;
        responseHead.setCookie(session);
    }

    public void redirect(String url) {
        responseHead.setState(ResponseHeader.Redirect);
        responseHead.setHeadValue("Location", url);
        responseHead.setHeadValue("Content-Length", "0");
        responseHead.Out(bos);
    }

    public void forbidden() {
        responseHead.setState(ResponseHeader.FORBIDDEN);
        responseHead.Out(bos);
    }

    public void notFound() {
        responseHead.setState(ResponseHeader.NOT_FOUND);
        responseHead.Out(bos);
    }

    public void badRequest() {
        responseHead.setState(ResponseHeader.Bad_Request);
        responseHead.Out(bos);
    }

    public GetData getParams() {
        if (data_get == null) {
            data_get = new GetData(requestHeader.getRequestLineFirst().requestTail);
        }
        return data_get;
    }

    public boolean isPost() {
        return requestHeader.getRequestLineFirst().getMethod() == RequestHeader.RequestLineFirst.POST;
    }

    public PostData getPostData() {
        return requestHeader.getPostData();
    }

    /**
     * just rend a string to browser
     */
    public void render(String i) {
        responseHead.Out(bos);
        try {
            bos.write((i).getBytes());
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void render(String template, Object data) {
        DefaultHtmlRenderInstance html = new DefaultHtmlRenderInstance(template, data, bos);
        html.render();
    }

    public void render(String template, HtmlRender htmlRender, JSONObject data) {
        htmlRender.setTemplate(template);
        htmlRender.setData(data);
        htmlRender.bindOutputStream(bos);
        htmlRender.render();
    }

    public void renderJSON(String json) {
        responseHead.setHeadValue(ResponseHeader.Content_Type, "application/json; charset=utf-8");
        responseHead.setHeadValue(ResponseHeader.Content_Length, "" + json.length());
        responseHead.Out(bos);
        try {
            bos.write((json).getBytes());
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void outFile(String path) {
        byte[] b = new byte[1024];
        responseHead.Out(bos); //todo add file length
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
            while ((bis.read(b)) != -1) {
                bos.write(b);
            }
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}