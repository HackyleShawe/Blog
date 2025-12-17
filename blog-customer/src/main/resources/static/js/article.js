/**
 * 目录生成器：TOCBOT
 */
tocbot.init({
  // 构建目录的容器
  tocSelector: 'aside',
  // 文章容器
  contentSelector: 'article',
  // 需要解析的标题
  headingSelector: 'h1, h2, h3, h4, h5, h6',
  //hasInnerContainers: true,
  // isCollapsedClass: 'is-collapsed',

  //跳转偏移
  scrollSmooth: true,
  scrollSmoothOffset: -80,
  headingsOffset: -500,

  //滚动跟随
  //positionFixedSelector: '.toc',
  //positionFixedClass: 'is-position-fixed',
  //fixedSidebarOffset: 'auto',

  collapseDepth: 6, //折叠深度
});

/**
 * 富文本编辑器TinyMCE
 */
tinymce.init({
  selector: 'textarea#commentTextarea',
  plugins: 'preview importcss searchreplace autolink autosave save directionality code ' +
      'visualblocks visualchars fullscreen image link media template codesample table ' +
      'charmap pagebreak nonbreaking anchor insertdatetime advlist lists wordcount help ' +
      'charmap quickbars emoticons',
  menubar: 'file edit view format tools table help', //insert
  toolbar: 'undo redo | bold italic underline strikethrough | numlist bullist forecolor backcolor removeformat |' +
      ' codesample charmap emoticons | outdent indent alignleft aligncenter alignright alignjustify | ' +
      ' fontfamily fontsize blocks fullscreen  preview ltr rtl ',
  height: 260,
  toolbar_mode: 'sliding',
});

//---------------------------------------------顶级评论 START---------------------------------------------
//清除所有输入框内的内容
$("#resetComment").click(function () {
  $("#name").val('')
  $("#email").val('')
  $("#link").val('')
  //tinyMCE.activeEditor.setContent('')
  tinyMCE.get('commentTextarea').setContent('');
})
//新增评论，提交到后端
let submitComment = $("#submitComment");
submitComment.click(function () {
  let name = $("#name").val()
  let email = $("#email").val()
  let link = $("#link").val()

  //let content = tinyMCE.activeEditor.getContent() //获取TinyMCE编辑器中的内容
  let content = tinyMCE.get('commentTextarea').getContent();
  postComment(name, email, link, content)
});

//推送评论到后端
//rootId：根评论；pid：父评论
function postComment(name, email, link, content, rootId, pid) {
  //入参校验，必填项校验
  if(name === '' || email === '' || content === '') {
    alert("NAME或EMAIL或评论内容未填，请填入")
    return
  }
  if(name.length < 2) {
    alert("NAME的长度不能小于2")
    return
  }
  if(email) {
    let emailRegx = new RegExp(/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/)
    if(!emailRegx.test(email)) {
      alert("EMAIL不合法")
      return;
    }
  }
  if(name.content < 1) {
    alert("CONTENT的长度不能小于2")
    return;
  }

  // if(pid === undefined || pid === '') {
  //   pid = ''
  // }

  //文章ID
  let articleId = document.getElementById("articleId").innerText

  let paramData = {
    commentatorName: name,
    commentatorEmail: email,
    commentatorLink: link,
    content: content,
    articleId: articleId,
    rootId: rootId,
    pid: pid
  }
  console.log("paramData: ", paramData)

  //提交评论
  $.post("/comment", paramData, function (resp) {
    console.log(resp)
    if(200 === resp.code) {
      //响应成功
      $("#name").val('')
      $("#email").val('')
      $("#link").val('')
      //tinyMCE.activeEditor.setContent('')
      tinyMCE.get('commentTextarea').setContent('');
      alert("提交成功，请等待管理员审核通过后呈现！")
    } else {
      alert(resp.msg)
    }
  }, 'json')
}
//---------------------------------------------顶级评论 END---------------------------------------------

//---------------------------------------------子评论 START---------------------------------------------
//子评论组件
let commentEle = "<div class=\"add-child-comment-container\" id=\"addReply\">" +
    "  <p style=\"text-align: center; font-size: large\">回复评论</p>" +
    "  <div>" +
    "    <p>" +
    "      Name:&emsp;<input type=\"text\" id=\"nameReply\" size=\"35\" class=\"form-input\">" +
    "    </p>" +
    "    <p>" +
    "      Email:&emsp;<input type=\"email\" id=\"emailReply\" size=\"35\" class=\"form-input\">" +
    "    </p>" +
    "    <p>" +
    "      Link:&emsp;<input type=\"text\" id=\"linkReply\" size=\"35\" class=\"form-input\">" +
    "    </p>" +
    "  </div>" +
    "" +
    "  <p style='margin: 5px auto'>" +
    "    <textarea name=\"replyComment\" id=\"replyCommentTextarea\" placeholder=\"Input reply comment, please\">" +
    "    </textarea>" +
    "  </p>" +
    "" +
    "  <div style=\"text-align: center; margin: 5px 5px\">" +
    "    <button id=\"submitReplyComment\" type=\"submit\" style=\"margin: 2px 5px; background-color:green\">SUBMIT</button>" +
    "    <button id=\"resetReplyComment\" type=\"reset\" style=\"margin: 2px 5px; background-color: red\">RESET</button>" +
    "  </div>" +
    "</div>";

//全局保存当前评论的父ID与子ID
let commentRootId = 0
let commentPid = 0

//弹出回复父评论的评论框
$(".reply2parent").click(function (event) {
  console.log("reply2parent", event.target)
  //获取到是那个组件触发了这个事件
  let parentEle = event.target
  let parentId = parentEle.id
  commentRootId = parentId
  commentPid = parentId //评论父评论，回复的就是它自己

  let addReply = $("#addReply").html()
  if(addReply === undefined || addReply === '') {
    //追加Document Element
    $("#reply2parentAfter"+parentId).append(commentEle)

    //初始化数据的输入框
    tinymce.init({
      selector: 'textarea#replyCommentTextarea',
      plugins: 'preview importcss searchreplace autolink autosave save directionality code ' +
          'visualblocks visualchars fullscreen image link media template codesample table ' +
          'charmap pagebreak nonbreaking anchor insertdatetime advlist lists wordcount help ' +
          'charmap quickbars emoticons',
      menubar: 'file edit view format tools table help', //insert
      toolbar: 'undo redo | bold italic underline strikethrough | numlist bullist forecolor backcolor removeformat |' +
          ' codesample charmap emoticons | outdent indent alignleft aligncenter alignright alignjustify | ' +
          ' fontfamily fontsize blocks fullscreen  preview ltr rtl ',
      height: 260,
      toolbar_mode: 'sliding',
    });
  } else {
    $("#addReply").remove()
    tinyMCE.get('replyCommentTextarea').remove()
  }
})

//弹出回复子评论的评论框
$(".reply2sub").click(function (event) {
  console.log("reply2sub", event.target)

  //获取到是那个组件触发了这个事件
  let subEle = event.target
  let subId = subEle.id
  commentRootId = subEle.getAttribute("rid")
  commentPid = subEle.id //评论子评论，回复的是它自己

  let addReply = $("#addReply").html()
  if(addReply === undefined || addReply === '') {
    //追加Document Element
    $("#reply2subAfter"+subId).append(commentEle)
    //初始化数据的输入框
    tinymce.init({
      selector: 'textarea#replyCommentTextarea',
      plugins: 'preview importcss searchreplace autolink autosave save directionality code ' +
          'visualblocks visualchars fullscreen image link media template codesample table ' +
          'charmap pagebreak nonbreaking anchor insertdatetime advlist lists wordcount help ' +
          'charmap quickbars emoticons',
      menubar: 'file edit view format tools table help', //insert
      toolbar: 'undo redo | bold italic underline strikethrough | numlist bullist forecolor backcolor removeformat |' +
          ' codesample charmap emoticons | outdent indent alignleft aligncenter alignright alignjustify | ' +
          ' fontfamily fontsize blocks fullscreen  preview ltr rtl ',
      height: 260,
      toolbar_mode: 'sliding',
    });
  } else {
    $("#addReply").remove()
    //tinymce.remove('textarea#replyCommentTextarea')
    tinymce.get("replyCommentTextarea").remove();
  }
})

//清除所有输入框内的内容。使用on解决普通click事件无法捕捉动态新增的元素
$(".reply2parentHandle").on("click", "#resetReplyComment", function () {
  console.log("resetReplyComment")

  $("#nameReply").val('')
  $("#emailReply").val('')
  $("#linkReply").val('')
  //tinyMCE.activeEditor.setContent('')
  tinyMCE.get('replyCommentTextarea').setContent('');
})
//提交输入框内的内容
$(".reply2parentHandle").on("click", "#submitReplyComment", function () {
  console.log("submitReplyComment, commentRootId",commentRootId,", commentPid", commentPid)

  //抓取页面上的数据，传向后端
  let nameReply = $("#nameReply").val()
  let emailReply = $("#emailReply").val()
  let linkReply = $("#linkReply").val()

  //let content = tinyMCE.activeEditor.getContent() //获取TinyMCE编辑器中的内容
  let contentReply = tinyMCE.get('replyCommentTextarea').getContent();

  postComment(nameReply, emailReply, linkReply, contentReply, commentRootId, commentPid)

  //移除评论框
  let addReply = $("#addReply").html()
  if(addReply !== undefined || addReply !== '') {
    $("#addReply").remove()
    tinymce.get("replyCommentTextarea").remove();
  }
})
//---------------------------------------------子评论 END---------------------------------------------



