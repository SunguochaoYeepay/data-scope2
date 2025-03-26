import type { Integration } from '@/types/integration';
import { ColumnAlign, FormComponentType } from '@/types/integration';

// 生成随机ID
const generateId = (): string => {
  return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
};

// 生成随机日期
const generateDate = (start: Date = new Date(2023, 0, 1), end: Date = new Date()): string => {
  const date = new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()));
  return date.toISOString();
};

// 模拟集成数据
export const mockIntegrations: Integration[] = [
  {
    id: 'int-001',
    name: '员工信息查询表单',
    description: '用于查询员工基本信息的集成表单',
    type: 'FORM',
    status: 'ACTIVE',
    queryId: 'query-001',
    formConfig: {
      layout: 'horizontal',
      conditions: [
        {
          field: 'employee_id',
          label: '员工编号',
          type: FormComponentType.INPUT,
          required: true,
          displayOrder: 0,
          visibility: 'visible'
        },
        {
          field: 'department',
          label: '部门',
          type: FormComponentType.SELECT,
          required: false,
          displayOrder: 1,
          visibility: 'visible',
          componentProps: {
            options: [
              { label: '研发部', value: 'RD' },
              { label: '市场部', value: 'MKT' },
              { label: '销售部', value: 'SALES' },
              { label: '人力资源部', value: 'HR' }
            ]
          }
        },
        {
          field: 'hire_date',
          label: '入职日期',
          type: FormComponentType.DATE,
          required: false,
          displayOrder: 2,
          visibility: 'visible'
        }
      ],
      buttons: [
        {
          type: 'submit',
          label: '查询',
          style: 'primary',
          icon: 'fas fa-search'
        },
        {
          type: 'reset',
          label: '重置',
          style: 'secondary'
        }
      ]
    },
    integrationPoint: {
      id: 'ip-001',
      name: '员工信息API',
      type: 'URL',
      urlConfig: {
        url: 'https://api.example.com/employees',
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'X-API-Key': 'mock-api-key'
        }
      }
    },
    createTime: generateDate(new Date(2023, 0, 1)),
    updateTime: generateDate(new Date(2023, 6, 1))
  },
  {
    id: 'int-002',
    name: '销售数据表格',
    description: '用于展示销售数据的集成表格',
    type: 'TABLE',
    status: 'ACTIVE',
    queryId: 'query-002',
    tableConfig: {
      columns: [
        {
          field: 'product_id',
          label: '产品编号',
          type: 'TEXT',
          sortable: true,
          filterable: true,
          align: ColumnAlign.LEFT,
          visible: true,
          displayOrder: 0
        },
        {
          field: 'product_name',
          label: '产品名称',
          type: 'TEXT',
          sortable: true,
          filterable: true,
          align: ColumnAlign.LEFT,
          visible: true,
          displayOrder: 1
        },
        {
          field: 'category',
          label: '类别',
          type: 'TEXT',
          sortable: true,
          filterable: true,
          align: ColumnAlign.CENTER,
          visible: true,
          displayOrder: 2
        },
        {
          field: 'sales_amount',
          label: '销售额',
          type: 'NUMBER',
          sortable: true,
          filterable: true,
          align: ColumnAlign.RIGHT,
          visible: true,
          displayOrder: 3
        },
        {
          field: 'sale_date',
          label: '销售日期',
          type: 'DATE',
          sortable: true,
          filterable: true,
          align: ColumnAlign.CENTER,
          visible: true,
          displayOrder: 4
        }
      ],
      actions: [
        {
          type: 'button',
          label: '查看详情',
          handler: 'viewDetails',
          icon: 'fas fa-eye'
        },
        {
          type: 'button',
          label: '编辑',
          handler: 'editRecord',
          icon: 'fas fa-edit'
        }
      ],
      pagination: {
        enabled: true,
        pageSize: 10,
        pageSizeOptions: [10, 20, 50, 100]
      },
      export: {
        enabled: true,
        formats: ['CSV', 'EXCEL'],
        maxRows: 1000
      }
    },
    integrationPoint: {
      id: 'ip-002',
      name: '销售数据API',
      type: 'URL',
      urlConfig: {
        url: 'https://api.example.com/sales',
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'X-API-Key': 'mock-api-key'
        }
      }
    },
    createTime: generateDate(new Date(2023, 1, 15)),
    updateTime: generateDate(new Date(2023, 7, 20))
  },
  {
    id: 'int-003',
    name: '客户反馈表单',
    description: '用于收集客户反馈的集成表单',
    type: 'FORM',
    status: 'DRAFT',
    queryId: 'query-003',
    formConfig: {
      layout: 'vertical',
      conditions: [
        {
          field: 'customer_name',
          label: '客户姓名',
          type: FormComponentType.INPUT,
          required: true,
          displayOrder: 0,
          visibility: 'visible'
        },
        {
          field: 'email',
          label: '电子邮箱',
          type: FormComponentType.INPUT,
          required: true,
          displayOrder: 1,
          visibility: 'visible'
        },
        {
          field: 'product',
          label: '产品',
          type: FormComponentType.SELECT,
          required: true,
          displayOrder: 2,
          visibility: 'visible',
          componentProps: {
            options: [
              { label: '产品A', value: 'A' },
              { label: '产品B', value: 'B' },
              { label: '产品C', value: 'C' }
            ]
          }
        },
        {
          field: 'rating',
          label: '评分',
          type: FormComponentType.RADIO,
          required: true,
          displayOrder: 3,
          visibility: 'visible',
          componentProps: {
            options: [
              { label: '很满意', value: '5' },
              { label: '满意', value: '4' },
              { label: '一般', value: '3' },
              { label: '不满意', value: '2' },
              { label: '很不满意', value: '1' }
            ]
          }
        },
        {
          field: 'feedback',
          label: '反馈内容',
          type: FormComponentType.TEXTAREA,
          required: true,
          displayOrder: 4,
          visibility: 'visible'
        }
      ],
      buttons: [
        {
          type: 'submit',
          label: '提交反馈',
          style: 'primary',
          icon: 'fas fa-paper-plane'
        },
        {
          type: 'reset',
          label: '清空',
          style: 'secondary'
        }
      ]
    },
    integrationPoint: {
      id: 'ip-003',
      name: '客户反馈提交',
      type: 'FORM_SUBMIT',
      formSubmitConfig: {
        formId: 'form-001',
        submitAction: 'submit',
        successMessage: '感谢您的反馈，我们将尽快处理！',
        errorMessage: '提交失败，请稍后重试。'
      }
    },
    createTime: generateDate(new Date(2023, 2, 10)),
    updateTime: generateDate(new Date(2023, 2, 10))
  },
  {
    id: 'int-004',
    name: '库存管理表格',
    description: '用于管理产品库存的集成表格',
    type: 'TABLE',
    status: 'INACTIVE',
    queryId: 'query-004',
    tableConfig: {
      columns: [
        {
          field: 'sku',
          label: 'SKU',
          type: 'TEXT',
          sortable: true,
          filterable: true,
          align: ColumnAlign.LEFT,
          visible: true,
          displayOrder: 0
        },
        {
          field: 'product_name',
          label: '产品名称',
          type: 'TEXT',
          sortable: true,
          filterable: true,
          align: ColumnAlign.LEFT,
          visible: true,
          displayOrder: 1
        },
        {
          field: 'quantity',
          label: '库存数量',
          type: 'NUMBER',
          sortable: true,
          filterable: true,
          align: ColumnAlign.RIGHT,
          visible: true,
          displayOrder: 2
        },
        {
          field: 'location',
          label: '库位',
          type: 'TEXT',
          sortable: true,
          filterable: true,
          align: ColumnAlign.CENTER,
          visible: true,
          displayOrder: 3
        },
        {
          field: 'last_updated',
          label: '最后更新',
          type: 'DATETIME',
          sortable: true,
          filterable: true,
          align: ColumnAlign.CENTER,
          visible: true,
          displayOrder: 4
        }
      ],
      actions: [
        {
          type: 'button',
          label: '调整库存',
          handler: 'adjustInventory',
          icon: 'fas fa-edit'
        },
        {
          type: 'button',
          label: '查看历史',
          handler: 'viewHistory',
          icon: 'fas fa-history'
        }
      ],
      pagination: {
        enabled: true,
        pageSize: 20,
        pageSizeOptions: [10, 20, 50, 100]
      },
      export: {
        enabled: true,
        formats: ['CSV', 'EXCEL'],
        maxRows: 2000
      }
    },
    integrationPoint: {
      id: 'ip-004',
      name: '库存管理API',
      type: 'URL',
      urlConfig: {
        url: 'https://api.example.com/inventory',
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'X-API-Key': 'mock-api-key'
        }
      }
    },
    createTime: generateDate(new Date(2023, 3, 5)),
    updateTime: generateDate(new Date(2023, 8, 15))
  }
];

// 模拟表格数据
export const mockTableData = [
  { product_id: 'P001', product_name: '笔记本电脑', category: '电子产品', sales_amount: 5999, sale_date: '2023-01-15' },
  { product_id: 'P002', product_name: '智能手机', category: '电子产品', sales_amount: 3999, sale_date: '2023-01-16' },
  { product_id: 'P003', product_name: '无线耳机', category: '电子配件', sales_amount: 899, sale_date: '2023-01-16' },
  { product_id: 'P004', product_name: '机械键盘', category: '电子配件', sales_amount: 499, sale_date: '2023-01-17' },
  { product_id: 'P005', product_name: '鼠标', category: '电子配件', sales_amount: 199, sale_date: '2023-01-17' },
  { product_id: 'P006', product_name: '显示器', category: '电子产品', sales_amount: 1999, sale_date: '2023-01-18' },
  { product_id: 'P007', product_name: 'U盘', category: '存储设备', sales_amount: 99, sale_date: '2023-01-18' },
  { product_id: 'P008', product_name: '移动硬盘', category: '存储设备', sales_amount: 399, sale_date: '2023-01-19' },
  { product_id: 'P009', product_name: '路由器', category: '网络设备', sales_amount: 299, sale_date: '2023-01-19' },
  { product_id: 'P010', product_name: '打印机', category: '办公设备', sales_amount: 999, sale_date: '2023-01-20' }
];

// 获取模拟集成数据
export const getMockIntegrations = (): Promise<Integration[]> => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve([...mockIntegrations]);
    }, 500);
  });
};

// 获取单个集成数据
export const getMockIntegrationById = (id: string): Promise<Integration | null> => {
  return new Promise((resolve) => {
    setTimeout(() => {
      const integration = mockIntegrations.find(item => item.id === id);
      resolve(integration || null);
    }, 300);
  });
};

// 创建模拟集成
export const createMockIntegration = (integration: Partial<Integration>): Promise<Integration> => {
  return new Promise((resolve) => {
    setTimeout(() => {
      const newIntegration: Integration = {
        id: `int-${generateId()}`,
        name: integration.name || '',
        description: integration.description || '',
        type: integration.type || 'FORM',
        status: integration.status || 'DRAFT',
        queryId: integration.queryId || '',
        formConfig: integration.formConfig,
        tableConfig: integration.tableConfig,
        integrationPoint: integration.integrationPoint || {
          id: `ip-${generateId()}`,
          name: '',
          type: 'URL',
          urlConfig: {
            url: '',
            method: 'GET',
            headers: {}
          }
        },
        createTime: new Date().toISOString(),
        updateTime: new Date().toISOString()
      };
      
      mockIntegrations.push(newIntegration);
      resolve(newIntegration);
    }, 600);
  });
};

// 更新模拟集成
export const updateMockIntegration = (id: string, integration: Partial<Integration>): Promise<Integration | null> => {
  return new Promise((resolve) => {
    setTimeout(() => {
      const index = mockIntegrations.findIndex(item => item.id === id);
      
      if (index !== -1) {
        const updatedIntegration = {
          ...mockIntegrations[index],
          ...integration,
          updateTime: new Date().toISOString()
        };
        
        mockIntegrations[index] = updatedIntegration;
        resolve(updatedIntegration);
      } else {
        resolve(null);
      }
    }, 600);
  });
};

// 删除模拟集成
export const deleteMockIntegration = (id: string): Promise<boolean> => {
  return new Promise((resolve) => {
    setTimeout(() => {
      const index = mockIntegrations.findIndex(item => item.id === id);
      
      if (index !== -1) {
        mockIntegrations.splice(index, 1);
        resolve(true);
      } else {
        resolve(false);
      }
    }, 400);
  });
};

// 模拟查询执行
export const executeMockQuery = (queryId: string, params: any): Promise<any> => {
  return new Promise((resolve) => {
    setTimeout(() => {
      // 根据不同的查询ID返回不同的数据
      if (queryId === 'query-002') {
        const pageSize = params.pageSize || 10;
        const page = params.page || 1;
        
        const start = (page - 1) * pageSize;
        const end = start + pageSize;
        
        const data = mockTableData.slice(start, end);
        
        resolve({
          data,
          total: mockTableData.length,
          page,
          pageSize
        });
      } else {
        resolve({
          data: [],
          total: 0,
          page: 1,
          pageSize: 10
        });
      }
    }, 800);
  });
};