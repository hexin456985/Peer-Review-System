<template>
  <div>
    <!-- 结果列表 -->
    <el-card>
      <el-table :data="myReviewPaperList" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="50">
          <template slot-scope="scope">
            <!-- (pageNo - 1) * pageSize + index + 1 -->
            {{ (searchModel.pageNo - 1) * searchModel.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="论文标题" width="250"></el-table-column>
        <el-table-column prop="paperAbstract" label="论文摘要" width="750"></el-table-column>
        <el-table-column prop="status" label="论文状态" width="150"></el-table-column>
        <el-table-column label="操作" width="450">
          <template slot-scope="scope">
            <el-button @click="downloadPDF(scope.row.paperId, scope.row.pDFPath)" type="primary" icon="el-icon-check"
              :disabled="scope.row.status !== 'reviewing'">稿件预览/下载</el-button>
            <el-button @click="openEditUI(scope.row.paperId)" type="success" icon="el-icon-edit"
              :disabled="scope.row.status !== 'reviewing'">稿件评分</el-button>
            <el-button @click="openEditUI2(scope.row.paperId)" type="warning" icon="el-icon-edit"
              :disabled="scope.row.status !== 'complete-rebuttal'">评分更新</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分页组件 -->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
      :current-page="searchModel.pageNo" :page-sizes="[5, 10, 20, 50]" :page-size="searchModel.pageSize"
      layout="total, sizes, prev, pager, next, jumper" :total="total">
    </el-pagination>

    <template>
      <!-- 稿件评分对话框 -->
      <el-dialog @close="clearForm" :title="title" :visible.sync="formVisible">
        <el-form :model="reviewForm" ref="reviewFormRef" :rules="rules">
          <el-form-item prop="reviewScore" label="稿件评分" :label-width="formLabelWidth">
            <el-input v-model="reviewForm.reviewScore" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item prop="reviewComment" label="稿件评语" :label-width="formLabelWidth">
            <el-input v-model="reviewForm.reviewComment" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item prop="reviewConfidence" label="Confidence" :label-width="formLabelWidth">
            <el-input v-model="reviewForm.reviewConfidence" autocomplete="off"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="formVisible = false">取 消</el-button>
          <el-button type="primary" @click="saveReview">确 定</el-button>
        </div>
      </el-dialog>
    </template>

    <template>
      <!-- rebuttal评分修改对话框 -->
      <el-dialog @close="clearForm2" :title="rebuttalreviewtitle" :visible.sync="formVisible2">
        <!-- 其他表单项 -->
        <el-form :model="rebuttalReviewForm" ref="rebuttalReviewFormRef" :rules="rules">
          <el-form-item prop="reviewScore" label="稿件评分" :label-width="formLabelWidth">
            <el-input v-model="rebuttalReviewForm.reviewScore" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item prop="reviewComment" label="稿件评语" :label-width="formLabelWidth">
            <el-input v-model="rebuttalReviewForm.reviewComment" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item prop="reviewConfidence" label="稿件信心" :label-width="formLabelWidth">
            <el-input v-model="rebuttalReviewForm.reviewConfidence" autocomplete="off"></el-input>
          </el-form-item>
          <br>
          <!-- 信息显示框 -->
          <el-alert v-if="rebuttalReviewForm.rebuttal" title="作者辩驳信息" type="info" :closable="false">
            {{ rebuttalReviewForm.rebuttal }}
          </el-alert>
          <br>
        </el-form>

        <!-- 底部按钮 -->
        <div slot="footer" class="dialog-footer">
          <el-button @click="formVisible2 = false">取 消</el-button>
          <el-button type="primary" @click="saveRebuttalReview">提 交</el-button>
        </div>
      </el-dialog>
    </template>

  </div>
</template>

<script>
import paperApi from '@/api/paperManage'
import conferenceApi from '@/api/conferenceManage'
import { getToken } from '@/utils/auth' // get token from cookie
export default {
  data() {
    var checkPDF = (rule, value, callback) => {
      callback();
    };
    return {
      tempPdfUrl: '',
      searchName: '',
      conferenceIds: [],
      selectedTopics: [],
      allTopics: [],
      paperConferenceId: 0,
      reviewForm: {
        reviewScore: 0,
        reviewComment: '',
        reviewConfidence: '',
        paperId: ''
      },
      rebuttalReviewForm: {
        reviewId: '',
        reviewScore: 0,
        reviewComment: '',
        reviewConfidence: '',
        paperId: '',
        rebuttal: ''
      },
      formLabelWidth: '130px',
      formVisible: false,
      formVisible2: false,
      conferenceId: null,
      reviewRules: {
        selectedReviewers: [
          { required: true, type: 'array', min: 1, message: '请选择至少一个审稿人', trigger: 'blur' },
        ],
      },
      pagination: {
        currentPage: 1,
        pageSize: 10,
      },
      formtitle: "更新投稿",
      rebuttalreviewtitle: "重新评分",
      total: 0,
      userTotal: 0,
      searchModel: {
        pageNo: 1,
        pageSize: 10
      },
      myReviewPaperList: [],
      curPaperId: -1,
      curRebuttalPaperId: -1,
      tempData: {},
      rules: {
        reviewScore: [
          { required: true, message: '请输入稿件评分', trigger: 'blur' },
          { validator: this.validateReviewScore, trigger: 'blur' },
        ],
        reviewComment: [
          { required: true, message: '请输入稿件评语', trigger: 'blur' },
          { max: 800, message: '评语不能超过800个字符', trigger: 'blur' },
        ],
        reviewConfidence: [
          { required: true, message: '请输入 Confidence', trigger: 'blur' },
        ],
      },
    }
  },
  methods: {
    clearForm() {
      this.reviewForm = {
        Title: '',
        Abstract: '',
        authors: [{ authorName: '', authorInstitution: '', authorRegion: '', authorEmail: '' }],
        paperId: ''
      };
    },
    clearForm2() {
      this.rebuttalReviewForm= {
        reviewId: '',
        reviewScore: 0,
        reviewComment: '',
        reviewConfidence: '',
        paperId: '',
        rebuttal: ''
      }
    },
    validateReviewScore(rule, value, callback) {
      const score = parseFloat(value);
      if (isNaN(score) || score < -2 || score > 2 || score === 0) {
        callback(new Error('评分必须在-2到2之间且不能为0'));
      } else {
        callback();
      }
    },
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize;
      this.getMyReviewPaperList();
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo;
      this.getMyReviewPaperList();
    },
    handleSelectionChange(selection) {
      this.invitationForm.selectedReviewers = selection.map(user => user.uid);
    },
    handleExceed(files, fileList) {
      this.$message.warning('文件数量超过限制');
    },
    getMyReviewPaperList() {
      const token = getToken();
      conferenceApi.getConferenceIdList(token).then(response => {
        this.conferenceIds = response.data.conferenceIds;
        console.log("conferenceIds: " + this.conferenceIds);
        paperApi.getMyPaperReviewList(this.searchModel, this.conferenceIds, token).then(response => {
          console.log("review paper data: " + response.data);
          // this.myReviewPaperList = response.data;
          this.myReviewPaperList = response.data.rows;
          this.total = response.data.total;
        });
      });
    },
    downloadPDF(paperId, localFilePath) {
      paperApi.getPdfDownload(paperId, localFilePath)
        .then(response => {
          // 成功提示
          this.tempPdfUrl = response.data.URL;
          window.open(this.tempPdfUrl, '_blank');
        })
        .catch(error => {
          // 失败提示
          this.$message({
            message: '下载链接创建失败',
            type: 'error'
          });
          console.error('Download error:', error);
        });
    },
    saveReview() {
      const token = getToken();
      this.reviewForm.paperId = this.curPaperId;
      paperApi.submitReview(this.reviewForm, token).then(response => {
        // 成功提示
        this.$message({
          message: response.message,
          type: 'success'
        });
      });
      this.formVisible = false;
      this.clearForm();
      this.getMyReviewPaperList();
    },
    saveRebuttalReview() {
      console.log('id: ' + this.rebuttalReviewForm.reviewId);
      paperApi.updateReview(this.rebuttalReviewForm).then(response => {
        // 成功提示
        this.$message({
          message: response.message,
          type: 'success'
        });
      });
      this.formVisible2 = false;
      this.clearForm2();
      this.getMyReviewPaperList();
    },
    openEditUI(paperId) {
      this.formVisible = true;
      this.curPaperId = paperId;
      console.log('paperId: ' + paperId);
    },
    openEditUI2(paperId) {
      const token = getToken();
      paperApi.getRebuttalReview(paperId, token).then(response => {
        this.rebuttalReviewForm = response.data.review;
      });
      console.log('rebuttal form: ' + this.rebuttalReviewForm);
      this.formVisible2 = true;
      this.curRebuttalPaperId = paperId;
    },
  },
  created() {
    this.getMyReviewPaperList();
  },

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
