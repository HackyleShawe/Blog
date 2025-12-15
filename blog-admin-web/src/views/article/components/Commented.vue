<!--
  是否可以评论的下拉选项
-->
<template>
  <el-dropdown :show-timeout="100" trigger="click">
    <el-button plain>
      {{ commented ? 'Open Comment':'Close Comment' }}
      <i class="el-icon-caret-bottom el-icon--right" />
    </el-button>
    <el-dropdown-menu slot="dropdown" class="no-padding">
      <el-dropdown-item>
        <el-radio-group v-model="commented" style="padding: 10px;">
          <el-radio :label="true">
            Open comment
          </el-radio>
          <el-radio :label="false">
            Close comment
          </el-radio>
        </el-radio-group>
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
export default {
  name: "Commented",
  props: {
    value: {
      type: Boolean,
      default: true
    }
  },
  computed: {
    commented: {
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
