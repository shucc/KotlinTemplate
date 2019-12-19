package org.cchao.http.db;

import androidx.annotation.Keep;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
@Keep
@Entity
public class CacheModel {

    @Id
    private String key;

    private String content;

    private int code;

    private String msg;

    private Long time;

    @Generated(hash = 2046987203)
    public CacheModel(String key, String content, int code, String msg, Long time) {
        this.key = key;
        this.content = content;
        this.code = code;
        this.msg = msg;
        this.time = time;
    }

    @Generated(hash = 666297882)
    public CacheModel() {
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
