package org.indrio.pingfm;

import org.indrio.pingfm.beans.Message;
import org.indrio.pingfm.beans.Service;
import org.indrio.pingfm.beans.Trigger;

import java.util.List;

public interface PingFmService {
    String ACTION_GET_ALL_SERVICE = "system.services";
    String ACTION_USER_VALIDATE = "user.validate";
    String ACTION_USER_SERVICE = "user.services";
    String ACTION_USER_TRIGGER = "user.triggers";
    String ACTION_USER_GET_MESSAGE = "user.latest";
    String ACTION_USER_POST = "user.post";
    String ACTION_USER_TPOST = "user.tpost";

    boolean validate();

    List<Service> getAllService();

    List<Service> getUserService();

    List<Trigger> getUserTriggers();

    List<Message> getUserLatestMessage(String _limit);

    boolean postMessage(Message _message, String _postMethod, boolean _isDebug);
}
