//--------------------------头部导航栏变换随着滑动颜色 START---------------------
const nav = document.querySelector('nav')
window.addEventListener('scroll', fixNav)

function fixNav() {
  if(window.scrollY > nav.offsetHeight + 150) {
    nav.classList.add('active')
  } else {
    nav.classList.remove('active')
  }
}
//--------------------------头部导航栏变换随着滑动颜色 END---------------------

//-----------------------让文章条目动画出入 START----------------------------------
const boxes = document.querySelectorAll('.box')
window.addEventListener('scroll', checkBoxes)
checkBoxes()

function checkBoxes() {
  const triggerBottom = window.innerHeight / 5 * 4
  boxes.forEach(box => {
    const boxTop = box.getBoundingClientRect().top

    if(boxTop < triggerBottom) {
      box.classList.add('show')
    } else {
      box.classList.remove('show')
    }
  })
}
//-----------------------让文章条目动画出入 END----------------------------------

//---------------------------------------------页切换 START---------------------------------------------
$("#passPageBtn").click(function (event) {
  toPage(event.target.value)
})
$("#nextPageBtn").click(function (event) {
  toPage(event.target.value)
})

function toPage(pageNum) {
  let uri = "/article/page/"+pageNum
  let keys = $("#search").val()
  if(keys.trim().length !== 0) {
    keys = keys.replaceAll(" ", ",") //替换为英文状态下的逗号
    uri += "?query="+keys
  }

  location.assign(location.protocol + "//" + location.host + uri) //跳转到该页
}
//---------------------------------------------页切换 END---------------------------------------------
