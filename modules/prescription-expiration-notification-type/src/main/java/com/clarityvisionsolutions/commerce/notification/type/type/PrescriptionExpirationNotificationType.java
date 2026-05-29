package com.clarityvisionsolutions.commerce.notification.type.type;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationQueueEntryLocalService;
import com.liferay.notification.service.NotificationRecipientLocalService;
import com.liferay.notification.service.NotificationRecipientSettingLocalService;
import com.liferay.notification.term.evaluator.NotificationTermEvaluatorTracker;
import com.liferay.notification.type.BaseNotificationType;
import com.liferay.notification.type.NotificationType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/*
 * SYNTAX REFERENCE
 *
 * Pattern 1 — Returning the notification type key:
 *
 *   @Override
 *   public String getType() {
 *       return TYPE_KEY;
 *   }
 *
 * Pattern 2 — Resolving a recipient from a role name:
 *
 *   for (NotificationRecipientSetting setting : notificationRecipientSettings) {
 *       if ("companyId".equals(setting.getName())) {
 *           long companyId = GetterUtil.getLong(setting.getValue());
 *
 *           Role role = _roleLocalService.fetchRole(companyId, "Role Name");
 *
 *           if (role != null) {
 *               List<User> users = userLocalService.getRoleUsers(
 *                   role.getRoleId());
 *
 *               if (!users.isEmpty()) {
 *                   return new Object[] {
 *                       users.get(0).getUserId()
 *                   };
 *               }
 *           }
 *       }
 *   }
 *
 *   return new Object[0];
 */

@Component(
	property = "notification.type.key=" + PrescriptionExpirationNotificationType.TYPE_KEY,
	service = {NotificationType.class, PrescriptionExpirationNotificationType.class}
)
public class PrescriptionExpirationNotificationType
	extends BaseNotificationType {

	public static final String TYPE_KEY = "prescription-expiration";

	@Override
	public String getType() {

		// TODO 1: Return the TYPE_KEY constant.

		return "";
	}

	@Override
	public String getTypeLanguageKey() {
		return TYPE_KEY;
	}

	@Override
	public Map<String, String> evaluateNotificationRecipientSettings(
			long companyId, NotificationContext notificationContext,
			Map<String, Object> termValues)
		throws PortalException {

		return Collections.singletonMap(
			"companyId", String.valueOf(companyId));
	}

	@Override
	public Set<String> getAllowedNotificationRecipientSettingsNames() {
		return Collections.singleton("companyId");
	}

	@Override
	public Object[] toRecipients(
		List<NotificationRecipientSetting> notificationRecipientSettings) {

		// TODO 2: Resolve the recipient's userId from the account ID.
		// Use _accountEntryUserRelLocalService to get the account's users,
		// then return the first user's userId as a single-element Object array,
		// or new Object[0] if the account has no users.

		return new Object[0];
	}

	@Override
	public void sendNotification(NotificationContext notificationContext)
		throws PortalException {

		siteDefaultLocale = LocaleUtil.getDefault();

		notificationContext.setType(TYPE_KEY);

		NotificationTemplate notificationTemplate =
			notificationContext.getNotificationTemplate();

		Map<String, Object> termValues = notificationContext.getTermValues();

		String body = formatLocalizedContent(
			notificationTemplate.getBodyMap(), notificationContext);

		String subject = formatLocalizedContent(
			notificationTemplate.getSubjectMap(), notificationContext);

		Map<String, String> recipientSettingsMap =
			evaluateNotificationRecipientSettings(
				notificationContext.getCompanyId(), notificationContext,
				termValues);

		User user = userLocalService.fetchUser(notificationContext.getUserId());

		if (user == null) {
			_log.warn(
				"PrescriptionExpirationNotificationType: user not found for " +
					"userId=" + notificationContext.getUserId() +
					", skipping notification");

			return;
		}

		prepareNotificationContext(
			user, body, notificationContext, recipientSettingsMap, subject);

		Object[] recipients = toRecipients(
			notificationContext.getNotificationRecipientSettings());

		for (Object recipient : recipients) {
			long recipientUserId = GetterUtil.getLong(recipient);

			if (recipientUserId == 0) {
				continue;
			}

			try {
				JSONObject payloadJSONObject = JSONFactoryUtil.createJSONObject();

				payloadJSONObject.put(
					"classPK", notificationContext.getClassPK());
				payloadJSONObject.put("subject", subject);
				payloadJSONObject.put("body", body);

				_userNotificationEventLocalService.sendUserNotificationEvents(
					recipientUserId, TYPE_KEY,
					UserNotificationDeliveryConstants.TYPE_WEBSITE,
					payloadJSONObject);

				if (_log.isInfoEnabled()) {
					_log.info(
						"PrescriptionExpirationNotificationType: user " +
							"notification sent to userId=" + recipientUserId);
				}
			}
			catch (Exception exception) {
				_log.error(
					"PrescriptionExpirationNotificationType: failed to send " +
						"user notification to userId=" + recipientUserId,
					exception);
			}
		}
	}

	// Redeclare @Reference for inherited protected fields from BaseNotificationType.
	// OSGi DS does not process @Reference annotations from superclasses in
	// external bundles — they must be re-wired explicitly in the concrete component.

	@Reference
	protected void setNotificationQueueEntryLocalService(
		NotificationQueueEntryLocalService notificationQueueEntryLocalService) {

		this.notificationQueueEntryLocalService =
			notificationQueueEntryLocalService;
	}

	@Reference
	protected void setNotificationRecipientLocalService(
		NotificationRecipientLocalService notificationRecipientLocalService) {

		this.notificationRecipientLocalService =
			notificationRecipientLocalService;
	}

	@Reference
	protected void setNotificationRecipientSettingLocalService(
		NotificationRecipientSettingLocalService
			notificationRecipientSettingLocalService) {

		this.notificationRecipientSettingLocalService =
			notificationRecipientSettingLocalService;
	}

	@Reference
	protected void setNotificationTermEvaluatorTracker(
		NotificationTermEvaluatorTracker notificationTermEvaluatorTracker) {

		this.notificationTermEvaluatorTracker = notificationTermEvaluatorTracker;
	}

	@Reference
	protected void setPortal(Portal portal) {
		this.portal = portal;
	}

	@Reference
	protected void setUserGroupLocalService(
		UserGroupLocalService userGroupLocalService) {

		this.userGroupLocalService = userGroupLocalService;
	}

	@Reference
	protected void setUserLocalService(UserLocalService userLocalService) {
		this.userLocalService = userLocalService;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PrescriptionExpirationNotificationType.class);

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}
