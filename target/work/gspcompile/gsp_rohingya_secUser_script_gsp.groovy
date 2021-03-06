import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_rohingya_secUser_script_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/secUser/_script.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
createClosureForHtmlPart(1, 1)
invokeTag('access','sec',5,['url':("/secUser/create")],1)
printHtmlPart(2)
createClosureForHtmlPart(3, 1)
invokeTag('access','sec',8,['url':("/secUser/update")],1)
printHtmlPart(2)
createClosureForHtmlPart(4, 1)
invokeTag('access','sec',11,['url':("/userDepartment/show")],1)
printHtmlPart(5)
expressionOut.print(createLink(controller:'secUser', action: 'create'))
printHtmlPart(6)
expressionOut.print(createLink(controller:'secUser', action: 'update'))
printHtmlPart(7)
expressionOut.print(createLink(controller: 'secUser', action: 'list'))
printHtmlPart(8)
expressionOut.print(createLink(controller: 'userDepartment', action: 'show'))
printHtmlPart(9)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1507603362543L
public static final String EXPRESSION_CODEC = 'none'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
