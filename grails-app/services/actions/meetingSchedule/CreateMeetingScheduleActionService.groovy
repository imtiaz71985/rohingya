package actions.meetingSchedule

import com.model.ListMeetingScheduleActionServiceModel
import com.pms.AppMail
import com.pms.MeetingSchedule
import grails.transaction.Transactional
import org.apache.log4j.Logger
import pms.ActionServiceIntf
import pms.BaseService
import pms.utility.DateUtility
import service.MeetingLogService

import javax.servlet.ServletContext

@Transactional
class CreateMeetingScheduleActionService extends BaseService implements ActionServiceIntf {

    private static final String SAVE_SUCCESS_MESSAGE = "Schedule has been saved successfully"
    private static final String ALREADY_EXIST_WEEK = "Meeting already scheduled for this time"
    private static final String MEETING_SCHEDULE = "meetingSchedule"
    ServletContext servletContext
    MeetingLogService meetingLogService

    private Logger log = Logger.getLogger(getClass())

    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if (!params.projectId) {
                return super.setError(params, INVALID_INPUT_MSG)
            }
            Date date;
            date = DateUtility.parseMaskedDateTime(params.scheduledDate.toString());
            MeetingSchedule meetingSchedule = MeetingSchedule.findByScheduledDate(date)
            if (meetingSchedule) {
                return super.setError(params, ALREADY_EXIST_WEEK)
            }

            MeetingSchedule meetingSchedule1 = buildObject(params)
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
            meetingLogService.sendMail(contactEmail, meetingSchedule)

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
        MeetingSchedule meetingSchedule = (MeetingSchedule) result.get(MEETING_SCHEDULE)
        ListMeetingScheduleActionServiceModel model = ListMeetingScheduleActionServiceModel.read(meetingSchedule.id)
        result.put(MEETING_SCHEDULE, model)
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

    private static MeetingSchedule buildObject(Map parameterMap) {
        parameterMap.scheduledDate = DateUtility.getSqlDateWithSeconds(DateUtility.parseMaskedDateTime(parameterMap.scheduledDate.toString()))
        MeetingSchedule meetingSchedule = new MeetingSchedule(parameterMap)
        return meetingSchedule
    }
}
