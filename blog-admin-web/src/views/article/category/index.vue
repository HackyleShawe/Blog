<template>
  <div>
    <!--添加-->
    <el-row :gutter="10">
      <el-col :span="6">
        <el-button type="primary" size="small" icon="el-icon-plus" @click="addDialogVisible=true">Add Category</el-button>
      </el-col>
    </el-row>

    <el-table :data="categoryList">
      <el-table-column label="No" type="index" width="50"></el-table-column>
      <el-table-column label="Name" prop="name" width="100"></el-table-column>
      <el-table-column label="Code" prop="code" width="80"></el-table-column>
      <el-table-column label="Description" prop="description"></el-table-column>
      <el-table-column label="UpdateTime" prop="updateTime"></el-table-column>

      <el-table-column label="Operate">
        <template v-slot="scope">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row)">Edit</el-button>
          <el-popconfirm title="Are you sure to delete?" icon="el-icon-delete" iconColor="red" @confirm="deleteCategoryById(scope.row.id)">
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

    <!--添加分类对话框-->
    <el-dialog title="Add Category" width="50%" :visible.sync="addDialogVisible" :close-on-click-modal="false" @close="addDialogClosed">
      <!--内容主体-->
      <el-form :model="addForm" :rules="formRules" ref="addFormRef" label-width="100px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="addForm.name"></el-input>
        </el-form-item>
        <el-form-item label="Code" prop="code">
          <el-input v-model="addForm.code"></el-input>
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input v-model="addForm.description"></el-input>
        </el-form-item>
      </el-form>
      <!--底部-->
      <span slot="footer">
				<el-button @click="addDialogVisible=false">Cancel</el-button>
				<el-button type="primary" @click="addCategory">Confirm</el-button>
			</span>
    </el-dialog>

    <!--编辑分类对话框-->
    <el-dialog title="Edit Category" width="50%" :visible.sync="editDialogVisible" :close-on-click-modal="false" @close="editDialogClosed">
      <!--内容主体-->
      <el-form :model="editForm" :rules="formRules" ref="editFormRef" label-width="100px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="Code" prop="code">
          <el-input v-model="editForm.code"></el-input>
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input v-model="editForm.description"></el-input>
        </el-form-item>
      </el-form>
      <!--底部-->
      <span slot="footer">
				<el-button @click="editDialogVisible=false">Cancel</el-button>
				<el-button type="primary" @click="editCategory">Confirm</el-button>
			</span>
    </el-dialog>
  </div>
</template>

<script>
import categoryApi from '@/api/article/categoryApi'

export default {
  name: "ArticleCategory",
  data() {
    return {
      //分页数据，设置初始值
      queryInfo: {
        pageNum: 1,
        pageSize: 10
      },
      categoryList: [],
      total: 0,
      addDialogVisible: false,
      editDialogVisible: false,
      addForm: {
        name: '', //中文名称
        code: '', //英文简称
        description: '', //分类描述
      },
      editForm: {},
      formRules: {
        name: [{required: true, message: 'Please Input Category Name', trigger: 'blur'}],
        code: [{required: true, message: 'Please Input Category Short Name', trigger: 'blur'}]
      }
    }
  },
  created() {
    this.getData()
  },
  methods: {
    getData() {
      let param = {
        "pageNum": this.queryInfo.pageNum,
        "pageSize": this.queryInfo.pageSize,
      }
      categoryApi.list(param).then(res => {
        this.categoryList = res.data.list
        this.total = res.data.total
      })
    },
    //监听 pageSize 改变事件
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getData()
    },
    //监听页码改变事件
    handleCurrentChange(newPage) {
      this.queryInfo.pageNum = newPage
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
    addCategory() {
      this.$refs.addFormRef.validate(valid => {
        if (valid) {
          let param= this.addForm
          categoryApi.add(param).then(res => {
            this.$message.success(res.msg)
            this.addDialogVisible = false
            this.getData()
          })
        }
      })
    },
    editCategory() {
      this.$refs.editFormRef.validate(valid => {
        if (valid) {
          let param=  this.editForm
          categoryApi.update(param).then(res => {
            this.$message.success(res.msg)
            this.editDialogVisible = false
            this.getData()
          })
        }
      })
    },
    showEditDialog(row) {
      //row 中没有对象(blogs是表单不需要的属性)，直接拓展运算符深拷贝一份(拓展运算符不能深拷贝对象，只能拷贝引用)
      //如果直接赋值，则为引用，表格上的数据也会随对话框中数据的修改而实时改变
      this.editForm = {...row}
      this.editDialogVisible = true
    },
    deleteCategoryById(id) {
      categoryApi.del(id).then(res => {
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
