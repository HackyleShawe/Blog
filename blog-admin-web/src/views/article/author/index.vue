<!--
  文章作者管理页
-->
<template>
  <div>
    <!--添加按钮-->
    <el-row :gutter="10">
      <el-col :span="6">
        <el-button type="primary" size="small" icon="el-icon-plus" @click="addDialogVisible=true">Add New Author</el-button>
      </el-col>
    </el-row>

    <!--Author列表显示-->
    <el-table :data="authorList">
      <el-table-column label="No" type="index" width="50"></el-table-column>
      <el-table-column label="NickName" prop="nickName" width="100"></el-table-column>
      <el-table-column label="RealName" prop="realName" width="100"></el-table-column>
      <el-table-column label="Description" prop="description"></el-table-column>
      <el-table-column label="UpdateTime" prop="updateTime"></el-table-column>

      <el-table-column label="Operate">
        <template v-slot="scope">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row)">EDIT</el-button>
          <el-popconfirm title="Are you sure to delete?" icon="el-icon-delete" iconColor="red" @confirm="deleteAuthorById(scope.row.id)">
            <el-button size="mini" type="danger" icon="el-icon-delete" slot="reference">DELETE</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageInfo.pageNum"
                   :page-sizes="[10, 20, 30, 50]" :page-size="pageInfo.pageSize" :total="total"
                   layout="total, sizes, prev, pager, next, jumper" background>
    </el-pagination>

    <!--添加标签对话框-->
    <el-dialog title="Add New Author" width="50%" :visible.sync="addDialogVisible" :close-on-click-modal="false" @close="addDialogClosed">
      <!--内容主体-->
      <el-form :model="addForm" :rules="formRules" ref="addFormRef" label-width="100px">
        <el-form-item label="NickName" prop="nickName">
          <el-input v-model="addForm.nickName"></el-input>
        </el-form-item>
        <el-form-item label="RealName" prop="realName">
          <el-input v-model="addForm.realName"></el-input>
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input v-model="addForm.description"></el-input>
        </el-form-item>
      </el-form>

      <span slot="footer">
				<el-button @click="addDialogVisible=false">取 消</el-button>
				<el-button type="primary" @click="addAuthor">确 定</el-button>
			</span>
    </el-dialog>

    <!--编辑标签对话框-->
    <el-dialog title="编辑标签" width="50%" :visible.sync="editDialogVisible" :close-on-click-modal="false" @close="editDialogClosed">
      <!--内容主体-->
      <el-form :model="editForm" :rules="formRules" ref="editFormRef" label-width="100px">
        <el-form-item label="NickName" prop="nickName">
          <el-input v-model="editForm.nickName"></el-input>
        </el-form-item>
        <el-form-item label="RealName" prop="realName">
          <el-input v-model="editForm.realName"></el-input>
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input v-model="editForm.description"></el-input>
        </el-form-item>

      </el-form>
      <span slot="footer">
				<el-button @click="editDialogVisible=false">取 消</el-button>
				<el-button type="primary" @click="editAuthor">确 定</el-button>
			</span>
    </el-dialog>
  </div>
</template>


<script>
import authorApi from '@/api/article/authorApi'

export default {
  name: "ArticleAuthor",
  data() {
    return {
      pageInfo: {
        pageNum: 1,
        pageSize: 10
      },
      authorList: [],
      total: 0,
      pages: 1,
      addDialogVisible: false,
      editDialogVisible: false,
      showDialogVisible: false,
      addForm: {
        nickName: '',
        realName: '',
        description: '',
        email: '',
        phone: '',
        address: '',
        birthday: '',
        gender: '',
        avatar: '',
        isLocked: false,
      },
      editForm: {},
      formRules: {
        nickName: [{required: true, message: 'Please Input Nick Name', trigger: 'blur'}],
        //description: [{required: true, message: 'Please Input Description', trigger: 'blur'}],
      },
    }
  },
  created() {
    this.getData()
  },
  methods: {
    getData() {
      let param = {
        "pageNum": this.pageInfo.pageNum,
        "pageSize": this.pageInfo.pageSize
      }
      authorApi.list(param).then(res => {
        this.authorList = res.data.list
        this.total = res.data.total
        this.pages = res.data.pages
      })
    },
    //监听 pageSize 改变事件
    handleSizeChange(newSize) {
      this.pageInfo.pageSize = newSize
      this.getData()
    },
    //监听页码改变事件
    handleCurrentChange(newPage) {
      this.pageInfo.pageNum = newPage
      this.getData()
    },
    addDialogClosed() {
      //重置表单到初始值，不是清空表单
      //如果resetFields()没有生效，是因为没有设置有效验证规则。解决：设置验证规则 prop
      //也就是说：el-form-item的prop值要与el-input v-model的值一致
      this.$refs.addFormRef.resetFields()
    },
    editDialogClosed() {
      this.editForm = {}
      this.$refs.editFormRef.resetFields()
    },
    addAuthor() {
      this.$refs.addFormRef.validate(valid => {
        if (valid) {
          let param = {
            "nickName": this.addForm.nickName,
            "realName": this.addForm.realName,
            "description": this.addForm.description,
          }
          authorApi.add(param).then(res => {
            this.$message.success(res.msg)
            this.addDialogVisible = false
            this.getData()
          })

        }
      })
    },
    editAuthor() {
      this.$refs.editFormRef.validate(valid => {
        if (valid) {
          let param = {
            "id": this.editForm.id,
            "nickName": this.editForm.nickName,
            "realName": this.editForm.realName,
            "description": this.editForm.description,
          }
          authorApi.update(param).then(res => {
            this.$message.success(res.msg)
            this.editDialogVisible = false
            this.getData()
          })
        }
      })
    },
    showEditDialog(row) {
      this.editForm = {...row}
      this.editDialogVisible = true
    },
    deleteAuthorById(id) {
      authorApi.del(id).then(res => {
        this.$message.success(res.msg)
        this.getData()
      })
    }
  }
}
</script>

<style scoped>
.el-button + span {
  margin-left: 10px;
}
</style>
