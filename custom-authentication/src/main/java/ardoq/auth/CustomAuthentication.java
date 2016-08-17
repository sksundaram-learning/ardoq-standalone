package ardoq.auth;
import javax.naming.*;
import javax.naming.directory.*;

import java.util.Hashtable;

public class CustomAuthentication {

    public static void main(String[] args) {
        authenticate("Kristian Helgesen", "ldap123");
    }

    public static boolean authenticate(String username, String password) {
        return authenticate("ldap://localhost:389", username, password);
    }

    public static boolean authenticate(String providerURL, String username, String password) {
        System.out.println("authenticating: "+username);

        // Set up environment for creating initial context
        Hashtable<String, Object> env = new Hashtable<String, Object>(11);

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, providerURL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "cn="+username+",dc=example,dc=org");
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            // Create initial context
            DirContext ctx = new InitialDirContext(env);
            System.out.println("ok "+ctx.getAttributes("cn="+username+",dc=example,dc=org"));
            ctx.close();

            return true;
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return false;
    }
}