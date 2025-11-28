// API Configuration
// Change this to match your server's address
// 
// For local development (same machine):
//   window.API_BASE_URL = 'http://localhost:8082/api';
//
// For remote access (different machine):
//   window.API_BASE_URL = 'http://YOUR_SERVER_IP:8082/api';
//   Example: window.API_BASE_URL = 'http://192.168.1.100:8082/api';
//
// For production (same domain):
//   window.API_BASE_URL = '/api';

// Auto-detect: Uses current hostname with port 8082
// Override this if your API server is on a different machine
(function() {
    const hostname = window.location.hostname;
    const protocol = window.location.protocol;
    
    if (hostname === 'localhost' || hostname === '127.0.0.1') {
        window.API_BASE_URL = 'http://localhost:8082/api';
    } else {
        // For remote access, you may need to change this to your server's IP
        // Example: window.API_BASE_URL = 'http://192.168.1.100:8082/api';
        window.API_BASE_URL = `${protocol}//${hostname}:8082/api`;
    }
    
    console.log('API Base URL configured:', window.API_BASE_URL);
})();

