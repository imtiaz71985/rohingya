package actions.decisionMaking

import com.model.ListDecisionMakingActionServiceModel
import grails.transaction.Transactional
import org.apache.log4j.Logger
import pms.ActionServiceIntf
import pms.BaseService

@Transactional
class ListDecisionMakingActionService  extends BaseService implements ActionServiceIntf {

    def mailService

    private Logger log = Logger.getLogger(getClass())

    public Map executePreCondition(Map params) {
        return params
    }

    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            Map resultMap = new HashMap()
            boolean isSysAdmin = isUserSystemAdmin(springSecurityService.principal.id)
            if(!isSysAdmin){
                long empId = currentUserEmployeeId()
                if(result.type.equals("givenDecision")){
                    Closure additionalParam = {
                        'eq'('recipientId', empId)
                        'eq'('isResolve', Boolean.parseBoolean(result.isResolved.toString()))
                    }
                    resultMap = super.getSearchResult(result, ListDecisionMakingActionServiceModel.class,additionalParam)
                }else{
                    Closure additionalParam = {
                        'eq'('createBy', empId)
                        'eq'('isResolve', Boolean.parseBoolean(result.isResolved.toString()))
                    }
                    resultMap = super.getSearchResult(result, ListDecisionMakingActionServiceModel.class,additionalParam)
                }
            }else{
                resultMap = super.getSearchResult(result, ListDecisionMakingActionServiceModel.class)
            }

            result.put(LIST, resultMap.list)
            result.put(COUNT, resultMap.count)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     *
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Since there is no success message return the same map
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
