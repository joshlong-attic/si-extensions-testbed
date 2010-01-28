package org.indrio.pingfm.beans;

import java.io.Serializable;
import java.util.Set;

public class Message implements Serializable {
    private static final long serialVersionUID = 5852290431653309904L;

    private String id_;
    private String method_;

    private String dateRfc_;
    private String dateUnix_;

    private String contentTitle_;
    private String contentBody_;
    private Set<Service> services_;

    public Message(String _id, String _method) {
        this(_id, _method, null, null);
    }

    public Message(String _id, String _method,
                   String _contentTitle, String _contentBody) {
        super();
        this.id_ = _id;
        this.method_ = _method;
        this.contentTitle_ = _contentTitle;
        this.contentBody_ = _contentBody;
    }

    public String getId() {
        return id_;
    }

    public String getMethod() {
        return method_;
    }

    public String getDateRfc() {
        return dateRfc_;
    }

    public String getDateUnix() {
        return dateUnix_;
    }

    public String getContentTitle() {
        return contentTitle_;
    }

    public String getContentBody() {
        return contentBody_;
    }

    public Set<Service> getServices() {
        return services_;
    }

    public void setId(String _id) {
        this.id_ = _id;
    }

    public void setMethod(String _method) {
        this.method_ = _method;
    }

    public void setDateRfc(String _dateRfc) {
        this.dateRfc_ = _dateRfc;
    }

    public void setDateUnix(String _dateUnix) {
        this.dateUnix_ = _dateUnix;
    }

    public void setContentTitle(String _contentTitle) {
        this.contentTitle_ = _contentTitle;
    }

    public void setContentBody(String _contentBody) {
        this.contentBody_ = _contentBody;
    }

    public void setServices(Set<Service> _services) {
        this.services_ = _services;
    }

}
