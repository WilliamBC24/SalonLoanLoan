package service.sllbackend.service;


import jakarta.mail.MessagingException;

public interface EmailService {
    void sendMail(String to, String subject, String content);
    void sendOrderPickupNotice(Integer orderId) throws MessagingException;
}
