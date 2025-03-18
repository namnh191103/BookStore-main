package com.bookstore.controller;

import java.io.UnsupportedEncodingException;

import com.bookstore.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.bookstore.config.Utility;
import com.bookstore.entity.Customer;
import com.bookstore.exception.CustomerNotFoundException;
import com.bookstore.model.EmailSettingBag;
import com.bookstore.service.SettingService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ForgotPasswordController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private SettingService settingService;

    @GetMapping("/forgot_password")
    public String showRequestForm() {
        return "customer/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processRequestForm(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        try {
            String token = customerService.updateResetPasswordToken(email);
            String link = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(link, email);

            model.addAttribute("message", "Link thay đổi mật khẩu đã được gửi tới email");
        } catch (CustomerNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Không thể gửi e-mail");
        }
        return "customer/forgot_password_form";
    }

    private void sendEmail(String link, String email) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

        String toAddress = email;
        String subject = "Cập nhật mật khẩu";
        String content = "<p>Gửi khách hàng BookStore</p>"
                + "<p>Yêu cầu cập nhập mật đã được chấp nhận</p>"
                + "<p>Sử dụng link sau để thay đổi mật khẩu</p>"
                + "<p><a href=\"" + link + "\">Thay đổi mật khẩu</a></p>"
                + "<br>"
                + "Bỏ qua nếu bạn không gửi yêu cầu này";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // Set UTF-8 encoding

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        helper.setText(content, true);
        mailSender.send(message);
    }

    @GetMapping("/reset_password")
    public String showResetForm(@Param("token") String token, Model model) {
        Customer customer = customerService.getByResetPasswordToken(token);
        if (customer != null) {
            model.addAttribute("token", token);
        } else {
            model.addAttribute("message", "Không hợp lệ");
            model.addAttribute("pageTitle", "Không hợp lệ");
            return "message";
        }
        return "customer/reset_passwrod_form";
    }

    @PostMapping("/reset_password")
    public String processResetForm(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        try {
            customerService.updatePassword(token, password);

            model.addAttribute("pageTitle", "Cập nhật mật khẩu");
            model.addAttribute("title", "Cập nhật mật khẩu");
            model.addAttribute("message", "Thay đổi mật khẩu thành công");
            
            return "message";
        } catch (CustomerNotFoundException e) {
            model.addAttribute("message", "Không hợp lệ");
            model.addAttribute("message", e.getMessage());
            return "message";
        }
    }


}
