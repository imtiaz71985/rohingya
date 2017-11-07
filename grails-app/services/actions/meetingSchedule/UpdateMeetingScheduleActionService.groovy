package actions.meetingSchedule

import com.model.ListMeetingScheduleActionServiceModel
import com.pms.AppMail
import com.pms.MeetingSchedule
import grails.plugin.mail.MailService
import grails.transaction.Transactional
import org.apache.log4j.Logger
import pms.ActionServiceIntf
import pms.BaseService
import pms.utility.DateUtility
import service.MeetingLogService

@Transactional
class UpdateMeetingScheduleActionService extends BaseService implements ActionServiceIntf {

    MailService mailService
    private static final String UPDATE_SUCCESS_MESSAGE = "Schedule has been updated successfully"
    private static final String ALREADY_EXIST_WEEK = "Meeting already scheduled for this time"
    private static final String MEETING_SCHEDULE = "meetingSchedule"
    MeetingLogService meetingLogService

    private Logger log = Logger.getLogger(getClass())

    public Map executePreCondition(Map params) {
        try {
            if (!params.projectId) {
                return super.setError(params, INVALID_INPUT_MSG)
            }
            long id = Long.parseLong(params.id.toString())
            Date date = DateUtility.parseMaskedDateTime(params.scheduledDate.toString());

            MeetingSchedule meetingSchedule = MeetingSchedule.findByScheduledDateAndIdNotEqual(date,id)

            if (meetingSchedule) {
                return super.setError(params, ALREADY_EXIST_WEEK)
            }

            MeetingSchedule oldObject = MeetingSchedule.read(id)
            MeetingSchedule meetingSchedule1 = buildObject(params, oldObject)

            params.put(MEETING_SCHEDULE, meetingSchedule1)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Transactional
    public Map execute(Map result) {
        try {
            MeetingSchedule meetingSchedule = (MeetingSchedule) result.get(MEETING_SCHEDULE)
            meetingSchedule.save()
            String contactEmail = meetingLogService.getAttendeesOfficialEmail(meetingSchedule.attendeesEmp)
            if (contactEmail!= null){
                meetingLogService.sendMail(contactEmail, meetingSchedule)
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
     * @param result - map received from execute method
     * @return - map with success message
     */
    public Map buildSuccessResultForUI(Map result) {
        MeetingSchedule meetingSchedule = (MeetingSchedule) result.get(MEETING_SCHEDULE)
        ListMeetingScheduleActionServiceModel model = ListMeetingScheduleActionServiceModel.read(meetingSchedule.id)
        result.put(MEETING_SCHEDULE, model)
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

    private static MeetingSchedule buildObject(Map parameterMap,MeetingSchedule oldObject) {
        parameterMap.scheduledDate = DateUtility.getSqlDateWithSeconds(DateUtility.parseMaskedDateTime(parameterMap.scheduledDate.toString()))
        MeetingSchedule meetingSchedule = new MeetingSchedule(parameterMap)
        oldObject.projectId = meetingSchedule.projectId
        oldObject.teamId=meetingSchedule.teamId
        oldObject.projectName=meetingSchedule.projectName
        oldObject.teamName=meetingSchedule.teamName
        oldObject.attendeesOthers = meetingSchedule.attendeesOthers
        oldObject.attendeesEmp = meetingSchedule.attendeesEmp
        oldObject.scheduledDate = meetingSchedule.scheduledDate
        oldObject.issues = meetingSchedule.issues
        oldObject.descStr = meetingSchedule.descStr
        return oldObject
    }

}
