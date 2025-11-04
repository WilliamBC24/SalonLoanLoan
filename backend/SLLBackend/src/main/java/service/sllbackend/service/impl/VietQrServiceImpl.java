package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import service.sllbackend.service.VietQrService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class VietQrServiceImpl implements VietQrService {

    @Value("${vietqr.bank-id}")
    private String bankId;

    @Value("${vietqr.account-no}")
    private String accountNo;

    @Value("${vietqr.template}")
    private String template;

    @Override
    public String generateQrUrl(Integer orderId, String username, Integer amount) {
        // Format: Thanh Toan Don Hang username orderID
        String description = String.format("Thanh Toan Don Hang %s %d", username, orderId);
        
        // URL encode the description
        String encodedDescription = URLEncoder.encode(description, StandardCharsets.UTF_8);
        
        // Generate VietQR URL
        // Format: https://img.vietqr.io/image/<BANK_ID>-<ACCOUNT_NO>-<TEMPLATE>.png?amount=<AMOUNT>&addInfo=<DESCRIPTION>
        return String.format(
            "https://img.vietqr.io/image/%s-%s-%s.png?amount=%d&addInfo=%s",
            bankId,
            accountNo,
            template,
            amount,
            encodedDescription
        );
    }
}
