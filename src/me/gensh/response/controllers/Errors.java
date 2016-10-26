package me.gensh.response.controllers;

import me.gensh.request.RequestHeader;
import me.gensh.response.core.Controller;
import me.gensh.response.core.ResponseHeader;
import me.gensh.response.core.session.HttpSession;
import me.gensh.tools.json.JSONException;
import me.gensh.tools.json.JSONObject;

import java.io.OutputStream;

/**
 * Created by cgs on 2016/1/1.
 */
public class Errors extends Controller {

    public Errors(OutputStream os, RequestHeader header, HttpSession session) {
        super(os, header, session);
        responseHead.setState(ResponseHeader.NOT_FOUND);
    }

    public void notFoundAction(String url,boolean isHtml) {
        if(!isHtml){
            responseHead.Out(bos);// output 404 state only
            return;
        }
        JSONObject data = new JSONObject();
        try {
            data.put("title", "not found");
            data.put("url", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        render("index/error.html", data);
    }
}