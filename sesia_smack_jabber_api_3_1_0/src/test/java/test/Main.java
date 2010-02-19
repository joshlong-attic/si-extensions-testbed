package test;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;


/**
 * A sprint to test some of the code that we need
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 * @see <a href="http://www.igniterealtime.org/community/thread/35976">this thread</a> for more info / examples
 */
public class Main {
    private static Logger logger = Logger.getLogger(Main.class);

    private XMPPConnection configureAndConnect(String usr, String pw, String host, int port, String serviceName, String resource, String saslMechanismSupported, int saslMechanismSupportedIndex) {
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
            } else {
                connection.login(usr, pw);
            }
            // See if you are authenticated
            logger.debug("authenticated? " + connection.isAuthenticated());
        } catch (XMPPException e1) {
            logger.debug("exception occurred trying to connnect", e1);
        }

        return connection;
    }

    public   void testGtalk(String usr, String pw) throws Throwable {
        XMPPConnection xmppConnection = this.configureAndConnect(usr, pw, "talk.google.com", 5222, "gmail.com", "resource", "PLAIN", 0);

        logger.debug("xmppConnection=" + xmppConnection.toString());
    }

    public static void main(String[] args) throws Throwable {
        String testUsr = System.getProperty("test.smack.user");
        String testPw = System.getProperty("test.smack.password");
        Main main = new Main();
        main.testGtalk(testUsr, testPw);

        System.in.read();
    }
}
