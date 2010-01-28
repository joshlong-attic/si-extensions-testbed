package org.indrio.pingfm.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Trigger implements Serializable {
    private static final long serialVersionUID = -3034369257697571052L;

    private String id_;
    private String method_;
    private List<Service> services_ = new ArrayList<Service>();

    public Trigger(String _id, String _method) {
        super();
        this.id_ = _id;
        this.method_ = _method;
    }

    public String getId() {
        return id_;
    }

    public String getMethod() {
        return method_;
    }

    public List<Service> getServices() {
        return services_;
    }

    public void setId(String _id) {
        this.id_ = _id;
    }

    public void setMethod(String _method) {
        this.method_ = _method;
    }

    public void setServices(List<Service> _services) {
        this.services_ = _services;
    }

    public void addService(Service _service) {
        this.services_.add(_service);
    }
}
