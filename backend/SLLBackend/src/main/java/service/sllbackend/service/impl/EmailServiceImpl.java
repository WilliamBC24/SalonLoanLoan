package service.sllbackend.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import service.sllbackend.entity.OrderInvoice;
import service.sllbackend.entity.OrderInvoiceDetails;
import service.sllbackend.repository.OrderInvoiceDetailsRepo;
import service.sllbackend.service.EmailService;
import service.sllbackend.service.OrderService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final OrderService orderService;
    private final OrderInvoiceDetailsRepo orderInvoiceDetailsRepo;

    @Override
    public void sendMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    @Override
    public void sendOrderPickupNotice(Integer orderId) throws MessagingException {
        OrderInvoice orderInvoice = orderService.getOrderDetails(orderId);
        String mail = orderInvoice.getUserAccount().getEmail();
        String subject = "Your Order #" + orderId + " Is Ready for Pickup!";
        List<OrderInvoiceDetails> details = orderInvoiceDetailsRepo.findByOrderInvoice(orderInvoice);
        String htmlTemplate = """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; color:#333; }
        .container {
            max-width: 600px;
            margin: 0 auto;
            padding: 16px;
            border: 1px solid #eee;
            border-radius: 8px;
            background: #fafafa;
        }
        h2 {
            text-align: center;
            color: #2b6cb0;
        }
        .info {
            font-size: 15px;
            margin-bottom: 18px;
            line-height: 1.5;
        }
        .address-box {
            background: #edf2f7;
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 18px;
            border-left: 4px solid #2b6cb0;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 12px;
            margin-bottom: 18px;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 10px 8px;
            text-align: left;
        }
        th {
            background: #2b6cb0;
            color: white;
            font-size: 14px;
        }
        tfoot td {
            font-weight: bold;
            background: #f7fafc;
        }
    </style>
</head>
<body>

<div class="container">
    <h2>Your Order Is Ready for Pickup!</h2>

    <p class="info">
        Dear customer,<br>
        Your order is now ready for pickup at our salon.
        Please stop by at your earliest convenience.
    </p>

    <div class="address-box">
        <strong>Pickup Location:</strong><br>
        91 Huỳnh Thúc Kháng, Thành Công<br>
        Ba Đình, Hà Nội
    </div>

    <h3>Order Summary</h3>

    <table>
        <thead>
            <tr>
                <th>Product</th>
                <th>Qty</th>
                <th>Price (₫)</th>
                <th>Subtotal (₫)</th>
            </tr>
        </thead>
        <tbody>
            [ROWS]
        </tbody>
        <tfoot>
            <tr>
                <td colspan="3">Total</td>
                <td>[TOTAL] ₫</td>
            </tr>
        </tfoot>
    </table>

    <p class="info">
        Thank you for choosing Salon Loan Loan!
        We look forward to seeing you soon.
    </p>
</div>

</body>
</html>
""";

        StringBuilder rows = new StringBuilder();

        for (OrderInvoiceDetails d : details) {
            rows.append(String.format("""
        <tr>
            <td>%s</td>
            <td>%d</td>
            <td>%s</td>
            <td>%s</td>
        </tr>
    """,
                    d.getProduct().getProductName(),
                    d.getQuantity(),
                    formatMoney(d.getPriceAtSale()),
                    formatMoney(d.getSubtotal())
            ));
        }
        String finalHtml = htmlTemplate
                .replace("[ROWS]", rows.toString())
                .replace("[TOTAL]", formatMoney(orderInvoice.getTotalPrice()));




        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
        mimeMessageHelper.setTo(mail);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(finalHtml, true);

        mailSender.send(mimeMessage);
    }

    private String formatMoney(Integer v) {
        return String.format("%,d", v);
    }

}