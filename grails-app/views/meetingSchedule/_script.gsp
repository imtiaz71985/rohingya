<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/meetingLog/createSchedule">
        <li onclick="addNewSchedule();"><i class="fa fa-plus-square"></i>Add</li>
    </sec:access>
    <sec:access url="/meetingLog/updateSchedule">
        <li onclick="editMeetingSchedule();"><i class="fa fa-edit"></i>Edit</li>
    </sec:access>
  </ul>
</script>

<script language="javascript">
    var gridMeetingSchedule,dataSource,scheduleModel,scheduledDate,multiSelectAttendees,dropDownCxTeam;

    $(document).ready(function () {
        onLoadSchedulePage();
        initMeetingScheduleGrid();
        initObservable();       
    });

    function onLoadSchedulePage() {
        $("#rowMeetingSchedule").hide();
        $("#descStr").kendoEditor({
            encoded: false,
            resizable: {
                content: false,
                toolbar: true
            },
            tools: [
                "bold",
                "italic",
                "underline",
                "backColor",
                "foreColor",
                "justifyLeft",
                "justifyCenter",
                "justifyRight",
                "justifyFull",
                "insertUnorderedList",
                "insertOrderedList",
                "createTable",
                "deleteRow",
                "deleteColumn"
            ]
        });
        $('#scheduledDate').kendoDateTimePicker({
            format: "dd/MM/yyyy hh:mm tt",
            parseFormats: ["yyyy-MM-dd hh:mm tt"]
        }).data("kendoDateTimePicker");

        $("#attendeesEmp").kendoMultiSelect({
            dataTextField: "name",
            dataValueField: "id",
            filter: "contains",
            dataSource: getBlankDataSource,
            value: [  ]
        });
       // dropDownCategory.bind("change", dropDownCategoryChange);

        multiSelectAttendees = $("#attendeesEmp").data("kendoMultiSelect");
        multiSelectAttendees.setDataSource(${lstEmployee});
        dropDownCxTeam = initKendoDropdown($('#teamId'), "team_name","team_id", null);
        dropDownCxTeam.setDataSource(getKendoEmptyDataSource(dropDownCxTeam, null));
        if(dropDownCxProject.dataSource.data().length<=1){
            dropDownCxProject.readonly();
            populateTeamList();
        }
        initializeForm($("#meetingScheduleForm"), onSubmitSchedule);
        defaultPageTile("Create Schedule",null);
    }
    // To populate Team List
    function populateTeamList() {
        var projectId = $("#projectId").val();
        if (projectId == '' || projectId == null) {
            dropDownCxTeam.setDataSource(getKendoEmptyDataSource(dropDownCxTeam, null));
            dropDownCxTeam.value('');
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'apiCalling', action: 'teamListByProjectId')}?projectId=" + projectId+"&teamType=external",
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                    return false;
                }
                console.log(JSON.stringify(data.lstTeam));
                dropDownCxTeam.setDataSource(data.lstTeam);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        return true;
    }
    function executePreCondition() {

        var  scheduledDate = $("#scheduledDate").val(),
        attendeesEmp = $("#attendeesEmp").val(),
                attendeesOthers = $("#attendeesOthers").val(),
                subject = $("#issues").val(),
        descStr = $("#descStr").val();
         if(scheduledDate == ''){
            showError('Please select meeting date');
            return false;
        }else if(attendeesEmp == null || attendeesEmp == ''){
            showError('Please select attendees of Friendship');
            return false;
        }else if(subject == null || subject == ''){
            showError('Please insert meeting subject');
            return false;
        }else if (descStr == ''){
            showError('Please insert meeting description');
            return false;
        }
        return true;
    }

    function onSubmitSchedule() {
        if (executePreCondition() == false) {
            return false;
        }
        $("#projectName").val($("#projectId").data("kendoDropDownList").text());
        $("#teamName").val($("#teamId").data("kendoDropDownList").text());

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'meetingLog', action: 'createSchedule')}";
        } else {
            actionUrl = "${createLink(controller:'meetingLog', action: 'updateSchedule')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#meetingScheduleForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
                setButtonDisabled($('#create'), false);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
        return false;
    }

    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.meetingSchedule;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridMeetingSchedule.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridMeetingSchedule.select();
                    var allItems = gridMeetingSchedule.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridMeetingSchedule.removeRow(selectedRow);
                    gridMeetingSchedule.dataSource.insert(selectedIndex, newEntry);
                }
                emptyForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function emptyForm() {
        clearForm($("#meetingScheduleForm"), $('#serviceId'));
        initObservable();
    }
    function resetForm() {
        initObservable();
        $("#projectName").val('');
        $("#teamName").val('');
        dropDownCxTeam.setDataSource(getKendoEmptyDataSource(dropDownCxTeam, null));
        $('#rowMeetingSchedule').hide();
        $('#create').html("<span class='k-icon k-i-plus'></span>Save");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "${createLink(controller: 'meetingLog', action: 'listSchedule')}",
                    dataType: "json",
                    type: "post"
                }
            },
            schema: {
                type: 'json',
                data: "list", total: "count",
                model: {
                    fields: {
                        id: { type: "number" },
                        version: { type: "number" },
                        projectId:{type: "number" },
                        teamId: { type: "number" },
                        projectName: { type: "string" },
                        teamName: { type: "string" },
                        attendeesEmp: { type: "string" },
                        attendeesEmpStr: { type: "string" },
                        attendeesOthers: { type: "string" },
                        scheduledDate: { type: "date" },
                        issues: { type: "string" },
                        descStr: { type: "string" }
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'scheduledDate', dir: 'desc'},
            pageSize: getDefaultPageSize(),
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initMeetingScheduleGrid() {
        initDataSource();
        $("#gridMeetingSchedule").kendoGrid({
            dataSource: dataSource,
            height: getGridHeightKendo(),
            selectable: true,
            sortable: true,
            resizable: true,
            reorderable: true,
            pageable: {
                refresh: true,
                pageSizes: getDefaultPageSizes(),
                buttonCount: 4
            },
            columns: [
                {field: "scheduledDate", title: "Date", width: 100, sortable: false, filterable: false,
                      template: "#=kendo.toString(kendo.parseDate(scheduledDate,'yyyy-MM-dd hh:mm:ss'), 'dd-MM-yyyy hh:mm tt')#",
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}
                },
                {field: "projectName", title: "Project", width: 100, sortable: false, filterable: kendoCommonFilterable(97)},
                {field: "teamName", title: "Team", width: 100, sortable: false, filterable: kendoCommonFilterable(97)},
                {field: "attendeesEmpStr", title: "Attendees Friendship", width: 200, sortable: false, filterable: false,
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}
                    ,template: "#=trimTextForKendo(attendeesEmpStr,200)#"
                },
                {field: "attendeesOthers", title: "Attendees Others", width: 200, sortable: false, filterable: false,
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}
                    ,template: "#=trimTextForKendo(attendeesOthers,200)#"
                },
                {field: "issues", title: "Subject",width: 150, sortable: false, filterable: false
                    ,template: "#=htmlDecode(issues)#"
                },
                {field: "descStr", title: "Description",width: 300, sortable: false, filterable: false
                    ,template: "#=htmlDecode(descStr)#"
                }
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridMeetingSchedule = $("#gridMeetingSchedule").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }
    function htmlDecode(value) {
        return value.replaceHtmlEntites();
    }
    function initObservable() {
        scheduleModel = kendo.observable(
                {
                    meetingSchedule: {
                        id: "",
                        version: "",
                        attendeesEmp: "",
                        attendeesOthers: "",
                        scheduledDate: "",
                        issues: "",
                        descStr: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), scheduleModel);
    }
    function addNewSchedule(){
        $("#rowMeetingSchedule").show();
    }
    function editMeetingSchedule() {
        if (executeCommonPreConditionForSelectKendo(gridMeetingSchedule, 'Schedule') == false) {
            return;
        }
        $("#rowMeetingSchedule").show();
        var meetingSchedule = getSelectedObjectFromGridKendo(gridMeetingSchedule);
        resetFormValue(meetingSchedule);
        populateTeamList();
        dropDownCxTeam.value(meetingSchedule.teamId);

    }

    function resetFormValue(meetingSchedule) {
        $('html,body').scrollTop(0);
        scheduleModel.set('meetingSchedule', meetingSchedule);
        var descStr = $("#descStr").data("kendoEditor");
        descStr.value(htmlDecode(meetingSchedule.descStr));
        if (meetingSchedule.attendeesEmp) multiSelectAttendees.value(meetingSchedule.attendeesEmp.split(","));
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }
</script>
