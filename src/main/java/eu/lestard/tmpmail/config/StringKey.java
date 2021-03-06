package eu.lestard.tmpmail.config;

public enum StringKey {

	/**
	 * This key is only needed for CDI injection and may not be used.
	 */
	_DEFAULT,

	/**
	 * The host of the SMTP server that the outgoing emails are send to.
	 */
	OUTGOING_SMTP_HOST,

	/**
	 * The username that is used to connect to the outgoing SMTP server.
	 */
	OUTGOING_SMTP_USERNAME,

	/**
	 * The password that is used to connect to the outgoing SMTP server.
	 */
	OUTGOING_SMTP_PASSWORD

}
