<div class="container-fluid">
    <div class="row" id="rowGoals">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading" style="height: 30px;">
                <div class="panel-title">
                    Monthly Reporting Pack (MRP)
                </div>
            </div>

            <g:form name='detailsForm' id='detailsForm' class="form-horizontal form-widgets" role="form">
                <div class="panel-body">
                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional" for="month">Month:</label>

                        <div class="col-md-2">
                            <input type='text' tabindex="1" required="required" onkeydown="return false;"
                                   class="kendo-date-picker" id="month" name="month"
                                   placeholder="Month" validationMessage="Required"/>
                        </div>
                        <label class="col-md-1 control-label label-optional"
                               for="serviceId">Service:</label>

                        <div class="col-md-3">
                            <app:dropDownService
                                    class="kendo-drop-down" is_in_sp="true"
                                    id="serviceId" name="serviceId" tabindex="2"
                                    data_model_name="dropDownService" onchange="setMinMonth();">
                            </app:dropDownService>
                        </div>

                        <div class="col-md-2">
                            <button id="create" name="create" type="submit" data-role="button"
                                    class="k-button k-button-icontext"
                                    role="button" tabindex="3"
                                    aria-disabled="false"><span class="k-icon k-i-search"></span>View Result
                            </button>
                        </div>

                    </div>
                </div>

            </g:form>
        </div>
    </div>

    <div class="row">
        <g:render template='/pmActions/mrp/tempSP'/>
    </div>

    <div class="row">
        <div id="grid"></div>
    </div>
</div>

