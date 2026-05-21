package com.clarityvisionsolutions.commerce.notification.type.evaluator;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.notification.term.evaluator.NotificationTermEvaluator;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Evaluates the five prescription-expiration template variables against the
 * AccountEntry Custom Fields (Expando) and the account's primary user.
 *
 * Registered with class.name=AccountEntry so NotificationTermEvaluatorTracker
 * routes term evaluation here whenever the notification context's className
 * is set to AccountEntry (which PrescriptionExpirationNotificationType does
 * via notificationContext.setType(TYPE_KEY) triggering the class.name lookup).
 *
 * The Object parameter in evaluate() is the notification context's termValues
 * Map<String, Object>, populated by PrescriptionStatusModelListener. Expected
 * keys:
 *   "id"        — AccountEntry primary key (String/long)
 *   "companyId" — company ID (String/long)
 *
 * Terms evaluated:
 *   [%CUSTOMER_NAME%]      — full name of the account's primary user (pre-configured)
 *   [%PRESCRIPTION_NUMBER%] — Expando "prescriptionNumber" (pre-configured)
 *   [%PRESCRIBING_DOCTOR%] — Expando "prescribingDoctor" (pre-configured)
 *   [%EXPIRATION_DATE%]    — Expando "expirationDate" (TODO 3)
 *   [%PRESCRIPTION_STATUS%] — Expando "prescriptionStatus" (pre-configured)
 */
@Component(
	property = "class.name=com.liferay.account.model.AccountEntry",
	service = NotificationTermEvaluator.class
)
public class PrescriptionExpirationNotificationTermEvaluator
	implements NotificationTermEvaluator {

	/**
	 * Evaluates a single template term against the account's data.
	 *
	 * @param context  CONTENT or RECIPIENT — not used here; all terms resolve
	 *                 the same way regardless of evaluation context
	 * @param object   the termValues Map<String, Object> from the notification
	 *                 context; cast internally to read "id" and "companyId"
	 * @param termName the template variable being evaluated, e.g.
	 *                 "[%CUSTOMER_NAME%]"
	 * @return the resolved string, or "" if the value cannot be found
	 */
	@Override
	@SuppressWarnings("unchecked")
	public String evaluate(
			NotificationTermEvaluator.Context context, Object object,
			String termName)
		throws PortalException {

		Map<String, Object> termValues = (Map<String, Object>)object;

		long accountEntryId = GetterUtil.getLong(termValues.get("id"));
		long companyId = GetterUtil.getLong(termValues.get("companyId"));

		if ("[%CUSTOMER_NAME%]".equals(termName)) {
			List<AccountEntryUserRel> rels =
				_accountEntryUserRelLocalService
					.getAccountEntryUserRelsByAccountEntryId(accountEntryId);

			if (!rels.isEmpty()) {
				User user = _userLocalService.fetchUser(
					rels.get(0).getAccountUserId());

				if (user != null) {
					return user.getFullName();
				}
			}

			return "";
		}

		if ("[%PRESCRIPTION_NUMBER%]".equals(termName)) {
			return GetterUtil.getString(
				_expandoValueLocalService.getData(
					companyId, AccountEntry.class.getName(),
					ExpandoTableConstants.DEFAULT_TABLE_NAME,
					"prescriptionNumber", accountEntryId, ""));
		}

		if ("[%PRESCRIBING_DOCTOR%]".equals(termName)) {
			return GetterUtil.getString(
				_expandoValueLocalService.getData(
					companyId, AccountEntry.class.getName(),
					ExpandoTableConstants.DEFAULT_TABLE_NAME,
					"prescribingDoctor", accountEntryId, ""));
		}

		if ("[%PRESCRIPTION_STATUS%]".equals(termName)) {
			return GetterUtil.getString(
				_expandoValueLocalService.getData(
					companyId, AccountEntry.class.getName(),
					ExpandoTableConstants.DEFAULT_TABLE_NAME,
					"prescriptionStatus", accountEntryId, ""));
		}

		if ("[%EXPIRATION_DATE%]".equals(termName)) {

			// TODO 3: Return the expiration date from the account's Expando
			// Follow the same pattern as [%PRESCRIPTION_NUMBER%] above.

			return "";
		}

		return "";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PrescriptionExpirationNotificationTermEvaluator.class);

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
