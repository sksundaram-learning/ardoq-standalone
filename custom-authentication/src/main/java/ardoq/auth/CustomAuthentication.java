package ardoq.auth;
import java.io.Console;
import java.text.MessageFormat;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
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

    /**
     * Main function only to test implementation
     *
     * @param args
     */
    public static void main(String[] args) {
        CustomAuthentication auth = new CustomAuthentication();
        if(auth.status() == STATUS.error) {
            System.out.println("Error starting LDAP-test application.");
            return;
        }

        Console console = System.console();
        String username = console.readLine("LDAP Username: ");
        char[] password = console.readPassword("LDAP Password: ");

        ArdoqUser user = auth.authenticate(username,new String(password));
        System.out.println("Name: "+user.getFullname());
        System.out.println("Email: "+user.getEmail());
    }


    public STATUS status() {
        try {
            getProviderURL();
            getSecurityPrincipal("");
            return STATUS.ok;
        } catch(Exception e) {
            System.err.println(e.getMessage());
            return STATUS.error;
        }
    }

    public ArdoqUser authenticate(String username, String password) {
        System.out.println("Authenticating: "+username);

        // Set up environment for creating initial context
        Hashtable<String, Object> env = new Hashtable<String, Object>(11);

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, getProviderURL());
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, getSecurityPrincipal(username));
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            // Create initial context
            DirContext ctx = new InitialDirContext(env);
            System.out.println("ok from LDAP server: "+ctx.getAttributes(getSecurityPrincipal(username)));

            Attributes attr = ctx.getAttributes(getSecurityPrincipal(username));

            ArdoqUser user = new ArdoqUser();
            user.setEmail(attr.get(getEmailAttribute()).get().toString());
            user.setFullname(attr.get(getNameAttribute()).get().toString());
            ctx.close();

            return user;
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getEnv(String attrName) {
        String attr = System.getenv(attrName);
        if (attr==null || attr.trim().length()==0){
            attr = System.getProperty(attrName);
        }
        if (attr==null || attr.trim().length()==0){
            throw new RuntimeException("Environt variable " + attrName + " not set");
        }
        return attr;
    }

    private String getProviderURL() {
        return getEnv("LDAP_PROVIDER_URL");
    }

    private String getEmailAttribute() {
        return getEnv("LDAP_EMAIL_ATTR");
    }

    private String getNameAttribute() {
        return getEnv("LDAP_NAME_ATTR");
    }

    private String getSecurityPrincipal(String username) {
        String securityPrincipal = getEnv("LDAP_SECURITY_PRINCIPAL");
        if (securityPrincipal.indexOf("{0}")<0) {
            throw new RuntimeException("Expects environment variable LDAP_SECURITY_PRINCIPAL, with format \"cn={0},dc=example,dc=org\"");
        }
        return MessageFormat.format(securityPrincipal, username);
    }


}