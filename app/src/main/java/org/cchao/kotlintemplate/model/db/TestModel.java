package org.cchao.kotlintemplate.model.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TestModel {

    @Id
    private int id;

    private String title;

    @Generated(hash = 1358593057)
    public TestModel(int id, String title) {
        this.id = id;
        this.title = title;
    }

    @Generated(hash = 1568142977)
    public TestModel() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
