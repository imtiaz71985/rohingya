<div class="container-fluid">
    <div class="row" id="rowDecisionMaking">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">
                    Post My Issues
                </div>
            </div>

            <g:form name='decisionMakingForm' id='decisionMakingForm' class="form-horizontal form-widgets" role="form">
                <div class="panel-body">
                    <input type="hidden" name="id" id="id" data-bind="value: decisionMaking.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: decisionMaking.version"/>

                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="col-md-2 control-label label-required"
                                   for="recipientId">Recipient:</label>

                            <div class="col-md-10">
                                <app:dropDownEmployee
                                        data_model_name="dropDownEmployee"
                                        placeholder="Recipient ID"
                                        required="true" class="kendo-drop-down"
                                        tabindex="1" id="recipientId" name="recipientId"
                                        data-bind="value: decisionMaking.recipientId">
                                </app:dropDownEmployee>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-2 control-label label-required"
                                   for="issueName">Issue:</label>

                            <div class="col-md-10">
                                <input type="text" class="form-control"
                                       id="issueName" name="issueName" tabindex="5"
                                       data-bind="value: decisionMaking.issueName"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-md-2 control-label label-required"
                                   for="description">Description:</label>

                            <div class="col-md-10">
                                <textarea id="description" name="description" style="height:115px;"
                                          class="form-control" tabindex="3"
                                          data-bind="value: decisionMaking.description"
                                          placeholder="Short Description"></textarea>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="col-md-3 control-label label-required" for="monthFor">Date:</label>

                            <div class="col-md-9">
                                <app:dateControl name="monthFor"
                                   tabindex="2" class="kendo-date-picker"
                                   data-bind="value: decisionMaking.monthFor"
                                   placeholder="Decision Deadline">
                                </app:dateControl>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-3 control-label label-optional"
                                   for="remarks">Remarks:</label>

                            <div class="col-md-9">
                                <textarea id="remarks" name="remarks" style="height:150px;"
                                          class="form-control" tabindex="5"
                                          data-bind="value: decisionMaking.remarks"
                                          placeholder="Remarks"></textarea>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer">
                    <button id="create" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="6"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Save
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="7"
                            aria-disabled="false" onclick='resetForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </g:form>
        </div>
    </div>

    <div class="row">
        <ul class="nav nav-tabs">
            <li><a data-toggle="tab" href="#menu1" onclick="reloadGrid('#gridIssues');">Current Issues</a></li>
            <li><a data-toggle="tab" href="#menu2" onclick="reloadGrid('#gridResolvedIssues');">Resolved Issues</a></li>
        </ul>

        <div class="tab-content">
            <div id="menu1" class="tab-pane fade in active">
                <div class="panel-primary">
                    <div class="panel-body">
                        <div class="row">
                            <div id="gridIssues"></div>
                        </div>
                    </div>
                </div>
            </div>

            <div id="menu2" class="tab-pane fade in active">
                <div class="panel-primary">
                    <div id="gridResolvedIssues"></div>
                </div>
            </div>
        </div>
    </div>
</div>