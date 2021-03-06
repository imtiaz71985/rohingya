<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/secUser/create">
        <li onclick="showForm();"><i class="fa fa-plus-square-o"></i>New</li>
    </sec:access>
    <sec:access url="/secUser/update">
        <li onclick="editSecUser();"><i class="fa fa-edit"></i>Edit</li>
    </sec:access>
    <sec:access url="/userDepartment/show">
        <li onclick="editHeadInCharge();"><i class="fa fa-black-tie"></i>User Department Mapping</li>
    </sec:access>
</ul>
</script>

<script language="javascript">
    var gridSecUser, dataSource, secUserModel,dropDownEmployee,dropDownService;

    $(document).ready(function () {
        onLoadSecUserPage();
        initSecUserGrid();
        initObservable();
    });

    function onLoadSecUserPage() {
        $("#userRow").hide();
        initializeForm($("#userForm"), onSubmitSecUser);
        defaultPageTile("Update User",null);
    }

    function executePreCondition() {
        if (!validateForm($("#userForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitSecUser() {
        if (executePreCondition() == false) {
            return false;
        }
        var password = $('#password').val(),
            confirmPassword = $('#confirmPassword').val(),
            fullName = $('#username').data("kendoDropDownList").text();
        if(password!=confirmPassword){
            showError('Password mis-match');
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'secUser', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'secUser', action: 'update')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#userForm").serialize()+"&fullName="+fullName,
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
                var newEntry = result.secUser;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridSecUser.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridSecUser.select();
                    var allItems = gridSecUser.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridSecUser.removeRow(selectedRow);
                    gridSecUser.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#userForm"), $('#username'));
        initObservable();
        $("#userRow").hide();
        $('#create').html("<span class='k-icon k-i-plus'></span>Save");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "${createLink(controller: 'secUser', action: 'list')}",
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
                        fullName: { type: "string" },
                        username: { type: "string" },
                        enabled: { type: "boolean" },
                        serviceId: { type: "number"},
                        service: { type: "string"},
                        accountLocked: { type: "boolean" },
                        accountExpired: { type: "boolean" }
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'id', dir: 'asc'},
            pageSize: 50,
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initSecUserGrid() {
        initDataSource();
        $("#gridSecUser").kendoGrid({
            dataSource: dataSource,
            height: getGridHeightKendo(),
            selectable: true,
            sortable: true,
            resizable: true,
            reorderable: true,
            pageable: {
                refresh: true,
                pageSizes: [10, 15, 20],
                buttonCount: 4
            },
            columns: [
                {field: "username", title: "Login ID", width: 70, sortable: false, filterable: kendoCommonFilterable(97)},
                {field: "fullName", title: "User Name", width: 80, sortable: false, filterable: kendoCommonFilterable(97)},
                {field: "service", title: "Department", width: 100, sortable: false, filterable: kendoCommonFilterable(97)},
                {field: "enabled", title: "Enabled", width: 30, sortable: false, filterable: false,attributes: {style: setAlignCenter()},
                    headerAttributes: {style: setAlignCenter()}, template:"#=enabled?'YES':'NO'#"},
                {field: "accountLocked", title: "Locked", width: 30, sortable: false, filterable: false,attributes: {style: setAlignCenter()},
                    headerAttributes: {style: setAlignCenter()}, template:"#=accountLocked?'YES':'NO'#"},
                {field: "accountExpired", title: "Expired", width: 30, sortable: false, filterable: false,attributes: {style: setAlignCenter()},
                    headerAttributes: {style: setAlignCenter()}, template:"#=accountExpired?'YES':'NO'#"}
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridSecUser = $("#gridSecUser").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        secUserModel = kendo.observable(
                {
                    secUser: {
                        id: "",
                        version: "",
                        serviceId: "",
                        username: "",
                        enabled: false,
                        accountLocked: false,
                        accountExpired: false
                    }
                }
        );
        kendo.bind($("#application_top_panel"), secUserModel);
    }
    function showForm() {
        $("#userRow").show();
    }
    function editSecUser() {
        if (executeCommonPreConditionForSelectKendo(gridSecUser, 'user') == false) {
            return;
        }
        $("#userRow").show();
        var secUser = getSelectedObjectFromGridKendo(gridSecUser);
        showSecUser(secUser);
    }

    function showSecUser(secUser) {
        secUserModel.set('secUser', secUser);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }
    function editHeadInCharge() {
        if (executeCommonPreConditionForSelectKendo(gridSecUser, 'user') == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGridKendo(gridSecUser);
        var loc = "${createLink(controller: 'userDepartment', action: 'show')}?userId=" + id;
        router.navigate(formatLink(loc));
        return false;
    }
</script>
