import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import 'normalize.css/normalize.css'; // A modern alternative to CSS resets
import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import './styles/global.scss';
import './styles/antd-theme.scss';
import { register as registerServiceWorker } from './registerServiceWorker';

Vue.use(Antd);

Vue.config.productionTip = false

// 注册Service Worker
registerServiceWorker();

// 创建Vue实例
new Vue({
  router,
  store,
  render: function (h) { return h(App) }
}).$mount('#app')
