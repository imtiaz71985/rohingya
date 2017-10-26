import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_rohingya_meetingSchedule_meetingRecord_script_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/meetingSchedule/meetingRecord/_script.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
createClosureForHtmlPart(1, 1)
invokeTag('access','sec',5,['url':("/meetingLog/create")],1)
printHtmlPart(2)
createClosureForHtmlPart(3, 1)
invokeTag('access','sec',8,['url':("/meetingLog/update")],1)
printHtmlPart(2)
createClosureForHtmlPart(4, 1)
invokeTag('access','sec',11,['url':("/meetingLog/delete")],1)
printHtmlPart(2)
createClosureForHtmlPart(5, 1)
invokeTag('access','sec',14,['url':("/meetingLog/uploadMeetingLog")],1)
printHtmlPart(6)
expressionOut.print(lstEmployee)
printHtmlPart(7)
expressionOut.print(createLink(controller:'meetingLog', action: 'listByScheduleMeeting'))
printHtmlPart(8)
if(true && (meetingType=='Monthly')) {
printHtmlPart(9)
}
printHtmlPart(10)
expressionOut.print(createLink(controller:'meetingLog', action: 'create'))
printHtmlPart(11)
expressionOut.print(createLink(controller:'meetingLog', action: 'update'))
printHtmlPart(12)
expressionOut.print(createLink(controller: 'meetingLog', action: 'list'))
printHtmlPart(13)
expressionOut.print(meetingTypeId)
printHtmlPart(14)
expressionOut.print(serviceId)
printHtmlPart(15)
expressionOut.print(meetingTypeId)
printHtmlPart(16)
expressionOut.print(createLink(controller: 'meetingLog', action:  'uploadMeetingLog'))
printHtmlPart(17)
expressionOut.print(createLink(controller: 'meetingLog', action:  'delete'))
printHtmlPart(18)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1508837748426L
public static final String EXPRESSION_CODEC = 'none'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
