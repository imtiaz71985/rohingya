package actions.decisionMaking

import com.model.ListDecisionMakingActionServiceModel
import com.pms.EdDashboard
import com.pms.EdDashboardIssues
import com.pms.SecUser
import grails.transaction.Transactional
import org.apache.log4j.Logger
import pms.ActionServiceIntf
import pms.BaseService
import pms.utility.DateUtility

@Transactional
class CreateDecisionMakingActionService extends BaseService implements ActionServiceIntf {

    private static final String SAVE_SUCCESS_MESSAGE = "Issue has been saved successfully"
    private static final String DECISION_MAKING = "decisionMaking"
    private static final String CUSTOM_ISSUE = "Custom Issue for Decision making"

    private Logger log = Logger.getLogger(getClass())


    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if (!params.recipientId && !params.issueName && !params.description && !params.monthFor) {
                return super.setError(params, INVALID_INPUT_MSG)
            }
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Transactional
    public Map execute(Map result) {
        try {
            SecUser user = currentUserObject()
            long empId = currentUserEmployeeId()

            EdDashboardIssues issues = EdDashboardIssues.findByIssueNameIlike(CUSTOM_ISSUE)

            EdDashboard edDashboard = new EdDashboard()
            edDashboard.serviceId = user.serviceId
            edDashboard.monthFor = DateUtility.getSqlDate(DateUtility.parseMaskedDate(result.monthFor.toString()))
            edDashboard.issueId = issues.id
            edDashboard.issueName = result.issueName
            edDashboard.description = result.description
            edDashboard.remarks = result.remarks
            edDashboard.createBy = empId
            edDashboard.createDate = DateUtility.getSqlDateWithSeconds(new Date())
            edDashboard.edAdvice = EMPTY_SPACE
            edDashboard.isFollowup=false
            edDashboard.isResolve=false
            edDashboard.type = 2
            edDashboard.recipientId =  Long.parseLong(result.recipientId.toString())
            edDashboard.save()

            result.put(DECISION_MAKING, edDashboard)
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
        EdDashboard edDashboard = (EdDashboard) result.get(DECISION_MAKING)
        ListDecisionMakingActionServiceModel model = ListDecisionMakingActionServiceModel.read(edDashboard.id)
        result.put(DECISION_MAKING, model)
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

}
