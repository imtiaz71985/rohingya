<!-- Navigation -->
<nav id="navbar" class="navbar navbar-default navbar-static-top" role="navigation"
     style="margin-bottom: 0;height: 2px;">
    <div class="navbar-header">
        <a style="padding-right: 2px;padding-top: 0;" class="navbar-brand" href="#login/dashBoard"><img
                src="images/logo.png" style="height: inherit;"/></a>
        <a style="padding-left: 120px;padding-top: 2px;" href=""><i id="spinner" class="fa fa-2x fa-refresh fa-spin"
                                                                    style="margin: 2px 4px;color:#9F9F9F"></i></a>
    </div>
    <!-- /.navbar-header -->

    <ul class="nav navbar-top-links navbar-right">
        <!-- /.dropdown -->
        <li class="dropdown">
        <li style="text-align: center">Welcome &nbsp;<strong><font color="green"><sec:fullName
                property='fullName'></sec:fullName>&nbsp;</font></strong></li>

        <a class="dropdown-toggle" data-toggle="dropdown" href="#">
            <i class="fa fa-user" style="font-size: 20px;"></i>  <i class="fa fa-caret-down"></i>
        </a>
        <ul class="dropdown-menu dropdown-user">
            <li>
                <a href="#login/resetPassword"><i class="fa fa-gear"></i>&nbsp;Reset password</a>
            </li>
            <li><a href="<g:createLink controller="logout"/>"><span
                    class="fa fa-sign-out"></span>&nbsp;Logout</a>
            </li>
        </ul>
        <!-- /.dropdown-user -->
    </li>
        <!-- /.dropdown -->
    </ul>
<!-- /.navbar-top-links -->
    <sec:isInitialPasswordLeftMenu>
        <div class="navbar-default sidebar" role="navigation">
            <div class="sidebar-nav navbar-collapse">
                <ul class="nav" id="side-menu">
                    <sec:ifAnyUrls urls="/reports/showMeetingStatus">
                        <li>
                            <a href="#"><i class="fa fa-file-pdf-o"></i>&nbsp;Reports<span
                                    class="fa arrow"></span></a>
                            <ul class="nav nav-second-level">
                                <sec:access url="/reports/showMeetingStatus">
                                    <li>
                                        <a href="#reports/showMeetingStatus?type=Scheduled Meeting">
                                            <i class="fa fa-handshake-o"></i>&nbsp;Scheduled Meeting
                                        </a>
                                    </li>
                                </sec:access>
                            </ul>
                            <!-- /.nav-second-level -->
                        </li>
                    </sec:ifAnyUrls>
                    <sec:ifAnyUrls urls="/decisionMaking/show">
                        <li>
                            <a href="#"><i class="fa fa-thumbs-o-up"></i>&nbsp;Decision Making<span
                                    class="fa arrow"></span></a>
                            <ul class="nav nav-second-level">
                                <sec:access url="/decisionMaking/show">
                                    <li>
                                        <a href="#decisionMaking/show?type=myIssues"><i
                                                class="fa fa-first-order"></i>&nbsp;My Issues</a>
                                    </li>
                                </sec:access>
                                <sec:access url="/decisionMaking/show">
                                    <li>
                                        <a href="#decisionMaking/show?type=givenDecision"><i
                                                class="fa fa-share-square-o"></i>&nbsp;Give Decision</a>
                                    </li>
                                </sec:access>
                            </ul>
                            <!-- /.nav-second-level -->
                        </li>
                    </sec:ifAnyUrls>
                    <sec:ifAnyUrls
                            urls="/meetingLog/show,/meetingLog/showQuarterAnnual,/meetingLog/showFunctional,/meetingLog/showSchedule,/meetingLog/showScheduledMeeting">
                        <li>
                            <a href="#"><i class="fa fa-sitemap"></i>&nbsp;Meeting<span
                                    class="fa arrow"></span></a>
                            <ul class="nav nav-second-level">
                                <sec:access url="/meetingLog/showSchedule">
                                    <li>
                                        <a href="#meetingLog/showSchedule"><i class="fa fa-meetup"></i>&nbsp;Schedule
                                        </a>
                                    </li>
                                </sec:access>
                                <sec:access url="/meetingLog/showScheduledMeeting">
                                    <li>
                                        <a href="#meetingLog/showScheduledMeeting?type=Scheduled Meeting"><i
                                                class="fa fa-handshake-o"></i>&nbsp;Meeting Record
                                        </a>
                                    </li>
                                </sec:access>
                            </ul>
                            <!-- /.nav-second-level -->
                        </li>
                    </sec:ifAnyUrls>
                    <sec:ifAnyUrls urls="/meetingSchedule/show">
                        <li>
                            <a href="#"><i class="fa fa-sitemap"></i>&nbsp;Meeting Schedule<span
                                    class="fa arrow"></span></a>
                            <ul class="nav nav-second-level">
                                <sec:access url="/meetingSchedule/show">
                                    <li>
                                        <a href="#meetingSchedule/show?type=Weekly"><i
                                                class="fa fa-wikipedia-w"></i>&nbsp;Weekly
                                        </a>
                                    </li>
                                    <li>
                                        <a href="#meetingSchedule/show?type=Monthly"><i
                                                class="fa fa-maxcdn"></i>&nbsp;Monthly
                                        </a>
                                    </li>
                                </sec:access>
                            </ul>
                            <!-- /.nav-second-level -->
                        </li>
                    </sec:ifAnyUrls>
                    <sec:ifAnyUrls
                            urls="/secUser/show,/secRole/show,/featureManagement/show">
                        <li>
                            <a href="#"><i class="fa fa-users"></i>&nbsp;User Management<span class="fa arrow"></span></a>
                            <ul class="nav nav-second-level">
                                <sec:access url="/secUser/show">
                                    <li>
                                        <a href="#secUser/show"><i class="fa fa-user"></i>&nbsp;User</a>
                                    </li>
                                </sec:access>
                                <sec:access url="/secRole/show">
                                    <li>
                                        <a href="#secRole/show"><i class="fa fa-cog"></i>&nbsp;Role</a>
                                    </li>
                                </sec:access>
                                <sec:access url="/featureManagement/show">
                                    <li>
                                        <a href="#featureManagement/show"><i class="fa fa-cogs"></i>&nbsp;Role Right</a>
                                    </li>
                                </sec:access>
                            </ul>
                            <!-- /.nav-second-level -->
                        </li>
                    </sec:ifAnyUrls>
                </ul>
            </div>
            <!-- /.sidebar-collapse -->
        </div>
    </sec:isInitialPasswordLeftMenu>
<!-- /.navbar-static-side -->
</nav>