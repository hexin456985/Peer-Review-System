<template>
  <div>
    <!-- 结果列表 -->
    <el-card>
      <el-table :data="stateList" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="50">
          <template slot-scope="scope">
            <!-- (pageNo - 1) * pageSize + index + 1 -->
            {{ (searchModel.pageNo - 1) * searchModel.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="invitationId" label="邀请ID" width="150"></el-table-column>
        <el-table-column prop="conferenceId" label="会议ID" width="150"></el-table-column>
        <el-table-column prop="shortName" label="会议简称"></el-table-column>
        <el-table-column prop="fullName" label="会议全称"></el-table-column>
        <el-table-column prop="invitedName" label="邀请的审稿人姓名"></el-table-column>
        <el-table-column prop="status" label="邀请状态" ></el-table-column>

      </el-table>
    </el-card>

    <!-- 分页组件 -->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
      :current-page="searchModel.pageNo" :page-sizes="[5, 10, 20, 50]" :page-size="searchModel.pageSize"
      layout="total, sizes, prev, pager, next, jumper" :total="total">
    </el-pagination>
  </div>
</template>

<script>
import invitationApi from '@/api/invitationManage'
import { getToken } from '@/utils/auth'
export default {
  data() {
    var checkPDF = (rule, value, callback) => {

      callback();
    };
    return {
      roleList: [],
      conferenceForm: {
        roleUidList: []
      },
      formLabelWidth: '130px',
      dialogFormVisible: false,
      dialogConferenceFormVisible: false,
      title: "",
      total: 0,
      searchModel: {
        pageNo: 1,
        pageSize: 10
      },
      stateList: []
    }
  },
  methods: {
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize;
      this.getstateList();
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo;
      this.getstateList();
    },
    getstateList() {
      const token = getToken();
      invitationApi.getstateList(this.searchModel, token).then(response => {
        this.stateList = response.data.records;
        this.total = response.data.total;
      });
    }
  },
  created() {
    this.getstateList();
  }
}
</script>

<style>
#search .el-input {
  width: 240px;
  margin-right: 10px;
}

.el-dialog .el-input {
  width: 100%
}

.upload-section {
  margin-left: 130px;
}
</style>
