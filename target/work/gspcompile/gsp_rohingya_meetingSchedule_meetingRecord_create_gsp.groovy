import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_rohingya_meetingSchedule_meetingRecord_create_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/meetingSchedule/meetingRecord/_create.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
createTagBody(1, {->
printHtmlPart(1)
createClosureForHtmlPart(2, 2)
invokeTag('dropDownMeetingSchedule','app',31,['class':("kendo-drop-down"),'id':("scheduleId"),'name':("scheduleId"),'tabindex':("1"),'onchange':("dropDownMeetingScheduleChange()"),'data-bind':("value: meetingLog.scheduleId"),'data_model_name':("dropDownScheduleTime")],2)
printHtmlPart(3)
})
invokeTag('form','g',133,['name':("meetingLogForm"),'id':("meetingLogForm"),'class':("form-horizontal form-widgets"),'role':("form")],1)
printHtmlPart(4)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1508836106300L
public static final String EXPRESSION_CODEC = 'none'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
