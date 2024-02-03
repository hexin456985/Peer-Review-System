<template>
  <div>
    <!-- 搜索栏 -->
    <el-card id="search">
      <el-row>
        <el-col :span="20">
          <el-input v-model="searchModel.shortName" placeholder="会议名" clearable></el-input>
          <el-button @click="getPendingConferenceList" type="primary" round icon="el-icon-search">查询</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 结果列表 -->
    <el-card>
      <el-table :data="pendingConferenceList" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="50">
          <template slot-scope="scope">
            <!-- (pageNo - 1) * pageSize + index + 1 -->
            {{ (searchModel.pageNo - 1) * searchModel.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="conferenceId" label="会议ID" width="100"></el-table-column>
        <el-table-column prop="shortName" label="会议名"></el-table-column>
        <el-table-column prop="fullName" label="会议全称"></el-table-column>
        <el-table-column prop="chairId" label="申请人ID"></el-table-column>
        <el-table-column prop="eventDate" label="举办时间" :formatter="formatEventDate"></el-table-column>
        <el-table-column prop="eventLocation" label="会议地点"></el-table-column>
        <el-table-column prop="submissionDeadline" label="提交截止日期" :formatter="formatEventDate"></el-table-column>
        <el-table-column prop="reviewresultDate" label="审核结果日期" :formatter="formatEventDate"></el-table-column>
        <el-table-column label="操作" width="240">
          <template slot-scope="scope">
            <el-button @click="acceptConference(scope.row.conferenceId)" type="success" icon="el-icon-circle-check">通过</el-button>
            <el-button @click="rejectConference(scope.row.conferenceId)" type="danger" icon="el-icon-circle-close">拒绝</el-button>
          </template>
        </el-table-column>
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
import conferenceApi from '@/api/conferenceManage'
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
      pendingConferenceList: [],
    }
  },
  methods: {
    acceptConference(id) {
      conferenceApi.acceptApply(id).then(response => {
        // 成功提示
        this.$message({
          message: response.message,
          type: 'success'
        });
        // 关闭对话框
        this.dialogFormVisible = false;
        // 再刷新表格
        this.getPendingConferenceList();
      });
    },
    rejectConference(id) {
      conferenceApi.rejectApply(id).then(response => {
        // 成功提示
        this.$message({
          message: response.message,
          type: 'success'
        });
        // 关闭对话框
        this.dialogFormVisible = false;
        // 再刷新表格
        this.getPendingConferenceList();
      });
    },
    formatEventDate(row, column, cellValue) {
      if (cellValue) {
        const date = new Date(cellValue); // 假设 cellValue 是一个有效的日期字符串
        const formattedDate = date.toLocaleDateString(); // 或使用其他格式化选项
        return formattedDate;
      }
      return '';
    },
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize;
      this.getPendingConferenceList();
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo;
      this.getPendingConferenceList();
    },
    getPendingConferenceList() {
      conferenceApi.getPendingConferenceList(this.searchModel).then(response => {
        this.pendingConferenceList = response.data.rows;
        this.total = response.data.total;
      });
    }
  },
  created() {
    this.getPendingConferenceList();
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
