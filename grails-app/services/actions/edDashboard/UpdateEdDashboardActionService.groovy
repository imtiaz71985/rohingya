package actions.edDashboard

import com.pms.EdDashboard
import com.pms.PmMcrsLog
import com.pms.SecUser
import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import org.apache.log4j.Logger
import pms.ActionServiceIntf
import pms.BaseService
import pms.utility.DateUtility
import service.EdDashboardService

import java.text.DateFormat
import java.text.SimpleDateFormat

@Transactional
class UpdateEdDashboardActionService extends BaseService implements ActionServiceIntf {

    SpringSecurityService springSecurityService
    private static final String SAVE_SUCCESS_MESSAGE = "Issue has been updated successfully"
    private static final String ADVICE_SUCCESS_MESSAGE = "Advice added successfully"
    private static final String ALREADY_EXIST = "Issue already exist"
    private static final String ED_DASHBOARD = "edDashboard"

    private Logger log = Logger.getLogger(getClass())
    BaseService baseService
    EdDashboardService edDashboardService


    @Transactional
    public Map executePreCondition(Map params) {
        try {
            if (!params.type) {
                if (!params.hfId && !params.description && !params.remarks) {
                    return super.setError(params, INVALID_INPUT_MSG)
                }
                else if(params.hfId=='' || params.description.trim()=='' || params.remarks.trim()==''){
                    return super.setError(params, INVALID_INPUT_MSG)
                }
            }
            else {
                if(params.type=='EdAdvice'){
                    EdDashboard dashboard = EdDashboard.read(Long.parseLong(params.hfDashboardId))
                    dashboard.edAdvice = params.edAdvice
                    dashboard.save()
                    return params
                }else {
                    if (!params.hfClickingRowNo && !params.selection) {
                        return super.setError(params, INVALID_INPUT_MSG)
                    } else if (params.selection != 'Resolve' && !params.remarks && !params.followupMonth) {
                        return super.setError(params, INVALID_INPUT_MSG)
                    } else if (params.selection != 'Resolve' && (params.remarks.trim() == '' || params.description.trim() == '')) {
                        return super.setError(params, 'Invalid current remarks and recommendations.')
                    }
                }
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
            if(result.type=='EdAdvice'){
                return result
            }
            if (!result.type) {
                Long id = Long.parseLong(result.hfId)
                EdDashboard edDashboard = EdDashboard.findById(id)

                edDashboard.description = result.description
                edDashboard.remarks = result.remarks

                edDashboard.save()
            } else {
                Long id = Long.parseLong(result.hfClickingRowNo)
                long serviceId = Long.parseLong(result.hfServiceIdModal.toString())

                DateFormat originalFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
                Calendar c = Calendar.getInstance();
                Date start = originalFormat.parse(result.hfMonthModal.toString());
                c = Calendar.getInstance();
                c.setTime(start);
                c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
                Date statusChangeDate = DateUtility.getSqlDate(c.getTime())

                EdDashboard edDashboardOld = EdDashboard.findById(id)
                String subDate = baseService.lastDashboardSubmissionDate(serviceId)
                long oldId = edDashboardService.ExistedInFutureDate(serviceId, edDashboardOld.monthFor, subDate,edDashboardOld.issueId)
                if (oldId > 0) {
                    EdDashboard edDashboardOld2 = EdDashboard.findById(oldId)
                    edDashboardOld2.delete()
                }
                if (result.selection == 'Resolve') {
                    edDashboardOld.isResolve = true
                    edDashboardOld.isFollowup = false
                    edDashboardOld.followupMonthFor = null
                    edDashboardOld.statusChangeDate = statusChangeDate
                    edDashboardOld.resolveNote = result.resolveNote
                    edDashboardOld.save()
                }
                else {
                    edDashboardOld.isResolve = false
                    edDashboardOld.isFollowup = false
                    edDashboardOld.followupMonthFor = null
                    edDashboardOld.statusChangeDate = null
                    edDashboardOld.save()

                    String followupDateStr = result.followupMonth
                    start = originalFormat.parse(followupDateStr);
                    c = Calendar.getInstance();
                    c.setTime(start);
                    c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
                    Date monthFor = DateUtility.getSqlDate(c.getTime())

                    EdDashboard edDashboard = new EdDashboard()

                    edDashboard.serviceId = serviceId
                    edDashboard.monthFor = monthFor
                    edDashboard.issueId = edDashboardOld.issueId
                    edDashboard.description = result.description
                    edDashboard.remarks = result.remarks
                    edDashboard.createBy = springSecurityService?.principal?.id
                    edDashboard.createDate = DateUtility.getSqlDate(new Date())
                    edDashboard.edAdvice = EMPTY_SPACE
                    edDashboard.isFollowup = true
                    edDashboard.isResolve = false
                    if (edDashboard.isFollowup) {
                        edDashboard.followupMonthFor = edDashboardOld.monthFor
                    }
                    edDashboard.save()
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
        if(result.containsKey("models[0][ID]")){
            return super.setSuccess(result, ADVICE_SUCCESS_MESSAGE)
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
