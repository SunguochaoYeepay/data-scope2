<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { useQueryStore } from '@/stores/query';
import { useMessageStore } from '@/stores/message';
import QueryParamsConfig from './QueryParamsConfig.vue';
import QueryPreview from './QueryPreview.vue';

// 组件属性
const props = defineProps<{
  modelValue: string;
  label?: string;
  placeholder?: string;
  error?: string;
  required?: boolean;
  disabled?: boolean;
  showPreview?: boolean; // 是否显示预览功能
}>();

// 组件事件
const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
  (e: 'selected', id: string, data: any): void;
  (e: 'paramsChange', params: Record<string, any>): void;
}>();

// Store
const queryStore = useQueryStore();
const message = useMessageStore();

// 状态
const queries = ref<Array<{ id: string; name: string; description?: string; type: string; }>>([]);
const loading = ref(false);
const searchText = ref('');
const selectedQueryId = ref(props.modelValue || '');

// 参数配置相关
const showParamsConfig = ref(false);
const paramValues = ref<Record<string, any>>({});
const paramsValid = ref(true);

// 预览相关
const showPreview = ref(false);
const previewPageSize = ref(10);

// 计算属性
const filteredQueries = computed(() => {
  if (!searchText.value) {
    return queries.value;
  }
  
  const searchLower = searchText.value.toLowerCase();
  return queries.value.filter(query => {
    return query.name.toLowerCase().includes(searchLower) ||
           (query.description && query.description.toLowerCase().includes(searchLower)) ||
           query.id.toLowerCase().includes(searchLower);
  });
});

// 监听selectedQueryId变化
watch(selectedQueryId, (newValue) => {
  emit('update:modelValue', newValue);
  
  // 查找选中的查询数据
  const selectedQuery = queries.value.find(q => q.id === newValue);
  if (selectedQuery) {
    emit('selected', newValue, selectedQuery);
  }
  
  // 重置参数值
  if (newValue) {
    paramValues.value = {};
  }
});

// 监听modelValue变化
watch(() => props.modelValue, (newValue) => {
  selectedQueryId.value = newValue;
});

// 生命周期钩子
onMounted(async () => {
  await loadQueries();
});

// 加载查询列表
const loadQueries = async () => {
  loading.value = true;
  
  try {
    const result = await queryStore.fetchQueries({ type: 'DATA' });
    
    if (result) {
      queries.value = result.map(query => ({
        id: query.id,
        name: query.name,
        description: query.description,
        type: query.type
      }));
    }
  } catch (error) {
    console.error('加载查询列表失败', error);
    message.error('加载查询列表失败');
  } finally {
    loading.value = false;
  }
};

// 刷新查询列表
const refreshQueries = async () => {
  await loadQueries();
};

// 查询选择变更处理
const handleQueryChange = (event: Event) => {
  const target = event.target as HTMLSelectElement;
  selectedQueryId.value = target.value;
};

// 处理参数变化
const handleParamsChange = (values: Record<string, any>) => {
  paramValues.value = values;
  emit('paramsChange', values);
};

// 处理参数验证状态变化
const handleParamsValidChange = (valid: boolean) => {
  paramsValid.value = valid;
};

// 切换预览显示
const togglePreview = () => {
  showPreview.value = !showPreview.value;
};
</script>

<template>
  <div class="query-selector">
    <label v-if="props.label" :for="'query-selector-' + (Math.random().toString(36).substring(2))" class="block text-sm font-medium text-gray-700 mb-1">
      {{ props.label }}
      <span v-if="props.required" class="text-red-500">*</span>
    </label>
    
    <div class="relative">
      <div v-if="loading" class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
        <i class="fas fa-circle-notch fa-spin text-gray-400"></i>
      </div>
      
      <select
        :value="selectedQueryId"
        @change="handleQueryChange"
        class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
        :class="{ 'pr-10': loading, 'border-red-300': props.error }"
        :placeholder="props.placeholder || '请选择数据查询'"
        :disabled="props.disabled || loading"
      >
        <option value="">请选择数据查询</option>
        <optgroup v-if="filteredQueries.length > 0" label="可用查询">
          <option 
            v-for="query in filteredQueries" 
            :key="query.id" 
            :value="query.id"
          >
            {{ query.name }}
          </option>
        </optgroup>
      </select>
      
      <div v-if="!loading" class="absolute inset-y-0 right-0 pr-3 flex items-center space-x-2">
        <button 
          v-if="selectedQueryId"
          type="button"
          @click="showParamsConfig = !showParamsConfig"
          class="text-gray-400 hover:text-gray-500 focus:outline-none"
          title="配置参数"
        >
          <i class="fas fa-cog"></i>
        </button>
        
        <button 
          v-if="props.showPreview && selectedQueryId"
          type="button"
          @click="togglePreview"
          class="text-gray-400 hover:text-gray-500 focus:outline-none"
          title="预览数据"
        >
          <i class="fas fa-eye"></i>
        </button>
        
        <button 
          type="button" 
          @click="refreshQueries"
          class="text-gray-400 hover:text-gray-500 focus:outline-none"
          title="刷新查询列表"
        >
          <i class="fas fa-sync-alt"></i>
        </button>
      </div>
    </div>
    
    <div v-if="props.error" class="mt-1 text-sm text-red-600">
      {{ props.error }}
    </div>
    
    <!-- 查询信息展示 -->
    <div v-if="selectedQueryId && queries.length > 0" class="mt-2 p-3 bg-gray-50 rounded-md border border-gray-200">
      <div v-if="filteredQueries.find(q => q.id === selectedQueryId)" class="text-sm">
        <div class="font-medium text-gray-700">
          {{ filteredQueries.find(q => q.id === selectedQueryId)?.name }}
        </div>
        <div v-if="filteredQueries.find(q => q.id === selectedQueryId)?.description" class="text-gray-500 mt-1">
          {{ filteredQueries.find(q => q.id === selectedQueryId)?.description }}
        </div>
        <div class="text-xs text-gray-500 mt-2">
          ID: {{ selectedQueryId }}
        </div>
      </div>
    </div>
    
    <!-- 参数配置面板 -->
    <div v-if="showParamsConfig && selectedQueryId" class="mt-4">
      <div class="bg-white shadow sm:rounded-lg">
        <div class="px-4 py-5 sm:p-6">
          <h3 class="text-lg leading-6 font-medium text-gray-900">
            查询参数配置
          </h3>
          <div class="mt-4">
            <QueryParamsConfig
              :query-id="selectedQueryId"
              v-model="paramValues"
              @valid="handleParamsValidChange"
            />
          </div>
        </div>
      </div>
    </div>
    
    <!-- 预览面板 -->
    <div v-if="showPreview && selectedQueryId" class="mt-4">
      <div class="bg-white shadow sm:rounded-lg">
        <div class="px-4 py-5 sm:p-6">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg leading-6 font-medium text-gray-900">
              数据预览
            </h3>
            <button
              type="button"
              @click="togglePreview"
              class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-md text-gray-700 bg-gray-100 hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500"
            >
              <i class="fas fa-times mr-1"></i>
              关闭预览
            </button>
          </div>
          
          <div class="mt-4">
            <QueryPreview
              :query-id="selectedQueryId"
              :params="paramValues"
              :page-size="previewPageSize"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>