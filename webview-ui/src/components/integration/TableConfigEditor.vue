<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue';
import { useQueryStore } from '@/stores/query';
import { useMessageStore } from '@/stores/message';
import type { TableConfig, TableColumn, TableAction, PaginationConfig, ExportConfig } from '@/types/integration';
import { ColumnAlign } from '@/types/integration';

// 组件属性
const props = defineProps<{
  modelValue: TableConfig;
  queryId?: string;
}>();

// 组件事件
const emit = defineEmits<{
  (e: 'update:modelValue', value: TableConfig): void;
}>();

// Store
const queryStore = useQueryStore();
const message = useMessageStore();

// 状态
const tableConfig = reactive<TableConfig>({
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
  // 新增: 批量操作配置
  batchActions: [],
  // 新增: 数据聚合配置  
  aggregation: {
    enabled: false,
    groupByFields: [],
    aggregationFunctions: []
  },
  // 新增: 高级过滤配置
  advancedFilters: {
    enabled: false,
    defaultFilters: [],
    savedFilters: []
  }
});

// 编辑状态
const editingColumn = ref<TableColumn | null>(null);
const isAddingColumn = ref(false);
const isEditMode = ref(false);
const availableColumns = ref<Array<{ field: string; label: string; type: string }>>([]);
const editingAction = ref<TableAction | null>(null);
const isAddingAction = ref(false);
const isEditingAction = ref(false);

// 添加tab选择状态
const activeTab = ref('columns'); // 默认显示列配置

// 新增: 过滤条件编辑状态
const isEditingFilter = ref(false);
const editingFilter = ref<any>(null);
const filterConditionTypes = [
  { value: 'equals', label: '等于' },
  { value: 'notEquals', label: '不等于' },
  { value: 'contains', label: '包含' },
  { value: 'notContains', label: '不包含' },
  { value: 'startsWith', label: '开头是' },
  { value: 'endsWith', label: '结尾是' },
  { value: 'greaterThan', label: '大于' },
  { value: 'lessThan', label: '小于' },
  { value: 'between', label: '介于' },
  { value: 'in', label: '在列表中' },
  { value: 'isNull', label: '为空' },
  { value: 'isNotNull', label: '不为空' }
];

// 新增: 批量操作编辑状态
const isEditingBatchAction = ref(false);
const editingBatchAction = ref<any>(null);

// 新增: 聚合编辑状态
const isEditingAggregation = ref(false);
const aggregationFunctions = [
  { value: 'sum', label: '求和' },
  { value: 'avg', label: '平均值' },
  { value: 'min', label: '最小值' },
  { value: 'max', label: '最大值' },
  { value: 'count', label: '计数' },
  { value: 'countDistinct', label: '去重计数' }
];

// 选项定义
const alignOptions = [
  { value: ColumnAlign.LEFT, label: '左对齐' },
  { value: ColumnAlign.CENTER, label: '居中' },
  { value: ColumnAlign.RIGHT, label: '右对齐' }
];

const maskTypeOptions = [
  { value: 'NONE', label: '不屏蔽' },
  { value: 'FULL', label: '完全屏蔽' },
  { value: 'PARTIAL', label: '部分屏蔽' },
  { value: 'CUSTOM', label: '自定义' }
];

const actionTypeOptions = [
  { value: 'link', label: '链接' },
  { value: 'button', label: '按钮' },
  { value: 'menu', label: '菜单项' }
];

// 计算属性
const sortedColumns = computed(() => {
  return [...tableConfig.columns].sort((a, b) => a.displayOrder - b.displayOrder);
});

// 监听props变化
watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    tableConfig.columns = [...newValue.columns];
    tableConfig.actions = [...newValue.actions];
    tableConfig.pagination = { ...newValue.pagination };
    tableConfig.export = { ...newValue.export };
    
    // 新增: 同步新增的配置
    tableConfig.batchActions = [...(newValue.batchActions || [])];
    tableConfig.aggregation = newValue.aggregation 
      ? { ...newValue.aggregation }
      : { enabled: false, groupByFields: [], aggregationFunctions: [] };
    tableConfig.advancedFilters = newValue.advancedFilters
      ? { ...newValue.advancedFilters }
      : { enabled: false, defaultFilters: [], savedFilters: [] };
  }
}, { deep: true });

// 监听tableConfig变化，触发更新事件
watch(tableConfig, (newValue) => {
  emit('update:modelValue', {
    columns: [...newValue.columns],
    actions: [...newValue.actions],
    pagination: { ...newValue.pagination },
    export: { ...newValue.export },
    // 新增: 加入新配置
    batchActions: [...newValue.batchActions],
    aggregation: { ...newValue.aggregation },
    advancedFilters: { ...newValue.advancedFilters }
  });
}, { deep: true });

// 监听queryId变化
watch(() => props.queryId, async (newValue) => {
  if (newValue) {
    await loadQueryColumns(newValue);
  }
});

// 生命周期钩子
onMounted(async () => {
  // 加载查询列
  if (props.queryId) {
    await loadQueryColumns(props.queryId);
  }
});

// 加载查询列
const loadQueryColumns = async (queryId: string) => {
  try {
    const query = await queryStore.fetchQueryById(queryId);
    
    if (query) {
      // 解析查询结果列 - 确保处理可能为空的查询列
      const columns = query.columns || [];
      
      // 指定column的类型
      interface QueryColumn {
        name: string;
        label?: string;
        type?: string;
      }
      
      availableColumns.value = columns.map((column: QueryColumn) => ({
        field: column.name || '',
        label: column.label || column.name || '',
        type: column.type ? mapDatabaseTypeToColumnType(column.type) : 'TEXT'
      }));
      
      // 如果没有列，自动从查询结果生成
      if (tableConfig.columns.length === 0 && availableColumns.value.length > 0) {
        autoGenerateColumns();
      }
    }
  } catch (error) {
    console.error('加载查询列失败', error);
    message.error('加载查询列失败');
  }
};

// 数据库类型映射到列类型
const mapDatabaseTypeToColumnType = (dbType: string): string => {
  const type = dbType.toLowerCase();
  
  if (type.includes('varchar') || type.includes('char') || type.includes('text')) {
    return 'TEXT';
  } else if (type.includes('int') || type.includes('decimal') || type.includes('float') || type.includes('double')) {
    return 'NUMBER';
  } else if (type.includes('date') && type.includes('time')) {
    return 'DATETIME';
  } else if (type.includes('date')) {
    return 'DATE';
  } else if (type.includes('bool')) {
    return 'BOOLEAN';
  }
  
  return 'TEXT';
};

// 自动生成表格列
const autoGenerateColumns = () => {
  // 从可用列自动生成表格列
  const columns: TableColumn[] = availableColumns.value.map((column, index) => ({
    field: column.field,
    label: column.label,
    type: column.type,
    sortable: true,
    filterable: true,
    align: ColumnAlign.LEFT,
    maskType: 'NONE',
    visible: true,
    displayOrder: index,
    // 新增: 列过滤条件
    filterConfig: {
      operators: ['equals', 'contains'],
      defaultValue: null,
      multiple: false
    }
  }));
  
  tableConfig.columns = columns;
};

// 添加列
const addColumn = () => {
  isAddingColumn.value = true;
  isEditMode.value = false;
  
  editingColumn.value = {
    field: '',
    label: '',
    type: 'TEXT',
    sortable: true,
    filterable: true,
    align: ColumnAlign.LEFT,
    visible: true,
    displayOrder: tableConfig.columns.length,
    // 新增: 列过滤条件
    filterConfig: {
      operators: ['equals', 'contains'],
      defaultValue: null,
      multiple: false
    }
  };
};

// 编辑列
const editColumn = (column: TableColumn) => {
  isAddingColumn.value = false;
  isEditMode.value = true;
  
  // 创建副本进行编辑
  editingColumn.value = { ...column };
  
  // 确保有过滤配置
  if (!editingColumn.value.filterConfig) {
    editingColumn.value.filterConfig = {
      operators: ['equals', 'contains'],
      defaultValue: null,
      multiple: false
    };
  }
};

// 保存列
const saveColumn = () => {
  if (!editingColumn.value) return;
  
  // 验证
  if (!editingColumn.value.field) {
    message.error('请选择字段');
    return;
  }
  
  if (!editingColumn.value.label) {
    message.error('请输入标签名称');
    return;
  }
  
  if (isEditMode.value) {
    // 更新现有列
    const index = tableConfig.columns.findIndex(c => c.field === editingColumn.value!.field);
    if (index !== -1) {
      tableConfig.columns[index] = { ...editingColumn.value };
    }
  } else {
    // 添加新列
    tableConfig.columns.push({ ...editingColumn.value });
  }
  
  // 重置编辑状态
  editingColumn.value = null;
  isAddingColumn.value = false;
  isEditMode.value = false;
};

// 取消编辑列
const cancelEditColumn = () => {
  editingColumn.value = null;
  isAddingColumn.value = false;
  isEditMode.value = false;
};

// 删除列
const deleteColumn = (field: string) => {
  const index = tableConfig.columns.findIndex(c => c.field === field);
  if (index !== -1) {
    tableConfig.columns.splice(index, 1);
  }
};

// 移动列
const moveColumn = (field: string, direction: 'up' | 'down') => {
  const index = tableConfig.columns.findIndex(c => c.field === field);
  if (index === -1) return;
  
  if (direction === 'up' && index > 0) {
    // 向上移动
    const temp = tableConfig.columns[index].displayOrder;
    tableConfig.columns[index].displayOrder = tableConfig.columns[index - 1].displayOrder;
    tableConfig.columns[index - 1].displayOrder = temp;
  } else if (direction === 'down' && index < tableConfig.columns.length - 1) {
    // 向下移动
    const temp = tableConfig.columns[index].displayOrder;
    tableConfig.columns[index].displayOrder = tableConfig.columns[index + 1].displayOrder;
    tableConfig.columns[index + 1].displayOrder = temp;
  }
};

// 选择字段改变
const handleColumnFieldChange = (field: string) => {
  if (!editingColumn.value) return;
  
  // 查找对应的字段信息
  const columnInfo = availableColumns.value.find(f => f.field === field);
  if (columnInfo) {
    editingColumn.value.field = columnInfo.field;
    editingColumn.value.label = columnInfo.label;
    editingColumn.value.type = columnInfo.type;
  }
};

// 添加操作
const addAction = () => {
  isAddingAction.value = true;
  isEditingAction.value = false;
  
  editingAction.value = {
    type: 'button',
    label: '',
    handler: '',
    icon: 'fas fa-external-link-alt'
  };
};

// 编辑操作
const editAction = (index: number) => {
  isAddingAction.value = false;
  isEditingAction.value = true;
  
  // 创建副本进行编辑
  editingAction.value = { ...tableConfig.actions[index] };
};

// 保存操作
const saveAction = () => {
  if (!editingAction.value) return;
  
  // 验证
  if (!editingAction.value.label) {
    message.error('请输入操作名称');
    return;
  }
  
  if (!editingAction.value.handler) {
    message.error('请输入处理函数名称');
    return;
  }
  
  if (isEditingAction.value) {
    // 更新现有操作
    const index = tableConfig.actions.findIndex(a => a.label === editingAction.value!.label);
    if (index !== -1) {
      tableConfig.actions[index] = { ...editingAction.value };
    }
  } else {
    // 添加新操作
    tableConfig.actions.push({ ...editingAction.value });
  }
  
  // 重置编辑状态
  editingAction.value = null;
  isAddingAction.value = false;
  isEditingAction.value = false;
};

// 取消编辑操作
const cancelEditAction = () => {
  editingAction.value = null;
  isAddingAction.value = false;
  isEditingAction.value = false;
};

// 删除操作
const deleteAction = (index: number) => {
  tableConfig.actions.splice(index, 1);
};

// 获取列对齐方式名称
const getAlignName = (align: ColumnAlign): string => {
  const option = alignOptions.find(opt => opt.value === align);
  return option ? option.label : align;
};

// 获取屏蔽类型名称
const getMaskTypeName = (type: string): string => {
  const option = maskTypeOptions.find(opt => opt.value === type);
  return option ? option.label : type;
};

// 新增: 添加批量操作
const addBatchAction = () => {
  isEditingBatchAction.value = true;
  editingBatchAction.value = {
    id: `batch_${Date.now()}`,
    label: '',
    icon: 'fas fa-tasks',
    type: 'button',
    handler: '',
    requiresSelection: true,
    confirmationRequired: false,
    confirmationMessage: ''
  };
};

// 新增: 保存批量操作
const saveBatchAction = () => {
  if (!editingBatchAction.value) return;
  
  // 验证
  if (!editingBatchAction.value.label) {
    message.error('请输入批量操作名称');
    return;
  }
  
  if (!editingBatchAction.value.handler) {
    message.error('请输入处理函数名称');
    return;
  }
  
  const existingIndex = tableConfig.batchActions.findIndex(a => a.id === editingBatchAction.value.id);
  if (existingIndex !== -1) {
    // 更新现有批量操作
    tableConfig.batchActions[existingIndex] = { ...editingBatchAction.value };
  } else {
    // 添加新批量操作
    tableConfig.batchActions.push({ ...editingBatchAction.value });
  }
  
  // 重置编辑状态
  editingBatchAction.value = null;
  isEditingBatchAction.value = false;
};

// 新增: 删除批量操作
const deleteBatchAction = (id: string) => {
  const index = tableConfig.batchActions.findIndex(a => a.id === id);
  if (index !== -1) {
    tableConfig.batchActions.splice(index, 1);
  }
};

// 新增: 添加数据聚合配置
const editAggregationSettings = () => {
  isEditingAggregation.value = true;
};

// 新增: 保存数据聚合配置
const saveAggregationSettings = () => {
  // 验证配置
  if (tableConfig.aggregation.enabled && tableConfig.aggregation.groupByFields.length === 0) {
    message.error('请至少选择一个分组字段');
    return;
  }
  
  isEditingAggregation.value = false;
};

// 新增: 添加高级筛选条件
const addAdvancedFilter = () => {
  isEditingFilter.value = true;
  editingFilter.value = {
    id: `filter_${Date.now()}`,
    name: '',
    conditions: [
      {
        field: '',
        operator: 'equals',
        value: null,
        logic: 'AND'
      }
    ],
    isDefault: false
  };
};

// 新增: 添加条件到当前筛选器
const addConditionToFilter = () => {
  if (editingFilter.value) {
    editingFilter.value.conditions.push({
      field: '',
      operator: 'equals',
      value: null,
      logic: 'AND'
    });
  }
};

// 新增: 删除筛选器中的条件
const removeConditionFromFilter = (index: number) => {
  if (editingFilter.value && editingFilter.value.conditions.length > 1) {
    editingFilter.value.conditions.splice(index, 1);
  }
};

// 新增: 保存高级筛选条件
const saveAdvancedFilter = () => {
  if (!editingFilter.value) return;
  
  // 验证
  if (!editingFilter.value.name) {
    message.error('请输入筛选器名称');
    return;
  }
  
  for (const condition of editingFilter.value.conditions) {
    if (!condition.field) {
      message.error('请为所有条件选择字段');
      return;
    }
  }
  
  const existingIndex = tableConfig.advancedFilters.savedFilters.findIndex(
    f => f.id === editingFilter.value.id
  );
  
  if (existingIndex !== -1) {
    // 更新现有筛选器
    tableConfig.advancedFilters.savedFilters[existingIndex] = { ...editingFilter.value };
  } else {
    // 添加新筛选器
    tableConfig.advancedFilters.savedFilters.push({ ...editingFilter.value });
  }
  
  // 如果是默认筛选器，更新默认筛选器列表
  if (editingFilter.value.isDefault) {
    // 如果不在默认筛选器列表中，添加进去
    const inDefaultList = tableConfig.advancedFilters.defaultFilters.some(
      f => f.id === editingFilter.value.id
    );
    
    if (!inDefaultList) {
      tableConfig.advancedFilters.defaultFilters.push(editingFilter.value.id);
    }
  } else {
    // 从默认筛选器列表中移除
    tableConfig.advancedFilters.defaultFilters = tableConfig.advancedFilters.defaultFilters.filter(
      id => id !== editingFilter.value.id
    );
  }
  
  // 重置编辑状态
  editingFilter.value = null;
  isEditingFilter.value = false;
};

// 新增: 删除高级筛选条件
const deleteAdvancedFilter = (id: string) => {
  // 从保存的筛选器列表中删除
  tableConfig.advancedFilters.savedFilters = tableConfig.advancedFilters.savedFilters.filter(
    f => f.id !== id
  );
  
  // 从默认筛选器列表中删除
  tableConfig.advancedFilters.defaultFilters = tableConfig.advancedFilters.defaultFilters.filter(
    filterId => filterId !== id
  );
};

// 新增: 获取操作类型名称
const getFilterOperatorName = (operator: string): string => {
  const option = filterConditionTypes.find(opt => opt.value === operator);
  return option ? option.label : operator;
};

// 在script部分添加toggleBatchActions方法
// 在methods或常规函数部分添加
const toggleBatchActions = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.checked && tableConfig.batchActions.length === 0) {
    // 如果启用但没有批量操作，添加一个默认的批量操作
    addBatchAction();
  } else if (!target.checked && tableConfig.batchActions.length > 0) {
    // 如果禁用但有批量操作，清空批量操作
    tableConfig.batchActions = [];
  }
};
</script>

<template>
  <div class="table-config-editor">
    <!-- 标签导航 -->
    <div class="mb-4 border-b border-gray-200">
      <ul class="flex flex-wrap -mb-px text-sm font-medium text-center">
        <li class="mr-2">
          <a 
            href="#" 
            @click.prevent="activeTab = 'columns'"
            :class="[
              'inline-block p-4 border-b-2 rounded-t-lg',
              activeTab === 'columns' 
                ? 'text-indigo-600 border-indigo-600' 
                : 'border-transparent hover:text-gray-600 hover:border-gray-300'
            ]"
          >
            <i class="fas fa-table mr-1"></i> 列配置
          </a>
        </li>
        <li class="mr-2">
          <a 
            href="#" 
            @click.prevent="activeTab = 'actions'"
            :class="[
              'inline-block p-4 border-b-2 rounded-t-lg',
              activeTab === 'actions' 
                ? 'text-indigo-600 border-indigo-600' 
                : 'border-transparent hover:text-gray-600 hover:border-gray-300'
            ]"
          >
            <i class="fas fa-mouse-pointer mr-1"></i> 操作按钮
          </a>
        </li>
        <li class="mr-2">
          <a 
            href="#" 
            @click.prevent="activeTab = 'batchActions'"
            :class="[
              'inline-block p-4 border-b-2 rounded-t-lg',
              activeTab === 'batchActions' 
                ? 'text-indigo-600 border-indigo-600' 
                : 'border-transparent hover:text-gray-600 hover:border-gray-300'
            ]"
          >
            <i class="fas fa-tasks mr-1"></i> 批量操作
          </a>
        </li>
        <li class="mr-2">
          <a 
            href="#" 
            @click.prevent="activeTab = 'filters'"
            :class="[
              'inline-block p-4 border-b-2 rounded-t-lg',
              activeTab === 'filters' 
                ? 'text-indigo-600 border-indigo-600' 
                : 'border-transparent hover:text-gray-600 hover:border-gray-300'
            ]"
          >
            <i class="fas fa-filter mr-1"></i> 高级筛选
          </a>
        </li>
        <li class="mr-2">
          <a 
            href="#" 
            @click.prevent="activeTab = 'aggregation'"
            :class="[
              'inline-block p-4 border-b-2 rounded-t-lg',
              activeTab === 'aggregation' 
                ? 'text-indigo-600 border-indigo-600' 
                : 'border-transparent hover:text-gray-600 hover:border-gray-300'
            ]"
          >
            <i class="fas fa-chart-bar mr-1"></i> 数据聚合
          </a>
        </li>
        <li class="mr-2">
          <a 
            href="#" 
            @click.prevent="activeTab = 'settings'"
            :class="[
              'inline-block p-4 border-b-2 rounded-t-lg',
              activeTab === 'settings' 
                ? 'text-indigo-600 border-indigo-600' 
                : 'border-transparent hover:text-gray-600 hover:border-gray-300'
            ]"
          >
            <i class="fas fa-cog mr-1"></i> 基础设置
          </a>
        </li>
      </ul>
    </div>

    <!-- 列配置标签页 -->
    <div v-show="activeTab === 'columns'" class="mb-6">
      <div class="flex justify-between items-center mb-2">
        <h3 class="text-sm font-medium text-gray-700">表格列配置</h3>
        <button 
          @click="addColumn"
          class="inline-flex items-center px-2.5 py-1.5 border border-transparent text-xs font-medium rounded text-indigo-700 bg-indigo-100 hover:bg-indigo-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          <i class="fas fa-plus mr-1"></i> 添加列
        </button>
      </div>
      
      <!-- 空状态 -->
      <div v-if="tableConfig.columns.length === 0 && !isAddingColumn" class="bg-gray-50 border border-dashed border-gray-300 rounded-md p-6 text-center">
        <i class="fas fa-table text-gray-400 text-3xl mb-2"></i>
        <p class="text-sm text-gray-500 mb-2">暂无表格列配置</p>
        <button 
          @click="addColumn"
          class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-md text-indigo-700 bg-indigo-100 hover:bg-indigo-200 focus:outline-none"
        >
          <i class="fas fa-plus mr-1"></i> 添加列
        </button>
      </div>
      
      <!-- 列编辑表单 -->
      <div v-if="editingColumn" class="bg-gray-50 border border-gray-200 rounded-md p-4 mb-4">
        <h4 class="text-sm font-medium text-gray-700 mb-3">
          {{ isEditMode ? '编辑列' : '添加列' }}
        </h4>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
          <!-- 字段选择 -->
          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">
              字段名称 <span class="text-red-500">*</span>
            </label>
            <select
              v-model="editingColumn.field"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
              @change="handleColumnFieldChange($event.target.value)"
              :disabled="isEditMode"
            >
              <option value="">请选择字段</option>
              <option 
                v-for="column in availableColumns" 
                :key="column.field" 
                :value="column.field"
              >
                {{ column.label }} ({{ column.field }})
              </option>
            </select>
          </div>
          
          <!-- 列标题 -->
          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">
              列标题 <span class="text-red-500">*</span>
            </label>
            <input 
              v-model="editingColumn.label"
              type="text"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
              placeholder="请输入列标题"
            />
          </div>
          
          <!-- 对齐方式 -->
          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">
              对齐方式
            </label>
            <select
              v-model="editingColumn.align"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
            >
              <option 
                v-for="option in alignOptions" 
                :key="option.value" 
                :value="option.value"
              >
                {{ option.label }}
              </option>
            </select>
          </div>
          
          <!-- 列宽 -->
          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">
              列宽
            </label>
            <input 
              v-model="editingColumn.width"
              type="text"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
              placeholder="请输入列宽 (如: 100px 或 15%)"
            />
          </div>
          
          <!-- 可见性 -->
          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">
              可见性
            </label>
            <div class="flex items-center mt-2">
              <input 
                v-model="editingColumn.visible"
                type="checkbox"
                class="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
              />
              <span class="ml-2 text-sm text-gray-500">默认显示此列</span>
            </div>
          </div>
          
          <!-- 可排序 -->
          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">
              可排序
            </label>
            <div class="flex items-center mt-2">
              <input 
                v-model="editingColumn.sortable"
                type="checkbox"
                class="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
              />
              <span class="ml-2 text-sm text-gray-500">允许对此列排序</span>
            </div>
          </div>
          
          <!-- 屏蔽类型 -->
          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">
              屏蔽类型
            </label>
            <select
              v-model="editingColumn.maskType"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
            >
              <option 
                v-for="option in maskTypeOptions" 
                :key="option.value" 
                :value="option.value"
              >
                {{ option.label }}
              </option>
            </select>
          </div>
          
          <!-- 显示顺序 -->
          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">
              显示顺序
            </label>
            <input 
              v-model="editingColumn.displayOrder"
              type="number"
              min="0"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
            />
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="flex justify-end space-x-2">
          <button 
            @click="cancelEditColumn"
            class="inline-flex items-center px-3 py-1.5 border border-gray-300 text-xs font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            取消
          </button>
          <button 
            @click="saveColumn"
            class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            保存
          </button>
        </div>
      </div>
      
      <!-- 列列表 -->
      <div v-if="tableConfig.columns.length > 0" class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                字段名称
              </th>
              <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                列标题
              </th>
              <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                对齐方式
              </th>
              <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                可见
              </th>
              <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                屏蔽类型
              </th>
              <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                顺序
              </th>
              <th scope="col" class="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                操作
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="column in sortedColumns" :key="column.field">
              <td class="px-4 py-2 whitespace-nowrap text-sm font-medium text-gray-900">
                {{ column.field }}
              </td>
              <td class="px-4 py-2 whitespace-nowrap text-sm text-gray-500">
                {{ column.label }}
              </td>
              <td class="px-4 py-2 whitespace-nowrap text-sm text-gray-500">
                {{ getAlignName(column.align) }}
              </td>
              <td class="px-4 py-2 whitespace-nowrap text-sm text-gray-500">
                <span 
                  :class="[
                    'px-2 inline-flex text-xs leading-5 font-semibold rounded-full',
                    column.visible ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                  ]"
                >
                  {{ column.visible ? '是' : '否' }}
                </span>
              </td>
              <td class="px-4 py-2 whitespace-nowrap text-sm text-gray-500">
                {{ getMaskTypeName(column.maskType || 'NONE') }}
              </td>
              <td class="px-4 py-2 whitespace-nowrap text-sm text-gray-500">
                {{ column.displayOrder }}
              </td>
              <td class="px-4 py-2 whitespace-nowrap text-right text-sm font-medium">
                <button 
                  @click="moveColumn(column.field, 'up')"
                  class="text-gray-500 hover:text-gray-700 mx-1"
                  title="上移"
                >
                  <i class="fas fa-arrow-up"></i>
                </button>
                <button 
                  @click="moveColumn(column.field, 'down')"
                  class="text-gray-500 hover:text-gray-700 mx-1"
                  title="下移"
                >
                  <i class="fas fa-arrow-down"></i>
                </button>
                <button 
                  @click="editColumn(column)"
                  class="text-indigo-600 hover:text-indigo-900 mx-1"
                  title="编辑"
                >
                  <i class="fas fa-edit"></i>
                </button>
                <button 
                  @click="deleteColumn(column.field)"
                  class="text-red-600 hover:text-red-900 mx-1"
                  title="删除"
                >
                  <i class="fas fa-trash-alt"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    
    <!-- 操作按钮配置标签页 -->
    <div v-show="activeTab === 'actions'" class="mb-6">
      <div class="flex justify-between items-center mb-2">
        <h3 class="text-sm font-medium text-gray-700">操作按钮配置</h3>
        <button 
          @click="addAction"
          class="inline-flex items-center px-2.5 py-1.5 border border-transparent text-xs font-medium rounded text-indigo-700 bg-indigo-100 hover:bg-indigo-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          <i class="fas fa-plus mr-1"></i> 添加操作
        </button>
      </div>
      
      <!-- 操作编辑表单 -->
      <div v-if="editingAction" class="bg-gray-50 border border-gray-200 rounded-md p-4 mb-4">
        <h4 class="text-sm font-medium text-gray-700 mb-3">
          {{ isEditingAction ? '编辑操作' : '添加操作' }}
        </h4>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
          <!-- 操作类型 -->
          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">
              操作类型
            </label>
            <select
              v-model="editingAction.type"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
            >
              <option 
                v-for="option in actionTypeOptions" 
                :key="option.value" 
                :value="option.value"
              >
                {{ option.label }}
              </option>
            </select>
          </div>
          
          <!-- 操作名称 -->
          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">
              操作名称 <span class="text-red-500">*</span>
            </label>
            <input 
              v-model="editingAction.label"
              type="text"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
              placeholder="请输入操作名称"
            />
          </div>
          
          <!-- 处理函数 -->
          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">
              处理函数 <span class="text-red-500">*</span>
            </label>
            <input 
              v-model="editingAction.handler"
              type="text"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
              placeholder="请输入处理函数名称"
            />
          </div>
          
          <!-- 图标 -->
          <div>
            <label class="block text-xs font-medium text-gray-700 mb-1">
              图标
            </label>
            <input 
              v-model="editingAction.icon"
              type="text"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
              placeholder="请输入FontAwesome图标类名"
            />
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="flex justify-end space-x-2">
          <button 
            @click="cancelEditAction"
            class="inline-flex items-center px-3 py-1.5 border border-gray-300 text-xs font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            取消
          </button>
          <button 
            @click="saveAction"
            class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            保存
          </button>
        </div>
      </div>
      
      <!-- 操作列表 -->
      <div v-if="tableConfig.actions.length > 0" class="bg-white rounded-md border border-gray-200 overflow-hidden">
        <ul class="divide-y divide-gray-200">
          <li v-for="(action, index) in tableConfig.actions" :key="index" class="p-4">
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <div class="flex-shrink-0 h-10 w-10 flex items-center justify-center bg-indigo-100 rounded-full text-indigo-500">
                  <i :class="action.icon || 'fas fa-bolt'"></i>
                </div>
                <div class="ml-4">
                  <h3 class="text-sm font-medium text-gray-900">{{ action.label }}</h3>
                  <div class="text-sm text-gray-500 flex items-center">
                    <span class="px-2 py-0.5 text-xs bg-gray-100 rounded mr-2">{{ action.type }}</span>
                    <span>处理函数: {{ action.handler }}</span>
                  </div>
                </div>
              </div>
              <div class="flex space-x-2">
                <button 
                  @click="editAction(index)"
                  class="text-indigo-600 hover:text-indigo-900"
                  title="编辑"
                >
                  <i class="fas fa-edit"></i>
                </button>
                <button 
                  @click="deleteAction(index)"
                  class="text-red-600 hover:text-red-900"
                  title="删除"
                >
                  <i class="fas fa-trash-alt"></i>
                </button>
              </div>
            </div>
          </li>
        </ul>
      </div>
      
      <!-- 空状态 -->
      <div v-else-if="!isAddingAction" class="bg-gray-50 border border-dashed border-gray-300 rounded-md p-6 text-center">
        <i class="fas fa-mouse-pointer text-gray-400 text-3xl mb-2"></i>
        <p class="text-sm text-gray-500 mb-2">暂无操作按钮</p>
        <button 
          @click="addAction"
          class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-md text-indigo-700 bg-indigo-100 hover:bg-indigo-200 focus:outline-none"
        >
          <i class="fas fa-plus mr-1"></i> 添加操作
        </button>
      </div>
    </div>
    
    <!-- 基础设置标签页 - 包含分页和导出配置 -->
    <div v-show="activeTab === 'settings'" class="mb-6">
      <!-- 分页配置 -->
      <div class="mb-6">
        <h3 class="text-sm font-medium text-gray-700 mb-2">分页配置</h3>
        
        <div class="bg-white p-4 rounded-md border border-gray-200">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <div class="flex items-center">
                <input 
                  v-model="tableConfig.pagination.enabled"
                  type="checkbox"
                  class="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
                />
                <span class="ml-2 text-sm text-gray-700">启用分页</span>
              </div>
            </div>
            
            <div>
              <label class="block text-xs font-medium text-gray-700 mb-1">
                默认每页条数
              </label>
              <select
                v-model="tableConfig.pagination.pageSize"
                class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                :disabled="!tableConfig.pagination.enabled"
              >
                <option 
                  v-for="size in tableConfig.pagination.pageSizeOptions" 
                  :key="size" 
                  :value="size"
                >
                  {{ size }} 条/页
                </option>
              </select>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 导出配置 -->
      <div>
        <h3 class="text-sm font-medium text-gray-700 mb-2">导出配置</h3>
        
        <div class="bg-white p-4 rounded-md border border-gray-200">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <div class="flex items-center">
                <input 
                  v-model="tableConfig.export.enabled"
                  type="checkbox"
                  class="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
                />
                <span class="ml-2 text-sm text-gray-700">启用导出功能</span>
              </div>
            </div>
            
            <div>
              <label class="block text-xs font-medium text-gray-700 mb-1">
                最大导出行数
              </label>
              <input 
                v-model="tableConfig.export.maxRows"
                type="number"
                min="1"
                max="100000"
                class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                :disabled="!tableConfig.export.enabled"
              />
            </div>
            
            <div class="md:col-span-2">
              <label class="block text-xs font-medium text-gray-700 mb-1">
                支持的导出格式
              </label>
              <div class="mt-1 space-y-2">
                <div class="flex items-center">
                  <input 
                    type="checkbox"
                    :checked="tableConfig.export.formats.includes('CSV')"
                    @change="(e) => {
                      if (e.target.checked) {
                        tableConfig.export.formats.push('CSV');
                      } else {
                        tableConfig.export.formats = tableConfig.export.formats.filter(f => f !== 'CSV');
                      }
                    }"
                    class="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
                    :disabled="!tableConfig.export.enabled"
                  />
                  <span class="ml-2 text-sm text-gray-700">CSV格式</span>
                </div>
                <div class="flex items-center">
                  <input 
                    type="checkbox"
                    :checked="tableConfig.export.formats.includes('EXCEL')"
                    @change="(e) => {
                      if (e.target.checked) {
                        tableConfig.export.formats.push('EXCEL');
                      } else {
                        tableConfig.export.formats = tableConfig.export.formats.filter(f => f !== 'EXCEL');
                      }
                    }"
                    class="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
                    :disabled="!tableConfig.export.enabled"
                  />
                  <span class="ml-2 text-sm text-gray-700">Excel格式</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 批量操作配置标签页 -->
    <div v-show="activeTab === 'batchActions'" class="card mb-4">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0">批量操作配置</h5>
        <div class="form-check form-switch">
          <input
            class="form-check-input"
            type="checkbox"
            id="enableBatchActions"
            :checked="tableConfig.batchActions.length > 0"
            @change="toggleBatchActions"
          />
          <label class="form-check-label" for="enableBatchActions">启用批量操作</label>
        </div>
      </div>
      <div class="card-body">
        <div v-if="tableConfig.batchActions.length > 0">
          <div v-if="isEditingBatchAction">
            <h6 class="mb-3">{{ editingBatchAction?.id ? '编辑' : '添加' }}批量操作</h6>
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">操作名称</label>
                <input
                  type="text"
                  class="form-control"
                  v-model="editingBatchAction.label"
                  placeholder="例如: 批量删除、批量导出"
                />
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">图标</label>
                <input
                  type="text"
                  class="form-control"
                  v-model="editingBatchAction.icon"
                  placeholder="例如: fas fa-trash"
                />
              </div>
            </div>
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">类型</label>
                <select class="form-select" v-model="editingBatchAction.type">
                  <option v-for="option in actionTypeOptions" :key="option.value" :value="option.value">
                    {{ option.label }}
                  </option>
                </select>
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">处理函数</label>
                <input
                  type="text"
                  class="form-control"
                  v-model="editingBatchAction.handler"
                  placeholder="处理函数名称"
                />
              </div>
            </div>
            <div class="row">
              <div class="col-md-6 mb-3">
                <div class="form-check">
                  <input
                    class="form-check-input"
                    type="checkbox"
                    id="requiresSelection"
                    v-model="editingBatchAction.requiresSelection"
                  />
                  <label class="form-check-label" for="requiresSelection">
                    需要选择记录
                  </label>
                </div>
              </div>
              <div class="col-md-6 mb-3">
                <div class="form-check">
                  <input
                    class="form-check-input"
                    type="checkbox"
                    id="confirmationRequired"
                    v-model="editingBatchAction.confirmationRequired"
                  />
                  <label class="form-check-label" for="confirmationRequired">
                    需要确认
                  </label>
                </div>
              </div>
            </div>
            <div class="mb-3" v-if="editingBatchAction.confirmationRequired">
              <label class="form-label">确认信息</label>
              <input
                type="text"
                class="form-control"
                v-model="editingBatchAction.confirmationMessage"
                placeholder="确认信息，例如: 确定要删除选中的记录吗?"
              />
            </div>
            <div class="d-flex justify-content-end gap-2">
              <button type="button" class="btn btn-secondary" @click="editingBatchAction = null; isEditingBatchAction = false">
                取消
              </button>
              <button type="button" class="btn btn-primary" @click="saveBatchAction">
                保存
              </button>
            </div>
          </div>
          <div v-else>
            <button type="button" class="btn btn-primary mb-3" @click="addBatchAction">
              <i class="fas fa-plus me-1"></i> 添加批量操作
            </button>
            <div v-if="tableConfig.batchActions.length > 0">
              <div class="table-responsive">
                <table class="table table-striped table-hover">
                  <thead>
                    <tr>
                      <th>名称</th>
                      <th>类型</th>
                      <th>处理函数</th>
                      <th>需要选择</th>
                      <th>需要确认</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="action in tableConfig.batchActions" :key="action.id">
                      <td>
                        <span v-if="action.icon" class="me-1"><i :class="action.icon"></i></span>
                        {{ action.label }}
                      </td>
                      <td>{{ action.type }}</td>
                      <td>{{ action.handler }}</td>
                      <td>{{ action.requiresSelection ? '是' : '否' }}</td>
                      <td>{{ action.confirmationRequired ? '是' : '否' }}</td>
                      <td>
                        <div class="btn-group btn-group-sm">
                          <button
                            type="button"
                            class="btn btn-outline-primary"
                            @click="editingBatchAction = { ...action }; isEditingBatchAction = true;"
                          >
                            <i class="fas fa-edit"></i>
                          </button>
                          <button
                            type="button"
                            class="btn btn-outline-danger"
                            @click="deleteBatchAction(action.id)"
                          >
                            <i class="fas fa-trash"></i>
                          </button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
            <div v-else class="alert alert-info">
              暂无批量操作，点击"添加批量操作"按钮添加。
            </div>
          </div>
        </div>
        <div v-else class="alert alert-secondary">
          启用批量操作功能，允许用户对表格中的多条记录进行批量操作，如批量删除、批量导出等。
        </div>
      </div>
    </div>

    <!-- 高级筛选配置标签页 -->
    <div v-show="activeTab === 'filters'" class="card mb-4">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0">高级筛选配置</h5>
        <div class="form-check form-switch">
          <input
            class="form-check-input"
            type="checkbox"
            id="enableAdvancedFilters"
            v-model="tableConfig.advancedFilters.enabled"
          />
          <label class="form-check-label" for="enableAdvancedFilters">启用高级筛选</label>
        </div>
      </div>
      <div class="card-body">
        <div v-if="tableConfig.advancedFilters.enabled">
          <div v-if="isEditingFilter">
            <h6 class="mb-3">{{ editingFilter?.id ? '编辑' : '添加' }}筛选器</h6>
            <div class="mb-3">
              <label class="form-label">筛选器名称</label>
              <input
                type="text"
                class="form-control"
                v-model="editingFilter.name"
                placeholder="例如: 活跃客户、高价值订单"
              />
            </div>
            <div class="mb-3">
              <div class="form-check">
                <input
                  class="form-check-input"
                  type="checkbox"
                  id="isDefaultFilter"
                  v-model="editingFilter.isDefault"
                />
                <label class="form-check-label" for="isDefaultFilter">
                  设为默认筛选器
                </label>
              </div>
            </div>
            <div class="mb-3">
              <label class="form-label">筛选条件</label>
              <div v-for="(condition, index) in editingFilter.conditions" :key="index" class="card mb-2">
                <div class="card-body p-3">
                  <div class="row mb-2">
                    <div class="col-md-5">
                      <label class="form-label small">字段</label>
                      <select class="form-select" v-model="condition.field">
                        <option value="">请选择字段</option>
                        <option v-for="column in availableColumns" :key="column.field" :value="column.field">
                          {{ column.label }}
                        </option>
                      </select>
                    </div>
                    <div class="col-md-4">
                      <label class="form-label small">操作符</label>
                      <select class="form-select" v-model="condition.operator">
                        <option v-for="op in filterConditionTypes" :key="op.value" :value="op.value">
                          {{ op.label }}
                        </option>
                      </select>
                    </div>
                    <div class="col-md-3">
                      <label class="form-label small">逻辑</label>
                      <select class="form-select" v-model="condition.logic">
                        <option value="AND">并且 (AND)</option>
                        <option value="OR">或者 (OR)</option>
                      </select>
                    </div>
                  </div>
                  <div class="row mb-2">
                    <div class="col-md-9" v-if="!['isNull', 'isNotNull'].includes(condition.operator)">
                      <label class="form-label small">值</label>
                      <input
                        type="text"
                        class="form-control"
                        v-model="condition.value"
                        placeholder="输入值"
                      />
                    </div>
                    <div class="col-md-3 d-flex align-items-end">
                      <button
                        type="button"
                        class="btn btn-outline-danger btn-sm"
                        @click="removeConditionFromFilter(index)"
                        :disabled="editingFilter.conditions.length <= 1"
                      >
                        <i class="fas fa-trash me-1"></i> 删除
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              <button type="button" class="btn btn-outline-secondary btn-sm" @click="addConditionToFilter">
                <i class="fas fa-plus me-1"></i> 添加条件
              </button>
            </div>
            <div class="d-flex justify-content-end gap-2">
              <button type="button" class="btn btn-secondary" @click="editingFilter = null; isEditingFilter = false">
                取消
              </button>
              <button type="button" class="btn btn-primary" @click="saveAdvancedFilter">
                保存
              </button>
            </div>
          </div>
          <div v-else>
            <button type="button" class="btn btn-primary mb-3" @click="addAdvancedFilter">
              <i class="fas fa-plus me-1"></i> 添加筛选器
            </button>
            <div v-if="tableConfig.advancedFilters.savedFilters.length > 0">
              <div class="table-responsive">
                <table class="table table-striped table-hover">
                  <thead>
                    <tr>
                      <th>名称</th>
                      <th>条件数量</th>
                      <th>默认筛选器</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="filter in tableConfig.advancedFilters.savedFilters" :key="filter.id">
                      <td>{{ filter.name }}</td>
                      <td>{{ filter.conditions.length }}</td>
                      <td>{{ tableConfig.advancedFilters.defaultFilters.includes(filter.id) ? '是' : '否' }}</td>
                      <td>
                        <div class="btn-group btn-group-sm">
                          <button
                            type="button"
                            class="btn btn-outline-primary"
                            @click="editingFilter = { ...filter }; isEditingFilter = true;"
                          >
                            <i class="fas fa-edit"></i>
                          </button>
                          <button
                            type="button"
                            class="btn btn-outline-danger"
                            @click="deleteAdvancedFilter(filter.id)"
                          >
                            <i class="fas fa-trash"></i>
                          </button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
            <div v-else class="alert alert-info">
              暂无保存的筛选器，点击"添加筛选器"按钮创建。
            </div>
          </div>
        </div>
        <div v-else class="alert alert-secondary">
          启用高级筛选功能，允许用户使用复杂的条件组合对表格数据进行过滤。
        </div>
      </div>
    </div>

    <!-- 数据聚合配置标签页 -->
    <div v-show="activeTab === 'aggregation'" class="card mb-4">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0">数据聚合配置</h5>
        <div class="form-check form-switch">
          <input
            class="form-check-input"
            type="checkbox"
            id="enableAggregation"
            v-model="tableConfig.aggregation.enabled"
          />
          <label class="form-check-label" for="enableAggregation">启用数据聚合</label>
        </div>
      </div>
      <div class="card-body">
        <div v-if="tableConfig.aggregation.enabled">
          <div v-if="isEditingAggregation">
            <h6 class="mb-3">聚合配置</h6>
            <div class="mb-3">
              <label class="form-label">分组字段</label>
              <select class="form-select" multiple v-model="tableConfig.aggregation.groupByFields">
                <option v-for="column in availableColumns" :key="column.field" :value="column.field">
                  {{ column.label }}
                </option>
              </select>
              <div class="form-text">选择要按哪些字段进行分组，可多选</div>
            </div>
            <div class="mb-3">
              <label class="form-label">聚合函数</label>
              <div class="table-responsive">
                <table class="table table-bordered">
                  <thead>
                    <tr>
                      <th>字段</th>
                      <th>函数</th>
                      <th>别名</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(func, index) in tableConfig.aggregation.aggregationFunctions" :key="index">
                      <td>
                        <select class="form-select" v-model="func.field">
                          <option v-for="column in availableColumns" :key="column.field" :value="column.field">
                            {{ column.label }}
                          </option>
                        </select>
                      </td>
                      <td>
                        <select class="form-select" v-model="func.function">
                          <option v-for="agg in aggregationFunctions" :key="agg.value" :value="agg.value">
                            {{ agg.label }}
                          </option>
                        </select>
                      </td>
                      <td>
                        <input type="text" class="form-control" v-model="func.alias" placeholder="显示名称" />
                      </td>
                      <td>
                        <button
                          type="button"
                          class="btn btn-outline-danger btn-sm"
                          @click="tableConfig.aggregation.aggregationFunctions.splice(index, 1)"
                        >
                          <i class="fas fa-trash"></i>
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <button
                type="button"
                class="btn btn-outline-secondary btn-sm"
                @click="tableConfig.aggregation.aggregationFunctions.push({
                  field: '',
                  function: 'sum',
                  alias: ''
                })"
              >
                <i class="fas fa-plus me-1"></i> 添加聚合函数
              </button>
            </div>
            <div class="d-flex justify-content-end gap-2">
              <button type="button" class="btn btn-secondary" @click="isEditingAggregation = false">
                取消
              </button>
              <button type="button" class="btn btn-primary" @click="saveAggregationSettings">
                保存
              </button>
            </div>
          </div>
          <div v-else>
            <button type="button" class="btn btn-primary mb-3" @click="editAggregationSettings">
              <i class="fas fa-cog me-1"></i> 配置聚合设置
            </button>
            <div v-if="tableConfig.aggregation.groupByFields.length > 0 || tableConfig.aggregation.aggregationFunctions.length > 0" class="mb-3">
              <div class="card mb-3">
                <div class="card-header">分组字段</div>
                <div class="card-body">
                  <div v-if="tableConfig.aggregation.groupByFields.length > 0">
                    <ul class="list-group list-group-flush">
                      <li v-for="field in tableConfig.aggregation.groupByFields" :key="field" class="list-group-item">
                        {{ availableColumns.find(c => c.field === field)?.label || field }}
                      </li>
                    </ul>
                  </div>
                  <div v-else>
                    未配置分组字段
                  </div>
                </div>
              </div>
              <div class="card">
                <div class="card-header">聚合函数</div>
                <div class="card-body">
                  <div v-if="tableConfig.aggregation.aggregationFunctions.length > 0">
                    <ul class="list-group list-group-flush">
                      <li v-for="(func, index) in tableConfig.aggregation.aggregationFunctions" :key="index" class="list-group-item">
                        {{ func.alias || (availableColumns.find(c => c.field === func.field)?.label + ' ' + 
                          aggregationFunctions.find(af => af.value === func.function)?.label) }}
                      </li>
                    </ul>
                  </div>
                  <div v-else>
                    未配置聚合函数
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="alert alert-info">
              未配置数据聚合，点击"配置聚合设置"按钮进行设置。
            </div>
          </div>
        </div>
        <div v-else class="alert alert-secondary">
          启用数据聚合功能，允许用户对表格数据进行分组和统计分析，如计算总和、平均值等。
        </div>
      </div>
    </div>
  </div>
</template>