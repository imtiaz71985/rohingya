import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_rohingya_reports_yearly_details_scripts_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/reports/yearly/details/_scripts.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
expressionOut.print(isSysAdmin)
printHtmlPart(1)
expressionOut.print(isTopMan)
printHtmlPart(1)
expressionOut.print(isSpAdmin)
printHtmlPart(1)
expressionOut.print(isMultiDept)
printHtmlPart(2)
expressionOut.print(isSysAdmin)
printHtmlPart(1)
expressionOut.print(isTopMan)
printHtmlPart(1)
expressionOut.print(isSpAdmin)
printHtmlPart(1)
expressionOut.print(isMultiDept)
printHtmlPart(3)
expressionOut.print(serviceId)
printHtmlPart(4)
expressionOut.print(createLink(controller: 'reports', action: 'listYearlySPDetails'))
printHtmlPart(5)
expressionOut.print(createLink(controller: 'pmActions', action: 'listIndicatorByActions'))
printHtmlPart(6)
expressionOut.print(createLink(controller: 'pmActions', action: 'listDetailsByIndicator'))
printHtmlPart(7)
expressionOut.print(createLink(controller: 'reports', action:  'downloadYearlySPDetails'))
printHtmlPart(8)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1507603362541L
public static final String EXPRESSION_CODEC = 'none'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
