package ardoq.auth;

/**
 * Don't change this interface - the Ardoq API server expects it to be excactly like this.
 *
 * @author Kristian Helgesen, Ardoq AS
 */
public interface ICustomAuthentication {
    public String status();

    public ArdoqUser authenticate(String username, String password);

    public static class ArdoqUser {
        private String email;
        private String fullname;

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getFullname() {
            return fullname;
        }
        public void setFullname(String fullname) {
            this.fullname = fullname;
        }
    }

}
