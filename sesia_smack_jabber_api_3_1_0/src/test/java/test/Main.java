package test;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

/**
 * A sprint to test some of the code that we need
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 * @see <a href="http://www.igniterealtime.org/community/thread/35976">this thread</a> for more info / examples
 */
public class Main {
    private static Logger logger = Logger.getLogger(Main.class);

    private XMPPConnection configureAndConnect(String usr,
                                               String pw,
                                               String host,
                                               int port,
                                               String serviceName,
                                               String resource,
                                               String saslMechanismSupported,
                                               int saslMechanismSupportedIndex) {
        ConnectionConfiguration cc = new ConnectionConfiguration(host, port, serviceName);
        XMPPConnection connection = new XMPPConnection(cc);

        try {
            connection.connect();
            // You have to put this code before you login
            if (!StringUtils.isEmpty(saslMechanismSupported)) {
                SASLAuthentication.supportSASLMechanism(saslMechanismSupported, saslMechanismSupportedIndex);
            }
            // You have to specify your gmail addres WITH @gmail.com at the end
            if (!StringUtils.isEmpty(resource)) {
                connection.login(usr, pw, resource);
            }
            else {
                connection.login(usr, pw);
            }
            // See if you are authenticated
            logger.debug("authenticated? " + connection.isAuthenticated());
        }
        catch (XMPPException e1) {
            logger.debug("exception occurred trying to connnect", e1);
        }

        return connection;
    }

    public void testGtalk(String usr, String pw) throws Throwable {
        XMPPConnection xmppConnection = null;
        try {
            xmppConnection = this.configureAndConnect(usr, pw, "talk.google.com", 5222, "gmail.com", "resource",
                                                      "PLAIN", 0);

            ChatManager chatManager = xmppConnection.getChatManager();
            chatManager.addChatListener(new ChatManagerListener() {
                public void chatCreated(final Chat chat, final boolean createdLocally) {
                    chat.addMessageListener(new MessageListener() {
                        public void processMessage(final Chat chat, final Message message) {

                            String msg = String.format(
                                    "%s says %s. Message toString() = %s", chat.getParticipant(), message.getBody(),
                                    ToStringBuilder.reflectionToString(message));

                            logger.debug(msg);
                        }
                    });
                }
            });
            logger.debug("xmppConnection=" + xmppConnection.toString());
            System.in.read();
        }
        finally {
            if (xmppConnection != null) {
                xmppConnection.disconnect();
                logger.debug("xmppConnection.disconnect() called");
            }
        }
    }

    public static void main(String[] args) throws Throwable {
        String testUsr = System.getProperty("test.smack.user");
        String testPw = System.getProperty("test.smack.password");
        Main main = new Main();
        main.testGtalk(testUsr, testPw);

    }
}
