
//-----------------------------------分类下的文章搜索 START----------------------------------
$("#categorySearchBtn").click(function () {
  let categoryCode = document.getElementById("categoryCode").innerText
  let uri = "/category/" +categoryCode+ "/1"
  let keys = $("#categorySearchInput").val()
  if(keys.trim().length !== 0) {
    keys = keys.replaceAll(" ", ",") //替换为英文状态下的逗号
    uri += "?categoryKeys="+keys
  }

  location.assign(location.protocol + "//" + location.host + uri) //跳转到该页
})
//-----------------------------------分类下的文章搜索 END-----------------------------------


//---------------------------------------------页切换 START---------------------------------------------
$("#categoryPagePass").click(function (event) {
  toPage(event.target.value)
})
$("#categoryPageNext").click(function (event) {
  toPage(event.target.value)
})

function toPage(pageNum) {
  let categoryCode = document.getElementById("categoryCode").innerText

  let uri = "/category/"+categoryCode+"/"+pageNum
  let keys = $("#categorySearchInput").val()
  if(keys.trim().length !== 0) {
    keys = keys.replaceAll(" ", ",") //替换为英文状态下的逗号
    uri += "?categoryKeys="+keys
  }

  location.assign(location.protocol + "//" + location.host + uri) //跳转到该页
}
//---------------------------------------------页切换 END---------------------------------------------

