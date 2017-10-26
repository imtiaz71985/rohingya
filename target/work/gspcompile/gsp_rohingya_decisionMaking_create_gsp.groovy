import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_rohingya_decisionMaking_create_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/decisionMaking/_create.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
createTagBody(1, {->
printHtmlPart(1)
createClosureForHtmlPart(2, 2)
invokeTag('dropDownEmployee','app',27,['data_model_name':("dropDownEmployee"),'placeholder':("Recipient ID"),'required':("true"),'class':("kendo-drop-down"),'tabindex':("1"),'id':("recipientId"),'name':("recipientId"),'data-bind':("value: decisionMaking.recipientId")],2)
printHtmlPart(3)
createClosureForHtmlPart(2, 2)
invokeTag('dateControl','app',63,['name':("monthFor"),'tabindex':("2"),'class':("kendo-date-picker"),'data-bind':("value: decisionMaking.monthFor"),'placeholder':("Decision Deadline")],2)
printHtmlPart(4)
})
invokeTag('form','g',94,['name':("decisionMakingForm"),'id':("decisionMakingForm"),'class':("form-horizontal form-widgets"),'role':("form")],1)
printHtmlPart(5)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1508738354779L
public static final String EXPRESSION_CODEC = 'none'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
