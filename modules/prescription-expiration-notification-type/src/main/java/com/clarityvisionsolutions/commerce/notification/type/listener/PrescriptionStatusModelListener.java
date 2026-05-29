package com.clarityvisionsolutions.commerce.notification.type.listener;

import com.clarityvisionsolutions.commerce.notification.type.type.PrescriptionExpirationNotificationType;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.context.NotificationContextBuilder;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;

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
 * Uses an ExpandoValue ModelListener's onBeforeUpdate hook. At this point
 * originalValue.getData() and updatedValue.getData() are in-memory model
 * objects that hold the genuine old and new raw data strings respectively,
 * without any database access. This is the only reliable hook for detecting
 * the transition because the Accounts Admin portlet writes ExpandoValues
 * BEFORE updating the AccountEntry, making AccountEntry-based listeners
 * unable to observe the pre-update Expando state.
 *
 * For Dropdown (String[]) Expando columns, Liferay serialises the selected
 * value using StringUtil.merge(), so a single-value dropdown stores its value
 * as a plain string (e.g. "Active", "Expiring Soon"). Direct string equality
 * on getData() is therefore reliable for this field.
 *
 * Error-handling contract:
 *   onBeforeUpdate: any failure → log error, never re-throw.
 *   A notification failure must never prevent the Expando update from persisting.
 */
@Component(
	service = ModelListener.class
)
public class PrescriptionStatusModelListener
	extends BaseModelListener<ExpandoValue> {

	@Override
	public void onBeforeUpdate(
			ExpandoValue originalValue, ExpandoValue updatedValue)
		throws ModelListenerException {

		try {
			ExpandoColumn column = _expandoColumnLocalService.fetchExpandoColumn(
				originalValue.getColumnId());

			if (column == null ||
				!"prescriptionStatus".equals(column.getName())) {

				return;
			}

			String oldData = originalValue.getData();
			String newData = updatedValue.getData();

			if (_log.isInfoEnabled()) {
				_log.info(
					"PrescriptionStatusModelListener: prescriptionStatus " +
						"update detected -> old=[" + oldData + "] new=[" +
						newData + "]");
			}

			boolean wasExpiringSoon = "Expiring Soon".equals(oldData);
			boolean isExpiringSoon = "Expiring Soon".equals(newData);

			if (!isExpiringSoon || wasExpiringSoon) {
				return;
			}

			long accountEntryId = updatedValue.getClassPK();

			AccountEntry accountEntry =
				_accountEntryLocalService.fetchAccountEntry(accountEntryId);

			if (accountEntry == null) {
				_log.warn(
					"PrescriptionStatusModelListener: accountEntry not found " +
						"for classPK=" + accountEntryId +
						" \u2014 skipping notification");

				return;
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					"PrescriptionStatusModelListener: prescriptionStatus " +
						"transitioned to \"Expiring Soon\" for accountEntryId=" +
						accountEntryId + " \u2014 dispatching notification");
			}

			_sendNotification(accountEntry);
		}
		catch (Exception exception) {
			_log.error(
				"PrescriptionStatusModelListener: failed to process " +
					"prescriptionStatus transition for columnId=" +
					originalValue.getColumnId() +
					" classPK=" + originalValue.getClassPK() +
					" \u2014 Expando update will still be committed",
				exception);

			// Do not re-throw — a notification failure must not abort the update
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
					"companyId=" + companyId + " -> skipping notification");

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
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Reference
	private PrescriptionExpirationNotificationType
		_prescriptionExpirationNotificationType;

}
