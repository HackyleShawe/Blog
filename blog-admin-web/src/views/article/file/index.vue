<!--
  文件（静态资源，如图片、视频、音乐、文档等）管理
-->
<template>
  <div>
    <!--搜索-->
    <div style="margin-top: 10px">
      <el-form :inline="true" :model="queryInfo" class="demo-form-inline">
        <el-form-item label="ArticleId">
          <el-input v-model="queryInfo.articleId" placeholder="Type ArticleId"></el-input>
        </el-form-item>
        <el-form-item label="FileLink">
          <el-input v-model="queryInfo.fileLink" placeholder="Type File Link URI"></el-input>
        </el-form-item>
        <el-form-item label="EmptyArticle">
          <el-radio v-model="queryInfo.emptyArticle" :label="true">YES</el-radio>
          <el-radio v-model="queryInfo.emptyArticle" :label="false">No</el-radio>
        </el-form-item>

        <!--<el-form-item label="Time">-->
        <!--  &lt;!&ndash;Notice：这里报错：Instead, use a data or computed property based on the prop's value. Prop being mutated: "placement"&ndash;&gt;-->
        <!--  &lt;!&ndash;原因是：element-ui版本2.15.9的bug&ndash;&gt;-->
        <!--  <el-date-picker-->
        <!--    v-model="queryInfo.timeRange"-->
        <!--    type="datetimerange"-->
        <!--    :picker-options="pickerOptions"-->
        <!--    range-separator="TO"-->
        <!--    start-placeholder="StartTime"-->
        <!--    end-placeholder="EndTime"-->
        <!--    align="right">-->
        <!--  </el-date-picker>-->
        <!--</el-form-item>-->

        <el-form-item>
          <el-button type="primary" @click="search">Query</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!--列表展示-->
    <el-table :data="fileList" row-key="id">
      <el-table-column label="No" type="index" width="50"></el-table-column>
      <el-table-column label="ArticleId" prop="articleId" width="150" show-overflow-tooltip></el-table-column>
      <el-table-column label="FileLink" prop="fileLink" width="350" show-overflow-tooltip></el-table-column>
      <!--<el-table-column label="FileType" prop="fileType" width="100" show-overflow-tooltip></el-table-column>-->

      <!--以超小图的方式预览-->
      <!--<el-table-column label="Thumbnail" width="250" show-overflow-tooltip>-->
      <!--  <template slot-scope="scope">-->
      <!--    <img style="width:60px" :src="scope.row.fileLink">-->
      <!--  </template>-->
      <!--</el-table-column>-->

      <el-table-column label="Preview" width="120">
        <template slot-scope="scope">
          <el-button type="text" icon="el-icon-view" size="mini" @click.native="previewFile(scope.row.fileLink)">Preview</el-button>
        </template>
      </el-table-column>

      <el-table-column label="Time" width="160" show-overflow-tooltip>
        <template v-slot="scope">{{ scope.row.createTime }}</template>
      </el-table-column>

      <el-table-column label="Download" width="200">
        <template slot-scope="scope">
          <el-button type="text" icon="el-icon-download" size="mini" @click.native="downloadFile(scope.row.id)">Download</el-button>
        </template>
      </el-table-column>

      <el-table-column label="Operate" width="200">
        <template v-slot="scope">
          <!--<el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row)">Edit</el-button>-->
          <!--需要进行删除检查：确保该文章中已不存在对应的图片链接-->
          <el-button type="danger" icon="el-icon-delete" size="mini" @click="deleteFile(scope.row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="queryInfo.pageNum"
                   :page-sizes="[10, 20, 30, 50]" :page-size="queryInfo.pageSize" :total="total"
                   layout="total, sizes, prev, pager, next, jumper" background>
    </el-pagination>

    <!--编辑对话框-->
    <!--主要修改图片对应的文章，注意，这里修改并不能同步修改文章实际内容-->
    <el-dialog title="Edit Link" width="50%" :visible.sync="editDialogVisible" :close-on-click-modal="false" @close="editDialogClosed">
      <!--内容主体-->
      <el-form :model="editForm" ref="editFormRef" label-width="100px">
        <el-form-item label="ArticleURI" prop="articleUri">
          <el-input v-model="editForm.articleUri"></el-input>
        </el-form-item>
        <el-form-item label="PicLinkURI" prop="linkUrl">
          <el-input v-model="editForm.fileLink"></el-input>
        </el-form-item>
      </el-form>
      <!--底部-->
      <span slot="footer">
				<el-button @click="editDialogVisible=false">Cancel</el-button>
				<el-button type="primary" @click="editConfirm">Confirm</el-button>
			</span>
    </el-dialog>

  </div>
</template>

<script>
import fileApi from '@/api/article/fileApi'

export default {
  name: "File",
  data() {
    return {
      pageId: null,
      queryInfo: { //查询搜索条件
        articleId: undefined,
        fileLink: undefined,
        emptyArticle: undefined, //没有article_uri的文件连接
        pageNum: 1,
        pageSize: 10,
      },
      total: 0,
      fileList: [],
      editDialogVisible: false,
      editForm: {
        id: 0,
        articleUri: '',
        fileLink: '',
      },
    }
  },
  created() {
    this.getFileList()
  },
  methods: {
    getFileList() {
      let param = {
        "pageNum": this.queryInfo.pageNum,
        "pageSize": this.queryInfo.pageSize,
        "articleId": this.queryInfo.articleId,
        "fileLink": this.queryInfo.fileLink,
        "emptyArticle": this.queryInfo.emptyArticle,
      }
      fileApi.list(param).then(res => {
        this.fileList = res.data.list
        this.total = res.data.total
      })
    },

    search() {
      this.getFileList()
    },

    //监听 pageSize 改变事件
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getFileList()
    },
    //监听页码改变事件
    handleCurrentChange(newPage) {
      this.queryInfo.pageNum = newPage
      this.getFileList()
    },
    //文件预览
    previewFile(fileLink) {
      //检查是否是浏览器支持打开的文件
      let filePart = fileLink.split('.')
      let fileType = filePart[filePart.length-1]
      let opened = ['html','txt','jpg','jpeg','gif','png','art','au','aiff','xbm']
      for(let idx in opened) {
        if(fileType.toLowerCase() === opened[idx]) {
          window.open(fileLink) //在新窗口打开这个链接
          return
        }
      }
      this.$message.error("This link can't be preview!")
    },
    //文件下载
    downloadFile(id) {
      //基于axios的文件下载：https://www.jb51.net/article/257569.htm
      fileApi.download(id).then(res => {
        console.log(res)
        let filename = res.headers.filename
        let blob = new Blob([res.data]);
        let url = window.URL.createObjectURL(blob); // 创建 url 并指向 blob
        let a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.click();
        window.URL.revokeObjectURL(url); // 释放该 url
      })
    },
    deleteFile(row) {
      this.$confirm('It will delete <strong style="color: red">this file</strong>, Continue?', 'Notice', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }).then(() => {
        const fileId = row.id
        fileApi.del(fileId).then(res => {
          this.$message.success(res.msg)
          this.getFileList()
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
    editConfirm() {
      this.$refs.editFormRef.validate(valid => {
        if (valid) {
          const param = {
            "id": this.editForm.id,
            "articleUri": this.editForm.articleUri,
            "fileLink": this.editForm.fileLink
          }
          fileApi.update(param).then(res => {
            this.$message.success(res.message)
            this.editDialogVisible = false
            this.getFileList()
          })
        }
      })
    },
  }
}
</script>

<style scoped>

</style>
