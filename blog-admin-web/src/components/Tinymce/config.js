
const plugins = 'print preview paste importcss searchreplace autolink autosave save directionality ' +
    ' code visualblocks visualchars fullscreen image link media template codesample table charmap ' +
    ' hr pagebreak nonbreaking anchor toc insertdatetime advlist lists wordcount imagetools textpattern ' +
    ' noneditable help charmap quickbars emoticons colorpicker '

const menubar = 'file edit view insert format tools table help'

const toolbar = 'undo redo | bold italic underline strikethrough forecolor backcolor subscript superscript removeformat | ' +
    ' blockquote codesample fontselect fontsizeselect formatselect |' +
    ' alignleft aligncenter alignright alignjustify outdent indent numlist bullist | ' +
    ' pagebreak nonbreaking hr ltr rtl visualblocks visualchars | ' +
    ' charmap emoticons link unlink openlink | fullscreen code preview save print | ' +
    ' insertfile image editimage imageoptions media template link anchor toc | '

/*
 * 选中一段文本后弹出来的快捷文本控制
 */
const quickToolbar = 'bold italic | quicklink h1 h2 h3 h4 blockquote quickimage quicktable'

/*
 * 定义codeSample支持那些格式的代码，配合Prism使用
 */
const codeSampleLanguages = [
  {text: 'Java', value: 'java line-numbers'},
  {text: 'JavaScript', value: 'javascript line-numbers'},
  {text: 'JSON', value: 'json line-numbers'},
  {text: 'CSS', value: 'css line-numbers'},
  {text: 'HTML', value: 'html line-numbers'},
  {text: 'C', value: 'c line-numbers'},
  {text: 'C++', value: 'cpp line-numbers'},
  {text: 'LaTeX', value: 'LaTeX line-numbers'},
  {text: 'SQL', value: 'sql line-numbers'},
  {text: 'Python', value: 'python line-numbers'},
  {text: 'Lua', value: 'lua line-numbers'},
  {text: 'Bash', value: 'Bash line-numbers'},
  {text: 'Scala', value: 'Scala line-numbers'},
  {text: 'XML', value: 'XML line-numbers'},
  {text: 'YAML', value: 'YAML line-numbers'},
  {text: 'Properties', value: 'Properties line-numbers'},
];

/**
 * 快速排版（自动替换）
 */
let textPatterns = [
  {start: '*', end: '*', format: 'italic'},
  {start: '**', end: '**', format: 'bold'},
  {start: '#', format: 'h1'},
  {start: '##', format: 'h2'},
  {start: '###', format: 'h3'},
  {start: '####', format: 'h4'},
  {start: '#####', format: 'h5'},
  {start: '######', format: 'h6'},
  {start: '1. ', cmd: 'InsertOrderedList'},
  {start: '* ', cmd: 'InsertUnorderedList'},
  {start: '- ', cmd: 'InsertUnorderedList' }
];

let fontsizeFormats = '12px 14px 16px 18px 24px 36px 48px 56px 72px';
let fontFormats =
  '微软雅黑=Microsoft YaHei UI' +
  '苹果苹方=PingFang SC' +
  '宋体=simsun,serif; 仿宋体=FangSong,serif;' +
  '黑体=SimHei,sans-serif;' +
  'Arial=arial,helvetica,sans-serif;Arial Black=arial black,avant garde;' +
  'Book Antiqua=book antiqua,palatino;';

export default {plugins, menubar, toolbar, quickToolbar,
  codeSampleLanguages, textPatterns,
  fontsizeFormats, fontFormats}

