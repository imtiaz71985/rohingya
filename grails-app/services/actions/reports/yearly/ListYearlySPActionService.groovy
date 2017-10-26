package actions.reports.yearly

import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import pms.ActionServiceIntf
import pms.BaseService

@Transactional
class ListYearlySPActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    /**
     * No pre conditions required for searching project domains
     *
     * @param params - Request parameters
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        return params
    }

    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            int year = Integer.parseInt(result.year.toString())
            long serviceId = Long.parseLong(result.serviceId.toString())
            if(result.containsKey("indicatorType")){
                List lstVal = buildResultList(serviceId, year,result.indicatorType.toString())
                result.put(LIST, lstVal)
                result.put(COUNT, lstVal.size())
                return result
            }
            List lstVal = buildResultList(serviceId, year)
            result.put(LIST, lstVal)
            result.put(COUNT, lstVal.size())
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

    private List<GroovyRowResult> buildResultList(long serviceId,int year) {

        String query = """
        SELECT a.id AS id,CAST(CONCAT(g.sequence,'. ',g.goal)AS CHAR CHARACTER SET utf8) goal,
        a.service_id AS serviceId,a.goal_id,a.id action_id,a.sequence,a.actions,a.start,a.end,
        COALESCE(GROUP_CONCAT(CONCAT('<strike>',DATE_FORMAT(aeh.end,'%M'),'</strike>') SEPARATOR' '),'') extendedEnd,
        a.note remarks,SUBSTRING_INDEX(a.res_person,'(',1) AS responsiblePerson,
        (SELECT GROUP_CONCAT(short_name SEPARATOR ', ') FROM pm_projects WHERE LOCATE(CONCAT(',',id,',') ,CONCAT(',',a.source_of_fund,', '))>0 ) project,
        (SELECT GROUP_CONCAT(short_name SEPARATOR ', ') FROM pm_service_sector WHERE LOCATE(CONCAT(',',id,',') ,CONCAT(',',a.support_department,','))>0 ) supportDepartment

        FROM pm_actions a
        JOIN pm_goals g ON g.id = a.goal_id
        JOIN pm_service_sector sc ON sc.id = a.service_id
        LEFT JOIN pm_actions_extend_history aeh ON aeh.actions_id=a.id
        WHERE a.year = ${year} AND sc.id = ${serviceId} GROUP BY a.id
        ORDER BY sc.id,a.year, a.goal_id ,a.tmp_seq;
        """
        List<GroovyRowResult> lstValue = executeSelectSql(query)
        return lstValue
    }

    private List<GroovyRowResult> buildResultList(long serviceId,int year,String type) {
        String actionIndicatorJoin = "JOIN pm_actions_indicator ai ON ai.actions_id = a.id"
        if(type.equals("Action Indicator")){
            actionIndicatorJoin = "JOIN (SELECT pai.* FROM pm_actions_indicator pai " +
                    "JOIN (SELECT MIN(id) id  FROM pm_actions_indicator GROUP BY actions_id ) tmp ON pai.id=tmp.id) ai ON ai.actions_id = a.id"
        }else if(type.equals("Preferred Indicator")){
            actionIndicatorJoin = "JOIN pm_actions_indicator ai ON ai.year = ${year} AND ai.actions_id = a.id AND ai.is_preference = TRUE"
        }

        String query = """
        SELECT @rownum := @rownum + 1 AS id,CAST(CONCAT(g.sequence,'. ',g.goal)AS CHAR CHARACTER SET utf8) goal,
        a.service_id AS serviceId,a.goal_id,a.id action_id,a.sequence,a.actions,a.start,a.end,
        COALESCE((SELECT GROUP_CONCAT(CONCAT('<strike>',CAST(DATE_FORMAT(END,'%M') AS CHAR(50)),'</strike>') SEPARATOR' ')
        FROM pm_actions_extend_history WHERE actions_id=a.id),'') extendedEnd,
        ai.id AS indicator_id,ai.indicator,ai.indicator_type,

         CASE WHEN  ai.indicator_type LIKE 'Repeatable%' THEN SUM(CASE WHEN idd.is_excluded<>TRUE THEN COALESCE(idd.target,0)ELSE 0 END) ELSE COALESCE(ai.target,0) END tot_tar_Sum,
        SUM(COALESCE(idd.achievement,0)) tot_acv_sum,
        COALESCE(ai.target,0) tot_tar,
        CASE WHEN  ai.indicator_type LIKE 'Repeatable%' THEN
                 ROUND((100*SUM(COALESCE(idd.achievement,0)))/SUM(CASE WHEN idd.is_excluded<>TRUE THEN COALESCE(idd.target,0)ELSE 0 END))
                 ELSE SUM(COALESCE(idd.achievement,0))  END tot_acv,

        a.note remarks,SUBSTRING_INDEX(a.res_person,'(',1) AS responsiblePerson,
        (SELECT GROUP_CONCAT(short_name SEPARATOR ', ') FROM pm_projects WHERE LOCATE(CONCAT(',',id,',') ,CONCAT(',',a.source_of_fund,', '))>0 ) project,
        (SELECT GROUP_CONCAT(short_name SEPARATOR ', ') FROM pm_service_sector WHERE LOCATE(CONCAT(',',id,',') ,CONCAT(',',a.support_department,','))>0 ) supportDepartment

        FROM pm_actions a
        JOIN pm_goals g ON g.id = a.goal_id
        ${actionIndicatorJoin}
        JOIN pm_actions_indicator_details idd ON idd.indicator_id = ai.id
        JOIN custom_month cm ON cm.name=idd.month_name
        JOIN pm_service_sector sc ON sc.id = a.service_id,
        (SELECT @rownum := 0) r
        WHERE a.year=${year} AND ai.year = ${year} AND sc.id = ${serviceId}
        GROUP BY a.id,ai.id
        ORDER BY sc.id,a.year, a.goal_id ,a.tmp_seq;
        """
        List<GroovyRowResult> lstValue = executeSelectSql(query)
        return lstValue
    }
}
