import request from '@/utils/request'

export default{
  invitePcMembers(conferenceId, inviteList) {
    return request({
      url: `/invite/${conferenceId}`,
      method: 'post',
      data: inviteList
    });
  },
  getMyInvitationList(searchModel, token) {
    return request({
      url: `/invite/todo`,
      method: 'get',
      params: {
        token,
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize,
      }
    });
  },
  getConferenceTopics(conferenceID) {
    return request({
      url: `/invite/view_topics`,
      method: 'get',
      params: {
        conferenceId: conferenceID
      }
    });
  },
  replyInvitation(inviteId, action) {
    return request({
      url: `/invite/reply/${inviteId}`,
      method: 'post',
      params: {
        action: action
      }
    });
  },
  getstateList(searchModel, token) {
    return request({
      url: `/invite/state`,
      method: 'get',
      params: {
        token,
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize,
      }
    });
  },
  insertTopicPCMember(invitationId, topicIds, token) {
    return request({
      url: `/invite/topicPCMember`,
      method: 'post',
      data: {
        invitationId: invitationId,
        topicIds: topicIds,
        token: token
      }
    });
  }
}
