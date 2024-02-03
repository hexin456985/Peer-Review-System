<template>
  <div>
    <!-- 搜索栏 -->
    <el-card id="search">
      <el-row>
        <el-col :span="20">
          <el-input v-model="searchModel.shortName" placeholder="会议名" clearable></el-input>
          <el-button @click="getConferenceList" type="primary" round icon="el-icon-search">查询</el-button>
        </el-col>
        <el-col :span="4" align="right">
          <el-button @click="openConferenceEditUI(null)" type="primary" icon="el-icon-plus" circle>
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 结果列表 -->
    <el-card>
      <el-table :data="conferenceList" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="50">
          <template slot-scope="scope">
            <!-- (pageNo - 1) * pageSize + index + 1 -->
            {{ (searchModel.pageNo - 1) * searchModel.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="conferenceId" label="会议ID" width="100"></el-table-column>
        <el-table-column prop="shortName" label="会议名" width="100"></el-table-column>
        <el-table-column prop="fullName" label="会议全称" width="150"></el-table-column>
        <el-table-column prop="eventLocation" label="会议地点" width="150"></el-table-column>
        <el-table-column prop="eventDate" label="举办时间" width="150" :formatter="formatEventDate"></el-table-column>
        <el-table-column prop="submissionDeadline" label="提交截止日期" width="150"
          :formatter="formatEventDate"></el-table-column>
        <el-table-column prop="reviewresultDate" label="审核结果日期" width="150"
          :formatter="formatEventDate"></el-table-column>
        <el-table-column prop="status" label="状态" width="150"></el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button @click="openEditUI(scope.row.conferenceId)" type="primary"
              :disabled="scope.row.status === '通过'">论文投稿</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分页组件 -->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
      :current-page="searchModel.pageNo" :page-sizes="[5, 10, 20, 50]" :page-size="searchModel.pageSize"
      layout="total, sizes, prev, pager, next, jumper" :total="total">
    </el-pagination>

    <!-- 论文提交对话框 -->
    <el-dialog @close="clearForm" :title="title" :visible.sync="dialogFormVisible">
      <el-form :model="paperForm" ref="paperFormRef" :rules="rules">
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
        </div>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="savePaper">确 定</el-button>
      </div>
    </el-dialog>


    <!-- 会议申请对话框 -->
    <el-dialog @close="clearForm" :title="title" :visible.sync="dialogConferenceFormVisible">
      <el-form :model="conferenceForm" ref="conferenceFormRef" :rules="conferenceRules" class="demo-dynamic">
        <div class="form-content">
          <div class="form-fields">
            <el-form-item prop="shortName" label="会议简称" :label-width="formLabelWidth">
              <el-input v-model="conferenceForm.shortName" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item prop="fullName" label="会议全称" :label-width="formLabelWidth">
              <el-input v-model="conferenceForm.fullName" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item prop="eventLocation" label="会议地点" :label-width="formLabelWidth">
              <el-input v-model="conferenceForm.eventLocation" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item prop="submissionDeadline" label="投稿截至日期" :label-width="formLabelWidth">
              <el-date-picker v-model="conferenceForm.submissionDeadline" type="date" placeholder="选择日期"
                format="yyyy-MM-dd"></el-date-picker>
            </el-form-item>
            <el-form-item prop="reviewresultDate" label="审核完成日期" :label-width="formLabelWidth">
              <el-date-picker v-model="conferenceForm.reviewresultDate" type="date" placeholder="选择日期"
                format="yyyy-MM-dd"></el-date-picker>
            </el-form-item>
            <el-form-item prop="eventDate" label="举办日期" :label-width="formLabelWidth">
              <el-date-picker v-model="conferenceForm.eventDate" type="date" placeholder="选择日期"
                format="yyyy-MM-dd"></el-date-picker>
            </el-form-item>
            <el-form-item v-for="(domain, index) in conferenceForm.topics" :label="'主题' + index" :key="domain.key"
              :prop="'topics.' + index + '.value'" :rules="{
                required: true, message: '主题不能为空', trigger: 'blur'
              }" :label-width="formLabelWidth">
              <el-input v-model="domain.value"></el-input><el-button @click.prevent="removeDomain(domain)">删除</el-button>
            </el-form-item>
            <el-form-item style="margin-left: 130px;">
              <el-button @click="addDomain">新增主题</el-button>
            </el-form-item>
          </div>
        </div>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogConferenceFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveConference">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import conferenceApi from '@/api/conferenceManage'
import paperApi from '@/api/paperManage'
import invitationApi from '@/api/invitationManage'
import { getToken } from '@/utils/auth' // get token from cookie

export default {
  data() {
    var checkPDF = (rule, value, callback) => {

      callback();
    };
    return {
      selectedTopics: [],
      allTopics: [],
      roleList: [],
      formData: new FormData(),
      conferenceForm: {
        topics: [{
          value: ''
        }],
      },
      paperConferenceId: 0,
      paperForm: {
        Title: '',
        Abstract: '',
        authors: [{ authorName: '', authorInstitution: '', authorRegion: '', authorEmail: '' }] // 初始至少有一位作者
      },
      uploadFile: null,
      uploadUrl: null,
      formLabelWidth: '130px',
      dialogFormVisible: false,
      dialogConferenceFormVisible: false,
      title: "",
      total: 0,
      searchModel: {
        pageNo: 1,
        pageSize: 10
      },
      conferenceList: [],
      rules: {
        Title: [
          { required: true, message: '请输入标题', trigger: 'blur' },
          { min: 10, max: 100, message: '长度在 10 到 100 个字符', trigger: 'blur' }
        ],
        Abstract: [
          { required: true, message: '请输入摘要', trigger: 'blur' },
          { min: 50, max: 800, message: '长度在 50 到 800 个字符', trigger: 'blur' }
        ],
        PDFPath: [
          { required: true, message: '请上传pdf文件', trigger: 'blur' },
          { validator: checkPDF, trigger: 'blur' }
        ],
        authors: [
          { validator: this.validateAuthors, trigger: 'blur' }
        ]
      },
      conferenceRules: {
        shortName: [
          { required: true, message: '请输入会议简称', trigger: 'blur' },
          { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' }
        ],
        fullName: [
          { required: true, message: '请输入会议全称', trigger: 'blur' },
          { min: 5, max: 100, message: '长度在 5 到 100 个字符', trigger: 'blur' }
        ],
        eventDate: [
          { required: true, message: '请指定举办日期', trigger: 'blur' },
        ],
        eventLocation: [
          { required: true, message: '请输入举办地点', trigger: 'blur' },
          { min: 3, max: 100, message: '长度在 3 到 100 个字符', trigger: 'blur' }
        ],
        submissionDeadline: [
          { required: true, message: '请指定投稿截止日期', trigger: 'blur' },
        ],
        reviewresultDate: [
          { required: true, message: '请指定审核完成日期', trigger: 'blur' },
        ]
      }
    }
  },
  methods: {
    async fetchData(id) {
      return new Promise((resolve, reject) => {
        invitationApi.getConferenceTopics(id).then(response => {
          console.log('response: ', response.data);
          this.allTopics = response.data;
          console.log('allTopics: ', this.allTopics);
          resolve(); // 解决 Promise
        }).catch(error => {
          reject(error); // 拒绝 Promise
        });
      });
    },
    saveConference() {
      const token = getToken();
      const topicsArray = this.conferenceForm.topics.map(topics => topics.value);
      const topicsString = topicsArray.join(',');
      // 触发表单验证
      this.$refs.conferenceFormRef.validate((valid) => {
        if (valid) {
          // 提交请求给后台
          console.log('topics: ' + topicsArray);
          console.log('topics2string: ' + topicsString);
          conferenceApi.applyConf(this.conferenceForm, topicsString, token).then(response => {
            // 成功提示
            this.$message({
              message: response.message,
              type: 'success'
            });
            // 关闭对话框
            this.dialogConferenceFormVisible = false;
            // 再刷新表格
            this.getConferenceList();
          });
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    addAuthor() {
      this.paperForm.authors.push({ authorName: '', authorInstitution: '', authorRegion: '', authorEmail: '' });
    },
    removeAuthor(index) {
      this.paperForm.authors.splice(index, 1);
    },
    validateAuthors(rule, value, callback) {
      if (value.length === 0) {
        callback(new Error('请至少输入一位作者的信息'));
      } else {
        callback();
      }
    },
    savePaper() {
      const isAuthorsValid = this.paperForm.authors.every(
        author => author.authorName !== '' && author.authorInstitution !== '' && author.authorEmail !== '' && author.authorRegion != ''
      );

      if (!isAuthorsValid) {
        this.$message.error('请填写所有作者信息！');
        return;
      }

      const token = getToken();
      const authorJ = JSON.stringify(this.paperForm.authors);
      this.formData.append('papertitle', this.paperForm.Title);
      this.formData.append('paperabstract', this.paperForm.Abstract);
      this.formData.append('file', this.uploadFile.raw);
      this.formData.append('author', authorJ);
      this.formData.append('topic', this.selectedTopics);
      this.formData.append('token', token);
      console.log("start...")
      // 提交请求给后台
      paperApi.uploadPaper(this.paperConferenceId, this.formData).then(response => {
        // 成功提示rr
        this.$message({
          message: response.message,
          type: 'success'
        });
        this.dialogFormVisible = false;
        this.getConferenceList();
        this.uploadFile = null;
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
    formatEventDate(row, column, cellValue) {
      if (cellValue) {
        const date = new Date(cellValue); // 假设 cellValue 是一个有效的日期字符串
        const formattedDate = date.toLocaleDateString(); // 或使用其他格式化选项
        return formattedDate;
      }
      return '';
    },
    handleExceed(files, fileList) {
      this.$message.warning('文件数量超过限制');
    },
    clearForm() {
      this.paperForm = {
        Title: '',
        Abstract: '',
        authors: [{ authorName: '', authorInstitution: '', authorRegion: '', authorEmail: '' }]
      };
      this.selectedTopics = [];
      this.conferenceForm = {};
      this.uploadFile =  null;
      this.uploadUrl = null;
      this.$set(this, 'uploadFile', null);
      this.formData = new FormData(); // 重置 FormData
    },
    openEditUI(id) {
      console.log('conferenceId: ' + id);
      this.fetchData(id);
      console.log('allTopics: ', this.allTopics); // 添加这行日志
      this.title = '论文投稿';
      this.paperConferenceId = id,
        this.dialogFormVisible = true;
    },
    openConferenceEditUI() {
      this.title = '会议申请';

      this.dialogConferenceFormVisible = true;
    },
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize;
      this.getConferenceList();
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo;
      this.getConferenceList();
    },
    getConferenceList() {
      conferenceApi.getConferenceList(this.searchModel).then(response => {
        this.conferenceList = response.data.rows;
        this.total = response.data.total;
      });
    },
    removeDomain(item) {
      var index = this.conferenceForm.topics.indexOf(item)
      if (index !== -1) {
        this.conferenceForm.topics.splice(index, 1)
      }
    },
    addDomain() {
      console.log('Adding domain...');
      const newDomain = {
        value: '',
        key: Date.now()
      };
      this.$set(this.conferenceForm.topics, this.conferenceForm.topics.length, newDomain);
      console.log('Domain added:', this.conferenceForm.topics);
    }
  },
  created() {
    this.getConferenceList();
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

.author-section {
  margin-bottom: 20px; /* 增加作者信息区域的垂直间距 */
}

.remove-button {
  margin-left: 130px; /* 调整删除按钮的左外边距 */
  margin-bottom: 30px;
}

.add-button {
  margin-top: 20px; /* 增加增加作者按钮的上外边距 */
  margin-left: 130px;
  margin-bottom: 60px;
}

/* 调整多选框样式 */
.el-checkbox-group {
  /* 调整多选框与上方元素的垂直间距 */
  margin-bottom: 30px;
  margin-left: 130px;
}

.el-checkbox--medium {
  margin-bottom: 10px;
  /* 调整多选框之间的垂直间距 */
}
</style>
