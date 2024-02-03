<template>
  <div class="register-container">
    <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="register-form" auto-complete="on"
      label-position="left">

      <div class="title-container">
        <h3 class="title">用户注册</h3>
      </div>

      <el-form-item prop="name">
        <span class="svg-container">
          <svg-icon icon-class="user" />
        </span>
        <el-input ref="name" v-model="registerForm.name" placeholder="用户名" name="name" type="text" tabindex="1"
          auto-complete="on" />
      </el-form-item>

      <el-form-item prop="email">
        <span class="svg-container">
          <svg-icon icon-class="email" />
        </span>
        <el-input ref="email" v-model="registerForm.email" placeholder="邮箱" name="email" type="text" tabindex="1"
          auto-complete="on" />
      </el-form-item>

      <el-form-item prop="region">
        <span class="svg-container">
          <svg-icon icon-class="earth" />
        </span>
        <el-input ref="region" v-model="registerForm.region" placeholder="所在地区" name="region" type="text" tabindex="1"
          auto-complete="on" />
      </el-form-item>

      <el-form-item prop="institution">
        <span class="svg-container">
          <svg-icon icon-class="institution" />
        </span>
        <el-input ref="institution" v-model="registerForm.institution" placeholder="所属机构" name="institution" type="text"
          tabindex="1" auto-complete="on" />
      </el-form-item>

      <el-row :gutter="10">
        <el-col :span="12" class="gender-age">
          <el-form-item prop="gender" label-width="0">
            <el-input ref="gender" v-model="registerForm.gender" placeholder="性别" name="gender" type="text" tabindex="1"
              auto-complete="on" />
          </el-form-item>
        </el-col>
        <el-col :span="12" class="gender-age">
          <el-form-item prop="age" label-width="0">
            <el-input ref="age" v-model="registerForm.age" placeholder="年龄" name="age" type="text" tabindex="1"
              auto-complete="on" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item prop="password">
        <span class="svg-container">
          <svg-icon icon-class="password" />
        </span>
        <el-input :key="passwordType" ref="password" v-model="registerForm.password" :type="passwordType" placeholder="密码"
          name="password" tabindex="2" auto-complete="on" @keyup.enter.native="handleLogin" />
        <span class="show-pwd" @click="showPwd">
          <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
        </span>
      </el-form-item>

      <el-row>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-button :loading="loading" type="primary" style="width:100%;"
              @click.native.prevent="handleRegister">注册</el-button>
          </el-col>
          <el-col :span="12">
            <el-button style="width:100%;" @click.native.prevent="backToLogin">返回</el-button>
          </el-col>
        </el-row>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import { validUsername } from '@/utils/validate'

export default {
  name: 'Login',
  data() {
    const validateUsername = (rule, value, callback) => {
      // 使用正则表达式匹配用户名
      const usernamePattern = /^[a-zA-Z-][a-zA-Z0-9-_]{3,30}$/;
      if (!value.length) {
        callback(new Error('需要输入用户名'));
      }
      else if (value.length < 5 || value.length > 32) {
        callback(new Error('用户名需要介于5~32位'));
      } else if (!usernamePattern.test(value)) {
        callback(new Error('用户名只能包含字母、数字或特殊字符(-_)，并以字母或-开头'));
      } else {
        callback();
      }
    };

    const validateRegion = (rule, value, callback) => {
      if (!value.length) {
        callback(new Error('需要输入所在地区'));
      }
    };

    const validateInstitution = (rule, value, callback) => {
      if (!value.length) {
        callback(new Error('需要输入所属机构'));
      }
    }

    const validatePassword = (rule, value, callback) => {
      const passwordPattern = /^(?=(.*\d){1,})(?=(.*[A-Za-z]){1,})(?=(.*[-_]){1,})[A-Za-z0-9-_]+$/;
      if (!value.length) {
        callback(new Error('需要输入密码'));
      }
      if (value.length < 6 || value.length > 32) {
        callback(new Error('密码需要介于6~32位'));
      } else if (!passwordPattern.test(value)) {
        callback(new Error('密码需要包含字母、数字或特殊字符(-_)中的至少两种'));
      } else {
        // 排除用户名
        const username = this.registerForm.name; // 使用this关键字来获取registerForm.name
        // console.log('Username:', username); // 在控制台中打印用户名
        if (value.includes(username)) {
          callback(new Error('密码不能包含用户名'));
        } else {
          callback();
        }
      };
    };
    const validateEmail = (rule, value, callback) => {
      const emailPattern = /^[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*\.[a-z]{2,}$/;
      if (!value.length) {
        callback(new Error('需要输入邮箱'));
      }
      if (!emailPattern.test(value)) {
        callback(new Error('邮箱格式错误'));
      } else {
        callback();
      }
    };

    return {
      registerForm: {
        name: '',
        email: '',
        institution: '',
        region: '',
        sex: '',
        age: '',
        password: ''
      },
      registerRules: {
        name: [{ required: true, trigger: 'blur', validator: validateUsername }],
        email: [{ required: true, trigger: 'blur', validator: validateEmail }],
        region: [{ required: true, trigger: 'blur', validator: validateRegion }],
        institution: [{ required: true, trigger: 'blur', validator: validateInstitution }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }]
      },
      loading: false,
      passwordType: 'password',
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        if (route.query && route.query.redirect && route.query.redirect !== '/login') {
          this.redirect = route.query.redirect;
        }
      },
      immediate: true
    }
  },
  methods: {
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    handleRegister() {
      this.loading = true;
      this.$store.dispatch('user/register', this.registerForm)
        .then(() => {
          this.$router.push('/login');
          this.$message({
            message: '注册成功',
            type: 'success'
          });
          this.loading = false;
        })
        .catch(error => {
          console.error('Registration error:', error);
          this.loading = false;
        });
    },
    backToLogin() {
      this.$router.push('/login')
    }
  }
}
</script>

<style lang="scss">
/* 修复input 背景不协调 和光标变色 */
/* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

$bg: #283443;
$light_gray: #fff;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .register-container .el-input input {
    color: $cursor;
  }
}

.gender-age {
  display: flex;
  align-items: center;
}


/* reset element-ui css */
.register-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
      caret-color: $cursor;

      &:-webkit-autofill {
        box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }
    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
}
</style>

<style lang="scss" scoped>
$bg: #5c5e60;
$dark_gray: #889aa4;
$light_gray: #eee;

.register-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;
  background-image: url('../../assets/login-books.jpg');
  background-size: 100%;


  display: flex;
  align-items: center;

  .register-form {
    position: relative;
    width: 480px;
    max-width: 100%;
    padding: 10px 50px 25px;
    margin: 0 auto;
    overflow: hidden;
    background-color: #100830;
    border-radius: 8px;
    opacity: 0.95;
  }

  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .svg-container-half-left {
    display: flex;
    /* 使用 Flexbox 布局 */
    justify-content: space-between;
    /* 将内容水平分布在两侧 */
    width: 50%;
    /* 设置宽度为 50% */
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    /* 设置颜色 */
    vertical-align: middle;
    /* 垂直居中对齐 */
    display: inline-block;
  }

  .svg-icon-left,
  .svg-icon-right {
    width: 50%;
    /* 子元素宽度为 50% */
  }


  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }
}
</style>
