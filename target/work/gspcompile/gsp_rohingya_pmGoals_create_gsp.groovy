import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_rohingya_pmGoals_create_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/pmGoals/_create.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
createTagBody(1, {->
printHtmlPart(1)
createClosureForHtmlPart(2, 2)
invokeTag('dropDownService','app',26,['class':("kendo-drop-down"),'readonly':("true"),'required':("true"),'validationMessage':("Required"),'id':("serviceId"),'name':("serviceId"),'tabindex':("1"),'data-bind':("value: goal.serviceId"),'data_model_name':("dropDownService")],2)
printHtmlPart(3)
})
invokeTag('form','g',64,['name':("goalForm"),'id':("goalForm"),'class':("form-horizontal form-widgets"),'role':("form")],1)
printHtmlPart(4)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1507603362524L
public static final String EXPRESSION_CODEC = 'none'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
