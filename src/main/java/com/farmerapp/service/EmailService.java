package com.farmerapp.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.farmerapp.entity.Otp;
import com.farmerapp.entity.OtpType;
import com.farmerapp.repository.OtpRepository;

@Service
public class EmailService {
	
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private OtpRepository otpRepository;

	public Object sendOtp(String toEmail, OtpType otpType) {
		Otp storeOtp = new Otp();

		String otp = generateOTP();

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject("Your OTP Code");
		message.setText("Your OTP is: " + otp + ". It will expire in 2 minutes.");

		try {
			mailSender.send(message);
		} catch (Exception e) {
			System.err.println("Failed to send email to " + toEmail + ": " + e.getMessage());
		}

		LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(2); // OTP valid for 2 minutes

		storeOtp.setEmail(toEmail);
		storeOtp.setOtpCode(otp);
		storeOtp.setExpirationTime(expiryTime);
		storeOtp.setType(otpType);
		otpRepository.save(storeOtp);

		return otp;
	}
	
	private String generateOTP() {
		return String.valueOf(new Random().nextInt(999999)); // 6-digit OTP
	}

	
}
