package testingforest

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class UserController {

    UserService userService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def log_in() {}

    def authenticate() {
        def hexPassword = params.password.encodeAsSHA1()
        def user = User.findByLoginAndPassword(params.login, hexPassword)
        if(user){
            session.user = user

            log.info("User ${session.user.login} logged in")

            flash.message = message(code:"login.message", args: [session.user.name])
            redirect uri: "/project/index"
        } else {
            flash.error = message(code:"login.error")
            log.error("User's authentication failed")
            redirect uri: "/user/log_in"
        }
    }

    def logout() {
        flash.message = message(code:"logout.message", args: [session.user.name])

        log.info("User ${session.user.login} logged out")

        session.user = null
        redirect uri: "/user/log_in"
    }

    def showInfo() {
        def user = User.get(session.user.id)
        def testCases = user.caseList
        def criteria = Project.createCriteria()
        def projects = criteria.list{
            teamList{
                idEq(user.id)
            }
        }
        return [projects:projects,testCases:testCases]
    }

    def deleteCurrentUser() {
        def user = session.user
        def criteria = Project.createCriteria()
        def projects = criteria.list{
            teamList{
                idEq(user.id)
            }
        }
        projects.each{
            if ( it.teamList.size() == 1 ){
                it.delete(flush:true)
            } else {
                it.teamList.removeElement(user)
            }
        }
        log.info("User ${session.user.login} was removed")
        User.get(user.id).caseList.each{
            it.delete(flush:true)
        }
        User.get(user.id).delete(flush:true)
        session.invalidate()
        redirect uri: "/user/log_in"
    }

    def show(Long id) {
        respond userService.get(id)
    }

    def create() {
        respond new User(params)
    }

    def save(User user) {
        user.role = "user"
        if (user.validate() && params.password == params.passwordRepeat) {
            user.save()
            log.info("User ${user.login} registered")
            flash.message = message(code: 'registration.success.message', args: [user.name])
            redirect uri: "/user/log_in"
        } else if (user.validate()) {
            render view: 'create'
            flash.error = message(code: 'user.password.error')
            log.error("Passwords mismatch for ${params.login} user login")
        } else {
            respond user.errors, view: 'create'
        }
    }

    def edit(Long id) {
        respond userService.get(id)
    }

    def update(User user) {

        if (user == null) {
            notFound()
            return
        }

        try {
            userService.save(user)
        } catch (ValidationException e) {
            log.error(user.errors)
            respond user.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), user.id])
                redirect uri: "/user/show/${user.id}"
            }
            '*'{ respond user, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        userService.delete(id)
        log.debug("User ${userService.get(id).login} account deleted")

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'user.label', default: 'User'), id])
                redirect uri: "/user/index"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
                redirect uri: "/user/index"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
