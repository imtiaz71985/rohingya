import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_rohingya_meetingLog_script_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/meetingLog/_script.gsp" }
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
if(true && (meetingType=='Monthly')) {
printHtmlPart(7)
expressionOut.print(categoryId==2)
printHtmlPart(8)
}
printHtmlPart(9)
expressionOut.print(lstEmployee)
printHtmlPart(10)
expressionOut.print(lstEmployeeDO)
printHtmlPart(11)
expressionOut.print(lstEmployee)
printHtmlPart(12)
if(true && (meetingType=='Monthly')) {
printHtmlPart(13)
}
printHtmlPart(14)
expressionOut.print(createLink(controller:'meetingLog', action: 'create'))
printHtmlPart(15)
expressionOut.print(createLink(controller:'meetingLog', action: 'update'))
printHtmlPart(16)
expressionOut.print(createLink(controller: 'meetingLog', action: 'list'))
printHtmlPart(17)
expressionOut.print(meetingTypeId)
printHtmlPart(18)
expressionOut.print(serviceId)
printHtmlPart(19)
expressionOut.print(meetingTypeId)
printHtmlPart(20)
expressionOut.print(createLink(controller: 'meetingLog', action:  'uploadMeetingLog'))
printHtmlPart(21)
expressionOut.print(createLink(controller: 'meetingLog', action:  'delete'))
printHtmlPart(22)
if(true && (meetingType=='Monthly')) {
printHtmlPart(23)
}
printHtmlPart(24)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1507603362519L
public static final String EXPRESSION_CODEC = 'none'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
