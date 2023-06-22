package example.cafekiosk.spring.api.service.mail;


import example.cafekiosk.spring.client.mail.MailSendClient;
import example.cafekiosk.spring.domain.history.mail.MailSendHistory;
import example.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailService {

    private final MailSendClient mailSendClient;
    private final MailSendHistoryRepository mailSendHistoryRepository;

    public boolean sendMail(String fromEmail, String toEmail, String title, String content) {

        boolean result = mailSendClient.sendEmail(fromEmail, toEmail, title, content);

        mailSendClient.d();
        if (result) {
            mailSendHistoryRepository.save(MailSendHistory.builder()
                    .toEmail(fromEmail)
                    .fromEmail(toEmail)
                    .subject(title)
                    .content(content)
                    .build()
            );
            return true;
        }

        return false;
    }
}
