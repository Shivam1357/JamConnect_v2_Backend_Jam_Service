package com.jamconnect.jamservice.util;

public final class Constants {

    private Constants() {
    }

    /**
     * Must match User Service's Constants.USER_TOPIC exactly —
     * confirm the literal value against that file before running.
     */
    public static final String USER_TOPIC = "user-events";

    public static final String JAM_SERVICE_CONSUMER_GROUP = "jam-service-group";

    public static final String JAM_TOPIC = "jam-events";
}
