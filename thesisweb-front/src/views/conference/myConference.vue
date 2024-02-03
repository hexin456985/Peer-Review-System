<template>
  <div>
    <!-- 搜索栏 -->
    <el-card id="search">
      <el-row>
        <el-col :span="20">
          <el-input v-model="searchModel.shortName" placeholder="会议名" clearable></el-input>
          <el-button @click="getMyConferenceList" type="primary" round icon="el-icon-search">查询</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 结果列表 -->
    <el-card>
      <el-table :data="myConferenceList" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="50">
          <template slot-scope="scope">
            <!-- (pageNo - 1) * pageSize + index + 1 -->
            {{ (searchModel.pageNo - 1) * searchModel.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="conferenceId" label="会议ID" width="100"></el-table-column>
        <el-table-column prop="shortName" label="会议名" width="100"></el-table-column>
        <el-table-column prop="fullName" label="会议全称" width="150"></el-table-column>
        <el-table-column prop="eventDate" label="会议日期" width="150"></el-table-column>
        <el-table-column prop="eventLocation" label="会议地点" width="150"></el-table-column>
        <el-table-column prop="submissionDeadline" label="提交截止日期" width="150"></el-table-column>
        <el-table-column prop="reviewresultDate" label="审核结果日期" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="150"></el-table-column>
        <el-table-column label="操作" width="750">
          <template slot-scope="scope">
            <el-button @click="beginSubmission(scope.row.conferenceId)" type="primary" icon="el-icon-circle-check"
              :disabled="scope.row.status !== '通过'">开启投稿</el-button>
            <el-button @click="invitePcMember(scope.row.conferenceId)" type="success" icon="el-icon-user-solid"
              :disabled="scope.row.status !== '通过' && scope.row.status !== '开放投稿'">邀请审稿人</el-button>
            <el-button @click="startReviewView(scope.row.conferenceId)" type="warning" icon="el-icon-user-solid"
              :disabled="scope.row.status !== '开放投稿'">开启审稿</el-button>
            <el-button @click="publishResult(scope.row.conferenceId)" type="info" icon="el-icon-user-solid"
              :disabled="scope.row.status !== '审稿中'">发布评审结果</el-button>
              <el-button @click="publishEmployed(scope.row.conferenceId)" type="danger" icon="el-icon-user-solid"
              :disabled="scope.row.status !== '完成审稿'">发布最终录用结果</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分页组件 -->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
      :current-page="searchModel.pageNo" :page-sizes="[5, 10, 20, 50]" :page-size="searchModel.pageSize"
      layout="total, sizes, prev, pager, next, jumper" :total="total">
    </el-pagination>


    <!-- 审稿人邀请对话框 -->
    <el-dialog @close="clearForm" :title="title" :visible.sync="dialogInvitationFormVisible">
      <el-row>
        <el-col :span="8">
          <el-input v-model="userSearchModel.name" placeholder="审稿人姓名" clearable style="width: 190px;"></el-input>
        </el-col>
        <el-col :span="4">
          <el-button @click="getUserList" type="primary" round icon="el-icon-search"></el-button>
        </el-col>
      </el-row>

      <el-table :data="userList" style="width: 100%" row-key="uid" @selection-change="handleSelectionChange"
        ref="userTable">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="name" label="姓名" width="180"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
        <el-table-column prop="region" label="地区" width="180"></el-table-column>
        <el-table-column prop="institution" label="机构" width="180"></el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
        :current-page="userSearchModel.pageNo" :page-sizes="[5, 10, 15, 20]" :page-size="userSearchModel.pageSize"
        layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogInvitationFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="sendInvitations">邀 请</el-button>
      </div>
    </el-dialog>

    <!-- 审稿分配策略对话框 -->
    <el-dialog :width="width" @close="clearForm" :title="title" :visible.sync="dialogAllocationFormVisible">
      <el-row>
        <el-col :span="24">
          <el-form :model="allocationForm" ref="allocationFormRef" label-width="100px">
            <el-form-item label="审稿分配策略">
              <el-radio-group v-model="allocationForm.allocationStrategy">
                <el-radio label="1">基于topic相关度的分配策略</el-radio>
                <el-radio label="2">基于审稿平均负担的分配策略</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

      <!-- 描述文字 -->
      <el-row v-if="allocationForm.allocationStrategy === '1'" style="margin-left: 15px; margin-right: 15px">
        <el-col :span="24">
          <p>
            每个topic下的稿件会分配给负责该topic的审稿人PC member，
            如果某个topic下的PC member小于3，则该topic下的所有稿件在所有PC member之间随机分配。
          </p>
        </el-col>
      </el-row>

      <el-row v-if="allocationForm.allocationStrategy === '2'" style="margin-left: 15px;">
        <el-col :span="22">
          <p>
            该会议下的所有稿件在该会议下的所有PC member之间随机分配，不考虑topic。
          </p>
        </el-col>
      </el-row>
      <br>
      <br>

      <div style="flex: 1; text-align: right;">
        <el-button @click="dialogAllocationFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="startReview">邀 请</el-button>
      </div>

    </el-dialog>


  </div>
</template>

<script>
import conferenceApi from '@/api/conferenceManage'
import invitationApi from '@/api/invitationManage'
import userApi from '@/api/userManage'
import { getToken } from '@/utils/auth' // get token from cookie
export default {
  data() {
    var checkPDF = (rule, value, callback) => {
      callback();
    };
    return {
      searchName: '',
      conferenceForm: {
      },
      allocationForm: {
        allocationStrategy: 0, // 默认选择第一种
      },
      curConferenceId: -1,
      formLabelWidth: '130px',
      width: '40%',
      dialogFormVisible: false,
      dialogAllocationFormVisible: false,
      dialogInvitationFormVisible: false,
      invitationForm: {
        selectedReviewers: [], // 存储所选审稿人的 UID
      },
      conferenceId: null,
      invitationRules: {
        selectedReviewers: [
          { required: true, type: 'array', min: 1, message: '请选择至少一个审稿人', trigger: 'blur' },
        ],
      },
      pagination: {
        currentPage: 1,
        pageSize: 10,
      },
      title: "",
      total: 0,
      userTotal: 0,
      searchModel: {
        pageNo: 1,
        pageSize: 10
      },
      userSearchModel: {
        pageNo: 1,
        pageSize: 10
      },
      userList: [],
      myConferenceList: [],
    }
  },
  methods: {
    clearForm() {
      this.conferenceForm = {
        roleUidList: []
      };
      // this.$refs.conferenceFormRef.clearValidate();
    },
    beginSubmission(id) {
      conferenceApi.startSubmit(id).then(response => {
        this.$message({
          type: 'success',
          message: response.message,
        });
        this.getMyConferenceList();
      });
    },
    invitePcMember(conferenceId) {
      this.title = '审稿人邀请';
      this.getUserList();
      this.dialogInvitationFormVisible = true;
      this.conferenceId = conferenceId;
    },
    startReviewView(id) {
      this.dialogAllocationFormVisible = true;
      this.curConferenceId = id;
    },
    publishResult(conferenceId) {
      const token = getToken();
      conferenceApi.publishResult(conferenceId, token).then(response => {
        this.$message({
          type: 'success',
          message: response.message,
        });
        this.getMyConferenceList();
      });
    },
    publishEmployed(conferenceId) {
      const token = getToken();
      conferenceApi.publishEmployed(conferenceId, token).then(response => {
        this.$message({
          type: 'success',
          message: response.message,
        });
        this.getMyConferenceList();
      });
    },
    startReview() {
      const allocationStrategy = this.allocationForm.allocationStrategy;
      console.log("cur console: " + this.curConferenceId + "/ cur stragety: " + allocationStrategy);
      // 调用后端接口
      conferenceApi.startReview(this.curConferenceId, allocationStrategy).then(response => {
        this.$message({
          type: 'success',
          message: response.message,
        });
        this.getMyConferenceList();
        // 关闭审稿分配策略视窗
        this.dialogAllocationFormVisible = false;
        this.allocationForm.allocationStrategy = 1;
        this.curConferenceId = -1;
      }).catch(error => {
        this.$message.error(error.message); // 处理错误信息
      });
    },
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize;
      this.getMyConferenceList();
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo;
      this.getMyConferenceList();
    },
    handleSelectionChange(selection) {
      this.invitationForm.selectedReviewers = selection.map(user => user.uid);
    },
    getMyConferenceList() {
      const token = getToken();
      conferenceApi.getMyConferenceList(this.searchModel, token).then(response => {
        this.myConferenceList = response.data.rows;
        this.total = response.data.total;
      });
    },
    getUserList() {
      const token = getToken();
      userApi.getUserWithoutChair(this.userSearchModel, token).then(response => {
        this.userList = response.data.rows;
        this.userTotal = response.data.total;
      });
    },
    sendInvitations() {
      invitationApi.invitePcMembers(this.conferenceId, this.invitationForm.selectedReviewers).then(response => {
        this.$message({
          type: 'success',
          message: response.message,
        });
        this.dialogInvitationFormVisible = false;
      });
    },
  },
  created() {
    this.getMyConferenceList();
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
