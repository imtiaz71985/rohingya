package actions.reports.spSummary

import com.pms.PmServiceSector
import grails.transaction.Transactional
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import pms.ActionServiceIntf
import pms.BaseService
import pms.utility.DateUtility

import java.text.DateFormat
import java.text.SimpleDateFormat

@Transactional
class DownloadSpSummaryActionService extends BaseService implements ActionServiceIntf {

    JasperService jasperService

    private static final String REPORT_FOLDER_ALL_IND = 'pmActions/monthly/allIndicator'
    private static final String JASPER_FILE_ALL_IND = 'spWithAllIndicator'
    private static final String REPORT_TITLE_LBL = 'reportTitle'
    private static final String REPORT_TITLE = ' -SP Report of '
    private static final String SERVICE_ID = "serviceId"
    private static final String SERVICE_NAME = "serviceName"
    private static final String SERVICE_SHORT_NAME = "shortName"
    private static final String YEAR = "year"
    private static final String MONTH_INT = "monthInt"
    private static final String DOT_STR = ". "
    private static final String MONTH_STR = "monthStr"
    private static final String CURRENT_MONTH = "currentMonth"


    /**
     * Get parameters from UI
     * @param parameters -serialized parameter from UI
     * @param obj -N/A
     * @return -a map containing necessary information for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        String monthStr = params.month.toString()
        DateFormat originalFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);

        Date date = originalFormat.parse(monthStr);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR)
        int monthInt = c.get(Calendar.MONTH)+ 1
        Date currentMonth = DateUtility.getSqlDate(c.getTime());

        String monthName = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

        long serviceId = Long.parseLong(params.serviceId.toString())
        PmServiceSector service = PmServiceSector.read(serviceId)
        params.put(SERVICE_ID, serviceId)
        params.put(SERVICE_NAME, service.name)
        params.put(SERVICE_SHORT_NAME, service.shortName)
        params.put(YEAR, year)
        params.put(CURRENT_MONTH, currentMonth)
        params.put(MONTH_STR, monthName)
        params.put(MONTH_INT, monthInt)

        return params
    }

    /**
     * Generates report
     * @param params -N/A
     * @param obj -a map returned from previous method
     * @return -a map containing all necessary information for downloading report
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            Map report = getReport(result)
            result.put(REPORT, report)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * do nothing for post operation
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Wrap sprint details list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
    private Map getReport(Map result) {
        boolean isChecked = Boolean.parseBoolean(result.checked.toString())
        String reportFolder = REPORT_FOLDER_ALL_IND
        String jesperFile = JASPER_FILE_ALL_IND

        String rootDir = result.reportDirectory + File.separator
        String logoDir = result.logoDirectory + File.separator
        String reportDir = result.reportDirectory + File.separator + reportFolder
        String subReportDir = reportDir + File.separator
        String outputFileName = result.get(MONTH_INT)+ DOT_STR + result.get(SERVICE_SHORT_NAME) + REPORT_TITLE + result.get(MONTH_STR) + SINGLE_SPACE + result.get(YEAR) + PDF_EXTENSION
        String titleStr = result.get(SERVICE_NAME) + REPORT_TITLE + EMPTY_SPACE + result.get(MONTH_STR) + SINGLE_SPACE + result.get(YEAR)

        Map reportParams = new LinkedHashMap()
        reportParams.put(ROOT_DIR, rootDir)
        reportParams.put(LOGO_DIR, logoDir)
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(SUBREPORT_DIR, subReportDir)
        reportParams.put(REPORT_TITLE_LBL, titleStr)
        reportParams.put(SERVICE_ID, result.get(SERVICE_ID))
        reportParams.put(SERVICE_NAME, result.get(SERVICE_NAME))
        reportParams.put(YEAR, result.get(YEAR))
        reportParams.put(MONTH_STR, result.get(MONTH_STR))
        reportParams.put(CURRENT_MONTH, result.get(CURRENT_MONTH))

        JasperReportDef reportDef = new JasperReportDef(name: jesperFile, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
