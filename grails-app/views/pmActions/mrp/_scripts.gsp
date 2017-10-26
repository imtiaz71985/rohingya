<script type="text/x-kendo-tmpl" id="template1">
    <tr>
        <td width='5%'>#:sequence#</td>
        <td width='90%'>#:goal#</td>
    </tr>
</script>

<script language="javascript">
    var month, serviceId, dropDownService,listViewGoal, gridAction, isApplicable = false,hidden=false,monthKendo,extendMonth;

    $(document).ready(function () {
        onLoadInfoPage();
        initListView();
        initGrid();
        $(document).on("input", ".amount", calculateTarget);
        if('${submissionDate}'!=''){
            onSubmitForm();
        }
    });
    function onLoadInfoPage() {
        serviceId = ${serviceId};
        dropDownService.value(serviceId);
        var str = moment().format('MMMM YYYY');
        monthKendo = $('#month').kendoDatePicker({
            format: "MMMM yyyy",
            parseFormats: ["yyyy-MM-dd"],
            start: "year",
            depth: "year"
        }).data("kendoDatePicker");
        var submissionDate='${submissionDate}';
        var nextMonth = moment(submissionDate).add(1, 'months');
        if(submissionDate!='') {
            var st = new Date(moment(nextMonth));
            monthKendo.min(submissionDate);
            monthKendo.max(st);
            monthKendo.value(moment(submissionDate).format('MMMM YYYY'));

        } else{
            monthKendo.min(new Date(moment(new Date()).startOf('year')));
            monthKendo.max(new Date(moment(new Date()).endOf('year')));
            monthKendo.value(str);
        }
        initializeForm($("#detailsForm"), onSubmitForm);
        defaultPageTile("Strategic Plan", 'pmActions/achievement');
    }
    function setMinMonth() {

        var actionUrl = "${createLink(controller:'pmActions', action: 'lastSubDateByService')}";
        serviceId = $('#serviceId').val();

        jQuery.ajax({
            type: 'post',
            data: {serviceId: serviceId},
            url: actionUrl,
            success: function (data, textStatus) {
                var submissionDate=data.subDate;
                var nextMonth = moment(submissionDate).add(1, 'months');
                if(submissionDate!='') {
                    var st = new Date(moment(nextMonth));
                    monthKendo.min(submissionDate);
                    monthKendo.max(st);
                    monthKendo.value(moment(submissionDate).format('MMMM YYYY'));

                } else{
                    monthKendo.min(new Date(moment(new Date()).startOf('year')));
                    monthKendo.max(new Date(moment(new Date()).endOf('year')));
                    monthKendo.value(str);
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.info('error');
            },
            complete: function (XMLHttpRequest, textStatus) {
                console.info('complete');
            }

        });


    }
    function initListView() {
        $("#lstGoal").kendoListView({
            autoBind: false,
            dataSource: {
                transport: {
                    read: {
                        url: false, dataType: "json", type: "post"
                    }
                }, schema: {
                    type: 'json', data: "list"
                }
            },
            template: kendo.template($("#template1").html())
        });
        listViewGoal = $("#lstGoal").data("kendoListView");
    }
    function initGrid() {
        $("#grid").kendoGrid({
            dataSource: {
                transport: {
                    read: {
                        url: false,
                        dataType: "json",
                        type: "post"
                    }
                },
                schema: {
                    type: 'json',
                    data: "list"
                },
                serverPaging: true,
                serverSorting: true
            },
            autoBind: false,
            height: getGridHeightKendo() - 50,
            sortable: false,
            pageable: false,
            detailInit: actionsDetails,
            dataBound: function () {
                this.expandRow(this.tbody.find("tr.k-master-row"));
            },
            columns: [
                {
                    field: "sequence", title: "ID#", width: 40, sortable: false, filterable: false,
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}
                },
                {field: "actions", title: "Action", width: 200, sortable: false, filterable: false},
                {
                    field: "start", title: "Start Date", width: 50, sortable: false, filterable: false,
                    template: "#=kendo.toString(kendo.parseDate(start, 'yyyy-MM-dd'), 'MMMM')#"
                },
                {
                    field: "end", title: "End Date", width: 50, sortable: false, filterable: false,
                    template: "#=formatExtendedDateStrike(kendo.toString(kendo.parseDate(end, 'yyyy-MM-dd'), 'MMMM'),extendedEnd)#"
                },
                {field: "resPerson", title: "Responsible Person", width: 90, sortable: false, filterable: false},
                {
                    field: "supportDepartmentStr", title: "Support Department", width: 90,
                    sortable: false, filterable: false
                },
                {field: "sourceOfFundStr", title: "Project", width: 80, sortable: false, filterable: false},
                {
                    field: "note",
                    title: "Remarks",
                    width: 120,
                    sortable: false,
                    filterable: false
                }
            ]
        });
        gridAction = $("#grid").data("kendoGrid");
    }

    function actionsDetails(e) {
        $("<div/>").appendTo(e.detailCell).kendoGrid({
            dataSource: {
                transport: {
                    read: {
                        url: "${createLink(controller: 'pmActions', action: 'listAchievement')}?serviceId=" + e.data.serviceId
                        + "&month=" + month + "&type=Details",
                        dataType: "json",
                        type: "post"
                    },
                    update: {
                        url: "${createLink(controller: 'pmActions', action: 'updateAchievement')}",
                        dataType: "json",
                        type: "post"
                    }
                },
                requestEnd: function (e) {
                    var response = e.response;

                    if (e.type == 'update') {
                        var isError = response["isError"];
                        var message = response["message"];
                        if (isError) {
                            if(message=="OpenPopup"){
                                showIncompleteIndicatorModal(response);
                            }else
                            showError(message);
                            var grid = $("#grid").data("kendoGrid");
                            var data = grid.dataSource;
                            data.read();
                        }else{
                            showSuccess(message);
                        }
                    }
                },
                schema: {
                    type: 'json',
                    data: "list",
                    model: {
                        id: "ind_details_id",    // have to set id otherwise remove row by clicking cancel
                        fields: {
                            id: {type: "number"},
                            ind_details_id: {type: "number"},
                            indicator: {editable: false, type: "string"},
                            unit_id: {editable: false, type: "number"},
                            unit_str: {editable: false, type: "string"},
                            indicator_type: {editable: false, type: "string"},
                            target: {editable: false, type: "string"},
                            total_achievement: {editable: false, type: "string"},
                            monthly_target: {editable: false, type: "number"},
                            is_excluded: {editable: true,type: "boolean"},
                            achievement: {type: "number"},
                            remarks: {type: "string"},
                            closing_note: {type: "string"}
                        }
                    }
                },
                serverPaging: true,
                serverSorting: true,
                serverFiltering: true,
                batch: true,
                pageSize: 50,
                filter: {field: "actionsId", operator: "eq", value: e.data.id}
            },
            selectable: false,
            sortable: false,
            resizable: false,
            reorderable: false,
            filterable: false,
            pageable: false,
/*            cancel: function(e){
                $("#grid").data("kendoGrid").cancelRow();
                $("#grid").data("kendoGrid").refresh();
            },
            edit:  onRowBound,
            dataBound: function onRowBound(e){
                var data = this.dataSource.data();
                $.each(data, function (i, row) {
                    if (row.get("indicator_type") != 'Repeatable%') {
                        var currenRow = $('tr[data-uid="' + row.uid + '"] ');
                        var chkbx = $(currenRow).find(".chkbx");
                        chkbx.hide();
                    }
                });
            },*/
            editable:"inline",
            columns: [
                {field: "indicator", title: "Indicator", width: "220px"},
                {
                    field: "indicator_type", title: "Type", width: "80px",
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}
                },
                {
                    field: "monthly_target", title: "Target", width: "80px",
                    template: "#=formatIndicator(indicator_type,monthly_target)#",
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}
                },
                {
                    field: "achievement", title: "Achievement", width: "100px", format: "{0:n0}",
                    template: "#=formatIndicator(indicator_type,achievement)#",
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}
                },
                {
                    field: "is_excluded",title: "N/A",width: 30,
                    template: '<input type="checkbox" class="chkbx" #= is_excluded ? "checked=checked" : "" #></input>'
                },
                {field: "remarks", title: "Remarks", width: "280px",template: "#=formatRemarks(remarks,closing_note)#",
                    editor: textEditorInitialize }
                <g:if test="${!isAdmin}">
                ,{command: [
                    {name:"edit",
                    click: function(e) {
                        // prevent page scroll position change
                        e.preventDefault();
                    },
                    text:{edit: "Achievement",update:"Save",cancel: "Cancel"}}], title: "&nbsp;", width: "130px"}

                </g:if>
            ]
        });
    }

    var textEditorInitialize = function(container, options) {
        $('<textarea name="' + options.field + '" style="width: ' + container.width() + 'px;height:' + container.height() + 'px" />')
                .appendTo(container);
    };

    function formatRemarks(remarks, closingNote) {
        if (closingNote!='') {
            return remarks+'</br><b>Closing note: </b>'+closingNote;
        }

        return remarks
    }
    function formatIndicator(indicatorType, target) {
        if (!target && indicatorType.match('%')) {
            return ' 0% ';
        }
        if (!target && !indicatorType.match('%')) {
            return ' 0 ';
        }
        if (indicatorType.match('%')) {
            return target + ' % ';
        }
        return target
    }

    function onSubmitForm() {
        month = $('#month').val();
        var serviceId = dropDownService.value();
        if (serviceId == '') {
            showError('Please select any service');
            return false;
        }
        var urlGoal = "${createLink(controller: 'pmActions', action: 'listAchievement')}?serviceId=" + serviceId + "&month=" + month + "&type=Goals";
        var url = "${createLink(controller: 'pmActions', action: 'listAchievement')}?serviceId=" + serviceId + "&month=" + month + "&type=Actions";
        populateGridKendo(listViewGoal, urlGoal);
        populateGridKendo(gridAction, url);
        return false;
    }
    function showIncompleteIndicatorModal(response) {
        $('#hfPrevExtendedEnd').val(response["prevExtendedEnd"]);

         extendMonth = $('#extendedEndMonth').kendoDatePicker({
            format: "MMMM yyyy",
            parseFormats: ["yyyy-MM-dd"],
            start: "year",
            depth: "year",
            change:setExtendedTarget
        }).data("kendoDatePicker");
        if ($('#month').val() != '') {
            var sDate ;
            if(moment($('#month').val(), 'MMMM').add(1, 'M').format('YYYY-MM-DD')==$('#hfPrevExtendedEnd').val())
                sDate= moment($('#hfPrevExtendedEnd').val()).add(1, 'M').format('YYYY-MM-DD');
            else
                sDate= moment($('#month').val(), 'MMMM').add(1, 'M').format('YYYY-MM-DD');
            extendMonth.min(sDate);
            var calYear = moment(sDate).format('YYYY');
            var ed = new Date(moment(calYear).endOf('year'));
            extendMonth.max(ed);
        } else {
            var sDate = new Date();
            extendMonth.min(moment(sDate).format('YYYY-MM-DD'));
            var calYear = moment(sDate).format('YYYY');
            var ed = new Date(moment(calYear).endOf('year'));
            extendMonth.max(ed);
        }

       var rowIdx=response["id"],totalTarget=response["totalTarget"],totalAcv=response["totalAchievement"],indType=response["indType"];
        $('#hfIndicatorDetailsId').val(rowIdx);
        $('#hfIndicatorDetailsMonth').val($('#month').val());
        $('#hfIndicatorDetailsAcv').val(response["achievement"]);
        $('#hfIndicatorDetailsRemarks').val(response["remarks"]);
        $('#extendedModalTargetLbl').text('0');
        $('#extendedEndMonth').val('');

        if(response["isExtend"]!='true' && response["closingNote"]!=''){
            $('#selectionExtendMonth').prop('checked', false);
            $('#selectionCloseWithRemain').prop('checked', true);
            $('#IndicatorClosingNote').val(response["closingNote"]);
            $('#divIndicatorClosingNote').show();
            $('#divExtendedEndMonth').hide();
        }else {
            if(response["extendedEnd"]!='') {
                extendMonth.value(moment(response["extendedEnd"]).format('MMMM YYYY'));
                setExtendedTargetForEdit(response["lstExtend"]);
            }
            if(moment($('#month').val(), 'MMMM').format('M')==12){
                $('#selectionExtendMonth').prop('checked', false);
                $('#selectionCloseWithRemain').prop('checked', true);
                $('#divIndicatorClosingNote').show();
                $('#divExtendedEndMonth').hide();
            }
            else {
                $('#selectionExtendMonth').prop('checked', true);
                $('#selectionCloseWithRemain').prop('checked', false);
            }
        }
        $('#totalTargetExtend').text(formatIndicator(indType,totalTarget));
        $('#totalAcvExtend').text(formatIndicator(indType,totalAcv));
        var remaining=parseFloat(totalTarget)-parseFloat(totalAcv);
        $('#totalRemainingExtend').text(formatIndicator(indType,remaining));
        $('#hfIndicatorDetailsRemainsTarget').val(remaining);

        $("#createIncompleteActionManage").modal('show');

    }
    function hideModal() {
        $('#hfIndicatorDetailsId').val('');
        $('#hfIndicatorDetailsMonth').val('');
        $('#selectionExtendMonth').prop('checked', true);
        $('#selectionCloseWithRemain').prop('checked', false);
        $('#hfIndicatorDetailsAcv').val('');
        $('#hfIndicatorDetailsRemarks').val('');
        $('#hfExtendDateCount').val('');
        $('#hfIndicatorDetailsRemainsTarget').val('');
        $('#hfPrevExtendedEnd').val('');
        $('#divIndicatorClosingNote').hide();
        $('#IndicatorClosingNote').val('');
        $('#extendedEndMonth').val('');
        $('#totalTargetExtend').text('');
        $('#totalAcvExtend').text('');
        $('#extendedModalTargetLbl').text('0');
        $('#divExtendedEndMonth').show();
        $("#i_logic_extend tr").remove();
        $("#createIncompleteActionManage").modal('hide');
        var grid = $("#grid").data("kendoGrid");
        var data = grid.dataSource;
        data.read();
    }
    function extendMonthSelect() {
        if(moment($('#month').val(), 'MMMM').format('M')==12) {
            $('#selectionExtendMonth').prop('checked', false);
            $('#selectionCloseWithRemain').prop('checked', true);
            return false;
        }
        $('#IndicatorClosingNote').val('');
        $('#divIndicatorClosingNote').hide();
        $('#extendedEndMonth').val('');
        $('#divExtendedEndMonth').show();
        $("#i_logic_extend tr").remove();
    }
    function closeIndicatorSelect() {
        $('#IndicatorClosingNote').val('');
        $('#divIndicatorClosingNote').show();
        $('#extendedEndMonth').val('');
        $('#extendedModalTargetLbl').text('0');
        $('#divExtendedEndMonth').hide();
        $("#i_logic_extend tr").remove();
    }
    function setExtendedTarget() {
        $("#i_logic_extend tr").remove();
        var start;
        if($('#hfPrevExtendedEnd').val()!='')
            start = moment($('#hfPrevExtendedEnd').val()).add(1, 'M').format('MMMM YYYY');

        else
            start = moment($('#month').val(), 'MMMM').add(1, 'M').format('MMMM YYYY');

        var end = moment($('#extendedEndMonth').val(), 'MMMM').format('MMMM YYYY');
        var list = monthNamesFromRange(start, end);
        var count = monthDifference(start, end);
        $('#hfExtendDateCount').val(count);
        $('#extendedModalTargetLbl').text('0');

        var t = 0;
        for (var i = 0; i < count; i++) {
            var tmpAmt = '';
            var trId = 'iddr' + (i + 1);
            var trData = "<tr id='" + trId + "'>" +
                    "<td width='60%'>" +
                    "<input name='month" + (i + 1) + "' value='" + list[i] + "' type='text' readonly='true' class='form-control'/>" +
                    "</td>" +
                    "<td width='20%'>" +
                    "<input id='target" + (i + 1) + "' name='target" + (i + 1) + "' tabindex=" + (i + 9) + " " +
                    "class='form-control amount' value='" + tmpAmt + "' type='text' placeholder='Target'>" +
                    "</td>" +
                    "</tr>";
            $('#i_logic_extend').append(trData);
            t += 2;
        }
    }
    function setExtendedTargetForEdit(listTarget) {
        $("#i_logic_extend tr").remove();
        if($('#hfPrevExtendedEnd').val()!='')
            start=moment($('#hfPrevExtendedEnd').val()).add(1, 'M').format('MMMM YYYY');
        else
            start=moment($('#month').val(), 'MMMM').add(1, 'M').format('MMMM YYYY');
        var end = moment($('#extendedEndMonth').val(), 'MMMM').format('MMMM YYYY');
        var count = monthDifference(start, end);
        $('#hfExtendDateCount').val(count);
        var t = 0,totalTarget=0;

        for (var i = 0; i < count; i++) {
                    var trId = 'iddr' + (i + 1);
                    var trData = "<tr id='" + trId + "'>" +
                            "<td width='60%'>" +
                            "<input name='month" + (i + 1) + "' value='" + listTarget[i].monthName + "' type='text' readonly='true' class='form-control'/>" +
                            "</td>" +
                            "<td width='20%'>" +
                            "<input id='target" + (i + 1) + "' name='target" + (i + 1) + "' tabindex=" + (i + 9) + " " +
                            "class='form-control amount' value='" + listTarget[i].target + "' type='text' placeholder='Target'>" +
                            "</td>" +
                            "</tr>";
                    $('#i_logic_extend').append(trData);
                    t += 2;
                    totalTarget+=listTarget[i].target;

        }
        $('#extendedModalTargetLbl').text(totalTarget);
    }
    function calculateTarget() {
            var sum = 0;
            $(".amount").each(function () {

                if (!isNaN(this.value) && this.value.length != 0) {

                    sum += parseInt(this.value);

                }
                else {
                    if (this.value != '') {
                        this.value = 0;
                    }
                }
            });
        if(sum>$('#hfIndicatorDetailsRemainsTarget').val()){
            showError('Current target & remaining target is not equal.');
            this.value = '';
            return false;
        }
            $('#extendedModalTargetLbl').text(sum);
    }
    function onSubmitExtendedIndicator() {
        if($('input[name=selection]:checked').val()=='ExtendMonth' && ($('#extendedModalTargetLbl').text()!=$('#hfIndicatorDetailsRemainsTarget').val())){
            showError('Current target & remaining target is not equal.');
            return false;
        }
        else if($('input[name=selection]:checked').val()=='CloseWithRemain' && $('#IndicatorClosingNote').val().trim().isEmpty()){
            showError('Closing note is empty.');
            return false;
        }

        showLoadingSpinner(true);

        jQuery.ajax({
            type: 'post',
            data: jQuery("#createIncompleteActionManageForm").serialize(),
            url: "${createLink(controller:'pmActions', action: 'updateExtendedTimeAchievement')}",
            success: function (data, textStatus) {
                if (data.isError) {
                    showError(data.message);
                    return false;
                }
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                showError(textStatus.message());
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
        } else {
            showSuccess(result.message);
            hideModal();
        }
        showLoadingSpinner(false);

    }
</script>