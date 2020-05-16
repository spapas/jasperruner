package gr.serafeim.jasperrunner;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRSaver;
//import net.sf.jasperreports.export.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class Main {
    static Logger logger = Logger.getLogger(Main.class.getName());

    public static Properties readProperties(String propertiesPath) throws IOException {

        if(propertiesPath==null || propertiesPath.isEmpty()) {
            propertiesPath = "config.properties";
        }
        logger.info("Will read properties from " + propertiesPath);

        InputStream input = new FileInputStream(propertiesPath);
        Properties props = new Properties();

        props.load(input);

        return props;
    }

    public static void main(String[] args) {
	    System.out.print("Starting...");

        try {
            Properties props = readProperties(args.length>0?args[0]:null);

            String dbUrl = props.getProperty("db.url");
            String dbUser = props.getProperty("db.user");
            String dbPassword= props.getProperty("db.password");
            String reportFile = props.getProperty("report.file");

            InputStream reportStream = new FileInputStream(reportFile);
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            //JRSaver.saveObject(jasperReport, "SHIPREG_epistolh.jasper");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("title", "Employee Report");

            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            logger.info("Connected to db");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, conn);

            JRDocxExporter exporter = new JRDocxExporter();

            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "koko.docx");
            System.out.print("2");
            //JRDocxExporter exporter = new JRDocxExporter();

            exporter.exportReport();
            System.out.print("3");

            /* DOCX
            SimpleDocxReportConfiguration reportConfig = new SimpleDocxReportConfiguration();

            exporter.setConfiguration(reportConfig);
            reportConfig.setFramesAsNestedTables(false);
            reportConfig.setFlexibleRowHeight(false);
            //reportConfig.setNewLineAsParagraph(false);
            reportConfig.setIgnoreHyperlink(true);
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("test.docx"));
            exporter.exportReport();

             */

            /** XLSX

            JRXlsxExporter exporter = new JRXlsxExporter();
            SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
            reportConfig.setSheetNames(new String[] { "Employee Data" });
            exporter.setConfiguration(reportConfig);
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("test.xlsx"));
            exporter.exportReport();
             */
            /** PDF
            JRPdfExporter exporter = new JRPdfExporter();

            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("employeeReport.pdf"));

            SimplePdfReportConfiguration reportConfig  = new SimplePdfReportConfiguration();
            reportConfig.setSizePageToContent(true);
            reportConfig.setForceLineBreakPolicy(false);

            SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
            exportConfig.setMetadataAuthor("baeldung");
            exportConfig.setEncrypted(true);

            exportConfig.setAllowedPermissionsHint("PRINTING");

            exporter.setConfiguration(reportConfig);
            exporter.setConfiguration(exportConfig);
            exporter.exportReport();
             */

            conn.close();
            System.out.print("4");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JRException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
