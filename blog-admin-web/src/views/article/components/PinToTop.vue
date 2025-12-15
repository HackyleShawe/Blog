<!--
  是否置顶的下拉选项
-->
<template>
  <el-dropdown :show-timeout="100" trigger="click">
    <el-button plain>
      <!--Draft：草稿；Release：发布-->
      {{ pinToTop ? 'PinTop':'NoTop' }}
      <i class="el-icon-caret-bottom el-icon--right" />
    </el-button>
    <el-dropdown-menu slot="dropdown" class="no-padding">
      <el-dropdown-item>
        <el-radio-group v-model="pinToTop" style="padding: 10px;">
          <el-radio :label="true">
            Pin to Top
          </el-radio>
          <el-radio :label="false">
            No Top
          </el-radio>
        </el-radio-group>
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
export default {
  name: "PinToTop",
  props: {
    value: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    pinToTop: {
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
