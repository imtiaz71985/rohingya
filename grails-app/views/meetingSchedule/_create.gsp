<div class="container-fluid">
    <div class="row" id="rowMeetingSchedule">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">
                    Create Meeting Schedule
                </div>
            </div>

            <g:form name='meetingScheduleForm' id='meetingScheduleForm' class="form-horizontal form-widgets"
                    role="form">
                <div class="panel-body">
                    <input type="hidden" name="id" id="id" data-bind="value: meetingSchedule.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: meetingSchedule.version"/>
                    <input type="hidden" name="projectName" id="projectName" data-bind="value: meetingSchedule.projectName"/>
                    <input type="hidden" name="teamName" id="teamName" data-bind="value: meetingSchedule.teamName"/>

                    <div class="form-group">
                        <div class="col-md-7">

                            <div class="form-group">
                                <label class="col-md-2 control-label label-required"
                                       for="projectId">Project:</label>

                                <div class="col-md-9">
                                <app:dropDownCxProject
                                        data_model_name="dropDownCxProject"
                                        required="required" tabindex="1"
                                        validationmessage="Required"
                                        class="kendo-drop-down"
                                        onchange="javascript:populateTeamList();"
                                        data-bind="value: meetingSchedule.projectId"
                                        id="projectId" name="projectId">
                                </app:dropDownCxProject>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-md-2 control-label label-required" >Team:</label>

                                <div class="col-md-9">
                                    <select id="teamId" name="teamId"
                                            required="required" validationMessage="Required"
                                            data-bind="value: meetingSchedule.teamId"
                                            class="kendo-drop-down"
                                            tabindex="2">
                                    </select>
                                </div>

                            </div>
                            <div class="form-group">
                                <label class="col-md-2 control-label label-required" for="scheduledDate">Date:</label>

                                <div class="col-md-9">
                                    <input id="scheduledDate" name="scheduledDate"
                                                     tabindex="2" class="kendo-date-picker"
                                                     data-bind="value: meetingSchedule.scheduledDate"
                                                     placeholder="scheduled Date"/>

                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-2 control-label label-required"
                                       for="attendeesEmp">Attendees Friendship:</label>

                                <div class="col-md-9">
                                    <select id="attendeesEmp" name="attendeesEmp"
                                            data-placeholder="Select ..." tabindex="3"
                                            data-bind="value: meetingSchedule.attendeesEmp">
                                    </select>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-2 control-label " for="attendeesOthers">Attendees Others:</label>

                                <div class="col-md-9">
                                    <textarea id="attendeesOthers" name="attendeesOthers"
                                              rows="3"
                                              class="form-control" tabindex="4"
                                              data-bind="value: meetingSchedule.attendeesOthers"
                                              placeholder="Attendees Others"></textarea>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-2 control-label label-required"
                                       for="issues">Subject:</label>

                                <div class="col-md-9">
                                    <input id="issues" name="issues"
                                              class="form-control" tabindex="5"
                                              data-bind="value: meetingSchedule.issues"
                                              placeholder="Subject"/>
                                </div>

                            </div>
                        </div>

                        <div class="col-md-5">

                            <div class="form-group">
                                <label class="col-md-2 control-label label-required" for="descStr">Description:</label>
                            </div>

                            <div class="form-group">
                                <div class="col-md-12">
                                    <textarea id="descStr" name="descStr" style="height:400px;"
                                              class="form-control" tabindex="6"
                                              data-bind="value: meetingSchedule.descStr"
                                              placeholder="Description"></textarea>
                                </div>
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
        <div id="gridMeetingSchedule"></div>
    </div>
</div>