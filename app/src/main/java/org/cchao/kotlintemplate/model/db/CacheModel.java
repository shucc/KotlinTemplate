package org.cchao.kotlintemplate.model.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author cchen6
 * @Date on 2019/7/17
 * @Description
 */
@Entity
public class CacheModel {

    @Id
    private String key;

    private String content;

    private long createDate = System.currentTimeMillis();

    @Generated(hash = 835591749)
    public CacheModel(String key, String content, long createDate) {
        this.key = key;
        this.content = content;
        this.createDate = createDate;
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

    public long getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
}
