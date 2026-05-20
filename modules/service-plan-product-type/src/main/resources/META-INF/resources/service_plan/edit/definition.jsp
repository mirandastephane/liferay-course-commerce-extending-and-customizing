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

    Select field:
    <aui:select label="field-key" name="fieldVariable" value="<%= fieldVariable %>">
        <aui:option label="Option A" value="Option A" />
        <aui:option label="Option B" value="Option B" />
    </aui:select>

    Integer field:
    <aui:input
        label="field-key"
        name="fieldVariable"
        type="number"
        value="<%= fieldVariable %>"
    />

    Decimal field:
    <aui:input
        label="field-key"
        name="fieldVariable"
        step="0.01"
        type="number"
        value="<%= fieldVariable %>"
    />

    Text field:
    <aui:input
        label="field-key"
        name="fieldVariable"
        type="text"
        value="<%= fieldVariable %>"
    />
--%>

<%@ include file="/init.jsp" %>

<%
CPDefinition cpDefinition = (CPDefinition)request.getAttribute(
    ServicePlanWebKeys.CP_DEFINITION);

ExpandoValueLocalService expandoValueLocalService =
    (ExpandoValueLocalService)request.getAttribute(
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

// TODO: Add gracePeriod Expando read here

String maxNumberOfClaims = GetterUtil.getString(
    expandoValueLocalService.getData(
        themeDisplay.getCompanyId(),
        CPDefinition.class.getName(),
        ExpandoTableConstants.DEFAULT_TABLE_NAME,
        "servicePlan_maxNumberOfClaims",
        cpDefinition.getCPDefinitionId(),
        _defaultValue));

// TODO: Add maxClaimValue Expando read here

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

    <div class="sheet">
        <div class="panel-group panel-group-flush">
            <aui:fieldset label="service-plan-settings">
                <aui:select label="coverage-duration" name="coverageDuration" value="<%= coverageDuration %>">
                    <aui:option label="12 months" value="12 months" />
                    <aui:option label="24 months" value="24 months" />
                    <aui:option label="36 months" value="36 months" />
                    <aui:option label="Custom" value="Custom" />
                </aui:select>

                <%-- TODO: Add Grace Period field here --%>

                <aui:input
                    label="max-number-of-claims"
                    name="maxNumberOfClaims"
                    type="text"
                    value="<%= maxNumberOfClaims %>"
                />

                <%-- TODO: Add Max Claim Value field here --%>

                <aui:select label="replacement-method" name="replacementMethod" value="<%= replacementMethod %>">
                    <aui:option label="Repair" value="Repair" />
                    <aui:option label="Repair or Replace" value="Repair or Replace" />
                    <aui:option label="Replace" value="Replace" />
                </aui:select>

                <aui:input
                    label="underwriter"
                    name="underwriter"
                    type="text"
                    value="<%= underwriter %>"
                />

                <aui:input
                    label="terms-url"
                    name="termsUrl"
                    type="text"
                    value="<%= termsUrl %>"
                />
            </aui:fieldset>
        </div>
    </div>
</aui:form>
