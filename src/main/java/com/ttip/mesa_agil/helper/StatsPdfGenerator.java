package com.ttip.mesa_agil.helper;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.ttip.mesa_agil.dto.*;
import com.ttip.mesa_agil.dto.responses.StatsDashboardResponse;
import com.ttip.mesa_agil.model.enums.StatsPeriod;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StatsPdfGenerator {
    private static final Font TITLE_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);

    private static final Font SECTION_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);

    private static final Font TEXT_FONT =
            FontFactory.getFont(FontFactory.HELVETICA, 11);

    private static final Font HEADER_FONT =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

    public byte[] generate(StatsPeriod period, StatsDashboardResponse dashboard) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, output);

            document.open();
            addTitle(document, period);
            addSummary(document, dashboard);
            addRevenueTimeline(document, dashboard);
            addTopProducts(document, dashboard);
            addTopRevenueProducts(document, dashboard);
            addCategoryRevenue(document, dashboard);
            addTableOrders(document, dashboard);
            addTableRevenue(document, dashboard);
            addFooter(document);
            document.close();

            return output.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }

    private void addTitle(Document document, StatsPeriod period) throws DocumentException {
        document.add(new Paragraph("MesaÁgil", TITLE_FONT));
        document.add(new Paragraph("Estadísticas " + getPeriodName(period), TEXT_FONT));
        document.add(new Paragraph(" "));
    }

    private void addSummary(Document document, StatsDashboardResponse dashboard) throws DocumentException {
        document.add(new Paragraph("Resumen", SECTION_FONT));

        PdfPTable table = createTable();

        table.addCell("Ingresos");
        table.addCell("$" + dashboard.summary().totalRevenue());

        table.addCell("Pedidos");
        table.addCell(dashboard.summary().totalOrders().toString());
        table.addCell("Ticket promedio");
        table.addCell("$" + dashboard.summary().avgTicket());

        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addRevenueTimeline(Document document, StatsDashboardResponse dashboard) throws DocumentException {
        document.add(new Paragraph("Evolución de ingresos", SECTION_FONT));
        PdfPTable table = createTable();

        addHeaderCell(table, "Fecha");
        addHeaderCell(table, "Ingreso");
        for (RevenuePointDto point : dashboard.revenueTimeline()) {
            table.addCell(point.label());
            table.addCell("$" + point.revenue());
        }
        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addTopProducts(Document document, StatsDashboardResponse dashboard) throws DocumentException {
        document.add(new Paragraph("Comidas más vendidas", SECTION_FONT));
        PdfPTable table = createTable();

        addHeaderCell(table, "Producto");
        addHeaderCell(table, "Cantidad");
        for (TopItemDto item : dashboard.topProducts()) {
            table.addCell(item.name());
            table.addCell(item.total().toString());
        }
        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addTopRevenueProducts(Document document, StatsDashboardResponse dashboard) throws DocumentException {
        document.add(new Paragraph("Comidas con mayor facturación", SECTION_FONT));
        PdfPTable table = createTable();

        addHeaderCell(table, "Producto");
        addHeaderCell(table, "Facturación");
        for (TopRevenueItemDto item : dashboard.topRevenueProducts()) {
            table.addCell(item.name());
            table.addCell("$" + item.totalRevenue());
        }
        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addCategoryRevenue(Document document, StatsDashboardResponse dashboard) throws DocumentException {
        document.add(new Paragraph("Ingresos por categoría", SECTION_FONT));
        PdfPTable table = createTable();

        addHeaderCell(table, "Categoría");
        addHeaderCell(table, "Ingreso");
        for (CategoryRevenueDto category : dashboard.categoryRevenue()) {
            table.addCell(category.category());
            table.addCell("$" + category.revenue());
        }
        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addTableOrders(Document document, StatsDashboardResponse dashboard) throws DocumentException {
        document.add(new Paragraph("Mesas más usadas", SECTION_FONT));

        PdfPTable table = createTable();

        addHeaderCell(table, "Mesa");
        addHeaderCell(table, "Pedidos");
        for (TableOrdersDto tableOrder : dashboard.tableOrders()) {
            table.addCell("Mesa " + tableOrder.tableNumber());
            table.addCell(tableOrder.totalOrders().toString());
        }
        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addTableRevenue(Document document, StatsDashboardResponse dashboard) throws DocumentException {
        document.add(new Paragraph("Ingresos por mesa", SECTION_FONT));

        PdfPTable table = createTable();

        addHeaderCell(table, "Mesa");
        addHeaderCell(table, "Ingreso");
        for (TableRevenueDto tableRevenue : dashboard.tableRevenue()) {
            table.addCell("Mesa " + tableRevenue.tableNumber());
            table.addCell("$" + tableRevenue.revenue());
        }
        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addFooter(Document document) throws DocumentException {
        document.add(new Paragraph("----------------------------"));
        document.add(new Paragraph("Generado automáticamente por MesaÁgil", TEXT_FONT));
        document.add(new Paragraph(LocalDateTime.now().
            format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), TEXT_FONT));
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));

        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);

        table.addCell(cell);
    }

    private PdfPTable createTable() {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(6);
        table.setSpacingAfter(12);

        return table;
    }

    private String getPeriodName(StatsPeriod period) {
        return switch (period) {
            case LAST_DAY -> "del último día";
            case LAST_WEEK -> "de la última semana";
            case LAST_MONTH -> "del último mes";
            case LAST_YEAR -> "del último año";
        };
    }
}