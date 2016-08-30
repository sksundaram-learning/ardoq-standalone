package ardoq.auth;

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

    public String status() {
        return "ok";
    }

    public ArdoqUser authenticate(String username, String password) {
        if (username.toLowerCase().startsWith("ldap")) {
            ArdoqUser user = new ArdoqUser();
            user.setEmail(username.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()+"@ardoq.com");
            user.setFullname(username);
            return user;
        }
        return null;
    }

}