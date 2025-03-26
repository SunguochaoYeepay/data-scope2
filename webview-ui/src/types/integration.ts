// 集成类型定义文件

// 表单组件类型枚举
export enum FormComponentType {
  INPUT = 'INPUT',
  NUMBER = 'NUMBER',
  DATE = 'DATE',
  DATETIME = 'DATETIME',
  SELECT = 'SELECT',
  MULTISELECT = 'MULTISELECT',
  CHECKBOX = 'CHECKBOX',
  RADIO = 'RADIO',
  TEXTAREA = 'TEXTAREA'
}

// 表格列对齐方式
export enum ColumnAlign {
  LEFT = 'left',
  CENTER = 'center',
  RIGHT = 'right'
}

// 表单布局类型
export type FormLayoutType = 'horizontal' | 'vertical' | 'grid';

// 表单条件
export interface FormCondition {
  field: string;
  label: string;
  type: FormComponentType;
  required?: boolean;
  defaultValue?: any;
  displayOrder: number;
  visibility?: 'visible' | 'hidden' | 'readonly';
  componentProps?: Record<string, any>;
}

// 表单按钮
export interface FormButton {
  type: 'submit' | 'reset' | 'button';
  label: string;
  style?: 'primary' | 'secondary' | 'danger' | 'warning' | 'info';
  icon?: string;
  handler?: string;
}

// 表单配置
export interface FormConfig {
  layout: FormLayoutType;
  conditions: FormCondition[];
  buttons: FormButton[];
}

// 表格列
export interface TableColumn {
  field: string;
  label: string;
  type: string;
  sortable?: boolean;
  filterable?: boolean;
  align: ColumnAlign;
  width?: string;
  maskType?: string;
  format?: string;
  visible: boolean;
  displayOrder: number;
  filterConfig?: ColumnFilterConfig;
}

// 表格操作
export interface TableAction {
  type: 'button' | 'link' | 'menu';
  label: string;
  handler: string;
  icon?: string;
  style?: 'primary' | 'secondary' | 'danger' | 'warning' | 'info';
  confirm?: boolean;
  confirmMessage?: string;
}

// 分页配置
export interface PaginationConfig {
  enabled: boolean;
  pageSize: number;
  pageSizeOptions: number[];
}

// 导出配置
export interface ExportConfig {
  enabled: boolean;
  formats: string[];
  maxRows: number;
}

// 批量操作
export interface BatchAction {
  id: string;
  label: string;
  icon?: string;
  type: 'button' | 'link' | 'menu';
  handler: string;
  requiresSelection: boolean;
  confirmationRequired: boolean;
  confirmationMessage?: string;
}

// 聚合配置
export interface AggregationConfig {
  enabled: boolean;
  groupByFields: string[];
  aggregationFunctions: Array<{
    field: string;
    function: 'sum' | 'avg' | 'min' | 'max' | 'count' | 'countDistinct';
    alias: string;
  }>;
}

// 过滤条件
export interface FilterCondition {
  field: string;
  operator: string;
  value: any;
  logic: 'AND' | 'OR';
}

// 保存的过滤器
export interface SavedFilter {
  id: string;
  name: string;
  conditions: FilterCondition[];
  isDefault: boolean;
}

// 高级过滤配置
export interface AdvancedFiltersConfig {
  enabled: boolean;
  defaultFilters: string[];
  savedFilters: SavedFilter[];
}

// 列过滤配置
export interface ColumnFilterConfig {
  operators: string[];
  defaultValue: any;
  multiple: boolean;
}

// 表格配置
export interface TableConfig {
  columns: TableColumn[];
  actions: TableAction[];
  pagination: PaginationConfig;
  export: ExportConfig;
  batchActions: BatchAction[];
  aggregation: AggregationConfig;
  advancedFilters: AdvancedFiltersConfig;
}

// URL配置
export interface UrlConfig {
  url: string;
  method: string;
  headers: Record<string, string>;
}

// 表单提交配置
export interface FormSubmitConfig {
  formId: string;
  submitAction: string;
  successMessage?: string;
  errorMessage?: string;
}

// 集成点
export interface IntegrationPoint {
  id: string;
  name: string;
  type: 'URL' | 'FORM_SUBMIT';
  urlConfig?: UrlConfig;
  formSubmitConfig?: FormSubmitConfig;
}

// 集成
export interface Integration {
  id: string;
  name: string;
  description?: string;
  type: 'FORM' | 'TABLE';
  status: 'DRAFT' | 'ACTIVE' | 'INACTIVE';
  queryId: string;
  formConfig?: FormConfig;
  tableConfig?: TableConfig;
  integrationPoint: IntegrationPoint;
  createTime: string;
  updateTime: string;
}