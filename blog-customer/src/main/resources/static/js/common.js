// ------------------------------------获取浏览器的信息，标识浏览器的唯一性 START ------------------------------------
//生成一个GUID来唯一标识此浏览器，存放于localStorage
//将GUID放于Cookie，每次请求都带上
function browserUniId() {
  let browserId = localStorage.getItem("browserId")
  if(browserId == null || browserId === '') {
    let uid = guid()
    //let userAgent = navigator.userAgent
    //userAgent = userAgent.replaceAll(" ", "")
    browserId = uid
    localStorage.setItem("browserId", uid)
  }

  //放入Guid到Cookie
  document.cookie="browserId="+browserId;

  return browserId
}

// Generate four random hex digits.
function S4() {
  return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
}
// Generate a pseudo-GUID by concatenating random hexadecimal.
function guid() {
  return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
}
// ------------------------------------获取浏览器的信息，标识浏览器的唯一性 NED ------------------------------------

//-----------------------------------文章搜索 START----------------------------------
$("#searchBtn").click(function () {
  let uri = "/article/page/1"

  let keys = $("#search").val()
  if(keys.trim().length !== 0) {
    keys = keys.replaceAll(" ", ",") //替换为英文状态下的逗号
    uri += "?query="+keys
  }

  location.assign(location.protocol + "//" + location.host + uri) //跳转到该页
})

$("#searchBtn").dblclick(function () {
  $("#search").val("")
})
//-----------------------------------文章搜索 END-----------------------------------


// ------------------------------------回到顶部 START ------------------------------------
function scrollFunction() {
  if (document.body.scrollTop > 200 || document.documentElement.scrollTop > 200) {
    document.getElementById("backToTopBtn").style.display = "block";
  } else {
    document.getElementById("backToTopBtn").style.display = "none";
  }
}

function topFunction() {
  document.body.scrollTop = 0;
  document.documentElement.scrollTop = 0;
}
// ------------------------------------回到顶部 NED ------------------------------------

// ------------------------------------检查客户端的类型 START ------------------------------------
function clientType() {
  if (/(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent)) {
    //alert("iPhone")
    return "iPhone"
  } else if (/(Android)/i.test(navigator.userAgent)) {
    //alert("Android")
    return "Android"
  } else {
    return "PC"
  }
}
// ------------------------------------检查客户端的类型 NED ------------------------------------
