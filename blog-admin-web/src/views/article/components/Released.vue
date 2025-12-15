<!--
  是否发布的下拉选项
-->
<template>
  <el-dropdown :show-timeout="100" trigger="click">
    <el-button plain>
      <!--Draft：草稿；Release：发布-->
      {{ released ? 'Release':'Draft' }}
      <i class="el-icon-caret-bottom el-icon--right" />
    </el-button>
    <el-dropdown-menu slot="dropdown" class="no-padding">
      <el-dropdown-item>
        <el-radio-group v-model="released" style="padding: 10px;">
          <el-radio :label="false">
            Save as Draft
          </el-radio>
          <el-radio :label="true">
            Release & publish
          </el-radio>
        </el-radio-group>
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
export default {
  name: "Released",
  props: {
    value: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    released: {
      get() {
        return this.value
      },
      set(val) {
        this.$emit('input', val)
      }
    }
  }
}
</script>
