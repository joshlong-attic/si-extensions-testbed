package org.indrio.pingfm.impl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.indrio.pingfm.ServerRequest;
import org.indrio.pingfm.ServerResponse;
import org.indrio.pingfm.XMLResultParser;
import org.indrio.pingfm.beans.Service;
import org.indrio.pingfm.beans.Trigger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ServerRequestImpl implements ServerRequest {

    private final Logger logger = Logger.getLogger(ServerRequest.class);

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private HttpClient httpClient = new HttpClient();

    public ServerResponse send(String _host, String _path, Map<String, String> _params) throws IOException {
        logger.debug("entering send method");
        logger.debug("	> _host : " + _host);
        logger.debug("	> _path : " + _path);
        logger.debug("	> _params : ");

        for (Map.Entry<String, String> entry : _params.entrySet()) {
            logger.debug("		> " + entry.getKey() + " : " + entry.getValue());
        }

        PostMethod postMethod = new PostMethod(_path);

        httpClient.getHostConfiguration().setHost(_host, 80, "http");

        postMethod.setRequestBody(genNameValuePair(_params));

        httpClient.executeMethod(postMethod);

        InputStream responseBody = postMethod.getResponseBodyAsStream();

        ServerResponse serverResponse = convertToServerResponse(responseBody);

        postMethod.releaseConnection();

        return serverResponse;
    }

    private NameValuePair[] genNameValuePair(Map<String, String> _params) {
        logger.debug("create params ");

        NameValuePair[] nameValuePair = new NameValuePair[_params.size()];

        int i = -1;
        for (Map.Entry<String, String> entry : _params.entrySet()) {
            nameValuePair[++i] = new NameValuePair(entry.getKey(), entry.getValue());
        }

        return nameValuePair;
    }

    private ServerResponse convertToServerResponse(InputStream _responseStream) {
        XMLResultParser parser = new XMLResultParser(_responseStream);

        ServerResponse serverResponse = new ServerResponse(parser.getStatus(), parser.getTransactionId(), parser.getMethod(), parser.getErrorMessage());

        logger.debug("status : " + serverResponse.getStatus());
        logger.debug("transactionId : " + serverResponse.getTransactionId());
        logger.debug("method : " + serverResponse.getMethod());
        logger.debug("error message : " + serverResponse.getErrorMessage());

        if (parser.getServices() != null) {
            serverResponse.setService(parser.getServices());

            for (Service service : serverResponse.getService()) {
                logger.debug("service : ");
                logger.debug("	> id : " + service.getId());
                logger.debug("	> name : " + service.getName());
                logger.debug("	> trigger : " + service.getTrigger());
                logger.debug("	> url : " + service.getUrl());
                logger.debug("	> icon : " + service.getIcon());
                logger.debug("	> methods : " + service.getMethods());
            }
        }

        if (parser.getTrigger() != null) {
            serverResponse.setTrigger(parser.getTrigger());

            for (Trigger trigger : serverResponse.getTrigger()) {
                logger.debug("trigger : ");
                logger.debug("	> id : " + trigger.getId());
                logger.debug("	> method : " + trigger.getMethod());
            }
        }

        if (parser.getMessages() != null) {
            serverResponse.setMessages(parser.getMessages());
        }

        return serverResponse;
    }
}
