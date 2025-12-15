import axios from '@/utils/request'
import request from "@/utils/request";

function add(data) {
  return axios({
    url: '/article/author',
    method: 'POST',
    data: {
      ...data
    }
  })
}

function del(ids) {
  return axios({
    url: '/article/author',
    method: 'DELETE',
    params: {
      ids
    }
  })
}

function update(data) {
  return axios({
    url: '/article/author',
    method: 'PUT',
    data: {
      ...data
    }
  })
}

function fetch(id) {
  return request({
    url: '/article/author',
    method: 'get',
    params: { id }
  })
}

function all() {
  return axios({
    url: '/article/author/all',
    method: 'GET',
  })
}

function list(data) {
  return axios({
    url: '/article/author/list',
    method: 'POST',
    data: {
      ...data
    }
  })
}

export default {add, del, update, fetch, all, list}
