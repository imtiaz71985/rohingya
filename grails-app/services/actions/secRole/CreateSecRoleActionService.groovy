package actions.secRole

import com.model.ListSecRoleActionServiceModel
import com.pms.SecRole
import grails.transaction.Transactional
import org.apache.log4j.Logger
import pms.ActionServiceIntf
import pms.BaseService
import service.FeatureManagementService
import service.SecRoleService

class CreateSecRoleActionService extends BaseService implements ActionServiceIntf {

    private static final String SAVE_SUCCESS_MESSAGE = "Role has been saved successfully"
    private static final String ALREADY_EXIST = "Same Role already exist"
    private static final String AUTHORITY_PREFIX = "ROLE_PMS_"
    private static final String SEC_ROLE = "secRole"

    private Logger log = Logger.getLogger(getClass())

    SecRoleService secRoleService
    FeatureManagementService featureManagementService
    /**
     * 1. input validation check
     * 2. duplicate check for role-name
     * @param params - receive role object from controller
     * @return - map.
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            //Check parameters
            if (!params.name) {
                return super.setError(params, INVALID_INPUT_MSG)
            }
            int duplicateCount = secRoleService.countByNameIlike(params.name.toString())
            if (duplicateCount > 0) {
                return super.setError(params, ALREADY_EXIST)
            }
            SecRole secRole = buildObject(params)
            params.put(SEC_ROLE, secRole)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }
    /**
     * 1. receive role object from pre execute method
     * 2. create new role
     * This method is in transactional block and will roll back in case of any exception
     * @param result - map received from pre execute method
     * @return - map.
     */
    @Transactional
    public Map execute(Map result) {
        try {
            SecRole secRole = (SecRole) result.get(SEC_ROLE)
            secRole.save()
            featureManagementService.addRoleToRoot(secRole.authority)
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
        SecRole secRole = (SecRole) result.get(SEC_ROLE)
        ListSecRoleActionServiceModel model = ListSecRoleActionServiceModel.read(secRole.id)
        result.put(SEC_ROLE, model)
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

    /**
     * Build role object
     * @param parameterMap -serialized parameters from UI
     * @return -new role object
     */
    private SecRole buildObject(Map parameterMap) {
        SecRole secRole = new SecRole(parameterMap)
        secRole.authority = AUTHORITY_PREFIX + secRole.name
        secRole.appsId = 1L
        return secRole
    }
}
