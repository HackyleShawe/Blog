<!--
  是否保存为新的版本的下拉选项
-->
<template>
  <el-dropdown :show-timeout="100" trigger="click">
    <el-button plain>
      <!--Draft：草稿；Release：发布-->
      {{ newVersion ? 'New Version' : 'Cur Version' }}
      <i class="el-icon-caret-bottom el-icon--right" />
    </el-button>

    <el-dropdown-menu slot="dropdown" class="no-padding">
      <el-dropdown-item>
        <el-radio-group v-model="newVersion" style="padding: 10px;">
          <el-radio :label="true">
            Save as New Version
          </el-radio>
          <el-radio :label="false">
            Save as Cur Version
          </el-radio>
        </el-radio-group>
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
export default {
  name: "NewVersion",
  props: {
    value: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    newVersion: {
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
