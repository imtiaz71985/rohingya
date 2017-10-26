package service

import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import pms.BaseService
import pms.utility.DateUtility

import java.text.DateFormat
import java.text.SimpleDateFormat

@Transactional
class EdDashboardService  extends BaseService{

    public List<GroovyRowResult> lstEdDashboardIssue(long serviceId) {
        long userServiceId = currentUserObject().serviceId
        String serviceString=''
        if(serviceId>0){
            serviceString=' where ed.service_id='+ serviceId
        }
        String queryForList = """
        SELECT ed.id ,ed.version, ed.service_id AS serviceId,ss.short_name AS serShortName,ed.month_for AS monthFor,edi.id AS issueId,edi.issue_name ,
       ed.description,ed.remarks,ed.ed_advice,ed.followup_month_for AS followupMonthFor,ed.is_followup AS isFollowup,edi.is_heading,edi.is_additional,lg.is_submitted
        FROM ed_dashboard_issues edi
        RIGHT JOIN ed_dashboard ed ON ed.issue_id=edi.id  AND ed.`type` = 1
        LEFT JOIN pm_mcrs_log lg ON lg.service_id = ed.service_id AND lg.month =MONTH(ed.month_for) AND lg.year = YEAR(ed.month_for)
        LEFT JOIN pm_service_sector ss ON ed.service_id=ss.id
        """+serviceString+"""
        ORDER BY ed.service_id ASC,ed.month_for DESC,edi.id ASC;

        """
        List<GroovyRowResult>  lst = executeSelectSql(queryForList)
        return lst
    }
    public List<GroovyRowResult> lstEdDashboardIssue(long serviceId,String d ) {
        DateFormat originalFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        Date start = originalFormat.parse(d.toString());
        Calendar c = Calendar.getInstance();
        c.setTime(start);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date month = DateUtility.getSqlDate(c.getTime())
        String queryForList = """
        SELECT ed.id ,ed.version,edi.issue_name as issueName ,ed.description,ed.remarks,ed.ed_advice as edAdvice
        FROM  ed_dashboard_issues edi
        RIGHT JOIN ed_dashboard ed ON ed.issue_id=edi.id  AND ed.`type` = 1
        AND ed.service_id = ${serviceId} AND DATE(ed.month_for)=DATE('${month}')
        WHERE edi.is_heading<>1 AND is_followup<>1
        ORDER BY edi.id
        """
        List<GroovyRowResult>  lst = executeSelectSql(queryForList)
        return lst
    }
    public List<GroovyRowResult> lstEdDashboardResolvedIssue(long serviceId) {
        String queryForList = """
        SELECT ed.id ,ed.version,edi.issue_name AS issueName ,DATE_FORMAT(month_for, '%M %Y') month,
        DATE_FORMAT(status_change_date, '%M %Y') resolvedMonth,ed.description,ed.remarks,ed.ed_advice AS edAdvice
        ,CASE WHEN DATE(ed.status_change_date) > (SELECT  DATE(CONCAT(YEAR,'-',MONTH,'-01')) FROM pm_mcrs_log WHERE
            service_id=ed.service_id  AND is_submitted_db=TRUE
            ORDER BY MONTH DESC,YEAR DESC LIMIT 1) THEN TRUE ELSE FALSE END AS isEditable

            FROM  ed_dashboard_issues edi
            RIGHT JOIN ed_dashboard ed ON ed.issue_id=edi.id AND ed.service_id = ${serviceId}  AND ed.`type` = 1
        WHERE  service_id = ${serviceId} AND is_resolve = TRUE
        ORDER BY edi.id
        """
        List<GroovyRowResult>  lst = executeSelectSql(queryForList)
        return lst
    }
    public List<GroovyRowResult> lstEdDashboardUpcomingFollowUpIssue(long serviceId,String d ) {
        DateFormat originalFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        Date start = originalFormat.parse(d.toString());
        Calendar c = Calendar.getInstance();
        c.setTime(start);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date month = DateUtility.getSqlDate(c.getTime())
        String queryForList = """
        SELECT ed1.id,ed.version,edi.issue_name AS issueName,DATE_FORMAT(ed.month_for, '%M %Y') month
        ,DATE_FORMAT(ed.followup_month_for, '%M %Y') followupFor,ed.description,ed.remarks,ed.ed_advice AS edAdvice
        FROM  ed_dashboard_issues edi
        RIGHT JOIN ed_dashboard ed ON ed.issue_id=edi.id AND ed.service_id =  ${serviceId}  AND ed.`type` = 1
        INNER JOIN ed_dashboard ed1 ON ed.followup_month_for=ed1.month_for AND ed1.is_followup <>1  AND ed1.`type` = 1
        AND ed1.service_id = ed.service_id AND ed.issue_id=ed1.issue_id
        WHERE ed.service_id = ${serviceId}  AND ed.month_for > DATE('${month}')
        ORDER BY edi.id
        """
        List<GroovyRowResult>  lst = executeSelectSql(queryForList)
        return lst
    }
    public List<GroovyRowResult> lstEdDashboardSectorOrCSUIssue(long serviceId,Date month) {
        String queryForList = """
        SELECT id,issue_name AS name FROM ed_dashboard_issues WHERE is_additional<>TRUE AND id NOT IN
            (SELECT issue_id FROM ed_dashboard WHERE service_id=${serviceId}  AND `type` = 1 AND MONTH(month_for)=MONTH('${month}') AND YEAR(month_for)=YEAR('${month}')  AND is_followup<>TRUE AND is_resolve<>TRUE)
        ORDER BY id ASC
        """
        List<GroovyRowResult>  lst = executeSelectSql(queryForList)
        return lst
    }
    public List<GroovyRowResult> lstEdDashboardDescriptionAndRemarks(long serviceId,Date month,long issuesId) {
        String queryForList = """
        SELECT DATE_FORMAT(month_for,'%M %Y') initiated_on,DATE_FORMAT(status_change_date,'%M %Y') resolved_on,resolve_note,
            GROUP_CONCAT(CONCAT('<FONT color="#0e65f2">',MONTHNAME(month_for),':</FONT>\\<br />\\<b> Remarks: \\</b>',remarks,'\\<br />\\<b>ED\\'s Advice: \\</b>',ed_advice)SEPARATOR'\\<br />') AS remarks
            FROM (SELECT resolve_note,month_for,remarks,ed_advice,description,status_change_date FROM ed_dashboard
            WHERE service_id=${serviceId}  AND `type` = 1 AND month_for=DATE('${month}') AND issue_id='${issuesId}' AND is_followup<>TRUE AND
               month_for<=(SELECT  DATE(CONCAT(YEAR,'-',MONTH,'-01')) FROM pm_mcrs_log WHERE service_id=${serviceId}  AND is_submitted_db=TRUE
            ORDER BY MONTH DESC,YEAR DESC LIMIT 1)
             UNION
             SELECT resolve_note,month_for,remarks,ed_advice,description,status_change_date FROM ed_dashboard
            WHERE service_id=${serviceId}  AND `type` = 1 AND followup_month_for=DATE('${month}')AND issue_id='${issuesId}' AND is_followup=TRUE AND
               month_for<=(SELECT  DATE(CONCAT(YEAR,'-',MONTH,'-01')) FROM pm_mcrs_log WHERE service_id=${serviceId}  AND is_submitted_db=TRUE
            ORDER BY MONTH DESC,YEAR DESC LIMIT 1)
                         ORDER BY month_for ASC  ) tbl GROUP BY description
        """
        /*String queryForList = """
        SELECT DATE_FORMAT(month_for,'%M %Y') initiated_on,DATE_FORMAT(status_change_date,'%M %Y') resolved_on,resolve_note,
            GROUP_CONCAT(CONCAT('<FONT color="#0e65f2">',MONTHNAME(month_for),':</FONT>\\<br />\\<b> Remarks: \\</b>',remarks,'\\<br />\\<b>ED\\'s Advice: \\</b>',ed_advice)SEPARATOR'\\<br />') AS remarks
            FROM (SELECT resolve_note,month_for,remarks,ed_advice,description,status_change_date FROM ed_dashboard
            WHERE service_id=${serviceId} AND (month_for=DATE('${month}') OR followup_month_for=DATE('${month}')) AND issue_id='${issuesId}' AND
               month_for<=(SELECT  DATE(CONCAT(YEAR,'-',MONTH,'-01')) FROM pm_mcrs_log WHERE service_id=${serviceId}  AND is_submitted_db=TRUE
            ORDER BY MONTH DESC,YEAR DESC LIMIT 1)
             ORDER BY month_for ASC ) tbl GROUP BY description
        """*/

        boolean l=executeSql("SET SESSION group_concat_max_len = 1000000")
        List<GroovyRowResult>  lst = executeSelectSql(queryForList)
        return lst
    }
    public long minimumAdditionalIssuesId(long serviceId,Date month) {
        String query = """
        SELECT CASE WHEN COALESCE(MAX(ed.issue_id),0)>6 THEN MAX(ed.issue_id)+1 ELSE MIN(edi.id) END AS cnt
        FROM ed_dashboard_issues edi
        LEFT JOIN ed_dashboard ed ON edi.id=ed.issue_id  AND ed.`type` = 1
        AND ed.service_id=${serviceId} AND MONTH(ed.month_for)=MONTH('${month}')
        AND YEAR(ed.month_for)=YEAR('${month}') AND ed.is_followup<>1
        WHERE  edi.is_additional<>0
        """
        List<GroovyRowResult> max = executeSelectSql(query)
        long con = max[0].cnt
        return con
    }
    public List<GroovyRowResult> lstUnresolveEdDashboardIssue(long serviceId,String d ) {
        DateFormat originalFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        Date start = originalFormat.parse(d.toString());
        Calendar c = Calendar.getInstance();
        c.setTime(start);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date month = DateUtility.getSqlDate(c.getTime())
        /*String queryForList = """
       SELECT ed.id ,ed.version,edi.issue_name ,ed.is_resolve
        ,CASE WHEN (SELECT COUNT(id)FROM ed_dashboard WHERE followup_month_for=ed.month_for AND service_id=ed.service_id AND issue_id=ed.issue_id GROUP BY followup_month_for)>0 THEN true ELSE
         ed.is_followup END AS is_followup,(SELECT MAX(month_for) FROM ed_dashboard WHERE followup_month_for=ed.month_for AND service_id=ed.service_id AND issue_id=ed.issue_id) AS followup_month_for,CONCAT(MONTHNAME(ed.month_for),' ',YEAR(ed.month_for)) AS issuedMonthStr
        ,CASE WHEN (SELECT COUNT(id)FROM ed_dashboard WHERE followup_month_for=ed.month_for AND service_id=ed.service_id AND issue_id=ed.issue_id AND ed.month_for>DATE('${month}') GROUP BY followup_month_for)>0 THEN 'Follow-up'
        WHEN ed.is_resolve = 1 THEN 'Resolved' ELSE 'Unresolve' END AS issue_status
        ,ed.description,ed.ed_advice,CASE WHEN (SELECT COUNT(id)FROM ed_dashboard WHERE followup_month_for=ed.month_for AND service_id=ed.service_id AND issue_id=ed.issue_id GROUP BY followup_month_for)>0 THEN
        (SELECT remarks FROM ed_dashboard WHERE followup_month_for=ed.month_for AND service_id=ed.service_id AND issue_id=ed.issue_id ORDER BY id DESC LIMIT 1) ELSE ed.remarks END AS remarks
         FROM  ed_dashboard ed INNER JOIN ed_dashboard_issues edi ON ed.issue_id=edi.id
        INNER JOIN pm_mcrs_log lg ON lg.service_id = ed.service_id AND COALESCE(lg.is_submitted_db,FALSE) =1
        AND MONTH(ed.month_for)=lg.month AND YEAR(ed.month_for)= lg.year
        WHERE ed.service_id = ${serviceId} AND COALESCE(ed.is_followup,FALSE)<>1 AND (COALESCE(ed.is_resolve,FALSE) <> 1
        OR (DATE(ed.status_change_date)=DATE('${month}') AND COALESCE(ed.is_followup,FALSE) <> 1))
        ORDER BY ed.issue_id,ed.month_for;
        """*/
        String queryForList = """
       SELECT ed.id ,ed.version,edi.issue_name ,ed.is_resolve
        ,CASE WHEN (SELECT COUNT(id)FROM ed_dashboard WHERE followup_month_for=ed.month_for  AND `type` = 1 AND service_id=ed.service_id
        AND issue_id=ed.issue_id AND month_for=DATE('${month}') GROUP BY followup_month_for)>0 THEN TRUE ELSE
         ed.is_followup END AS is_followup,(SELECT month_for FROM ed_dashboard WHERE followup_month_for=ed.month_for  AND `type` = 1
         AND service_id=ed.service_id AND issue_id=ed.issue_id AND month_for=DATE('${month}')) AS followup_month_for
         ,CONCAT(MONTHNAME(ed.month_for),' ',YEAR(ed.month_for)) AS issuedMonthStr
        ,CASE WHEN (SELECT COUNT(id)FROM ed_dashboard WHERE followup_month_for=ed.month_for  AND `type` = 1 AND service_id=ed.service_id
        AND issue_id=ed.issue_id AND month_for=DATE('${month}') GROUP BY followup_month_for)>0 THEN 'Follow-up'
        WHEN ed.is_resolve = 1 THEN 'Resolved' ELSE 'Unresolve' END AS issue_status
        ,ed.description,ed.ed_advice,CASE WHEN (SELECT COUNT(id)FROM ed_dashboard WHERE  `type` = 1 AND followup_month_for=ed.month_for
        AND service_id=ed.service_id AND issue_id=ed.issue_id GROUP BY followup_month_for)>0 THEN
        (SELECT remarks FROM ed_dashboard WHERE followup_month_for=ed.month_for  AND `type` = 1 AND service_id=ed.service_id
        AND issue_id=ed.issue_id ORDER BY id DESC LIMIT 1) ELSE ed.remarks END AS remarks
         FROM  ed_dashboard ed
         INNER JOIN ed_dashboard_issues edi ON ed.issue_id=edi.id
         LEFT JOIN ed_dashboard ed1 ON ed.followup_month_for=ed1.month_for AND ed1.is_followup <>1  AND ed1.`type` = 1
        INNER JOIN pm_mcrs_log lg ON lg.service_id = ed.service_id AND COALESCE(lg.is_submitted_db,FALSE) =1
        AND MONTH(ed.month_for)=lg.month AND YEAR(ed.month_for)= lg.year
        WHERE ed.service_id = ${serviceId} AND COALESCE(ed.is_resolve,FALSE) <> 1 AND COALESCE(ed.is_followup,FALSE)<>1
         AND ed.`type` = 1 AND ed.month_for NOT IN
        (SELECT followup_month_for FROM ed_dashboard
        WHERE followup_month_for=ed.month_for  AND `type` = 1 AND service_id=ed.service_id
        AND issue_id=ed.issue_id AND month_for>DATE('${month}'))
        ORDER BY ed.issue_id,ed.month_for;
        """
        List<GroovyRowResult>  lst = executeSelectSql(queryForList)
        return lst
    }
    public Long ExistedInFutureDate(long serviceId,Date d,String subDate,long issueId ) {
        String queryForList = """
        SELECT id
            FROM ed_dashboard
        WHERE service_id='${serviceId}'
            AND followup_month_for=DATE('${d}')
            AND month_for>=DATE('${subDate}')
            AND issue_id='${issueId}'
            AND `type` = 1
        """
        List<GroovyRowResult>  lst = executeSelectSql(queryForList)
        Long id=0
        if(lst.size()>0)
            id=lst[0]['id']
        return id
    }
}
