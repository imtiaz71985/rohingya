<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading" style="height: 30px;">
                <div class="panel-title">
                    Annual SAP
                    <button id="downloadSP" name="downloadSP" type="button" data-role="button"
                            class="k-button k-button-icontext pull-right" role="button"
                            aria-disabled="false" onclick='downloadYearlySpReport();'><span
                            class="fa fa-file-pdf-o"></span>&nbsp;Download
                    </button>
                    <label class="control-label label-optional pull-right" style="font-size: 10px;">
                        <input type="radio" name="downloadType" value="true" checked> All column
                        <input type="radio" name="downloadType" value="false">Selected column
                    &nbsp;</label>
                </div>
            </div>

            <g:form name='detailsForm' id='detailsForm' class="form-horizontal form-widgets" role="form">
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
                                    class="kendo-drop-down" is_in_sp="true"
                                    id="serviceId" name="serviceId" tabindex="2"
                                    consider_all="true"
                                    data_model_name="dropDownService">
                            </app:dropDownService>
                        </div>
                        <div class="col-md-2">
                            <select class="kendo-drop-down" id="indicatorType" name="indicatorType" tabindex="3"></select>
                        </div>
                        <div class="col-md-2">
                            <button id="create" name="create" type="submit" data-role="button"
                                    class="k-button k-button-icontext"
                                    role="button" tabindex="4"
                                    aria-disabled="false"><span class="k-icon k-i-search"></span>View Result
                            </button>
                        </div>

                    </div>
                </div>
            </g:form>
        </div>
    </div>
    <div class="row">
        <div id="gridYearlySP"></div>
    </div>
</div>