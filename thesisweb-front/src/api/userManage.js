import request from '@/utils/request'

export default{
  getUserList(searchModel) {
    return request({
      url: '/user/list',
      method: 'get',
      params:{
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize,
        name: searchModel.name,
      }
    });
  },
  addUser(user) {
    return request({
      url: '/user',
      method: 'post',
      data: user
    });
  },
  getUserById(uid) {
    return request({
      url: `/user/${uid}`,
      method: 'get',
    });
  },
  getUserWithoutChair(searchModel, token) {
    return request({
      url: `/user/withoutChair`,
      method: 'get',
      params:{
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize,
        name: searchModel.name,
        token
      }
    });
  },
  updateUser(user) {
    return request({
      url: '/user',
      method: 'put',
      data: user
    });
  },
  saveUser(user) {
    if(user.uid == null || user.uid == undefined) {
      return this.addUser(user);
    }
    else {
      return this.updateUser(user);
    }
  },
  deleteUserById(uid) {
    return request({
      url: `/user/${uid}`,
      method: 'delete',
    });
  }
}

