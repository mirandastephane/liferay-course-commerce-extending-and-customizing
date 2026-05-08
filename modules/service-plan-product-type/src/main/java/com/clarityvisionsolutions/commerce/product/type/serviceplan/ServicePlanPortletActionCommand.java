package com.clarityvisionsolutions.commerce.product.type.serviceplan;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.Serializable;
import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_cp_definition_service_plan"
	},
	service = MVCActionCommand.class
)
public class ServicePlanPortletActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long cpDefinitionId = ParamUtil.getLong(actionRequest, "cpDefinitionId");

		if (cpDefinitionId <= 0) {
			return;
		}

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		if (!ServicePlanCPType.NAME.equals(cpDefinition.getProductTypeName())) {
			return;
		}

		ExpandoBridge expandoBridge = cpDefinition.getExpandoBridge();

		_ensureAttribute(
			expandoBridge, "servicePlan_coverageDuration",
			ExpandoColumnConstants.STRING);
		_ensureAttribute(
			expandoBridge, "servicePlan_gracePeriod",
			ExpandoColumnConstants.STRING);
		_ensureAttribute(
			expandoBridge, "servicePlan_maxNumberOfClaims",
			ExpandoColumnConstants.STRING);
		_ensureAttribute(
			expandoBridge, "servicePlan_maxClaimValue",
			ExpandoColumnConstants.STRING);
		_ensureAttribute(
			expandoBridge, "servicePlan_replacementMethod",
			ExpandoColumnConstants.STRING);
		_ensureAttribute(
			expandoBridge, "servicePlan_underwriter",
			ExpandoColumnConstants.STRING);
		_ensureAttribute(
			expandoBridge, "servicePlan_termsUrl",
			ExpandoColumnConstants.STRING);

		expandoBridge.setAttribute(
			"servicePlan_coverageDuration",
			ParamUtil.getString(actionRequest, "coverageDuration"));
		expandoBridge.setAttribute(
			"servicePlan_gracePeriod",
			ParamUtil.getString(actionRequest, "gracePeriod"));
		expandoBridge.setAttribute(
			"servicePlan_maxNumberOfClaims",
			ParamUtil.getString(actionRequest, "maxNumberOfClaims"));
		expandoBridge.setAttribute(
			"servicePlan_maxClaimValue",
			ParamUtil.getString(actionRequest, "maxClaimValue"));
		expandoBridge.setAttribute(
			"servicePlan_replacementMethod",
			ParamUtil.getString(actionRequest, "replacementMethod"));
		expandoBridge.setAttribute(
			"servicePlan_underwriter",
			ParamUtil.getString(actionRequest, "underwriter"));
		expandoBridge.setAttribute(
			"servicePlan_termsUrl",
			ParamUtil.getString(actionRequest, "termsUrl"));

		int workflowAction = ParamUtil.getInteger(
			actionRequest, "workflowAction",
			WorkflowConstants.ACTION_SAVE_DRAFT);

		if (workflowAction == WorkflowConstants.ACTION_PUBLISH) {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				CPDefinition.class.getName(), actionRequest);

			_cpDefinitionLocalService.updateStatus(
				serviceContext.getUserId(), cpDefinitionId,
				WorkflowConstants.STATUS_APPROVED, serviceContext,
				Collections.<String, Serializable>emptyMap());
		}

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		sendRedirect(actionRequest, actionResponse, redirect);
	}

	private void _ensureAttribute(
			ExpandoBridge expandoBridge, String name, int type)
		throws Exception {

		if (!expandoBridge.hasAttribute(name)) {
			expandoBridge.addAttribute(name, type, false);
		}
	}

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

}
