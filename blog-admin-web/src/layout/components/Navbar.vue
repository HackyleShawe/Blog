<!--
  右侧上部分的功能按钮导航
-->
<template>
  <div class="navbar">
    <hamburger :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <!--面包屑-->
    <breadcrumb class="breadcrumb-container" />

    <!--右侧功能菜单-->
    <div class="right-menu">
      <template>
        <!--搜索按钮-->
        <search id="search" class="right-menu-item" />

        <!--全屏-->
        <screen-full id="screenfull" class="right-menu-item hover-effect" />
      </template>

      <!--头像及下拉框-->
      <el-dropdown class="avatar-container" trigger="click">
        <!--头像-->
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar">
          <i class="el-icon-caret-bottom" />
        </div>

        <!--头像下面的下拉框-->
        <el-dropdown-menu slot="dropdown" class="user-dropdown">
          <router-link to="/config/admin-info">
            <el-dropdown-item>
              My Info
            </el-dropdown-item>
          </router-link>
          <a target="_blank" href="https://hackyle.com">
            <el-dropdown-item>Home</el-dropdown-item>
          </a>
          <a target="_blank" href="https://github.com/HackyleShawe">
            <el-dropdown-item>Github</el-dropdown-item>
          </a>

          <el-dropdown-item divided @click.native="logout">
            <span style="display:block;">Log Out</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import ScreenFull from '@/components/ScreenFull'
import Search from '@/components/HeaderSearch'

export default {
  components: {
    Breadcrumb, Hamburger, ScreenFull, Search
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar'
    ])
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      await this.$store.dispatch('user/logout')
      this.$router.push(`/login?redirect=${this.$route.fullPath}`)
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color:transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
