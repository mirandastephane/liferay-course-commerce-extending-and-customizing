package com.clarityvisionsolutions.commerce.notification.type.handler;

import com.clarityvisionsolutions.commerce.notification.type.type.PrescriptionExpirationNotificationType;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;

import org.osgi.service.component.annotations.Component;

@Component(
	immediate = true,
	service = UserNotificationHandler.class
)
public class PrescriptionExpirationNotificationHandler
	extends BaseUserNotificationHandler {

	public PrescriptionExpirationNotificationHandler() {
		setPortletId(PrescriptionExpirationNotificationType.TYPE_KEY);
	}

	@Override
	protected String getBody(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject payloadJSONObject = JSONFactoryUtil.createJSONObject(
			userNotificationEvent.getPayload());

		return payloadJSONObject.getString("subject");
	}

	@Override
	protected String getLink(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		return "";
	}

}
