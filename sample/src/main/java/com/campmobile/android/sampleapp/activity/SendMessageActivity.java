package com.campmobile.android.sampleapp.activity;

import static com.campmobile.android.sampleapp.SampleConstants.RequestCode.MEMBER_SELECT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.campmobile.android.bandsdk.BandManager;
import com.campmobile.android.bandsdk.BandManagerFactory;
import com.campmobile.android.bandsdk.api.ApiCallbacks;
import com.campmobile.android.bandsdk.entity.Band;
import com.campmobile.android.bandsdk.entity.Member;
import com.campmobile.android.sampleapp.R;
import com.campmobile.android.sampleapp.SampleConstants;

public class SendMessageActivity extends BaseToolbarActivity {

	private enum MessageType {
		INVITATION_MESSAGE, GAME_MESSAGE;
	}

	private BandManager bandManager;

	private TextView userNameTextView;
	private EditText titleEditText;
	private EditText messageEditText;
	private EditText imageEditText;
	private EditText linkButtonTextEditText;
	private EditText androidCustomUrlEditText;
	private EditText iosCustomUrlEditText;
	private Button sendMessageButton;

	private Band selectedBand;
	private Member selectedMember;
	private MessageType messageType = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_message);

		initParam();
		initUI();
		setToolbar(R.string.title_send_message);
	}

	private void initParam() {
		bandManager = BandManagerFactory.getSingleton();
	}

	@SuppressLint("SimpleDateFormat")
	private void initUI() {
		final ImageView gameMsgImgaeView = (ImageView) findViewById(R.id.game_msg_img);
		final ImageView invitationMsgImgaeView = (ImageView) findViewById(R.id.invitation_msg_img);

		findViewById(R.id.game_msg_layout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				messageType = MessageType.GAME_MESSAGE;
				gameMsgImgaeView.setImageResource(R.drawable.btn_thumbphoto_check);
				invitationMsgImgaeView.setImageResource(R.drawable.btn_thumbphoto_uncheck);
			}
		});

		findViewById(R.id.invitation_msg_layout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				messageType = MessageType.INVITATION_MESSAGE;
				invitationMsgImgaeView.setImageResource(R.drawable.btn_thumbphoto_check);
				gameMsgImgaeView.setImageResource(R.drawable.btn_thumbphoto_uncheck);
			}
		});

		userNameTextView = (TextView) findViewById(R.id.user_name_text_view);
		userNameTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SendMessageActivity.this, MemberListActivity.class);
				startActivityForResult(intent, SampleConstants.RequestCode.MEMBER_SELECT);
			}
		});
		titleEditText = (EditText) findViewById(R.id.title_edit_text);
		messageEditText = (EditText) findViewById(R.id.message_edit_text);
		imageEditText = (EditText) findViewById(R.id.image_edit_text);
		linkButtonTextEditText = (EditText) findViewById(R.id.link_button_text_edit_text);
		androidCustomUrlEditText = (EditText) findViewById(R.id.android_custom_url_edit_text);
		iosCustomUrlEditText = (EditText) findViewById(R.id.ios_custom_url_edit_text);
		sendMessageButton = (Button) findViewById(R.id.send_message_button);
		sendMessageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});
	}

	private void sendMessage() {

		if (messageType == null) {
			showMessage(R.string.send_msg_select_type);
			return;
		}

		if (selectedMember == null) {
			showMessage(R.string.send_msg_select_member);
			return;
		}

		String userKey = selectedMember.getUserKey();
		String bandKey = selectedBand.getBandKey();
		String title = titleEditText.getText().toString();
		String message = messageEditText.getText().toString();
		String imageUrl = imageEditText.getText().toString();
		String linkButtonText = linkButtonTextEditText.getText().toString();
		String androidCustomUrl = androidCustomUrlEditText.getText().toString();
		String iosCustomUrl = iosCustomUrlEditText.getText().toString();

		if (messageType == MessageType.GAME_MESSAGE) {
			bandManager.sendMessage(userKey, bandKey, title, message, imageUrl, linkButtonText, androidCustomUrl, iosCustomUrl, new ApiCallbacks<Void>() {
				@Override
				public void onResponse(Void result) {
					showMessage(R.string.success);
				}

				@Override
				public void onError(VolleyError error) {
					super.onError(error);
					showMessage(error.getMessage());
				}
			});
		} else if (messageType == MessageType.INVITATION_MESSAGE) {
			bandManager.sendInvitation(userKey, bandKey, title, message, imageUrl, linkButtonText, androidCustomUrl, iosCustomUrl, new ApiCallbacks<Void>() {
				@Override
				public void onResponse(Void result) {
					showMessage(R.string.success);
				}

				@Override
				public void onError(VolleyError error) {
					super.onError(error);
					showMessage(error.getMessage());
				}
			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case MEMBER_SELECT:
				if (resultCode == RESULT_OK) {
					selectedBand = data.getParcelableExtra(SampleConstants.ParameterKey.BAND);
					selectedMember = data.getParcelableExtra(SampleConstants.ParameterKey.MEMBER);
					userNameTextView.setText(selectedMember.getName());
				}
				break;
		}
	}
}
