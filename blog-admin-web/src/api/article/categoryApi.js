import axios from '@/utils/request'
import request from "@/utils/request";

function add(data) {
  return axios({
    url: '/article/category',
    method: 'POST',
    data: {
      ...data
    }
  })
}

function del(ids) {
  return axios({
    url: '/article/category',
    method: 'DELETE',
    params: {
      ids
    }
  })
}

function update(data) {
  return axios({
    url: '/article/category',
    method: 'PUT',
    data: {
      ...data
    }
  })
}

function fetch(id) {
  return request({
    url: '/article/category',
    method: 'get',
    params: { id }
  })
}

function all() {
  return axios({
    url: '/article/category/all',
    method: 'GET',
  })
}

function list(queryInfo) {
  return axios({
    url: '/article/category/list',
    method: 'POST',
    data: {
      ...queryInfo
    }
  })
}

export default {add, del, update, fetch, list, all}
