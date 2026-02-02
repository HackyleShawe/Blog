<!--
  文章编辑器：用于创建和修改文件内容
-->
<template>
  <div class="createPost-container">
    <el-form ref="articleDataForm" :model="articleDataForm" :rules="rules" class="form-container">
      <!--顶部固定栏-->
      <sticky :z-index="10" :class-name="'sub-navbar '+articleDataForm.status">
        <commented v-model="articleDataForm.commented" style="margin-left: 50px;" />
        <released v-model="articleDataForm.released" style="margin-left: 10px;" />
        <!--<new-version v-model="articleDataForm.newVersion" style="margin-left: 10px;" />-->
        <pin-to-top v-model="articleDataForm.toTop" style="margin-left: 10px;" />

        <!--保存文章-->
        <el-button v-loading="loading" style="margin-left: 10px;" type="success" @click="submitForm">
          SAVE
        </el-button>
      </sticky>

      <div class="createPost-main-container">
        <el-form-item style="margin-bottom: 2px;" label-width="40px" label="Title:">
          <el-input v-model="articleDataForm.title" :rows="1" type="textarea"
                    class="article-textarea" autosize placeholder="Please enter the article title" />
          <span v-show="titleContentLength" class="word-counter">{{ titleContentLength }}words</span>
        </el-form-item>

        <el-form-item style="margin-bottom: 2px;" label-width="70px" label="Summary:">
          <el-input v-model="articleDataForm.summary" :rows="1" type="textarea"
                    class="article-textarea" autosize placeholder="Please enter the article summery" />
          <span v-show="summaryContentLength" class="word-counter">{{ summaryContentLength }}words</span>
        </el-form-item>

        <el-form-item style="margin-bottom: 2px;" label-width="70px" label="Keywords:">
          <el-input v-model="articleDataForm.keywords" :rows="1" type="textarea"
                    class="article-textarea" autosize placeholder="Please enter the article keywords as meta tag of keywords for SEO" />
          <span v-show="keywordsContentLength" class="word-counter">{{ keywordsContentLength }}words</span>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Authors" prop="authorList">
              <!--<select-authors :initAuthors="articleDataForm.authors" @chooseAuthor="chooseAuthorHandler"/>-->
              <el-select v-model="articleDataForm.authors" placeholder="Please select authors"
                         :filterable="true" :multiple="true"
                         style="width: 100%;" value-key="id">
                <el-option :label="author.nickName" :value="author" v-for="author in authorListOptions" :key="author.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Categories" prop="categoryList">
              <!--<select-categories :initCategories="articleDataForm.categories" @chooseCategory="chooseCategoryHandler" />-->
              <el-select v-model="articleDataForm.categories" placeholder="Please select categories"
                         :filterable="true" :multiple="true"
                         style="width: 100%;" value-key="id">
                <el-option :label="category.name" :value="category" v-for="category in categoryListOptions" :key="category.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!--TinyMCE富文本编辑器-->
        <el-form-item prop="content" style="margin-bottom: 30px;">
          <!--save：可以在子组件Tinymec中调用父组件的submitForm方法-->
          <Tinymce ref="editor" v-model="articleDataForm.content" :height="400" @save="submitForm"/>
        </el-form-item>

      </div>
    </el-form>
  </div>
</template>

<script>

//导入公共组件
import Tinymce from '@/components/Tinymce' //富文本TinyMCE组件
import Sticky from '@/components/Sticky' //Header黏贴组件：当页面滚动时，固定header不动

//导入子组件
import Commented from './Commented'
import Released from './Released'
import pinToTop from './PinToTop'
//import NewVersion from './NewVersion'
//import SelectCategories from "./SelectCategories"
//import SelectAuthors from './SelectAuthors'

//导入JS
import { isExternal } from '@/utils/validate'
import articleApi from '@/api/article/articleApi'
import categoryApi from "@/api/article/categoryApi";
import authorApi from "@/api/article/authorApi";

export default {
  name: 'ArticleEditor',
  components: {Tinymce, Sticky, Released, Commented, pinToTop},
  data() {
    const validateRequire = (rule, value, callback) => {
      if (value === '') {
        this.$message({
          message: rule.field + '为必传项',
          type: 'error'
        })
        callback(new Error(rule.field + '为必传项'))
      } else {
        callback()
      }
    }
    const validateSourceUri = (rule, value, callback) => {
      if (value) {
        if (isExternal(value)) {
          callback()
        } else {
          this.$message({
            message: '外链url填写不正确',
            type: 'error'
          })
          callback(new Error('外链url填写不正确'))
        }
      } else {
        callback()
      }
    }
    return {
      articleDataForm: {
        id: "",
        uri: "", //文章外链
        authors: [], //作者
        categories: [], //文章分类
        title: '', //文章题目
        summary: '', //文章摘要
        keywords: '', //文章关键字，直接用于meta标签，SEO
        content: '', //文章内容
        faceImgLink: '', //文章图片
        released: false, //是否发布
        commented: true, //是否可以评论
        newVersion: false, //是否保存为新版本
        toTop: false, //文章是否置顶类型
      },
      loading: false,
      //submitted: false, //是否已提交，用于防止多次发送新增文章请求。不设置这个限定条件，方便随时保存
      categoryListOptions: [], //所有的分类
      authorListOptions: [], //所有的作者
      rules: {
        //image_uri: [{ validator: validateRequire }],
        title: [{ validator: validateRequire }],
        content: [{ validator: validateRequire }],
        linkUri: [{ validator: validateSourceUri, trigger: 'blur' }]
      },
      tempRoute: {}
    }
  },
  computed: {
    titleContentLength() {
      if(null == this.articleDataForm.title) return 0
      return this.articleDataForm.title.length
    },
    summaryContentLength() {
      if(null == this.articleDataForm.summary) return 0
      return this.articleDataForm.summary.length
    },
    keywordsContentLength() {
      if(null == this.articleDataForm.keywords) return 0
      return this.articleDataForm.keywords.length
    },

    displayTime: {
      // set and get is useful when the data
      // returned by the back end api is different from the front end
      // back end return => "2013-06-25 06:59:25"
      // front end need timestamp => 1372114765000
      get() {
        return (+new Date(this.articleDataForm.display_time))
      },
      set(val) {
        this.articleDataForm.display_time = new Date(val)
      }
    }
  },
  created() {
    // 检查URL中是否含有id参数，如果有则为编辑模式，获取该文章数据，填充到表单中；否则为新增模式
    if (this.$route.query.id) {
      console.log("编辑模式，articleId=", this.$route.query.id)
      // const id = this.$route.params && this.$route.params.id
      this.articleDataForm.id = this.$route.query.id
      this.fetchArticleData(this.$route.query.id)
    }

    //获取选项栏的所有数据
    this.getAllCategory()
    this.getAllAuthor()

    // Why need to make a copy of this.$route here?
    // Because if you enter this page and quickly switch tag, may be in the execution of the setTagsViewTitle function, this.$route is no longer pointing to the current page
    // https://github.com/PanJiaChen/vue-element-admin/issues/1221
    this.tempRoute = Object.assign({}, this.$route)
  },

  mounted() {
    window.addEventListener('keydown', this.handleKeydown)
  },
  beforeDestroy() {
    window.removeEventListener('keydown', this.handleKeydown)
  },
  methods: {
    fetchArticleData(id) {
      articleApi.get(id).then(response => {
        this.articleDataForm = response.data
        //console.log("this.articleDataForm:", this.articleDataForm)

        this.setTagsViewTitle() // set tagsview title
        this.setPageTitle() // set page title
      }).catch(err => {
        console.log(err)
      })
    },
    setTagsViewTitle() {
      const title = 'Edit'
      //提取ID的前10个字符，展示在路由页上
      const route = Object.assign({}, this.tempRoute, { title: `${title}-${this.articleDataForm.id}` })
      this.$store.dispatch('tagsView/updateVisitedView', route)
    },
    setPageTitle() {
      const title = 'Edit Article'
      document.title = `${title} - ${this.articleDataForm.id}`
    },

    getAllCategory() {
      categoryApi.all().then(resp => {
        // console.log("resp === ", resp)
        this.categoryListOptions = []
        this.categoryListOptions = resp.data;
      }).catch(err => {
        console.log(err)
      })
    },
    getAllAuthor() {
      authorApi.all().then(resp => {
        this.authorListOptions = []
        this.authorListOptions = resp.data;
      }).catch(err => {
        console.log(err)
      })
    },

    //表单提交
    submitForm() {
      let authorIds = "";
      let categoryIds = "";
      //console.log("this.articleDataForm.categories: ", this.articleDataForm.categories)
      //console.log("this.articleDataForm.authors: ", this.articleDataForm.authors)

      if(this.articleDataForm.categories != null) {
        this.articleDataForm.categories.forEach(ele => {
          categoryIds += ele.id + ","
        })
      }
      if(this.articleDataForm.authors != null) {
        this.articleDataForm.authors.forEach(ele => {
          authorIds += ele.id + ","
        })
      }

      //准备请求参数
      let data = {
        "id": this.articleDataForm.id,
        "authorIds": authorIds,
        "categoryIds": categoryIds,
        "title": this.articleDataForm.title,
        "uri": this.articleDataForm.uri,
        "summary": this.articleDataForm.summary,
        "keywords": this.articleDataForm.keywords,
        "content": this.articleDataForm.content,
        "faceImgLink": this.articleDataForm.faceImgLink,
        "released": this.articleDataForm.released,
        "commented": this.articleDataForm.commented,
        "newVersion": this.articleDataForm.newVersion,
        "toTop": this.articleDataForm.toTop,
      }

      //编辑模式
      if (this.articleId) {
        articleApi.update(data).then(resp => {
          console.log("文章编辑模式resp", resp)
          this.$notify({
            title: 'SUCCESS',
            message: '编辑文章保存成功',
            type: 'success',
            duration: 2000
          })
        }).catch(err => {
          console.log("文章编辑模式err", err)
          this.$notify({
            title: '失败',
            message: '编辑文章保存失败',
            type: 'error',
            duration: 2000
          })
        });
      } else { //新增模式
        articleApi.add(data).then(resp => {
          console.log("文章新增模式resp", resp)
          this.articleDataForm.id = resp.data
          this.$notify({
            title: '成功',
            message: '新建文章保存成功',
            type: 'success',
            duration: 2000
          })
        }).catch(err => {
          console.log("文章新增模式err", err)
          this.$notify({
            title: '失败',
            message: '新建文章保存失败',
            type: 'error',
            duration: 2000
          })
        });
      }

    },

    //当按下Ctrl+S时，调用自定义的函数
    handleKeydown(e) {
      // Windows / Linux：Ctrl + S
      // Mac：Command + S
      if ((e.ctrlKey || e.metaKey) && e.key === 's') {
        e.preventDefault() // 阻止浏览器保存
        console.log("save by Ctrl + s")
        this.submitForm() //调用保存接口
      }
    },
  }

}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/mixin.scss';
@import '@/assets/styles/variables.module.scss';

.createPost-container {
  position: relative;

  .createPost-main-container {
    padding: 5px 45px 20px 50px;

    .postInfo-container {
      position: relative;
      @include clearfix;
      margin-bottom: 10px;

      .postInfo-container-item {
        float: left;
        //margin-top: 20px;
      }
    }
  }

  .word-counter {
    width: 40px;
    position: absolute;
    right: 10px;
    top: 0px;
  }
}

.article-textarea ::v-deep {
  textarea {
    margin: 10px 10px;
    padding-right: 50px;
    resize: none;
    border: none;
    border-radius: 0px;
    font-size: larger;
    border-bottom: 1px solid #bfcbd9;
  }
}
</style>
