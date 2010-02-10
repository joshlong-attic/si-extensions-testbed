package org.indrio.pingfm.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Service
        implements Serializable {
    private static final long serialVersionUID = 6661305932969082292L;
    private String id_;
    private String name_;
    private Trigger trigger_;
    private String url_;
    private String icon_;
    private List<String> methods_ = new ArrayList<String>();

    public Service(String _id, String _name) {
        super();
        this.id_ = _id;
        this.name_ = _name;
    }

    public String getId() {
        return id_;
    }

    public String getName() {
        return name_;
    }

    public Trigger getTrigger() {
        return trigger_;
    }

    public String getUrl() {
        return url_;
    }

    public String getIcon() {
        return icon_;
    }

    public List<String> getMethods() {
        return methods_;
    }

    public void setId(String _id) {
        this.id_ = _id;
    }

    public void setName(String _method) {
        this.name_ = _method;
    }

    public void setTrigger(Trigger _trigger) {
        this.trigger_ = _trigger;
    }

    public void setUrl(String _url) {
        this.url_ = _url;
    }

    public void setIcon(String _icon) {
        this.icon_ = _icon;
    }

    public void setMethods(List<String> _methods) {
        this.methods_ = _methods;
    }

    public void addMethod(String _method) {
        this.methods_.add(_method);
    }
}
