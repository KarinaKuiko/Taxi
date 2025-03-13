package com.example.reportservice.controller;

import com.example.reportservice.service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class EmailSenderController {

    private final EmailSenderService emailSenderService;

    @GetMapping("/driver/{id}")
    public void sendEmailWithAttachment(@PathVariable("id") Long id) {
        emailSenderService.sendReportByDriverId(id);
    }

}
