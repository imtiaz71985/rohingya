<style type="text/css">
.centered {
    margin: 0 auto;
    text-align: left;
    width: 600px;
}

.k-tooltip {
    width: 300px;
    height: auto;
    white-space: normal;
    color: #080808;
    background-color: #faebcc !important;
}
</style>

<sec:isDashboardForUser>
    <div class="container-fluid">
        <div class="row">
            <div class="panel panel-primary">
                <div class="panel-body">
                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional"
                               for="month">Month:</label>

                        <div class="col-md-2">
                            <input type='text' tabindex="1" required="required" onkeydown="return false;"
                                   class="kendo-date-picker" id="month" name="month"
                                   placeholder="Month" validationMessage="Required"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="form-group" id="divUser" style="padding-left: 20%">
                <div class="col-md-8" id="chartUser"></div>
            </div>
        </div>
    </div>

    <script language="javascript">
        var dropDownService, chartUser;
        $(document).ready(function () {
            var str = moment().subtract(1, 'months').format('MMMM YYYY');

            $('#month').kendoDatePicker({
                format: "MMMM yyyy",
                parseFormats: ["yyyy-MM-dd"],
                start: "year",
                depth: "year",
                change: populateKendoChart
            }).data("kendoDatePicker");
            $('#month').val(moment().format('MMMM YYYY'));

            createUserPieChart();
            populateKendoChart();
        });
        function populateKendoChart() {
            var month = $('#month').val();
            var actionUrl = "${createLink(controller: 'login', action: 'lstUserPieDashboard')}?month=" + month;
            chartUser.dataSource.transport.options.read.url = actionUrl;
            chartUser.dataSource.read();
        }
        function createUserPieChart() {
            $('#chartUser').kendoChart({
                title: {
                    text: "Critical Decision Status"
                },
                legend: {
                    visible: false
                },
                autoBind: false,
                dataSource: {
                    transport: {
                        read: {
                            url: false,
                            dataType: "json"
                        }
                    }
                },
                chartArea: {
                    background: ""
                },
                seriesDefaults: {
                    type: "pie",
                    labels: {
                        visible: true,
                        background: "transparent",
                        template: "#= category #: \n #= value#"
                    }
                },
                series: [{
                    field: "issue_count",
                    categoryField: "act_name",
                    colorField: "act_color"
                }],
                tooltip: {
                    visible: true
                }
            });
            chartUser = $("#chartUser").data("kendoChart");
        }
    </script>

</sec:isDashboardForUser>

