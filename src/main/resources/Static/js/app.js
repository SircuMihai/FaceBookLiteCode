// FacebookLite Frontend Application
class FacebookLiteApp {
    constructor() {
        this.apiBaseUrl = 'http://localhost:8082/api';
        this.currentUser = null;
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.checkAuthStatus();
        this.showPage('home-page');
    }

    setupEventListeners() {
        // Navigation toggle for mobile
        const navToggle = document.getElementById('nav-toggle');
        const navMenu = document.getElementById('nav-menu');
        
        if (navToggle) {
            navToggle.addEventListener('click', () => {
                navMenu.classList.toggle('active');
            });
        }

        // Login form
        const loginForm = document.getElementById('login-form');
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        }

        // Sign up form
        const signupForm = document.getElementById('signup-form');
        if (signupForm) {
            signupForm.addEventListener('submit', (e) => this.handleSignUp(e));
        }

        // Post form
        const postForm = document.getElementById('post-form');
        if (postForm) {
            postForm.addEventListener('submit', (e) => this.handleCreatePost(e));
        }
    }

    // Navigation Functions
    showPage(pageId) {
        // Hide all pages
        const pages = document.querySelectorAll('.page');
        pages.forEach(page => page.classList.remove('active'));

        // Show selected page
        const targetPage = document.getElementById(pageId);
        if (targetPage) {
            targetPage.classList.add('active');
        }

        // Update navigation
        this.updateNavigation();

        // Load page-specific content
        if (pageId === 'dashboard-page' && this.currentUser) {
            this.loadDashboard();
        }
    }

    updateNavigation() {
        const navLinks = document.getElementById('nav-links');
        const navAuth = document.getElementById('nav-auth');

        if (this.currentUser) {
            // User is logged in
            navLinks.innerHTML = `
                <a href="#" onclick="app.showPage('dashboard-page')">Dashboard</a>
                <a href="#" onclick="app.showPage('dashboard-page')">Posts</a>
            `;
            navAuth.innerHTML = `
                <span>Welcome, ${this.currentUser.username}</span>
                <button class="btn btn-secondary" onclick="app.logout()">Logout</button>
            `;
        } else {
            // User is not logged in
            navLinks.innerHTML = '';
            navAuth.innerHTML = `
                <button class="btn btn-primary" onclick="app.showLogin()">Login</button>
                <button class="btn btn-secondary" onclick="app.showSignUp()">Sign Up</button>
            `;
        }
    }

    // Authentication Functions
    async handleLogin(e) {
        e.preventDefault();
        this.showLoading(true);

        const formData = new FormData(e.target);
        const loginData = {
            username: formData.get('username'),
            password: formData.get('password')
        };

        try {
            // Get all users and find matching username
            const response = await fetch(`${this.apiBaseUrl}/users`);
            const users = await response.json();
            
            const user = users.find(u => u.username === loginData.username);
            
            if (user) {
                this.currentUser = user;
                localStorage.setItem('currentUser', JSON.stringify(user));
                this.showToast('Login successful!', 'success');
                this.showPage('dashboard-page');
            } else {
                this.showToast('Invalid username or password', 'error');
            }
        } catch (error) {
            console.error('Login error:', error);
            this.showToast('Login failed. Please try again.', 'error');
        } finally {
            this.showLoading(false);
        }
    }

    async handleSignUp(e) {
        e.preventDefault();
        this.showLoading(true);

        const formData = new FormData(e.target);
        const userData = {
            username: formData.get('username'),
            email: formData.get('email'),
            password: formData.get('password'),
            firstName: formData.get('firstName'),
            lastName: formData.get('lastName'),
            role: 'USER',
            privateAccount: false
        };

        try {
            const response = await fetch(`${this.apiBaseUrl}/users`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userData)
            });

            if (response.ok) {
                const newUser = await response.json();
                this.currentUser = newUser;
                localStorage.setItem('currentUser', JSON.stringify(newUser));
                this.showToast('Account created successfully!', 'success');
                this.showPage('dashboard-page');
            } else {
                const error = await response.text();
                this.showToast('Registration failed: ' + error, 'error');
            }
        } catch (error) {
            console.error('Registration error:', error);
            this.showToast('Registration failed. Please try again.', 'error');
        } finally {
            this.showLoading(false);
        }
    }

    logout() {
        this.currentUser = null;
        localStorage.removeItem('currentUser');
        this.showToast('Logged out successfully', 'success');
        this.showPage('home-page');
    }

    checkAuthStatus() {
        const savedUser = localStorage.getItem('currentUser');
        if (savedUser) {
            this.currentUser = JSON.parse(savedUser);
        }
    }

    // Page Navigation Functions
    showLogin() {
        this.showPage('login-page');
    }

    showSignUp() {
        this.showPage('signup-page');
    }

    // Dashboard Functions
    async loadDashboard() {
        if (!this.currentUser) return;

        // Update user name in dashboard
        const userNameElement = document.getElementById('user-name');
        if (userNameElement) {
            userNameElement.textContent = this.currentUser.username;
        }

        // Load posts
        await this.loadPosts();
    }

    async loadPosts() {
        try {
            const response = await fetch(`${this.apiBaseUrl}/posts`);
            const posts = await response.json();
            this.displayPosts(posts);
        } catch (error) {
            console.error('Error loading posts:', error);
            this.showToast('Failed to load posts', 'error');
        }
    }

    displayPosts(posts) {
        const postsFeed = document.getElementById('posts-feed');
        if (!postsFeed) return;

        if (posts.length === 0) {
            postsFeed.innerHTML = '<p class="text-center">No posts yet. Create the first one!</p>';
            return;
        }

        postsFeed.innerHTML = posts.map(post => `
            <div class="post-item">
                <div class="post-header">
                    <span class="post-author">${post.user ? post.user.username : 'Unknown User'}</span>
                    <span class="post-date">${this.formatDate(post.createdAt)}</span>
                </div>
                <div class="post-content">${post.content}</div>
                <div class="post-actions">
                    <button class="post-action" onclick="app.likePost(${post.postId})">
                        <i class="fas fa-heart"></i> Like
                    </button>
                    <button class="post-action" onclick="app.commentPost(${post.postId})">
                        <i class="fas fa-comment"></i> Comment
                    </button>
                </div>
            </div>
        `).join('');
    }

    async handleCreatePost(e) {
        e.preventDefault();
        if (!this.currentUser) {
            this.showToast('Please log in to create posts', 'error');
            return;
        }

        const content = document.getElementById('post-content').value;
        if (!content.trim()) {
            this.showToast('Please enter some content', 'error');
            return;
        }

        this.showLoading(true);

        try {
            const response = await fetch(`${this.apiBaseUrl}/simple/create-post`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    content: content,
                    userId: this.currentUser.userId
                })
            });

            if (response.ok) {
                const result = await response.json();
                this.showToast('Post created successfully!', 'success');
                document.getElementById('post-content').value = '';
                await this.loadPosts(); // Reload posts
            } else {
                const error = await response.text();
                this.showToast('Failed to create post: ' + error, 'error');
            }
        } catch (error) {
            console.error('Error creating post:', error);
            this.showToast('Failed to create post', 'error');
        } finally {
            this.showLoading(false);
        }
    }

    // Utility Functions
    showLoading(show) {
        const loading = document.getElementById('loading');
        if (loading) {
            loading.classList.toggle('hidden', !show);
        }
    }

    showToast(message, type = 'success') {
        const container = document.getElementById('toast-container');
        if (!container) return;

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.textContent = message;

        container.appendChild(toast);

        // Remove toast after 3 seconds
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 3000);
    }

    formatDate(dateString) {
        if (!dateString) return 'Unknown date';
        const date = new Date(dateString);
        return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
    }

    // Post Actions (placeholder functions)
    likePost(postId) {
        this.showToast('Like functionality coming soon!', 'success');
    }

    commentPost(postId) {
        this.showToast('Comment functionality coming soon!', 'success');
    }
}

// Initialize the application
const app = new FacebookLiteApp();

// Global functions for HTML onclick handlers
function showLogin() {
    app.showLogin();
}

function showSignUp() {
    app.showSignUp();
}

function logout() {
    app.logout();
}
