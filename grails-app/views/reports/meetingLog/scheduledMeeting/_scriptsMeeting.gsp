<script language="javascript">
    var gridMeeting, dataSource;

    $(document).ready(function () {
        onLoadMeetingPage();
        initMeetingGrid();
        populateMeetingGrid();
    });

    function onLoadMeetingPage() {
        var str = moment().format('YYYY');
        $('#year').kendoDatePicker({
            format: "yyyy",
            parseFormats: ["yyyy-MM-dd"],
            start: "decade",
            depth: "decade",
            change: populateMeetingGrid
        }).data("kendoDatePicker");
        $('#year').val(str);
        defaultPageTile("Scheduled meeting","reports/showMeetingStatus?type=${meetingType}");
    }

    function populateMeetingGrid(){
        var year = $('#year').val();
        if(year==''){
            showError('Please select year');
            return false;
        }
        var params = "?year="+year+"&meetingTypeId=" + ${meetingTypeId};
        var url ="${createLink(controller: 'reports', action: 'listMeetingStatus')}" + params;
        populateGridKendo(gridMeeting, url);
        return false;
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url:false,
                    dataType: "json",
                    type: "post"
                }
            },
            schema: {
                model: {
                    fields: {
                        id: { type: "number" },
                        version: { type: "number" },
                        heldOn: { type: "date" },
                        scheduled_date: { type: "date" },
                        uploadDate: { type: "date" },
                        descStr: { type: "string" },
                        issues: { type: "string" },
                        log_str: { type: "string" },
                        scheduled_date: { type: "date" },
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

    function initMeetingGrid() {
        initDataSource();
        $("#gridMeeting").kendoGrid({
            autoBind:false,
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
                {field: "scheduled_date", title: "Schedule Date", width: 100, sortable: false, filterable: false,
                    template: "#=kendo.toString(kendo.parseDate(scheduled_date, 'yyyy-MM-dd hh:mm:ss'), 'dd-MM-yyyy hh:mm tt')#",
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}
                },
                {field: "projectName", title: "Project",width: 100, sortable: false, filterable: false

                },
                {field: "teamName", title: "Team",width: 100, sortable: false, filterable: false
                },

                {field: "heldOn", title: "Meeting Date", width: 90, sortable: false, filterable: false,
                    template: "#=kendo.toString(kendo.parseDate(heldOn, 'yyyy-MM-dd hh:mm:ss'), 'dd-MM-yyyy hh:mm tt')#",
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}
                },
                {field: "uploadDate", title: "Upload Date", width: 90, sortable: false, filterable: false,
                    template: "#=kendo.toString(kendo.parseDate(uploadDate, 'yyyy-MM-dd'), 'dd-MM-yyyy')#",
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}
                },
                {field: "participants", title: "Participants",width: 100, sortable: false, filterable: false
                    ,attributes: {style: setAlignRight()}, headerAttributes: {style: setAlignRight()}
                },
                {field: "issues", title: "Agenda",sortable: false, filterable: false

                },
                {field: "log_str", title: "Action Log",sortable: false, filterable: false,
                    template: "#=trimTextForKendo(htmlDecode(log_str),430)#"
                },
                {field: "descStr", title: "Description",sortable: false, filterable: false,
                    template: "#=trimTextForKendo(htmlDecode(descStr),430)#"
                }
            ],
            filterable: {
                mode: "row"
            }
        });
        gridMeeting = $("#gridMeeting").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }
    function htmlDecode(value) {
        return value.replaceHtmlEntites();
    }
    $("#gridMeeting").kendoTooltip({
        show: function(e){
            if(this.content.text().length > 200){
                this.content.parent().css("visibility", "visible");
            }
        },
        hide:function(e){
            this.content.parent().css("visibility", "hidden");
        },
        filter: "td:nth-child(3)",
        width: 450,
        position: "top",
        content: function (e) {
            var dataItem = $("#gridMeeting").data("kendoGrid").dataItem(e.target.closest("tr"));
            return dataItem.descStr;
        }
    }).data("kendoTooltip");
    function downloadMeetingFile(id) {
        showLoadingSpinner(true);
        var msg = 'Do you want to download the attachment now?',
                params = "?id=" +id,
                url = "${createLink(controller: 'meetingLog', action:  'downloadFile')}" + params;
        confirmDownload(msg, url);
        return false;
    }


</script>
