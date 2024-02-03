<template>
  <div>
    <!-- 结果列表 -->
    <el-card>
      <el-table :data="myPaperList" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="50">
          <template slot-scope="scope">
            <!-- (pageNo - 1) * pageSize + index + 1 -->
            {{ (searchModel.pageNo - 1) * searchModel.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="论文标题" width="250"></el-table-column>
        <el-table-column prop="paperAbstract" label="论文摘要" width="1000"></el-table-column>
        <el-table-column prop="status" label="论文状态" width="150"></el-table-column>
        <el-table-column label="操作" width="600">
          <template slot-scope="scope">
            <el-button @click="openEditUI(scope.row.paperId, scope.row.conferenceId)" type="primary"
              icon="el-icon-circle-check" :disabled="scope.row.status !== 'NotReviewed'">更新投稿</el-button>
            <el-button @click="openEditUI2(scope.row.paperId, scope.row.conferenceId)" type="success"
              icon="el-icon-circle-check" :disabled="scope.row.status !== 'review-complete'">查看审核结果</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分页组件 -->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
      :current-page="searchModel.pageNo" :page-sizes="[5, 10, 20, 50]" :page-size="searchModel.pageSize"
      layout="total, sizes, prev, pager, next, jumper" :total="total">
    </el-pagination>


    <!-- 更新审稿对话框 -->
    <el-dialog @close="clearForm" :title="formtitle" :visible.sync="formVisible">
      <el-form :model="paperForm" ref="paperFormRef">
        <div class="form-content">
          <div class="form-fields">
            <el-form-item prop="Title" label="论文标题" :label-width="formLabelWidth">
              <el-input v-model="paperForm.Title" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item prop="Abstract" label="论文摘要" :label-width="formLabelWidth">
              <el-input type="textarea" v-model="paperForm.Abstract"></el-input>
            </el-form-item>
            <!-- 将多选框代码嵌入到这里 -->
            <el-row>
              <el-col>
                <el-checkbox-group v-model="selectedTopics" :min="1">
                  <el-checkbox v-for="topic in this.allTopics" :key="topic.id" :label="topic.id"
                    class="el-checkbox--medium">
                    {{ topic.topicName }}
                  </el-checkbox>
                </el-checkbox-group>
              </el-col>
            </el-row>
          </div>

          <!-- 作者信息表单区域 -->
          <div v-for="(author, index) in paperForm.authors" :key="index">
            <el-form-item :label="'作者 ' + (index + 1)" :label-width="formLabelWidth">
              <el-input v-model="author.authorName" placeholder="姓名"></el-input>
            </el-form-item>
            <el-form-item :label="''" :label-width="formLabelWidth">
              <el-input v-model="author.authorInstitution" placeholder="单位"></el-input>
            </el-form-item>
            <el-form-item :label="''" :label-width="formLabelWidth">
              <el-input v-model="author.authorRegion" placeholder="国家/地区"></el-input>
            </el-form-item>
            <el-form-item :label="''" :label-width="formLabelWidth">
              <el-input v-model="author.authorEmail" placeholder="邮箱"></el-input>
            </el-form-item>

            <!-- 提供删除作者按钮 -->
            <el-button type="danger" @click="removeAuthor(index)" class="remove-button">删除作者</el-button>
          </div>

          <!-- 提供增加作者按钮 -->
          <el-button type="primary" @click="addAuthor" class="add-button">增加作者</el-button>


          <div class="upload-section">
            <el-upload class="upload" action="none" :auto-upload="false" :on-change="checkPDF"
              :before-upload="beforeUpload" :on-exceed="handleExceed" :show-file-list="true"
              enctype="multipart/form-data">
              <i class="el-icon-upload"></i>
              <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
              <div class="el-upload__tip" slot="tip">只能上传pdf文件，且不超过50mb</div>
            </el-upload>
          </div>
          <br>
          <div slot="footer" class="dialog-footer" style="display: flex; justify-content: flex-end;">
            <el-button @click="formVisible = false" class="el-button--plain">取 消</el-button>
            <el-button type="primary" @click="updatePaper">确 定</el-button>
          </div>
        </div>
      </el-form>
    </el-dialog>

    <!-- 查看评分对话框 -->
    <el-dialog @close="clearForm" :title="formtitle2" :visible.sync="formVisible2">
      <el-table :data="reviewForm" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="50">
          <template slot-scope="scope">
            <!-- (pageNo - 1) * pageSize + index + 1 -->
            {{ (searchModel2.pageNo - 1) * searchModel2.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="reviewScore" label="稿件评分" width="150"></el-table-column>
        <el-table-column prop="reviewComment" label="稿件评语" width="500"></el-table-column>
        <el-table-column prop="reviewConfidence" label="评价信心" width="200"></el-table-column>
      </el-table>
      <br>
      <!-- 文字输入框 -->
      <el-row>
        <el-col :span="24">
          <el-form :model="rebuttal" label-position="left" label-width="100px">
            <el-form-item label="审稿结果辩驳">
              <!-- 说明文字 -->
              <span>有任意稿件评分为-1,-2者,有一次辩驳机会,审稿人将基于辩驳内容重新评分：</span>
              <!-- 文字输入框 -->
              <el-input v-model="rebuttal.inputText" placeholder=""></el-input>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

      <div slot="footer" class="dialog-footer">
        <el-button type="info" @click="formVisible2 = false">关 闭</el-button>
        <el-button type="primary" @click="submitRebuttal">提 交</el-button>
      </div>
    </el-dialog>
    <!--
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
      :current-page="searchModel2.pageNo" :page-sizes="[5, 10, 20, 50]" :page-size="searchModel2.pageSize"
      layout="total, sizes, prev, pager, next, jumper" :total="total">
    </el-pagination> -->

  </div>
</template>

<script>
import invitationApi from '@/api/invitationManage'
import paperApi from '@/api/paperManage'
import { getToken } from '@/utils/auth' // get token from cookie
export default {
  data() {
    var checkPDF = (rule, value, callback) => {
      callback();
    };
    return {
      rebuttal: {
        inputText: ''
      },
      uploadFile: null,
      uploadUrl: null,
      formData: new FormData(),
      searchName: '',
      paperForm: {
      },
      selectedTopics: [],
      allTopics: [],
      paperConferenceId: 0,
      paperForm: {
        Title: '',
        Abstract: '',
        authors: [{ authorName: '', authorInstitution: '', authorRegion: '', authorEmail: '' }] // 初始至少有一位作者
      },
      reviewForm: {

      },
      formLabelWidth: '130px',
      formVisible: false,
      formVisible2: false,
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
      formtitle: "更新投稿",
      formtitle2: "评分结果",
      total: 0,
      userTotal: 0,
      searchModel: {
        pageNo: 1,
        pageSize: 10
      },
      searchModel2: {
        pageNo: 1,
        pageSize: 10
      },
      myPaperList: [],
      curPaperId: -1,
    }
  },
  methods: {
    async fetchData(conferenceId) {
      console.log('fetching...')
      invitationApi.getConferenceTopics(conferenceId).then(response => {
        console.log('response: ', response.data);
        this.allTopics = response.data;
        console.log('allTopics: ', this.allTopics);
        resolve(); // 解决 Promise
      }).catch(error => {
        reject(error); // 拒绝 Promise
      });
    },
    clearForm() {
      this.paperForm = {
        Title: '',
        Abstract: '',
        authors: [{ authorName: '', authorInstitution: '', authorRegion: '', authorEmail: '' }]
      };
      this.selectedTopics = [];
      this.uploadFile = null;
      this.uploadUrl = null;
      this.$set(this, 'uploadFile', null);
      this.formData = new FormData(); // 重置 FormData
    },
    addAuthor() {
      this.paperForm.authors.push({ authorName: '', authorInstitution: '', authorRegion: '', authorEmail: '' });
    },
    removeAuthor(index) {
      this.paperForm.authors.splice(index, 1);
    },
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize;
      this.getMyPaperList();
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo;
      this.getMyPaperList();
    },
    handleSizeChange2(pageSize) {
      this.searchModel2.pageSize = pageSize;
      this.openEditUI2();
    },
    handleCurrentChange2(pageNo) {
      this.searchModel2.pageNo = pageNo;
      this.openEditUI2();
    },
    handleSelectionChange(selection) {
      this.invitationForm.selectedReviewers = selection.map(user => user.uid);
    },
    handleExceed(files, fileList) {
      this.$message.warning('文件数量超过限制');
    },
    getMyPaperList() {
      const token = getToken();
      paperApi.getMyPaperList(this.searchModel, token).then(response => {
        this.myPaperList = response.data.rows;
        this.total = response.data.total;
      });
    },
    checkPDF(file, fileList) {
      console.log('file: ' + file);
      this.uploadFile = file;
      this.uploadUrl = URL.createObjectURL(this.uploadFile.raw);
      ;
    },
    beforeUpload(file) {
      // 在文件上传之前执行的操作
      if (file.type !== 'application/pdf') {
        this.$message.error('只能上传 PDF 文件');
        return false; // 阻止文件上传
      }

      if (file.size > 50 * 1024 * 1024) {
        this.$message.error('文件大小不能超过 50MB');
        return false; // 阻止文件上传
      }
      // 清空之前的文件
      this.$set(this, 'uploadFile', file);
      this.uploadFile = file;
      console.log(file);
      // 文件合法，可以上传
      return true;
    },
    openEditUI(paperId, conferenceId) {
      console.log('conferenceId：' + conferenceId);
      this.fetchData(conferenceId);
      this.formVisible = true;
      this.curPaperId = paperId;
      console.log('paperId: ' + paperId);
    },
    openEditUI2(paperId, conferenceId) {
      this.formVisible2 = true;
      this.curPaperId = paperId;
      console.log('paperId: ' + paperId);
      paperApi.authorGetReviews(this.searchModel2, paperId).then(response => {
        this.reviewForm = response.data.rows;
        this.total2 = response.data.total;
      });
    },
    updatePaper() {
      const token = getToken();
      const authorJ = JSON.stringify(this.paperForm.authors);
      console.log('authorj: ' + authorJ);

      this.formData.append('papertitle', this.paperForm.Title);
      this.formData.append('paperabstract', this.paperForm.Abstract);

      // Check if uploadFile is not null or undefined before appending it
      if (this.uploadFile) {
        this.formData.append('file', this.uploadFile.raw);
      }
      else {
        console.log("empty file");
        this.formData.set('file', '');
      }

      this.formData.append('author', authorJ);
      this.formData.append('topic', this.selectedTopics);
      this.formData.append('token', token);

      console.log("start..." + this.formData.file);

      // 提交请求给后台
      paperApi.updatePaper(this.curPaperId, this.formData).then(response => {
        // 成功提示rr
        this.$message({
          message: response.message,
          type: 'success'
        });
        this.formVisible = false;
        this.formVisible2 = false;
        this.uploadFile = null;
      });
      this.curPaperId = -1;
    },
    submitRebuttal() {
      paperApi.submitRebuttal(this.curPaperId, this.rebuttal.inputText).then(response => {
        this.$message({
          message: response.message,
          type: 'success'
        });
        this.formVisible2 = false;
        this.rebuttal = {
          inputText: ''
        };
      });
    }
  },
  created() {
    this.getMyPaperList();
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

.rebuttal-row .el-form-item {
  text-align: right;
}

.rebuttal-label {
  display: inline-block;
  text-align: left;
}
</style>
