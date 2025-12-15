<!--
  已删除的评论
-->
<template>
  <div>
    <!--搜索-->
    <div style="margin-top: 10px">
      <el-form :inline="true" :model="queryInfo" class="demo-form-inline">
        <el-form-item label="Name">
          <el-input v-model="queryInfo.name" placeholder="Type Name"></el-input>
        </el-form-item>
        <el-form-item label="Email">
          <el-input v-model="queryInfo.email" placeholder="Type Email"></el-input>
        </el-form-item>
        <el-form-item label="Link">
          <el-input v-model="queryInfo.link" placeholder="Type Link"></el-input>
        </el-form-item>

        <el-form-item label="ArticleId">
          <el-input v-model="queryInfo.articleId" placeholder="Type ArticleId"></el-input>
        </el-form-item>

        <el-form-item label="Content">
          <el-input v-model="queryInfo.content" placeholder="Type Content"></el-input>
        </el-form-item>

        <el-form-item label="RootId">
          <el-input v-model="queryInfo.rootId" placeholder="Type RootId"></el-input>
        </el-form-item>
        <el-form-item label="ParentId">
          <el-input v-model="queryInfo.pid" placeholder="Type ParentId"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">Query</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!--评论展示列表-->
    <el-table :data="commentList" row-key="id" :tree-props="{children: 'replyComments'}">
      <el-table-column label="ID" type="index" width="50" show-overflow-tooltip></el-table-column>

      <el-table-column label="ArticleId" prop="articleId" show-overflow-tooltip width="130"></el-table-column>

      <el-table-column label="Name" prop="commentatorName" width="100" show-overflow-tooltip></el-table-column>
      <!--<el-table-column label="Email" prop="commentatorEmail" width="150" show-overflow-tooltip></el-table-column>-->
      <!--<el-table-column label="Link" prop="commentatorLink" show-overflow-tooltip></el-table-column>-->
      <!--<el-table-column label="IP" prop="commentatorIp" width="100" show-overflow-tooltip></el-table-column>-->
      <el-table-column label="Content" prop="content" show-overflow-tooltip width="400"></el-table-column>

      <el-table-column label="ReplyWho" prop="pname" width="100" show-overflow-tooltip></el-table-column>

      <!--跳转到评论所在文章页面-->
      <!--todo: 不能用articleId，得后端拼接号文章url跳转-->
      <!--<template v-slot="scope">
        <el-link type="success" :href="`/blog/${scope.row.targetId}`" target="_blank">{{scope.row.targetId}}</el-link>
      </template>-->

      <el-table-column label="Time" width="150">
        <template v-slot="scope">{{ scope.row.updateTime }}</template>
      </el-table-column>
      <el-table-column label="Released" width="85">
        <template v-slot="scope">
          <el-switch v-model="scope.row.released" @change="commentReleasedChanged(scope.row)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="Deleted" width="85">
        <template v-slot="scope">
          <el-switch v-model="scope.row.deleted" @change="commentDeletedChanged(scope.row)"></el-switch>
        </template>
      </el-table-column>

      <el-table-column label="Operate" width="200">
        <template v-slot="scope">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row)">Edit</el-button>
          <el-button type="danger" icon="el-icon-delete" size="mini" @click="deleteCommentById(scope.row.id)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="queryInfo.pageNum"
                   :page-sizes="[10, 20, 30, 50]" :page-size="queryInfo.pageSize" :total="total"
                   layout="total, sizes, prev, pager, next, jumper" background>
    </el-pagination>

    <!--编辑评论对话框-->
    <el-dialog title="Edit Comment" width="50%" :visible.sync="editDialogVisible" :close-on-click-modal="false" @close="editDialogClosed">
      <!--内容主体-->
      <el-form :model="editForm" :rules="formRules" ref="editFormRef" label-width="80px">
        <el-form-item label="Name" prop="nickname">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="editForm.email"></el-input>
        </el-form-item>
        <el-form-item label="Link" prop="website">
          <el-input v-model="editForm.link"></el-input>
        </el-form-item>
        <el-form-item label="IP" prop="ip">
          <el-input v-model="editForm.ip"></el-input>
        </el-form-item>
        <el-form-item label="Content" prop="content">
          <el-input v-model="editForm.content" type="textarea" maxlength="250" :rows="5" show-word-limit></el-input>
        </el-form-item>
      </el-form>
      <!--底部-->
      <span slot="footer">
				<el-button @click="editDialogVisible=false">Cancel</el-button>
				<el-button type="primary" @click="editComment">Confirm</el-button>
			</span>
    </el-dialog>

  </div>
</template>

<script>
import commentApi from '@/api/article/commentApi'
import {checkEmail} from "@/utils/regex";
import {checkIpv4} from "@/utils/regex";

export default {
  name: "ArticleComment",

  data() {
    return {
      pageId: null,
      queryInfo: { //查询搜索条件
        pageNum: 1,
        pageSize: 10,

        name: undefined, //评论者信息
        email: undefined, //评论者信息
        link: undefined, //评论者信息
        articleId: undefined, //文章ID
        content: undefined, //评论内容
        rootId: undefined, //根评论ID
        pid: undefined, //父评论ID
      },
      total: 0,
      commentList: [],
      editDialogVisible: false,
      editForm: {
        id: '',
        name: '',
        email: '',
        link: '',
        ip: '',
        content: ''
      },
      formRules: {
        name: [{required: true, message: 'Please type name', trigger: 'blur'}],
        email: [
          {required: true, message: 'Please type email', trigger: 'blur'},
          {validator: checkEmail, trigger: 'blur'}
        ],
        ip: [
          {required: true, message: 'Please type IP', trigger: 'blur'},
           {validator: checkIpv4, trigger: 'blur'}
        ],
        content: [
          {required: true, message: 'Please type content', trigger: 'blur'},
          {max: 255, message: 'Content max length is 255', trigger: 'blur'}
        ],
      }
    }
  },
  created() {
    this.getCommentList()
  },
  methods: {
    getCommentList() {
      let param = {
        "pageNum": this.queryInfo.pageNum,
        "pageSize": this.queryInfo.pageSize,
        "name": this.queryInfo.name,
        "email": this.queryInfo.email,
        "link": this.queryInfo.link,
        "content": this.queryInfo.content,
        "articleId": this.queryInfo.articleId,
        "rootId": this.queryInfo.rootId,
        "pid": this.queryInfo.pid,
      }
      commentApi.list(param).then(res => {
        this.commentList = res.data.list
        this.total = res.data.total
      })
    },

    //点击搜索按钮时触发
    search() {
      this.getCommentList()
    },

    //监听 pageSize 改变事件
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getCommentList()
    },
    //监听页码改变事件
    handleCurrentChange(newPage) {
      this.queryInfo.pageNum = newPage
      this.getCommentList()
    },
    //切换评论公开状态（如果切换成隐藏，则该评论的所有子评论都修改为同样的隐藏状态）
    commentDeletedChanged(row) {
      let param = {
        "id": row.id,
        "deleted": row.deleted,
      }
      commentApi.update(param).then(res => {
        this.$message.success(res.msg)
        this.getCommentList() //成功后再获取新的数据
      })
    },
    commentReleasedChanged(row) {
      let param = {
        "id": row.id,
        "released": row.released,
      }
      commentApi.update(param).then(res => {
        this.$message.success(res.msg)
        this.getCommentList() //成功后再获取新的数据
      })
    },

    deleteCommentById(id) {
      this.$confirm('It will delete <strong style="color: red">this Comment</strong>, Continue?', 'Notice', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }).then(() => {
        commentApi.del(id).then(res => {
          this.$message.success(res.msg)
          this.getCommentList()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Canceled'
        });
      });
    },
    showEditDialog(row) {
      this.editForm = {...row}
      this.editDialogVisible = true
    },
    editDialogClosed() {
      this.editForm = {}
      this.$refs.editFormRef.resetFields()
    },
    editComment() {
      this.$refs.editFormRef.validate(valid => {
        if (valid) {
          const param = {
            id: this.editForm.id,
            name: this.editForm.name,
            email: this.editForm.email,
            link: this.editForm.link,
            ip: this.editForm.ip,
            content: this.editForm.content,
          }
          commentApi.update(param).then(res => {
            this.$message.success(res.msg)
            this.editDialogVisible = false
            this.getCommentList()
          })
        }
      })
    },
  }
}
</script>

<style scoped>

</style>
