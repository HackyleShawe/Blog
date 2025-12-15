<template>
  <div :class="{fullscreen:fullscreen}" class="tinymce-container" :style="{width:containerWidth}">
    <textarea :id="tinymceId" class="tinymce-textarea" />
  </div>
</template>

<script>
import {getToken} from '@/utils/auth'
import tinymceConfig from './config'
import load from './dynamicLoadScript'

//const tinymceCDN = 'https://cdn.jsdelivr.net/npm/tinymce-all-in-one@4.9.3/tinymce.min.js'
const tinymceCDN = 'https://cdn.tiny.cloud/1/r9kerxnm8ydga1tchdwlcs7tx8gsxqwjoxedsryjpsdche70/tinymce/5/tinymce.min.js'

export default {
  name: 'Tinymce',
  props: {
    id: {
      type: String,
      default: function() {
        return 'vue-tinymce-' + +new Date() + ((Math.random() * 1000).toFixed(0) + '')
      }
    },
    value: {
      type: String,
      default: ''
    },
    toolbar: {
      type: Array,
      required: false,
      default() {
        return []
      }
    },
    menubar: {
      type: String,
      default: 'file edit insert view format table'
    },
    height: {
      type: [Number, String],
      required: false,
      default: 900
    },
    width: {
      type: [Number, String],
      required: false,
      default: 'auto'
    }
  },
  data() {
    return {
      hasChange: false,
      hasInit: false,
      tinymceId: this.id,
      fullscreen: false,
    }
  },
  computed: {
    containerWidth() {
      const width = this.width
      if (/^[\d]+(\.[\d]+)?$/.test(width)) { // matches `100`, `'100'`
        return `${width}px`
      }
      return width
    }
  },
  watch: {
    value(val) {
      if (!this.hasChange && this.hasInit) {
        this.$nextTick(() =>
          window.tinymce.get(this.tinymceId).setContent(val || ''))
      }
    }
  },
  mounted() {
    this.init()
  },
  activated() {
    if (window.tinymce) {
      this.initTinymce()
    }
  },
  deactivated() {
    this.destroyTinymce()
  },
  destroyed() {
    this.destroyTinymce()
  },
  methods: {
    init() {
      // dynamic load tinymce from cdn
      load(tinymceCDN, (err) => {
        if (err) {
          this.$message.error(err.message)
          return
        }
        this.initTinymce()
      })
    },
    initTinymce() {
      const _this = this
      window.tinymce.init({
        selector: `#${this.tinymceId}`,
        height: this.height,
        body_class: 'panel-body',
        object_resizing: false,

        menubar: tinymceConfig.menubar.length > 0 ? tinymceConfig.menubar : this.menubar,
        plugins: tinymceConfig.plugins,

        //工具栏粘住
        toolbar: this.toolbar.length > 0 ? this.toolbar : tinymceConfig.toolbar,
        toolbar_sticky: true,

        quickbars_selection_toolbar: tinymceConfig.quickToolbar,

        //自动保存
        //autosave_ask_before_unload: true,
        //autosave_interval: '30s',
        //autosave_prefix: '{path}{query}-{id}-',
        //autosave_restore_when_empty: false,
        //autosave_retention: '2m',

        //定义文章默认的字体格式。注意：这样会造成HTML代码很冗余
        //content_style: 'body { font-family:Microsoft YaHei UI; font-size:16px }',
        //字体配置
        fontsize_formats: tinymceConfig.fontsizeFormats,
        font_formats: tinymceConfig.fontFormats,

        //定义codeSample支持那些格式的代码
        codesample_languages: tinymceConfig.codeSampleLanguages,
        code_dialog_height: 600,
        code_dialog_width: 1200,

        //快速排版
        textpattern_patterns: tinymceConfig.textPatterns,

        //图片工具
        imagetools_toolbar: 'editimage imageoptions', //图片工具栏，其中"rotateleft rotateright | flipv fliph | "需要配置imagetools_proxy
        //imagetools_cors_hosts: ['hackyle.com', 'blog.hackyle.com'], //指定跨域资源共享 (CORS)的站点
        //imagetools_cors_hosts: ['picsum.photos'],

        //图片相关配置
        automatic_uploads: true, //从外部复制到编辑器、编辑图片也会自动上传图片
        file_picker_types: 'file image media', //文件选择器类型，现在就支持三个
        image_caption: true, //启用图片标题
        image_title: true, //鼠标悬在图片上显示说明
        //image_uploadtab: true, //显示图片上传对话框
        image_advtab: true, //图像编辑的高级操作
        //启用了automatic_uploads，则会自动触发上传。上传处理程序应返回上传文件的新位置：{ "location": "folder/sub-folder/name.png" }
        images_upload_url: process.env.VUE_APP_BACKEND_API + '/article/file/uploadImgTinyMCE?token='+getToken(), //图片上传URL
        //自定义图片上传逻辑
        //images_upload_handler: function (blobInfo, success, failure){
        //  let formData = new FormData();
        //  formData.append('file',blobInfo.blob());
        //  formData.append('articleType',1);
        //  formData.append('attachmetType',3);
        //},

        //父剪贴板中复制入的相关配置
        paste_block_drop: false, //允许拖入资源文件
        paste_data_images: true, //允许粘贴图片

        // 检测类似于 URL 的文本并将文本更改为超链接
        // 检测类似于图像 URL 的文本，并尝试用图像替换文本
        smart_paste: true,

        //toc目录
        toc_depth: 6, //目录深度
        toc_header: 'div', //生成标题，默认情况下会用H2标签标记；改为div

        end_container_on_empty_block: true,
        advlist_bullet_styles: 'square',
        advlist_number_styles: 'default',
        default_link_target: '_blank',
        link_title: false,
        nonbreaking_force_tab: true, // inserting nonbreaking space &nbsp; need Nonbreaking Space Plugin
        init_instance_callback: editor => {
          if (_this.value) {
            editor.setContent(_this.value)
          }
          _this.hasInit = true
          editor.on('NodeChange Change KeyUp SetContent', () => {
            this.hasChange = true
            this.$emit('input', editor.getContent())
          })
        },
        setup(editor) {
          editor.on('FullscreenStateChanged', (e) => {
            _this.fullscreen = e.state
          })
        },
        // it will try to keep these URLs intact
        // https://www.tiny.cloud/docs-3x/reference/configuration/Configuration3x@convert_urls/
        // https://stackoverflow.com/questions/5196205/disable-tinymce-absolute-to-relative-url-conversions
        convert_urls: false
      })
    },
    destroyTinymce() {
      const tinymce = window.tinymce.get(this.tinymceId)
      if (this.fullscreen) {
        tinymce.execCommand('mceFullScreen')
      }

      if (tinymce) {
        tinymce.destroy()
      }
    },
    setContent(value) {
      window.tinymce.get(this.tinymceId).setContent(value)
    },
    getContent() {
      window.tinymce.get(this.tinymceId).getContent()
    },
    imageSuccessCBK(arr) {
      arr.forEach(v => window.tinymce.get(this.tinymceId).insertContent(`<img class="wscnph" src="${v}" >`))
    }
  }
}
</script>

<style lang="scss" scoped>
.tinymce-container {
  position: relative;
  line-height: normal;
}

.tinymce-container {
  ::v-deep {
    .mce-fullscreen {
      z-index: 10000;
    }
  }
}

.tinymce-textarea {
  visibility: hidden;
  z-index: -1;
}

.editor-custom-btn-container {
  position: absolute;
  right: 4px;
  top: 4px;
  z-index: 2005;
}

.fullscreen .editor-custom-btn-container {
  z-index: 10000;
  position: fixed;
}

.editor-upload-btn {
  display: inline-block;
}
</style>
