/**
 * Data Source Management JavaScript
 */

// API Base URL
const API_BASE_URL = '/api';

// Data Source API Endpoints
const DATASOURCE_API = {
  LIST: `${API_BASE_URL}/datasources`,
  CREATE: `${API_BASE_URL}/datasources`,
  GET: (id) => `${API_BASE_URL}/datasources/${id}`,
  UPDATE: (id) => `${API_BASE_URL}/datasources/${id}`,
  DELETE: (id) => `${API_BASE_URL}/datasources/${id}`,
  ACTIVATE: (id) => `${API_BASE_URL}/datasources/${id}/activate`,
  DEACTIVATE: (id) => `${API_BASE_URL}/datasources/${id}/deactivate`,
  TEST_CONNECTION: `${API_BASE_URL}/datasources/test-connection`,
  SYNC: (id) => `${API_BASE_URL}/datasources/${id}/sync`,
  SEARCH: `${API_BASE_URL}/datasources/search`,
  FILTER_BY_TYPE: (type) => `${API_BASE_URL}/datasources/filter/type/${type}`,
  FILTER_BY_STATUS: (status) => `${API_BASE_URL}/datasources/filter/status/${status}`,
  FILTER_BY_TYPE_AND_STATUS: (type, status) => `${API_BASE_URL}/datasources/filter/type/${type}/status/${status}`
};

// Data Source Type Mapping
const DATASOURCE_TYPES = {
  MYSQL: 'MySQL',
  POSTGRESQL: 'PostgreSQL',
  DB2: 'DB2',
  ORACLE: 'Oracle',
  SQLSERVER: 'SQL Server',
  HIVE: 'Hive',
  CLICKHOUSE: 'ClickHouse',
  H2: 'H2'
};

// Data Source Status Mapping
const DATASOURCE_STATUS = {
  ACTIVE: 'Active',
  INACTIVE: 'Inactive',
  ERROR: 'Error'
};

// Data Source Status Color Mapping
const STATUS_COLORS = {
  ACTIVE: 'bg-green-100 text-green-800',
  INACTIVE: 'bg-gray-100 text-gray-800',
  ERROR: 'bg-red-100 text-red-800'
};

/**
 * Data Source Manager
 */
class DataSourceManager {
  constructor() {
    this.dataSources = [];
    this.currentPage = 1;
    this.pageSize = 10;
    this.totalPages = 1;
    this.totalItems = 0;
    this.searchTerm = '';
    this.typeFilter = '';
    this.statusFilter = '';

    // Initialize event listeners
    this.initEventListeners();
  }

  /**
   * Initialize event listeners
   */
  initEventListeners() {
    // Search input
    document.getElementById('search-input')?.addEventListener('input', (e) => {
      this.searchTerm = e.target.value;
      this.debounceSearch();
    });

    // Type filter
    document.getElementById('type-filter')?.addEventListener('change', (e) => {
      this.typeFilter = e.target.value;
      this.applyFilters();
    });

    // Status filter
    document.getElementById('status-filter')?.addEventListener('change', (e) => {
      this.statusFilter = e.target.value;
      this.applyFilters();
    });

    // Add data source button
    document.getElementById('add-datasource-btn')?.addEventListener('click', () => {
      window.location.href = '/datasource/form.html';
    });

    // Pagination
    document.querySelectorAll('.pagination-btn')?.forEach(btn => {
      btn.addEventListener('click', (e) => {
        const page = parseInt(e.target.dataset.page);
        if (!isNaN(page)) {
          this.goToPage(page);
        }
      });
    });

    // Form submission
    const form = document.getElementById('datasource-form');
    if (form) {
      form.addEventListener('submit', (e) => {
        e.preventDefault();
        this.saveDataSource();
      });

      // Test connection button
      document.getElementById('test-connection-btn')?.addEventListener('click', () => {
        this.testConnection();
      });
    }
  }

  /**
   * Debounce search to avoid too many API calls
   */
  debounceSearch() {
    clearTimeout(this.searchTimeout);
    this.searchTimeout = setTimeout(() => {
      this.applyFilters();
    }, 300);
  }

  /**
   * Apply filters and search
   */
  applyFilters() {
    this.currentPage = 1;
    this.loadDataSources();
  }

  /**
   * Go to specific page
   */
  goToPage(page) {
    if (page < 1 || page > this.totalPages) {
      return;
    }
    this.currentPage = page;
    this.loadDataSources();
  }

  /**
   * Load data sources from API
   */
  async loadDataSources() {
    try {
      let url = DATASOURCE_API.LIST;

      // Apply filters
      if (this.searchTerm) {
        url = `${DATASOURCE_API.SEARCH}?name=${encodeURIComponent(this.searchTerm)}`;
      } else if (this.typeFilter && this.statusFilter) {
        url = DATASOURCE_API.FILTER_BY_TYPE_AND_STATUS(this.typeFilter, this.statusFilter);
      } else if (this.typeFilter) {
        url = DATASOURCE_API.FILTER_BY_TYPE(this.typeFilter);
      } else if (this.statusFilter) {
        url = DATASOURCE_API.FILTER_BY_STATUS(this.statusFilter);
      }

      const response = await fetch(url);
      const result = await response.json();

      if (result.success) {
        this.dataSources = result.data;
        this.totalItems = this.dataSources.length;
        this.totalPages = Math.ceil(this.totalItems / this.pageSize);

        // Render data sources
        this.renderDataSources();
        // Update pagination
        this.updatePagination();
      } else {
        this.showError(result.message || 'Failed to load data sources');
      }
    } catch (error) {
      console.error('Error loading data sources:', error);
      this.showError('Failed to load data sources. Please try again later.');
    }
  }

  /**
   * Render data sources list
   */
  renderDataSources() {
    const container = document.getElementById('datasource-list');
    if (!container) return;

    // Calculate pagination
    const start = (this.currentPage - 1) * this.pageSize;
    const end = Math.min(start + this.pageSize, this.totalItems);
    const paginatedData = this.dataSources.slice(start, end);

    if (paginatedData.length === 0) {
      container.innerHTML = `
                <li class="px-4 py-4 flex items-center sm:px-6">
                    <div class="min-w-0 flex-1 sm:flex sm:items-center sm:justify-between">
                        <p class="text-gray-500">No data sources found.</p>
                    </div>
                </li>
            `;
      return;
    }

    container.innerHTML = paginatedData.map(ds => this.renderDataSourceItem(ds)).join('');

    // Add event listeners to action buttons
    document.querySelectorAll('.edit-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const id = e.currentTarget.dataset.id;
        window.location.href = `/datasource/form.html?id=${id}`;
      });
    });

    document.querySelectorAll('.delete-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const id = e.currentTarget.dataset.id;
        const name = e.currentTarget.dataset.name;
        this.confirmDelete(id, name);
      });
    });

    document.querySelectorAll('.activate-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const id = e.currentTarget.dataset.id;
        this.activateDataSource(id);
      });
    });

    document.querySelectorAll('.deactivate-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const id = e.currentTarget.dataset.id;
        this.deactivateDataSource(id);
      });
    });

    document.querySelectorAll('.sync-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const id = e.currentTarget.dataset.id;
        this.syncMetadata(id);
      });
    });
  }

  /**
   * Render a single data source item
   */
  renderDataSourceItem(dataSource) {
    const statusClass = STATUS_COLORS[dataSource.status] || 'bg-gray-100 text-gray-800';
    const statusText = DATASOURCE_STATUS[dataSource.status] || 'Unknown';
    const typeText = DATASOURCE_TYPES[dataSource.type] || dataSource.type;

    return `
            <li class="border-b border-gray-200 last:border-b-0">
                <div class="px-4 py-4 flex items-center sm:px-6">
                    <div class="min-w-0 flex-1 sm:flex sm:items-center sm:justify-between">
                        <div>
                            <div class="flex text-sm">
                                <p class="font-medium text-indigo-600 truncate">${dataSource.name}</p>
                                <p class="ml-1 flex-shrink-0 font-normal text-gray-500">${typeText}</p>
                            </div>
                            <div class="mt-2 flex">
                                <div class="flex items-center text-sm text-gray-500">
                                    <i class="fas fa-server flex-shrink-0 mr-1.5 text-gray-400"></i>
                                    <p>${dataSource.host}:${dataSource.port}</p>
                                </div>
                            </div>
                        </div>
                        <div class="mt-4 flex-shrink-0 sm:mt-0 sm:ml-5">
                            <div class="flex -space-x-1 overflow-hidden">
                                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusClass}">
                                    ${statusText}
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="ml-5 flex-shrink-0">
                        <div class="flex space-x-2">
                            <button type="button" class="sync-btn inline-flex items-center p-2 border border-transparent rounded-full shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500" data-id="${dataSource.id}">
                                <i class="fas fa-sync-alt"></i>
                            </button>
                            <button type="button" class="edit-btn inline-flex items-center p-2 border border-transparent rounded-full shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500" data-id="${dataSource.id}">
                                <i class="fas fa-edit"></i>
                            </button>
                            ${dataSource.status === 'ACTIVE' ?
      `<button type="button" class="deactivate-btn inline-flex items-center p-2 border border-transparent rounded-full shadow-sm text-white bg-yellow-600 hover:bg-yellow-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-500" data-id="${dataSource.id}">
                                    <i class="fas fa-pause"></i>
                                </button>` :
      `<button type="button" class="activate-btn inline-flex items-center p-2 border border-transparent rounded-full shadow-sm text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500" data-id="${dataSource.id}">
                                    <i class="fas fa-play"></i>
                                </button>`
    }
                            <button type="button" class="delete-btn inline-flex items-center p-2 border border-transparent rounded-full shadow-sm text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500" data-id="${dataSource.id}" data-name="${dataSource.name}">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </li>
        `;
  }

  /**
   * Update pagination UI
   */
  updatePagination() {
    const paginationContainer = document.getElementById('pagination-container');
    if (!paginationContainer) return;

    const paginationInfo = document.getElementById('pagination-info');
    if (paginationInfo) {
      const start = Math.min((this.currentPage - 1) * this.pageSize + 1, this.totalItems);
      const end = Math.min(start + this.pageSize - 1, this.totalItems);
      paginationInfo.textContent = `Showing ${start} to ${end} of ${this.totalItems} results`;
    }

    const paginationNav = document.getElementById('pagination-nav');
    if (!paginationNav) return;

    let paginationHTML = `
            <a href="#" class="pagination-btn relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 ${this.currentPage === 1 ? 'opacity-50 cursor-not-allowed' : ''}" data-page="${this.currentPage - 1}">
                <span class="sr-only">Previous</span>
                <i class="fas fa-chevron-left"></i>
            </a>
        `;

    // Generate page buttons
    for (let i = 1; i <= this.totalPages; i++) {
      if (
        i === 1 ||
        i === this.totalPages ||
        (i >= this.currentPage - 1 && i <= this.currentPage + 1)
      ) {
        paginationHTML += `
                    <a href="#" class="pagination-btn relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium ${i === this.currentPage ? 'text-indigo-600 bg-indigo-50' : 'text-gray-700 hover:bg-gray-50'}" data-page="${i}">
                        ${i}
                    </a>
                `;
      } else if (
        i === this.currentPage - 2 ||
        i === this.currentPage + 2
      ) {
        paginationHTML += `
                    <span class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700">
                        ...
                    </span>
                `;
      }
    }

    paginationHTML += `
            <a href="#" class="pagination-btn relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 ${this.currentPage === this.totalPages ? 'opacity-50 cursor-not-allowed' : ''}" data-page="${this.currentPage + 1}">
                <span class="sr-only">Next</span>
                <i class="fas fa-chevron-right"></i>
            </a>
        `;

    paginationNav.innerHTML = paginationHTML;

    // Add event listeners to pagination buttons
    document.querySelectorAll('.pagination-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        const page = parseInt(e.currentTarget.dataset.page);
        if (!isNaN(page) && page >= 1 && page <= this.totalPages) {
          this.goToPage(page);
        }
      });
    });
  }

  /**
   * Load data source details for editing
   */
  async loadDataSourceDetails(id) {
    try {
      const response = await fetch(DATASOURCE_API.GET(id));
      const result = await response.json();

      if (result.success) {
        this.populateForm(result.data);
      } else {
        this.showError(result.message || 'Failed to load data source details');
      }
    } catch (error) {
      console.error('Error loading data source details:', error);
      this.showError('Failed to load data source details. Please try again later.');
    }
  }

  /**
   * Populate form with data source details
   */
  populateForm(dataSource) {
    const form = document.getElementById('datasource-form');
    if (!form) return;

    // Set form values
    form.elements['name'].value = dataSource.name || '';
    form.elements['type'].value = dataSource.type || '';
    form.elements['description'].value = dataSource.description || '';
    form.elements['host'].value = dataSource.host || '';
    form.elements['port'].value = dataSource.port || '';
    form.elements['database'].value = dataSource.database || '';
    form.elements['schema'].value = dataSource.schema || '';
    form.elements['username'].value = dataSource.username || '';

    // Don't populate password for security reasons
    form.elements['password'].value = '';

    // Set advanced settings
    form.elements['timeout'].value = dataSource.timeout || 30;
    form.elements['poolSize'].value = dataSource.poolSize || 10;
    form.elements['autoSync'].checked = dataSource.autoSync || false;

    // Store data source ID for update
    form.dataset.id = dataSource.id;

    // Update form title
    const formTitle = document.getElementById('form-title');
    if (formTitle) {
      formTitle.textContent = 'Edit Data Source';
    }

    // Update submit button text
    const submitButton = document.querySelector('button[type="submit"]');
    if (submitButton) {
      submitButton.textContent = 'Update Data Source';
    }
  }

  /**
   * Save data source (create or update)
   */
  async saveDataSource() {
    try {
      const form = document.getElementById('datasource-form');
      if (!form) return;

      // Get form data
      const formData = new FormData(form);
      const dataSource = {
        name: formData.get('name'),
        type: formData.get('type'),
        description: formData.get('description'),
        host: formData.get('host'),
        port: parseInt(formData.get('port')),
        database: formData.get('database'),
        schema: formData.get('schema'),
        username: formData.get('username'),
        password: formData.get('password'),
        timeout: parseInt(formData.get('timeout')),
        poolSize: parseInt(formData.get('poolSize')),
        autoSync: formData.get('autoSync') === 'on'
      };

      // Validate required fields
      if (!dataSource.name || !dataSource.type || !dataSource.host || !dataSource.port || !dataSource.database || !dataSource.username) {
        this.showError('Please fill in all required fields');
        return;
      }

      const id = form.dataset.id;
      let url = DATASOURCE_API.CREATE;
      let method = 'POST';

      // If ID exists, update instead of create
      if (id) {
        url = DATASOURCE_API.UPDATE(id);
        method = 'PUT';
        dataSource.id = id;
      }

      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
          'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
        },
        body: JSON.stringify(dataSource)
      });

      const result = await response.json();

      if (result.success) {
        // Show success message
        this.showSuccess(id ? 'Data source updated successfully' : 'Data source created successfully');

        // Redirect to list page after a short delay
        setTimeout(() => {
          window.location.href = '/datasource/list.html';
        }, 1500);
      } else {
        this.showError(result.message || 'Failed to save data source');
      }
    } catch (error) {
      console.error('Error saving data source:', error);
      this.showError('Failed to save data source. Please try again later.');
    }
  }

  /**
   * Test data source connection
   */
  async testConnection() {
    try {
      const form = document.getElementById('datasource-form');
      if (!form) return;

      // Get form data
      const formData = new FormData(form);
      const connectionData = {
        name: formData.get('name'),
        type: formData.get('type'),
        host: formData.get('host'),
        port: parseInt(formData.get('port')),
        database: formData.get('database'),
        schema: formData.get('schema'),
        username: formData.get('username'),
        password: formData.get('password')
      };

      // Validate required fields
      if (!connectionData.host || !connectionData.port || !connectionData.database || !connectionData.username || !connectionData.password) {
        this.showError('Please fill in all connection details');
        return;
      }

      // Show testing message
      this.showInfo('Testing connection...');

      const response = await fetch(DATASOURCE_API.TEST_CONNECTION, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(connectionData)
      });

      const result = await response.json();

      if (result.success && result.data) {
        this.showSuccess('Connection successful!');
      } else {
        this.showError(result.message || 'Connection failed');
      }
    } catch (error) {
      console.error('Error testing connection:', error);
      this.showError('Failed to test connection. Please try again later.');
    }
  }

  /**
   * Confirm data source deletion
   */
  confirmDelete(id, name) {
    if (confirm(`Are you sure you want to delete the data source "${name}"? This action cannot be undone.`)) {
      this.deleteDataSource(id);
    }
  }

  /**
   * Delete data source
   */
  async deleteDataSource(id) {
    try {
      const response = await fetch(DATASOURCE_API.DELETE(id), {
        method: 'DELETE',
        headers: {
          'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
        }
      });

      const result = await response.json();

      if (result.success) {
        this.showSuccess('Data source deleted successfully');
        // Reload data sources
        this.loadDataSources();
      } else {
        this.showError(result.message || 'Failed to delete data source');
      }
    } catch (error) {
      console.error('Error deleting data source:', error);
      this.showError('Failed to delete data source. Please try again later.');
    }
  }

  /**
   * Activate data source
   */
  async activateDataSource(id) {
    try {
      const response = await fetch(DATASOURCE_API.ACTIVATE(id), {
        method: 'POST',
        headers: {
          'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
        }
      });

      const result = await response.json();

      if (result.success) {
        this.showSuccess('Data source activated successfully');
        // Reload data sources
        this.loadDataSources();
      } else {
        this.showError(result.message || 'Failed to activate data source');
      }
    } catch (error) {
      console.error('Error activating data source:', error);
      this.showError('Failed to activate data source. Please try again later.');
    }
  }

  /**
   * Deactivate data source
   */
  async deactivateDataSource(id) {
    try {
      const response = await fetch(DATASOURCE_API.DEACTIVATE(id), {
        method: 'POST',
        headers: {
          'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
        }
      });

      const result = await response.json();

      if (result.success) {
        this.showSuccess('Data source deactivated successfully');
        // Reload data sources
        this.loadDataSources();
      } else {
        this.showError(result.message || 'Failed to deactivate data source');
      }
    } catch (error) {
      console.error('Error deactivating data source:', error);
      this.showError('Failed to deactivate data source. Please try again later.');
    }
  }

  /**
   * Sync data source metadata
   */
  async syncMetadata(id) {
    try {
      this.showInfo('Syncing metadata...');

      const response = await fetch(DATASOURCE_API.SYNC(id), {
        method: 'POST',
        headers: {
          'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
        }
      });

      const result = await response.json();

      if (result.success) {
        this.showSuccess('Metadata synced successfully');
        // Reload data sources
        this.loadDataSources();
      } else {
        this.showError(result.message || 'Failed to sync metadata');
      }
    } catch (error) {
      console.error('Error syncing metadata:', error);
      this.showError('Failed to sync metadata. Please try again later.');
    }
  }

  /**
   * Show success message
   */
  showSuccess(message) {
    this.showNotification(message, 'success');
  }

  /**
   * Show error message
   */
  showError(message) {
    this.showNotification(message, 'error');
  }

  /**
   * Show info message
   */
  showInfo(message) {
    this.showNotification(message, 'info');
  }

  /**
   * Show notification
   */
  showNotification(message, type = 'info') {
    // Check if notification container exists, if not create it
    let notificationContainer = document.getElementById('notification-container');
    if (!notificationContainer) {
      notificationContainer = document.createElement('div');
      notificationContainer.id = 'notification-container';
      notificationContainer.className = 'fixed top-4 right-4 z-50 flex flex-col space-y-2';
      document.body.appendChild(notificationContainer);
    }

    // Create notification element
    const notification = document.createElement('div');
    notification.className = 'px-4 py-3 rounded-lg shadow-md transition-all duration-300 transform translate-x-full';

    // Set notification style based on type
    switch (type) {
      case 'success':
        notification.className += ' bg-green-100 text-green-800 border-l-4 border-green-500';
        break;
      case 'error':
        notification.className += ' bg-red-100 text-red-800 border-l-4 border-red-500';
        break;
      case 'info':
      default:
        notification.className += ' bg-blue-100 text-blue-800 border-l-4 border-blue-500';
        break;
    }

    // Set notification content
    notification.innerHTML = `
            <div class="flex items-center justify-between">
                <div class="flex items-center">
                    <span class="font-medium">${message}</span>
                </div>
                <button class="ml-4 text-gray-400 hover:text-gray-600 focus:outline-none">
                    <i class="fas fa-times"></i>
                </button>
            </div>
        `;

    // Add notification to container
    notificationContainer.appendChild(notification);

    // Animate notification in
    setTimeout(() => {
      notification.classList.remove('translate-x-full');
    }, 10);

    // Add close button event listener
    const closeButton = notification.querySelector('button');
    closeButton.addEventListener('click', () => {
      this.closeNotification(notification);
    });

    // Auto close after 5 seconds
    setTimeout(() => {
      this.closeNotification(notification);
    }, 5000);
  }

  /**
   * Close notification
   */
  closeNotification(notification) {
    // Animate notification out
    notification.classList.add('translate-x-full');

    // Remove notification after animation
    setTimeout(() => {
      notification.remove();
    }, 300);
  }

  /**
   * Initialize data source form
   */
  initForm() {
    // Check if we're on the form page
    const form = document.getElementById('datasource-form');
    if (!form) return;

    // Check if we're editing an existing data source
    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get('id');

    if (id) {
      // Load data source details
      this.loadDataSourceDetails(id);
    }
  }

  /**
   * Initialize data source list
   */
  initList() {
    // Check if we're on the list page
    const list = document.getElementById('datasource-list');
    if (!list) return;

    // Load data sources
    this.loadDataSources();
  }

  /**
   * Initialize the data source manager
   */
  init() {
    // Initialize form or list based on current page
    this.initForm();
    this.initList();
  }
}

// Initialize data source manager when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
  const dataSourceManager = new DataSourceManager();
  dataSourceManager.init();
});
