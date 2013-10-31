<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<h2><fmt:message key="robots.label.manageRobots"/></h2>

<c:set var="siteNode" value="${renderContext.mainResource.node.resolveSite}"/>
<form class="form-horizontal">
    <div class="control-group">
        <label class="control-label" for="robotsString"><fmt:message key='robots.label.robotsString'/></label>

        <div class="controls">
            <textarea id="robotsString" rows="7">
                <c:if test="${jcr:isNodeType(siteNode, 'jmix:robots')}">
                    ${siteNode.properties.robots.string}
                </c:if>
            </textarea>
        </div>
    </div>
    <div class="control-group">
        <div class="form-actions">
            <button type="submit" class="btn btn-primary"><fmt:message key="robots.label.submit"/></button>
        </div>
    </div>
</form>