<%@ page pageEncoding="UTF-8" contentType="text/xml;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>

<c:catch var="exception">
<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	${param.sql}
	<c:if test="${param.actions eq 'user'}">
		<sql:param value="${sessionScope['username']}"/>
	</c:if>
</sql:query>
</c:catch>

<c:choose>
<c:when test="${!empty exception}">
	<blockquote>
	<div class="error">
		${exception}
	</div>
	</blockquote>
</c:when>
<c:otherwise>

	<div  style="color:#36393D;background:#FFFFFF;text-align:left" >


	<c:choose>
	<c:when test="${(sessionScope.maxpages eq 0)}">
		No records.
	</c:when>
	<c:otherwise>
		<c:if test="${param.paging eq 'true'}">

						Pages:

						<c:forEach var='item' begin='0' end='${sessionScope.maxpages-1}'>
								<c:if test="${param.page eq item}">
									[
								</c:if>
									<a href="
									<c:url value="${thispage}">
										<c:param name="page" value="${item}"/>
										<c:param name="pagesize" value="${param.pagesize}"/>
									</c:url>
									">${item+1}</a>
								<c:if test="${param.page eq item}">
									]
								</c:if>
								&nbsp;
						</c:forEach>

		</c:if>
		<c:if test="${param.paging eq 'true'}">
		&nbsp;Sorted by: <i>${param[sessionScope.order]}</i>
		</c:if>
		</div>

		<table width="100%" border="0" bgcolor="${tablecolor}">
		<tr bgcolor="#DDDDDD">
			<th>#</th>
		<c:forEach var="columnName" items="${rs.columnNames}">
				<c:set var="title">${param[columnName]}</c:set>
				<c:choose>
				<c:when test="${empty title}">
			  </c:when>
				<c:when test="${title eq 'ID'}">
			  </c:when>
				<c:when test="${title eq 'xml'}">
					<th>QMRF Title</th>
			  </c:when>
			  <c:otherwise>
					<th align="left">
						<a href="
						<c:url value="${thispage}">
						  <c:param name="order" value="${columnName}"/>
							<c:param name="changedirection" value="true"/>
						</c:url>
						">${title}</a>
			    </th>
		    </c:otherwise>
		  </c:choose>
		</c:forEach>
			<th align="left"> View</th>
			<th align="left"> Download</th>

				<c:choose>
				<c:when test="${empty param.actions}">
			  </c:when>
				<c:when test="${param.actions eq 'admin'}">
					<th align="left"> Actions</th>
			  </c:when>
				<c:when test="${param.actions eq 'user'}">
					<th align="left"> Actions</th>
			  </c:when>
			  <c:otherwise>
			</c:otherwise>
			</c:choose>

		</tr>

		<c:set var="counter" value="${param.page*param.pagesize}"/>
		<c:forEach var="row" items="${rs.rows}">
			<c:set var="counter" value="${counter+1}"/>
					<c:set var="clr" value="${tablecolor}"/>
							<c:if test="${(counter % 2)==0}" >
								<c:set var="clr" value="${rowcolor}"/>
							</c:if>
			<tr bgcolor="${clr}">
						<td align="left">
		    				${counter}
				    </td>

			<c:forEach var="columnName" items="${rs.columnNames}">
					<c:set var="title">${param[columnName]}</c:set>
					<c:choose>
					<c:when test="${empty title}">
				  </c:when>
					<c:when test="${title eq 'ID'}">
				  </c:when>
					<c:when test="${title eq 'xml'}">
						<td>
							<x:parse xml="${row.xml}" var="doc" systemId="/WEB-INF/xslt/qmrf.dtd"/>
							<x:out escapeXml="false" select="$doc//QMRF/QMRF_chapters/QSAR_identifier/QSAR_title"/>
						</td>
				  </c:when>
				  <c:otherwise>
						<td align="left">
							<c:choose>
							<c:when test="${columnName eq 'qmrf_number'}">
							<a href="
								<c:url value="${param.viewpage}">
							  <c:param name="id" value="${row.idqmrf}"/>
							  <c:param name="idstructure" value="${param.idstructure}"/>
								</c:url>
							" >	${row[columnName]}
								</a>

							</c:when>
							<c:when test="${columnName eq 'status'}">
											<c:choose>
											<c:when test="${row[columnName] eq 'draft'}">
														<font color="#4444FF">
														<b>
														${row[columnName]}
													</b>
													  </font>
										  </c:when>
											<c:when test="${row[columnName] eq 'returned for revision'}">
														<font color="#FF4444">
														<b>
														${row[columnName]}
													</b>
													  </font>
										  </c:when>
											<c:when test="${row[columnName] eq 'published'}">
														<font color="#44FF44">
														<b>
														${row[columnName]}
													</b>
													  </font>
										  </c:when>
											<c:otherwise>
												${row[columnName]}
										  </c:otherwise>
										  </c:choose>
		    			</c:when>
		    			<c:otherwise>
		    				${row[columnName]}
		    			</c:otherwise>
		    			</c:choose>
				    </td>
			    </c:otherwise>
			  </c:choose>
			</c:forEach>

		    <td>
							<a href="
								<c:url value="${param.viewpage}">
							  <c:param name="id" value="${row.idqmrf}"/>
							  <c:param name="idstructure" value="${param.idstructure}"/>
								</c:url>
							" ><img src="images/view.png" height="18" width="18" alt="View" border="0"/></a>

							<a href="
								<c:url value="${param.viewpage}">
							  <c:param name="id" value="${row.idqmrf}"/>
							  <c:param name="idstructure" value="${param.idstructure}"/>
								<c:param name="viewmode" value="attachments"/>
								</c:url>
							" ><img src="images/attachment.png" alt="Attachments" border="0"/></a>


							<c:if test="${row['version']>1}">
								<a href="
								<c:url value="${param.viewpage}">
							  <c:param name="id" value="${row.idqmrf}"/>
								<c:param name="history" value="true"/>
								</c:url>
							" ><img src="images/revision.png"  alt="Previous versions" border="0"/></a>
		  				</c:if>
				</td>
				<td>

							<a href="
								<c:url value="download.jsp">
							  <c:param name="id" value="${row.idqmrf}"/>
							  <c:param name="filetype" value="html"/>
								</c:url>
							"><img src="images/html.png"  alt="HTML file" border="0"/></a>

							<a href="
								<c:url value="download.jsp">
							  <c:param name="id" value="${row.idqmrf}"/>
							  <c:param name="filetype" value="pdf"/>
								</c:url>
							"><img src="images/pdf.png" border="0" alt="Adobe PDF"/></a>


							<a href="
								<c:url value="download.jsp">
							  <c:param name="id" value="${row.idqmrf}"/>
							  <c:param name="filetype" value="xls"/>
								</c:url>
							" ><img src="images/xls.png" " alt="MS Excel XLS file" border="0"/></a>

							<a href="
								<c:url value="download.jsp">
							  <c:param name="filetype" value="xml"/>
							  <c:param name="id" value="${row.idqmrf}"/>
							  <c:param name="action" value="dbattachments"/>
								</c:url>
							"><img src="images/xml.png"  alt="QMRF XML" border="0"/></a>

				 </td>
				   <td>
						<c:choose>
						<c:when test="${empty param.actions}">
					  </c:when>
						<c:when test="${param.actions eq 'admin'}">
							<!-- Admin actions -->
									<c:choose>
									  <c:when test="${row.status eq 'published'}">

									  </c:when>
									  <c:when test="${row.status eq 'archived'}">
											<i>newer revisions exist - TODO link</i>
									  </c:when>
									  <c:when test="${row.status eq 'submitted'}">
											<a href="
											<c:url value="admin_review.jsp">
											  <c:param name="status" value="under review"/>
											  <c:param name="id" value="${row.idqmrf}"/>
											</c:url>
											">Review</a>
									  </c:when>
									  <c:when test="${row.status eq 'under review'}">
											<a href="
											<c:url value="admin_review.jsp">
											  <c:param name="status" value="under review"/>
											  <c:param name="id" value="${row.idqmrf}"/>
											</c:url>
											">Review</a>

											<a href="
											<c:url value="admin_edit.jsp">
											  <c:param name="status" value="under review"/>
											  <c:param name="id" value="${row.idqmrf}"/>
											</c:url>
											">Edit</a>

											<a href="
											<c:url value="publish.jsp">
											  <c:param name="status" value="published"/>
											  <c:param name="id" value="${row.idqmrf}"/>
											</c:url>
											">Publish</a>

											<a href="
											<c:url value="admin_return.jsp">
											  <c:param name="status" value="returned for revision"/>
											  <c:param name="id" value="${row.idqmrf}"/>
											</c:url>
											">Return to author</a>

									  </c:when>
									  <c:when test="${row.status eq 'returned for revision'}">
									    <i>User action required</i>
									  </c:when>
									  <c:otherwise>
									  </c:otherwise>
									</c:choose>
							<!-- Admin actions end-->
					  </c:when>
						<c:when test="${param.actions eq 'user'}">
							<!-- User actions -->
								<c:choose>
								<c:when test="${row.status eq 'submitted'}">
									<i>Waiting for administrator approval</i>
								</c:when>
							  <c:when test="${row.status eq 'draft'}">
											<a href="
											<c:url value="edit.jsp">
											  <c:param name="id" value="${row.idqmrf}"/>
											</c:url>
											">Edit</a>
											<a href="
											<c:url value="edit_attachmentsdb.jsp">
											  <c:param name="id" value="${row.idqmrf}"/>
											</c:url>
											">Attachments</a>
											<a href="
											<c:url value="update_status.jsp">
											  <c:param name="id" value="${row.idqmrf}"/>
											</c:url>
											">Validate and submit</a>
							  </c:when>
							  <c:when test="${row.status eq 'returned for revision'}">
											<a href="
											<c:url value="edit.jsp">
											  <c:param name="id" value="${row.idqmrf}"/>
											</c:url>
											">Edit</a>
											<a href="
											<c:url value="edit_attachmentsdb.jsp">
											  <c:param name="id" value="${row.idqmrf}"/>
											</c:url>
											">Attachments</a>
							  </c:when>
							  <c:otherwise>
										<a href="
											<c:url value="edit_attachmentsdb.jsp">
											  <c:param name="id" value="${row.idqmrf}"/>
											</c:url>
											">Attachments</a>
							  </c:otherwise>
							</c:choose>
							<!-- User actions end-->
					  </c:when>
					  <c:otherwise>
						</c:otherwise>
						</c:choose>

				</td>

		  </tr>
		</c:forEach>

		</table>

		<div>
		<c:if test="${param.paging eq 'true'}">
		Current page ${param.page+1}  Results per page: ${param.pagesize}
		</c:if>
		</div>
	</c:otherwise>
	</c:choose>
</c:otherwise>
</c:choose>



