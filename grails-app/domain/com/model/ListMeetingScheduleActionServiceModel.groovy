package com.model

class ListMeetingScheduleActionServiceModel {

    public static final String MODEL_NAME = 'list_meeting_schedule_action_service_model'
    public static final String SQL_LIST_MEETING_LOG_MODEL = """
       CREATE OR REPLACE VIEW list_meeting_schedule_action_service_model AS
          SELECT ms.id, ms.version,ms.project_id,ms.team_id,ms.project_name,ms.team_name, ms.scheduled_date,ms.desc_str,ms.issues,
          ms.attendees_others,ms.attendees_emp,ms.is_recorded,
              (SELECT GROUP_CONCAT(NAME SEPARATOR ', ') FROM mis.employee
          WHERE LOCATE(CONCAT(',',id,',') ,CONCAT(',',ms.attendees_emp,', '))>0 ) attendees_emp_str

          FROM meeting_schedule ms
          LEFT JOIN login_auth.sec_user u ON u.employee_id = ms.attendees_emp
          ORDER BY ms.scheduled_date DESC;
    """

    long id
    long version
    long projectId
    long teamId
    String projectName
    String teamName
    String attendeesEmp
    String attendeesEmpStr
    String attendeesOthers
    String issues
    String descStr
    Date scheduledDate
    Boolean isRecorded


    static mapping = {
        cache usage: "read-only"
    }
}
