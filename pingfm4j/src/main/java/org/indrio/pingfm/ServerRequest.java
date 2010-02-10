package org.indrio.pingfm;

import java.io.IOException;
import java.util.Map;

public interface ServerRequest {
    String PARAM_API_KEY = "api_key";
    String PARAM_USER_APP_KEY = "user_app_key";
    String PARAM_LIMIT = "limit";
    String PARAM_ORDER = "order";
    String PARAM_POST_METHOD = "post_method";
    String PARAM_POST_DEBUG = "debug";
    String PARAM_MESSAGE_TITLE = "title";
    String PARAM_MESSAGE_BODY = "body";
    String PARAM_MESSAGE_SERVICE = "service";
    String PARAM_TRIGGER = "trigger";

    public ServerResponse send(String _host, String _path, Map<String, String> _params)
            throws IOException;
}
