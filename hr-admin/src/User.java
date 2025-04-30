public class User {
    private String usr, pass, empType;

    public User(String usr, String pass) {
        this.usr = usr;
        this.pass = pass;
    }
    
    public void setEmpType(String type) {
        this.empType = type;
    }

    public String getEmpType() {
        return this.empType;
    }

    public String getUsername() {
        return this.usr;
    }

    public boolean validatePassword(String pwd) {
        return this.pass.equals(pwd);
    }
    
}
