package com.campmobile.android.sampleapp.activity;

import static com.campmobile.android.sampleapp.SampleConstants.RequestCode.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.campmobile.android.bandsdk.BandManager;
import com.campmobile.android.bandsdk.BandManagerFactory;
import com.campmobile.android.bandsdk.api.ApiCallbacks;
import com.campmobile.android.bandsdk.entity.Band;
import com.campmobile.android.bandsdk.entity.Friend;
import com.campmobile.android.bandsdk.entity.Member;
import com.campmobile.android.bandsdk.entity.OfficialBand;
import com.campmobile.android.bandsdk.entity.Quota;
import com.campmobile.android.bandsdk.entity.QuotaType;
import com.campmobile.android.bandsdk.entity.Quotas;
import com.campmobile.android.sampleapp.CustomAdapter.MenuAdapter;
import com.campmobile.android.sampleapp.CustomAdapter.MenuItem;
import com.campmobile.android.sampleapp.R;
import com.campmobile.android.sampleapp.SampleConstants;

public class MainActivity extends BaseToolbarActivity {
	private BandManager bandManager;
	private MenuAdapter menuAdapter;

	private ListView menuListView;

	private BandSelectType bandSelectType;
	private OfficialBand officialBand;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		setToolbar(R.string.title_main);
		menuListView = (ListView) findViewById(R.id.main_list);

		bandManager = BandManagerFactory.getSingleton();
		menuAdapter = new MenuAdapter(this);
		menuAdapter.addList(getMenuItems());
		menuListView.setAdapter(menuAdapter);

		getOfficialBandInformation(false);
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.exit_dialog_title);
		builder.setMessage(R.string.exit_dialog_description);
		builder.setPositiveButton(R.string.yes_text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.setNegativeButton(R.string.no_text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
	}

	@NonNull
	private List<MenuItem> getMenuItems() {
		List<MenuItem> menuItems = new ArrayList<>();
		menuItems.add(new MenuItem(getResources().getString(R.string.main_title_account), true, null));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_logout), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				logout();
			}
		}));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_disconnect), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				disconnect();
			}
		}));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_access_token), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				getAccessToken();
			}
		}));

		menuItems.add(new MenuItem(getResources().getString(R.string.main_title_user), true, null));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_my_profile), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoMyProfileActivity();
			}
		}));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_quota_info), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				getQuota();
			}
		}));

		menuItems.add(new MenuItem(getResources().getString(R.string.main_title_friend_list), true, null));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_friend_list), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoFriendListActivity();
			}
		}));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_member_list), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoMemberListActivity();
			}
		}));

		menuItems.add(new MenuItem(getResources().getString(R.string.main_title_create_band), true, null));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_create_guild_band), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoGuildBandCreateActivity();
			}
		}));

		menuItems.add(new MenuItem(getResources().getString(R.string.main_title_join_band), true, null));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_join_official_band), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				joinOfficialBand();
			}
		}));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_join_guild_band), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoGuildBandJoinActivity();
			}
		}));

		menuItems.add(new MenuItem(getResources().getString(R.string.main_title_band_information), true, null));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_band_list), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoBandListActivity();
			}
		}));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_official_band_information), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				getOfficialBandInformation(true);
			}
		}));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_official_band_posts), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(officialBand == null) {
					return;
				} else if(!officialBand.isMember()) {
					showMessage(R.string.is_not_official_band_member);
					return;
				}
				gotoPostsActivity(officialBand.getBandKey(), SampleConstants.BandType.OFFICIAL, SampleConstants.PostType.POST);
			}
		}));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_official_band_notices), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(officialBand == null) {
					return;
				} else if(!officialBand.isMember()) {
					showMessage(R.string.is_not_official_band_member);
					return;
				}
				gotoPostsActivity(officialBand.getBandKey(), SampleConstants.BandType.OFFICIAL, SampleConstants.PostType.NOTICE);
			}
		}));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_guild_band_posts), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoPostsActivity(SampleConstants.BandType.GUILD, SampleConstants.PostType.POST);
			}
		}));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_guild_band_notices), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoPostsActivity(SampleConstants.BandType.GUILD, SampleConstants.PostType.NOTICE);
			}
		}));

		menuItems.add(new MenuItem(getResources().getString(R.string.main_title_posts), true, null));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_write_post), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoWritePostActivity();
			}
		}));

		menuItems.add(new MenuItem(getResources().getString(R.string.main_title_messages), true, null));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_send_message), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SendMessageActivity.class);
				startActivity(intent);
			}
		}));

		menuItems.add(new MenuItem(getResources().getString(R.string.main_title_open), true, null));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_goto_band), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoBandSelect(BandSelectType.GOTO_BAND);
			}
		}));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_goto_chat), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoBandSelect(BandSelectType.GOTO_CHAT);
			}
		}));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_goto_band_app), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				bandManager.installBandApp(MainActivity.this);
			}
		}));

		menuItems.add(new MenuItem(getResources().getString(R.string.main_title_leave), true, null));
		menuItems.add(new MenuItem(getResources().getString(R.string.main_menu_guild_band_leave), false, new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoBandSelect(BandSelectType.LEAVE_BAND);
			}
		}));

		return menuItems;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case BAND_SELECT:
				if (resultCode == RESULT_OK) {
					Band band = data.getParcelableExtra(SampleConstants.ParameterKey.BAND);
					if (band != null) {
						if(bandSelectType == null) {
							showMessage("selected band : " + band.getName());
							return;
						}
						switch (bandSelectType) {
							case GOTO_BAND:
								bandManager.openBand(MainActivity.this, band.getBandKey());
								break;
							case GOTO_CHAT:
								bandManager.openBandChat(MainActivity.this, band.getBandKey());
								break;
							case LEAVE_BAND:
								leaveGuildBand(band.getBandKey());
								break;
						}

					}
				}
				break;
			case MEMBER_SELECT:
				if (resultCode == RESULT_OK) {
					Member selectedMember = data.getParcelableExtra(SampleConstants.ParameterKey.MEMBER);
					showMessage("selected member : " + selectedMember.getName());
				}
				break;
			case FRIEND_SELECT:
				if (resultCode == RESULT_OK) {
					Friend selectedFriend = data.getParcelableExtra(SampleConstants.ParameterKey.FRIEND);
					showMessage("selected friend : " + selectedFriend.getName());
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void gotoMyProfileActivity() {
		Intent intent = new Intent(this, MemberProfileActivity.class);
		startActivity(intent);
	}

	private void gotoFriendListActivity() {
		Intent intent = new Intent(this, FriendListActivity.class);
		startActivityForResult(intent, SampleConstants.RequestCode.FRIEND_SELECT);
	}

	private void gotoMemberListActivity() {
		Intent intent = new Intent(this, MemberListActivity.class);
		startActivityForResult(intent, SampleConstants.RequestCode.MEMBER_SELECT);
	}

	private void gotoGuildBandCreateActivity() {
		Intent intent = new Intent(this, GuildBandCreateActivity.class);
		startActivity(intent);
	}

	private void gotoGuildBandJoinActivity() {
		Intent intent = new Intent(this, GuildBandJoinActivity.class);
		startActivity(intent);
	}

	private void gotoBandListActivity() {
		this.bandSelectType = null;
		Intent intent = new Intent(this, BandListActivity.class);
		startActivityForResult(intent, SampleConstants.RequestCode.BAND_SELECT);
	}

	private void gotoPostsActivity(String bandKey, int bandType, int postType) {
		Intent intent = new Intent(this, BandPostsActivity.class);
		intent.putExtra(SampleConstants.BandType.INTENT_EXTRA_KEY, bandType);
		intent.putExtra(SampleConstants.PostType.INTENT_EXTRA_KEY, postType);
		if(bandKey != null && !bandKey.isEmpty()) {
			intent.putExtra(SampleConstants.ParameterKey.BAND_KEY, bandKey);
		}
		startActivity(intent);
	}

	private void gotoPostsActivity(int bandType, int postType) {
		Intent intent = new Intent(this, BandPostsActivity.class);
		intent.putExtra(SampleConstants.BandType.INTENT_EXTRA_KEY, bandType);
		intent.putExtra(SampleConstants.PostType.INTENT_EXTRA_KEY, postType);
		startActivity(intent);
	}

	private void gotoWritePostActivity() {
		Intent intent = new Intent(MainActivity.this, WritePostActivity.class);
		startActivity(intent);
	}

	private void gotoBandSelect(BandSelectType bandSelectType) {
		this.bandSelectType = bandSelectType;
		Intent intent = new Intent(this, BandListActivity.class);
		startActivityForResult(intent, SampleConstants.RequestCode.BAND_SELECT);
	}

	private void logout() {
		bandManager.logout(new ApiCallbacks<Void>() {
			@Override
			public void onResponse(Void response) {
				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onError(VolleyError error) {
				showDialog(R.string.fail, error.toString());
			}
		});
	}

	private void disconnect() {
		bandManager.disconnect(new ApiCallbacks<Void>() {
			@Override
			public void onResponse(Void response) {
				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onError(VolleyError error) {
				showDialog(R.string.fail, error.getMessage());
			}
		});
	}

	private void getAccessToken() {
		bandManager.getAccessToken(new ApiCallbacks<String>() {
			@Override
			public void onResponse(String accessToken) {
				showDialog(R.string.success, accessToken);
			}

			@Override
			public void onError(VolleyError volleyError) {
				showDialog(R.string.fail, volleyError.getMessage());
			}
		});
	}

	private void getQuota() {
		bandManager.getQuota(new ApiCallbacks<Quotas>() {
			@Override
			public void onResponse(Quotas quotas) {
				StringBuilder builder = new StringBuilder();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
				for (QuotaType quotaType : QuotaType.values()) {
					Quota quota = quotas.getQuota(quotaType);

					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.SECOND, (int) quota.getExpiredIn());

					builder.append("*").append(quotaType).append("\n");
					builder.append("  Total   : ").append(quota.getTotal()).append(" times\n");
					builder.append("  Remain  : ").append(quota.getRemaining()).append(" times\n");
					builder.append("  RenewAt : ").append(sdf.format(cal.getTime())).append("\n");
				}
				showDialog(R.string.success, builder.toString());
			}

			@Override
			public void onError(VolleyError error) {
				super.onError(error);
				showDialog(R.string.fail, error.getMessage());
			}
		});
	}

	private void joinOfficialBand() {
		bandManager.joinOfficialBand(new ApiCallbacks<Void>() {
			@Override
			public void onResponse(Void result) {
				showDialog(R.string.main_menu_join_official_band, "success");
			}

			@Override
			public void onError(VolleyError error) {
				super.onError(error);
				showDialog(R.string.main_menu_join_official_band, "error : " + error.getMessage());
			}

			@Override
			public void onPostExecute(boolean isSuccess) {
				if(isSuccess) {
					getOfficialBandInformation(false);
				}
			}
		});
	}

	private void getOfficialBandInformation(final boolean showInfoDialog) {
		bandManager.getOfficialBandInformation(new ApiCallbacks<OfficialBand>() {
			@Override
			public void onResponse(OfficialBand band) {
				if(showInfoDialog) {
					showDialog(R.string.main_menu_official_band_information, "official_band_key : " + band.getBandKey() + "\nis_official_band_member : " + band.isMember());
				}
				officialBand = band;
			}
		});
	}

	private void leaveGuildBand(String bandKey) {
		bandManager.leaveGuildBand(bandKey, new ApiCallbacks<Void>() {
			@Override
			public void onResponse(Void aVoid) {
				showDialog(R.string.main_menu_guild_band_leave, "success");
			}

			@Override
			public void onError(VolleyError error) {
				super.onError(error);
				showDialog(R.string.main_menu_guild_band_leave, "fail");
			}
		});
	}

	private enum BandSelectType {
		GOTO_BAND,
		GOTO_CHAT,
		LEAVE_BAND;
	}
}
