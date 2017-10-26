package com.model

class ListDecisionMakingActionServiceModel {

    public static final String MODEL_NAME = 'list_decision_making_action_service_model'
    public static final String SQL_LIST_DECISION_MAKING_MODEL = """
    CREATE OR REPLACE VIEW list_decision_making_action_service_model AS
          SELECT ed.id, ed.version,ed.create_by,e.name `owner`,ed.create_date,ed.issue_id,ed.month_for,ed.description,
          ed.remarks,ed.ed_advice,ed.is_resolve,ed.resolve_note,ed.is_followup,ed.followup_month_for,
          ed.service_id,ed.status_change_date,ed.type,ed.issue_name,ed.recipient_id,ed.advice_date,e2.name recipient
          FROM ed_dashboard ed
          LEFT JOIN mis.employee e ON e.id = ed.create_by
          LEFT JOIN mis.employee e2 ON e2.id = ed.recipient_id
          WHERE ed.type <> 1
          ORDER BY ed.month_for DESC;
    """

    long id
    long version
    long createBy
    Date createDate
    String owner
    long issueId
    Date monthFor
    String description
    String remarks
    String edAdvice
    long serviceId
    Boolean isResolve
    String resolveNote
    Boolean isFollowup
    Date followupMonthFor
    Date statusChangeDate
    int type
    String issueName
    long recipientId
    String recipient
    Date adviceDate

    static mapping = {
        cache usage: "read-only"
    }
}
