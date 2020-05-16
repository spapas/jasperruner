package gr.serafeim.jasperrunner;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRSaver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import net.sf.jasperreports.export.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
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

    public static void createReport(Connection conn, String reportFile, Map<String, Object> parameters, int idx ) throws FileNotFoundException, JRException {
        InputStream reportStream = new FileInputStream(reportFile);

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        //JRSaver.saveObject(jasperReport, "SHIPREG_epistolh.jasper");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);

        JRDocxExporter exporter = new JRDocxExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "report_"+idx+".docx");

        exporter.exportReport();

    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);
            map.put(key, value);
        }
        return map;
    }

    public static void main(String[] args) {
	    System.out.print("Starting...");

        try {
            Properties props = readProperties(args.length>0?args[0]:null);

            String dbUrl = props.getProperty("db.url");
            String dbUser = props.getProperty("db.user");
            String dbPassword= props.getProperty("db.password");
            String reportFile = props.getProperty("report.file");
            String paramsFile = props.getProperty("params.file");

            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            logger.info("Connected to db");

            String jsonContent = new String(Files.readAllBytes(Paths.get(paramsFile)));
            JSONArray jarr = new JSONArray(jsonContent);

            for(int i=0;i<jarr.length();i++) {
                JSONObject jo = jarr.getJSONObject(i);
                Map<String, Object> parameters = toMap(jo);
                logger.info("Creating report for " +jo.toString() );
                createReport(conn, reportFile, parameters, i);
            }

            /*** HOW TO USE for jasperreport latest versions ***

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
