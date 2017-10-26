<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/meetingLog/create">
        <li onclick="addNewLog();"><i class="fa fa-plus-square"></i>Add</li>
    </sec:access>
    <sec:access url="/meetingLog/update">
        <li onclick="editMeetingLog();"><i class="fa fa-edit"></i>Edit</li>
    </sec:access>
    <sec:access url="/meetingLog/delete">
        <li onclick="deleteMeetingLog();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <sec:access url="/meetingLog/uploadMeetingLog">
        <li onclick="uploadMeetingLog();"><i class="fa fa-upload"></i>Upload</li>
    </sec:access>
</ul>
</script>

<script language="javascript">
    var gridMeetingLog,dataSource,logModel,heldOn,multiSelectAttendees,isSubmit;

    $(document).ready(function () {
        onLoadLogPage();
        initMeetingLogGrid();
        initObservable();
    });

    function onLoadLogPage() {
        $("#rowMeetingLog").hide();
        $("#logStr").kendoEditor({
            encoded: false,
            resizable: {
                content: false,
                toolbar: true
            },
            tools: [
                "bold",
                "italic",
                "underline",
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

        $('#heldOn').kendoDateTimePicker({
            format: "dd/MM/yyyy hh:mm tt",
            parseFormats: ["yyyy-MM-dd"]
        }).data("kendoDateTimePicker");
        $("#attendees").kendoMultiSelect({
            dataTextField: "name",
            dataValueField: "id",
            filter: "contains",
            dataSource: getBlankDataSource,
            value: [  ]
        });

        multiSelectAttendees = $("#attendees").data("kendoMultiSelect");
        multiSelectAttendees.setDataSource(${lstEmployee});
        initializeForm($("#meetingLogForm"), onSubmitLog);
        defaultPageTile("Create Meeting",null);
    }
    function dropDownMeetingScheduleChange(){
        showLoadingSpinner(true);
        var actionUrl = "${createLink(controller:'meetingLog', action: 'listByScheduleMeeting')}?scheduleId=" + dropDownScheduleTime.value();
        jQuery.ajax({
            type: 'post',
            url: actionUrl,
            success: function (data, textStatus) {
                $('html,body').scrollTop(0);
                /*var descStr = $("#descStr").data("kendoEditor");
                descStr.value(htmlDecode(data.list[0].descStr));*/
                $("#issues").val(data.list[0].issues);
                $("#attendeesOthers").val(data.list[0].attendeesOthers);

                if (data.list[0].attendeesEmp) multiSelectAttendees.value(data.list[0].attendeesEmp.split(","));

            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
    }

    function executePreCondition() {
        <g:if test="${meetingType=='Monthly'}">
        if(dropDownScheduleTime.value()==''){
            showError('Please select meeting category');
            return false;
        }
        </g:if>
        var serviceId = $("#serviceId").val(),
        heldOn = $("#heldOn").val(),
        attendees = $("#attendees").val(),
        issues = $("#issues").val(),
        logStr = $("#logStr").val(),
        descStr = $("#descStr").val();
        if (serviceId=='') {
            showError('Please select service');
            return false;
        }else if(heldOn == ''){
            showError('Please select meeting date');
            return false;
        }else if(attendees == null || attendees == ''){
            showError('Please select attendees');
            return false;
        }else if(issues == null || issues == ''){
            showError('Please insert meeting issues');
            return false;
        }else if (logStr == ''){
            showError('Please insert meeting action log');
            return false;
        }else if (descStr == ''){
            showError('Please insert meeting description');
            return false;
        }
        return true;
    }

    function onSubmitLog() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'meetingLog', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'meetingLog', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#meetingLogForm").serialize(),
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
                var newEntry = result.meetingLog;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridMeetingLog.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridMeetingLog.select();
                    var allItems = gridMeetingLog.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridMeetingLog.removeRow(selectedRow);
                    gridMeetingLog.dataSource.insert(selectedIndex, newEntry);
                }
                emptyForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function emptyForm() {
        clearForm($("#meetingLogForm"), $('#serviceId'));
        initObservable();
    }
    function resetForm() {
        initObservable();
        $('#rowMeetingLog').hide();
        $('#create').html("<span class='k-icon k-i-plus'></span>Save");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "${createLink(controller: 'meetingLog', action: 'list')}?meetingTypeId=" + ${meetingTypeId},
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
                        scheduleId: { type: "number" },
                        serviceId: { type: "number" },
                        meetingCatId: { type: "number" },
                        meetingTypeId: { type: "number" },
                        attendees: { type: "string" },
                        attendeesStr: { type: "string" },
                        attendeesOthers: { type: "string" },
                        heldOn: { type: "date" },
                        issues: { type: "string" },
                        logStr: { type: "string" },
                        descStr: { type: "string" },
                        projectName: { type: "string" },
                        teamName: { type: "string" },
                        participants: { type: "number" }
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'heldOn', dir: 'desc'},
            pageSize: getDefaultPageSize(),
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initMeetingLogGrid() {
        initDataSource();
        $("#gridMeetingLog").kendoGrid({
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
                {field: "heldOn", title: "Date", width: 70, sortable: false, filterable: false,
                    template: "#=kendo.toString(kendo.parseDate(heldOn, 'yyyy-MM-dd HH:mm:ss'), 'dd-MM-yyyy hh:mm tt')#",
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}
                },
                {field: "projectName", title: "Project",width: 100, sortable: false, filterable: false

                },
                {field: "teamName", title: "Team",width: 100, sortable: false, filterable: false
                },
                {field: "participants", title: "Participants",width: 100, sortable: false, filterable: false
                    ,attributes: {style: setAlignRight()}, headerAttributes: {style: setAlignRight()}
                },
                {field: "attendeesStr", title: "Attendees", width: 100, sortable: false, filterable: false,
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}
                    ,template: "#=trimTextForKendo(attendeesStr,200)#"
                },{field: "attendeesOthers", title: "Attendees Others", width: 100, sortable: false, filterable: false,
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}
                    ,template: "#=trimTextForKendo(attendeesOthers,200)#"
                },
                {field: "issues", title: "Agenda",width: 150, sortable: false, filterable: false
                    ,template: "#=trimTextForKendo(issues,200)#"
                },
                {field: "logStr", title: "Action Log",width: 150, sortable: false, filterable: false
                    ,template: "#=htmlDecode(logStr)#"
                },
                {field: "descStr", title: "Description",width: 150, sortable: false, filterable: false
                    ,template: "#=htmlDecode(descStr)#"
                }
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridMeetingLog = $("#gridMeetingLog").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }
    function htmlDecode(value) {
        return value.replaceHtmlEntites();
    }
    function initObservable() {
        logModel = kendo.observable(
                {
                    meetingLog: {
                        id: "",
                        version: "",
                        scheduleId: "",
                        attendees: "",
                        serviceId: ${serviceId},
                        meetingTypeId: ${meetingTypeId},
                        attendeesOthers: "",
                        heldOn: "",
                        issues: "",
                        logStr: "",
                        descStr: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), logModel);
    }
    function uploadMeetingLog() {
        if (executeCommonPreConditionForSelectKendo(gridMeetingLog, 'log') == false) {
            return;
        }
        var msg = 'Are you sure you want to upload the selected meeting minutes?',
                url = "${createLink(controller: 'meetingLog', action:  'uploadMeetingLog')}";
        confirmDelete(msg, url, gridMeetingLog);
    }
    function deleteMeetingLog() {
        if (executeCommonPreConditionForSelectKendo(gridMeetingLog, 'log') == false) {
            return;
        }
        var msg = 'Are you sure you want to delete the selected record?',
                url = "${createLink(controller: 'meetingLog', action:  'delete')}";
        confirmDelete(msg, url, gridMeetingLog);
    }
    function addNewLog(){
        $("#rowMeetingLog").show();
    }
    function editMeetingLog() {
        if (executeCommonPreConditionForSelectKendo(gridMeetingLog, 'log') == false) {
            return;
        }
        $("#rowMeetingLog").show();
        var meetingLog = getSelectedObjectFromGridKendo(gridMeetingLog);
        resetFormValue(meetingLog);
    }

    function resetFormValue(meetingLog) {
        $('html,body').scrollTop(0);
        logModel.set('meetingLog', meetingLog);
        dropDownScheduleTime.value(meetingLog.scheduleId);
        $("#attendeesOthers").val(meetingLog.attendeesOthers);
        var descStr = $("#descStr").data("kendoEditor");
        descStr.value(htmlDecode(meetingLog.descStr));
        var logStr = $("#logStr").data("kendoEditor");
        logStr.value(htmlDecode(meetingLog.logStr));
        if (meetingLog.attendees) multiSelectAttendees.value(meetingLog.attendees.split(","));
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

</script>
