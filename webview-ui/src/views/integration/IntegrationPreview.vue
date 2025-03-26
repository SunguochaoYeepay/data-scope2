<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useIntegrationStore } from '@/stores/integration';
import { useMessageStore } from '@/stores/message';
import { storeToRefs } from 'pinia';
import type { Integration, FormConfig, TableConfig, FormCondition } from '@/types/integration';

// 路由相关
const route = useRoute();
const router = useRouter();
const integrationId = computed(() => route.params.id as string);

// Store
const integrationStore = useIntegrationStore();
const message = useMessageStore();
const { loading } = storeToRefs(integrationStore);

// 状态
const integration = ref<Integration | null>(null);
const formValues = ref<Record<string, any>>({});
const showSuccessMessage = ref(false);
const successMessage = ref('');
const showErrorMessage = ref(false);
const errorMessage = ref('');
const tableData = ref<any[]>([]);
const tableLoading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const totalRows = ref(0);

// 计算属性
const renderedFormConditions = computed(() => {
  if (!integration.value || integration.value.type !== 'FORM' || !integration.value.formConfig) {
    return [];
  }
  
  return integration.value.formConfig.conditions
    .filter(condition => condition.visibility !== 'hidden')
    .sort((a, b) => a.displayOrder - b.displayOrder);
});

const formLayout = computed(() => {
  if (!integration.value || integration.value.type !== 'FORM' || !integration.value.formConfig) {
    return 'vertical';
  }
  
  return integration.value.formConfig.layout;
});

const formClasses = computed(() => {
  if (formLayout.value === 'horizontal') {
    return 'grid grid-cols-1 md:grid-cols-2 gap-4';
  } else if (formLayout.value === 'vertical') {
    return 'space-y-4';
  } else {
    return 'grid grid-cols-1 md:grid-cols-3 gap-4';
  }
});

const formLabelClasses = computed(() => {
  if (formLayout.value === 'horizontal') {
    return 'block text-sm font-medium text-gray-700 mb-1';
  } else {
    return 'block text-sm font-medium text-gray-700 mb-1';
  }
});

const tableColumns = computed(() => {
  if (!integration.value || integration.value.type !== 'TABLE' || !integration.value.tableConfig) {
    return [];
  }
  
  return integration.value.tableConfig.columns
    .filter(column => column.visible)
    .sort((a, b) => a.displayOrder - b.displayOrder);
});

const tableActions = computed(() => {
  if (!integration.value || integration.value.type !== 'TABLE' || !integration.value.tableConfig) {
    return [];
  }
  
  return integration.value.tableConfig.actions || [];
});

const pagination = computed(() => {
  if (!integration.value || integration.value.type !== 'TABLE' || !integration.value.tableConfig) {
    return {
      enabled: true,
      pageSize: 10,
      pageSizeOptions: [10, 20, 50, 100]
    };
  }
  
  return integration.value.tableConfig.pagination;
});

const exportConfig = computed(() => {
  if (!integration.value || integration.value.type !== 'TABLE' || !integration.value.tableConfig) {
    return {
      enabled: true,
      formats: ['CSV', 'EXCEL'],
      maxRows: 1000
    };
  }
  
  return integration.value.tableConfig.export;
});

// 生命周期钩子
onMounted(async () => {
  if (integrationId.value) {
    await loadIntegration(integrationId.value);
  }
});

// 加载集成
const loadIntegration = async (id: string) => {
  try {
    const result = await integrationStore.fetchIntegrationById(id);
    
    if (result) {
      integration.value = result;
      
      // 初始化表单值
      if (result.type === 'FORM' && result.formConfig) {
        initFormValues(result.formConfig.conditions);
      }
      
      // 加载表格数据
      if (result.type === 'TABLE') {
        await loadTableData();
      }
    } else {
      message.error('未找到集成项');
      router.push('/integration');
    }
  } catch (error) {
    console.error('加载集成项失败', error);
    message.error('加载集成项失败');
    router.push('/integration');
  }
};

// 初始化表单值
const initFormValues = (conditions: FormCondition[]) => {
  const values: Record<string, any> = {};
  
  conditions.forEach(condition => {
    values[condition.field] = condition.defaultValue || '';
  });
  
  formValues.value = values;
};

// 加载表格数据
const loadTableData = async () => {
  if (!integration.value || !integration.value.queryId) return;
  
  tableLoading.value = true;
  
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    };
    
    // 调用查询接口
    const result = await integrationStore.executeQuery(integration.value.queryId, params);
    
    if (result) {
      tableData.value = result.data || [];
      totalRows.value = result.total || 0;
    }
  } catch (error) {
    console.error('加载表格数据失败', error);
    message.error('加载表格数据失败');
  } finally {
    tableLoading.value = false;
  }
};

// 提交表单
const submitForm = async () => {
  if (!integration.value) return;
  
  // 表单验证
  const hasErrors = validateForm();
  if (hasErrors) {
    message.error('请完善表单信息');
    return;
  }
  
  // 调用集成点
  if (integration.value.integrationPoint) {
    await callIntegrationPoint();
  } else {
    // 如果没有集成点，则直接显示成功消息
    showSuccessMessage.value = true;
    successMessage.value = '表单提交成功';
    
    // 3秒后自动隐藏
    setTimeout(() => {
      showSuccessMessage.value = false;
    }, 3000);
  }
};

// 表单验证
const validateForm = (): boolean => {
  if (!integration.value || integration.value.type !== 'FORM' || !integration.value.formConfig) {
    return false;
  }
  
  let hasErrors = false;
  
  // 检查必填字段
  integration.value.formConfig.conditions.forEach(condition => {
    if (condition.required && (!formValues.value[condition.field] || formValues.value[condition.field].trim() === '')) {
      hasErrors = true;
    }
  });
  
  return hasErrors;
};

// 调用集成点
const callIntegrationPoint = async () => {
  if (!integration.value || !integration.value.integrationPoint) return;
  
  try {
    const integrationPoint = integration.value.integrationPoint;
    
    if (integrationPoint.type === 'URL' && integrationPoint.urlConfig) {
      // 调用URL集成点
      const response = await integrationStore.callUrlIntegrationPoint(
        integrationPoint.urlConfig.url,
        integrationPoint.urlConfig.method,
        formValues.value,
        integrationPoint.urlConfig.headers
      );
      
      if (response && response.success) {
        showSuccessMessage.value = true;
        successMessage.value = response.message || '操作成功';
      } else {
        showErrorMessage.value = true;
        errorMessage.value = response.message || '操作失败';
      }
    } else if (integrationPoint.type === 'FORM_SUBMIT' && integrationPoint.formSubmitConfig) {
      // 调用表单提交集成点
      const response = await integrationStore.callFormSubmitIntegrationPoint(
        integrationPoint.formSubmitConfig.formId,
        integrationPoint.formSubmitConfig.submitAction,
        formValues.value
      );
      
      if (response && response.success) {
        showSuccessMessage.value = true;
        successMessage.value = integrationPoint.formSubmitConfig.successMessage || '提交成功';
      } else {
        showErrorMessage.value = true;
        errorMessage.value = integrationPoint.formSubmitConfig.errorMessage || '提交失败';
      }
    }
    
    // 3秒后自动隐藏消息
    setTimeout(() => {
      showSuccessMessage.value = false;
      showErrorMessage.value = false;
    }, 3000);
  } catch (error) {
    console.error('调用集成点失败', error);
    showErrorMessage.value = true;
    errorMessage.value = '调用集成点失败';
    
    // 3秒后自动隐藏错误消息
    setTimeout(() => {
      showErrorMessage.value = false;
    }, 3000);
  }
};

// 重置表单
const resetForm = () => {
  if (!integration.value || integration.value.type !== 'FORM') return;
  
  initFormValues(integration.value.formConfig.conditions);
};

// 处理表格操作
const handleTableAction = (action: any, row: any) => {
  console.log('表格操作:', action, row);
  
  if (action.handler) {
    try {
      // 动态执行处理函数
      const fn = new Function('row', `return ${action.handler}(row)`);
      fn(row);
    } catch (error) {
      console.error('执行操作处理函数失败', error);
      message.error('执行操作失败');
    }
  }
};

// 处理页码变更
const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadTableData();
};

// 处理每页条数变更
const handlePageSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
  loadTableData();
};

// 导出表格数据
const exportTable = (format: string) => {
  if (!integration.value) return;
  
  message.info(`正在导出${format === 'CSV' ? 'CSV' : 'Excel'}文件...`);
  
  // 实际导出逻辑
  // ...
};

// 获取字段对齐样式
const getColumnAlignClass = (align: string): string => {
  switch (align) {
    case 'center':
      return 'text-center';
    case 'right':
      return 'text-right';
    default:
      return 'text-left';
  }
};

// 返回编辑页面
const goToEdit = () => {
  if (!integration.value) return;
  
  router.push(`/integration/edit/${integration.value.id}`);
};

// 返回列表页面
const goToList = () => {
  router.push('/integration');
};
</script>

<template>
  <div class="integration-preview">
    <div class="page-header mb-6">
      <div class="flex justify-between items-center">
        <h1 class="text-2xl font-bold text-gray-900">
          预览集成
        </h1>
        <div class="flex space-x-2">
          <button 
            @click="goToList"
            class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            <i class="fas fa-arrow-left mr-2"></i>
            返回列表
          </button>
          <button 
            @click="goToEdit"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            <i class="fas fa-edit mr-2"></i>
            编辑集成
          </button>
        </div>
      </div>
    </div>
    
    <!-- 加载中 -->
    <div v-if="loading" class="bg-white shadow rounded-lg p-10 text-center">
      <i class="fas fa-circle-notch fa-spin text-indigo-500 text-3xl mb-4"></i>
      <p class="text-gray-500">正在加载集成数据...</p>
    </div>
    
    <!-- 集成信息 -->
    <div v-else-if="integration" class="bg-white shadow rounded-lg overflow-hidden">
      <!-- 集成头部信息 -->
      <div class="bg-gray-50 p-4 border-b border-gray-200">
        <div class="flex items-center">
          <h2 class="text-lg font-medium text-gray-900 flex-1">{{ integration.name }}</h2>
          <span 
            :class="[
              'px-2 py-1 text-xs font-semibold rounded-full',
              integration.status === 'ACTIVE' ? 'bg-green-100 text-green-800' :
              integration.status === 'INACTIVE' ? 'bg-gray-100 text-gray-800' :
              'bg-yellow-100 text-yellow-800'
            ]"
          >
            {{ integration.status === 'ACTIVE' ? '已激活' : 
               integration.status === 'INACTIVE' ? '已停用' : '草稿' }}
          </span>
        </div>
        <p v-if="integration.description" class="mt-1 text-sm text-gray-500">
          {{ integration.description }}
        </p>
      </div>
      
      <!-- 表单集成预览 -->
      <div v-if="integration.type === 'FORM'" class="p-6">
        <!-- 成功消息 -->
        <div v-if="showSuccessMessage" class="mb-4 bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded relative" role="alert">
          <span class="block sm:inline">{{ successMessage }}</span>
        </div>
        
        <!-- 错误消息 -->
        <div v-if="showErrorMessage" class="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded relative" role="alert">
          <span class="block sm:inline">{{ errorMessage }}</span>
        </div>
        
        <!-- 表单 -->
        <form @submit.prevent="submitForm">
          <div :class="formClasses">
            <template v-for="condition in renderedFormConditions" :key="condition.field">
              <div>
                <label :for="condition.field" :class="formLabelClasses">
                  {{ condition.label }}
                  <span v-if="condition.required" class="text-red-500">*</span>
                </label>
                
                <!-- 文本输入框 -->
                <input 
                  v-if="condition.type === 'INPUT'"
                  :id="condition.field"
                  v-model="formValues[condition.field]"
                  type="text"
                  class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                  :required="condition.required"
                />
                
                <!-- 数字输入框 -->
                <input 
                  v-else-if="condition.type === 'NUMBER'"
                  :id="condition.field"
                  v-model="formValues[condition.field]"
                  type="number"
                  class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                  :required="condition.required"
                />
                
                <!-- 日期选择器 -->
                <input 
                  v-else-if="condition.type === 'DATE'"
                  :id="condition.field"
                  v-model="formValues[condition.field]"
                  type="date"
                  class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                  :required="condition.required"
                />
                
                <!-- 日期时间选择器 -->
                <input 
                  v-else-if="condition.type === 'DATETIME'"
                  :id="condition.field"
                  v-model="formValues[condition.field]"
                  type="datetime-local"
                  class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                  :required="condition.required"
                />
                
                <!-- 下拉选择框 -->
                <select
                  v-else-if="condition.type === 'SELECT'"
                  :id="condition.field"
                  v-model="formValues[condition.field]"
                  class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                  :required="condition.required"
                >
                  <option value="">请选择</option>
                  <option 
                    v-for="option in condition.componentProps?.options" 
                    :key="option.value" 
                    :value="option.value"
                  >
                    {{ option.label }}
                  </option>
                </select>
                
                <!-- 多选框 -->
                <select
                  v-else-if="condition.type === 'MULTISELECT'"
                  :id="condition.field"
                  v-model="formValues[condition.field]"
                  multiple
                  class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                  :required="condition.required"
                >
                  <option 
                    v-for="option in condition.componentProps?.options" 
                    :key="option.value" 
                    :value="option.value"
                  >
                    {{ option.label }}
                  </option>
                </select>
                
                <!-- 复选框 -->
                <div v-else-if="condition.type === 'CHECKBOX'" class="flex items-center mt-1">
                  <input 
                    :id="condition.field"
                    v-model="formValues[condition.field]"
                    type="checkbox"
                    class="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
                    :required="condition.required"
                  />
                  <label :for="condition.field" class="ml-2 text-sm text-gray-500">
                    {{ condition.componentProps?.checkboxLabel || '是' }}
                  </label>
                </div>
                
                <!-- 单选框组 -->
                <div v-else-if="condition.type === 'RADIO'" class="mt-1 space-y-2">
                  <div 
                    v-for="option in condition.componentProps?.options" 
                    :key="option.value"
                    class="flex items-center"
                  >
                    <input 
                      :id="`${condition.field}-${option.value}`"
                      v-model="formValues[condition.field]"
                      type="radio"
                      :value="option.value"
                      class="h-4 w-4 border-gray-300 text-indigo-600 focus:ring-indigo-500"
                      :required="condition.required"
                    />
                    <label :for="`${condition.field}-${option.value}`" class="ml-2 text-sm text-gray-700">
                      {{ option.label }}
                    </label>
                  </div>
                </div>
                
                <!-- 多行文本框 -->
                <textarea 
                  v-else-if="condition.type === 'TEXTAREA'"
                  :id="condition.field"
                  v-model="formValues[condition.field]"
                  rows="3"
                  class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                  :required="condition.required"
                ></textarea>
                
                <!-- 默认文本输入框 -->
                <input 
                  v-else
                  :id="condition.field"
                  v-model="formValues[condition.field]"
                  type="text"
                  class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                  :required="condition.required"
                />
              </div>
            </template>
          </div>
          
          <!-- 表单按钮 -->
          <div class="mt-6 flex justify-end space-x-3">
            <button 
              v-if="integration.formConfig.buttons && integration.formConfig.buttons.length > 1"
              type="button"
              @click="resetForm"
              class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              <i v-if="integration.formConfig.buttons[1].icon" :class="[integration.formConfig.buttons[1].icon, 'mr-2']"></i>
              {{ integration.formConfig.buttons[1].label || '重置' }}
            </button>
            <button 
              type="submit"
              class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              <i v-if="integration.formConfig.buttons && integration.formConfig.buttons[0] && integration.formConfig.buttons[0].icon" :class="[integration.formConfig.buttons[0].icon, 'mr-2']"></i>
              {{ integration.formConfig.buttons && integration.formConfig.buttons[0] ? integration.formConfig.buttons[0].label : '查询' }}
            </button>
          </div>
        </form>
      </div>      
      <!-- 表格集成预览 -->
      <div v-if="integration.type === 'TABLE'" class="p-6">
        <!-- 表格工具栏 -->
        <div class="mb-4 flex justify-between items-center">
          <div class="flex space-x-2">
            <!-- 导出按钮 -->
            <div v-if="exportConfig.enabled" class="relative inline-block text-left">
              <div>
                <button type="button" class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500" id="export-menu" aria-expanded="true" aria-haspopup="true">
                  <i class="fas fa-file-export mr-2"></i>
                  导出
                  <i class="fas fa-chevron-down ml-2"></i>
                </button>
              </div>
              
              <div class="origin-top-left absolute left-0 mt-2 w-40 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none z-10" role="menu" aria-orientation="vertical" aria-labelledby="export-menu">
                <div class="py-1" role="none">
                  <a v-if="exportConfig.formats.includes('CSV')" href="#" @click.prevent="exportTable('CSV')" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 hover:text-gray-900" role="menuitem">导出为CSV</a>
                  <a v-if="exportConfig.formats.includes('EXCEL')" href="#" @click.prevent="exportTable('EXCEL')" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 hover:text-gray-900" role="menuitem">导出为Excel</a>
                </div>
              </div>
            </div>
          </div>
          
          <div v-if="pagination.enabled" class="flex items-center space-x-2">
            <span class="text-sm text-gray-500">每页显示:</span>
            <select
              v-model="pageSize"
              @change="handlePageSizeChange($event.target.value)"
              class="block w-20 rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
            >
              <option 
                v-for="size in pagination.pageSizeOptions" 
                :key="size" 
                :value="size"
              >
                {{ size }}
              </option>
            </select>
          </div>
        </div>
        
        <!-- 表格 -->
        <div class="overflow-x-auto bg-white border border-gray-200 rounded-md">
          <!-- 加载中 -->
          <div v-if="tableLoading" class="p-10 text-center">
            <i class="fas fa-circle-notch fa-spin text-indigo-500 text-3xl mb-4"></i>
            <p class="text-gray-500">正在加载数据...</p>
          </div>
          
          <!-- 空状态 -->
          <div v-else-if="tableData.length === 0" class="p-10 text-center">
            <div class="rounded-full bg-gray-100 h-16 w-16 flex items-center justify-center mx-auto mb-4">
              <i class="fas fa-table text-gray-400 text-2xl"></i>
            </div>
            <h3 class="text-sm font-medium text-gray-900">暂无数据</h3>
            <p class="mt-1 text-sm text-gray-500">
              当前查询没有返回任何数据
            </p>
          </div>
          
          <!-- 数据表格 -->
          <table v-else class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th 
                  v-for="column in tableColumns" 
                  :key="column.field"
                  :class="[
                    'px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider',
                    getColumnAlignClass(column.align)
                  ]"
                >
                  {{ column.label }}
                </th>
                <th v-if="tableActions.length > 0" scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  操作
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="(row, rowIndex) in tableData" :key="rowIndex">
                <td 
                  v-for="column in tableColumns" 
                  :key="column.field"
                  :class="[
                    'px-6 py-4 whitespace-nowrap text-sm',
                    getColumnAlignClass(column.align)
                  ]"
                >
                  {{ row[column.field] }}
                </td>
                <td v-if="tableActions.length > 0" class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <button 
                    v-for="action in tableActions" 
                    :key="action.label"
                    @click="handleTableAction(action, row)"
                    :class="[
                      'mx-1',
                      action.style === 'primary' ? 'text-indigo-600 hover:text-indigo-900' :
                      action.style === 'danger' ? 'text-red-600 hover:text-red-900' :
                      'text-gray-600 hover:text-gray-900'
                    ]"
                    :title="action.label"
                  >
                    <i v-if="action.icon" :class="action.icon"></i>
                    <span v-else>{{ action.label }}</span>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <!-- 分页 -->
        <div v-if="pagination.enabled && totalRows > 0" class="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6 mt-4">
          <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
            <div>
              <p class="text-sm text-gray-700">
                显示
                <span class="font-medium">{{ (currentPage - 1) * pageSize + 1 }}</span>
                至
                <span class="font-medium">{{ Math.min(currentPage * pageSize, totalRows) }}</span>
                条，共
                <span class="font-medium">{{ totalRows }}</span>
                条记录
              </p>
            </div>
            <div>
              <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
                <!-- 上一页 -->
                <button
                  @click="handlePageChange(currentPage - 1)"
                  :disabled="currentPage === 1"
                  :class="[
                    'relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium',
                    currentPage === 1 ? 'text-gray-300 cursor-not-allowed' : 'text-gray-500 hover:bg-gray-50'
                  ]"
                >
                  <i class="fas fa-chevron-left"></i>
                </button>
                
                <!-- 页码 -->
                <template v-for="page in Math.min(5, Math.ceil(totalRows / pageSize))" :key="page">
                  <button
                    @click="handlePageChange(page)"
                    :class="[
                      'relative inline-flex items-center px-4 py-2 border',
                      page === currentPage
                        ? 'bg-indigo-50 border-indigo-500 text-indigo-600 z-10'
                        : 'bg-white border-gray-300 text-gray-500 hover:bg-gray-50'
                    ]"
                  >
                    {{ page }}
                  </button>
                </template>
                
                <!-- 下一页 -->
                <button
                  @click="handlePageChange(currentPage + 1)"
                  :disabled="currentPage === Math.ceil(totalRows / pageSize)"
                  :class="[
                    'relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium',
                    currentPage === Math.ceil(totalRows / pageSize) ? 'text-gray-300 cursor-not-allowed' : 'text-gray-500 hover:bg-gray-50'
                  ]"
                >
                  <i class="fas fa-chevron-right"></i>
                </button>
              </nav>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>