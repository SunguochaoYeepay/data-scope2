import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './styles/index.css'
import { initQueryTemplates } from './services/queryTemplates'

// 引入Font Awesome
import '@fortawesome/fontawesome-free/css/all.min.css'

// 初始化查询模板
initQueryTemplates()

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')