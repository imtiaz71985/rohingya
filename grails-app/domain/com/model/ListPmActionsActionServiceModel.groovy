package com.model

class ListPmActionsActionServiceModel {
    public static final String MODEL_NAME = 'list_pm_actions_action_service_model'

    public static final String SQL_LIST_ACTIONS_MODEL = """
             CREATE OR REPLACE VIEW list_pm_actions_action_service_model AS

             SELECT d.id, d.version,d.actions,d.is_editable, g.id AS goal_id, g.goal,d.res_person_id,d.start, d.end
             ,COALESCE(GROUP_CONCAT(CONCAT('<strike>',DATE_FORMAT(aeh.end,'%M'),'</strike>') SEPARATOR' '),'') extended_end,d.sequence, d.tmp_seq, sc.id AS service_id,sc.name AS service,
             d.indicator,sc.short_name AS ser_short_name, d.res_person,d.strategy_map_ref, d.source_of_fund,d.note,d.support_department,d.total_indicator,
            (SELECT GROUP_CONCAT(short_name SEPARATOR ', ') FROM pm_service_sector WHERE LOCATE(CONCAT(',',id,',') ,CONCAT(',',d.support_department,', '))>0 ) support_department_str,
            (SELECT GROUP_CONCAT(short_name SEPARATOR ', ') FROM pm_projects WHERE LOCATE(CONCAT(',',id,',') ,CONCAT(',',d.source_of_fund,', '))>0 ) source_of_fund_str

            FROM  pm_service_sector  sc
             JOIN pm_goals g ON  sc.id = g.service_id
             JOIN pm_actions d ON  g.id =d.goal_id
             LEFT JOIN pm_actions_extend_history aeh ON aeh.actions_id=d.id
            GROUP BY  sc.id,g.id,EXTRACT(YEAR FROM d.start), d.tmp_seq ,d.id
            ORDER BY sc.id,EXTRACT(YEAR FROM d.start) , g.id ,d.tmp_seq;
    """

    long id
    long version
    long serviceId
    long goalId
    long resPersonId
    int totalIndicator
    String service
    String serShortName
    String goal
    String actions
    boolean isEditable
    String sequence
    int tmpSeq
    String resPerson
    String strategyMapRef
    String supportDepartment
    String supportDepartmentStr
    String sourceOfFund
    String sourceOfFundStr
    String note
    String indicator
    Date start
    Date end
    String extendedEnd

    static constraints = {
    }
}
