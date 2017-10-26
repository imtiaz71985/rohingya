package actions.decisionMaking

import com.model.ListDecisionMakingActionServiceModel
import com.pms.EdDashboard
import grails.transaction.Transactional
import org.apache.log4j.Logger
import pms.ActionServiceIntf
import pms.BaseService
import pms.utility.DateUtility

@Transactional
class UpdateDecisionMakingActionService extends BaseService implements ActionServiceIntf {

    private static final String SAVE_SUCCESS_MESSAGE = "Issue has been updated successfully"
    private static final String ADVICE_SUCCESS_MESSAGE = "Advice added successfully"
    private static final String RESOLVE_SUCCESS_MESSAGE = "Issue resolve successfully"
    private static final String NOT_FOR_DELETE = "After getting feedback from others not possible to delete"
    private static final String NOT_FOR_DELETE_AS_RESOLVED = "Issue already resolved"
    private static final String DECISION_MAKING = "decisionMaking"

    private Logger log = Logger.getLogger(getClass())

    @Transactional
    public Map executePreCondition(Map params) {
        try {
            if (!params.id && !params.recipientId && !params.issueName && !params.description && !params.monthFor) {
                return super.setError(params, INVALID_INPUT_MSG)
            }

            if (params.containsKey("advicePanel") && !params.edAdvice) {
                return super.setError(params, INVALID_INPUT_MSG)
            }
            if (!params.containsKey("advicePanel") && params.edAdvice && !params.containsKey("resolvePanel")) {
                return super.setError(params, NOT_FOR_DELETE)
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
            long empId = currentUserEmployeeId()
            long id = Long.parseLong(result.id.toString())
            EdDashboard edDashboard = EdDashboard.read(id)
            if (result.containsKey("resolvePanel")) {
                edDashboard.isResolve = Boolean.TRUE
                edDashboard.statusChangeDate = DateUtility.getSqlDateWithSeconds(new Date())
                edDashboard.save()
            }else{
                if(result.containsKey("advicePanel") && edDashboard.recipientId==empId){
                    if(edDashboard.isResolve){
                        return super.setError(result, NOT_FOR_DELETE_AS_RESOLVED)
                    }
                    edDashboard.edAdvice = result.edAdvice
                    edDashboard.adviceDate = DateUtility.getSqlDateWithSeconds(new Date())
                    edDashboard.save()
                }else{
                    if(edDashboard.edAdvice.equals(EMPTY_SPACE) || edDashboard.edAdvice.equals(null)){
                        return super.setError(result, NOT_FOR_DELETE)
                    }
                    edDashboard.issueName = result.issueName
                    edDashboard.recipientId = Long.parseLong(result.recipientId.toString())
                    edDashboard.description = result.description
                    edDashboard.remarks = result.remarks
                    edDashboard.monthFor = DateUtility.getSqlDate(DateUtility.parseMaskedDate(result.monthFor.toString()))
                    edDashboard.save()
                }
            }

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
        long empId = currentUserEmployeeId()
        EdDashboard edDashboard = (EdDashboard) result.get(DECISION_MAKING)
        ListDecisionMakingActionServiceModel model = ListDecisionMakingActionServiceModel.read(edDashboard.id)
        result.put(DECISION_MAKING, model)
        if(edDashboard.recipientId==empId){
            return super.setSuccess(result, ADVICE_SUCCESS_MESSAGE)
        }
        if (result.containsKey("resolvePanel")) {
            return super.setSuccess(result, RESOLVE_SUCCESS_MESSAGE)
        }
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
