package actions.pmMcrsLog

import com.pms.AppMail
import com.pms.PmMcrsLog
import com.pms.PmMcrsLogDetails
import com.pms.PmServiceSector
import grails.transaction.Transactional
import org.apache.log4j.Logger
import pms.ActionServiceIntf
import pms.BaseService
import pms.utility.DateUtility
import service.PmActionsService

@Transactional
class SubmitDashBoardActionService extends BaseService implements ActionServiceIntf {

    def mailService

    private static final String SAVE_SUCCESS_MESSAGE = "ED's Dashboard has been submitted successfully"
    private static final String ED_DASHBOARD = "ED's Dashboard"
    private static final String PM_MCRS_LOG = "pmMcrsLog"
    private static final String THANK_YOU_MAIL = "THANK_YOU_MAIL"
    private static final String THANK_YOU_MAIL_AFTER_DEADLINE = "THANK_YOU_MAIL_AFTER_DEADLINE"

    private Logger log = Logger.getLogger(getClass())
    PmActionsService pmActionsService

    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            long id = Long.parseLong(params.id.toString())
            PmMcrsLog pmMcrsLog = PmMcrsLog.read(id)
            boolean isSubmittable=pmActionsService.isDashboardSubmittable(pmMcrsLog.serviceId,pmMcrsLog.month,pmMcrsLog.year)
            if(!isSubmittable){
                return super.setError(params, "Must resolve or follow-up for issues from previous months.")
            }
            params.put(PM_MCRS_LOG, pmMcrsLog)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Transactional
    public Map execute(Map result) {
        try {
            PmMcrsLog pmMcrsLog = (PmMcrsLog) result.get(PM_MCRS_LOG)
            pmMcrsLog.isSubmittedDb = Boolean.TRUE
            pmMcrsLog.submissionDateDb = DateUtility.getSqlDate(new Date())
            pmMcrsLog.isEditableDb = Boolean.FALSE
            pmMcrsLog.save()

            PmMcrsLogDetails details = PmMcrsLogDetails.findByLogIdAndLogTypeIdAndIsCurrent(pmMcrsLog.id,2L, Boolean.TRUE)
            if(details){
                details.submittedOn = pmMcrsLog.submissionDateDb
                details.save()
            }

            /// send thank you mail
            PmServiceSector sc = PmServiceSector.read(pmMcrsLog.serviceId)
            if(DateUtility.getSqlFromDateWithSeconds(pmMcrsLog.submissionDate) <= DateUtility.getSqlFromDateWithSeconds(pmMcrsLog.deadLine)){
                AppMail appMail = AppMail.findByTransactionCodeAndIsActive(THANK_YOU_MAIL, true)
                if(appMail){
                    sendMail(sc.departmentHead,sc.contactEmail, THANK_YOU_MAIL,sc.departmentHeadGender,pmMcrsLog.deadLine,pmMcrsLog.monthStr,pmMcrsLog.year)
                }
            }else{
                AppMail appMail2 = AppMail.findByTransactionCodeAndIsActive(THANK_YOU_MAIL_AFTER_DEADLINE, true)
                if(appMail2){
                    sendMail(sc.departmentHead,sc.contactEmail, THANK_YOU_MAIL_AFTER_DEADLINE,sc.departmentHeadGender,pmMcrsLog.deadLine,pmMcrsLog.monthStr,pmMcrsLog.year)
                }
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }
    /**
     *
     * @param result - map received from execute method
     * @return - map
     */
    public Map executePostCondition(Map result) {
        return result
    }
    /**
     *
     * @param result - map received from executePost method
     * @return - map containing success message
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, SAVE_SUCCESS_MESSAGE)
    }
    /**
     *
     * @param result - map received from previous method
     * @return - map
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
    private String sendMail(String user,String email,String transactionCode, String genderStr,Date deadLine,String monthStr,int year) {
        AppMail appMail = AppMail.findByTransactionCodeAndIsActive(transactionCode, true)
        String subjectStr = """${appMail.subject}"""
        subjectStr = subjectStr?.replaceAll("__MCRS_TYPE__",ED_DASHBOARD)
        String mailBody = """${appMail.body}"""
        mailBody = mailBody?.replaceAll("__APP_USER__",user+SINGLE_SPACE+genderStr)
                .replaceAll("_MONTH_NAME_",monthStr+SINGLE_SPACE+year)
                .replaceAll("__DEADLINE__",DateUtility.getDateForUI(deadLine))
                .replaceAll("__MCRS_TYPE__",ED_DASHBOARD)

        Thread trd = new Thread() {
            public void run() {
                mailService.sendMail {
                    to "${email}"
                    from "sp.notification@friendship-bd.org"
                    subject "${subjectStr}"
                    html (mailBody)
                }
            }
        }.start();
        return null
    }
}
