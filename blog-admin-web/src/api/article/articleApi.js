import request from '@/utils/request'

function add(data) {
  return request({
    url: '/article',
    method: 'post',
    data
  })
}

function del(ids) {
  return request({
    url: '/article',
    method: 'delete',
    params: {ids}
  })
}

function update(data) {
  return request({
    url: 'article',
    method: 'put',
    data
  })
}

function list(data) {
  return request({
    url: '/article/list',
    method: 'post',
    data
  })
}

function get(id) {
  return request({
    url: '/article',
    method: 'get',
    params: { id }
  })
}


export default {add, del, update, list, get}
