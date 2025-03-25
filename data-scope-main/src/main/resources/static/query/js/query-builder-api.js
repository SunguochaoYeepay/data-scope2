/**
 * DataScope Query Builder - API Module
 * 处理所有与API相关的功能
 */

// API Base URL
const API_BASE_URL = '/api';

// API Endpoints
const API = {
  DATASOURCES: `${API_BASE_URL}/datasources`,
  DATASOURCE_METADATA: (id) => `${API_BASE_URL}/datasources/${id}/metadata`,
  QUERIES: `${API_BASE_URL}/queries`,
  QUERY: (id) => `${API_BASE_URL}/queries/${id}`,
  EXECUTE_QUERY: `${API_BASE_URL}/query/execute`,
  VALIDATE_QUERY: `${API_BASE_URL}/query/validate`
};

// 加载数据源
async function loadDataSources() {
  try {
    showLoading();

    const response = await fetch(API.DATASOURCES);

    if (!response.ok) {
      throw new Error('Failed to load data sources');
    }

    const result = await response.json();

    if (!result.success) {
      throw new Error(result.message || 'Failed to load data sources');
    }

    const dataSources = result.data;

    // 更新数据源选择器
    const dataSourceSelect = document.getElementById('dataSourceSelect');
    if (dataSourceSelect) {
      // 清除现有选项
      dataSourceSelect.innerHTML = '<option value="">选择数据源</option>';

      // 添加数据源
      dataSources.forEach(dataSource => {
        const option = document.createElement('option');
        option.value = dataSource.id;
        option.textContent = dataSource.name;
        dataSourceSelect.appendChild(option);
      });

      // 设置选中的数据源（如果存在）
      if (state.dataSource) {
        dataSourceSelect.value = state.dataSource.id;
      }
    }

    hideLoading();
  } catch (error) {
    console.error('Error loading data sources:', error);
    showError('加载数据源失败: ' + error.message);
    hideLoading();
  }
}

// 加载数据源元数据
async function loadDataSourceMetadata(dataSourceId) {
  try {
    showLoading();

    const response = await fetch(API.DATASOURCE_METADATA(dataSourceId));

    if (!response.ok) {
      throw new Error('Failed to load data source metadata');
    }

    const result = await response.json();

    if (!result.success) {
      throw new Error(result.message || 'Failed to load data source metadata');
    }

    const metadata = result.data;

    // 更新状态
    state.tables = metadata.tables || [];
    state.fields = {};

    // 处理字段
    if (metadata.tables) {
      metadata.tables.forEach(table => {
        if (table.columns) {
          state.fields[table.name] = table.columns;
        }
      });
    }

    // 更新表格树
    updateTablesTree();

    // 更新表格选择器
    updateTableSelect();

    hideLoading();
  } catch (error) {
    console.error('Error loading data source metadata:', error);
    showError('加载数据源元数据失败: ' + error.message);
    hideLoading();
  }
}

// 加载保存的查询
async function loadSavedQueries() {
  try {
    const response = await fetch(API.QUERIES);

    if (!response.ok) {
      throw new Error('Failed to load saved queries');
    }

    const result = await response.json();

    if (!result.success) {
      throw new Error(result.message || 'Failed to load saved queries');
    }

    const queries = result.data;

    // 更新状态
    state.savedQueries = queries;

    // 更新保存的查询列表
    updateSavedQueriesList();
  } catch (error) {
    console.error('Error loading saved queries:', error);
    showError('加载保存的查询失败: ' + error.message);
  }
}

// 加载查询
async function loadQuery(queryId) {
  try {
    showLoading();

    const response = await fetch(API.QUERY(queryId));

    if (!response.ok) {
      throw new Error('Failed to load query');
    }

    const result = await response.json();

    if (!result.success) {
      throw new Error(result.message || 'Failed to load query');
    }

    const query = result.data;

    // 更新状态
    state.queryId = query.id;
    state.queryName = query.name;
    state.queryDescription = query.description || '';
    state.sql = query.sql;

    // 更新UI
    const queryNameInput = document.getElementById('queryName');
    if (queryNameInput) {
      queryNameInput.value = state.queryName;
    }

    const queryDescriptionInput = document.getElementById('queryDescription');
    if (queryDescriptionInput) {
      queryDescriptionInput.value = state.queryDescription;
    }

    // 加载数据源
    if (query.dataSourceId) {
      // 选择数据源
      const dataSourceSelect = document.getElementById('dataSourceSelect');
      if (dataSourceSelect) {
        dataSourceSelect.value = query.dataSourceId;
      }

      // 触发数据源变更
      await handleDataSourceChange({target: {value: query.dataSourceId}});
    }

    // 显示SQL预览
    const sqlPreview = document.getElementById('sqlPreview');
    const sqlPreviewContainer = document.getElementById('sqlPreviewContainer');
    if (sqlPreview && sqlPreviewContainer) {
      sqlPreview.textContent = state.sql;
      sqlPreviewContainer.classList.remove('hidden');
    }

    hideLoading();
    showSuccess('查询已加载');
  } catch (error) {
    console.error('Error loading query:', error);
    showError('加载查询失败: ' + error.message);
    hideLoading();
  }
}

// 保存查询
async function saveQuery() {
  try {
    showLoading();

    const queryNameInput = document.getElementById('queryName');
    const queryDescriptionInput = document.getElementById('queryDescription');

    if (!queryNameInput.value) {
      throw new Error('请输入查询名称');
    }

    if (!state.dataSource) {
      throw new Error('请选择数据源');
    }

    if (!state.sql) {
      throw new Error('请构建查询');
    }

    const query = {
      id: state.queryId,
      name: queryNameInput.value,
      description: queryDescriptionInput.value,
      dataSourceId: state.dataSource.id,
      sql: state.sql
    };

    const url = state.queryId ? API.QUERY(state.queryId) : API.QUERIES;
    const method = state.queryId ? 'PUT' : 'POST';

    const response = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(query)
    });

    if (!response.ok) {
      throw new Error('Failed to save query');
    }

    const result = await response.json();

    if (!result.success) {
      throw new Error(result.message || 'Failed to save query');
    }

    // 更新状态
    state.queryId = result.data.id;

    hideLoading();
    showSuccess('查询已保存');

    // 重新加载保存的查询
    loadSavedQueries();
  } catch (error) {
    console.error('Error saving query:', error);
    showError('保存查询失败: ' + error.message);
    hideLoading();
  }
}

// 执行查询
async function executeQuery() {
  try {
    showLoading();

    if (!state.dataSource) {
      throw new Error('请选择数据源');
    }

    if (!state.sql) {
      throw new Error('请构建查询');
    }

    const request = {
      dataSourceId: state.dataSource.id,
      sql: state.sql,
      parameters: {}
    };

    const response = await fetch(API.EXECUTE_QUERY, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(request)
    });

    if (!response.ok) {
      throw new Error('Failed to execute query');
    }

    const result = await response.json();

    if (!result.success) {
      throw new Error(result.message || 'Failed to execute query');
    }

    // 更新状态
    state.results = result.data;

    // 显示结果
    displayResults();

    hideLoading();
  } catch (error) {
    console.error('Error executing query:', error);
    showError('执行查询失败: ' + error.message);
    hideLoading();
  }
}

// 验证查询
async function validateQuery() {
  try {
    showLoading();

    if (!state.dataSource) {
      throw new Error('请选择数据源');
    }

    if (!state.sql) {
      throw new Error('请构建查询');
    }

    const request = {
      dataSourceId: state.dataSource.id,
      sql: state.sql
    };

    const response = await fetch(API.VALIDATE_QUERY, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(request)
    });

    if (!response.ok) {
      throw new Error('Failed to validate query');
    }

    const result = await response.json();

    if (!result.success) {
      throw new Error(result.message || 'Failed to validate query');
    }

    hideLoading();
    showSuccess('查询验证通过');
  } catch (error) {
    console.error('Error validating query:', error);
    showError('验证查询失败: ' + error.message);
    hideLoading();
  }
}
