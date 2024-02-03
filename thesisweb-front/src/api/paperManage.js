import request from '@/utils/request'

export default{
  uploadPaper(conferenceID, formdata) {
    return request({
      url: `/papers/submit-paper/${conferenceID}`,
      method: 'post',
      data: formdata,
    });
  },
  updatePaper(paperId, formdata) {
    return request({
      url: `/papers/update-paper/${paperId}`,
      method: 'post',
      data: formdata,
    });
  },
  getMyPaperList(searchModel, token) {
    return request({
      url: '/papers/list',
      method: 'get',
      params: {
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize,
        token
      }
    });
  },
  getMyPaperReviewList(searchModel, conferenceIds, token) {
    return request({
      url: '/papers/assignment-papers/show',
      method: 'get',
      params: {
        conferenceIds: conferenceIds.join(','),
        token,
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize
      }
    });
  },
  getPdfDownload(paperId, localFilePath) {
    return request({
      url: '/papers/assignment-papers/download',
      method: 'get',
      params: {
        paperId: paperId,
        localFilePath: localFilePath,
      }
    });
  },
  submitReview(reviewForm, token) {
    return request({
      url: `/review/submit`,
      method: 'post',
      data: reviewForm,
      params: { token: token }
    });
  },
  authorGetReviews(searchModel, paperId) {
    return request({
      url:`/review/author`,
      method: 'get',
      params: {
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize,
        paperId: paperId
      }
    });
  },
  submitRebuttal(paperId, rebuttal) {
    return request({
      url:`/review/rebuttal`,
      method: 'put',
      params: {
        paperId: paperId,
        rebuttal: rebuttal
      }
    });
  },
  getRebuttalReview(paperId, token) {
    return request({
      url:`/review/rebuttal-reviewer`,
      method: 'get',
      params: {
        paperId: paperId,
        token: token
      }
    });
  },
  updateReview(rebuttalReviewForm) {
    return request({
      url:`/review/update`,
      method: 'put',
      data: rebuttalReviewForm
    });
  }
}
