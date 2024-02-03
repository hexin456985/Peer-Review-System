import request from '@/utils/request'

export default{
  getConferenceList(searchModel) {
    return request({
      url: '/conferences/list-passed',
      method: 'get',
      params:{
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize,
        shortName: searchModel.shortName,
      }
    });
  },
  getPendingConferenceList(searchModel) {
    return request({
      url: '/conferences/list-pending',
      method: 'get',
      params:{
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize,
        shortName: searchModel.shortName,
      }
    });
  },
  getMyConferenceList(searchModel, token) {
    return request({
      url: '/conferences/chair',
      method: 'get',
      params: {
        token,
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize,
        shortName: searchModel.shortName
      }
    });
  },
  applyConf(conferences, topics, token) {
    return request({
      url: '/conferences/apply',
      method: 'post',
      data: conferences,
      params: { topics, token }
    });
  },
  getApply() {
    return request({
      url: `/conferences/apply`,
      method: 'get',
    });
  },
  getConferenceById(conferenceID) {
    return request({
      url: `/conferences/${conferenceID}`,
      method: 'get',
    });
  },
  acceptApply(conferenceID) {
    return request({
      url: `/conferences/${conferenceID}`,
      method: 'post',
      params:{
        action: 'accept'
      }
    });
  },
  rejectApply(conferenceID) {
    return request({
      url: `/conferences/${conferenceID}`,
      method: 'post',
      params:{
        action: 'reject'
      }
    });
  },
  startSubmit(conferenceID) {
    return request({
      url: `/conferences/start-submission/${conferenceID}`,
      method: 'post',
    });
  },
  startReview(conferenceID, strategy) {
    return request({
      url: `/conferences/start-review/${conferenceID}`,
      method: 'post',
      params:{
        strategy: strategy
      }
    });
  },
  getConferenceIdList(token) {
    return request({
      url: `/pcMembers/conferences`,
      method: 'get',
      params:{
        token
      }
    });
  },
  publishResult(conferenceId, token) {
    return request({
      url: `/conferences/publish-review/${conferenceId}`,
      method: 'post',
      params:{
        token: token
      }
    });
  },
  publishEmployed(conferenceId, token) {
    return request({
      url: `/conferences/publish-employed/${conferenceId}`,
      method: 'post',
      params:{
        token: token
      }
    });
  },
}
