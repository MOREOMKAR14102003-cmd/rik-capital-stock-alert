package in.rikcapital.stockalert.service;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import in.rikcapital.stockalert.model.StockAlertRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StockAlertEmailService {

    private final String adminEmail;
    private final String fromEmail;
    private final String apiKey;

    public StockAlertEmailService(
            @Value("${stock-alert.admin-email}") String adminEmail,
            @Value("${stock-alert.from-email}") String fromEmail,
            @Value("${resend.api-key}") String apiKey) {

        this.adminEmail = adminEmail;
        this.fromEmail = fromEmail;
        this.apiKey = apiKey;
    }

    public void send(StockAlertRequest request) {

        try {

            System.out.println("Sending Stock Alert email...");

            StringBuilder companies = new StringBuilder();

            for (int i = 0; i < request.companies().size(); i++) {
                companies.append(i + 1)
                        .append(". ")
                        .append(request.companies().get(i))
                        .append("\n");
            }

            String emailBody =
                    "Dear RIK Capital Team,\n\n"
                            + "A new Stock Alert request has been submitted "
                            + "through the RIK Capital website.\n\n"

                            + "USER DETAILS\n"
                            + "--------------------------------\n"
                            + "Name: " + request.name() + "\n"
                            + "Email ID: " + request.email() + "\n"
                            + "Phone Number: " + request.phone() + "\n\n"

                            + "SELECTED COMPANIES\n"
                            + "--------------------------------\n"
                            + companies

                            + "\nPlease review the request and take "
                            + "the necessary action.\n\n"

                            + "Regards,\n"
                            + "RIK Capital\n"
                            + "Strategic Investor Relations Advisory";

            Resend resend = new Resend(apiKey);

            CreateEmailOptions emailOptions =
                    CreateEmailOptions.builder()
                            .from(fromEmail)
                            .to(adminEmail)
                            .replyTo(request.email())
                            .subject("Stock Alert Request – RIK Capital")
                            .text(emailBody)
                            .build();

            var response = resend.emails().send(emailOptions);

            System.out.println("======================================");
            System.out.println("EMAIL SENT SUCCESSFULLY");
            System.out.println("Resend Response: " + response);
            System.out.println("Sent To: " + adminEmail);
            System.out.println("======================================");

        } catch (Exception e) {

            System.err.println("======================================");
            System.err.println("EMAIL SENDING FAILED");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("======================================");

            throw new RuntimeException(
                    "Unable to send email: " + e.getMessage(), e
            );
        }
    }
}