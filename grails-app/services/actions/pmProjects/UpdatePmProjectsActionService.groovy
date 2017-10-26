package actions.pmProjects

import com.model.ListPmGoalsActionServiceModel
import com.model.ListPmProjectsActionServiceModel
import com.pms.PmProjects
import com.pms.PmProjects
import grails.transaction.Transactional
import org.apache.log4j.Logger
import pms.ActionServiceIntf
import pms.BaseService
import pms.utility.DateUtility

import java.text.DateFormat
import java.text.SimpleDateFormat

@Transactional
class UpdatePmProjectsActionService extends BaseService implements ActionServiceIntf {

    private static final String UPDATE_SUCCESS_MESSAGE = "Project has been updated successfully"
    private static final String ALREADY_EXIST = "Project already exist"
    private static final String PROJECTS_OBJ = "pmProjects"

    private Logger log = Logger.getLogger(getClass())

    public Map executePreCondition(Map params) {
        try {
            if ((!params.typeId)||(!params.name)|| (!params.shortName)|| (!params.code)) {
                return super.setError(params, INVALID_INPUT_MSG)
            }
            long id = Long.parseLong(params.id.toString())
            PmProjects oldObject = PmProjects.read(id)
            PmProjects projects = buildObject(params,oldObject)
            params.put(PROJECTS_OBJ, projects)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Transactional
    public Map execute(Map result) {
        try {
            PmProjects projects = (PmProjects) result.get(PROJECTS_OBJ)
            projects.save()
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
     * @param result - map received from execute method
     * @return - map with success message
     */
    public Map buildSuccessResultForUI(Map result) {
        PmProjects projects = (PmProjects) result.get(PROJECTS_OBJ)
        ListPmProjectsActionServiceModel model = ListPmProjectsActionServiceModel.read(projects.id)
        result.put(PROJECTS_OBJ, model)
        return super.setSuccess(result, UPDATE_SUCCESS_MESSAGE)
    }

    /**
     *
     * @param result - map received from previous method
     * @return - map
     */
    public Map buildFailureResultForUI(Map params) {
        return params
    }

    private static PmProjects buildObject(Map parameterMap,PmProjects oldObject) {
        parameterMap.startDate=DateUtility.getSqlDate(DateUtility.parseMaskedDate(parameterMap.startDate.toString()))
        parameterMap.endDate=DateUtility.getSqlDate(DateUtility.parseMaskedDate(parameterMap.endDate.toString()))

        PmProjects projects = new PmProjects(parameterMap)
        oldObject.code = projects.code
        oldObject.name = projects.name
        oldObject.shortName = projects.shortName
        oldObject.donor = projects.donor
        oldObject.startDate = projects.startDate
        oldObject.endDate = projects.endDate
        oldObject.description = projects.description
        oldObject.typeId = projects.typeId
        oldObject.isActive=true
        return oldObject
    }
}
