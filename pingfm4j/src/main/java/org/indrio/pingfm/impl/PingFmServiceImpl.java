package org.indrio.pingfm.impl;

import org.indrio.pingfm.PingFmService;
import org.indrio.pingfm.ServerRequest;
import org.indrio.pingfm.ServerResponse;
import org.indrio.pingfm.beans.Message;
import org.indrio.pingfm.beans.Service;
import org.indrio.pingfm.beans.Trigger;
import org.indrio.pingfm.util.SettingProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PingFmServiceImpl implements PingFmService {
    private static final long serialVersionUID = 3153787174172310015L;

    private static final String HOSTNAME = SettingProperties.getLocationHost();
    private final String developerKey_;
    private final String apiKey_;

    private ServerRequest serverRequest = new ServerRequestImpl();

    public PingFmServiceImpl(String _developerKey, String _apiKey) {
        this.developerKey_ = _developerKey;
        this.apiKey_ = _apiKey;
    }

    public boolean validate() {
        String actionPath = "/" + SettingProperties.getApiVersion() + "/" + ACTION_USER_VALIDATE;

        Map<String, String> params = new HashMap<String, String>();
        params.put(ServerRequest.PARAM_API_KEY, this.developerKey_);
        params.put(ServerRequest.PARAM_USER_APP_KEY, this.apiKey_);

        try {
            ServerResponse serverResponse = serverRequest.send(HOSTNAME, actionPath, params);
            return serverResponse.getStatus().equals("OK");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Service> getAllService() {
        String actionPath = "/" + SettingProperties.getApiVersion() + "/" + ACTION_GET_ALL_SERVICE;

        Map<String, String> params = new HashMap<String, String>();
        params.put(ServerRequest.PARAM_API_KEY, this.developerKey_);
        params.put(ServerRequest.PARAM_USER_APP_KEY, this.apiKey_);

        try {
            ServerResponse serverResponse = serverRequest.send(HOSTNAME, actionPath, params);
            return serverResponse.getService();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Message> getUserLatestMessage(String _limit) {
        String actionPath = "/" + SettingProperties.getApiVersion() + "/" + ACTION_USER_GET_MESSAGE;

        Map<String, String> params = new HashMap<String, String>();
        params.put(ServerRequest.PARAM_API_KEY, this.developerKey_);
        params.put(ServerRequest.PARAM_USER_APP_KEY, this.apiKey_);
        params.put(ServerRequest.PARAM_LIMIT, _limit);

        try {
            ServerResponse serverResponse = serverRequest.send(HOSTNAME, actionPath, params);
            return serverResponse.getMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Service> getUserService() {
        String actionPath = "/" + SettingProperties.getApiVersion() + "/" + ACTION_USER_SERVICE;

        Map<String, String> params = new HashMap<String, String>();
        params.put(ServerRequest.PARAM_API_KEY, this.developerKey_);
        params.put(ServerRequest.PARAM_USER_APP_KEY, this.apiKey_);

        try {
            ServerResponse serverResponse = serverRequest.send(HOSTNAME, actionPath, params);
            return serverResponse.getService();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Trigger> getUserTriggers() {
        String actionPath = "/" + SettingProperties.getApiVersion() + "/" + ACTION_USER_TRIGGER;

        Map<String, String> params = new HashMap<String, String>();
        params.put(ServerRequest.PARAM_API_KEY, this.developerKey_);
        params.put(ServerRequest.PARAM_USER_APP_KEY, this.apiKey_);

        try {
            ServerResponse serverResponse = serverRequest.send(HOSTNAME, actionPath, params);
            return serverResponse.getTrigger();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean postMessage(Message _message, String _postMethod, boolean isDebug) {
        String actionPath = "/" + SettingProperties.getApiVersion() + "/" + ACTION_USER_POST;

        if (checkData(_message, _postMethod)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(ServerRequest.PARAM_API_KEY, this.developerKey_);
            params.put(ServerRequest.PARAM_USER_APP_KEY, this.apiKey_);
            params.put(ServerRequest.PARAM_POST_METHOD, _postMethod);
            params.put(ServerRequest.PARAM_MESSAGE_TITLE, _message.getContentTitle());
            params.put(ServerRequest.PARAM_MESSAGE_BODY, _message.getContentBody());
            params.put(ServerRequest.PARAM_POST_DEBUG, (isDebug ? "1" : "0"));

            try {
                ServerResponse serverResponse = serverRequest.send(HOSTNAME, actionPath, params);
                return serverResponse.getStatus().equals("OK");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private boolean checkData(Message _message, String _postMethod) {
        if (_message != null && _postMethod != null && _message.getContentBody() != null) {
            return true;
        }

        return false;
    }

    public String getApiKey_() {
        return apiKey_;
    }

    public String getDeveloperKey_() {
        return developerKey_;
    }


}
