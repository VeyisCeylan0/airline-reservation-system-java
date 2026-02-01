package Users;

public class Customer extends User {

    public Customer(String username, String password) {
        super(username, password, Role.CUSTOMER);
    }
}
