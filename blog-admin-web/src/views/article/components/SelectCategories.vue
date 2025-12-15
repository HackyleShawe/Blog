<!--
  文章分类选择组件
  暂时弃用：需要解决子、父组件之间，即时传递数据的问题。
  和SelectAuthors组件也存在同样的问题
-->
<template>
  <el-select v-model="selectedCategory" placeholder="Please Select Categories"
             :filterable="true" :multiple="true" default-first-option
             style="width: 100%;" @change="selectedCategoryHandler">
    <el-option :label="item.name" :value="item.id" v-for="item in categoryListOptions" :key="item.id"></el-option>
  </el-select>
</template>

<script>
import categoryApi from "@/api/article/category";

export default {
  name: "SelectCategory",
  //存在问题：页面刷新后，就无法从父组件传递initCategories数据过来
  props: ['initCategories'],
  data() {
    return {
      //当前选择了那些文章分类
      selectedCategory: [],
      //从后端获取所有的文章分类以供选择
      categoryListOptions: [],
    }
  },
  emits: ['chooseCategory'], //自定义事件，向父组件传递数据
  created() {
    this.getAllCategories();
  },
  beforeUpdate() {
    //存在问题：每次选中和反选中，initCategories的数据反而会变化
    if(this.initCategories.length > 0) {
      this.selectedCategory = this.initCategories
    }
  },
  methods: {
    //获取所有文章分类
    getAllCategories() {
      categoryApi.all().then(resp => {
        this.categoryListOptions = []
        this.categoryListOptions = resp.data;
      }).catch(err => {
        console.log(err)
      })
    },
    selectedCategoryHandler() {
      //存在问题：如何一有变化，就把数据即时传递出去
      //向父组件传递已经选择了的分类信息
      this.$emit('chooseCategory', this.selectedCategory);
    }
  }
}
</script>

<style scoped>

</style>
