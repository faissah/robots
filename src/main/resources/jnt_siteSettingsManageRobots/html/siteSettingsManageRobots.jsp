<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>

<c:set var="site" value="${renderContext.mainResource.node.resolveSite}"/>
<c:url var="siteURL" value='${url.base}${site.path}'/>
<fmt:message key="robots.label.saved" var="i18nSaved"/><c:set var="i18nSaved" value="${functions:escapeJavaScript(i18nSaved)}"/>


<template:addResources type="javascript" resources="jquery.min.js"/>
<template:addResources>
    <script type="text/javascript">
        $(document).ready(function () {
            $('#updateSiteRobotsForm').submit(function () {
                var updateSiteRobotsSubmit = $('#updateSiteRobotsSubmit');
                updateSiteRobotsSubmit.attr('disabled', 'disabled');
                $(this).ajax({
                    url: "${siteURL}",
                    data: {
                        mixins: {
                            jmix_robots: {
                                robots: $('#robotsString').val()
                            }
                        }
                    },
                    type: "PUT",
                    success: function (response) {
                        if (response.warn != undefined) {
                            alert(response.warn);
                        } else {
                            alert('${i18nSaved}');
                        }
                        updateSiteRobotsSubmit.removeAttr('disabled');
                    },
                    error: function () {
                        updateSiteRobotsSubmit.removeAttr('disabled');
                    }
                });
                // always return false to prevent standard browser submit and page navigation
                return false;
            });
        });
    </script>
</template:addResources>

<h2>${fn:escapeXml(currentNode.displayableName)} - ${fn:escapeXml(site.displayableName)}</h2>

<p><fmt:message key="robots.label.description"/>:</p>


<form class="form-horizontal" id="updateSiteRobotsForm" action="#">
    <div class="control-group">
    <label class="control-label" for="robotsString"><fmt:message key='robots.label.robotsString'/></label>

        <div class="controls">
            <textarea id="robotsString" name="robots" rows="7"><c:if test="${jcr:isNodeType(site, 'jmix:robots')}">${site.properties.robots.string}</c:if></textarea>
        </div>
    </div>
    <div class="control-group">
        <div class="form-actions">
            <button type="submit" class="btn btn-primary" name="save" id="updateSiteRobotsSubmit"><i class="icon-ok icon-white"></i><fmt:message key="robots.label.save"/></button>
        </div>
    </div>
</form>