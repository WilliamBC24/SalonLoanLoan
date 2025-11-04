package service.sllbackend.service;

public interface VietQrService {
    String generateQrUrl(Integer orderId, String username, Integer amount);
}
