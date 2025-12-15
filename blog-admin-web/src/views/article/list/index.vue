<!--
  文章列表
-->
<template>
  <div>
    <!--搜索-->
    <el-form :model="queryInfo" ref="queryForm" size="small" :inline="true" >
      <el-form-item label="标题" prop="title">
        <el-input
            v-model="queryInfo.title"
            placeholder="请输入文章标题"
            clearable
            @keyup.enter.native="search"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="search">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!--表格显示-->
    <el-table :data="articleList">
      <el-table-column label="No" type="index" width="50"></el-table-column>
      <el-table-column label="Title" prop="title" show-overflow-tooltip></el-table-column>
      <el-table-column label="Category" prop="categoryName" width="120" show-overflow-tooltip></el-table-column>
      <el-table-column label="Author" prop="authorName" width="100" show-overflow-tooltip></el-table-column>

      <el-table-column class-name="status-col" label="Released" width="90">
        <template v-slot="scope">
          <el-switch v-model="scope.row.released" @change="changePublishedStatus(scope.row)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" label="Commented" width="105">
        <template v-slot="scope">
          <el-switch v-model="scope.row.commented" @change="changePublishedStatus(scope.row)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" label="deleted" width="90">
        <template v-slot="scope">
          <el-switch v-model="scope.row.deleted" @change="changePublishedStatus(scope.row)"></el-switch>
        </template>
      </el-table-column>

      <!--在列表里不展示时间，在详情页展示-->
      <el-table-column label="Update Time" width="150">
        <template v-slot="scope">{{ scope.row.updateTime }}</template>
      </el-table-column>
      <el-table-column label="Operate" width="200">
        <template v-slot="scope">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="goBlogEditPage(scope.row.id)">Edit</el-button>
          <el-popconfirm title="Are you sure to delete?" icon="el-icon-delete" iconColor="red" @confirm="deleteBlogById(scope.row.id)">
            <el-button size="mini" type="danger" icon="el-icon-delete" slot="reference">Delete</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="queryInfo.pageNum"
                   :page-sizes="[10, 20, 30, 50]" :page-size="queryInfo.pageSize" :total="total"
                   layout="total, sizes, prev, pager, next, jumper" background>
    </el-pagination>
  </div>
</template>


<script>
import articleApi from '@/api/article/articleApi'
import categoryApi from '@/api/article/categoryApi'

export default {
  name: "ArticleReleased",
  data() {
    return {
      queryInfo: {
        title: '',
        categoryIds: '',
        pageNum: 1,
        pageSize: 10
      },
      //文章列表
      articleList: [],
      //文章分类
      categoryList: [],
      total: 0,
      articleId: 0,
      visForm: {
        appreciation: false,
        recommend: false,
        commentEnabled: false,
        top: false,
        published: false,
        password: '',
      }
    }
  },
  created() {
    this.fetchArticleData()
    //console.log("this.articleList", this.articleList)
  },
  methods: {
    fetchArticleData() {
      let param = {
        "pageNum": this.queryInfo.pageNum,
        "pageSize": this.queryInfo.pageSize,
        // "released": true, //已发布
        // "deleted": false, //未删除
        "title": this.queryInfo.title,
      }

      //分页获取文章列表
      articleApi.list(param).then(res => {
        this.articleList = res.data.list
        this.total = res.data.total
      })

      //获取所有分类
      categoryApi.all().then(resp => {
        this.categoryList = resp.data
      })
    },

    search() {
      this.queryInfo.pageNum = 1
      this.queryInfo.pageSize = 10
      this.fetchArticleData()
    },
    //切换文章发布状态
    changePublishedStatus(row) {
      let param = {
        "id": row.id,
        "released":row.released,
        "commented":row.commented,
        "deleted":row.deleted,
      }
      articleApi.update(param).then(res => {
        this.$message.success(res.msg);
        this.fetchArticleData();
      })
    },
    //监听 pageSize 改变事件
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.fetchArticleData()
    },
    //监听页码改变的事件
    handleCurrentChange(newPage) {
      this.queryInfo.pageNum = newPage
      this.fetchArticleData()
    },
    //路由到编辑页
    goBlogEditPage(id) {
      this.$router.push('/article/writing?id='+id)
    },
    deleteBlogById(id) {
      //逻辑删除
      articleApi.del(id).then(res => {
        this.$message.success(res.msg)
        this.fetchArticleData()
      })
      //this.$confirm('It will delete this Article<strong style="color: red">and all child</strong>, Continue?', 'Notice', {
      //  confirmButtonText: 'Confirm',
      //  cancelButtonText: 'Cancel',
      //  type: 'warning',
      //  dangerouslyUseHTMLString: true
      //}).then(() => {
      //  console.log("del")
      //  articleApi.del(id).then(res => {
      //    this.$message.success(res.message)
      //    this.fetchArticleData()
      //  })
      //}).catch(() => {
      //  this.$message({
      //    type: 'info',
      //    message: 'Canceled'
      //  })
      //})
    },
    resetQuery() {
      this.queryInfo.title = ''
      this.queryInfo.categoryIds = ''
      this.queryInfo.pageNum = 1
      this.queryInfo.pageSize = 10
    }
  }
}
</script>

<style scoped>
.el-button + span {
  margin-left: 10px;
}
</style>

