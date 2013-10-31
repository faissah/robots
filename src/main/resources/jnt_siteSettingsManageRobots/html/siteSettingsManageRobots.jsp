<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<h2>Manage robots.txt</h2>
<table class="table table-bordered table-striped table-hover" >
    <thead>
        <tr>
            <th width="4%">#</th>
            <th><fmt:message key="siteSettings.label.modules.name"/></th>
            <th><fmt:message key="siteSettings.label.modules.id"/></th>
            <th><fmt:message key="siteSettings.label.modules.version"/></th>
            <th><fmt:message key="siteSettings.label.modules.type"/></th>
            <th><fmt:message key="siteSettings.label.modules.source"/></th>
        </tr>
    </thead>
    <tbody>
    <tr class="info" >
            <td colspan="6"><h3><fmt:message key="label.modules"/></h3></td>
        </tr>
        <tr class="warning" >
            <td colspan="6"><h3><fmt:message key="siteSettings.requiredModules"/></h3></td>
        </tr>
    </tbody>
</table>
