<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
</head>
<body>
<div class="nav" role="navigation">
    <u1>
        <li>
            <g:link class="list" action="index">
                <g:message code="project.list.label.button"/>
            </g:link>
        </li>
        <li><g:link class="list" uri="/user/logout">
            <g:message code="logout.label"/>
        </g:link></li>
    </u1>
</div>
<div id="create-project" class="content scaffold-create" role="main">
    <g:form resource="${this.project}" method="POST">
        <fieldset class="form">
            <div class="fieldcontain ${hasErrors(bean: project, field: 'projectName','error')}">
                <label>
                    <g:message code="project.name.label.field"/>
                </label>
                <g:textField name="projectName" value="${project.projectName}"/>
            </div>
        </fieldset>
        <fieldset class="buttons">
            <g:submitButton name="create" class="save" value="${message(code: 'project.index.create.button')}" />
        </fieldset>
    </g:form>
</div>
</body>
</html>