/**
 * API Configuration
 * 
 * Handles dynamic API endpoint selection based on environment
 */

// Your backend URL - Update this when deploying to production
// For Vercel: Set via Environment Variables in Vercel Dashboard
const API_BASE = (() => {
    const hostname = window.location.hostname;
    
    // Local development
    if (hostname === 'localhost' || hostname === '127.0.0.1') {
        return 'http://localhost:8083';
    }
    
    // Production/Remote - ngrok tunnel for remote access
    return 'https://interdental-farcically-bernardina.ngrok-free.dev';
})();

// Make API_BASE available globally
window.API_BASE = API_BASE;

console.log('🔧 API Configuration loaded');
console.log('📍 Environment:', window.location.hostname);
console.log('🌐 API Base URL:', API_BASE);

