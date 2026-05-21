package com.clarityvisionsolutions.commerce.notification.type.listener;

import com.clarityvisionsolutions.commerce.notification.type.type.PrescriptionExpirationNotificationType;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.context.NotificationContextBuilder;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Fires a prescription-expiration notification whenever an AccountEntry's
 * prescriptionStatus Custom Field (Expando) transitions TO "Expiring Soon".
 *
 * Transition guard — notification fires ONLY when:
 *   old prescriptionStatus != "Expiring Soon"
 *   AND new prescriptionStatus == "Expiring Soon"
 *
 * This prevents re-firing if the status is later updated while already at
 * "Expiring Soon", or when the account moves from "Expiring Soon" to "Expired".
 *
 * Both originalEntry and updatedEntry Expando values are read using the
 * ExpandoValueLocalService directly (NOT expandoBridge.getAttribute()) to
 * ensure consistent read semantics across the transaction boundary.
 */
@Component(
	service = ModelListener.class
)
public class PrescriptionStatusModelListener
	extends BaseModelListener<AccountEntry> {

	@Override
	public void onAfterUpdate(
			AccountEntry originalEntry, AccountEntry updatedEntry)
		throws ModelListenerException {

		try {
			String oldStatus = GetterUtil.getString(
				_expandoValueLocalService.getData(
					originalEntry.getCompanyId(),
					AccountEntry.class.getName(),
					ExpandoTableConstants.DEFAULT_TABLE_NAME,
					"prescriptionStatus",
					originalEntry.getAccountEntryId(),
					""));

			String newStatus = GetterUtil.getString(
				_expandoValueLocalService.getData(
					updatedEntry.getCompanyId(),
					AccountEntry.class.getName(),
					ExpandoTableConstants.DEFAULT_TABLE_NAME,
					"prescriptionStatus",
					updatedEntry.getAccountEntryId(),
					""));

			if (!"Expiring Soon".equals(newStatus) ||
				"Expiring Soon".equals(oldStatus)) {

				return;
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					"PrescriptionStatusModelListener: prescriptionStatus " +
						"transitioned from \"" + oldStatus + "\" to \"" +
						newStatus + "\" for accountEntryId=" +
						updatedEntry.getAccountEntryId() +
						" — dispatching notification");
			}

			_sendNotification(updatedEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private NotificationTemplate _findNotificationTemplate(long companyId) {
		DynamicQuery dynamicQuery =
			_notificationTemplateLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("companyId", companyId));
		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"type", PrescriptionExpirationNotificationType.TYPE_KEY));

		List<NotificationTemplate> notificationTemplates =
			_notificationTemplateLocalService.dynamicQuery(dynamicQuery);

		if (notificationTemplates.isEmpty()) {
			return null;
		}

		return notificationTemplates.get(0);
	}

	private void _sendNotification(AccountEntry accountEntry)
		throws Exception {

		long companyId = accountEntry.getCompanyId();

		NotificationTemplate notificationTemplate = _findNotificationTemplate(
			companyId);

		if (notificationTemplate == null) {
			_log.warn(
				"PrescriptionStatusModelListener: no notification template " +
					"found for type \"" +
					PrescriptionExpirationNotificationType.TYPE_KEY +
					"\" in companyId=" + companyId +
					" — skipping notification");

			return;
		}

		long accountEntryId = accountEntry.getAccountEntryId();

		long userId = accountEntry.getUserId();

		List<AccountEntryUserRel> rels =
			_accountEntryUserRelLocalService
				.getAccountEntryUserRelsByAccountEntryId(accountEntryId);

		if (!rels.isEmpty()) {
			userId = rels.get(0).getAccountUserId();
		}

		Map<String, Object> termValues = new HashMap<>();

		termValues.put("id", String.valueOf(accountEntryId));
		termValues.put("companyId", String.valueOf(companyId));

		NotificationContext notificationContext = new NotificationContextBuilder(
		).className(
			AccountEntry.class.getName()
		).classPK(
			accountEntryId
		).companyId(
			companyId
		).groupId(
			0
		).notificationTemplate(
			notificationTemplate
		).termValues(
			termValues
		).userId(
			userId
		).build();

		_prescriptionExpirationNotificationType.sendNotification(
			notificationContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PrescriptionStatusModelListener.class);

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	@Reference
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Reference
	private PrescriptionExpirationNotificationType
		_prescriptionExpirationNotificationType;

}
