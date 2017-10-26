package pms.usage

import pms.BaseTagLibExecutor
import taglib.GetDropDownCxProjectTaglibActionService
import taglib.GetDropDownCxTeamTaglibActionService
import taglib.GetDropDownEmployeeTaglibActionService
import taglib.GetDropDownGoalTaglibActionService
import taglib.GetDropDownMeetingCategoryTaglibActionService
import taglib.GetDropDownMeetingScheduleTaglibActionService
import taglib.GetDropDownProjectTypeTaglibActionService
import taglib.GetDropDownServiceCategoryTaglibActionService
import taglib.GetDropDownServiceTaglibActionService
import taglib.GetDropDownSystemEntityTypeTaglibActionService

class ApplicationTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetDropDownSystemEntityTypeTaglibActionService getDropDownSystemEntityTypeTaglibActionService
    GetDropDownServiceCategoryTaglibActionService getDropDownServiceCategoryTaglibActionService
    GetDropDownMeetingCategoryTaglibActionService getDropDownMeetingCategoryTaglibActionService
    GetDropDownServiceTaglibActionService getDropDownServiceTaglibActionService
    GetDropDownEmployeeTaglibActionService getDropDownEmployeeTaglibActionService
    GetDropDownProjectTypeTaglibActionService getDropDownProjectTypeTaglibActionService
    GetDropDownGoalTaglibActionService getDropDownGoalTaglibActionService
    GetDropDownCxProjectTaglibActionService getDropDownCxProjectTaglibActionService
    GetDropDownCxTeamTaglibActionService getDropDownCxTeamTaglibActionService
    GetDropDownMeetingScheduleTaglibActionService getDropDownMeetingScheduleTaglibActionService


    /**
     * Render html select of Department
     * example: <depart:dropDownDepartment id=""></depart:dropDownDepartment>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name of html component
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationmessage - validation message to be shown (Default is 'Required')
     * @attr data-bind - bind with kendo observable
     */
    def dropDownSystemEntityType = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownSystemEntityTypeTaglibActionService, attrs)
        out << (String) attrs.html
    }

    def dropDownServiceCategory = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownServiceCategoryTaglibActionService, attrs)
        out << (String) attrs.html
    }
    def dropDownMeetingCategory = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownMeetingCategoryTaglibActionService, attrs)
        out << (String) attrs.html
    }
    def dropDownService = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownServiceTaglibActionService, attrs)
        out << (String) attrs.html
    }
    def dropDownEmployee = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownEmployeeTaglibActionService, attrs)
        out << (String) attrs.html

    }
    def dropDownProjectsType = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownProjectTypeTaglibActionService, attrs)
        out << (String) attrs.html
    }
    def dropDownGoals = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownGoalTaglibActionService, attrs)
        out << (String) attrs.html
    }
    def dropDownMeetingSchedule = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownMeetingScheduleTaglibActionService, attrs)
        out << (String) attrs.html
    }
    def dropDownCxProject = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownCxProjectTaglibActionService, attrs)
        out << (String) attrs.html

    }
    def dropDownCxTeam = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownCxTeamTaglibActionService, attrs)
        out << (String) attrs.html

    }
}
