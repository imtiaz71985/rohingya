package com.pms

class MeetingSchedule {

    long id
    long version
    long projectId
    long teamId
    String projectName
    String teamName
    String attendeesEmp
    String attendeesOthers
    String issues
    String descStr
    Date scheduledDate
    Boolean  isRecorded = Boolean.FALSE

    static mapping = {
        projectId     index: 'project_id_idx'
        descStr       sqlType: 'text'
    }

    static constraints = {
        attendeesEmp(nullable: true)
        attendeesOthers(nullable: true)
        issues(nullable: true)
        descStr(nullable: true)
        isRecorded(nullable: true)
    }
}
