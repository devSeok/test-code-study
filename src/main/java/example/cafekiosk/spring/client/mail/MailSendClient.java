package example.cafekiosk.spring.client.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MailSendClient {
    public boolean sendEmail(String fromEmail, String toEmail, String title, String content) {

        log.info("메일 전송");
        throw new IllegalArgumentException("메일 전송");

    }

    public void d() {
        log.info("테스트");
    }
}
