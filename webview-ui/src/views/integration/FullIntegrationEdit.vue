<!-- 完整版集成编辑页面 -->
<template>
  <div class="p-4 sm:p-6 bg-white">
    <!-- 页面标题和模式切换 -->
    <div class="flex justify-between items-center mb-6">
      <div class="flex items-center">
        <h1 class="text-xl font-bold text-gray-900">{{ isCreateMode ? '创建集成' : '编辑集成' }}</h1>
        <div class="ml-4 flex items-center">
          <span class="text-sm text-gray-500 mr-2">简化模式</span>
          <button
            type="button"
            class="relative inline-flex flex-shrink-0 h-6 w-11 border-2 border-transparent rounded-full cursor-pointer transition-colors ease-in-out duration-200 focus:outline-none"
            :class="[isSimpleMode ? 'bg-gray-200' : 'bg-indigo-600']"
            @click="toggleMode"
          >
            <span
              class="pointer-events-none inline-block h-5 w-5 rounded-full bg-white shadow transform ring-0 transition ease-in-out duration-200"
              :class="[isSimpleMode ? 'translate-x-0' : 'translate-x-5']"
            />
          </button>
          <span class="text-sm text-gray-500 ml-2">完整模式</span>
        </div>
      </div>
      <div class="flex items-center">
        <button
          type="button"
          class="mr-3 py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none"
          @click="cancelEdit"
        >
          取消
        </button>
        <button
          type="button"
          class="py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none"
          @click="saveIntegration"
        >
          保存
        </button>
      </div>
    </div>

    <!-- 表单内容 -->
    <div class="space-y-8">
      <!-- 基本信息 -->
      <div>
        <h2 class="text-lg font-medium text-gray-900 mb-4">基本信息</h2>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
          <!-- 名称 -->
          <div>
            <label for="name" class="block text-sm font-medium text-gray-700 mb-1">
              集成名称 <span class="text-red-500">*</span>
            </label>
            <input 
              id="name"
              v-model="integration.name"
              type="text"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
              placeholder="请输入集成名称"
            />
          </div>
          
          <!-- 状态 -->
          <div>
            <label for="status" class="block text-sm font-medium text-gray-700 mb-1">
              状态
            </label>
            <select
              id="status"
              v-model="integration.status"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
            >
              <option value="DRAFT">草稿</option>
              <option value="ACTIVE">已激活</option>
              <option value="INACTIVE">已禁用</option>
            </select>
          </div>
        </div>
        
        <!-- 描述 -->
        <div>
          <label for="description" class="block text-sm font-medium text-gray-700 mb-1">
            描述
          </label>
          <textarea
            id="description"
            v-model="integration.description"
            rows="3"
            class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
            placeholder="请输入集成描述"
          ></textarea>
        </div>
      </div>
      
      <!-- 数据配置 -->
      <div>
        <h2 class="text-lg font-medium text-gray-900 mb-4">数据配置</h2>
        
        <!-- 查询选择器 -->
        <div class="mb-6">
          <QuerySelectorEnhanced
            v-model="integration.queryId"
            :show-preview="true"
            @update:modelValue="handleQueryChange"
          />
        </div>

        <!-- 集成类型选择 -->
        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-2">集成类型</label>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div
              v-for="type in integrationTypes"
              :key="type.value"
              class="relative rounded-lg border p-4 cursor-pointer hover:border-indigo-500"
              :class="[
                integration.type === type.value
                  ? 'border-indigo-500 bg-indigo-50'
                  : 'border-gray-300'
              ]"
              @click="selectIntegrationType(type.value)"
            >
              <div class="flex items-center justify-between">
                <div class="flex items-center">
                  <div class="flex-shrink-0">
                    <component :is="type.icon" class="h-6 w-6" />
                  </div>
                  <div class="ml-3">
                    <h3 class="text-sm font-medium text-gray-900">
                      {{ type.label }}
                    </h3>
                    <p class="text-sm text-gray-500">{{ type.description }}</p>
                  </div>
                </div>
                <div v-if="integration.type === type.value" class="flex-shrink-0">
                  <svg class="h-5 w-5 text-indigo-500" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                  </svg>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 表格配置 -->
        <div v-if="integration.type === 'TABLE'" class="mb-6">
          <div class="bg-white rounded-lg border border-gray-200">
            <div class="p-6">
              <TableConfigEditor
                v-model="integration.tableConfig"
                :queryId="integration.queryId"
              />
            </div>
          </div>
        </div>

        <!-- 表单配置 -->
        <div v-if="integration.type === 'FORM'" class="mb-6">
          <div class="bg-white rounded-lg border border-gray-200">
            <div class="p-6">
              <FormConfigEditor
                v-model="integration.formConfig"
                :queryId="integration.queryId"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useIntegrationStore } from '@/stores/integration';
import { useMessageStore } from '@/stores/message';
import type { Integration } from '@/types/integration';
import QuerySelectorEnhanced from '@/components/integration/QuerySelectorEnhanced.vue';
import TableConfigEditor from '@/components/integration/TableConfigEditor.vue';
import FormConfigEditor from '@/components/integration/FormConfigEditor.vue';

const route = useRoute();
const router = useRouter();
const integrationStore = useIntegrationStore();
const messageStore = useMessageStore();

// 是否为简化模式
const isSimpleMode = ref(false);

// 是否为创建模式
const isCreateMode = computed(() => route.path.includes('/create'));

// 集成类型选项
const integrationTypes = [
  {
    value: 'TABLE',
    label: '表格集成',
    description: '创建带查询条件的数据表格，支持高级过滤、导出等功能',
    icon: 'TableIcon'
  },
  {
    value: 'FORM',
    label: '表单集成',
    description: '创建数据录入表单，支持字段验证、联动等功能',
    icon: 'FormIcon'
  }
];

// 集成信息
const integration = reactive<Integration>({
  id: '',
  name: '',
  description: '',
  type: 'TABLE', // 默认选择表格类型
  status: 'DRAFT',
  queryId: '',
  formConfig: {
    layout: 'vertical',
    conditions: [],
    buttons: []
  },
  tableConfig: {
    columns: [],
    actions: [],
    pagination: {
      enabled: true,
      pageSize: 10,
      pageSizeOptions: [10, 20, 50, 100]
    },
    export: {
      enabled: true,
      formats: ['CSV', 'EXCEL'],
      maxRows: 1000
    },
    batchActions: [],
    aggregation: {
      enabled: false,
      groupByFields: [],
      aggregationFunctions: []
    },
    advancedFilters: {
      enabled: true, // 默认启用高级过滤
      defaultFilters: [],
      savedFilters: []
    }
  },
  integrationPoint: {
    id: '',
    name: '',
    type: 'URL',
    urlConfig: {
      url: '',
      method: 'GET',
      headers: {}
    }
  },
  createTime: '',
  updateTime: ''
});

// 切换模式
const toggleMode = () => {
  isSimpleMode.value = !isSimpleMode.value;
  if (isSimpleMode.value) {
    router.push('/integration/simple' + (isCreateMode.value ? '/create' : '/edit/' + route.params.id));
  }
};

// 选择集成类型
const selectIntegrationType = (type: string) => {
  integration.type = type;
};

// 处理查询变更
const handleQueryChange = async (queryId: string) => {
  if (queryId) {
    integration.queryId = queryId;
    // 可以在这里添加其他逻辑，比如根据查询自动配置表格列等
  }
};

// 取消编辑
const cancelEdit = () => {
  router.push('/integration/list');
};

// 保存集成
const saveIntegration = async () => {
  try {
    if (!integration.name) {
      messageStore.error('请输入集成名称');
      return;
    }

    if (!integration.queryId) {
      messageStore.error('请选择数据查询');
      return;
    }

    const result = isCreateMode.value
      ? await integrationStore.createIntegration(integration)
      : await integrationStore.updateIntegration(integration);

    if (result) {
      messageStore.success(isCreateMode.value ? '创建成功' : '保存成功');
      router.push('/integration/list');
    }
  } catch (error) {
    console.error('保存集成失败', error);
    messageStore.error('保存失败');
  }
};

// 生命周期钩子
onMounted(async () => {
  if (!isCreateMode.value && route.params.id) {
    try {
      const result = await integrationStore.fetchIntegrationById(route.params.id as string);
      if (result) {
        Object.assign(integration, result);
      }
    } catch (error) {
      console.error('加载集成失败', error);
      messageStore.error('加载失败');
      router.push('/integration/list');
    }
  }
});
</script>