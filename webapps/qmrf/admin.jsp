<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="thispage" value='admin.jsp'/>

<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isadmin']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${sessionScope['isadmin']=='false'}" >
  <c:redirect url="/user.jsp"/>
</c:if>

<jsp:include page="query_settings.jsp" flush="true"/>

<c:if test="${!empty param.status && (sessionScope.record_status ne param.status)}">
	<c:set var="page" value="0" scope="session"  />
</c:if>

<c:set var="startpage" value="${sessionScope.page}"/>
<c:set var="startrecord" value="${sessionScope.page * sessionScope.pagesize}"/>

<html>
	<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
	<meta http-equiv="Content-Type" contentType="text/xml;charset=UTF-8">
  <head>
    <title>QMRF documents</title>
  </head>
  <body>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="login"/>
</jsp:include>

<jsp:include page="menuall.jsp" flush="true">
		    <jsp:param name="highlighted" value="admin" />
</jsp:include>

<c:catch var="exception">
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		select count(*)as no,status from documents where status = 'under review' || status = 'submitted'  group by status;
	</sql:query>
	<c:if test="${rs.rowCount > 0}">
		<div class="center">
		<font color="red">Pending documents:
		<c:forEach var="row" items="${rs.rows}">
			<b>${row.status}</b>&nbsp;(${row.no})&nbsp;
		</c:forEach>
		</font>
		</div>
	</c:if>
</c:catch>

<jsp:include page="records_status.jsp" flush="true">
    <jsp:param name="status" value="${param.status}"/>
	<jsp:param name="status_allowed" value="all,submitted,under review,returned for revision,published,archived"/>
</jsp:include>


<!-- count max pages -->
<c:if test="${startrecord eq 0}">
	<c:choose>
		<c:when test="${(empty sessionScope.record_status) || (sessionScope.record_status eq 'all')}">
			<c:set var="sql" value="select count(idqmrf) as c from documents where status != 'published' && status != 'draft' && status != 'archived'"/>
		</c:when>
		<c:otherwise>
			<c:set var="sql" value="select count(idqmrf) as c from documents where  status = '${sessionScope.record_status}'"/>
		</c:otherwise>
	</c:choose>
	<c:catch var="error">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			${sql}
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
			<c:set var="maxpages" scope="session">
			<fmt:formatNumber type="number" value="${(row.c / sessionScope.pagesize) +0.5}" pattern="###"/>
			</c:set>
			<c:if test="${sessionScope.page > maxpages}">
					<c:set var="page" value="0" scope="session"  />
					<c:set var="startrecord" value="0" />
			</c:if>
		</c:forEach>
	</c:catch>
	<c:if test="${!empty error}">
		${error}
	</c:if>
</c:if>

<c:set var="sql" value="select idqmrf,qmrf_number,version,user_name,updated,status,reviewer from documents where  status = '${sessionScope.record_status}' order by ${sessionScope.order} ${sessionScope.order_direction} limit ${startrecord},${sessionScope.pagesize}"/>
<c:if test="${(empty sessionScope.record_status) || (sessionScope.record_status eq 'all')}">
	<c:set var="sql" value="select idqmrf,qmrf_number,version,user_name,updated,status,reviewer from documents where status != 'published' && status != 'draft' && status != 'archived' order by ${sessionScope.order} ${sessionScope.order_direction} limit ${startrecord},${pagesize}"/>
</c:if>

<!--
select idqmrf,qmrf_number,version,user_name,updated,status from documents where status != 'published' && status != 'draft' && status != 'archived' order by ${sessionScope.order} ${sessionScope.order_direction}
-->
<jsp:include page="records.jsp" flush="true">
    <jsp:param name="sql" value="${sql}"/>

		<jsp:param name="qmrf_number" value="QMRF#"/>
		<jsp:param name="version" value="Version"/>
		<jsp:param name="user_name" value="Author"/>
		<jsp:param name="updated" value="Last updated"/>
		<jsp:param name="status" value="Status"/>
		<jsp:param name="reviewer" value="Reviewer"/>

		<jsp:param name="actions" value="admin"/>

		<jsp:param name="paging" value="true"/>
		<jsp:param name="viewpage" value="admin.jsp"/>
		<jsp:param name="page" value="${startpage}"/>
		<jsp:param name="pagesize" value="${pagesize}"/>
		<jsp:param name="viewmode" value="html"/>
</jsp:include>

<hr>
<jsp:include page="view.jsp" flush="true">
    <jsp:param name="highlighted" value="${param.id}"/>
</jsp:include>

<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
		</jsp:include>
	</p>
</div>

  </body>
</html>

