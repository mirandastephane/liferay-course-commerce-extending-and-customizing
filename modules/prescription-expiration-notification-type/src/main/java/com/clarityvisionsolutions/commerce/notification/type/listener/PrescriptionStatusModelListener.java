package com.clarityvisionsolutions.commerce.notification.type.listener;

import com.clarityvisionsolutions.commerce.notification.type.type.PrescriptionExpirationNotificationType;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.context.NotificationContextBuilder;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationTemplateLocalService;
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
 * Listens on ExpandoValue rather than AccountEntry because the Headless API
 * updates Expando rows before touching AccountEntry, so an AccountEntry
 * ModelListener always sees the post-update Expando value for both old and new.
 * The ExpandoValue listener receives the genuine pre/post values via
 * originalValue and updatedValue.
 */
@Component(
	service = ModelListener.class
)
public class PrescriptionStatusModelListener
	extends BaseModelListener<ExpandoValue> {

	@Override
	public void onAfterUpdate(
			ExpandoValue originalValue, ExpandoValue updatedValue)
		throws ModelListenerException {

		try {
			ExpandoColumn column = _expandoColumnLocalService.fetchExpandoColumn(
				updatedValue.getColumnId());

			if ((column == null) ||
				!"prescriptionStatus".equals(column.getName())) {

				return;
			}

			ExpandoTable table = _expandoTableLocalService.fetchExpandoTable(
				updatedValue.getTableId());

			if ((table == null) ||
				!AccountEntry.class.getName().equals(table.getClassName())) {

				return;
			}

			String oldStatus = GetterUtil.getString(originalValue.getData());
			String newStatus = GetterUtil.getString(updatedValue.getData());

			if (!"Expiring Soon".equals(newStatus) ||
				"Expiring Soon".equals(oldStatus)) {

				return;
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					"PrescriptionStatusModelListener: prescriptionStatus " +
						"transitioned from \"" + oldStatus + "\" to \"" +
						newStatus + "\" for accountEntryId=" +
						updatedValue.getClassPK() +
						" — dispatching notification");
			}

			AccountEntry accountEntry = _accountEntryLocalService.fetchAccountEntry(
				updatedValue.getClassPK());

			if (accountEntry == null) {
				_log.warn(
					"PrescriptionStatusModelListener: AccountEntry not found " +
						"for accountEntryId=" + updatedValue.getClassPK());

				return;
			}

			_sendNotification(accountEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private NotificationTemplate _findNotificationTemplate(long companyId) {
		return _notificationTemplateLocalService
			.fetchNotificationTemplateByExternalReferenceCode(
				"prescription-expiration-alert", companyId);
	}

	private void _sendNotification(AccountEntry accountEntry)
		throws Exception {

		long companyId = accountEntry.getCompanyId();

		NotificationTemplate notificationTemplate = _findNotificationTemplate(
			companyId);

		if (notificationTemplate == null) {
			_log.warn(
				"PrescriptionStatusModelListener: no notification template " +
					"found with ERC \"prescription-expiration-alert\" in " +
					"companyId=" + companyId + " — skipping notification");

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
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Reference
	private PrescriptionExpirationNotificationType
		_prescriptionExpirationNotificationType;

}
