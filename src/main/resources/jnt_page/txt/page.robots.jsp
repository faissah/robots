<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<c:set target="${renderContext}" property="contentType" value="text/plain;charset=UTF-8"/>

<c:set var="site" value="${renderContext.mainResource.node.resolveSite}"/>
<c:if test="${jcr:isNodeType(site, 'jmix:robots')}">
    ${site.properties.robots.string}
</c:if>