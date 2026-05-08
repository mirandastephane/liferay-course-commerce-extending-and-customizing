<%--
    SYNTAX REFERENCE: Copy and adapt for the TODO sections below.

    Expando read example:
    String fieldVariable = GetterUtil.getString(
        expandoValueLocalService.getData(
            themeDisplay.getCompanyId(),
            CPDefinition.class.getName(),
            ExpandoTableConstants.DEFAULT_TABLE_NAME,
            "servicePlan_fieldName",
            cpDefinition.getCPDefinitionId(),
            (String)null));

    Field display example:
    <aui:input
        disabled="<%= true %>"
        label="field-key"
        name="fieldVariable"
        type="text"
        value="<%= fieldVariable %>"
    />
--%>

<%@ include file="/init.jsp" %>

<%
CPDefinition cpDefinition = (CPDefinition)renderRequest.getAttribute(
    ServicePlanWebKeys.CP_DEFINITION);

ExpandoValueLocalService expandoValueLocalService =
    (ExpandoValueLocalService)renderRequest.getAttribute(
        "expandoValueLocalService");

String _defaultValue = null;

String coverageDuration = GetterUtil.getString(
    expandoValueLocalService.getData(
        themeDisplay.getCompanyId(),
        CPDefinition.class.getName(),
        ExpandoTableConstants.DEFAULT_TABLE_NAME,
        "servicePlan_coverageDuration",
        cpDefinition.getCPDefinitionId(),
        _defaultValue));

String maxNumberOfClaims = GetterUtil.getString(
    expandoValueLocalService.getData(
        themeDisplay.getCompanyId(),
        CPDefinition.class.getName(),
        ExpandoTableConstants.DEFAULT_TABLE_NAME,
        "servicePlan_maxNumberOfClaims",
        cpDefinition.getCPDefinitionId(),
        _defaultValue));

String replacementMethod = GetterUtil.getString(
    expandoValueLocalService.getData(
        themeDisplay.getCompanyId(),
        CPDefinition.class.getName(),
        ExpandoTableConstants.DEFAULT_TABLE_NAME,
        "servicePlan_replacementMethod",
        cpDefinition.getCPDefinitionId(),
        _defaultValue));

String underwriter = GetterUtil.getString(
    expandoValueLocalService.getData(
        themeDisplay.getCompanyId(),
        CPDefinition.class.getName(),
        ExpandoTableConstants.DEFAULT_TABLE_NAME,
        "servicePlan_underwriter",
        cpDefinition.getCPDefinitionId(),
        _defaultValue));

String termsUrl = GetterUtil.getString(
    expandoValueLocalService.getData(
        themeDisplay.getCompanyId(),
        CPDefinition.class.getName(),
        ExpandoTableConstants.DEFAULT_TABLE_NAME,
        "servicePlan_termsUrl",
        cpDefinition.getCPDefinitionId(),
        _defaultValue));
%>

<portlet:actionURL name="/cp_definitions/edit_cp_definition_service_plan" var="editServicePlanURL">
    <portlet:param name="cpDefinitionId" value="<%= String.valueOf(cpDefinition.getCPDefinitionId()) %>" />
</portlet:actionURL>

<aui:form action="<%= editServicePlanURL %>" method="post" name="fm">
    <aui:input name="cpDefinitionId" type="hidden" value="<%= cpDefinition.getCPDefinitionId() %>" />

    <aui:fieldset>
        <aui:input
            disabled="<%= true %>"
            label="coverage-duration"
            name="coverageDuration"
            type="text"
            value="<%= coverageDuration %>"
        />

        <%-- TODO: Add Grace Period field here --%>

        <aui:input
            disabled="<%= true %>"
            label="max-number-of-claims"
            name="maxNumberOfClaims"
            type="text"
            value="<%= maxNumberOfClaims %>"
        />

        <%-- TODO: Add Max Claim Value field here --%>

        <aui:input
            disabled="<%= true %>"
            label="replacement-method"
            name="replacementMethod"
            type="text"
            value="<%= replacementMethod %>"
        />

        <aui:input
            disabled="<%= true %>"
            label="underwriter"
            name="underwriter"
            type="text"
            value="<%= underwriter %>"
        />

        <aui:input
            disabled="<%= true %>"
            label="terms-url"
            name="termsUrl"
            type="text"
            value="<%= termsUrl %>"
        />
    </aui:fieldset>

    <aui:button-row>
        <aui:button type="submit" />
    </aui:button-row>
</aui:form>
