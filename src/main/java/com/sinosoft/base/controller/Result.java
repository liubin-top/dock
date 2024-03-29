package com.sinosoft.base.controller;

import java.io.Serializable;

public class Result  implements Serializable {
    public boolean res;
    public String msg;
    public Object data;

    public boolean isRes() {
        return res;
    }

    public void setRes(boolean res) {
        this.res = res;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Result(){

    }

    public static Result success(){
        Result result=new Result();
        result.setRes(true);
        return result;
    }

    public static Result success(Object data){
        Result result=new Result();
        result.setRes(true);
        result.setData(data);
        return result;
    }

    public static Result success(String msg,Object data){
        Result result=new Result();
        result.setRes(true);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static Result fail(){
        Result result=new Result();
        result.setRes(false);
        return result;
    }

    public static Result fail(String msg){
        Result result=new Result();
        result.setRes(false);
        result.setMsg(msg);
        return result;
    }

    public static Result fail(String msg,Object data){
        Result result=new Result();
        result.setRes(false);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

}
