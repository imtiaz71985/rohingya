<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">
                    SAP Backup Download
                </div>
            </div>

            <div class="panel-body">
                <div class="form-group">
                    <label class="col-md-1 control-label label-optional" for="year">Year:</label>

                    <div class="col-md-2">
                        <input type='text' tabindex="1" required="required" onkeydown="return false;"
                               class="kendo-date-picker" id="year" name="year"
                               placeholder="Year" validationMessage="Required"/>
                    </div>
                    <label class="col-md-1 control-label label-optional"
                           for="serviceId">Service:</label>

                    <div class="col-md-3">
                        <app:dropDownService
                                class="kendo-drop-down" is_in_sp="true" hints_text="ALL CSU/Sector"
                                id="serviceId" name="serviceId" tabindex="2"
                                onchange="populateBackupGrid()"
                                data_model_name="dropDownService">
                        </app:dropDownService>
                    </div>

                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="year"></span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div id="gridSAPBackup"></div>
    </div>
</div>

<g:render template='/reports/backupView/scripts'/>


