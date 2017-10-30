<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/decisionMaking/update">
        <li onclick="editDashboard();"><i class="fa fa-edit"></i>Advice/Suggestion</li>
    </sec:access>
</ul>
</script>

<script language="javascript">
    var gridIssues, dataSource, dataSourceResolve, dropDownEmployee, monthFor,decisionMakingModel;

    $(document).ready(function () {
        onLoadEdDashboardPage();
        initIssueGrid();
        initResolvedIssueGrid();
        initObservable();
    });
    function onLoadEdDashboardPage() {
        activaTab('menu1');
        $("#rowDecisionMaking").hide();
        initializeForm($("#decisionMakingForm"), onSubmitEdDashboard);
        defaultPageTile("Decision Making", "decisionMaking/show?type=givenDecision");
    }
    function activaTab(tab) {
        $('.nav-tabs a[href="#' + tab + '"]').tab('show');
    }
    function openDecisionMakingWindow(){
        $("#rowDecisionMaking").show();
    }
    function resetForm(){
        clearForm($("#decisionMakingForm"), $('#recipientId'));
        $("#rowDecisionMaking").hide();
    }

    function initObservable() {
        decisionMakingModel = kendo.observable(
                {
                    decisionMaking: {
                        id: "",
                        version: "",
                        createBy: "",
                        recipientId: "",
                        issueName: "",
                        description: "",
                        edAdvice: "",
                        monthFor: "",
                        remarks: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), decisionMakingModel);
    }
    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "${createLink(controller: 'decisionMaking', action: 'list')}?type=givenDecision&isResolved=false",
                    dataType: "json",
                    type: "post"
                }
            },
            schema: {
                type: 'json',
                data: "list", total: "count",
                model: {
                    fields: {
                        id: {type: "number"},
                        version: {type: "number"},
                        issueName: {type: "string"},
                        description: {type: "string"},
                        remarks: {type: "string"},
                        edAdvice: {type: "string"},
                        monthFor: {type: "date"},
                        createBy: {type: "number"},
                        recipientId: {type: "number"},
                        recipientName: {type: "string"},
                        adviceDate: {type: "date"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'id', dir: 'asc'},
            pageSize: getDefaultPageSize(),
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initIssueGrid() {
        initDataSource();
        $("#gridIssues").kendoGrid({
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
                {field: "owner", title: "Issued By", width: "10%", sortable: false, filterable: false},
                {field: "issueName", title: "Issue", width: "10%", sortable: false, filterable: false},
                {field: "description", title: "Description", width: "18%", sortable: false, filterable: false},
                {field: "remarks", title: "Remarks & Recommendations", width: "19%", sortable: false,filterable: false},
                {field: "edAdvice", title: "Advice/Suggestions", width: "20%", sortable: false, filterable: false},
                {field: "monthFor", title: "Expected Date", width: "10%", sortable: false, filterable: false,
                    template: "#=monthFor?kendo.toString(kendo.parseDate(monthFor, 'yyyy-MM-dd'), 'dd-MM-yyyy'):''#",
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}},
                {field: "adviceDate", title: "Advice Date", width: "8%", sortable: false, filterable: false,
                    template: "#=adviceDate?kendo.toString(kendo.parseDate(adviceDate, 'yyyy-MM-dd'), 'dd-MM-yyyy'):''#",
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}},
                {field: "isResolve", title: "Resolved", width: "8%", sortable: false, filterable: false,
                template: "#=isResolve?'YES':'NO'#",attributes: {style: setAlignCenter()},
                    headerAttributes: {style: setAlignCenter()}}
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridIssues = $("#gridIssues").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }


    function onSubmitEdDashboard() {
        if (executePreCondition() == false) {
            return false;
        }
        showLoadingSpinner(true);
        var formData = jQuery('#decisionMakingForm').serializeArray();
        formData.push({name: 'advicePanel', value: ''});

        jQuery.ajax({
            type: 'post',
            data: formData,
            url: "${createLink(controller:'decisionMaking', action: 'update')}",
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

    function executePreCondition() {
        if (!validateForm($("#decisionMakingForm"))) {
            return false;
        }
        return true;
    }
    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.decisionMaking;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridIssues.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridIssues.select();
                    var allItems = gridIssues.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridIssues.removeRow(selectedRow);
                    gridIssues.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function editDashboard() {
        if (executeCommonPreConditionForSelectKendo(gridIssues, 'issue') == false) {
            return;
        }
        $("#rowDecisionMaking").show();
        var decisionMaking = getSelectedObjectFromGridKendo(gridIssues);
        showDecisionMakingEditPanel(decisionMaking);
    }

    function showDecisionMakingEditPanel(decisionMaking) {
        decisionMakingModel.set('decisionMaking', decisionMaking);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function reloadGrid(gridName){
        $(gridName).data("kendoGrid").read();
    }
    function initDataSourceResolved() {
        dataSourceResolve = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "${createLink(controller: 'decisionMaking', action: 'list')}?type=givenDecision&isResolved=true",
                    dataType: "json",
                    type: "post"
                }
            },
            schema: {
                type: 'json',
                data: "list", total: "count",
                model: {
                    fields: {
                        id: {type: "number"},
                        version: {type: "number"},
                        owner: {type: "string"},
                        issueName: {type: "string"},
                        description: {type: "string"},
                        remarks: {type: "string"},
                        edAdvice: {type: "string"},
                        monthFor: {type: "date"},
                        statusChangeDate: {type: "date"},
                        recipientId: {type: "number"},
                        recipientName: {type: "string"},
                        edAdviceDate: {type: "date"},
                        isResolve: {type: "boolean"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'id', dir: 'asc'},
            pageSize: getDefaultPageSize(),
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }
    function initResolvedIssueGrid() {
        initDataSourceResolved();
        $("#gridResolvedIssues").kendoGrid({
            dataSource: dataSourceResolve,
            height: getGridHeightKendo()-60,
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
                {field: "owner", title: "Issued By", width: "10%", sortable: false, filterable: false},
                {field: "issueName", title: "Issue", width: "10%", sortable: false, filterable: false},
                {field: "description", title: "Description", width: "18%", sortable: false, filterable: false},
                {field: "remarks", title: "Remarks & Recommendations", width: "19%", sortable: false,filterable: false},
                {field: "edAdvice", title: "Advice/Suggestions", width: "20%", sortable: false, filterable: false},
                {field: "monthFor", title: "Expected Date", width: "10%", sortable: false, filterable: false,
                    template: "#=monthFor?kendo.toString(kendo.parseDate(monthFor, 'yyyy-MM-dd'), 'dd-MM-yyyy'):''#",
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}},
                {field: "adviceDate", title: "Advice Date", width: "8%", sortable: false, filterable: false,
                    template: "#=adviceDate?kendo.toString(kendo.parseDate(adviceDate, 'yyyy-MM-dd'), 'dd-MM-yyyy'):''#",
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}},
                {field: "isResolve", title: "Resolve Date", width: "9%", sortable: false, filterable: false,
                    template:"#=isResolve?kendo.toString(kendo.parseDate(statusChangeDate, 'yyyy-MM-dd'), 'dd-MM-yyyy'):''#"}
            ],
            filterable: {
                mode: "row"
            }
        });
    }


</script>
