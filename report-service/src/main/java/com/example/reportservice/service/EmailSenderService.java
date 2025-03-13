package com.example.reportservice.service;

import com.example.reportservice.client.DriverClient;
import com.example.reportservice.dto.DriverReadDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final JavaMailSender mailSender;
    private final PDFGeneratorService pdfGeneratorService;
    private final DriverClient driverClient;

    @Value("${sender.address}")
    private String toAddress;

    @Value("${sender.subject}")
    private String subject;

    @Scheduled(fixedDelayString = "PT${app.report-scheduler.interval}")
    public void sendReportForAllDrivers() {
        List<DriverReadDto> drivers = driverClient.findFullList();
        for (DriverReadDto driver : drivers) {
            sendReportByDriverId(driver.id());
        }
    }

    public void sendReportByDriverId(Long driverId) {
        DriverReadDto driver = driverClient.findById(driverId);
        sendReport(driver);
    }

    private void sendReport(DriverReadDto driver) {
        try {
            byte[] pdfBytes = pdfGeneratorService.generatePDFForDriver(driver);

            Path tempFile = Files.createTempFile("report-", ".pdf");
            Files.write(tempFile, pdfBytes);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(toAddress);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText("Report");

            mimeMessageHelper.addAttachment(tempFile.getFileName().toString(), new FileSystemResource(tempFile));
            mailSender.send(mimeMessage);

            Files.deleteIfExists(tempFile);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("I/O error while reading response: " + e.getMessage(), e);
        }
    }
}
