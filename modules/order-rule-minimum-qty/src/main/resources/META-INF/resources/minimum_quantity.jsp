<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/commerce-ui" prefix="commerce-ui" %>

<%@ page import="com.clarityvisionsolutions.commerce.order.rule.web.display.context.MinimumQuantityDisplayContext" %>

<%@ page import="com.liferay.portal.kernel.util.WebKeys" %>

<%
MinimumQuantityDisplayContext minimumQuantityDisplayContext = (MinimumQuantityDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<div class="row">
	<div class="col">
		<commerce-ui:panel
			bodyClasses="flex-fill"
			title="Configuration"
		>
			<div class="row">
				<div class="col">
					<aui:input label="minimum-quantity" name="type--settings--minimum-quantity--" required="<%= true %>" type="text" value="<%= minimumQuantityDisplayContext.getMinimumQuantity() %>">
						<aui:validator name="number" />
					</aui:input>
				</div>
			</div>
		</commerce-ui:panel>
	</div>
</div>