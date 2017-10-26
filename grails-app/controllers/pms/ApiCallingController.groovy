package pms

import com.pms.PropertiesReader
import grails.converters.JSON

class ApiCallingController  extends BaseController {
    private static final String API_URL = PropertiesReader.getProperty ("remote.api.url", PropertiesReader.CONFIG_FILE_DB)

    static allowedMethods = [
            teamListByProjectId: "POST", projectList: "POST", teamList: "POST"
    ]

    BaseService baseService

    def projectList(){
        String apiString = API_URL + "/setup/getProjects"
        def resp = baseService.rest.post(apiString){
            accept "application/json"
            contentType "application/json"
        }
        Map result = [lstProject: resp.json as JSON]
        render result as JSON
    }

    def teamList(){
        String apiString = API_URL + "/setup/getTeams"
        def resp = baseService.rest.post(apiString){
            accept "application/json"
            contentType "application/json"
        }
        Map result = [lstTeam: resp.json as JSON]
        render result as JSON
    }

    def teamListByProjectId(){
        String apiString = API_URL + "/setup/getProjectAndTeam"
        int projectId = Integer.parseInt(params.projectId.toString())
        String teamType=params.teamType.toString()
        def resp = baseService.rest.post(apiString){
            accept "application/json"
            contentType "application/json"
            json{
                project_id= projectId
                team_type=teamType
            }
        }
        def lstCxTeam = resp.json
        lstCxTeam = baseService.listForKendoDropdown(lstCxTeam.data, "team_name", "Please Select")

        Map result = [lstTeam: lstCxTeam]
        render result as JSON
    }
}
