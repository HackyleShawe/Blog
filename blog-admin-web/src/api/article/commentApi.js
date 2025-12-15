import axios from '@/utils/request'

function add(data) {
  return axios({
    url: '/article/comment',
    method: 'POST',
    data: {
      ...data
    }
  })
}

function del(ids) {
  return axios({
    url: '/article/comment',
    method: 'DELETE',
    params: {
      ids
    }
  })
}

function update(data) {
  return axios({
    url: '/article/comment',
    method: 'PUT',
    data: {
      ...data
    }
  })
}

function list(data) {
  return axios({
    url: '/article/comment/list',
    method: 'POST',
    data: {
      ...data
    }
  })
}

//分页获取层级评论
function fetchListByHierarchy(data) {
  return axios({
    url: '/article/comment/fetchListByHierarchy',
    method: 'POST',
    data: {
      ...data
    }
  })
}

export default {add, del, update, list, fetchListByHierarchy}
