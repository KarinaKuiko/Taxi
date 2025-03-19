package com.example.reportservice.service;

import com.example.reportservice.client.RideClient;
import com.example.reportservice.dto.DriverReadDto;
import com.example.reportservice.dto.RideReadDto;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.itextpdf.kernel.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import com.itextpdf.layout.Document;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PDFGeneratorService {

    private final RideClient rideClient;

    public byte[] generatePDFForDriver(DriverReadDto driver) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument)) {
        String title = "Report for " + driver.firstName() + " " + driver.lastName();
            addTitleText(document, title);
            addReport(document, driver.id());
        }
        return outputStream.toByteArray();
    }

    private void addTitleText(Document document, String title) {
        Paragraph paragraph = new Paragraph(title)
                .simulateBold()
                .setFontSize(20)
                .setMarginTop(20)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(paragraph);
    }

    private void addReport(Document document, Long id) {
        List<RideReadDto> rides = rideClient.findTop100ByDriverId(id);

        Table table = new Table(8);
        table.addCell(new Paragraph("ID").setFontSize(15));
        table.addCell(new Paragraph("Driver ID").setFontSize(15));
        table.addCell(new Paragraph("Passenger ID").setFontSize(15));
        table.addCell(new Paragraph("Address From").setFontSize(15));
        table.addCell(new Paragraph("Address To").setFontSize(15));
        table.addCell(new Paragraph("Driver Status").setFontSize(15));
        table.addCell(new Paragraph("Passenger Status").setFontSize(15));
        table.addCell(new Paragraph("Cost").setFontSize(15));

        for (RideReadDto ride : rides) {
            table.addCell(String.valueOf(ride.id()));
            table.addCell(String.valueOf(ride.driverId()));
            table.addCell(String.valueOf(ride.passengerId()));
            table.addCell(ride.addressFrom());
            table.addCell(ride.addressTo());
            table.addCell(ride.driverRideStatus());
            table.addCell(ride.passengerRideStatus());
            table.addCell(ride.cost().toString());
        }

        document.add(table);

    }
}
