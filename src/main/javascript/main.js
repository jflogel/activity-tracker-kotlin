import routes from './routes';

const router = new VueRouter({
  routes,
  linkExactActiveClass: 'active'
})

const app = new Vue({
    router
}).$mount('#app')