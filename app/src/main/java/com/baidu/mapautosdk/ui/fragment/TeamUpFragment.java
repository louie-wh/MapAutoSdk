package com.baidu.mapautosdk.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.mapautosdk.BDMapAutoInitConfig;
import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.base.CoordType;
import com.baidu.mapautosdk.api.search.team.TeamOption;
import com.baidu.mapautosdk.interfaces.team.IAutoTeamManager;
import com.baidu.mapautosdk.model.AutoCreateGroupInfo;
import com.baidu.mapautosdk.model.AutoGetMyGroupsInfo;
import com.baidu.mapautosdk.model.AutoLocData;
import com.baidu.mapautosdk.model.AutoUpLocationInfo;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.mapautosdk.util.SpUtil;
import com.baidu.platform.comapi.sdk.mapapi.model.LatLng;

import java.util.ArrayList;

public class TeamUpFragment extends BaseFragment {
    private static final String TAG = "TeamUpFragment";
    private ViewGroup mContentView;
    private String channelId;
    private String accessToken;
    private TextView tvList;
    private TextView tvListTwo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_team_up, container, false);
        } else {
            ViewGroup parentView = (ViewGroup) mContentView.getParent();
            if (parentView != null) {
                parentView.removeView(mContentView);
            }
        }
        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BDMapAutoInitConfig bdMapAutoInitConfig = new BDMapAutoInitConfig(getContext());
        channelId = bdMapAutoInitConfig.getChannelId();
        accessToken = SpUtil.getInStace().getString("access_token", null);
        Log.d("main", "onViewCreated: " + channelId);

        // 查询本人队伍
        mContentView.findViewById(R.id.btn_team_up_my_team).setOnClickListener(clickListener);
        // 创建队伍
        mContentView.findViewById(R.id.btn_team_up_create_team).setOnClickListener(clickListener);
        // 解散队伍
        mContentView.findViewById(R.id.btn_team_up_del_team).setOnClickListener(clickListener);
        // 加入队伍
        mContentView.findViewById(R.id.btn_team_up_enter_team).setOnClickListener(clickListener);
        // 退出队伍
        mContentView.findViewById(R.id.btn_team_up_quit_team).setOnClickListener(clickListener);
        // 踢出队伍
        mContentView.findViewById(R.id.btn_team_up_kick_team).setOnClickListener(clickListener);
        // 查询队伍信息
        mContentView.findViewById(R.id.btn_team_up_get_group_info).setOnClickListener(clickListener);
        // 修改队伍信息
        mContentView.findViewById(R.id.btn_team_up_update_group_info).setOnClickListener(clickListener);
        // 修改本人昵称
        mContentView.findViewById(R.id.btn_team_up_update_nickname).setOnClickListener(clickListener);
        // 上报位置
        mContentView.findViewById(R.id.btn_team_up_loc).setOnClickListener(clickListener);

        tvList = mContentView.findViewById(R.id.tv_list);
        tvListTwo = mContentView.findViewById(R.id.tv_list_two);
        TextView tvListThree = mContentView.findViewById(R.id.tv_list_three);

        BDAutoMapFactory.getAutoTeamManager().setTeamChangeCallback(new IAutoTeamManager.OnTeamChangeCallback() {

            @Override
            public void onAddedMember() {
                tvListThree.setText("onAddedMember");
                Log.d(TAG, "onAddedMember: ");
            }

            @Override
            public void onKickedMember() {
                tvListThree.setText("onKickedMember");
                Log.d(TAG, "onKickedMember: ");
            }

            @Override
            public void onGroupInfoChange() {
                tvListThree.setText("onGroupInfoChange");
                Log.d(TAG, "onGroupInfoChange: ");
            }

            @Override
            public void onNikeNameChange() {
                tvListThree.setText("onNikeNameChange");
                Log.d(TAG, "onNikeNameChange: ");
            }

            @Override
            public void onGroupDeleted() {
                tvListThree.setText("onGroupDeleted");
                Log.d(TAG, "onGroupDeleted: ");
            }

            @Override
            public void onQuitTeam() {
                tvListThree.setText("onQuitTeam");
                Log.d(TAG, "onQuitTeam: ");
            }
        });
    }

    private String groupId;
    private String groupCode;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TeamOption teamOption = new TeamOption().setAccessToken(accessToken).setChannelId(channelId);
            AutoLocData curLocation = BDAutoMapFactory.getAutoLocationManager().getCurLocation(CoordType.BD09LL);
            switch (v.getId()) {
                case R.id.btn_team_up_my_team:
                    BDAutoMapFactory.getAutoTeamManager().requestUserMyGroup(teamOption);
                    BDAutoMapFactory.getAutoTeamManager().setOnUserMyGroupResultListener(
                            new IAutoTeamManager.OnUserMyGroupResultListener() {
                                @Override
                                public void onGetUserMyGroupResult(AutoGetMyGroupsInfo info) {
                                    Log.d(TAG, "onGetUserMyGroupResult: " + info);
                                    if (info != null) {
                                        AutoGetMyGroupsInfo.MyGroupList myGroupList = info.getMyGroupList();
                                        if (myGroupList != null) {
                                            ArrayList<AutoGetMyGroupsInfo.CreatedGroups> createdGroups = myGroupList.getCreatedGroups();
                                            if (createdGroups != null && createdGroups.size() > 0) {
                                                for (AutoGetMyGroupsInfo.CreatedGroups createdGroup : createdGroups) {
                                                    tvList.setText(createdGroup.getGroupMembersCnt());
                                                    groupId = createdGroup.getGroupId();
                                                    groupCode = createdGroup.getGroupCode();
                                                    Log.d(TAG, "onGetUserMyGroupResult: " + groupId);
                                                    Log.d(TAG, "onGetUserMyGroupResult: " + groupCode);
                                                    for (AutoGetMyGroupsInfo.GroupMembers groupMember : createdGroup.getGroupMembers()) {

                                                    }


                                                }
                                            }
                                            ArrayList<AutoGetMyGroupsInfo.CreatedGroups> joinedGroups = myGroupList.getJoinedGroups();
                                            if (joinedGroups != null && joinedGroups.size() > 0) {
                                                tvListTwo.setText(joinedGroups.size() + "");

                                            }
                                        }
                                    }
                                }
                            });
                    break;
                case R.id.btn_team_up_create_team:
                    TeamOption createTeamOption = new TeamOption().setAccessToken(accessToken)
                            .setChannelId(channelId).setLocation(new LatLng(curLocation.latitude, curLocation.longitude));
                    BDAutoMapFactory.getAutoTeamManager().createGroup(createTeamOption);
                    BDAutoMapFactory.getAutoTeamManager().setOnCreateGroupResultListener(
                            new IAutoTeamManager.OnCreateGroupResultListener() {
                        @Override
                        public void onGetCreateGroupResult(AutoCreateGroupInfo info) {
                            Log.d(TAG, "onGetCreateGroupResult: " + info);

                        }
                    });
                    break;
                case R.id.btn_team_up_del_team:
                    TeamOption delTeamOption = new TeamOption().setAccessToken(accessToken)
                            .setChannelId(channelId).setGroupId(groupId);
                    BDAutoMapFactory.getAutoTeamManager().deleteGroup(delTeamOption);
                    BDAutoMapFactory.getAutoTeamManager().setOnDeleteGroupResultListener(
                            new IAutoTeamManager.OnDeleteGroupResultListener() {
                                @Override
                                public void onDeleteGroupResult(AutoCreateGroupInfo info) {
                                    BDAutoMapFactory.getAutoTeamManager().unInit();
                                    Log.d(TAG, "onDeleteGroupResult: " + info.getError());
                                }
                            });
                    break;

                case R.id.btn_team_up_enter_team:
                    TeamOption enterTeamOption = new TeamOption().setAccessToken(accessToken)
                            .setChannelId(channelId).setGroupCode("295930")
                            .setLocation(new LatLng(curLocation.latitude, curLocation.longitude));
                    BDAutoMapFactory.getAutoTeamManager().groupEnter(enterTeamOption);
                    BDAutoMapFactory.getAutoTeamManager().setOnGroupEnterResultListener(
                            new IAutoTeamManager.OnGroupEnterResultListener() {
                        @Override
                        public void onGetGroupEnterResult(AutoCreateGroupInfo info) {
                            Log.d(TAG, "onGetGroupEnterResult: " + info);
                        }
                    });
                    break;

                case R.id.btn_team_up_quit_team:
                    TeamOption exitTeamOption = new TeamOption().setAccessToken(accessToken)
                            .setChannelId(channelId).setGroupId("5139537");
                    BDAutoMapFactory.getAutoTeamManager().exitGroup(exitTeamOption);
                    BDAutoMapFactory.getAutoTeamManager().setOnExitGroupResultListener(
                            new IAutoTeamManager.OnExitGroupResultListener() {
                        @Override
                        public void onExitGroupResult(AutoCreateGroupInfo info) {
                            Log.d(TAG, "onExitGroupResult: " + info);
                        }
                    });
                    break;

                case R.id.btn_team_up_kick_team:
                    TeamOption kickTeamOption = new TeamOption().setAccessToken(accessToken)
                            .setChannelId(channelId).setGroupId(groupId)
                            .setUserId("f1968a627ac5f82c02bd703917a6ce97");
                    BDAutoMapFactory.getAutoTeamManager().kickMember(kickTeamOption);
                    BDAutoMapFactory.getAutoTeamManager().setOnKickMemberResultListener(
                            new IAutoTeamManager.OnKickMemberResultListener() {
                        @Override
                        public void onKickMemberResult(AutoCreateGroupInfo info) {
                            Log.d(TAG, "onKickMemberResult: " + info);
                        }
                    });
                    break;
                case R.id.btn_team_up_get_group_info:
                    TeamOption getTeamOption = new TeamOption().setAccessToken(accessToken)
                            .setChannelId(channelId).setType("0")
                            .setGroupId(groupId);
                    BDAutoMapFactory.getAutoTeamManager().getGroupInfo(getTeamOption);
                    BDAutoMapFactory.getAutoTeamManager().setOnGetGroupInfoResultListener(
                            new IAutoTeamManager.OnGetGroupInfoResultListener() {
                        @Override
                        public void onGetGetGroupInfoResult(AutoCreateGroupInfo info) {
                            Log.d(TAG, "onGetGetGroupInfoResult: " + info);
                        }
                    });
                    break;
                case R.id.btn_team_up_update_group_info:
                    TeamOption updateTeamOption = new TeamOption().setAccessToken(accessToken)
                            .setChannelId(channelId).setType("2").setContent("百度大厦,116.307611,40.056957,111")
                            .setGroupId(groupId);
                    BDAutoMapFactory.getAutoTeamManager().updateGroupInfo(updateTeamOption);
                    BDAutoMapFactory.getAutoTeamManager().setOnUpdateGroupInfoResultListener(
                            new IAutoTeamManager.OnUpdateGroupInfoResultListener() {
                        @Override
                        public void onUpdateGroupInfoResult(AutoCreateGroupInfo info) {
                            Log.d(TAG, "onGetGetGroupInfoResult: " + info);
                        }
                    });
                    break;
                case R.id.btn_team_up_update_nickname:
                    TeamOption updateNickNameTeamOption = new TeamOption().setAccessToken(accessToken)
                            .setChannelId(channelId).setNickname("成员1")
                            .setGroupId(groupId);
                    BDAutoMapFactory.getAutoTeamManager().setGroupNickName(updateNickNameTeamOption);
                    BDAutoMapFactory.getAutoTeamManager().setOnSetGroupNickNameResultListener(
                            new IAutoTeamManager.OnSetGroupNickNameResultListener() {
                        @Override
                        public void onSetGroupNickNameResult(AutoCreateGroupInfo info) {
                            Log.d(TAG, "onSetGroupNickNameResult: " + info);
                        }
                    });
                    break;
                case R.id.btn_team_up_loc:
                    TeamOption upLocTeamOption = new TeamOption().setAccessToken(accessToken)
                            .setChannelId(channelId).setGroupId(groupId)
                            .setLocation(new LatLng(curLocation.latitude, curLocation.longitude));
                    BDAutoMapFactory.getAutoTeamManager().upLocation(upLocTeamOption);
                    BDAutoMapFactory.getAutoTeamManager().setOnUpLocationResultListener(
                            new IAutoTeamManager.OnUpLocationResultListener() {
                        @Override
                        public void onUpLocationResult(AutoUpLocationInfo info) {
                            Log.d(TAG, "onSetGroupNickNameResult: " + info);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}