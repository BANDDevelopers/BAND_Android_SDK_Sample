package com.campmobile.android.sampleapp;

public class SampleConstants {

	public static class BandType {
		public static final String INTENT_EXTRA_KEY = "BandType";

		public static final int GUILD = 1;
		public static final int OFFICIAL = 2;
	}

	public static class PostType {
		public static final String INTENT_EXTRA_KEY = "PostType";

		public static final int POST = 1;
		public static final int NOTICE = 2;
	}

	public static class RequestCode {
		public static final int MEMBER_SELECT = 0;
		public static final int BAND_SELECT = 1;
		public static final int FRIEND_SELECT = 2;
	}

	public static class ParameterKey {
		public static final String BAND = "band";
		public static final String BAND_KEY = "band_key";
		public static final String MEMBER = "member";
		public static final String FRIEND = "friend";
	}
}
