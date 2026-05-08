package com.clarityvisionsolutions.commerce.datasource;

import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.data.source.CPDataSource;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.helper.CPDefinitionHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = {
		"commerce.product.data.source.name=" + ServicePlanCPDataSource.NAME
	},
	service = CPDataSource.class
)
public class ServicePlanCPDataSource implements CPDataSource {

	public static final String NAME = "service-plan-featured";

	@Override
	public String getLabel(Locale locale) {

		// TODO: Return the data source label

		return NAME;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public CPDataSourceResult getResult(
			HttpServletRequest httpServletRequest, int start, int end)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long companyId = themeDisplay.getCompanyId();
		long groupId = themeDisplay.getScopeGroupId();
		Locale locale = themeDisplay.getLocale();

		// Fetch all approved Service Plan products

		DynamicQuery dynamicQuery = _cpDefinitionLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.eq("companyId", companyId));
		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("productTypeName", "service-plan"));
		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"status", WorkflowConstants.STATUS_APPROVED));

		List<CPDefinition> cpDefinitions =
			_cpDefinitionLocalService.dynamicQuery(dynamicQuery);

		// Filter by servicePlan_maxClaimValue Expando attribute

		List<CPDefinition> filteredDefinitions = new ArrayList<>();

		for (CPDefinition cpDefinition : cpDefinitions) {
			double maxClaimValue = 0;

			try {
				maxClaimValue = GetterUtil.getDouble(
					GetterUtil.getString(
						_expandoValueLocalService.getData(
							companyId, CPDefinition.class.getName(),
							ExpandoTableConstants.DEFAULT_TABLE_NAME,
							"servicePlan_maxClaimValue",
							cpDefinition.getCPDefinitionId())));
			}
			catch (Exception e) {
				_log.warn(
					"ServicePlanCPDataSource: Expando read failed for " +
						"cpDefinitionId=" + cpDefinition.getCPDefinitionId() +
						" - " + e.getMessage());
			}

			// TODO: Add filter condition here

			filteredDefinitions.add(cpDefinition);
		}

		// Build CPCatalogEntries with pagination

		int totalCount = filteredDefinitions.size();
		int safeStart = Math.min(start, totalCount);
		int safeEnd = (end < 0) ? totalCount : Math.min(end, totalCount);

		List<CPCatalogEntry> cpCatalogEntries = new ArrayList<>();

		for (CPDefinition cpDefinition :
				filteredDefinitions.subList(safeStart, safeEnd)) {

			try {
				CPCatalogEntry cpCatalogEntry =
					_cpDefinitionHelper.getCPCatalogEntry(
						-1L, groupId, cpDefinition.getCPDefinitionId(), locale);

				if (cpCatalogEntry != null) {
					cpCatalogEntries.add(cpCatalogEntry);
				}
			}
			catch (Exception e) {
				_log.warn(
					"ServicePlanCPDataSource: getCPCatalogEntry threw: " +
						e.getMessage());
			}
		}

		return new CPDataSourceResult(cpCatalogEntries, totalCount);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServicePlanCPDataSource.class);

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	@Reference
	private Language _language;

}
