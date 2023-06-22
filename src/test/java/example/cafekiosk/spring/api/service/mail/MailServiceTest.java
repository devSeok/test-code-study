package example.cafekiosk.spring.api.service.mail;

import example.cafekiosk.spring.client.mail.MailSendClient;
import example.cafekiosk.spring.domain.history.mail.MailSendHistory;
import example.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private MailSendClient mailSendClient;

    @Mock
    private MailSendHistoryRepository mailSendHistoryRepository;

    @InjectMocks
    private MailService mailService;

    @Test
    @DisplayName("메일 전송 테스트")
    void sendMail() throws Exception {
        //given
//        MailSendClient mailSendClient = Mockito.mock(MailSendClient.class);
//        MailSendHistoryRepository mailSendHistoryRepository = Mockito.mock(MailSendHistoryRepository.class);

//        MailService mailService = new MailService(mailSendClient, mailSendHistoryRepository);

        given(mailSendClient.sendEmail(anyString(), anyString(), anyString(), anyString()))
                .willReturn(true);

//        when(mailSendClient.sendEmail(anyString(), anyString(), anyString(), anyString()))


//        doReturn(true)
//                .when(mailSendClient)
//                .sendEmail(anyString(), anyString(), anyString(), anyString());



        //when
        boolean result = mailService.sendMail("", "", "", "");

        //then
        assertThat(result).isTrue();
        verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));
    }

}