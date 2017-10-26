package service

import com.pms.PmActionsIndicatorDetails
import com.pms.PmSapBackupLog
import com.pms.PmServiceSector
import com.pms.PropertiesReader
import com.pms.SecUser
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import pms.BaseService
import pms.utility.DateUtility
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import pms.utility.Tools

import java.text.DateFormat
import java.text.SimpleDateFormat

@Transactional
class PmActionsService extends BaseService {

    JasperService jasperService

    public List<GroovyRowResult> lstActionsByServiceIdAndYear(long serviceId, String year) {
        String queryForList = """
            SELECT o.id,o.sequence AS name FROM pm_actions o
                WHERE o.service_id = ${serviceId}
                AND YEAR(start)<='${year}' AND YEAR(end) >='${year}'
                ORDER BY o.goal_id,o.tmp_seq
        """
        List<GroovyRowResult> lst = executeSelectSql(queryForList)
        return lst
    }
    public List<GroovyRowResult> lstActionsForDropDown(long goalId) {
        String queryForList = """
            SELECT o.id AS id, CONCAT(o.sequence,'. ',o.actions) AS name
                FROM pm_actions o
                WHERE o.goal_id = ${goalId}
                ORDER BY o.tmp_seq
        """
        List<GroovyRowResult> lst = executeSelectSql(queryForList)
        return lst
    }
    public boolean taskDateWithinActionsDate(Date start,Date end,long actionsId) {
        String queryForList = """
            SELECT COUNT(id) as c FROM pm_actions
            WHERE START<='${start}' AND END >='${end}' AND id=${actionsId}
        """
        List<GroovyRowResult> lst = executeSelectSql(queryForList)
        int count =(int) lst[0].c
        boolean isWithin=false
        if(count>0){
            isWithin=true
        }
        return isWithin
    }
    public List<GroovyRowResult> lstDepartmentSpStatus(String dateStr) {
        int year = Integer.parseInt(dateStr)

        String query = """
            SELECT ss.name service,l.id AS log_id,l.dead_line,
                l.submission_date,l.is_editable,l.is_submitted,l.year
            FROM pm_service_sector ss
            LEFT JOIN pm_sp_log l ON l.service_id = ss.id AND l.year = ${year}
            WHERE ss.is_in_sp = TRUE
            ORDER BY ss.name,l.year;
        """
        List<GroovyRowResult> lst = executeSelectSql(query)
        return lst
    }
    public List<GroovyRowResult> lstDepartmentMcrsStatus(String yearStr) {
        int year = Integer.parseInt(yearStr)
        String query = """
               SELECT tmp.short_name service,
                MAX(tmp.JanuaryID) JanuaryID     ,MAX(tmp.January) January     ,MAX(tmp.JanuaryDsb) JanuaryDsb     ,MAX(tmp.JanuaryD) JanuaryD ,
                MAX(tmp.FebruaryID) FebruaryID   ,MAX(tmp.February) February   ,MAX(tmp.FebruaryDsb) FebruaryDsb   ,MAX(tmp.FebruaryD) FebruaryD ,
                MAX(tmp.MarchID) MarchID         ,MAX(tmp.March) March         ,MAX(tmp.MarchDsb) MarchDsb         ,MAX(tmp.MarchD) MarchD ,
                MAX(tmp.AprilID) AprilID         ,MAX(tmp.April) April         ,MAX(tmp.AprilDsb) AprilDsb         ,MAX(tmp.AprilD) AprilD ,
                MAX(tmp.MayID) MayID             ,MAX(tmp.May) May             ,MAX(tmp.MayDsb) MayDsb             ,MAX(tmp.MayD) MayD ,
                MAX(tmp.JuneID) JuneID           ,MAX(tmp.June) June           ,MAX(tmp.JuneDsb) JuneDsb           ,MAX(tmp.JuneD) JuneD ,
                MAX(tmp.JulyID) JulyID           ,MAX(tmp.July) July           ,MAX(tmp.JulyDsb) JulyDsb           ,MAX(tmp.JulyD) JulyD ,
                MAX(tmp.AugustID) AugustID       ,MAX(tmp.August) August       ,MAX(tmp.AugustDsb) AugustDsb       ,MAX(tmp.AugustD) AugustD ,
                MAX(tmp.SeptemberID) SeptemberID ,MAX(tmp.September) September ,MAX(tmp.SeptemberDsb) SeptemberDsb ,MAX(tmp.SeptemberD) SeptemberD ,
                MAX(tmp.OctoberID) OctoberID     ,MAX(tmp.October) October     ,MAX(tmp.OctoberDsb) OctoberDsb     ,MAX(tmp.OctoberD) OctoberD,
                MAX(tmp.NovemberID) NovemberID   ,MAX(tmp.November) November   ,MAX(tmp.NovemberDsb) NovemberDsb   ,MAX(tmp.NovemberD) NovemberD  ,
                MAX(tmp.DecemberID) DecemberID   ,MAX(tmp.December) December   ,MAX(tmp.DecemberDsb) DecemberDsb   ,MAX(tmp.DecemberD) DecemberD,
                CASE WHEN COALESCE(MAX(tmp.JanCount),0)>0 THEN TRUE ELSE FALSE END editedMRPJan,
                CASE WHEN COALESCE(MAX(tmp.JanCountDb),0)>0 THEN TRUE ELSE FALSE END editedDsbJan,
                CASE WHEN COALESCE(MAX(tmp.FebCount),0)>0 THEN TRUE ELSE FALSE END editedMRPFeb,
                CASE WHEN COALESCE(MAX(tmp.FebCountDb),0)>0 THEN TRUE ELSE FALSE END editedDsbFeb,
                CASE WHEN COALESCE(MAX(tmp.MarCount),0)>0 THEN TRUE ELSE FALSE END editedMRPMar,
                CASE WHEN COALESCE(MAX(tmp.MarCountDb),0)>0 THEN TRUE ELSE FALSE END editedDsbMar,
                CASE WHEN COALESCE(MAX(tmp.AprCount),0)>0 THEN TRUE ELSE FALSE END editedMRPApr,
                CASE WHEN COALESCE(MAX(tmp.AprCountDb),0)>0 THEN TRUE ELSE FALSE END editedDsbApr,
                CASE WHEN COALESCE(MAX(tmp.MayCount),0)>0 THEN TRUE ELSE FALSE END editedMRPMay,
                CASE WHEN COALESCE(MAX(tmp.MayCountDb),0)>0 THEN TRUE ELSE FALSE END editedDsbMay,
                CASE WHEN COALESCE(MAX(tmp.JunCount),0)>0 THEN TRUE ELSE FALSE END editedMRPJune,
                CASE WHEN COALESCE(MAX(tmp.JunCountDb),0)>0 THEN TRUE ELSE FALSE END editedDsbJune,
                CASE WHEN COALESCE(MAX(tmp.JulCount),0)>0 THEN TRUE ELSE FALSE END editedMRPJuly,
                CASE WHEN COALESCE(MAX(tmp.JulCountDb),0)>0 THEN TRUE ELSE FALSE END editedDsbJuly,
                CASE WHEN COALESCE(MAX(tmp.AugCount),0)>0 THEN TRUE ELSE FALSE END editedMRPAug,
                CASE WHEN COALESCE(MAX(tmp.AugCountDb),0)>0 THEN TRUE ELSE FALSE END editedDsbAug,
                CASE WHEN COALESCE(MAX(tmp.SepCount),0)>0 THEN TRUE ELSE FALSE END editedMRPSep,
                CASE WHEN COALESCE(MAX(tmp.SepCountDb),0)>0 THEN TRUE ELSE FALSE END editedDsbSep,
                CASE WHEN COALESCE(MAX(tmp.OctCount),0)>0 THEN TRUE ELSE FALSE END editedMRPOct,
                CASE WHEN COALESCE(MAX(tmp.OctCountDb),0)>0 THEN TRUE ELSE FALSE END editedDsbOct,
                CASE WHEN COALESCE(MAX(tmp.NovCount),0)>0 THEN TRUE ELSE FALSE END editedMRPNov,
                CASE WHEN COALESCE(MAX(tmp.NovCountDb),0)>0 THEN TRUE ELSE FALSE END editedDsbNov,
                CASE WHEN COALESCE(MAX(tmp.DecCount),0)>0 THEN TRUE ELSE FALSE END editedMRPDec,
                CASE WHEN COALESCE(MAX(tmp.DecCountDb),0)>0 THEN TRUE ELSE FALSE END editedDsbDec


                FROM (SELECT ss.short_name,
                CASE WHEN l.month_str='January' THEN l.id ELSE NULL END JanuaryID,
                CASE WHEN l.month_str='January' THEN l.submission_date ELSE NULL END January,
                CASE WHEN l.month_str='January' THEN l.submission_date_db ELSE NULL END JanuaryDsb,
                CASE WHEN l.month_str='January' THEN l.dead_line ELSE NULL END JanuaryD,
                CASE WHEN l.month_str='January' AND MIN(ld.log_type_id)=1 THEN COUNT(l.id) ELSE 0 END JanCount,
                CASE WHEN l.month_str='January' AND MAX(ld.log_type_id)=2 THEN COUNT(l.id) ELSE 0 END JanCountDb,

                CASE WHEN l.month_str='February' THEN l.id ELSE NULL END FebruaryID,
                CASE WHEN l.month_str='February' THEN l.submission_date ELSE NULL END February,
                CASE WHEN l.month_str='February' THEN l.submission_date_db ELSE NULL END FebruaryDsb,
                CASE WHEN l.month_str='February' THEN l.dead_line ELSE NULL END FebruaryD,
                CASE WHEN l.month_str='February' AND MIN(ld.log_type_id)=1 THEN COUNT(l.id) ELSE 0 END FebCount,
                CASE WHEN l.month_str='February' AND MAX(ld.log_type_id)=2 THEN COUNT(l.id) ELSE 0 END FebCountDb,

                CASE WHEN l.month_str='March' THEN l.id ELSE NULL END MarchID,
                CASE WHEN l.month_str='March' THEN l.submission_date ELSE NULL END March,
                CASE WHEN l.month_str='March' THEN l.submission_date_db ELSE NULL END MarchDsb,
                CASE WHEN l.month_str='March' THEN l.dead_line ELSE NULL END MarchD,
                CASE WHEN l.month_str='March' AND MIN(ld.log_type_id)=1 THEN COUNT(l.id) ELSE 0 END MarCount,
                CASE WHEN l.month_str='March' AND MAX(ld.log_type_id)=2 THEN COUNT(l.id) ELSE 0 END MarCountDb,

                CASE WHEN l.month_str='April' THEN l.id ELSE NULL END AprilID,
                CASE WHEN l.month_str='April' THEN l.submission_date ELSE NULL END April,
                CASE WHEN l.month_str='April' THEN l.submission_date_db ELSE NULL END AprilDsb,
                CASE WHEN l.month_str='April' THEN l.dead_line ELSE NULL END AprilD,
                CASE WHEN l.month_str='April' AND MIN(ld.log_type_id)=1 THEN COUNT(l.id) ELSE 0 END AprCount,
                CASE WHEN l.month_str='April' AND MAX(ld.log_type_id)=2 THEN COUNT(l.id) ELSE 0 END AprCountDb,

                CASE WHEN l.month_str='May' THEN l.id ELSE NULL END MayID,
                CASE WHEN l.month_str='May' THEN l.submission_date ELSE NULL END May,
                CASE WHEN l.month_str='May' THEN l.submission_date_db ELSE NULL END MayDsb,
                CASE WHEN l.month_str='May' THEN l.dead_line ELSE NULL END MayD,
                CASE WHEN l.month_str='May' AND MIN(ld.log_type_id)=1 THEN COUNT(l.id) ELSE 0 END MayCount,
                CASE WHEN l.month_str='May' AND MAX(ld.log_type_id)=2 THEN COUNT(l.id) ELSE 0 END MayCountDb,

                CASE WHEN l.month_str='June' THEN l.id ELSE NULL END JuneID,
                CASE WHEN l.month_str='June' THEN l.submission_date ELSE NULL END June,
                CASE WHEN l.month_str='June' THEN l.submission_date_db ELSE NULL END JuneDsb,
                CASE WHEN l.month_str='June' THEN l.dead_line ELSE NULL END JuneD,
                CASE WHEN l.month_str='June' AND MIN(ld.log_type_id)=1 THEN COUNT(l.id) ELSE 0 END JunCount,
                CASE WHEN l.month_str='June' AND MAX(ld.log_type_id)=2 THEN COUNT(l.id) ELSE 0 END JunCountDb,

                CASE WHEN l.month_str='July' THEN l.id ELSE NULL END JulyID,
                CASE WHEN l.month_str='July' THEN l.submission_date ELSE NULL END July,
                CASE WHEN l.month_str='July' THEN l.submission_date_db ELSE NULL END JulyDsb,
                CASE WHEN l.month_str='July' THEN l.dead_line ELSE NULL END JulyD,
                CASE WHEN l.month_str='July' AND MIN(ld.log_type_id)=1 THEN COUNT(l.id) ELSE 0 END JulCount,
                CASE WHEN l.month_str='July' AND MAX(ld.log_type_id)=2 THEN COUNT(l.id) ELSE 0 END JulCountDb,

                CASE WHEN l.month_str='August' THEN l.id ELSE NULL END AugustID,
                CASE WHEN l.month_str='August' THEN l.submission_date ELSE NULL END August,
                CASE WHEN l.month_str='August' THEN l.submission_date_db ELSE NULL END AugustDsb,
                CASE WHEN l.month_str='August' THEN l.dead_line ELSE NULL END AugustD,
                CASE WHEN l.month_str='August' AND MIN(ld.log_type_id)=1 THEN COUNT(l.id) ELSE 0 END AugCount,
                CASE WHEN l.month_str='August' AND MAX(ld.log_type_id)=2 THEN COUNT(l.id) ELSE 0 END AugCountDb,

                CASE WHEN l.month_str='September' THEN l.id ELSE NULL END SeptemberID,
                CASE WHEN l.month_str='September' THEN l.submission_date ELSE NULL END September,
                CASE WHEN l.month_str='September' THEN l.submission_date_db ELSE NULL END SeptemberDsb,
                CASE WHEN l.month_str='September' THEN l.dead_line ELSE NULL END SeptemberD,
                CASE WHEN l.month_str='September' AND MIN(ld.log_type_id)=1 THEN COUNT(l.id) ELSE 0 END SepCount,
                CASE WHEN l.month_str='September' AND MAX(ld.log_type_id)=2 THEN COUNT(l.id) ELSE 0 END SepCountDb,

                CASE WHEN l.month_str='October' THEN l.id ELSE NULL END OctoberID,
                CASE WHEN l.month_str='October' THEN l.submission_date ELSE NULL END October,
                CASE WHEN l.month_str='October' THEN l.submission_date_db ELSE NULL END OctoberDsb,
                CASE WHEN l.month_str='October' THEN l.dead_line ELSE NULL END OctoberD,
                CASE WHEN l.month_str='October' AND MIN(ld.log_type_id)=1 THEN COUNT(l.id) ELSE 0 END OctCount,
                CASE WHEN l.month_str='October' AND MAX(ld.log_type_id)=2 THEN COUNT(l.id) ELSE 0 END OctCountDb,

                CASE WHEN l.month_str='November' THEN l.id ELSE NULL END NovemberID,
                CASE WHEN l.month_str='November' THEN l.submission_date ELSE NULL END November,
                CASE WHEN l.month_str='November' THEN l.submission_date_db ELSE NULL END NovemberDsb,
                CASE WHEN l.month_str='November' THEN l.dead_line ELSE NULL END NovemberD,
                CASE WHEN l.month_str='November' AND MIN(ld.log_type_id)=1 THEN COUNT(l.id) ELSE 0 END NovCount,
                CASE WHEN l.month_str='November' AND MAX(ld.log_type_id)=2 THEN COUNT(l.id) ELSE 0 END NovCountDb,

                CASE WHEN l.month_str='December' THEN l.id ELSE NULL END DecemberID,
                CASE WHEN l.month_str='December' THEN l.submission_date ELSE NULL END December,
                CASE WHEN l.month_str='December' THEN l.submission_date_db ELSE NULL END DecemberDsb,
                CASE WHEN l.month_str='December' THEN l.dead_line ELSE NULL END DecemberD,
                CASE WHEN l.month_str='December' AND MIN(ld.log_type_id)=1 THEN COUNT(l.id) ELSE 0 END DecCount,
                CASE WHEN l.month_str='December' AND MAX(ld.log_type_id)=2 THEN COUNT(l.id) ELSE 0 END DecCountDb
                FROM pm_service_sector ss
                LEFT JOIN pm_mcrs_log l ON l.service_id = ss.id AND l.year = ${year}
                LEFT JOIN pm_mcrs_log_details ld ON l.id=ld.log_id
                WHERE ss.is_in_sp = TRUE  GROUP BY ss.short_name,l.id
                ORDER BY ss.name,l.month) tmp GROUP BY tmp.short_name;
        """
        List<GroovyRowResult> lst = executeSelectSql(query)
        return lst
    }
    public List<GroovyRowResult> lstServiceWiseAcvStatus(String monthStr) {
        DateFormat originalFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        Date date = originalFormat.parse(monthStr);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH)+1
        int year = c.get(Calendar.YEAR)
        SecUser user = currentUserObject();

        String query = """
                    SELECT ROUND(SUM(a_pert)/COUNT(cat_axe)) act_val, "Achieved" act_name,"#069302" act_color
            FROM (
                SELECT t.cat_axe,IF(ROUND(SUM(t.avgr)/COUNT(t.actions_id))>100,100,ROUND(SUM(t.avgr)/COUNT(t.actions_id))) a_pert
                FROM (
                    SELECT t.service_id,t.goal_id,t.cat_axe,t.actions_id,
                    SUM(ROUND((t.achievement/t.target)*100,2))/COUNT(t.indicator_id) avgr
                    FROM (
                        SELECT sc.id service_id,g.id AS goal_id,sc.name service,sc.short_name,a.id actions_id,ai.id indicator_id,
                        CONCAT('Goal ',g.sequence) cat_axe,aid.target,COALESCE(aid.achievement,0) achievement
                        FROM pm_goals g
                        LEFT JOIN pm_service_sector sc ON sc.id = g.service_id
                        LEFT JOIN pm_actions a ON a.goal_id = g.id
                        LEFT JOIN pm_actions_indicator ai ON a.id = ai.actions_id
                        LEFT JOIN pm_actions_indicator_details aid ON ai.id=aid.indicator_id
                        JOIN custom_month cm ON cm.name=aid.month_name,
                        (SELECT @curmon := ${month}) r
                        WHERE a.year = ${year} AND cm.sl_index=@curmon AND aid.target > 0 AND a.service_id = ${user.serviceId}
                        ORDER BY sc.sequence,g.id,a.id,ai.id,aid.id
                    ) t GROUP BY t.goal_id ,t.actions_id
                ) t GROUP BY t.goal_id
            ) tmp

            UNION ALL

            SELECT 100-ROUND(SUM(a_pert)/COUNT(cat_axe)) act_val, "Remaining" act_name,"#ff8a00" act_color
            FROM (
                SELECT t.cat_axe,IF(ROUND(SUM(t.avgr)/COUNT(t.actions_id))>100,100,ROUND(SUM(t.avgr)/COUNT(t.actions_id))) a_pert
                FROM (
                    SELECT t.service_id,t.goal_id,t.cat_axe,t.actions_id,
                    SUM(ROUND((t.achievement/t.target)*100,2))/COUNT(t.indicator_id) avgr
                    FROM (
                        SELECT sc.id service_id,g.id AS goal_id,sc.name service,sc.short_name,a.id actions_id,ai.id indicator_id,
                        CONCAT('Goal ',g.sequence) cat_axe,aid.target,COALESCE(aid.achievement,0) achievement
                        FROM pm_goals g
                        LEFT JOIN pm_service_sector sc ON sc.id = g.service_id
                        LEFT JOIN pm_actions a ON a.goal_id = g.id
                        LEFT JOIN pm_actions_indicator ai ON a.id = ai.actions_id
                        LEFT JOIN pm_actions_indicator_details aid ON ai.id=aid.indicator_id
                        JOIN custom_month cm ON cm.name=aid.month_name,
                        (SELECT @curmon := ${month}) r
                        WHERE a.year = ${year} AND cm.sl_index=@curmon AND aid.target > 0 AND a.service_id = ${user.serviceId}
                        ORDER BY sc.sequence,g.id,a.id,ai.id,aid.id
                    ) t GROUP BY t.goal_id ,t.actions_id
                ) t GROUP BY t.goal_id
            ) tmp2;
        """
        List<GroovyRowResult> lst = groovySql.rows(query)
        return lst
    }
    public List<GroovyRowResult> lstGoalWiseActionStatus(String monthStr) {
        DateFormat originalFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        Date date = originalFormat.parse(monthStr);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH)+1
        int year = c.get(Calendar.YEAR)
        SecUser user = currentUserObject();

        String query = """
                SELECT t.cat_axe,t.goal,COUNT(t.actions_id) ac_count,'#ff8a00' t_color, '#069302' a_color,
                IF(ROUND(SUM(t.avgr)/COUNT(t.actions_id))>100,100,ROUND(SUM(t.avgr)/COUNT(t.actions_id))) a_pert,
                ROUND(IF(ROUND(SUM(t.avgr)/COUNT(t.actions_id))>100,100,ROUND(SUM(t.avgr)/COUNT(t.actions_id)))*COUNT(t.actions_id)/100,2) a_col,
                COUNT(t.actions_id)-ROUND(IF(ROUND(SUM(t.avgr)/COUNT(t.actions_id))>100,100,ROUND(SUM(t.avgr)/COUNT(t.actions_id)))*COUNT(t.actions_id)/100,2) t_col
                FROM (
                    SELECT t.goal_id,t.goal,t.cat_axe,t.actions_id,
                    SUM(ROUND((t.achievement/t.target)*100,2))/COUNT(t.indicator_id) avgr
                    FROM (
                        SELECT g.id AS goal_id,g.goal,a.id actions_id,ai.id indicator_id,
                        CONCAT('Goal ',g.sequence) cat_axe,aid.target,COALESCE(aid.achievement,0) achievement
                        FROM pm_goals g
                        LEFT JOIN pm_service_sector sc ON sc.id = g.service_id
                        LEFT JOIN pm_actions a ON a.goal_id = g.id
                        LEFT JOIN pm_actions_indicator ai ON a.id = ai.actions_id
                        LEFT JOIN pm_actions_indicator_details aid ON ai.id=aid.indicator_id
                        JOIN custom_month cm ON cm.name=aid.month_name,
                        (SELECT @curmon := ${month}) r
                        WHERE g.service_id = ${user.serviceId} AND a.year = ${year} AND cm.sl_index=@curmon AND aid.target > 0
                        ORDER BY sc.sequence,g.id,a.id,ai.id,aid.id
                    ) t GROUP BY t.goal_id ,t.actions_id
                ) t GROUP BY t.goal_id
        """
        List<GroovyRowResult> lst = groovySql.rows(query)
        return lst
    }
    public List<GroovyRowResult> lstServiceWiseActionStatus(String monthStr) {
        DateFormat originalFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        Date date = originalFormat.parse(monthStr);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH)+1
        int year = c.get(Calendar.YEAR)

        String additionalParam = EMPTY_SPACE
        boolean isDepartmental = isUserOnlyDepartmental()
        if(isDepartmental){
            String lstDeptStr = currentUserDepartmentListStr()
            additionalParam = " AND sc.id IN (${lstDeptStr}) "
        }

        String query = """
            SELECT tmp.service_id,tmp.service,tmp.short_name,
            ROUND(SUM(tmp.avgr)/COUNT(tmp.goal_id)) a_pert,'#069302' a_color,
            100-ROUND(SUM(tmp.avgr)/COUNT(tmp.goal_id)) r_pert,'#ff8a00' r_color
            FROM (
                SELECT t.service_id,t.goal_id,t.service,t.short_name,t.cat_axe,
                IF(ROUND(SUM(t.avgr)/COUNT(t.actions_id))>100,100,ROUND(SUM(t.avgr)/COUNT(t.actions_id))) avgr
                FROM (
                    SELECT t.service_id,t.goal_id,t.service,t.short_name,t.cat_axe,t.actions_id,
                    SUM(ROUND((t.achievement/t.target)*100,2))/COUNT(t.indicator_id) avgr
                    FROM (
                        SELECT sc.id service_id,g.id AS goal_id,sc.name service,sc.short_name,a.id actions_id,ai.id indicator_id,
                        CONCAT('Goal ',g.sequence) cat_axe,aid.target,COALESCE(aid.achievement,0) achievement
                        FROM pm_goals g
                        LEFT JOIN pm_service_sector sc ON sc.id = g.service_id
                        LEFT JOIN pm_actions a ON a.goal_id = g.id
                        LEFT JOIN pm_actions_indicator ai ON a.id = ai.actions_id
                        LEFT JOIN pm_actions_indicator_details aid ON ai.id=aid.indicator_id
                        JOIN custom_month cm ON cm.name=aid.month_name,
                        (SELECT @curmon := ${month}) r
                        WHERE a.year = ${year} AND cm.sl_index=@curmon AND aid.target > 0
                        ${additionalParam}
                        ORDER BY sc.sequence,g.id,a.id,ai.id,aid.id
                    ) t GROUP BY t.goal_id ,t.actions_id
                ) t GROUP BY t.goal_id
            ) tmp GROUP BY service_id;
        """
        List<GroovyRowResult> lst = groovySql.rows(query)
        return lst
    }
    public List<GroovyRowResult> findAllDetailsByActionsIdAndIndicatorIdNotExtended(long actionsId, long indicatorId) {
        String query = """
            SELECT aid.id,aid.version,aid.actions_id,aid.create_by,aid.create_date,aid.indicator_id,aid.month_name,
            COALESCE(aid.target,0) target,COALESCE(aid.achievement,0) achievement,ai.indicator_type,
            CASE WHEN  COALESCE(MONTHNAME(ai.closing_month),'')=aid.month_name THEN CONCAT(aid.remarks,'\\</br><b>Closing note: \\</b>'
            ,ai.closing_note) ELSE aid.remarks END remarks,aid.is_excluded
            FROM pm_actions_indicator_details aid
            JOIN pm_actions_indicator ai ON ai.id=aid.indicator_id
            JOIN pm_actions a ON ai.actions_id=a.id
            JOIN custom_month cm ON cm.name=aid.month_name
            WHERE aid.actions_id = ${actionsId} AND aid.indicator_id = ${indicatorId}
           AND (COALESCE(ai.is_extended,FALSE)=TRUE OR cm.sl_index <= MONTH(COALESCE((SELECT END FROM
            pm_actions_extend_history WHERE actions_id=a.id ORDER BY id ASC LIMIT 1),a.end)))
        """
        List<GroovyRowResult> lst = executeSelectSql(query)
        return lst
    }

    private static final String BACKUP_DIR = PropertiesReader.getProperty("sap.backup.location", PropertiesReader.CONFIG_FILE_DB)
    private static final String REPORT_FOLDER = 'pmActions/yearly/Details'
    private static final String JASPER_FILE = 'sapDetails'
    private static final String REPORT_TITLE_LBL = 'reportTitle'
    private static final String REPORT_TITLE = ' -Strategic Action Plan '
    private static final String SERVICE_ID = "serviceId"
    private static final String SERVICE_NAME = "serviceName"
    private static final String YEAR = "year"

    public void sapDetailsBackup(long serviceId, int year) {
        PmServiceSector service = PmServiceSector.read(serviceId)
        String rootDir = SCH.servletContext.getRealPath('/reports') + File.separator
        String logoDir = SCH.servletContext.getRealPath('/images') + File.separator
        String reportDir = rootDir + REPORT_FOLDER
        String titleStr = service.shortName + REPORT_TITLE + EMPTY_SPACE + year

        Date date = new Date() ;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String outputFileName =  service.shortName + UNDERSCORE+DateUtility.getSqlDate(date).toString().replace("-","_")+UNDERSCORE+String.format("%02d",c.getInstance().get(c.HOUR_OF_DAY))+UNDERSCORE+ String.format("%02d",c.getInstance().get(c.MINUTE)) + PDF_EXTENSION

        Map reportParams = new LinkedHashMap()
        reportParams.put(ROOT_DIR, rootDir)
        reportParams.put(LOGO_DIR, logoDir)
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(SUBREPORT_DIR, reportDir + File.separator)
        reportParams.put(REPORT_TITLE_LBL, titleStr)
        reportParams.put(SERVICE_ID, serviceId)
        reportParams.put(SERVICE_NAME, service.name)
        reportParams.put(YEAR, year)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        ByteArrayOutputStream baos = jasperService.generateReport(reportDef)
        try {
            File theDir = new File(BACKUP_DIR);
            if (!theDir.exists()) {
                System.out.println("creating directory: " + theDir.getName());
                boolean result = false;
                try{
                    theDir.mkdir();
                    result = true;
                }
                catch(SecurityException se){
                    //handle it
                }
                if(result) {
                    System.out.println("DIR created");
                }
            }
            FileOutputStream fos = new FileOutputStream (new File(BACKUP_DIR+outputFileName));
            baos.writeTo(fos);
            baos.flush();
            baos.close();
            fos.flush();
            fos.close();

            PmSapBackupLog pmSapBackupLog=new PmSapBackupLog()
            pmSapBackupLog.fileName=outputFileName
            pmSapBackupLog.createDate=DateUtility.getSqlDate(new Date())
            pmSapBackupLog.serviceId=serviceId
            pmSapBackupLog.year=year
            pmSapBackupLog.save()

        } catch(Exception e) {
           // ioe.printStackTrace();
        }
    }

    public void deleteInvalidIndicatorDetails(long indicatorId, String start, String end) {
        if (start == 'February') {
            PmActionsIndicatorDetails jan = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'January')
            jan?.delete()
        } else if (start == 'March') {
            PmActionsIndicatorDetails jan = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'January')
            jan?.delete()
            PmActionsIndicatorDetails feb = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'February')
            feb?.delete()
        } else if (start == 'April') {
            PmActionsIndicatorDetails jan = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'January')
            jan?.delete()
            PmActionsIndicatorDetails feb = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'February')
            feb?.delete()
            PmActionsIndicatorDetails mar = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'March')
            mar?.delete()
        } else if (start == 'May') {
            PmActionsIndicatorDetails jan = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'January')
            jan?.delete()
            PmActionsIndicatorDetails feb = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'February')
            feb?.delete()
            PmActionsIndicatorDetails mar = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'March')
            mar?.delete()
            PmActionsIndicatorDetails apr = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'April')
            apr?.delete()
        } else if (start == 'June') {
            PmActionsIndicatorDetails jan = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'January')
            jan?.delete()
            PmActionsIndicatorDetails feb = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'February')
            feb?.delete()
            PmActionsIndicatorDetails mar = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'March')
            mar?.delete()
            PmActionsIndicatorDetails apr = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'April')
            apr?.delete()
            PmActionsIndicatorDetails may = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'May')
            may?.delete()
        } else if (start == 'July') {
            PmActionsIndicatorDetails jan = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'January')
            jan?.delete()
            PmActionsIndicatorDetails feb = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'February')
            feb?.delete()
            PmActionsIndicatorDetails mar = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'March')
            mar?.delete()
            PmActionsIndicatorDetails apr = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'April')
            apr?.delete()
            PmActionsIndicatorDetails may = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'May')
            may?.delete()
            PmActionsIndicatorDetails june = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'June')
            june?.delete()
        } else if (start == 'August') {
            PmActionsIndicatorDetails jan = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'January')
            jan?.delete()
            PmActionsIndicatorDetails feb = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'February')
            feb?.delete()
            PmActionsIndicatorDetails mar = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'March')
            mar?.delete()
            PmActionsIndicatorDetails apr = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'April')
            apr?.delete()
            PmActionsIndicatorDetails may = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'May')
            may?.delete()
            PmActionsIndicatorDetails june = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'June')
            june?.delete()
            PmActionsIndicatorDetails july = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'July')
            july?.delete()
        } else if (start == 'September') {
            PmActionsIndicatorDetails jan = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'January')
            jan?.delete()
            PmActionsIndicatorDetails feb = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'February')
            feb?.delete()
            PmActionsIndicatorDetails mar = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'March')
            mar?.delete()
            PmActionsIndicatorDetails apr = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'April')
            apr?.delete()
            PmActionsIndicatorDetails may = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'May')
            may?.delete()
            PmActionsIndicatorDetails june = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'June')
            june?.delete()
            PmActionsIndicatorDetails july = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'July')
            july?.delete()
            PmActionsIndicatorDetails aug = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'August')
            aug?.delete()
        } else if (start == 'October') {
            PmActionsIndicatorDetails jan = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'January')
            jan?.delete()
            PmActionsIndicatorDetails feb = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'February')
            feb?.delete()
            PmActionsIndicatorDetails mar = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'March')
            mar?.delete()
            PmActionsIndicatorDetails apr = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'April')
            apr?.delete()
            PmActionsIndicatorDetails may = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'May')
            may?.delete()
            PmActionsIndicatorDetails june = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'June')
            june?.delete()
            PmActionsIndicatorDetails july = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'July')
            july?.delete()
            PmActionsIndicatorDetails aug = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'August')
            aug?.delete()
            PmActionsIndicatorDetails sep = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'September')
            sep?.delete()
        } else if (start == 'November') {
            PmActionsIndicatorDetails jan = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'January')
            jan?.delete()
            PmActionsIndicatorDetails feb = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'February')
            feb?.delete()
            PmActionsIndicatorDetails mar = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'March')
            mar?.delete()
            PmActionsIndicatorDetails apr = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'April')
            apr?.delete()
            PmActionsIndicatorDetails may = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'May')
            may?.delete()
            PmActionsIndicatorDetails june = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'June')
            june?.delete()
            PmActionsIndicatorDetails july = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'July')
            july?.delete()
            PmActionsIndicatorDetails aug = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'August')
            aug?.delete()
            PmActionsIndicatorDetails sep = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'September')
            sep?.delete()
            PmActionsIndicatorDetails oct = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'October')
            oct?.delete()
        } else if (start == 'December') {
            PmActionsIndicatorDetails jan = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'January')
            jan?.delete()
            PmActionsIndicatorDetails feb = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'February')
            feb?.delete()
            PmActionsIndicatorDetails mar = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'March')
            mar?.delete()
            PmActionsIndicatorDetails apr = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'April')
            apr?.delete()
            PmActionsIndicatorDetails may = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'May')
            may?.delete()
            PmActionsIndicatorDetails june = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'June')
            june?.delete()
            PmActionsIndicatorDetails july = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'July')
            july?.delete()
            PmActionsIndicatorDetails aug = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'August')
            aug?.delete()
            PmActionsIndicatorDetails sep = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'September')
            sep?.delete()
            PmActionsIndicatorDetails oct = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'October')
            oct?.delete()
            PmActionsIndicatorDetails nov = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'November')
            nov?.delete()
        }

        if (end == 'January') {
            PmActionsIndicatorDetails feb = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'February')
            feb?.delete()
            PmActionsIndicatorDetails mar = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'March')
            mar?.delete()
            PmActionsIndicatorDetails apr = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'April')
            apr?.delete()
            PmActionsIndicatorDetails may = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'May')
            may?.delete()
            PmActionsIndicatorDetails june = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'June')
            june?.delete()
            PmActionsIndicatorDetails july = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'July')
            july?.delete()
            PmActionsIndicatorDetails aug = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'August')
            aug?.delete()
            PmActionsIndicatorDetails sep = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'September')
            sep?.delete()
            PmActionsIndicatorDetails oct = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'October')
            oct?.delete()
            PmActionsIndicatorDetails nov = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'November')
            nov?.delete()
            PmActionsIndicatorDetails dec = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'December')
            dec?.delete()
        }else if (end == 'February') {
            PmActionsIndicatorDetails mar = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'March')
            mar?.delete()
            PmActionsIndicatorDetails apr = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'April')
            apr?.delete()
            PmActionsIndicatorDetails may = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'May')
            may?.delete()
            PmActionsIndicatorDetails june = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'June')
            june?.delete()
            PmActionsIndicatorDetails july = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'July')
            july?.delete()
            PmActionsIndicatorDetails aug = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'August')
            aug?.delete()
            PmActionsIndicatorDetails sep = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'September')
            sep?.delete()
            PmActionsIndicatorDetails oct = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'October')
            oct?.delete()
            PmActionsIndicatorDetails nov = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'November')
            nov?.delete()
            PmActionsIndicatorDetails dec = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'December')
            dec?.delete()
        } else if (end == 'March') {
            PmActionsIndicatorDetails apr = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'April')
            apr?.delete()
            PmActionsIndicatorDetails may = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'May')
            may?.delete()
            PmActionsIndicatorDetails june = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'June')
            june?.delete()
            PmActionsIndicatorDetails july = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'July')
            july?.delete()
            PmActionsIndicatorDetails aug = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'August')
            aug?.delete()
            PmActionsIndicatorDetails sep = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'September')
            sep?.delete()
            PmActionsIndicatorDetails oct = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'October')
            oct?.delete()
            PmActionsIndicatorDetails nov = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'November')
            nov?.delete()
            PmActionsIndicatorDetails dec = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'December')
            dec?.delete()
        } else if (end == 'April') {
            PmActionsIndicatorDetails may = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'May')
            may?.delete()
            PmActionsIndicatorDetails june = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'June')
            june?.delete()
            PmActionsIndicatorDetails july = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'July')
            july?.delete()
            PmActionsIndicatorDetails aug = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'August')
            aug?.delete()
            PmActionsIndicatorDetails sep = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'September')
            sep?.delete()
            PmActionsIndicatorDetails oct = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'October')
            oct?.delete()
            PmActionsIndicatorDetails nov = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'November')
            nov?.delete()
            PmActionsIndicatorDetails dec = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'December')
            dec?.delete()
        } else if (end == 'May') {
            PmActionsIndicatorDetails june = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'June')
            june?.delete()
            PmActionsIndicatorDetails july = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'July')
            july?.delete()
            PmActionsIndicatorDetails aug = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'August')
            aug?.delete()
            PmActionsIndicatorDetails sep = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'September')
            sep?.delete()
            PmActionsIndicatorDetails oct = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'October')
            oct?.delete()
            PmActionsIndicatorDetails nov = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'November')
            nov?.delete()
            PmActionsIndicatorDetails dec = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'December')
            dec?.delete()
        } else if (end == 'June') {
            PmActionsIndicatorDetails july = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'July')
            july?.delete()
            PmActionsIndicatorDetails aug = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'August')
            aug?.delete()
            PmActionsIndicatorDetails sep = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'September')
            sep?.delete()
            PmActionsIndicatorDetails oct = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'October')
            oct?.delete()
            PmActionsIndicatorDetails nov = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'November')
            nov?.delete()
            PmActionsIndicatorDetails dec = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'December')
            dec?.delete()
        } else if (end == 'July') {
            PmActionsIndicatorDetails aug = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'August')
            aug?.delete()
            PmActionsIndicatorDetails sep = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'September')
            sep?.delete()
            PmActionsIndicatorDetails oct = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'October')
            oct?.delete()
            PmActionsIndicatorDetails nov = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'November')
            nov?.delete()
            PmActionsIndicatorDetails dec = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'December')
            dec?.delete()
        } else if (end == 'August') {
            PmActionsIndicatorDetails sep = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'September')
            sep?.delete()
            PmActionsIndicatorDetails oct = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'October')
            oct?.delete()
            PmActionsIndicatorDetails nov = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'November')
            nov?.delete()
            PmActionsIndicatorDetails dec = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'December')
            dec?.delete()
        } else if (end == 'September') {
            PmActionsIndicatorDetails oct = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'October')
            oct?.delete()
            PmActionsIndicatorDetails nov = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'November')
            nov?.delete()
            PmActionsIndicatorDetails dec = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'December')
            dec?.delete()
        } else if (end == 'October') {
            PmActionsIndicatorDetails nov = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'November')
            nov?.delete()
            PmActionsIndicatorDetails dec = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'December')
            dec?.delete()
        } else if (end == 'November') {
            PmActionsIndicatorDetails dec = PmActionsIndicatorDetails.findByIndicatorIdAndMonthName(indicatorId, 'December')
            dec?.delete()
        }
    }
    public boolean isMRPSubmittable(long serviceId,String monthName,long year,int month) {
        String queryForList = """
           SELECT COUNT(*)c FROM(
                SELECT a.id
                FROM pm_actions a
                LEFT JOIN pm_actions_indicator i ON i.actions_id = a.id AND a.service_id=${serviceId} AND a.year=${year}
                LEFT JOIN pm_actions_indicator_details idd ON idd.indicator_id = i.id AND idd.month_name = '${monthName}'
                WHERE  idd.target > 0 AND COALESCE(idd.achievement,0)=0 AND (idd.remarks IS NULL OR idd.remarks='')
                UNION
                SELECT a.id
                FROM pm_actions a
                LEFT JOIN pm_actions_indicator i ON i.actions_id = a.id AND a.service_id=${serviceId} AND a.year=${year}
                WHERE (MONTHNAME(a.end)='${monthName}' AND i.closing_month IS NULL AND i.target>(SELECT SUM(achievement) FROM pm_actions_indicator_details
                WHERE actions_id=a.id AND indicator_id=i.id))
                UNION
                SELECT id FROM pm_mcrs_log ml WHERE  ml.service_id=${serviceId} AND ml.year=${year} AND ml.month<${month} AND is_submitted<>TRUE
                )tbl
        """
        List<GroovyRowResult> lst = executeSelectSql(queryForList)
        int count =(int) lst[0].c
        boolean isSubmittable=true
        if(count>0){
            isSubmittable=false
        }
        return isSubmittable
    }
    public boolean isDashboardSubmittable(long serviceId,int month,long year) {
        String queryForList = """
            SELECT COUNT(*) c FROM(
              SELECT ed.id FROM ed_dashboard ed
              WHERE ed.service_id = ${serviceId} AND COALESCE(ed.is_resolve,FALSE) <> 1 AND COALESCE(ed.is_followup,FALSE)<>1
              AND MONTH(month_for) < ${month} AND YEAR(month_for)=${year} AND ed.month_for NOT IN (SELECT followup_month_for FROM ed_dashboard
              WHERE DATE(followup_month_for)=DATE(ed.month_for) AND service_id=ed.service_id AND issue_id=ed.issue_id
              AND MONTH(month_for) >= ${month} AND YEAR(month_for)=${year})
            UNION
                SELECT id FROM pm_mcrs_log ml WHERE  ml.service_id=${serviceId} AND ml.year=${year} AND ml.month<${month} AND is_submitted_db<>TRUE
                )tbl
        """
        List<GroovyRowResult> lst = executeSelectSql(queryForList)
        int count =(int) lst[0].c
        boolean isSubmittable=true
        if(count>0){
            isSubmittable=false
        }
        return isSubmittable
    }
    public List<GroovyRowResult> findTotalTargetAchievements(long actionsId, long indicatorId) {
        String query = """
        SELECT ai.target AS target, COALESCE(SUM(COALESCE(idd.achievement,0)),0) achievement
        FROM pm_actions a
        JOIN pm_actions_indicator ai ON ai.actions_id = a.id
        JOIN pm_actions_indicator_details idd ON idd.indicator_id = ai.id
        JOIN custom_month cm ON cm.name=idd.month_name
        AND cm.sl_index <=(SELECT MONTH FROM pm_mcrs_log WHERE service_id=a.service_id AND is_submitted=TRUE ORDER BY MONTH DESC,YEAR DESC LIMIT 1 )
        WHERE idd.actions_id=${actionsId} AND idd.indicator_id=${indicatorId} GROUP BY ai.id
        """
        List<GroovyRowResult> lst = executeSelectSql(query)
        return lst
    }
    public boolean canDeleteActionDetails(Long actionsId,Long indId,Long id,Date subDate) {
        String queryForList = """
            SELECT COUNT(*) c FROM pm_actions_indicator_details aid JOIN custom_month cm ON aid.month_name=cm.name
            WHERE aid.actions_id=${actionsId} AND aid.indicator_id!=${indId} AND aid.id>${id} AND cm.sl_index>MONTH('${subDate}') AND aid.is_extended=TRUE
        """
        List<GroovyRowResult> lst = executeSelectSql(queryForList)
        int count =(int) lst[0].c
        boolean canDelete=true
        if(count>0){
            canDelete=false
        }
        return canDelete
    }


    public List<GroovyRowResult> lstCriticalDecisionStatus(String monthStr) {
        DateFormat originalFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        Date date = originalFormat.parse(monthStr);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH)+1
        int year = c.get(Calendar.YEAR)
        long empId = currentUserEmployeeId()


        String query = """
                SELECT COUNT(ed2.id) issue_count,"Waiting for Decision" act_name,"#ff8a00" act_color
                FROM ed_dashboard ed
                LEFT JOIN ed_dashboard ed2 ON ed2.id=ed.id AND ed2.is_resolve = FALSE
                WHERE ed.`type`=2 AND ed.create_by = ${empId}
                AND MONTH(ed.create_date) = ${month} AND YEAR(ed.create_date)= ${year}

                UNION ALL

                SELECT COUNT(ed.id)-COUNT(ed2.id) issue_count,"Issue Resolved" act_name,"#069302" act_color
                FROM ed_dashboard ed
                LEFT JOIN ed_dashboard ed2 ON ed2.id=ed.id AND ed2.is_resolve = FALSE
                WHERE ed.`type`=2 AND ed.create_by = ${empId}
                AND MONTH(ed.create_date) = ${month} AND YEAR(ed.create_date)= ${year}
        """
        List<GroovyRowResult> lst = groovySql.rows(query)
        return lst
    }

    public List<GroovyRowResult> lstMeetingStatus(String monthStr) {
        DateFormat originalFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        Date date = originalFormat.parse(monthStr);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH)+1
        int year = c.get(Calendar.YEAR)

        String query = """
                SELECT ms.team_name cat_axe,COUNT(ml.id) m_count
                FROM meeting_log ml
                JOIN system_entity se ON se.id=ml.meeting_type_id AND se.name='Scheduled Meeting'
                JOIN meeting_schedule ms ON ms.id=ml.schedule_id
                WHERE is_uploaded=TRUE AND YEAR(ml.held_on)= ${year} AND MONTH(ml.held_on)= ${month}
                GROUP BY ms.team_id
        """
        List<GroovyRowResult> lst = groovySql.rows(query)
        return lst
    }
}
