package com.ttip.mesa_agil.helper;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.ttip.mesa_agil.dto.*;
import com.ttip.mesa_agil.dto.responses.BillItemResponse;
import com.ttip.mesa_agil.dto.responses.BillSummaryResponse;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class BillPdfGenerator {

    private static final Font TITLE_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);

    private static final Font SECTION_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);

    private static final Font TEXT_FONT =
            FontFactory.getFont(FontFactory.HELVETICA, 11);

    private static final Font HEADER_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

    public byte[] generate(BillSummaryResponse bill) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, output);

            document.open();

            addTitle(document);
            addOrderInfo(document, bill);
            addItems(document, bill);
            addTotal(document, bill);
            addFooter(document);

            document.close();

            return output.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }

    private void addTitle(Document document) throws DocumentException {
        document.add(new Paragraph("MesaÁgil", TITLE_FONT));
        document.add(new Paragraph("Resumen de cuenta", TEXT_FONT));
        document.add(new Paragraph(" "));
    }

    private void addOrderInfo(Document document, BillSummaryResponse bill)
            throws DocumentException {
        document.add(new Paragraph("Información de la orden", SECTION_FONT));

        PdfPTable table = createTable(2);

        addHeaderCell(table, "Mesa");
        table.addCell(String.valueOf(bill.tableNumber()));

        addHeaderCell(table, "Fecha de la orden");
        table.addCell(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        .withZone(ZoneId.systemDefault())
                        .format(bill.orderedAt())
        );

        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addItems(Document document, BillSummaryResponse bill)
            throws DocumentException {
        document.add(new Paragraph("Productos", SECTION_FONT));

        PdfPTable table = createTable(4);
        table.setWidths(new float[]{5, 2, 3, 3});

        addHeaderCell(table, "Producto");
        addHeaderCell(table, "Cantidad");
        addHeaderCell(table, "Precio");
        addHeaderCell(table, "Total");

        for (BillItemResponse item : bill.items()) {
            table.addCell(item.productName());
            table.addCell(item.quantity().toString());
            table.addCell(" × $" + item.unitPrice());
            table.addCell("$" + item.totalPrice());
        }
        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addTotal(Document document, BillSummaryResponse bill)
            throws DocumentException {
        Paragraph total = new Paragraph(
                "TOTAL: $" + bill.total(),
                SECTION_FONT
        );

        total.setAlignment(Element.ALIGN_RIGHT);

        document.add(total);
        document.add(new Paragraph(" "));
    }

    private void addFooter(Document document)
            throws DocumentException {
        document.add(new Paragraph("----------------------------"));

        Paragraph footer = new Paragraph("Generado automáticamente por MesaÁgil", TEXT_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);

        document.add(footer);

        Paragraph thanks = new Paragraph("Gracias por elegirnos.", TEXT_FONT);
        thanks.setAlignment(Element.ALIGN_CENTER);

        document.add(thanks);
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(
                new Phrase(text, HEADER_FONT)
        );

        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);

        table.addCell(cell);
    }

    private PdfPTable createTable(int columns) {
        PdfPTable table = new PdfPTable(columns);

        table.setWidthPercentage(100);
        table.setSpacingBefore(6);
        table.setSpacingAfter(12);

        return table;
    }

}
