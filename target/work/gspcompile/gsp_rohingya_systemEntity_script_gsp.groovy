import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_rohingya_systemEntity_script_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/systemEntity/_script.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
createClosureForHtmlPart(1, 1)
invokeTag('access','sec',5,['url':("/systemEntity/update")],1)
printHtmlPart(2)
createClosureForHtmlPart(3, 1)
invokeTag('access','sec',8,['url':("/systemEntity/delete")],1)
printHtmlPart(4)
expressionOut.print(createLink(controller:'systemEntity', action: 'create'))
printHtmlPart(5)
expressionOut.print(createLink(controller:'systemEntity', action: 'update'))
printHtmlPart(6)
expressionOut.print(createLink(controller: 'systemEntity', action: 'list'))
printHtmlPart(7)
expressionOut.print(createLink(controller: 'systemEntity', action:  'delete'))
printHtmlPart(8)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1507603362545L
public static final String EXPRESSION_CODEC = 'none'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
