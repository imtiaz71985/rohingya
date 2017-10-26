package pms

import actions.decisionMaking.CreateDecisionMakingActionService
import actions.decisionMaking.DeleteDecisionMakingActionService
import actions.decisionMaking.ListDecisionMakingActionService
import actions.decisionMaking.UpdateDecisionMakingActionService

class DecisionMakingController extends BaseController {


    static allowedMethods = [
            show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST"
    ]

    CreateDecisionMakingActionService createDecisionMakingActionService
    UpdateDecisionMakingActionService updateDecisionMakingActionService
    DeleteDecisionMakingActionService deleteDecisionMakingActionService
    ListDecisionMakingActionService listDecisionMakingActionService

    def show() {
        if(params.type=='myIssues'){
            render(view: "/decisionMaking/show")
            return
        }else{
            render(view: "/decisionMaking/givenDecision/show")
        }
    }

    def create() {
        renderOutput(createDecisionMakingActionService, params)
    }

    def update() {
        renderOutput(updateDecisionMakingActionService, params)
    }

    def delete() {
        renderOutput(deleteDecisionMakingActionService, params)
    }

    def list() {
        renderOutput(listDecisionMakingActionService, params)
    }

}
