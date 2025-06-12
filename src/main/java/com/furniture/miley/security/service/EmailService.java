package com.furniture.miley.security.service;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.ErrorResponseDTO;
import com.furniture.miley.commons.dto.ResponseDTO;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${front.url}")
    private String FRONT_PATH;

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final UserRepository userRepository;

    public ResponseDTO sendHtmlTemplateEmail(String to ) throws MessagingException {
        try {
            User user = userRepository.findByEmail( to ).orElseThrow(() -> new ResourceNotFoundException("Ingrese un correo que este registrado"));
            try {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                Context context = new Context();

                UUID token = UUID.randomUUID();
                user.setTokenPassword( token.toString() );

                User userUpdate = userRepository.save( user );

                Map<String, Object> model = new HashMap<>();
                model.put("username", userUpdate.getPersonalInformation().getFirstName());
                /* https://creaciones-joaquin-front.vercel.app/ */
                /* http://localhost:3000/ */
                model.put("url", FRONT_PATH + "/auth/change-password/confirm/" + token);
                context.setVariables( model );

                String htmlText =  templateEngine.process("email_template", context);
                helper.setFrom("cortezkevinq@gmail.com");
                helper.setTo(to);
                helper.setSubject("Prueba envio email");
                helper.setText(htmlText, true);

                javaMailSender.send(mimeMessage);
                return new SuccessResponseDTO<>(
                        ResponseMessage.EMAIL_SENT,
                        HttpStatus.OK.name(),
                        ""
                );
            }catch (MessagingException e) {
                return new ErrorResponseDTO(
                        "Ocurrio un error al enviar el correo",
                        HttpStatus.BAD_REQUEST.name()
                );
            }
        }catch (ResourceNotFoundException e){
            return new ErrorResponseDTO(
                    "Ocurrio un error al enviar el correo",
                    HttpStatus.BAD_REQUEST.name()
            );
        }
    }
}
