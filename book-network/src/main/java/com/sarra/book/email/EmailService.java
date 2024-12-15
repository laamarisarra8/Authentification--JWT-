package com.sarra.book.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;


   @Async // we added this since the sending of mail is a heavy and time consumning. so we don't want the user to wait so we enable a Async
          // we add @EnableAsync in main to !! dont forget

    public void sendEmail( // it's and ASYNC methode
            String to,
            String username,
            EmailTemplateName emailTemplate,
            String confirmationUrl,
            String activationCode,
            String subject

    ) throws MessagingException {
        String templateName;
        if(emailTemplate == null){
            templateName = "config-email";
        }else {
            templateName= emailTemplate.name();
        }
        //Configuration of our mailSender
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MULTIPART_MODE_MIXED, // MimeMessageHelper.MULTIPART_MODE_MIXED -> amalna add static import bech wallet hakka
                UTF_8.name() // fil asell hiya hakka  StandardCharsets.UTF_8.name()

        );
        //passing parameters to our Email Template
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activation_code", activationCode);

         // this is how we send propertites to our Temlate (Context is our Template)
         Context context = new Context();
         context.setVariables(properties);

         helper.setFrom("contact@laamarisarra.com");
         helper.setTo(to);
         helper.setSubject(subject);

         String template = templateEngine.process(templateName,context); //by defaut it will automatically point to the template , and it will try to find an activate_account.html template and then it will process it with context

          helper.setText(template,true);
          mailSender.send(mimeMessage);


    }
}
