package ardoq.auth;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;


/**
 * Example implementation of custom authentication for Ardoq.
 *
 * Ardoq API looks for a class with
 * - name ardoq.auth.CustomAuthentication
 * - empty constructor
 * - implementing methods from interface ardoq.auth.ICustomAuthentication
 *
 * @author Kristian Helgesen, Ardoq AS
 */
public class CustomAuthentication implements ICustomAuthentication {

    public static void main(String[] args) {
        new CustomAuthentication().authenticate("Kristian Helgesen", "ldap123");
    }

    public ArdoqUser authenticate(String username, String password) {
        return authenticate("ldap://localhost:389", username, password);
    }

    public String status() {
        return "ok";
    }

    public ArdoqUser authenticate(String providerURL, String username, String password) {
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
            System.out.println("ok from LDAP server: "+ctx.getAttributes("cn="+username+",dc=example,dc=org"));
            ctx.close();

            ArdoqUser user = new ArdoqUser();
            user.setEmail("kristian+custom4@ardoq.com");
            user.setFullname("Kristian Helgesen");
            return user;
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return null;
    }

}