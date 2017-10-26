package taglib

import grails.converters.JSON
import grails.transaction.Transactional
import org.apache.log4j.Logger
import pms.ActionServiceIntf
import pms.BaseService

@Transactional
class GetDropDownCxTeamTaglibActionService extends BaseService implements ActionServiceIntf {

    private static final String NAME = 'name'
    private static final String ID = 'id'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String ERROR_FOR_INVALID_INPUT = 'Error for invalid input'

    private Logger log = Logger.getLogger(getClass())

    /** Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param parameters - a map of given attributes
     * @return - a map containing all necessary properties with value
     */
    public Map executePreCondition(Map params) {
        try {
            String name = params.get(NAME)
            String id = params.get(ID)
            String dataModelName = params.get(DATA_MODEL_NAME)

            // check required parameters
            if ((!id) || (!name) || (!dataModelName)) {
                return setError(params, ERROR_FOR_INVALID_INPUT)
            }

            // Set default values for optional parameters
            params.put(HINTS_TEXT, params.hints_text ? params.hints_text : PLEASE_SELECT)
            params.put(SHOW_HINTS, params.show_hints ? new Boolean(Boolean.parseBoolean(params.show_hints.toString())) : Boolean.TRUE)
            params.put(REQUIRED, params.required ? new Boolean(Boolean.parseBoolean(params.required.toString())) : Boolean.FALSE)
            params.put(VALIDATION_MESSAGE, params.validationmessage ? params.validationmessage : DEFAULT_MESSAGE)

            return params
        } catch (Exception e) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
    }

    /** Get the list of Roles
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @return - html string for 'select'
     */
    public Map execute(Map result) {
        try {
            String apiString = "http://192.168.21.4:9595/rohingya_response/setup/getTeams"
            def resp = rest.post(apiString){
                accept "application/json"
                contentType "application/json"
            }
            String html = buildDropDown(resp.json, result)
            result.html = html
            return result
        } catch (Exception e) {
            log.error(e.message)
            return super.setError(result, INVALID_INPUT_MSG)
        }
    }

    /**
     * Do nothing in post condition
     * @param result - A map returned by execute method
     * @return - returned the received map
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * do nothing for build success operation
     * @param result - A map returned by post condition method.
     * @return - returned the same received map containing isError = false
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Do nothing here
     * @param result - map returned from previous any of method
     * @return - a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    private static final String SELECT_END = "</select>"

    /** Generate the html for select
     * @param lstTeam - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(def lstCxTeam, Map dropDownAttributes) {
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)

        Map attributes = (Map) dropDownAttributes
        String strAttributes = EMPTY_SPACE
        attributes.each {
            if (it.value) {
                strAttributes = strAttributes + "${it.key} = '${it.value}' "
            }
        }

        String html = "<select ${strAttributes}>\n" + SELECT_END

        if (showHints.booleanValue()) {
            lstCxTeam = listForKendoDropdown(lstCxTeam.data, "team_name", "Please Select")
        }
        String jsonData = lstCxTeam as JSON
        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                     if (${dataModelName}){
                            ${dataModelName}.destroy();
                     }
                        \$('#${name}').kendoDropDownList({
                            dataTextField   : 'team_name',
                            dataValueField  : 'team_id',
                            filter          : "contains",
                            suggest         : true,
                            dataSource      : ${jsonData}
                        });
                });
                ${dataModelName} = \$("#${name}").data("kendoDropDownList");
            </script>
        """
        return html + script
    }

}
