/**
 * DataScope Query List JavaScript
 *
 * This file contains the functionality for the query list page,
 * including loading, filtering, and managing queries.
 */

// API Base URL
const API_BASE_URL = '/api';

// Query API Endpoints
const QUERY_API = {
  LIST: `${API_BASE_URL}/queries`,
  CREATE: `${API_BASE_URL}/queries`,
  GET: (id) => `${API_BASE_URL}/queries/${id}`,
  UPDATE: (id) => `${API_BASE_URL}/queries/${id}`,
  DELETE: (id) => `${API_BASE_URL}/queries/${id}`,
  FAVORITE: (id) => `${API_BASE_URL}/queries/${id}/favorite`,
  SEARCH: `${API_BASE_URL}/queries/search`,
  FILTER_BY_DATASOURCE: (dataSourceId) => `${API_BASE_URL}/queries/filter/datasource/${dataSourceId}`,
  FILTER_BY_USER: (userId) => `${API_BASE_URL}/queries/filter/user/${userId}`,
  FILTER_BY_FAVORITE: `${API_BASE_URL}/queries/filter/favorite`
};

// DOM Elements
const queryListElement = document.querySelector('.divide-y.divide-gray-200');
const searchInput = document.querySelector('input[placeholder="Search queries..."]');
const dataSourceSelect = document.querySelector('select:nth-of-type(1)');
const createdBySelect = document.querySelector('select:nth-of-type(2)');
const sortBySelect = document.querySelector('select:nth-of-type(3)');
const newQueryButton = document.querySelector('button:has(i.fa-plus)');
const favoritesButton = document.querySelector('button:has(i.fa-star)');
const paginationInfo = document.querySelector('.text-sm.text-gray-700');
const paginationContainer = document.querySelector('nav[aria-label="Pagination"]');

// State
let queries = [];
let currentPage = 1;
let pageSize = 10;
let totalPages = 1;
let totalItems = 0;
let searchTerm = '';
let dataSourceFilter = '';
let userFilter = '';
let sortField = 'lastModified';
let sortDirection = 'desc';
let showFavoritesOnly = false;

/**
 * Initialize the query list page
 */
function initQueryList() {
  // Add event listeners
  if (searchInput) {
    searchInput.addEventListener('input', debounce(handleSearch, 300));
  }

  if (dataSourceSelect) {
    dataSourceSelect.addEventListener('change', handleDataSourceFilter);
    loadDataSources();
  }

  if (createdBySelect) {
    createdBySelect.addEventListener('change', handleUserFilter);
  }

  if (sortBySelect) {
    sortBySelect.addEventListener('change', handleSortChange);
  }

  if (newQueryButton) {
    newQueryButton.addEventListener('click', handleNewQuery);
  }

  if (favoritesButton) {
    favoritesButton.addEventListener('click', handleToggleFavorites);
  }

  // Load queries
  loadQueries();
}

/**
 * Load queries from API
 */
async function loadQueries() {
  try {
    // Determine the API endpoint based on filters
    let url = QUERY_API.LIST;
    const params = new URLSearchParams();

    // Add pagination
    params.append('page', currentPage - 1); // API uses 0-based indexing
    params.append('size', pageSize);

    // Add sorting
    if (sortField) {
      params.append('sort', `${sortField},${sortDirection}`);
    }

    // Add search term
    if (searchTerm) {
      params.append('search', searchTerm);
    }

    // Add data source filter
    if (dataSourceFilter) {
      params.append('dataSourceId', dataSourceFilter);
    }

    // Add user filter
    if (userFilter) {
      params.append('userId', userFilter);
    }

    // Add favorites filter
    if (showFavoritesOnly) {
      url = QUERY_API.FILTER_BY_FAVORITE;
    }

    // Append params to URL
    url = `${url}?${params.toString()}`;

    // Show loading state
    showLoading();

    // Fetch queries
    const response = await fetch(url, {
      headers: {
        'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
      }
    });

    if (!response.ok) {
      throw new Error('Failed to load queries');
    }

    const result = await response.json();

    // Check if API response is successful
    if (!result.success) {
      throw new Error(result.message || 'Failed to load queries');
    }

    // Get data from API response
    const data = result.data;

    // Update state
    queries = data.content || data;
    totalItems = data.totalElements || queries.length;
    totalPages = data.totalPages || Math.ceil(totalItems / pageSize);

    // Render queries
    renderQueries();

    // Update pagination
    updatePagination();

    // Hide loading state
    hideLoading();
  } catch (error) {
    console.error('Error loading queries:', error);
    showNotification('加载查询失败: ' + error.message, 'error');
    hideLoading();
  }
}

/**
 * Render queries to the list
 */
function renderQueries() {
  if (!queryListElement) return;

  // Clear existing queries
  queryListElement.innerHTML = '';

  // If no queries, show empty state
  if (queries.length === 0) {
    queryListElement.innerHTML = `
            <li>
                <div class="px-4 py-4 flex items-center sm:px-6">
                    <div class="min-w-0 flex-1 sm:flex sm:items-center sm:justify-between">
                        <p class="text-gray-500">没有找到查询。</p>
                    </div>
                </div>
            </li>
        `;
    return;
  }

  // Render each query
  queries.forEach(query => {
    const li = document.createElement('li');

    // Format date
    const modifiedDate = new Date(query.lastModified || query.createdAt);
    const timeAgo = formatTimeAgo(modifiedDate);

    li.innerHTML = `
            <div class="px-4 py-4 flex items-center sm:px-6">
                <div class="min-w-0 flex-1 sm:flex sm:items-center sm:justify-between">
                    <div>
                        <div class="flex text-sm">
                            <p class="font-medium text-indigo-600 truncate">${query.name}</p>
                            <p class="ml-1 flex-shrink-0 font-normal text-gray-500">v${query.version || '1.0'}</p>
                        </div>
                        <div class="mt-2">
                            <div class="flex items-center text-sm text-gray-500">
                                <i class="fas fa-database flex-shrink-0 mr-1.5 text-gray-400"></i>
                                <p>${query.dataSourceName || '未知数据源'}</p>
                                <i class="fas fa-clock ml-4 flex-shrink-0 mr-1.5 text-gray-400"></i>
                                <p>修改于 ${timeAgo}</p>
                            </div>
                        </div>
                    </div>
                    <div class="mt-4 flex-shrink-0 sm:mt-0 sm:ml-5">
                        <div class="flex space-x-2">
                            <button
                                class="favorite-btn inline-flex items-center p-2 border border-transparent rounded-full shadow-sm text-white ${query.favorite ? 'bg-yellow-500 hover:bg-yellow-600' : 'bg-gray-300 hover:bg-gray-400'} focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-500"
                                type="button"
                                data-id="${query.id}">
                                <i class="fas fa-star"></i>
                            </button>
                            <button
                                class="edit-btn inline-flex items-center p-2 border border-transparent rounded-full shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                                type="button"
                                data-id="${query.id}">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button
                                class="delete-btn inline-flex items-center p-2 border border-transparent rounded-full shadow-sm text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
                                type="button"
                                data-id="${query.id}"
                                data-name="${query.name}">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;

    queryListElement.appendChild(li);
  });

  // Add event listeners to buttons
  document.querySelectorAll('.favorite-btn').forEach(btn => {
    btn.addEventListener('click', handleFavoriteToggle);
  });

  document.querySelectorAll('.edit-btn').forEach(btn => {
    btn.addEventListener('click', handleEditQuery);
  });

  document.querySelectorAll('.delete-btn').forEach(btn => {
    btn.addEventListener('click', handleDeleteQuery);
  });
}

/**
 * Update pagination controls
 */
function updatePagination() {
  // Update pagination info
  if (paginationInfo) {
    const start = (currentPage - 1) * pageSize + 1;
    const end = Math.min(start + pageSize - 1, totalItems);
    paginationInfo.innerHTML = `
            显示 <span class="font-medium">${start}</span> 到
            <span class="font-medium">${end}</span> 条，共
            <span class="font-medium">${totalItems}</span> 条结果
        `;
  }

  // Update pagination controls
  if (paginationContainer) {
    paginationContainer.innerHTML = '';

    // Previous button
    const prevButton = document.createElement('a');
    prevButton.href = '#';
    prevButton.className = `relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium ${currentPage > 1 ? 'text-gray-500 hover:bg-gray-50' : 'text-gray-300 cursor-not-allowed'}`;
    prevButton.innerHTML = '<span class="sr-only">上一页</span><i class="fas fa-chevron-left"></i>';

    if (currentPage > 1) {
      prevButton.addEventListener('click', (e) => {
        e.preventDefault();
        goToPage(currentPage - 1);
      });
    }

    paginationContainer.appendChild(prevButton);

    // Page numbers
    const maxVisiblePages = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      const pageButton = document.createElement('a');
      pageButton.href = '#';
      pageButton.className = `relative inline-flex items-center px-4 py-2 border ${i === currentPage ? 'bg-indigo-50 border-indigo-500 text-indigo-600 z-10' : 'bg-white border-gray-300 text-gray-700 hover:bg-gray-50'}`;
      pageButton.textContent = i;

      if (i !== currentPage) {
        pageButton.addEventListener('click', (e) => {
          e.preventDefault();
          goToPage(i);
        });
      }

      paginationContainer.appendChild(pageButton);
    }

    // Next button
    const nextButton = document.createElement('a');
    nextButton.href = '#';
    nextButton.className = `relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium ${currentPage < totalPages ? 'text-gray-500 hover:bg-gray-50' : 'text-gray-300 cursor-not-allowed'}`;
    nextButton.innerHTML = '<span class="sr-only">下一页</span><i class="fas fa-chevron-right"></i>';

    if (currentPage < totalPages) {
      nextButton.addEventListener('click', (e) => {
        e.preventDefault();
        goToPage(currentPage + 1);
      });
    }

    paginationContainer.appendChild(nextButton);
  }
}

/**
 * Go to a specific page
 */
function goToPage(page) {
  if (page < 1 || page > totalPages || page === currentPage) {
    return;
  }

  currentPage = page;
  loadQueries();
}

/**
 * Handle search input
 */
function handleSearch(e) {
  searchTerm = e.target.value.trim();
  currentPage = 1; // Reset to first page
  loadQueries();
}

/**
 * Handle data source filter change
 */
function handleDataSourceFilter(e) {
  dataSourceFilter = e.target.value === 'All Sources' ? '' : e.target.value;
  currentPage = 1; // Reset to first page
  loadQueries();
}

/**
 * Handle user filter change
 */
function handleUserFilter(e) {
  const value = e.target.value;

  if (value === 'All Users') {
    userFilter = '';
  } else if (value === 'Me') {
    userFilter = 'current-user'; // In a real app, this would be the actual user ID
  } else if (value === 'Team') {
    userFilter = 'team'; // In a real app, this would be the team ID
  }

  currentPage = 1; // Reset to first page
  loadQueries();
}

/**
 * Handle sort change
 */
function handleSortChange(e) {
  const value = e.target.value;

  if (value === 'Last Modified') {
    sortField = 'lastModified';
    sortDirection = 'desc';
  } else if (value === 'Name') {
    sortField = 'name';
    sortDirection = 'asc';
  } else if (value === 'Created Date') {
    sortField = 'createdAt';
    sortDirection = 'desc';
  }

  loadQueries();
}

/**
 * Handle new query button click
 */
function handleNewQuery() {
  window.location.href = '/query/editor.html';
}

/**
 * Handle toggle favorites button click
 */
function handleToggleFavorites() {
  showFavoritesOnly = !showFavoritesOnly;

  // Update button style
  if (favoritesButton) {
    if (showFavoritesOnly) {
      favoritesButton.classList.remove('border-gray-300', 'text-gray-700', 'bg-white');
      favoritesButton.classList.add('border-transparent', 'text-white', 'bg-yellow-500', 'hover:bg-yellow-600');
    } else {
      favoritesButton.classList.remove('border-transparent', 'text-white', 'bg-yellow-500', 'hover:bg-yellow-600');
      favoritesButton.classList.add('border-gray-300', 'text-gray-700', 'bg-white', 'hover:bg-gray-50');
    }
  }

  currentPage = 1; // Reset to first page
  loadQueries();
}

/**
 * Handle favorite toggle for a query
 */
async function handleFavoriteToggle(e) {
  const button = e.currentTarget;
  const queryId = button.dataset.id;

  try {
    const response = await fetch(QUERY_API.FAVORITE(queryId), {
      method: 'POST',
      headers: {
        'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
      }
    });

    if (!response.ok) {
      throw new Error('Failed to update favorite status');
    }

    const result = await response.json();

    // Check if API response is successful
    if (!result.success) {
      throw new Error(result.message || 'Failed to update favorite status');
    }

    // Get data from API response
    const data = result.data;
    const isFavorite = data.favorite;

    // Update button style
    if (isFavorite) {
      button.classList.remove('bg-gray-300', 'hover:bg-gray-400');
      button.classList.add('bg-yellow-500', 'hover:bg-yellow-600');
    } else {
      button.classList.remove('bg-yellow-500', 'hover:bg-yellow-600');
      button.classList.add('bg-gray-300', 'hover:bg-gray-400');
    }

    // Show notification
    showNotification(`查询已${isFavorite ? '添加到' : '从'}收藏夹${isFavorite ? '' : '中移除'}`, 'success');

    // Reload queries if showing favorites only
    if (showFavoritesOnly) {
      loadQueries();
    }
  } catch (error) {
    console.error('Error updating favorite status:', error);
    showNotification('更新收藏状态失败: ' + error.message, 'error');
  }
}

/**
 * Handle edit query button click
 */
function handleEditQuery(e) {
  const queryId = e.currentTarget.dataset.id;
  window.location.href = `/query/editor.html?id=${queryId}`;
}

/**
 * Handle delete query button click
 */
async function handleDeleteQuery(e) {
  const button = e.currentTarget;
  const queryId = button.dataset.id;
  const queryName = button.dataset.name;

  // Confirm deletion
  if (!confirm(`确定要删除查询 "${queryName}" 吗？此操作无法撤销。`)) {
    return;
  }

  try {
    const response = await fetch(QUERY_API.DELETE(queryId), {
      method: 'DELETE',
      headers: {
        'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
      }
    });

    if (!response.ok) {
      throw new Error('Failed to delete query');
    }

    const result = await response.json();

    // Check if API response is successful
    if (!result.success) {
      throw new Error(result.message || 'Failed to delete query');
    }

    // Show notification
    showNotification('查询已成功删除', 'success');

    // Reload queries
    loadQueries();
  } catch (error) {
    console.error('Error deleting query:', error);
    showNotification('删除查询失败: ' + error.message, 'error');
  }
}

/**
 * Load data sources for filter dropdown
 */
async function loadDataSources() {
  try {
    const response = await fetch('/api/datasources', {
      headers: {
        'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
      }
    });

    if (!response.ok) {
      throw new Error('Failed to load data sources');
    }

    const result = await response.json();

    // Check if API response is successful
    if (!result.success) {
      throw new Error(result.message || 'Failed to load data sources');
    }

    // Get data from API response
    const dataSources = result.data;

    // Clear existing options except the first one
    while (dataSourceSelect.options.length > 1) {
      dataSourceSelect.remove(1);
    }

    // Add data sources to dropdown
    dataSources.forEach(dataSource => {
      if (dataSource.status === 'ACTIVE') {
        const option = document.createElement('option');
        option.value = dataSource.id;
        option.textContent = dataSource.name;
        dataSourceSelect.appendChild(option);
      }
    });
  } catch (error) {
    console.error('Error loading data sources:', error);
    // Don't show notification for this error to avoid cluttering the UI
  }
}

/**
 * Show loading state
 */
function showLoading() {
  // Implementation depends on UI design
  // Could add a loading spinner or disable buttons
}

/**
 * Hide loading state
 */
function hideLoading() {
  // Implementation depends on UI design
  // Could remove loading spinner or enable buttons
}

/**
 * Format time ago
 */
function formatTimeAgo(date) {
  const now = new Date();
  const diffInSeconds = Math.floor((now - date) / 1000);

  if (diffInSeconds < 60) {
    return '刚刚';
  }

  const diffInMinutes = Math.floor(diffInSeconds / 60);
  if (diffInMinutes < 60) {
    return `${diffInMinutes} 分钟前`;
  }

  const diffInHours = Math.floor(diffInMinutes / 60);
  if (diffInHours < 24) {
    return `${diffInHours} 小时前`;
  }

  const diffInDays = Math.floor(diffInHours / 24);
  if (diffInDays < 30) {
    return `${diffInDays} 天前`;
  }

  const diffInMonths = Math.floor(diffInDays / 30);
  if (diffInMonths < 12) {
    return `${diffInMonths} 个月前`;
  }

  const diffInYears = Math.floor(diffInMonths / 12);
  return `${diffInYears} 年前`;
}

/**
 * Debounce function to limit how often a function is called
 */
function debounce(func, wait) {
  let timeout;
  return function (...args) {
    const context = this;
    clearTimeout(timeout);
    timeout = setTimeout(() => func.apply(context, args), wait);
  };
}

/**
 * Show notification
 */
function showNotification(message, type = 'info') {
  // Create notification element
  const notification = document.createElement('div');
  notification.className = `fixed bottom-4 right-4 px-4 py-2 rounded-lg shadow-lg flex items-center space-x-2 ${
    type === 'error' ? 'bg-red-500 text-white' :
      type === 'success' ? 'bg-green-500 text-white' :
        type === 'warning' ? 'bg-yellow-500 text-white' :
          'bg-blue-500 text-white'
  }`;

  // Icon
  const icon = document.createElement('i');
  icon.className = `fas ${
    type === 'error' ? 'fa-exclamation-circle' :
      type === 'success' ? 'fa-check-circle' :
        type === 'warning' ? 'fa-exclamation-triangle' :
          'fa-info-circle'
  }`;
  notification.appendChild(icon);

  // Message
  const messageEl = document.createElement('span');
  messageEl.textContent = message;
  notification.appendChild(messageEl);

  // Close button
  const closeButton = document.createElement('button');
  closeButton.className = 'ml-2 text-white';
  closeButton.innerHTML = '<i class="fas fa-times"></i>';
  closeButton.addEventListener('click', () => {
    document.body.removeChild(notification);
  });
  notification.appendChild(closeButton);

  // Add to body
  document.body.appendChild(notification);

  // Auto-remove after 5 seconds
  setTimeout(() => {
    if (document.body.contains(notification)) {
      document.body.removeChild(notification);
    }
  }, 5000);
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', initQueryList);
