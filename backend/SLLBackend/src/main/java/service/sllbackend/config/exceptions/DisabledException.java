package service.sllbackend.config.exceptions;

public class DisabledException extends RuntimeException {
    public DisabledException(String username) {
        super("Account disabled: " + username);
    }
}
