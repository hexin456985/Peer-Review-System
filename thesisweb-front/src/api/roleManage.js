import request from '@/utils/request'

export default{
  getRoleList(searchModel) {
    return request({
      url: '/role/list',
      method: 'get',
      params:{
        name: searchModel.name,
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize
      }
    });
  },
  getAllRoleList() {
    return request({
      url: '/role/all',
      method: 'get'
    });
  },
  addRole(role) {
    return request({
      url: '/role',
      method: 'post',
      data: role
    });
  },
  getRoleById(id) {
    return request({
      url: `/role/${id}`,
      method: 'get'
    });
  },
  updateRole(role) {
    return request({
      url: '/role',
      method: 'put',
      data: role
    });
  },
  saveRole(role) {
    if(role.id == null || role.id == undefined) {
      return this.addRole(role);
    }
    else {
      return this.updateRole(role);
    }
  },
  deleteRoleById(id) {
    return request({
      url: `/role/${id}`,
      method: 'delete',
    });
  }
}

