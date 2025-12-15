<!--
  文章作者选择组件
  暂时弃用：需要解决子、父组件之间，即时传递数据的问题。
  问题同SelectCategories
-->
<template>
  <el-select v-model="selectedAuthor" placeholder="Please Select Authors"
             multiple filterable default-first-option
             style="width: 100%;" @change="selectedAuthorHandler">
    <el-option :label="item.nickName" :value="item.id+item.name" v-for="item in authorListOptions" :key="item.id"></el-option>
  </el-select>
</template>

<script>
import authorApi from "@/api/article/author";

export default {
  name: "SelectAuthor",
  props: ['initAuthors'],
  data() {
    return {
      //当前选择了那些作者
      selectedAuthor: [],
      //从后端获取所有的作者以供选择
      authorListOptions: [],
    }
  },
  emits: ['chooseAuthor'], //自定义事件，向父组件传递数据
  created() {
    this.getAllAuthors();
  },
  beforeUpdate() {
    if(this.initAuthors.length > 0) {
      this.selectedAuthor = this.initAuthors
    }
  },
  methods: {
    //获取所有作者
    getAllAuthors() {
      authorApi.all().then(resp => {
        this.authorListOptions = []
        this.authorListOptions = resp.data
      }).catch(err => {
        console.log(err)
      })
    },
    selectedAuthorHandler() {
      //向父组件传递已经选择了的分类信息
      this.$emit('chooseAuthor', this.selectedAuthor);
    }
  }
}
</script>

<style scoped>

</style>
