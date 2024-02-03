<template>
  <div>

    <!-- 结果列表 -->
    <el-card>
      <el-table :data="invitationList" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="50">
          <template slot-scope="scope">
            <!-- (pageNo - 1) * pageSize + index + 1 -->
            {{ (searchModel.pageNo - 1) * searchModel.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="invitationId" label="邀请ID" width="150"></el-table-column>
        <el-table-column prop="conferenceId" label="会议ID" width="150"></el-table-column>
        <el-table-column label="会议简称" prop="shortName"></el-table-column>
        <el-table-column label="会议全称" prop="fullName"></el-table-column>
        <el-table-column prop="eventDate" label="会议时间" :formatter="formatEventDate"></el-table-column>
        <el-table-column prop="chairName" label="会议主席"></el-table-column>
        <el-table-column label="操作" width="240">
          <template slot-scope="scope">
            <el-button @click="showDialog(scope.row.invitationId, scope.row.conferenceId)" type="primary"
              icon="el-icon-circle-check">接受</el-button>
            <el-button @click="rejectInvitation(scope.row.invitationId)" type="danger"
              icon="el-icon-circle-close">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分页组件 -->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
      :current-page="searchModel.pageNo" :page-sizes="[5, 10, 20, 50]" :page-size="searchModel.pageSize"
      layout="total, sizes, prev, pager, next, jumper" :total="total">
    </el-pagination>

    <!-- 弹窗 -->
    <el-dialog :visible.sync="dialogVisible" title="选择负责的主题">
      <!-- 显示所有的 topics -->
      <el-row>
        <el-col :span="20">
          <el-checkbox-group v-model="selectedTopics" :min="1">
            <el-checkbox v-for="topic in this.allTopics" :key="topic.id" :label="topic.id" class="el-checkbox--medium">
              {{ topic.topicName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-col>
      </el-row>

      <!-- 添加一些垂直间距 -->
      <el-row style="margin-top: 30px;">
        <el-col :span="24">
          <!-- 确认按钮 -->
          <el-button @click="confirmSelection" type="primary">确认选择</el-button>
        </el-col>
      </el-row>
    </el-dialog>

  </div>
</template>

<script>
import userApi from '@/api/userManage'
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
      allTopics: [], // 保存从后端获取的所有 topics
      selectedTopics: [], // 保存用户选择的 topics 的 ID
      formLabelWidth: '130px',
      dialogVisible: false,
      dialogConferenceFormVisible: false,
      title: "",
      total: 0,
      searchModel: {
        pageNo: 1,
        pageSize: 10
      },
      invitationId: 0,
      action: "",
      invitationList: [],
      tempconferenceId: -1,
      tempinvitationId: -1
    }
  },
  methods: {
    async fetchData(id) {
      invitationApi.getConferenceTopics(id).then(response => {
        console.log('response: ' + response.data.id);
        this.allTopics = response.data;
        console.log('topics: ' + this.allTopics.topicName);
      })

    },
    showDialog(invitationId, conferenceid) {
      this.tempinvitationId = invitationId;
      this.tempconferenceId = conferenceid;

      console.log('fetch data: ')
      // 打开弹窗前先获取 topics 数据
      this.fetchData(conferenceid);
      // 显示弹窗
      this.dialogVisible = true;
    },
    confirmSelection() {
      // 在这里处理用户的选择
      console.log('Selected Topics:', this.selectedTopics);
      // 可以将 this.selectedTopics 发送给后端处理
      if (this.selectedTopics.length > 0) {
        // 执行确认操作
        const token = getToken();
        console.log("do something..." + this.tempinvitationId + " /// " + this.selectedTopics)
        invitationApi.insertTopicPCMember(this.tempinvitationId, this.selectedTopics, token).then(response => {
          this.$message({
            message: "接受成功",
            type: 'success'
          });
          this.getinvitationList();
        });
      } else {
        // 提示用户至少选择一个主题
        this.$message.warning('请至少选择一个主题');
      }
      this.tempinvitationId = -1;
      this.tempconferenceId = -1;
      this.selectedTopics = [];
      // 隐藏弹窗
      this.dialogVisible = false;
    },
    acceptInvitation(id) {
      this.action = 'accept'
      invitationApi.replyInvitation(id, this.action).then(response => {
        this.$message({
          message: "接受成功",
          type: 'success'
        });
        this.getinvitationList();
      });
    },
    rejectInvitation(id) {
      this.action = 'reject'
      invitationApi.replyInvitation(id, this.action).then(response => {
        this.$message({
          message: "拒绝成功",
          type: 'success'
        });
        this.getinvitationList();
      });
    },
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize;
      this.getinvitationList();
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo;
      this.getinvitationList();
    },
    formatEventDate(row, column, cellValue) {
      if (cellValue) {
        const date = new Date(cellValue); // 假设 cellValue 是一个有效的日期字符串
        const formattedDate = date.toLocaleDateString(); // 或使用其他格式化选项
        return formattedDate;
      }
      return '';
    },
    getinvitationList() {
      const token = getToken();
      invitationApi.getMyInvitationList(this.searchModel, token).then(response => {
        this.invitationList = response.data.records;
        this.total = response.data.total;
      });
    }
  },
  created() {
    this.getinvitationList();
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
