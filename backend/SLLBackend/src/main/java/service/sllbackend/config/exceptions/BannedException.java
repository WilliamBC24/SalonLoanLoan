package service.sllbackend.config.exceptions;

public class BannedException extends RuntimeException {
    public BannedException(String username) {
        super("Account banned: " + username);
    }
}
