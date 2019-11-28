<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<header>
    <c:set var="context" value="${pageContext.request.contextPath}"/>
    <a href="${context}/meals"><spring:message code="app.title"/></a> | <a href="${context}/users"><spring:message
        code="user.title"/></a> | <a href="${context}"><spring:message code="app.home"/></a>
</header>